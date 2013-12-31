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
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "j3_chg_record")
public class ChangeRecord implements Serializable {

        protected String nodeID;
        protected Long originatingUSN;
        
        private Long id;

        @Column(name="change_contents")
        public byte[] getContents() {
                return contents;
        }

        public void setContents(byte[] contents) {
                this.contents = contents;
        }
        /*protected ChangeRecordIDType changeID;
         protected Object changeRecordNull;
         protected ChangeRecordNewData changeRecordNewData;
         protected ChangeRecordDelete changeRecordDelete;
         protected ChangeRecordPublisherAssertion changeRecordPublisherAssertion;
         protected ChangeRecordHide changeRecordHide;
         protected ChangeRecordDeleteAssertion changeRecordDeleteAssertion;
         protected ChangeRecordAcknowledgement changeRecordAcknowledgement;
         protected ChangeRecordCorrection changeRecordCorrection;
         protected ChangeRecordNewDataConditional changeRecordNewDataConditional;
         protected ChangeRecordConditionFailed changeRecordConditionFailed;
         protected boolean acknowledgementRequested;
         * */
        byte[] contents;

        enum RecordType {

                ChangeRecordNewData,
                ChangeRecordDelete,
                ChangeRecordPublisherAssertion,
                ChangeRecordHide,
                ChangeRecordDeleteAssertion,
                ChangeRecordAcknowledgement,
                ChangeRecordCorrection,
                ChangeRecordNewDataConditional,
                ChangeRecordConditionFailed
        }

        @Id
        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        @Column(name="node_id")
        public String getNodeID() {
                return nodeID;
        }

        public void setNodeID(String value) {
                this.nodeID = value;
        }

        
        @Column(name="orginating_usn")
        public Long getOriginatingUSN() {
                return originatingUSN;
        }

        public void setOriginatingUSN(Long value) {
                this.originatingUSN = value;
        }
}
