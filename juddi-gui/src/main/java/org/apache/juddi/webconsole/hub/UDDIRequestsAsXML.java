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
import javax.xml.bind.JAXB;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
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

    private static String PrettyPrintXML(String input) {
        if (input == null || input.length() == 0) {
            return "";
        }
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            //initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            StreamSource source = new StreamSource(new StringReader(input.trim()));
            transformer.transform(source, result);
            String xmlString = result.getWriter().toString();
            return (xmlString);
        } catch (Exception ex) {
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
        return PrettyPrintXML(sw.toString());
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
        return PrettyPrintXML(sw.toString());
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
        return PrettyPrintXML(sw.toString());
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
        return PrettyPrintXML(sw.toString());
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
            return JAXB.unmarshal(sr, FindBinding.class);
        }
        if (method.equalsIgnoreCase("findBusiness")) {
            return JAXB.unmarshal(sr, FindBusiness.class);
        }
        if (method.equalsIgnoreCase("findService")) {
            return JAXB.unmarshal(sr, FindService.class);
        }
        if (method.equalsIgnoreCase("findRelatedBusines")) {
            return JAXB.unmarshal(sr, FindRelatedBusinesses.class);
        }
        if (method.equalsIgnoreCase("findTModel")) {
            return JAXB.unmarshal(sr, FindTModel.class);
        }
        if (method.equalsIgnoreCase("getBindingDetail")) {
            return JAXB.unmarshal(sr, GetBindingDetail.class);
        }
        if (method.equalsIgnoreCase("getBusinessDetail")) {
            return JAXB.unmarshal(sr, GetBusinessDetail.class);
        }
        if (method.equalsIgnoreCase("getServiceDetail")) {
            return JAXB.unmarshal(sr, GetServiceDetail.class);
        }
        if (method.equalsIgnoreCase("getOperationalInfo")) {
            return JAXB.unmarshal(sr, GetOperationalInfo.class);
        }
        if (method.equalsIgnoreCase("getTModelDetail")) {
            return JAXB.unmarshal(sr, GetTModelDetail.class);
        }
        return null;
    }

    private static Object getObjectPublish(String method, String content) {
        StringReader sr = new StringReader(content);
        if (method.equalsIgnoreCase("addPublisherAssertions")) {
            return JAXB.unmarshal(sr, AddPublisherAssertions.class);
        }
        if (method.equalsIgnoreCase("deleteBinding")) {
            return JAXB.unmarshal(sr, DeleteBinding.class);
        }
        if (method.equalsIgnoreCase("deleteBusiness")) {
            return JAXB.unmarshal(sr, DeleteBusiness.class);
        }
        if (method.equalsIgnoreCase("deletePublisherAssertions")) {
            return JAXB.unmarshal(sr, DeletePublisherAssertions.class);
        }
        if (method.equalsIgnoreCase("deleteService")) {
            return JAXB.unmarshal(sr, DeleteService.class);
        }
        if (method.equalsIgnoreCase("deleteTModel")) {
            return JAXB.unmarshal(sr, DeleteTModel.class);
        }
        if (method.equalsIgnoreCase("getAssertionStatusReport")) {
            return JAXB.unmarshal(sr, GetAssertionStatusReport.class);
        }
        if (method.equalsIgnoreCase("getPublisherAssertions")) {
            return JAXB.unmarshal(sr, GetPublisherAssertions.class);
        }
        if (method.equalsIgnoreCase("getRegisteredInfo")) {
            return JAXB.unmarshal(sr, GetRegisteredInfo.class);
        }
        if (method.equalsIgnoreCase("saveBinding")) {
            return JAXB.unmarshal(sr, SaveBinding.class);
        }
        if (method.equalsIgnoreCase("saveBusiness")) {
            return JAXB.unmarshal(sr, SaveBusiness.class);
        }
        if (method.equalsIgnoreCase("saveTModel")) {
            return JAXB.unmarshal(sr, SaveTModel.class);
        }
        if (method.equalsIgnoreCase("saveService")) {
            return JAXB.unmarshal(sr, SaveService.class);
        }
        if (method.equalsIgnoreCase("setPublisherAssertions")) {
            return JAXB.unmarshal(sr, SetPublisherAssertions.class);
        }
        return null;
    }

    private static Object getObjectCustody(String method, String content) {
        StringReader sr = new StringReader(content);
        if (method.equalsIgnoreCase("discardTransferToken")) {
            return JAXB.unmarshal(sr, SetPublisherAssertions.class);
        }
        if (method.equalsIgnoreCase("getTransferToken")) {
            return JAXB.unmarshal(sr, SetPublisherAssertions.class);
        }
        if (method.equalsIgnoreCase("transferEntities")) {
            return JAXB.unmarshal(sr, SetPublisherAssertions.class);
        }
        return null;
    }

    private static Object getObjectSubscription(String method, String content) {
        StringReader sr = new StringReader(content);
        if (method.equalsIgnoreCase("deleteSubscription")) {
            return JAXB.unmarshal(sr, DeleteSubscription.class);
        }
        if (method.equalsIgnoreCase("getSubscriptionResults")) {
            return JAXB.unmarshal(sr, GetSubscriptionResults.class);
        }
        if (method.equalsIgnoreCase("getSubscriptions")) {
            return JAXB.unmarshal(sr, GetSubscriptions.class);
        }
        if (method.equalsIgnoreCase("saveSubscription")) {
            return JAXB.unmarshal(sr, SaveSubscription.class);
        }
        return null;
    }
}
