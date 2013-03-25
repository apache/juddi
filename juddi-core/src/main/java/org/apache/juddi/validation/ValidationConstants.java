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
package org.apache.juddi.validation;

/**
 * Validation constants for various items, such as maximum string lengths
 *
 * @author Alex O'Ree
 */
public class ValidationConstants {

    public static final int MAX_accessPoint = 4096;
    public static final int MAX_addressLine = 80;
    public static final int MAX_authInfo = 4096;
    public static final int MAX_Key = 255;
    public static final int MAX_description = 255;
    public static final int MAX_discoveryURL = 4096;
    public static final int MAX_email = 255;
    public static final int MAX_instanceParms = 8192;
    public static final int MAX_keyName = 255;
    public static final int MAX_keyValue = 255;
    public static final int MAX_name = 255;
    public static final int MAX_operator = 255;
    public static final int MAX_overviewURL = 4096;
    public static final int MAX_personName = 255;
    public static final int MAX_phone = 50;
    public static final int MAX_sortCode = 10;
    public static final int MAX_useType = 255;
    public static final int MAX_completionStatus = 32;
    public static final int MAX_xml_lang = 26;
//2.3.2 Subscription API
    public static final int MAX_subscriptionKey = 255;
//2.3.3 Replication API
    public static final int MAX_nodeId = 255;
    public static final int MAX_notifyingNode = 255;
    public static final int MAX_operatorNodeID = 255;
    public static final int MAX_requestingNode = 255;
}
