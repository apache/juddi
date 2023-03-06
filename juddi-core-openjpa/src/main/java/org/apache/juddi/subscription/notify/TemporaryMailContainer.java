/*
 * Copyright 2015 The Apache Software Foundation.
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
package org.apache.juddi.subscription.notify;

import org.apache.juddi.model.Publisher;
import org.apache.juddi.model.Subscription;

/**
 *
 * @author alex
 */
public class TemporaryMailContainer {

        public TemporaryMailContainer(Subscription obj, Publisher publisher, Publisher deletedBy) {

                this.obj=obj;
                this.publisher = publisher;
                this.deletedBy=deletedBy;
        }
        private Subscription obj;
        private Publisher publisher;
        private Publisher deletedBy;


        public Subscription getObj() {
                return obj;
        }

        public void setObj(Subscription obj) {
                this.obj = obj;
        }

        public Publisher getPublisher() {
                return publisher;
        }

        public void setPublisher(Publisher publisher) {
                this.publisher = publisher;
        }

        public Publisher getDeletedBy() {
                return deletedBy;
        }

        public void setDeletedBy(Publisher deletedBy) {
                this.deletedBy = deletedBy;
        }
}
