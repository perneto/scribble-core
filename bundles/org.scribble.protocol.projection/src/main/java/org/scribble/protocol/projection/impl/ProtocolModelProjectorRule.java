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
package org.scribble.protocol.projection.impl;

import java.text.MessageFormat;

import org.scribble.common.logging.Journal;
import org.scribble.protocol.model.*;

/**
 * This class provides the ProtocolModel implementation of the
 * projector rule.
 */
public class ProtocolModelProjectorRule implements ProjectorRule {
		
	/**
	 * This method determines whether the projection rule is
	 * appropriate for the supplied model object.
	 * 
	 * @param obj The model object to be projected
	 * @return Whether the rule is relevant for the
	 * 				model object
	 */
	public boolean isSupported(ModelObject obj) {
		return(obj.getClass() == ProtocolModel.class);
	}
	
	/**
	 * This method projects the supplied model object based on the
	 * specified role.
	 * 
	 * @param model The model object
	 * @param role The role
	 * @param l The model listener
	 * @return The projected model object
	 */
	public ModelObject project(ProjectorContext context, ModelObject model,
					Role role, Journal l) {
		ProtocolModel ret=new ProtocolModel();
		ProtocolModel source=(ProtocolModel)model;
		
		ret.derivedFrom(source);
		
		// Project import statements
		for (int i=0; i < source.getImports().size(); i++) {
			
			ImportList newImport=(ImportList)
					context.project(source.getImports().get(i),
								role, l);
			
			if (newImport != null) {
				ret.getImports().add(newImport);
			}
		}
		
		Protocol protocol=null;
		
		// Get enclosing protocol for the supplied role
		Role roleDefn=null;
		for (Role sr : source.getRoles()) {
			if (sr.equals(role)) {
				roleDefn = sr;
				break;
			}
		}
		
		if (roleDefn == null) {
			l.error(MessageFormat.format(
					java.util.PropertyResourceBundle.getBundle(
					"org.scribble.protocol.projection.Messages").getString("_UNKNOWN_ROLE"),
						role.getName()), ret.getProperties());
			ret = null;
		} else {
		
			// Get enclosing protocol for the supplied role
			Protocol srcprotocol=((RoleList)roleDefn.getParent()).enclosingProtocol();
		
			if (srcprotocol != null) {
				protocol = (Protocol)context.project(srcprotocol,
							role, l);
			}
			
			ret.setProtocol(protocol);
		}
		
		return(ret);
	}
}
