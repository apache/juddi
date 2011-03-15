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


package org.apache.juddi;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

import org.apache.juddi.api_v3.Publisher;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 * This object was copied from the UDDI generated package 
 * and hand-edited to fit the juddi specific Publisher structures.
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@XmlRegistry
public class ObjectFactory {
	
	 private final static QName _InitialContextInfo_QNAME = new QName("", "initialContextInfo");
    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.apache.juddi.api_v3
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Publisher }
     * 
     */
    public InitialContextInfo createInitialContextInfo() {
        return new InitialContextInfo();
    }
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Publisher }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "initialContextInfo")
    public JAXBElement<InitialContextInfo> createInitialContextInfo(InitialContextInfo value) {
        return new JAXBElement<InitialContextInfo>(_InitialContextInfo_QNAME, InitialContextInfo.class, null, value);
    }

  
}
