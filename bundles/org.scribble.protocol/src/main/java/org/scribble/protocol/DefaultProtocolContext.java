/*
 * Copyright 2009-11 www.scribble.org
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
package org.scribble.protocol;

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.scribble.common.logging.Journal;
import org.scribble.common.resource.ResourceLocator;
import org.scribble.protocol.model.ProtocolImport;
import org.scribble.protocol.model.ProtocolModel;
import org.scribble.protocol.parser.ProtocolParserManager;

/**
 * This interface represents the context in which a protocol related
 * tool will operate.
 *
 */
public class DefaultProtocolContext implements ProtocolContext {

	private ProtocolParserManager m_parserManager=null;
	private ResourceLocator m_resourceLocator=null;
	
	private static Logger logger=Logger.getLogger(DefaultProtocolContext.class.getName());
	
	/**
	 * This is the constructor for the default protocol context
	 * implementation.
	 * 
	 * @param parserManager The parser manager
	 * @param locator The resource locator
	 */
	public DefaultProtocolContext(ProtocolParserManager parserManager,
						ResourceLocator locator) {
		m_parserManager = parserManager;
		m_resourceLocator = locator;
	}
	
	/**
	 * This method returns the resource locator.
	 * 
	 * @return The resource locator
	 */
	public ResourceLocator getResourceLocator() {
		return(m_resourceLocator);
	}
	
	/**
	 * This method retrieves a protocol model associated with a protocol
	 * import statement.
	 *  
	 * @param pi The protocol import
	 * @param journal The journal for reporting issues
	 * @return The protocol model, or null if not found
	 */
	public ProtocolModel getProtocolModel(ProtocolImport pi, Journal journal) {
		ProtocolModel ret=null;
		
		if (pi.getLocation() == null || pi.getLocation().trim().length() == 0) {
			journal.error("Protocol import does not define a location", pi.getProperties());
		} else {

			try {
				java.net.URI uri=getResourceLocator().getResourceURI(pi.getLocation());
				
				java.io.InputStream is=uri.toURL().openStream();
									
				if (is != null) {
					String sourceType=null;
					int index=pi.getLocation().lastIndexOf('.');
					if (index != -1) {
						sourceType = pi.getLocation().substring(index+1);
					
						ret = m_parserManager.parse(sourceType, is, journal, this);
					} else {
						journal.error("Unable to determine source type for '"+
									pi.getLocation()+"'", pi.getProperties());
					}
					
					is.close();
				}
				
			} catch(MalformedURLException mue) {
				journal.error("Invalid URL '"+mue+"'", pi.getProperties());
			} catch(Exception e) {
				logger.log(Level.SEVERE, "Failed to read protocol from '"+pi.getLocation()+"'", e);
				
				journal.error("Failed to read protocol from '"+pi.getLocation()+"'",
							pi.getProperties());
			}
		}
		
		return(ret);
	}
	
}
