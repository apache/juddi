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
import org.uddi.sub_v3.GetSubscriptionResults;

/**
 * <p>Java class for syncSubscription complex type.
 *
 * <p>The following schema fragment specifies the expected content contained
 * within this class.
 *
 * <pre>
 * &lt;complexType name="syncSubscription">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="authInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="list" type="{urn:uddi-org:sub_v3}get_subscriptionResults" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "syncSubscription", propOrder = {
        "authInfo",
        "list"
})
public class SyncSubscription implements Serializable {

        @XmlTransient
        private static final long serialVersionUID = -8839314757295513399L;
        private String authInfo;
        @XmlElement(nillable = true)
        private List<GetSubscriptionResults> list;

        /**
         * Gets the value of the authInfo property.
         *
         * @return possible object is {@link String }
         *
         */
        public String getAuthInfo() {
                return authInfo;
        }

        /**
         * Sets the value of the authInfo property.
         *
         * @param value allowed object is {@link String }
         *
         */
        public void setAuthInfo(String value) {
                this.authInfo = value;
        }

        /**
         * Gets the value of the list property.
         *
         * <p>
         * This accessor method returns a reference to the live list, not a
         * snapshot. Therefore any modification you make to the returned list
         * will be present inside the JAXB object. This is why there is not a
         * <CODE>set</CODE> method for the list property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getList().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
     * {@link GetSubscriptionResults }
         *
         *
         */
        public List<GetSubscriptionResults> getList() {
                if (list == null) {
                        list = new ArrayList<GetSubscriptionResults>();
                }
                return this.list;
        }
}
