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
package org.apache.juddi.v3.client;

/**
 *
 * @author alexoree@apache.org
 */
public interface UDDIConstantsV2 {

        public static final String TYPES = ("uuid:C1ACF26D-9672-4404-9D70-39B756E62AB4");
        public static final String NODE = ("uuid:327A56F0-3299-4461-BC23-5CD513E95C55");
        /**
         * Basic types of business relationships
         */
        public static final String RELATIONSHIPS = ("uuid:807A2C6A-EE22-470D-ADC7-E0424A337C03");
        /**
         * Category system used to point to the businessEntity associated with
         * the publisher of the tModel.
         */
        public static final String OWNING_BUSINESS = ("uuid:4064c064-6d14-4f35-8953-9652106476a9");
        /**
         * Identifier system used to point to the UDDI entity, using UDDI keys,
         * that is the logical replacement for the one in which isReplacedBy is
         * used.
         */
        public static final String IS_REPLACED_BY = ("uuid:e59ae320-77a5-11d5-b898-0004ac49cc1e");
        /**
         * Category system used to point a value set or category group system
         * tModel to associated value set Web service implementations. used for
         * keyedReference tmodel key<br>
         * hint: use the ValueSetValidation binding template key as the value
         */
        public static final String IS_VALIDATED_BY = ("uuid:25b22e3e-3dfa-3024-b02a-3438b9050b59");
        public static final String IS_DERIVED_FROM = ("uuid:5678dd4f-f95d-35f9-9ea6-f79a7dd64656");
        /**
         * Category system used to declare that a value set uses entity keys as
         * valid values.
         */
        public static String ENTITY_KEY_VALUES = ("uuid:916b87bf-0756-3919-8eae-97dfa325e5a4");

        public static String INQUIRY = "uuid:01b9bbff-a8f5-3735-9a5e-5ea5ade7daaf";
        public static String PUBLISH = "uuid:72ade754-c6cc-315b-b014-7c94791fe15c";
        public static String SECURITY = "uuid:e4cd70e2-22ec-3032-b1e6-cc31a9d55935";
        public static String REPLICATION = "uuid:998053a9-8672-3bf3-908a-c82deb4baecf";
        public static String CUSTODY_TRANSFER = "uuid:07ae0f8f-1bdc-32a7-b8dc-fe1d93d929a7";
        public static String NODE_TRANSFER = "uuid:215c7844-5e81-347c-a2bf-54023ad463c8";
        public static String VSV_CACHE = "uuid:a24d9150-cdbb-3cb4-8843-41a5d0547170";
        public static String VSV_VALIDATE = "uuid:056fc4a2-bea3-30e5-8382-6d61e1ee23ce";
        public static String SUBSCRIPTION = "uuid:c6eb3d94-8051-3fbb-9320-a6147e266e57";
        public static String SUBSCRIPTION_LISTENER = "uuid:0f965bee-b120-3a66-bdc2-4908819c1174";
        
            /**
     * Secure Sockets Layer Version 3.0 with Server Authentication
     */
    public static String PROTOCOL_SSLv3 = "uuid:c8aea832-3faf-33c6-b32a-bbfd1b926294";
    /**
     * Secure Sockets Layer Version 3.0 with Mutual Authentication
     */
    public static String PROTOCOL_SSLv3_CLIENT_CERT = "uuid:9555b5b6-55d4-3b0e-bb17-e084fed4e33f";
    
      /**
     * A Web service that uses HTTP transport -
     */
    public static String TRANSPORT_HTTP = "uuid:68DE9E80-AD09-469D-8A37-088422BFBC36";
    /**
     * E-mail based Web service
     */
    public static String TRANSPORT_EMAIL = "uuid:93335D49-3EFB-48A0-ACEA-EA102B60DDC6";
    /**
     * File Transfer Protocol (FTP) based Web service
     */
    public static String TRANSPORT_FTP = "uuid:5FCF5CD0-629A-4C50-8B16-F94E9CF2A674";
    /**
     * Fax-based Web service
     */
    public static String TRANSPORT_FAX = "uuid:1A2B00BE-6E2C-42F5-875B-56F32686E0E7";
    /**
     * Telephone based service
     */
    public static String TRANSPORT_POTS = "uuid:38E12427-5536-4260-A6F9-B5B530E63A07";
  
    
    
}
