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
package org.scribble.protocol.export;

/**
 * This interface manages a set of protocol exporters.
 *
 */
public interface ProtocolExportManager {
	
	/**
	 * This method adds a new exporter to the manager.
	 * 
	 * @param exporter The exporter
	 */
	public void addExporter(ProtocolExporter exporter);

	/**
	 * This method removes an existing exporter from the manager.
	 * 
	 * @param exporter The exporter
	 * @return Whether the exporter has been removed
	 */
	public boolean removeExporter(ProtocolExporter exporter);

	/**
	 * This method returns the protocol exporter associated with
	 * the supplied id.
	 * 
	 * @param id The id
	 * @return The exporter, or null if not found
	 */
	public ProtocolExporter getExporter(String id);
	
	/**
	 * This method returns the list of exporters registered
	 * with the manager.
	 * 
	 * @return The list of exporters
	 */
	public java.util.List<ProtocolExporter> getExporters();
	
}
