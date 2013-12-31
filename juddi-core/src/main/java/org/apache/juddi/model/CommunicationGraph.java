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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "j3_chg_graph")
public class CommunicationGraph implements Serializable {

        private Long id;
        private List<Node> node;
        private List<ControlMessage> controlledMessage;
        private List<Edge> edge;

        
        @OneToMany(targetEntity = Node.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        public List<Node> getNode() {
                if (node == null) {
                        node = new ArrayList<Node>();
                }
                return this.node;
        }

        public void setNode(List<Node> nodes) {

                this.node = nodes;
        }

        @OneToMany(targetEntity = ControlMessage.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        public List<ControlMessage> getControlMessage() {
                if (controlledMessage == null) {
                        controlledMessage = new ArrayList<ControlMessage>();
                }
                return this.controlledMessage;
        }

        public void setControlMessage(List<ControlMessage> controlledMessages) {

                this.controlledMessage = controlledMessages;
        }

        // @OneToMany( fetch = FetchType.LAZY,targetEntity = Edge.class, mappedBy = "Edge")
        @OneToMany(targetEntity = Edge.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        public List<Edge> getEdge() {
                return this.edge;
        }
        
         public void setEdge( List<Edge> edges) {
                this.edge=edges;
        }

        @Id
        @Column(name = "j3_id")
        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }
}
