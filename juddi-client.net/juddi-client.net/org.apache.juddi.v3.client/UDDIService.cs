/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
 *
 */
using System;
using System.Collections.Generic;
using System.Text;
using org.uddi.apiv3;
namespace juddi_dotnet
{
    public class UDDIService
    {
        public static UDDI_Inquiry_SoapBinding getUDDIInquiryPort()
        {
            return new UDDI_Inquiry_SoapBinding();
        }
        public static UDDI_Inquiry_SoapBinding getUDDIInquiryPort(String endpoint)
        {
            UDDI_Inquiry_SoapBinding i = new UDDI_Inquiry_SoapBinding();
            i.Url = endpoint;
            return i;
        }
        public static UDDI_Publication_SoapBinding getUDDIPublicationPort()
        {
            return new UDDI_Publication_SoapBinding();
        }
        public static UDDI_Publication_SoapBinding getUDDIPublicationPort(String endpoint)
        {
            UDDI_Publication_SoapBinding i = new UDDI_Publication_SoapBinding();
            i.Url = endpoint;
            return i;
        }


    }
}
