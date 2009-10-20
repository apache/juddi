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

import org.uddi.sub_v3.SubscriptionResultsList;


/**
 * <p>Java class for publisherDetail type. Specific to juddi.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "syncSubscriptionDetail", propOrder = {
    "list"
})
public class SyncSubscriptionDetail implements Serializable{
	
	@XmlTransient
	private static final long serialVersionUID = -4039550325209019888L;
	private List<SubscriptionResultsList> list;

	public List<SubscriptionResultsList> getSubscriptionResultsList() {
		if (this.list==null) {
			this.list = new ArrayList<SubscriptionResultsList>();
		}
        return this.list;
    }

}
