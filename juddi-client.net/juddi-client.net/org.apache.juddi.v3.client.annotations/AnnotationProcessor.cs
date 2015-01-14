/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

using org.apache.juddi.v3.client.annotations;
using org.apache.juddi.v3.client.log;
using org.uddi.apiv3;
using System;
using System.Collections.Generic;

using System.Text;
using System.Web.Services;

namespace org.apache.juddi.v3.client.config
{
    class AnnotationProcessor
    {
        static Log log = LogFactory.getLog(typeof(AnnotationProcessor));
        private static readonly String KEYED_REFERENCE = "keyedReference=";
        private static readonly String KEY_NAME = "keyName=";
        private static readonly String KEY_VALUE = "keyValue=";
        private static readonly String TMODEL_KEY = "tModelKey=";

        public List<businessService> readServiceAnnotations(string[] classes, Properties properties)
        {
            List<businessService> items = new List<businessService>();

            if (classes != null)
            {
                foreach (string s in classes)
                {
                    businessService b = readServiceAnnotations(s, properties);
                    if (b != null)
                        items.Add(b);
                }

            }
            return items;

        }

        public businessService readServiceAnnotations(String classWithAnnotations, Properties properties)
        {
           
            Type t = Type.GetType(classWithAnnotations, false, true);
            if (t != null)
            {
                businessService service = new businessService();
                object[] attrib = t.GetCustomAttributes(typeof(UDDIService), true);

                object[] ws = t.GetCustomAttributes(typeof(System.Web.Services.WebServiceBindingAttribute), true);
                WebServiceBindingAttribute webServiceAnnotation = null;
                if (ws != null && ws.Length > 0)
                {
                    webServiceAnnotation = ((WebServiceBindingAttribute[])ws)[0];
                }
                if (attrib != null && attrib.Length > 0)
                {

                    UDDIService[] bits = attrib as UDDIService[];
                    UDDIService uddiService = bits[0];
                    name n = new name();
                    n.lang = uddiService.lang;
                    service.businessKey = (TokenResolver.replaceTokens(uddiService.businessKey, properties));
                    service.serviceKey = (TokenResolver.replaceTokens(uddiService.serviceKey, properties));
                    if (!"".Equals(uddiService.serviceName, StringComparison.CurrentCultureIgnoreCase))
                    {
                        n.Value = (TokenResolver.replaceTokens(uddiService.serviceName, properties));
                    }
                    else if (webServiceAnnotation != null && !"".Equals(webServiceAnnotation.Name))
                    {
                        n.Value = (webServiceAnnotation.Name);
                    }
                    else
                    {
                        n.Value = (classWithAnnotations);
                    }
                    service.name = new name[] { n };
                    description d = new description();
                    d.lang = (uddiService.lang);
                    d.Value = (TokenResolver.replaceTokens(uddiService.description, properties));
                    service.description = new description[] { d };

                    //categoryBag on the service
                    if (!"".Equals(uddiService.categoryBag))
                    {
                        categoryBag categoryBag = parseCategoryBag(uddiService.categoryBag);
                        service.categoryBag = (categoryBag);
                    }

                    //bindingTemplate on service
                    bindingTemplate bindingTemplate = parseServiceBinding(classWithAnnotations, uddiService.lang, webServiceAnnotation, properties);
                    if (bindingTemplate != null)
                    {
                        bindingTemplate.serviceKey = (service.serviceKey);
                        if (service.bindingTemplates == null)
                        {
                            service.bindingTemplates = new bindingTemplate[] { bindingTemplate };
                        }
                        else
                        {
                            List<bindingTemplate> l = new List<bindingTemplate>();
                            l.AddRange(service.bindingTemplates);
                            l.Add(bindingTemplate);
                            service.bindingTemplates = l.ToArray();
                        }
                    }

                    return service;
                }
                else
                {
                    log.error("Missing UDDIService annotation in class " + classWithAnnotations);
                }
            }
            log.error("Unable to load type " + classWithAnnotations);
            return null;
            
        }



        private bindingTemplate parseServiceBinding(string classWithAnnotations, string lang, WebServiceBindingAttribute webServiceAnnotation, Properties properties)
        {

            bindingTemplate bindingTemplate = null;
            Type t = Type.GetType(classWithAnnotations, false, false);
            UDDIServiceBinding uddiServiceBinding = null;
            object[] attrib = t.GetCustomAttributes(typeof(UDDIServiceBinding), true);
            if (attrib != null && attrib.Length > 0)
                uddiServiceBinding = attrib[0] as UDDIServiceBinding;

            //= (UDDIServiceBinding) classWithAnnotations.getAnnotation(UDDIServiceBinding.class);
            //binding
            if (uddiServiceBinding != null)
            {
                bindingTemplate = new bindingTemplate();

                bindingTemplate.bindingKey = (TokenResolver.replaceTokens(uddiServiceBinding.bindingKey, properties));

                String bindingLang = (lang);
                if (uddiServiceBinding.lang != null)
                {
                    bindingLang = TokenResolver.replaceTokens(uddiServiceBinding.lang, properties);
                }
                description bindingDescription = new description();
                bindingDescription.lang = (bindingLang);
                bindingDescription.Value = (TokenResolver.replaceTokens(uddiServiceBinding.description, properties));
                bindingTemplate.description = new description[] { (bindingDescription) };

                accessPoint accessPoint = new accessPoint();
                accessPoint.useType = (AccessPointType.wsdlDeployment.ToString());
                if (!"".Equals(uddiServiceBinding.accessPointType))
                {
                    accessPoint.useType = (uddiServiceBinding.accessPointType);
                }
                if (!"".Equals(uddiServiceBinding.accessPoint))
                {
                    String endPoint = uddiServiceBinding.accessPoint;
                    endPoint = TokenResolver.replaceTokens(endPoint, properties);
                    log.debug("AccessPoint EndPoint=" + endPoint);
                    accessPoint.Value = (endPoint);
                }
                else if (webServiceAnnotation != null && webServiceAnnotation.Location != null)
                {
                    accessPoint.Value = (webServiceAnnotation.Location);
                }
                bindingTemplate.Item = (accessPoint);

                //tModelKeys on the binding
                if (!"".Equals(uddiServiceBinding.tModelKeys))
                {
                    String[] tModelKeys = uddiServiceBinding.tModelKeys.Split(',');
                    foreach (String tModelKey in tModelKeys)
                    {
                        tModelInstanceInfo instanceInfo = new tModelInstanceInfo();
                        instanceInfo.tModelKey = (tModelKey);
                        if (bindingTemplate.tModelInstanceDetails == null)
                        {
                            bindingTemplate.tModelInstanceDetails = (new tModelInstanceInfo[] { instanceInfo });
                        }
                        List<tModelInstanceInfo> l = new List<tModelInstanceInfo>();
                        l.AddRange(bindingTemplate.tModelInstanceDetails);
                        l.Add(instanceInfo);
                        bindingTemplate.tModelInstanceDetails = l.ToArray();

                    }
                }
                //categoryBag on the binding
                if (!"".Equals(uddiServiceBinding.categoryBag))
                {
                    categoryBag categoryBag = parseCategoryBag(uddiServiceBinding.categoryBag);
                    bindingTemplate.categoryBag = (categoryBag);
                }
            }
            else
            {
                log.error("Missing UDDIServiceBinding annotation in class " + classWithAnnotations);
            }
            return bindingTemplate;
        }

        private categoryBag parseCategoryBag(string categoryBagStr)
        {

            categoryBag cb = new categoryBag();
            log.debug("CategoryBag Annotation=" + cb);
            if (!"".Equals(categoryBagStr))
            {
                List<keyedReference> cbs = new List<keyedReference>();
                String[] sections = categoryBagStr.Split(',');
                foreach (String section in sections)
                {
                    if (section.StartsWith(KEYED_REFERENCE))
                    {
                        String keyedReferenceStr = section.Substring(KEYED_REFERENCE.Length, section.Length);
                        log.debug("Found KeyedReference=" + keyedReferenceStr);
                        String[] keyedReferences = keyedReferenceStr.Split(';');
                        keyedReference keyedReference = new keyedReference();
                        foreach (String key in keyedReferences)
                        {
                            if (key.StartsWith(KEY_NAME)) keyedReference.keyName = (key.Substring(KEY_NAME.Length, key.Length));
                            if (key.StartsWith(KEY_VALUE)) keyedReference.keyValue = (key.Substring(KEY_VALUE.Length, key.Length));
                            if (key.StartsWith(TMODEL_KEY)) keyedReference.tModelKey = (key.Substring(TMODEL_KEY.Length, key.Length));
                        }
                        log.debug("KeyedReference = " + KEY_NAME + keyedReference.keyName + " "
                                                      + KEY_VALUE + keyedReference.keyValue + " "
                                                      + TMODEL_KEY + keyedReference.tModelKey);
                        cbs.Add(keyedReference);
                    }
                    else
                    {
                        log.warn("Ignoring " + section);
                        //TODO add support for KeyedReferenceGroups?
                    }
                }
                cb.Items = cbs.ToArray();
            }
            return cb;
        }
    }
}



