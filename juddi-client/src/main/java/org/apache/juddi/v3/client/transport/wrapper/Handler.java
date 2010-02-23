/*
 * Copyright 2001-2010 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.juddi.v3.client.transport.wrapper;

public class Handler {
	private String methodName;
	private Class<?> parameter;

	public Handler() {
		super();
	}
	
	public Handler(String methodName, Class<?> parameter) {
		super();
		this.methodName = methodName;
		this.parameter = parameter;
	}
	
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Class<?> getParameter() {
		return parameter;
	}
	public void setParameter(Class<?> parameter) {
		this.parameter = parameter;
	}	
}
