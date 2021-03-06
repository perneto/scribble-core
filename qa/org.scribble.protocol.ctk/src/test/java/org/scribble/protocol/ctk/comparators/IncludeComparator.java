/*
 * Copyright 2009-10 www.scribble.org
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
package org.scribble.protocol.ctk.comparators;

import java.util.Comparator;

import org.scribble.protocol.ctk.ComparatorUtil;
import org.scribble.protocol.model.*;

public class IncludeComparator implements Comparator<ModelObject> {

	@Override
	public int compare(ModelObject arg0, ModelObject arg1) {
		Include m=(Include)arg0;
		Include e=(Include)arg1;
		
		if (m.getReference() != null && e.getReference() != null) {
			ProtocolReferenceComparator prcomp=(ProtocolReferenceComparator)
					ComparatorUtil.getComparator(ProtocolReference.class);
			
			if (prcomp.compare(m.getReference(), e.getReference()) != 0) {
				return(1);
			}
		} else if (m.getReference() != null || e.getReference() != null) {
			return(1);
		}

		if (m.getParameters().size() != e.getParameters().size()) {
			return(1);
		}
		
		ParameterComparator dbc=(ParameterComparator)
					ComparatorUtil.getComparator(Parameter.class);
		
		for (int i=0; i < m.getParameters().size(); i++) {
			if (dbc.compare(m.getParameters().get(i), e.getParameters().get(i)) != 0) {
				return(1);
			}
		}
		
		return(0);
	}
}
