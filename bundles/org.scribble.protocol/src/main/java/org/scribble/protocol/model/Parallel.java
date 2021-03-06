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
 * This class represents the Parallel construct with
 * two or more concurrent paths.
 * 
 */
public class Parallel extends Activity {
	
	private java.util.List<Block> m_blocks=new ContainmentList<Block>(this, Block.class);

	/**
	 * This is the default constructor.
	 * 
	 */
	public Parallel() {
	}
	
	/**
	 * This method returns the list of mutually exclusive
	 * activity blocks that comprise the multi-path construct.
	 * 
	 * @return The list of blocks
	 */
	public java.util.List<Block> getBlocks() {
		return(m_blocks);
	}
	
	/**
	 * This method visits the model object using the supplied
	 * visitor.
	 * 
	 * @param visitor The visitor
	 */
	public void visit(Visitor visitor) {
		visitor.start(this);
		
		for (Block b : getBlocks()) {
			b.visit(visitor);
		}
		
		visitor.end(this);
	}
}
