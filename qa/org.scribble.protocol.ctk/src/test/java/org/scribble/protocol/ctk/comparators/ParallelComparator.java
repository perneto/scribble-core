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
import org.scribble.protocol.model.*;

public class ParallelComparator implements Comparator<ModelObject> {

	@Override
	public int compare(ModelObject arg0, ModelObject arg1) {
		Parallel m=(Parallel)arg0;
		Parallel e=(Parallel)arg1;
		
		if (m.getBlocks().size() != e.getBlocks().size()) {
			return(1);
		}
		
		Comparator<java.util.List<org.scribble.common.model.Annotation>> ancomp=new AnnotationsComparator();	
		
		if (ancomp.compare(m.getAnnotations(), e.getAnnotations()) != 0) {
			return(1);
		}
		
		return(0);
	}
}
