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
package org.apache.juddi.api_v3.rest;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is used by the jUDDI Inquiry REST web service. It's just a simple wrapper
 * to make serialization of XML and JSON objects simpler. 
 * @author Alex O'Ree
 */
@XmlRootElement
public class UriContainer {

        private List<String> yourlist;

        @XmlElement
        public List<String> getUriList() {
                if (yourlist==null)yourlist = new ArrayList<String>();
                return yourlist;
        }

        public void setUriList(List<String> yourlist) {
                this.yourlist = yourlist;
        }
}
