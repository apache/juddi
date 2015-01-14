/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

using System;
using System.Collections.Generic;

using System.Text;

namespace org.apache.juddi.v3.client.annotations
{
    /// <summary>
    /// Use this class to annotate your services programmatically. When your server start up, you can then automatically register endpoints using
    /// this mechanism.
    /// </summary>
    /// <see>UDDIClient.registerAnnotatedServices</see>
    [System.AttributeUsage(System.AttributeTargets.Class, AllowMultiple = false)]
    public class UDDIServiceBinding : System.Attribute
    {
        /// <summary>
        /// defaults to "en" lang
        /// </summary>
        public UDDIServiceBinding()
        {
            lang = "en";
            bindingKey = "";
            description = "";
            accessPoint = "";
            tModelKeys = "";
            categoryBag = "";
            accessPointType = "wsdlDeployment";
        }
        /** name which can be referenced by TModelInstanceRegistration, this key is not send
 * down to the UDDI Registry. */
        public String bindingKey { get; set; }
        /** Description of the ServiceBinding. */
        public String description { get; set; }
        /** AccessPoint Type, which could be one of endPoint, wsdlDeployment, bindingTemplate, hostingDirector */
        public String accessPointType { get; set; }
        /** The URL of the accessPoint. */
        public String accessPoint { get; set; }
        /** Language code i.e.: en, fr, nl. */
        public String lang { get; set; }
        /** Comma separated list of tModel Keys */
        public String tModelKeys { get; set; }
        /** List of KeyedReferences */
        public String categoryBag { get; set; }
    }
}
