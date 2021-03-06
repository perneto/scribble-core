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

import org.scribble.protocol.model.*;
import org.scribble.common.logging.Journal;

/**
 * This class provides the ImportList implementation of the
 * projector rule.
 */
public class TypeImportListProjectorRule implements ProjectorRule {

	/**
	 * This method determines whether the projection rule is
	 * appropriate for the supplied model object.
	 * 
	 * @param obj The model object to be projected
	 * @return Whether the rule is relevant for the
	 * 				model object
	 */
	public boolean isSupported(ModelObject obj) {
		return(obj.getClass() == TypeImportList.class);
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
		TypeImportList ret=new TypeImportList();
		TypeImportList source=(TypeImportList)model;
		
		ret.derivedFrom(source);
		
		ret.setFormat(source.getFormat());
		ret.setLocation(source.getLocation());
		
		for (int i=0; i < source.getTypeImports().size(); i++) {
			TypeImport p=(TypeImport)context.project(source.getTypeImports().get(i), role, l);

			if (p != null) {
				ret.getTypeImports().add(p);
			}
		}
		
		return(ret);
	}
}
