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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author Alex O'Ree
 */
@Entity(name = "j3_ctrl_msg")
public class ControlMessage implements Serializable{
        private static final long serialVersionUID = 1L;
        
        
        private Long id;

        private String message;

        @Column(name = "j3_message")
        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }

        @Id
        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }
}
