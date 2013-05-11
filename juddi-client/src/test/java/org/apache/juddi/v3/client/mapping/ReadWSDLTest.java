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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class ReadWSDLTest {

    @Test
    public void readFromFile() throws WSDLException, URISyntaxException {

        ReadWSDL readWSDL = new ReadWSDL();
        Definition definition = readWSDL.readWSDL("wsdl/HelloWorld.wsdl");
        Assert.assertNotNull(definition);
    }

    @Test
    public void readFromURL() throws WSDLException, URISyntaxException, MalformedURLException {

        ReadWSDL readWSDL = new ReadWSDL();
        Definition definition = readWSDL.readWSDL(new URL("http://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php?wsdl"));
        Assert.assertNotNull(definition);
    }

    @Test
    public void readFromJar() throws WSDLException, URISyntaxException {

        ReadWSDL readWSDL = new ReadWSDL();
        Definition definition = readWSDL.readWSDL("uddi_v3_service.wsdl");
        Assert.assertNotNull(definition);
    }
}
