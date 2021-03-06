/*
 * Copyright 2009-10 www.scribble.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.scribble.protocol.monitor;

import org.scribble.protocol.monitor.model.ChoiceNode;
import org.scribble.protocol.monitor.model.DecisionNode;
import org.scribble.protocol.monitor.model.Description;
import org.scribble.protocol.monitor.model.MessageNode;
import org.scribble.protocol.monitor.model.ParallelNode;
import org.scribble.protocol.monitor.model.Scope;
import org.scribble.protocol.monitor.model.SendMessage;
import org.scribble.protocol.monitor.model.ReceiveMessage;
import org.scribble.protocol.monitor.model.Node;
import org.scribble.protocol.monitor.model.Path;
import org.scribble.protocol.monitor.model.ReceiveChoice;
import org.scribble.protocol.monitor.model.ReceiveDecision;
import org.scribble.protocol.monitor.model.SendChoice;
import org.scribble.protocol.monitor.model.SendDecision;
import org.scribble.protocol.monitor.model.TryNode;

// NOTES:
// All top level event processes have the same structure:
// (1) They process all node indexes associated with the context to check for match
// (2) If match not found, then process any nested conversations
// (3) If nested conversation returns match, then check if it is finished, and if so remove from parent context
// Outside scope of monitor, the environment should check that when a match is found, whether the associated
// context is finished, to remove the top level conversation instance monitoring.

public class DefaultProtocolMonitor implements ProtocolMonitor {
	
	public DefaultProtocolMonitor() {
	}

	/**
	 * This method initializes the supplied context based on the supplied description
	 * and role.
	 * 
	 * @param protocol The protocol description
	 * @param conv The conversation
	 */
	public void initialize(MonitorContext context, Description protocol, Session conv) {
		if (protocol.getNode().size() > 0) {
			addNodeToConversation(context, protocol, conv, 0);
		}
	}
	
	public Result messageSent(MonitorContext context, Description protocol,
						Session conv, String role, Message mesg) {
		Result ret=Result.NOT_HANDLED;
	
		// Check if context has state that is waiting for a send message
		for (int i=0; ret == Result.NOT_HANDLED && i < conv.getNumberOfNodeIndexes(); i++) {
			ret = checkForSendMessage(context, protocol,
						i, conv.getNodeIndexAt(i), conv, role, mesg);
		}
		
		for (int i=0; ret == Result.NOT_HANDLED && i < conv.getNestedConversations().size(); i++) {
			Session nested=conv.getNestedConversations().get(i);
			
			if ((ret = messageSent(context, protocol, nested, role, mesg)).isValid()) {
			
				// If nested conversation has a 'main' conversation, then check that it
				// has been terminated
				// TODO: Issue is efficiency of doing this check for each message?
				if (nested.getMainConversation() != null) {
					Session main=nested.getMainConversation();
					
					if (conv.getNestedConversations().remove(main)) {
						
						// Terminate other catch blocks
						for (Session cc : main.getCatchConversations()) {
							if (cc != nested) {
								conv.getNestedConversations().remove(cc);
							}
						}
					}
				}
				
				// If matched, then check if nested conversation has finished
				if (nested.isFinished()) {
					nestedConversationFinished(context, protocol, conv, nested);
				}
			}
		}
		
		return(ret);
	}
	
	protected Result checkForSendMessage(MonitorContext context, Description protocol,
			int pos, int nodeIndex, Session conv, String role, Message mesg) {
		Result ret=Result.NOT_HANDLED;
		
		Node node=protocol.getNode().get(nodeIndex);
		
		if (node instanceof SendMessage) {
			
			if ((ret = checkMessage(context, conv, (SendMessage)node, role, mesg)).isValid()) {
				// Remove processed node
				conv.removeNodeIndexAt(pos);
				
				// Add next node, if not end
				if (node.getNextIndex() != -1) {
					addNodeToConversation(context, protocol, conv, node.getNextIndex());
				}
			}
			
		} else if (node instanceof SendChoice) {
			// Check if lookahead for the indexes associated with each
			// choice path
			for (int j=0; ret == Result.NOT_HANDLED && j < ((SendChoice)node).getPath().size(); j++) {
				
				Path cn=((SendChoice)node).getPath().get(j);
				
				if (cn.getNextIndex() != -1) {
					ret = checkForSendMessage(context, protocol,
								pos, cn.getNextIndex(), conv, role, mesg);
				}
			}
			
		} else if (node instanceof SendDecision) {
			
			if (((SendDecision)node).getInnerIndex() != -1) {
				ret = checkForSendMessage(context, protocol,
							pos, ((SendDecision)node).getInnerIndex(), conv, role, mesg);
			}
			
			if (ret == Result.NOT_HANDLED && node.getNextIndex() != -1) {
				ret = checkForSendMessage(context, protocol,
							pos, node.getNextIndex(), conv, role, mesg);				
			}
		}

		return(ret);
	}
	
	public Result messageReceived(MonitorContext context, Description protocol,
					Session conv, String role, Message mesg) {
		Result ret=Result.NOT_HANDLED;
		
		// Check if context has state that is waiting for a send message
		for (int i=0; ret == Result.NOT_HANDLED && i < conv.getNumberOfNodeIndexes(); i++) {
			ret = checkForReceiveMessage(context, protocol,
						i, conv.getNodeIndexAt(i), conv, role, mesg);
		}
		
		for (int i=0; ret == Result.NOT_HANDLED && i < conv.getNestedConversations().size(); i++) {
			Session nested=conv.getNestedConversations().get(i);
			
			if ((ret = messageReceived(context, protocol, nested, role, mesg)).isValid()) {
				
				// If nested conversation has a 'main' conversation, then check that it
				// has been terminated
				// TODO: Issue is efficiency of doing this check for each message?
				if (nested.getMainConversation() != null) {
					Session main=nested.getMainConversation();
					
					if (conv.getNestedConversations().remove(main)) {
						
						// Terminate other catch blocks
						for (Session cc : main.getCatchConversations()) {
							if (cc != nested) {
								conv.getNestedConversations().remove(cc);
							}
						}
					}
				}
				
				// If matched, then check if nested conversation has finished
				if (nested.isFinished()) {
					nestedConversationFinished(context, protocol, conv, nested);
				}				
			}
		}
		
		return(ret);
	}
	
	protected Result checkForReceiveMessage(MonitorContext context, Description protocol,
				int pos, int nodeIndex, Session conv, String role, Message mesg) {
		Result ret=Result.NOT_HANDLED;
		
		Node node=protocol.getNode().get(nodeIndex);
		
		if (node instanceof ReceiveMessage) {
			
			if ((ret = checkMessage(context, conv, (ReceiveMessage)node, role, mesg)).isValid()) {
				// Remove processed node
				conv.removeNodeIndexAt(pos);
				
				// Add next node, if not end
				if (node.getNextIndex() != -1) {
					addNodeToConversation(context, protocol, conv, node.getNextIndex());
				}
			}
			
		} else if (node instanceof ReceiveChoice) {
			// Check if lookahead for the indexes associated with each
			// choice path
			for (int j=0; ret == Result.NOT_HANDLED && j < ((ReceiveChoice)node).getPath().size(); j++) {
				
				Path cn=((ReceiveChoice)node).getPath().get(j);
				
				if (cn.getNextIndex() != -1) {
					ret = checkForReceiveMessage(context, protocol, 
								pos, cn.getNextIndex(), conv, role, mesg);
				}
			}
			
		} else if (node instanceof ReceiveDecision) {
			
			if (((ReceiveDecision)node).getInnerIndex() != -1) {
				ret = checkForReceiveMessage(context, protocol, 
							pos, ((ReceiveDecision)node).getInnerIndex(), conv, role, mesg);
			}
			
			if (ret == Result.NOT_HANDLED && node.getNextIndex() != -1) {
				ret = checkForReceiveMessage(context, protocol, 
						pos, node.getNextIndex(), conv, role, mesg);				
			}
		}

		return(ret);
	}
	
	
	protected Result checkMessage(MonitorContext context, Session conv, MessageNode node,
								String role, Message sig) {
		Result ret=Result.NOT_HANDLED;
		
		if ((node.getOtherRole() == null || role == null ||
					node.getOtherRole().equals(role)) && (sig.getOperator() == null ||
							node.getOperator() == null ||
							node.getOperator().equals(sig.getOperator()))) {
			ret = context.validate(node, sig);
		}
		
		return(ret);
	}
	
	public Result sendChoice(MonitorContext context, Description protocol,
					Session conv, String role, String label) {
		return(checkChoice(context, protocol, conv, role, label, true));
	}
	
	public Result receiveChoice(MonitorContext context, Description protocol,
					Session conv, String role, String label) {
		return(checkChoice(context, protocol, conv, role, label, false));
	}
	
	protected Result checkChoice(MonitorContext context, Description protocol,
					Session conv, String role, String label, boolean send) {
		Result ret=Result.NOT_HANDLED;
		
		// Check if context has state that is waiting for an appropriate 'send or receive choice'
		for (int i=0; ret == Result.NOT_HANDLED && i < conv.getNumberOfNodeIndexes(); i++) {
			
			Node node=protocol.getNode().get(conv.getNodeIndexAt(i));
			
			if ((node instanceof SendChoice && send) || (node instanceof ReceiveChoice && !send)) {
				ChoiceNode choiceNode=(ChoiceNode)node;
				
				// Check if id is associated with a path
				for (int j=0; ret == Result.NOT_HANDLED && j < choiceNode.getPath().size(); j++) {
					
					Path cn=choiceNode.getPath().get(j);
					
					if (cn.getId().equals(label)) {
						ret = Result.VALID;
						
						// Remove processed node
						conv.removeNodeIndexAt(i);
						
						// Add next node, if not end
						if (cn.getNextIndex() != -1) {
							addNodeToConversation(context, protocol, conv, cn.getNextIndex());
						}
					}
				}
			}
		}
		
		for (int i=0; ret == Result.NOT_HANDLED && i < conv.getNestedConversations().size(); i++) {
			Session nested=conv.getNestedConversations().get(i);
			
			ret = checkChoice(context, protocol, nested, role, label, send);
			
			// If matched, then check if nested conversation has finished
			if (ret.isValid() && nested.isFinished()) {
				nestedConversationFinished(context, protocol, conv, nested);
			}
		}
		
		return(ret);
	}
	
	public Result sendDecision(MonitorContext context, Description protocol,
					Session conv, String role, boolean bool) {
		return(checkDecision(context, protocol, conv, role, bool, true));
	}
	
	public Result receiveDecision(MonitorContext context, Description protocol,
					Session conv, String role, boolean bool) {
		return(checkDecision(context, protocol, conv, role, bool, false));
	}
	
	protected Result checkDecision(MonitorContext context, Description protocol,
					Session conv, String role, boolean bool, boolean send) {
		Result ret=Result.NOT_HANDLED;
		
		// Check if context has state that is waiting for an appropriate 'send or receive decision'
		for (int i=0; ret == Result.NOT_HANDLED && i < conv.getNumberOfNodeIndexes(); i++) {
			Node node=protocol.getNode().get(conv.getNodeIndexAt(i));
			
			if ((node instanceof SendDecision && send) || (node instanceof ReceiveDecision && !send)) {
				int index=node.getNextIndex();
				
				if (bool) {
					index = ((DecisionNode)node).getInnerIndex();
				}
				
				if (index != -1) {
					addNodeToConversation(context, protocol, conv, index);
				}
				
				// Remove processed node
				conv.removeNodeIndexAt(i);
				
				ret = Result.VALID;
			}
		}
		
		for (int i=0; ret == Result.NOT_HANDLED && i < conv.getNestedConversations().size(); i++) {
			Session nested=conv.getNestedConversations().get(i);
			
			ret = checkDecision(context, protocol, nested, role, bool, send);
			
			// If matched, then check if nested conversation has finished
			if (ret.isValid() && nested.isFinished()) {
				nestedConversationFinished(context, protocol, conv, nested);
			}
		}
		
		return(ret);
	}
	
	protected void addNodeToConversation(MonitorContext context, Description protocol,
					Session conv, int nodeIndex) {
		
		// Check if specified node is a 'Run' node type
		Node node=protocol.getNode().get(nodeIndex);

		if (node.getClass() == Scope.class) {
			initScope(context, protocol, conv, (Scope)node);
			
		} else if (node.getClass() == TryNode.class) {
			Session nested=initScope(context, protocol, conv, (TryNode)node);
			
			// TODO: Need to register catch blocks against the nested context in the
			// parent context
			for (int ci : ((TryNode)node).getCatchIndex()) {
				
				if (ci != -1) {
					Session catchScope=conv.createCatchConversation(nested, node.getNextIndex());
					catchScope.addNodeIndex(ci);
					
					//nested.getCatchConversations().add(catchScope);
				}
			}
			
		} else if (node.getClass() == org.scribble.protocol.monitor.model.Call.class) {
			
			if (((org.scribble.protocol.monitor.model.Call)node).getCallIndex() != -1) {
				addNodeToConversation(context, protocol, conv,
						((org.scribble.protocol.monitor.model.Call)node).getCallIndex());
			}
			
			if (node.getNextIndex() != -1) {
				addNodeToConversation(context, protocol, conv, node.getNextIndex());
			}

		} else if (node.getClass() == org.scribble.protocol.monitor.model.ParallelNode.class) {
			
			if (node.getNextIndex() != -1) {
				Session nestedContext=conv.createNestedConversation(node.getNextIndex());
				
				ParallelNode pnode=(ParallelNode)node;
				
				// TODO: Check what happens if no activities in the parallel paths?
				
				for (Path path : pnode.getPath()) {
					if (path.getNextIndex() != -1) {
						addNodeToConversation(context, protocol, nestedContext, path.getNextIndex());
					}
				}
			}
		} else {
			conv.addNodeIndex(nodeIndex);
		}
	}

	/**
	 * This method initializes the supplied scope within the supplied context. This involves
	 * creating a nested context, which is subsequently returned.
	 * 
	 * @param protocol The protocol description
	 * @param conv The current context
	 * @param scope The scope to be initialized
	 * @return The new nested context, or null if failed
	 */
	protected Session initScope(MonitorContext context, Description protocol, Session conv, Scope scope) {
		Session nestedContext=null;
		
		// Check if internal or external run
		if (scope.getInnerIndex() != -1) {
			// Internal
			nestedContext = conv.createNestedConversation(scope.getNextIndex());
			
			addNodeToConversation(context, protocol, nestedContext, scope.getInnerIndex());
			
		} else {
			// External - protocol name is in the 'name' field
			System.err.println("EXTERNAL PROTOCOL MONITORING NOT CURRENTLY SUPPORTED");
		}
		
		return(nestedContext);
	}
	
	protected void nestedConversationFinished(MonitorContext context, Description protocol,
					Session conv, Session nested) {
		// Check if nested conversation has a return index
		if (nested.getReturnIndex() != -1) {
			addNodeToConversation(context, protocol, conv, nested.getReturnIndex());
		}
		
		// Remove nested conversation from parent context
		conv.removeNestedConversation(nested);		
		
		// Cancel dependent contexts
		// TODO: Note this implies immediate cancellation of catch blocks. If
		// we want to delay until subsequent activity, or explicit control message,
		// then will need to change. Issue with subsequent activity is that it
		// may not be within the containing context.
		for (Session cc : nested.getCatchConversations()) {
			conv.removeNestedConversation(cc);
		}
	}	
}
