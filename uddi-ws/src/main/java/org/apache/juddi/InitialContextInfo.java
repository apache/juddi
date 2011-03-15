/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
package org.apache.juddi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@XmlRootElement
public class InitialContextInfo implements Serializable {

	@XmlTransient
	private static final long serialVersionUID = 5639256817185443090L;
	
	@XmlElement
	protected List<Property> contextProperty;
	
	public InitialContextInfo() {
		super();
	}

	public List<Property> getContextProperty() {
		if (contextProperty==null) {
			contextProperty = new ArrayList<Property>();
		}
		return contextProperty;
	}

	public void setProperty(List<Property> contextProperty) {
		this.contextProperty = contextProperty;
	}

}
