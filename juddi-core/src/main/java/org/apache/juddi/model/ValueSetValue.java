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
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
@Entity
@Table(name = "j3_valuesetval")
public class ValueSetValue implements java.io.Serializable {

        private static final long serialVersionUID = 7767275374035531912L;
        private Long id;
        private String tmodelKey;
        private String value = null;

        public ValueSetValue() {
        }

        public ValueSetValue(String tmodelkey, String value) {
                
                this.value=(value);
                this.tmodelKey = tmodelkey;
        }

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        public Long getId() {
                return this.id;
        }

        public void setId(Long id) {
                this.id = id;
        }
        
        @Column(name="j3_tmodelkey", nullable=false, length=255)
        public String getTModelKey() {
                return this.tmodelKey;
        }

        public void setTModelKey(String key) {
                this.tmodelKey = key;
        }

        @Column(name="j3_value", nullable=false, length=255)
        public String getValue() {
                return this.value;
        }

        public void setValues(String values) {
                this.value = values;
        }
}
