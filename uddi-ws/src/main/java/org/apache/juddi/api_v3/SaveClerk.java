/*
 * Copyright 2001-2008 The Apache Software Foundation.
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

package org.apache.juddi.api_v3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for save_subscriptionInfo type.  Specific to juddi.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "save_clerkInfo", propOrder = {
	"authInfo",
    "clerk"
})
public class SaveClerk implements Serializable{
	
	@XmlTransient
	private static final long serialVersionUID = 2909722102936229047L;
	protected String authInfo;
    @XmlElement(required = true)
    protected List<Clerk> clerk;
    
	public String getAuthInfo() {
		return authInfo;
	}
	
	public void setAuthInfo(String authInfo) {
		this.authInfo = authInfo;
	}
	
	public List<Clerk> getClerk() {
		if (clerk == null) {
			clerk = new ArrayList<Clerk>();
		}
		return clerk;
	}
	
	

}
