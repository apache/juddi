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
 * UDDI provides a mechanism that may be used by publishers to assert
 * relationships between businessEntity structures they publish and other
 * businessEntity structures according to any number of relationship type
 * schemes. See Appendix A Relationships and Publisher Assertions for more
 * information. This section defines a tModel representing a relationship type
 * system for use in describing the way businessEntity structures relate to one
 * another.
 * <Br><Br>
 * While UDDI provides for any number of relationship type system to be used in
 * relating businessEntity structures to one another, it is useful to define a
 * "starter set" of relationship types that publishers may use without needing
 * to define their own. The uddi-org:relationships relationship type system is
 * such a starter set that covers a number of basic relationships. All three
 * attributes are significant in keyedReferences that describe relationship
 * types. The keyValue attributes should contain well known but somewhat broad
 * versions of the relationship type, like those described in this relationship
 * type set. The keyName attributes should be used to more explicitly type the
 * relationship.
 *
 * @author Alex O'Ree
 */
public class Uddiuddiorgrelationships extends  AbstractSimpleValidator{

        @Override
        public List<String> getValidValues() {
                List<String> ret = new ArrayList<String>();
                ret.add("parent-child");
                ret.add("identity");
                ret.add("peer-peer");
                return ret;
        }

        @Override
        public String getMyKey() {
                return "uddi:uddi.org:relationships";
        }
        

}
