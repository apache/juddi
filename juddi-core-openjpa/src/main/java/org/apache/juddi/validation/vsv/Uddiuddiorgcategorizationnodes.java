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
package org.apache.juddi.validation.vsv;

import java.util.ArrayList;
import java.util.List;

/**
 * UDDI provides a mechanism that may be used by publishers to categorize
 * businessEntity and tModel elements according to any number of category
 * systems and by inquirers to discover entities so categorized. See Appendix F
 * Using Categorization for more information.
 *
 * This section defines a tModel used to categorize a businessEntity as
 * representing a UDDI node in the registry in which the businessEntity appears.
 * See Section 6.2.2.1 Normative Modeling of Node Business Entity.
 *
 * 11.1.3.2 Design Goals Each UDDI registry can be comprised of a number of
 * nodes. Each UDDI node has a special businessEntity associated with it, called
 * its Node Business Entity. The businessService elements in this businessEntity
 * represent Web services that relate to the node's role in the UDDI registry.
 *
 * The uddi-org:nodes category system is designed to allow reliable discovery of
 * the Node Business Entity structures for nodes in a UDDI registry so that UDDI
 * clients can locate the businessService structures associated with the
 * operation of the registry.
 *
 * Checking of references to this value set consists of ensuring that the
 * publisher is the UDDI node and the keyValue has the value "node". Each node
 * allows the use of uddi-org:nodes only by itself, only on its own Node
 * Business Entity, and only with the value of "node". This value is used in the
 * keyValue attributes of keyedReference elements that are contained in
 * categoryBag elements to locate the Node Business Entity elements in the
 * registry.
 *
 *
 * @author Alex O'Ree
 */
public class Uddiuddiorgcategorizationnodes extends AbstractSimpleValidator {

        
        @Override
        public List<String> getValidValues() {
                List<String> ret = new ArrayList<String>();
                ret.add("node");
                return ret;
        }

        @Override
        public String getMyKey() {
                return "uddi:uddi.org:categorization:nodes";
        }

}
