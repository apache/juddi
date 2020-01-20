/*
 * Copyright 2017 The Apache Software Foundation.
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
package org.apache.juddi.v3.client.cryptor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;

/**
 *
 * @since 3.3.5
 * @author Alex O'Ree
 */
public class XmlUtils {

        private static final Log log = LogFactory.getLog(XmlUtils.class);

        public static Object unmarshal(Reader reader, Class...clazz) {

                try {
                        SAXParserFactory spf = SAXParserFactory.newInstance();
                        spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
                        spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                        spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                        spf.setNamespaceAware(true);

                        Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(reader));
                        JAXBContext jc = JAXBContext.newInstance(clazz);
                        Unmarshaller um = jc.createUnmarshaller();
                        return um.unmarshal(xmlSource);
                } catch (Exception ex) {
                        log.warn("Failed to unmarshall object. Increase logging to debug for additional information. 1" + ex.getMessage());
                        log.debug(ex.getMessage(), ex);
                }
                return null;

        }

        public static Object unmarshal(InputStream reader, Class clazz) {
                try {
                        SAXParserFactory spf = SAXParserFactory.newInstance();
                        spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
                        spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                        spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                        spf.setNamespaceAware(true);
                        Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(reader));
                        JAXBContext jc = JAXBContext.newInstance(clazz);
                        Unmarshaller um = jc.createUnmarshaller();
                        return um.unmarshal(xmlSource);
                } catch (Exception ex) {
                        log.warn("Failed to unmarshall object. Increase logging to debug for additional information. 2" + ex.getMessage());
                        log.debug(ex.getMessage(), ex);
                }
                return null;

        }

        public static Object unmarshal(Reader reader, String packageName) {
                try {
                        SAXParserFactory spf = SAXParserFactory.newInstance();
                        spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
                        spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                        spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                        spf.setNamespaceAware(true);
                        Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(reader));
                        JAXBContext jc = JAXBContext.newInstance(packageName);
                        
                        Unmarshaller um = jc.createUnmarshaller();
                        return ((javax.xml.bind.JAXBElement)um.unmarshal(xmlSource)).getValue();
                } catch (Exception ex) {
                        log.warn("Failed to unmarshall object. Increase logging to debug for additional information. 3" + ex.getMessage());
                        log.debug(ex.getMessage(), ex);
                }
                return null;

        }

        public static Object unmarshal(URL url, Class clazz) {
                InputStream openStream = null;
                Object obj = null;
                try {
                        SAXParserFactory spf = SAXParserFactory.newInstance();
                        spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
                        spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                        spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                        spf.setNamespaceAware(true);
                        openStream = url.openStream();
                        Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(openStream));
                        JAXBContext jc = JAXBContext.newInstance(clazz);
                        Unmarshaller um = jc.createUnmarshaller();
                        obj = um.unmarshal(xmlSource);
                } catch (Exception ex) {
                        log.warn("Failed to unmarshall object. Increase logging to debug for additional information. 4" + ex.getMessage());
                        log.debug(ex.getMessage(), ex);
                } finally {
                        if (openStream != null) {
                                try {
                                        openStream.close();
                                } catch (IOException ex) {
                                        log.debug(ex.getMessage(), ex);
                                }
                        }
                }
                return obj;
        }

        public static Object unmarshal(File file, Class clazz)  {
                try {
                        return unmarshal(file.toURI().toURL(), clazz);
                } catch (Exception ex) {
                        log.warn("Failed to unmarshall object. Increase logging to debug for additional information. 5" + ex.getMessage());
                        log.debug(ex.getMessage(), ex);
                }
                return null;
        }
}
