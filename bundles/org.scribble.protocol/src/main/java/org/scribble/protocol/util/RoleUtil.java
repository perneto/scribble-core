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
package org.scribble.protocol.util;

import org.scribble.protocol.model.*;

/**
 * This class provides utility functions related to the Role protocol
 * component.
 *
 */
public class RoleUtil {

	/**
	 * This method returns the roles defined within the parent scope of the
	 * supplied activity.
	 * 
	 * @param block The parent scope
	 * @return The set of roles
	 */
	public static java.util.Set<Role> getRoles(Block block) {
		java.util.Set<Role> ret=new java.util.HashSet<Role>();
		
		for (int i=0; i < block.getContents().size(); i++) {
			
			if (block.getContents().get(i) instanceof RoleList) {
				ret.addAll(((RoleList)block.getContents().get(i)).getRoles());
			}
		}
		
		return(ret);
	}
	
	/**
	 * This method determines the set of roles that are in the
	 * scope for the supplied activity.
	 * 
	 * @param activity The activity
	 * @return The set of roles in scope for the supplied activity
	 */
	public static java.util.Set<Role> getRolesInScope(Activity activity) {
		java.util.Set<Role> ret=new java.util.HashSet<Role>();
		
		if (activity != null) {
			
			// Identify enclosing protocol definition
			Protocol protocol=activity.enclosingProtocol();
			
			if (protocol != null) {
				RoleLocator visitor=new RoleLocator(protocol, activity, ret);
				
				protocol.visit(visitor);
			}
		}
		
		return(ret);
	}
	
	public static class RoleLocator implements org.scribble.protocol.model.Visitor {
		
		public RoleLocator(Protocol protocol, Activity activity, 
								java.util.Set<Role> result) {
			m_protocol = protocol;
			m_activity = activity;
			m_result = result;
		}
		
		public boolean start(Block elem) {
			return(startBlock(elem));	
		}
		
		protected boolean startBlock(Block elem) {
			// Push new role list onto the stack
			m_roleStack.add(new java.util.Vector<Role>());
			
			return(true);
		}
		
		public void end(Block elem) {
			endBlock(elem);
		}
		
		protected void endBlock(Block elem) {
			// Pop top role list from the stack
			m_roleStack.remove(m_roleStack.size()-1);
		}
		
		public void end(Choice elem) {
		}

		public void end(Optional elem) {
		}

		public void end(Unordered elem) {
		}

		public void end(Parallel elem) {
		}

		public void end(Protocol elem) {
		}

		public void end(Repeat elem) {
		}

		public void end(RecBlock elem) {
		}

		public void end(Try elem) {
		}

		public void end(Catch elem) {
		}

		public void end(Run elem) {
		}

		public boolean start(Choice elem) {
			checkActivity(elem);
			return(m_recurse);
		}

		public boolean start(When elem) {
			return(m_recurse);
		}
	
		public void end(When elem) {
		}
		
		public boolean start(Optional elem) {
			checkActivity(elem);
			return(m_recurse);
		}

		public boolean start(Unordered elem) {
			checkActivity(elem);
			return(m_recurse);
		}

		public boolean start(Parallel elem) {
			checkActivity(elem);
			return(m_recurse);
		}

		public boolean start(Protocol elem) {
			// If this is the enclosing protocol, then
			// recursively visit it - otherwise don't
			boolean ret=(m_protocol==elem);
			
			if (ret) {
				java.util.List<Role> rlist=new java.util.Vector<Role>();
				m_roleStack.add(rlist);
				
				if (elem.getRole() != null) {
					rlist.add(elem.getRole());
				}
				
				for (ParameterDefinition p : elem.getParameterDefinitions()) {
					if (p.getType() == null) {
						rlist.add(new Role(p.getName()));
					}
				}
				
			}
			
			return(ret);
		}

		public boolean start(Repeat elem) {
			checkActivity(elem);
			return(m_recurse);
		}

		public boolean start(RecBlock elem) {
			checkActivity(elem);
			return(m_recurse);
		}

		public boolean start(Try elem) {
			checkActivity(elem);
			return(m_recurse);
		}

		public boolean start(Catch elem) {
			for (Interaction interaction : elem.getInteractions()) {
				checkActivity(interaction);
			}
			return(m_recurse);
		}

		public boolean start(Run elem) {
			checkActivity(elem);
			return(m_recurse);
		}

		public void accept(TypeImportList elem) {
		}

		public void accept(ProtocolImportList elem) {
		}

		public void accept(Interaction elem) {
			checkActivity(elem);
		}

		public void accept(RoleList elem) {
			java.util.List<Role> rlist=m_roleStack.get(m_roleStack.size()-1);
			
			rlist.addAll(elem.getRoles());
		}

		public void accept(Raise elem) {
			checkActivity(elem);
		}

		public void accept(Include elem) {
			checkActivity(elem);
		}
		
		public void accept(Recursion elem) {
			checkActivity(elem);
		}
		
		public void accept(TypeImport elem) {			
		}
		
		public void accept(ProtocolImport elem) {
		}
		
		protected void checkActivity(Activity elem) {
			
			if (elem == m_activity) {
				// Record the current stack of roles in the result
				for (java.util.List<Role> plist : m_roleStack) {
					
					for (Role p : plist) {
						m_result.add(p);
					}
				}
				
				// Remainder of description should be traversed quickly
				m_recurse = false;
			}
		}
		
		private boolean m_recurse=true;
		private Protocol m_protocol=null;
		private Activity m_activity=null;
		private java.util.Set<Role> m_result=null;
		private java.util.List<java.util.List<Role>> m_roleStack=
					new java.util.Vector<java.util.List<Role>>();
	}
}
