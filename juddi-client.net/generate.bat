rem Licensed to the Apache Software Foundation (ASF) under one
rem or more contributor license agreements.  See the NOTICE file
rem distributed with this work for additional information
rem regarding copyright ownership.  The ASF licenses this file
rem to you under the Apache License, Version 2.0 (the
rem "License"); you may not use this file except in compliance
rem with the License.  You may obtain a copy of the License at
rem 
rem   http://www.apache.org/licenses/LICENSE-2.0
rem 
rem Unless required by applicable law or agreed to in writing,
rem software distributed under the License is distributed on an
rem "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
rem KIND, either express or implied.  See the License for the
rem specific language governing permissions and limitations
rem under the License.
rem This will generate the client proxy code using the juddi wsdls and xsds via .NET 2.0 ASP.NET (System.Web.Services.WebClient)

set path=%path%;C:\Program Files (x86)\Microsoft SDKs\Windows\v8.0A\bin\NETFX 4.0 Tools


wsdl  ..\uddi-ws\src\main\resources\juddi_api_v1.wsdl ..\uddi-ws\src\main\resources\uddi_api_v3_binding.wsdl  ..\uddi-ws\src\main\resources\uddi_api_v3_portType.wsdl ..\uddi-ws\src\main\resources\uddi_custody_v3_binding.wsdl ..\uddi-ws\src\main\resources\uddi_custody_v3_portType.wsdl ..\uddi-ws\src\main\resources\uddi_repl_v3_binding.wsdl ..\uddi-ws\src\main\resources\uddi_repl_v3_portType.wsdl ..\uddi-ws\src\main\resources\uddi_subr_v3_binding.wsdl ..\uddi-ws\src\main\resources\uddi_subr_v3_portType.wsdl ..\uddi-ws\src\main\resources\uddi_sub_v3_binding.wsdl ..\uddi-ws\src\main\resources\uddi_sub_v3_portType.wsdl ..\uddi-ws\src\main\resources\uddi_v3_service.wsdl ..\uddi-ws\src\main\resources\uddi_vscache_v3_binding.wsdl ..\uddi-ws\src\main\resources\uddi_vscache_v3_portType.wsdl ..\uddi-ws\src\main\resources\uddi_vs_v3_binding.wsdl ..\uddi-ws\src\main\resources\uddi_vs_v3_portType.wsdl  ..\uddi-ws\src\main\resources\uddi_v3.xsd ..\uddi-ws\src\main\resources\uddi_v3custody.xsd ..\uddi-ws\src\main\resources\uddi_v3policy.xsd ..\uddi-ws\src\main\resources\uddi_v3policy_instanceParms.xsd ..\uddi-ws\src\main\resources\uddi_v3replication.xsd ..\uddi-ws\src\main\resources\uddi_v3subscription.xsd ..\uddi-ws\src\main\resources\uddi_v3subscriptionListener.xsd ..\uddi-ws\src\main\resources\uddi_v3valueset.xsd ..\uddi-ws\src\main\resources\uddi_v3valuesetcaching.xsd 

rem this is for the configuration file
xsd /c ..\juddi-client\src\main\resources\xsd\uddi-client.xsd /namespace:org.apache.juddi.v3.client.config 
