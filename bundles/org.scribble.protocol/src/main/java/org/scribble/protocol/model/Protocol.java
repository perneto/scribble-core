/*
 * Copyright 2009 www.scribble.org
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
package org.scribble.protocol.model;

/**
 * This class represents the protocol notation.
 */
public class Protocol extends Activity {
	
	private String m_name=null;
	private Role m_role=null;
	private Block m_block=null;
	private java.util.List<ParameterDefinition> m_parameterDefs=
		new ContainmentList<ParameterDefinition>(this, ParameterDefinition.class);

	/**
	 * The default constructor.
	 */
	public Protocol() {
	}
	
	/**
	 * This method returns the name.
	 * 
	 * @return The name
	 */
	public String getName() {
		return(m_name);
	}
	
	/**
	 * This method sets the name.
	 * 
	 * @param name The name
	 */
	public void setName(String name) {
		m_name = name;
	}
	
	/**
	 * This method returns the located role. This
	 * field is set when the protocol represents a local
	 * model.
	 * 
	 * @return The located role
	 */
	public Role getRole() {
		return(m_role);
	}
	
	/**
	 * This method sets the located role. This
	 * field is set when the protocol represents a local
	 * model.
	 * 
	 * @param role The located role
	 */
	public void setRole(Role role) {
		
		if (m_role != null) {
			m_role.setParent(null);
		}
		
		m_role = role;
		
		if (m_role != null) {
			m_role.setParent(this);
		}
	}
	
	/**
	 * This method returns the parameters associated with
	 * the protocol.
	 * 
	 * @return The parameter definitions
	 */
	public java.util.List<ParameterDefinition> getParameterDefinitions() {
		return(m_parameterDefs);
	}
	
	/**
	 * This method returns the block of activities associated
	 * with the definition.
	 * 
	 * @return The block of activities
	 */
	public Block getBlock() {
		
		if (m_block == null) {
			m_block = new Block();
			m_block.setParent(this);
		}
		
		return(m_block);
	}
	
	/**
	 * This method sets the block of activities associated
	 * with the definition.
	 * 
	 * @param block The block of activities
	 */
	public void setBlock(Block block) {
		if (m_block != null) {
			m_block.setParent(null);
		}
		
		m_block = block;
		
		if (m_block != null) {
			m_block.setParent(this);
		}
	}
	
	/**
	 * This method returns the model in which this definition
	 * is contained.
	 * 
	 * @return The model, or null if not contained within
	 * 					a model
	 */
	public ProtocolModel getModel() {
		ProtocolModel ret=null;
		ModelObject cur=this;
		
		while (ret == null && cur != null) {
			if (cur instanceof ProtocolModel) {
				ret = (ProtocolModel)cur;
			} else {
				cur = cur.getParent();
			}
		}
		
		return(ret);
	}
	
	/**
	 * This method returns the protocol in which this
	 * activity is contained.
	 * 
	 * @return The protocol, or null if not found
	 */
	@Override
	public Protocol enclosingProtocol() {
		return(this);
	}
	
	/**
	 * This method returns the top level protocol.
	 * 
	 * @return The top level protocol
	 */
	public Protocol getTopLevelProtocol() {
		Protocol ret=this;
		
		if (getParent() instanceof Protocol) {
			ret = ((Protocol)getParent()).getTopLevelProtocol();
		}
		
		return(ret);
	}
	
	/**
	 * This method returns the sub-protocol associated
	 * with the supplied name.
	 * 
	 * @param name The name
	 * @return The sub-protocol for the supplied name,
	 * 				or null if not found
	 */
	public Protocol getSubProtocol(String name) {
		Protocol ret=null;
	
		for (int i=0; ret == null &&
				i < getBlock().getContents().size(); i++) {
			Activity act=getBlock().getContents().get(i);
			
			if (act instanceof Protocol &&
					((Protocol)act).getName().equals(name)) {
				ret = (Protocol)act;
			}
		}
		
		return(ret);
	}
	
	/**
	 * This method returns the list of roles defined within
	 * the protocol definition.
	 * 
	 * @return The list of roles
	 */
	public java.util.List<Role> getRoles() {
		final java.util.List<Role> ret=new java.util.Vector<Role>();
		
		for (ParameterDefinition p : getParameterDefinitions()) {
			if (p.isRole()) {
				ret.add(p.getRole());
			}
		}
		
		visit(new DefaultVisitor() {
			
			public boolean start(Protocol elem) {
				return(Protocol.this == elem);
			}
			
			public void accept(RoleList elem) {
				ret.addAll(elem.getRoles());
			}

		});
		
		/*
		for (int i=0; i < getBlock().getContents().size(); i++) {
		
			if (getBlock().getContents().get(i) instanceof RoleList) {
				ret.addAll(((RoleList)getBlock().getContents().get(i)).getRoles());
			}
		}
		*/
		
		return(ret);
	}
	
	/**
	 * This method visits the model object using the supplied
	 * visitor.
	 * 
	 * @param visitor The visitor
	 */
	public void visit(Visitor visitor) {
		
		if (visitor.start(this)) {
		
			if (getRole() != null) {
				getRole().visit(visitor);
			}
			
			if (getBlock() != null) {
				getBlock().visit(visitor);
			}
		}
		
		visitor.end(this);
	}
}
