/*
 * Copyright 2009 www.scribble.org
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
package org.scribble.protocol.model;

/**
 * This class represents an imported protocol.
 * 
 */
public class ProtocolImport extends ModelObject {

	private String m_location=null;	
	private String m_name=null;

	/**
	 * The default constructor.
	 */
	public ProtocolImport() {
	}
	
	/**
	 * This method returns the name of the
	 * type being imported.
	 * 
	 * @return The name
	 */
	public String getName() {
		return(m_name);
	}
	
	/**
	 * This method sets the name of the
	 * type being imported.
	 * 
	 * @param name The name
	 */
	public void setName(String name) {
		m_name = name;
	}
	
	/**
	 * This method returns the location of the schema.
	 * 
	 * @return
	 */
	public String getLocation() {
		return(m_location);
	}
	
	/**
	 * This method sets the location of the schema.
	 * 
	 * @param location The location
	 */
	public void setLocation(String location) {
		m_location = location;
	}
	
	/**
	 * This method visits the model object using the supplied
	 * visitor.
	 * 
	 * @param visitor The visitor
	 */
	public void visit(Visitor visitor) {
		visitor.accept(this);
	}
	
	public String toString() {
		String ret=getName();
		
		if (ret == null) {
			ret = "<Unnamed Protocol>";
		}
		
		return(ret);
	}
}
