/*
 * Copyright 2014 The Apache Software Foundation.
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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author alex
 */
@Entity
@Table(name = "j3_chg_replcfgear")
public class EdgeReceiverAlternate implements Serializable{
        private static final long serialVersionUID = -3199894835641632162L;
       Long id;
       String rx;
       
               @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }
        
         @ManyToOne(fetch = FetchType.LAZY, targetEntity = Edge.class)
	@JoinColumn(name = "entity_key_ed", nullable = false)
         public Edge getParent(){
                 return this.edge;
         }
         private Edge edge;
         public void setParent(Edge e){
                 this.edge = e;
         }

         @Column
         public String getReceiverAlternate() {
                return this.rx;
        }
         
        public void setReceiverAlternate(String s) {
                this.rx = s;
        }
}
