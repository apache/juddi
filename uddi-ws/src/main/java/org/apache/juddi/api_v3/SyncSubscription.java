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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.uddi.sub_v3.GetSubscriptionResults;


/**
 * <p>Java class for publisherDetail type. Specific to juddi.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "syncSubscription", propOrder = {
	"authInfo",
    "list"
})
public class SyncSubscription implements Serializable{
	
	@XmlTransient
	private static final long serialVersionUID = -8839314757295513399L;
	private String authInfo;
	private List<GetSubscriptionResults> list;
	
	/**
     * Gets the value of the authInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthInfo() {
        return authInfo;
    }

    /**
     * Sets the value of the authInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthInfo(String value) {
        this.authInfo = value;
    }
    
	public List<GetSubscriptionResults> getGetSubscriptionResultsList() {
		if (this.list==null) {
			this.list = new ArrayList<GetSubscriptionResults>();
		}
		return list;
	}

	

}
