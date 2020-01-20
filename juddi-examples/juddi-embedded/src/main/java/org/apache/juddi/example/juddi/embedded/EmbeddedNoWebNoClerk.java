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
import java.util.HashSet;
import java.util.Set;
import org.apache.juddi.Registry;
import org.apache.juddi.api.impl.JUDDIApiImpl;
import org.apache.juddi.api.impl.UDDICustodyTransferImpl;
import org.apache.juddi.api.impl.UDDIInquiryImpl;
import org.apache.juddi.api.impl.UDDIPublicationImpl;
import org.apache.juddi.api.impl.UDDISecurityImpl;
import org.apache.juddi.api.impl.UDDISubscriptionImpl;
import org.apache.juddi.api.impl.UDDIValueSetCachingImpl;
import org.apache.juddi.api.impl.UDDIValueSetValidationImpl;
import static org.apache.juddi.config.AppConfig.JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY;
import org.apache.juddi.v3.client.UDDIConstants;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.Name;

/**
 * In this case, we are not using juddi's client/clerk/transport apis, just
 * using the instance classes directly
 *
 * @author Alex O'Ree
 */
public class EmbeddedNoWebNoClerk {

    public static void main(String[] args) throws Exception {
        //tell juddi to load this server configuration file from disk
        File cfg = new File("juddi-server.xml").getCanonicalFile();
        System.setProperty(JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY, cfg.getCanonicalPath());

        //start up the services
        Registry.start();
        //note these instance classes will be used to server web requests
        //do not share embedded access with web access classes due to context
        //sharing issues.
        JUDDIApiImpl juddi = new JUDDIApiImpl();
        UDDIPublicationImpl publish = new UDDIPublicationImpl();
        UDDIInquiryImpl inquiry = new UDDIInquiryImpl();
        UDDISecurityImpl security = new UDDISecurityImpl();
        UDDISubscriptionImpl subscription = new UDDISubscriptionImpl();
        UDDICustodyTransferImpl custody = new UDDICustodyTransferImpl();
        UDDIValueSetCachingImpl cache = new UDDIValueSetCachingImpl();
        UDDIValueSetValidationImpl validation = new UDDIValueSetValidationImpl();

        //clients within this process can use invm transport
        {
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

            BusinessList result = inquiry.findBusiness(fb);
            System.out.println(result.getBusinessInfos().getBusinessInfo().size() + " businesses available");
            //uddiClientHttp.stop();
        }

        //for cases that require authentication...
        //the authenticator should work the same as it is in tomcat
        //except if you use http style authentication. in this case, you'll have 
        //to do this....
        //Set<String> roles = new HashSet<String>();
        //Note: juddi doesn't use servlet container roles
        //publish.setContext(new MyWebContext("uddi", roles));

        System.out.println("ready, press enter to stop");
        //  System.console().readLine();

        //shutdown
        Registry.stop();
    }
}
