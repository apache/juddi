/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.v3.client.mapping;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class ReadWSDLTest {

    @Test
    public void readFromFile() throws WSDLException, URISyntaxException, Exception {

        ReadWSDL readWSDL = new ReadWSDL();
        Definition definition = readWSDL.readWSDL("wsdl/HelloWorld.wsdl");
        Assert.assertNotNull(definition);
    }

    private static boolean IsReachable(String url) {
        System.out.println("Testing connectivity to " + url);
        try {
            //make a URL to a known source
            URL url2 = new URL(url);

            //open a connection to that source
            HttpURLConnection urlConnect = (HttpURLConnection) url2.openConnection();

            //trying to retrieve data from the source. If there
            //is no connection, this line will fail
            Object objData = urlConnect.getContent();
            urlConnect.disconnect();

        } catch (Exception e) {
            System.out.println("Connectivity failed " + e.getMessage());
            return false;
        }
        System.out.println("Connectivity passed" );
        return true;

    }

    /**
     * normally, this test will work correctly if and only if you're connected
     * to the big bad internet. if you happen to be offline, this test will
     * fail.
     */
    @Test
    public void readFromURL() throws MalformedURLException, Exception {
        
        boolean b = IsReachable("http://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php?wsdl");
        if (!b) {
            System.out.println("Skipping test for a remote WSDL due to connectivity problems");
        }


        org.junit.Assume.assumeTrue(b);
        ReadWSDL readWSDL = new ReadWSDL();
        Definition definition = null;
        try {
            definition = readWSDL.readWSDL(new URL("http://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php?wsdl"));
            Assert.assertNotNull(definition);
        } catch (UnknownHostException ex) {
        }


    }

    @Test
    public void readFromJar() throws WSDLException, URISyntaxException, Exception {

        ReadWSDL readWSDL = new ReadWSDL();
        Definition definition = readWSDL.readWSDL("uddi_v3_service.wsdl");
        Assert.assertNotNull(definition);
    }
}
