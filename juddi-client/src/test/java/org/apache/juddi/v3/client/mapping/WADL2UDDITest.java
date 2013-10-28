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
package org.apache.juddi.v3.client.mapping;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.juddi.jaxb.PrintUDDI;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.mappings.wadl.Application;
import org.junit.Assert;
import org.junit.Test;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;

/**
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class WADL2UDDITest {

    static PrintUDDI<TModel> pTModel = new PrintUDDI<TModel>();
    static Properties properties = new Properties();
    
  
    /**
     * tests loading a wadl from a file
     * @throws Exception 
     */
    @Test
    public void Test1() throws Exception {
        
        
        Application app = WADL2UDDI.ParseWadl(new File("src/test/resources/wadl/cxf.wadl"));
        Assert.assertNotNull(app);
        Assert.assertNotNull(app);
        List<URL> urls = WADL2UDDI.GetBaseAddresses(app);
        URL url = urls.get(0);
        String domain = url.getHost();
        PrintUDDI<TModel> tmodelPrinter = new PrintUDDI<TModel>();
        TModel keygen = UDDIClerk.createKeyGenator("uddi:" + domain + ":keygenerator", domain, "en");
        //save the keygen
        SaveTModel stm = new SaveTModel();
        stm.getTModel().add(keygen);
        System.out.println(tmodelPrinter.print(keygen));

        properties.put("keyDomain", domain);
        properties.put("businessName", domain);
        properties.put("serverName", url.getHost());
        properties.put("serverPort", url.getPort());
        //wsdlURL = wsdlDefinition.getDocumentBaseURI();
        WADL2UDDI wadl2UDDI = new WADL2UDDI(null, new URLLocalizerDefaultImpl(), properties);

        Assert.assertNotNull(wadl2UDDI);
        
        BusinessService businessServices = wadl2UDDI.createBusinessService(new QName("CXF.Sample.WADL.namespace", "Servicename"), app);

        Assert.assertNotNull(businessServices);
        Assert.assertNotNull(businessServices.getBindingTemplates());
        Assert.assertNotNull(businessServices.getName().get(0));
        Assert.assertNotNull(businessServices.getBindingTemplates().getBindingTemplate().get(0));
        Assert.assertNotNull(businessServices.getBindingTemplates().getBindingTemplate().get(0).getAccessPoint());
        Assert.assertNotNull(businessServices.getBindingTemplates().getBindingTemplate().get(0).getAccessPoint().getValue());
        
        
        

        PrintUDDI<BusinessService> servicePrinter = new PrintUDDI<BusinessService>();

        System.out.println(servicePrinter.print(businessServices));



    }
}
