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
package org.apache.juddi.webconsole.hub;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXB;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.juddi.v3.client.cryptor.XmlUtils;
import org.uddi.api_v3.AddPublisherAssertions;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeletePublisherAssertions;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindRelatedBusinesses;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetAssertionStatusReport;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetOperationalInfo;
import org.uddi.api_v3.GetPublisherAssertions;
import org.uddi.api_v3.GetRegisteredInfo;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.SetPublisherAssertions;
import org.uddi.custody_v3.DiscardTransferToken;
import org.uddi.custody_v3.GetTransferToken;
import org.uddi.custody_v3.TransferEntities;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.GetSubscriptions;
import org.uddi.sub_v3.SaveSubscription;

/**
 * This class generates XML as String objects for UDDI requests.
 * This is used from the "advanced" web pages
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UDDIRequestsAsXML {

    private static String prettyPrintXML(String input) {
        if (input == null || input.length() == 0) {
            return "";
        }
        try {
            TransformerFactory transFactory = TransformerFactory.newInstance();
            transFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            Transformer transformer = transFactory.newTransformer();
            
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StreamResult result = new StreamResult(new StringWriter());
            StreamSource source = new StreamSource(new StringReader(input.trim()));
            transformer.transform(source, result);
            String xmlString = result.getWriter().toString();
            return (xmlString);
        } catch (Exception ex) {
            Logger.getLogger(UDDIRequestsAsXML.class.getName()).log(Level.WARNING, null, ex);
        }
        return null;
    }

    public static String getInquiry(String method) {
        StringWriter sw = new StringWriter();
        if (method.equalsIgnoreCase("findBinding")) {
            JAXB.marshal(new FindBinding(), sw);
        }
        if (method.equalsIgnoreCase("findBusiness")) {
            JAXB.marshal(new FindBusiness(), sw);
        }
        if (method.equalsIgnoreCase("findService")) {
            JAXB.marshal(new FindService(), sw);
        }
        if (method.equalsIgnoreCase("findRelatedBusines")) {
            JAXB.marshal(new FindRelatedBusinesses(), sw);
        }
        if (method.equalsIgnoreCase("findTModel")) {
            JAXB.marshal(new FindTModel(), sw);
        }
        if (method.equalsIgnoreCase("getBindingDetail")) {
            JAXB.marshal(new GetBindingDetail(), sw);
        }
        if (method.equalsIgnoreCase("getBusinessDetail")) {
            JAXB.marshal(new GetBusinessDetail(), sw);
        }
        if (method.equalsIgnoreCase("getServiceDetail")) {
            JAXB.marshal(new GetServiceDetail(), sw);
        }
        if (method.equalsIgnoreCase("getOperationalInfo")) {
            JAXB.marshal(new GetOperationalInfo(), sw);
        }
        if (method.equalsIgnoreCase("getTModelDetail")) {
            JAXB.marshal(new GetTModelDetail(), sw);
        }
        return prettyPrintXML(sw.toString());
    }

    public static String getPublish(String method) {
        StringWriter sw = new StringWriter();
        if (method.equalsIgnoreCase("addPublisherAssertions")) {
            JAXB.marshal(new AddPublisherAssertions(), sw);
        }
        if (method.equalsIgnoreCase("deleteBinding")) {
            JAXB.marshal(new DeleteBinding(), sw);
        }
        if (method.equalsIgnoreCase("deleteBusiness")) {
            JAXB.marshal(new DeleteBusiness(), sw);
        }
        if (method.equalsIgnoreCase("deletePublisherAssertions")) {
            JAXB.marshal(new DeletePublisherAssertions(), sw);
        }
        if (method.equalsIgnoreCase("deleteService")) {
            JAXB.marshal(new DeleteService(), sw);
        }
        if (method.equalsIgnoreCase("deleteTModel")) {
            JAXB.marshal(new DeleteTModel(), sw);
        }
        if (method.equalsIgnoreCase("getAssertionStatusReport")) {
            JAXB.marshal(new GetAssertionStatusReport(), sw);
        }
        if (method.equalsIgnoreCase("getPublisherAssertions")) {
            JAXB.marshal(new GetPublisherAssertions(), sw);
        }
        if (method.equalsIgnoreCase("getRegisteredInfo")) {
            JAXB.marshal(new GetRegisteredInfo(), sw);
        }
        if (method.equalsIgnoreCase("saveBinding")) {
            JAXB.marshal(new SaveBinding(), sw);
        }
        if (method.equalsIgnoreCase("saveBusiness")) {
            JAXB.marshal(new SaveBusiness(), sw);
        }
        if (method.equalsIgnoreCase("saveTModel")) {
            JAXB.marshal(new SaveTModel(), sw);
        }
        if (method.equalsIgnoreCase("saveService")) {
            JAXB.marshal(new SaveService(), sw);
        }
        if (method.equalsIgnoreCase("setPublisherAssertions")) {
            JAXB.marshal(new SetPublisherAssertions(), sw);
        }
        return prettyPrintXML(sw.toString());
    }

    public static String getCustody(String method) {
        StringWriter sw = new StringWriter();
        if (method.equalsIgnoreCase("discardTransferToken")) {
            JAXB.marshal(new DiscardTransferToken(), sw);
        }
        if (method.equalsIgnoreCase("getTransferToken")) {
            JAXB.marshal(new GetTransferToken(), sw);
        }
        if (method.equalsIgnoreCase("transferEntities")) {
            JAXB.marshal(new TransferEntities(), sw);
        }
        return prettyPrintXML(sw.toString());
    }

    public static String getSubscription(String method) {
        StringWriter sw = new StringWriter();
        if (method.equalsIgnoreCase("deleteSubscription")) {
            JAXB.marshal(new DeleteSubscription(), sw);
        }
        if (method.equalsIgnoreCase("getSubscriptionResults")) {
            JAXB.marshal(new GetSubscriptionResults(), sw);
        }
        if (method.equalsIgnoreCase("getSubscriptions")) {
            JAXB.marshal(new GetSubscriptions(), sw);
        }
        if (method.equalsIgnoreCase("saveSubscription")) {
            JAXB.marshal(new SaveSubscription(), sw);
        }
        return prettyPrintXML(sw.toString());
    }
    public static final String custody = "custody";
    public static final String inquiry = "inquiry";
    public static final String publish = "publish";
    public static final String subscription = "subscription";

    public static Object getObject(String service, String method, String content) {
        if (service.equalsIgnoreCase(inquiry)) {
            return getObjectInquiry(method, content);
        }
        if (service.equalsIgnoreCase(publish)) {
            return getObjectPublish(method, content);
        }
        if (service.equalsIgnoreCase(custody)) {
            return getObjectCustody(method, content);
        }
        if (service.equalsIgnoreCase(subscription)) {
            return getObjectSubscription(method, content);
        }
        return null;

    }

    private static Object getObjectInquiry(String method, String content) {
        StringReader sr = new StringReader(content);
        if (method.equalsIgnoreCase("findBinding")) {
            return JAXBunmarshal(sr, FindBinding.class);
        }
        if (method.equalsIgnoreCase("findBusiness")) {
            return JAXBunmarshal(sr, FindBusiness.class);
        }
        if (method.equalsIgnoreCase("findService")) {
            return JAXBunmarshal(sr, FindService.class);
        }
        if (method.equalsIgnoreCase("findRelatedBusines")) {
            return JAXBunmarshal(sr, FindRelatedBusinesses.class);
        }
        if (method.equalsIgnoreCase("findTModel")) {
            return JAXBunmarshal(sr, FindTModel.class);
        }
        if (method.equalsIgnoreCase("getBindingDetail")) {
            return JAXBunmarshal(sr, GetBindingDetail.class);
        }
        if (method.equalsIgnoreCase("getBusinessDetail")) {
            return JAXBunmarshal(sr, GetBusinessDetail.class);
        }
        if (method.equalsIgnoreCase("getServiceDetail")) {
            return JAXBunmarshal(sr, GetServiceDetail.class);
        }
        if (method.equalsIgnoreCase("getOperationalInfo")) {
            return JAXBunmarshal(sr, GetOperationalInfo.class);
        }
        if (method.equalsIgnoreCase("getTModelDetail")) {
            return JAXBunmarshal(sr, GetTModelDetail.class);
        }
        return null;
    }

    private static Object getObjectPublish(String method, String content) {
        StringReader sr = new StringReader(content);
        if (method.equalsIgnoreCase("addPublisherAssertions")) {
            return JAXBunmarshal(sr, AddPublisherAssertions.class);
        }
        if (method.equalsIgnoreCase("deleteBinding")) {
            return JAXBunmarshal(sr, DeleteBinding.class);
        }
        if (method.equalsIgnoreCase("deleteBusiness")) {
            return JAXBunmarshal(sr, DeleteBusiness.class);
        }
        if (method.equalsIgnoreCase("deletePublisherAssertions")) {
            return JAXBunmarshal(sr, DeletePublisherAssertions.class);
        }
        if (method.equalsIgnoreCase("deleteService")) {
            return JAXBunmarshal(sr, DeleteService.class);
        }
        if (method.equalsIgnoreCase("deleteTModel")) {
            return JAXBunmarshal(sr, DeleteTModel.class);
        }
        if (method.equalsIgnoreCase("getAssertionStatusReport")) {
            return JAXBunmarshal(sr, GetAssertionStatusReport.class);
        }
        if (method.equalsIgnoreCase("getPublisherAssertions")) {
            return JAXBunmarshal(sr, GetPublisherAssertions.class);
        }
        if (method.equalsIgnoreCase("getRegisteredInfo")) {
            return JAXBunmarshal(sr, GetRegisteredInfo.class);
        }
        if (method.equalsIgnoreCase("saveBinding")) {
            return JAXBunmarshal(sr, SaveBinding.class);
        }
        if (method.equalsIgnoreCase("saveBusiness")) {
            return JAXBunmarshal(sr, SaveBusiness.class);
        }
        if (method.equalsIgnoreCase("saveTModel")) {
            return JAXBunmarshal(sr, SaveTModel.class);
        }
        if (method.equalsIgnoreCase("saveService")) {
            return JAXBunmarshal(sr, SaveService.class);
        }
        if (method.equalsIgnoreCase("setPublisherAssertions")) {
            return JAXBunmarshal(sr, SetPublisherAssertions.class);
        }
        return null;
    }
    
    private static Object JAXBunmarshal(StringReader content, Class clazz) {
        return XmlUtils.unmarshal(content, clazz);
    }

    private static Object getObjectCustody(String method, String content) {
        StringReader sr = new StringReader(content);
        if (method.equalsIgnoreCase("discardTransferToken")) {
            return JAXBunmarshal(sr, SetPublisherAssertions.class);
        }
        if (method.equalsIgnoreCase("getTransferToken")) {
            return JAXBunmarshal(sr, SetPublisherAssertions.class);
        }
        if (method.equalsIgnoreCase("transferEntities")) {
            return JAXBunmarshal(sr, SetPublisherAssertions.class);
        }
        return null;
    }

    private static Object getObjectSubscription(String method, String content) {
        StringReader sr = new StringReader(content);
        if (method.equalsIgnoreCase("deleteSubscription")) {
            return JAXBunmarshal(sr, DeleteSubscription.class);
        }
        if (method.equalsIgnoreCase("getSubscriptionResults")) {
            return JAXBunmarshal(sr, GetSubscriptionResults.class);
        }
        if (method.equalsIgnoreCase("getSubscriptions")) {
            return JAXBunmarshal(sr, GetSubscriptions.class);
        }
        if (method.equalsIgnoreCase("saveSubscription")) {
            return JAXBunmarshal(sr, SaveSubscription.class);
        }
        return null;
    }
}
