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
 */
package org.apache.juddi.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
@Entity
@Table(name = "j3_valuesets")
public class ValueSetValues implements java.io.Serializable {

        public static final transient String TABLE_NAME = "j3_valuesets";
        public static final transient String COLUMN_TMODELKEY = "j3_tmodelkey";
        private static final long serialVersionUID = 7767295374035531912L;
        // private Long id;
        private String tmodelKey;
        private String ValidatorClass;
        
        
        public ValueSetValues() {
        }

        public ValueSetValues(String tmodelkey, String clazz) {
                this.tmodelKey = tmodelkey;
                this.ValidatorClass=clazz;
        }

        /*
         @GeneratedValue(strategy = GenerationType.AUTO)
         public Long getId() {
         return this.id;
         }

         public void setId(Long id) {
         this.id = id;
         }*/
        @Id
        @Column(name = "j3_tmodelkey", nullable = false, length = 255)
        public String getTModelKey() {
                return this.tmodelKey;
        }

        public void setTModelKey(String key) {
                this.tmodelKey = key;
        }
        
        /**
         * 
         * @return should be one of businessService, businessEntity, bindingTemplate, tModel
         */
        @Column(name = "j3_validatorclass", nullable = false, length = 255)
        public String getValidatorClass() {
                return this.ValidatorClass;
        }

        /**
         * 
         * @param type should be one of businessService, businessEntity, bindingTemplate, tModel
        */
        public void setValidatorClass(String clazz) {
                this.ValidatorClass=clazz;
        }

       
}
