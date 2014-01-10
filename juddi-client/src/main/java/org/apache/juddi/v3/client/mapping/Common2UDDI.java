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
package org.apache.juddi.v3.client.mapping;

import java.util.ArrayList;
import java.util.List;
import org.apache.juddi.v3.client.UDDIConstants;
import org.uddi.api_v3.Description;

/**
 *
 * @author Alex O'Ree
 */
public abstract class Common2UDDI {

        public static List<Description> mapDescription(String content, String lang) {

                List<Description> ret = new ArrayList<Description>();
                if (content == null) {
                        return ret;
                }
                if (content.length() > UDDIConstants.MAX_description_length) {
                        int offset = 0;
                        while (offset < content.length()) {
                                Description description = new Description();
                                description.setLang(lang);
                                int trim = offset + UDDIConstants.MAX_description_length;
                                if (trim > content.length()) {
                                        trim = content.length()-1;
                                }
                                description.setValue(content.substring(offset, trim));
                                offset = offset + UDDIConstants.MAX_description_length;
                                ret.add(description);
                                
                        }
                } else {
                        ret.add(new Description(content, lang));
                }
                return ret;

        }
}
