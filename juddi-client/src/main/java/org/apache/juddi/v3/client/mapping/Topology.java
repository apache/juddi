/*
 * Copyright 2001-2011 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.v3.client.mapping;

import java.io.Serializable;
import java.util.List;

/**
 * Data structure to store a list of Endpoint References (ERPs). 
 * Given a certain Policy an EPR is selected from the list.
 * The Policy implementation can use the Pointer to keep track
 * of which EPR was used last.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class Topology implements Serializable{
	
	public Topology(List<String> eprs) {
		super();
		this.eprs = eprs;
	}
	private static final long serialVersionUID = 3884817160534937195L;
	/** List of Endpoint References (EPRs) for a certain service Key */
	private List<String> eprs;
	/** pointer to the last selected EPR from the list */
	private int pointer = 0;
	/** can be set by a policy if for instance local first is important */
	private Boolean hasLocal = null;
	
	
	public Boolean getHasLocal() {
		return hasLocal;
	}
	public void setHasLocal(Boolean hasLocal) {
		this.hasLocal = hasLocal;
	}
	public List<String> getEprs() {
		return eprs;
	}
	public void setEprs(List<String> eprs) {
		this.eprs = eprs;
	}
	public int getPointer() {
		return pointer;
	}
	public void setPointer(int pointer) {
		this.pointer = pointer;
	}
}
