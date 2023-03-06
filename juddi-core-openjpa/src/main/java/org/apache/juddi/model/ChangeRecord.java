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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "j3_chg_record"
        //, uniqueConstraints = @UniqueConstraint(columnNames = {"node_id", "orginating_usn"}) 
)
 
public class ChangeRecord implements Serializable {

        private static final long serialVersionUID = 1L;

        protected String nodeID;
        protected Long originatingUSN;
        private byte[] contents;
        private RecordType e = RecordType.ChangeRecordNull;
        private Long id;
        private String entityKey;
        private boolean appliedLocally = false;

        @Column(name = "change_contents")
        @Lob
        public byte[] getContents() {
                return contents;
        }

        public void setContents(byte[] contents) {
                this.contents = contents;
        }

        public enum RecordType {

                ChangeRecordNewData,
                ChangeRecordDelete,
                ChangeRecordPublisherAssertion,
                ChangeRecordHide,
                ChangeRecordDeleteAssertion,
                ChangeRecordAcknowledgement,
                ChangeRecordCorrection,
                ChangeRecordNewDataConditional,
                ChangeRecordConditionFailed,
                ChangeRecordNull
        }

        public void setRecordType(RecordType e) {
                this.e = e;
        }

        @Column(name = "record_type")
        public RecordType getRecordType() {
                return e;
        }

        @Id
        @GeneratedValue(strategy = GenerationType.TABLE,
             generator = "personGen")
        @TableGenerator(name = "personGen",
             table = "JPAGEN_GENERATORS",
             pkColumnName = "NAME",
             pkColumnValue = "JPAGEN_PERSON_GEN",
             valueColumnName = "VALUE")
        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        @Column(name = "node_id")
        public String getNodeID() {
                return nodeID;
        }

        public void setNodeID(String value) {
                this.nodeID = value;
        }

        @Column(name = "entity_key")
        public String getEntityKey() {
                return entityKey;
        }

        public void setEntityKey(String value) {
                this.entityKey = value;
        }

        @Column(name = "orginating_usn")
        public Long getOriginatingUSN() {
                return originatingUSN;
        }

        public void setOriginatingUSN(Long value) {
                this.originatingUSN = value;
        }
        
        /**
         * returns true if the changes represented by this change record were applied successfully at this node
         * @return 
         */
        @Column(name = "appliedlocal")
        public boolean getIsAppliedLocally() {
                return appliedLocally;
        }

        public void setIsAppliedLocally(boolean value) {
                this.appliedLocally = value;
        }
}
