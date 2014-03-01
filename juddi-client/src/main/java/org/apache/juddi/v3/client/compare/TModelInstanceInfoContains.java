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
package org.apache.juddi.v3.client.compare;

import java.util.List;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;

/**
 *
 * @author Alex O'Ree
 */
public class TModelInstanceInfoContains {

        public static InstanceDetails contains(TModelInstanceDetails input, String key) {
                if (input == null) {
                        return null;
                }
                if (key==null)return null;
                if (input.getTModelInstanceInfo() == null || input.getTModelInstanceInfo().isEmpty()) {
                        return null;
                }
                for (int i=0; i < input.getTModelInstanceInfo().size(); i++){
                        if (input.getTModelInstanceInfo().get(i).getTModelKey()!=null && 
                             input.getTModelInstanceInfo().get(i).getTModelKey().equalsIgnoreCase(key))
                                return input.getTModelInstanceInfo().get(i).getInstanceDetails();
                }
                return null;
        }
}
