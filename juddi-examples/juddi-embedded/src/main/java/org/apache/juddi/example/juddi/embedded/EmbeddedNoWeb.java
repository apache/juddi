/*
 * Copyright 2020 The Apache Software Foundation.
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
package org.apache.juddi.example.juddi.embedded;

import java.io.File;
import static org.apache.juddi.config.AppConfig.JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.Name;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This sample shows you how to interact with jUDDI without exposing any web
 * services to the network. It's basically a standalone java process that can
 * used for any purpose.
 *
 * @author Alex O'Ree
 */
public class EmbeddedNoWeb {

    public static void main(String[] args) throws Exception {
        

        //access the client as normal using invm transports
        File cfg = new File("juddi-server.xml").getCanonicalFile();
        System.setProperty(JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY, cfg.getCanonicalPath());
        cfg = new File("uddi-invm.xml");
        UDDIClient uddiClientEmdded = new UDDIClient(cfg.getCanonicalPath());
        uddiClientEmdded.start();
        Transport transport = uddiClientEmdded.getTransport("default");
        UDDISecurityPortType clientSecurity = transport.getUDDISecurityService();
        UDDIInquiryPortType clientInquiry = transport.getUDDIInquiryService();

        System.out.println("started, verifying embedded access");
        FindBusiness fb = new FindBusiness();
        fb.setMaxRows(200);
        fb.setListHead(0);
        // fb.setAuthInfo(GetToken());
        org.uddi.api_v3.FindQualifiers fq = new org.uddi.api_v3.FindQualifiers();
        fq.getFindQualifier().add(UDDIConstants.CASE_INSENSITIVE_MATCH);
        fq.getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
        fq.getFindQualifier().add(UDDIConstants.SORT_BY_NAME_ASC);
        fb.setFindQualifiers(fq);
        Name searchname = new Name();
        searchname.setLang("%");
        searchname.setValue("%");
        fb.getName().add(searchname);

        BusinessList result = clientInquiry.findBusiness(fb);
        System.out.println(result.getBusinessInfos().getBusinessInfo().size() + " businesses available");
        uddiClientEmdded.stop();
        
        //this appears to hang, there's a background thread spawned somewhere that doesn't die

    }
}
