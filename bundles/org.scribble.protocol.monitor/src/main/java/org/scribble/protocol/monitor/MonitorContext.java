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
package org.scribble.protocol.monitor;

import org.scribble.protocol.monitor.model.MessageNode;

/**
 * This interface provides access to capabilities offered by
 * the monitor.
 *
 */
public interface MonitorContext {

	/**
	 * This method determines whether the supplied message is valid
	 * in respect of the supplied message node.
	 * 
	 * If the message is not relevant, then a Result.NOT_HANDLED
	 * should be returned. Otherwise Result.VALID, Result.INVALID
	 * or a specific instance of Result should be returned to provide
	 * the relevant information from the monitoring environment's
	 * validation.
	 * 
	 * @param mesgNode The message node
	 * @param mesg The message to be validated
	 * @return The result
	 */
	public Result validate(MessageNode mesgNode, Message mesg);
	
}
