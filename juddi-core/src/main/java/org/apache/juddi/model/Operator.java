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
package org.apache.juddi.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "j3_operator")
public class Operator implements Serializable {

        private String operatorNodeID;
        private OperatorStatusType operatorStatus;
        private List<Contact> contact;
        private String soapReplicationURL;
        private List<KeyInfo> keyInfo;

        @Id
        public String getOperatorNodeID() {
                return operatorNodeID;
        }
        public void setOperatorNodeID(String value) {
                this.operatorNodeID = value;
        }

        @Column(name = "operator_status")
        public OperatorStatusType getOperatorStatus() {
                return operatorStatus;
        }

        public void setOperatorStatus(OperatorStatusType value) {
                this.operatorStatus = value;
        }

        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = Contact.class)
        public List<Contact> getContact() {
                if (contact == null) {
                        contact = new ArrayList<Contact>();
                }
                return this.contact;
        }

        public void setContact(List<Contact> c) {

                this.contact = c;
        }

        @Column(name = "replicationurl")
        public String getSoapReplicationURL() {
                return soapReplicationURL;
        }

        public void setSoapReplicationURL(String value) {
                this.soapReplicationURL = value;
        }

        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = KeyInfo.class)
        public List<KeyInfo> getKeyInfo() {
                if (keyInfo == null) {
                        keyInfo = new ArrayList<KeyInfo>();
                }
                return this.keyInfo;
        }
        
        public void setKeyInfo(List<KeyInfo> c) {
                this.keyInfo=c;
        }
}
