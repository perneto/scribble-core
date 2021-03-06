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
 * This class represents the Raise construct.
 * 
 */
public class Recursion extends Activity {

    private String m_label=null;

	/**
	 * This is the default constructor.
	 * 
	 */
	public Recursion() {
	}
	
	/**
	 * This method returns the label associated with the recursion construct.
	 * 
	 * @return The label
	 */
	public String getLabel() {
		return(m_label);
	}
	
	/**
	 * This method sets the label associated with the recursion construct.
	 * 
	 * @param label The label
	 */
	public void setLabel(String label) {
		m_label = label;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Recursion that = (Recursion) o;

        return !(m_label != null
                ? !m_label.equals(that.m_label)
                : that.m_label != null);
    }

    @Override
    public int hashCode() {
        return m_label != null ? m_label.hashCode() : 0;
    }

    @Override
    public String toString() {
        return '#' + m_label;
    }
}
