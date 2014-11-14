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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "j3_edge")
public class Edge {

        private Long id;
        private List<ControlMessage>  message;
        private Node messageSender;
        private Node messageReceiver;
        private Set<Node> messageReceiverAlternate;
        private CommunicationGraph parent;

         /**
         * link the parent object
         * @param val 
         */
        @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = CommunicationGraph.class)
        //@JoinColumn(name = "OWNER_ID")
        public CommunicationGraph getCommunicationGraph() {
                return parent;
        }

        /**
         * link the parent object
         * @param val 
         */
        public void setCommunicationGraph(CommunicationGraph val) {
                parent = val;
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

        @JoinColumn(referencedColumnName ="name" )
        @ManyToOne(targetEntity = Node.class)
        public Node getMessageSender() {
                return messageSender;
        }

        public void setMessageSender(Node value) {
                this.messageSender = value;
        }

        /**
         * For each directed edge, the primary edge Node id
                 *
         * @return
         */
        @JoinColumn(referencedColumnName ="name" )
        @ManyToOne(targetEntity = Node.class)
        public Node getMessageReceiver() {
                return messageReceiver;
        }

        public void setMessageReceiver(Node value) {
                this.messageReceiver = value;
        }

        /**
         * For each directed edge, an ordered sequence of zero or more
         * alternate, backup edges MAY be listed using the
         * messageReceiverAlternate element
         *
         * @return
         */
        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = Node.class)
        public Set<Node> getMessageReceiverAlternate() {
                if (messageReceiverAlternate == null) {
                        messageReceiverAlternate = new HashSet<Node>();
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

        public void setMessageReceiverAlternate(Set<Node> messageReceiverAlternate) {
                this.messageReceiverAlternate = messageReceiverAlternate;
        }

     

        public void setId(Long id) {
                this.id = id;
        }
}
