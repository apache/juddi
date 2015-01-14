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
    [System.AttributeUsage(System.AttributeTargets.Interface | System.AttributeTargets.Class, AllowMultiple = false)]
    public class UDDIService : System.Attribute
    {
        public UDDIService()
        {
            this.lang = "en";
            businessKey = "";
            serviceName = "";
            categoryBag = "";
        }
        /** Name of the Service, this can be omitted if one is specified in a WebService annotation */
        public String serviceName { get; set; }
        /** Description of the Service */
        public String description { get; set; }
        /** Unique key of this service */
        public String serviceKey { get; set; }
        /** Unique key of the business to which this Service belongs. */
        public String businessKey { get; set; }
        /** Language code i.e.: en, fr, nl. */
        public String lang { get; set; }
        /** List of KeyedReferences */
        public String categoryBag { get; set; }
    }
}
