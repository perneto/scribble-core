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
package org.scribble.protocol.parser.antlr;

import java.io.IOException;
import java.io.InputStream;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.scribble.protocol.ProtocolContext;
import org.scribble.protocol.model.*;
import org.scribble.protocol.parser.AnnotationProcessor;
import org.scribble.protocol.parser.ProtocolParser;
import org.scribble.common.logging.Journal;

/**
 * This class provides the ANTLR implementation of the Protocol Parser
 * interface.
 *
 */
public class ANTLRProtocolParser implements ProtocolParser {

	private AnnotationProcessor m_annotationProcessor=null;

	public ANTLRProtocolParser() {
	}
	
	public boolean isSupported(String sourceType) {
		return(org.scribble.protocol.ProtocolDefinitions.PROTOCOL_TYPE.equals(sourceType));
	}

	public ProtocolModel parse(InputStream is, Journal journal,
							ProtocolContext context) throws IOException {
		ProtocolModel ret=null;
		
        try {
        	byte[] b=new byte[is.available()];
        	is.read(b);
        	
        	is.close();
        	
        	String document=new String(b);
        	
            ScribbleProtocolLexer lex = new ScribbleProtocolLexer(new ANTLRStringStream(document));
           	CommonTokenStream tokens = new CommonTokenStream(lex);
           	
    		ScribbleProtocolParser parser = new ScribbleProtocolParser(tokens);

    		ProtocolTreeAdaptor adaptor=new ProtocolTreeAdaptor(m_annotationProcessor, journal);
    		adaptor.setParser(parser);
    		
    		parser.setDocument(document);
    		parser.setTreeAdaptor(adaptor);
    		
    		parser.setJournal(journal);

    		parser.description();
    		
    		if (parser.isErrorOccurred() == false) {
    			ret = adaptor.getProtocolModel();
    		}
            
        } catch (Exception e)  {
            e.printStackTrace();
        }
		
		return(ret);
	}

	/**
	 * This method sets an annotation processor.
	 * 
	 * @param ap The annotation processor
	 */
	public void setAnnotationProcessor(AnnotationProcessor ap) {
		m_annotationProcessor = ap;
	}
}
