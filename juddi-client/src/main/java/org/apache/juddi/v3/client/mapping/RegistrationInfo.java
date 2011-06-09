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
import java.net.URL;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class RegistrationInfo implements Serializable{
	
	private static final long serialVersionUID = 8589346728228576240L;
	private QName serviceQName;
	private String portName;
	private URL serviceUrl;
	private URL wsdlUrl;
	private Definition wsdlDefinition;
	private String version;
	private RegistrationType registrationType;
	
	public QName getServiceQName() {
		return serviceQName;
	}
	public void setServiceQName(QName serviceQName) {
		this.serviceQName = serviceQName;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public URL getServiceUrl() {
		return serviceUrl;
	}
	public void setServiceUrl(URL serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	public Definition getWsdlDefinition() {
		return wsdlDefinition;
	}
	public void setWsdlDefinition(Definition wsdlDefinition) {
		this.wsdlDefinition = wsdlDefinition;
	}
	public RegistrationType getRegistrationType() {
		return registrationType;
	}
	public void setRegistrationType(RegistrationType registrationType) {
		this.registrationType = registrationType;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public URL getWsdlUrl() {
		return wsdlUrl;
	}
	public void setWsdlUrl(URL wsdlUrl) {
		this.wsdlUrl = wsdlUrl;
	}
	
	
}
