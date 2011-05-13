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
package org.apache.juddi.jaxb;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @param <T>
 */
public class PrintUDDI<T> {

	static JAXBContext jaxbContext = null;
	
	private Marshaller getUDDIMarshaller() throws JAXBException {
		if (jaxbContext==null) {
			jaxbContext=JAXBContext.newInstance("org.uddi.api_v3");
		}
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		
		return marshaller;
	}
	
	public String print(T UDDIEntity) {
		String xml = "";
		@SuppressWarnings("unchecked")
		Class<T> type = (Class<T>) UDDIEntity.getClass();
		try {
			StringWriter writer = new StringWriter();
			JAXBElement<T> element = new JAXBElement<T>(new QName("",UDDIEntity.getClass().getName()),type,UDDIEntity);
			getUDDIMarshaller().marshal(element,writer);
			xml=writer.toString();
		} catch (JAXBException je) {
			
		}
		return xml;
	}

}
