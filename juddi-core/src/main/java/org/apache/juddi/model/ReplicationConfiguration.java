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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "j3_chg_replconf")
public class ReplicationConfiguration implements Serializable {

        private static final long serialVersionUID = 1L;
        
        private long serialNumber;
        private String timeOfConfigurationUpdate;
        private List<Operator> operator = new ArrayList<Operator>(0);
        private CommunicationGraph communicationGraph;
        private BigInteger maximumTimeToSyncRegistry;
        private BigInteger maximumTimeToGetChanges;
        private List<Signature> signatures = new ArrayList<Signature>(0);
        private Contact contact;

        /**
         * Gets the value of the contact property.
         *
         * @return possible object is {@link Contact }
         *
         */
        @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = Contact.class)
        public Contact getContact() {
                return contact;
        }

        /**
         * Sets the value of the contact property.
         *
         * @param value allowed object is {@link Contact }
         *
         */
        public void setContact(Contact value) {
                this.contact = value;
        }

        /**
         * Gets the value of the serialNumber property.
         *
         */
        @Column(name = "serialnumb")
        @OrderBy(value = "SerialNumber DESC")
        @Id
        public long getSerialNumber() {
                return serialNumber;
        }

        /**
         * Sets the value of the serialNumber property.
         *
         */
        public void setSerialNumber(long value) {
                this.serialNumber = value;
        }

        /**
         * Gets the value of the timeOfConfigurationUpdate property.
         *
         * @return possible object is {@link String }
         *
         */
        @Column(name = "configupdate")
        public String getTimeOfConfigurationUpdate() {
                return timeOfConfigurationUpdate;
        }

        /**
         * Sets the value of the timeOfConfigurationUpdate property.
         *
         * @param value allowed object is {@link String }
         *
         */
        public void setTimeOfConfigurationUpdate(String value) {
                this.timeOfConfigurationUpdate = value;
        }

        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = Operator.class)
        public List<Operator> getOperator() {
                if (operator == null) {
                        operator = new ArrayList<Operator>();
                }
                return this.operator;
        }
        
        
         public void setOperator(List<Operator> v) {
                
                this.operator=v;
        }

        /**
         * Gets the value of the communicationGraph property.
         *
         * @return possible object is {@link CommunicationGraph }
         *
         */
        @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = CommunicationGraph.class)
        public CommunicationGraph getCommunicationGraph() {
                return communicationGraph;
        }

        /**
         * Sets the value of the communicationGraph property.
         *
         * @param value allowed object is {@link CommunicationGraph }
         *
         */
        public void setCommunicationGraph(CommunicationGraph value) {
                this.communicationGraph = value;
        }

        /**
         * Gets the value of the maximumTimeToSyncRegistry property.
         *
         * @return possible object is {@link BigInteger }
         *
         */
        @Column(name = "maxsynctime")
        public BigInteger getMaximumTimeToSyncRegistry() {
                return maximumTimeToSyncRegistry;
        }

        /**
         * Sets the value of the maximumTimeToSyncRegistry property.
         *
         * @param value allowed object is {@link BigInteger }
         *
         */
        public void setMaximumTimeToSyncRegistry(BigInteger value) {
                this.maximumTimeToSyncRegistry = value;
        }

        /**
         * Gets the value of the maximumTimeToGetChanges property.
         *
         * @return possible object is {@link BigInteger }
         *
         */
        @Column(name = "maxgettime")
        public BigInteger getMaximumTimeToGetChanges() {
                return maximumTimeToGetChanges;
        }

        /**
         * Sets the value of the maximumTimeToGetChanges property.
         *
         * @param value allowed object is {@link BigInteger }
         *
         */
        public void setMaximumTimeToGetChanges(BigInteger value) {
                this.maximumTimeToGetChanges = value;
        }

        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "replicationConfiguration")
        @OrderBy
        public List<Signature> getSignatures() {
                return signatures;
        }

        public void setSignatures(List<Signature> signatures) {
                this.signatures = signatures;
        }

        
}
