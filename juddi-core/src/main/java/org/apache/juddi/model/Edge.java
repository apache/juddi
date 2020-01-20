/*
 * Copyright 2013 The Apache Software Foundation.
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
 */
package org.apache.juddi.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "j3_edge")
public class Edge implements Serializable{

        private Long id;
        private List<ControlMessage>  message;
        private String messageSender;
        private String messageReceiver;
        private List<EdgeReceiverAlternate> messageReceiverAlternate;
        private ReplicationConfiguration parent;

         @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ReplicationConfiguration", nullable = false)
        public ReplicationConfiguration getReplicationConfiguration() {
                return parent;
        }
        
        public void setReplicationConfiguration(ReplicationConfiguration p){
                parent = p;
        }
        
        /**
         * The message elements contain the local name of the Replication API message elements
         * @return 
         */
        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = ControlMessage.class)
        public List<ControlMessage> getMessages() {
                if (message == null) {
                        message = new ArrayList<ControlMessage>();
                }
                return this.message;
        }
        public void setMessages(List<ControlMessage>  values) {
                this.message = values;
        }

        
        @Column
        public String getMessageSender() {
                return messageSender;
        }

        public void setMessageSender(String value) {
                this.messageSender = value;
        }

        /**
         * For each directed edge, the primary edge Node id
                 *
         * @return
         */
        @Column
        public String getMessageReceiver() {
                return messageReceiver;
        }

        public void setMessageReceiver(String value) {
                this.messageReceiver = value;
        }

        /**
         * For each directed edge, an ordered sequence of zero or more
         * alternate, backup edges MAY be listed using the
         * messageReceiverAlternate element
         *
         * @return
         */
        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = EdgeReceiverAlternate.class
                //, mappedBy = "messageReceiverAlternate"
        )
        public List<EdgeReceiverAlternate> getMessageReceiverAlternate() {
                if (messageReceiverAlternate == null) {
                        messageReceiverAlternate = new ArrayList<EdgeReceiverAlternate>();
                }
                return this.messageReceiverAlternate;
        }

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        public Long getId() {
                return id;
        }

        public void setMessage(List<ControlMessage> message) {
                this.message = message;
        }

        public void setMessageReceiverAlternate(List<EdgeReceiverAlternate> messageReceiverAlternate) {
                this.messageReceiverAlternate = messageReceiverAlternate;
        }

     

        public void setId(Long id) {
                this.id = id;
        }
}
