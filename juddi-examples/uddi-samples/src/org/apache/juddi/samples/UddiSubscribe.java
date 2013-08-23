/*
 * Copyright 2001-2013 The Apache Software Foundation.
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
package org.apache.juddi.samples;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Holder;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.*;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * Thie class shows you how to create a business and a subscription using UDDI
 * Subscription asynchronous callbacks
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UddiSubscribe {

    private static UDDISecurityPortType security = null;
    private static JUDDIApiPortType juddiApi = null;
    private static UDDIPublicationPortType publish = null;
    private static UDDIInquiryPortType uddiInquiryService = null;
    private static UDDISubscriptionPortType uddiSubscriptionService = null;
    boolean callbackRecieved = false;

    public UddiSubscribe() {
        try {
            // create a manager and read the config in the archive; 
            // you can use your config file name
            UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
            // register the clerkManager with the client side container
            UDDIClientContainer.addClient(clerkManager);            // a ClerkManager can be a client to multiple UDDI nodes, so 
            // supply the nodeName (defined in your uddi.xml.
            // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
            Transport transport = clerkManager.getTransport("default");
            // Now you create a reference to the UDDI API
            security = transport.getUDDISecurityService();
            juddiApi = transport.getJUDDIApiService();
            publish = transport.getUDDIPublishService();
            uddiInquiryService = transport.getUDDIInquiryService();
            uddiSubscriptionService = transport.getUDDISubscriptionService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws Exception {
        UddiSubscribe sp = new UddiSubscribe();
        sp.SetupAndRegisterCallback();
    }

    private void SetupAndRegisterCallback() throws Exception {
        String url = "http://localhost:7777/uddi_subscription_back";
        Endpoint ep = Endpoint.publish(url, new ClientSubscriptionCallback(this));


        DatatypeFactory df = DatatypeFactory.newInstance();
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        XMLGregorianCalendar xcal = df.newXMLGregorianCalendar(gcal);

        //?? note, you won't get subscription updates if you own the entity, at least with juddi


        //in this case, the user "root" is subscribing to a business entity published by the user "uddi"
        //"uddi" then updates the item and then logically "root" should be notified
        //before this happens "root" needs to publish a business, service and binding template that is used for the 
        //subscription callback

        /*
         * so the order of operation is
         * 1) uddi publisher's an entity 
         * 2) root wants updates on uddi's entity
         * 3) root starts a subscription listener service to process updates
         * 4) root creates a business and service with a bindingtemplate with the access point value = the the subscription listener's address
         * 5) root creates a subscription
         * 6) uddi updates the entity
         * 7) root's sub listener is notified (hopefully)
         * 
         */


        GetAuthToken getAuthTokenRoot = new GetAuthToken();
        getAuthTokenRoot.setUserID("root");
        getAuthTokenRoot.setCred("root");

        // Making API call that retrieves the authentication token for the 'root' user.
        AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
        System.out.println("root AUTHTOKEN = " + rootAuthToken.getAuthInfo());


        getAuthTokenRoot = new GetAuthToken();
        getAuthTokenRoot.setUserID("uddi");
        getAuthTokenRoot.setCred("uddi");

        // Making API call that retrieves the authentication token for the 'root' user.
        AuthToken uddiAuthToken = security.getAuthToken(getAuthTokenRoot);
        System.out.println("uddi AUTHTOKEN = " + rootAuthToken.getAuthInfo());

        //first publish a business user the user uddi
        BusinessEntity myBusEntity = new BusinessEntity();
        Name myBusName = new Name();
        myBusName.setLang("en");
        myBusName.setValue("User UDDI's Business" + " " + xcal.toString());
        myBusEntity.getName().add(myBusName);
        SaveBusiness sb = new SaveBusiness();
        sb.getBusinessEntity().add(myBusEntity);
        sb.setAuthInfo(uddiAuthToken.getAuthInfo());
        BusinessDetail bd = publish.saveBusiness(sb);

        String keyRootSubscribesTo = bd.getBusinessEntity().get(0).getBusinessKey();
        System.out.println("UDDI's business saved as biz key " + keyRootSubscribesTo);
        BusinessEntity uddisBusiness = bd.getBusinessEntity().get(0);

        //reset
        List<Subscription> subscriptions = uddiSubscriptionService.getSubscriptions(rootAuthToken.getAuthInfo());
        DeleteSubscription ds = new DeleteSubscription();
        ds.setAuthInfo(rootAuthToken.getAuthInfo());
        for (int i = 0; i < subscriptions.size(); i++) {
            ds.getSubscriptionKey().add(subscriptions.get(i).getSubscriptionKey());
        }
        if (!ds.getSubscriptionKey().isEmpty()) {
            System.out.println("removing " + ds.getSubscriptionKey().size() + " old subscriptions");
            uddiSubscriptionService.deleteSubscription(ds);
        }

        // Creating the parent business entity that will contain our service.
        myBusEntity = new BusinessEntity();
        myBusName = new Name();
        myBusName.setLang("en");
        myBusName.setValue("My Business Dept 1" + " " + xcal.toString());
        myBusEntity.getName().add(myBusName);
        myBusEntity.setBusinessServices(new BusinessServices());
        BusinessService bs = new BusinessService();
        bs.getName().add(new Name());
        bs.getName().get(0).setValue("my callback endpoint");

        bs.setBindingTemplates(new BindingTemplates());
        BindingTemplate bt = new BindingTemplate();
        //bt.setCategoryBag(new CategoryBag());
        //   KeyedReference kr = new KeyedReference();
        //         kr.setTModelKey(UDDIConstants.);
        // bt.getCategoryBag().getKeyedReference().add(kr);
        bt.setAccessPoint(new AccessPoint());
        bt.getAccessPoint().setValue(url);
        bt.getAccessPoint().setUseType("endPoint");
        //Added per Kurt
        TModelInstanceInfo instanceInfo = new TModelInstanceInfo();
        instanceInfo.setTModelKey("uddi:uddi.org:transport:http");
        bt.setTModelInstanceDetails(new TModelInstanceDetails());
        bt.getTModelInstanceDetails().getTModelInstanceInfo().add(instanceInfo);

        bs.getBindingTemplates().getBindingTemplate().add(bt);


        myBusEntity.getBusinessServices().getBusinessService().add(bs);
        // Adding the business entity to the "save" structure, using our publisher's authentication info and saving away.
        sb = new SaveBusiness();
        sb.getBusinessEntity().add(myBusEntity);
        sb.setAuthInfo(rootAuthToken.getAuthInfo());
        bd = publish.saveBusiness(sb);

        String callbackBindingTemplate = bd.getBusinessEntity().get(0).getBusinessServices().getBusinessService().get(0).getBindingTemplates().getBindingTemplate().get(0).getBindingKey();
        System.out.println("myBusiness key:  " + bd.getBusinessEntity().get(0).getBusinessKey());
        System.out.println("callback binding template key:  " + callbackBindingTemplate);

        System.out.println("i'll stay awake until someone kills me");

        Holder<List<Subscription>> subscription = new Holder<List<Subscription>>();
        subscription.value = new ArrayList<Subscription>();
        Subscription sub = new Subscription();

        //this is required
        sub.setBindingKey(callbackBindingTemplate);

        sub.setBrief(true);
        sub.setNotificationInterval(df.newDuration(0));
        sub.setSubscriptionFilter(new SubscriptionFilter());
        sub.getSubscriptionFilter().setGetBusinessDetail(new GetBusinessDetail());
        sub.getSubscriptionFilter().getGetBusinessDetail().setAuthInfo(rootAuthToken.getAuthInfo());
        sub.getSubscriptionFilter().getGetBusinessDetail().getBusinessKey().add(keyRootSubscribesTo);
        subscription.value.add(sub);
        uddiSubscriptionService.saveSubscription(rootAuthToken.getAuthInfo(), subscription);


        //ok now we're ready to mess around with the user's "uddi" business
        //in this case, we're just setting the lang
        uddisBusiness.getName().get(0).setLang("en");
        sb = new SaveBusiness();
        sb.setAuthInfo(uddiAuthToken.getAuthInfo());
        sb.getBusinessEntity().add(uddisBusiness);
        publish.saveBusiness(sb);

        long timer = 310000;
        while (timer > 0 && !callbackRecieved) {
            Thread.sleep(5000);
            timer = timer - 5000;
        }
        ep.stop();
        if (callbackRecieved) {
            System.out.println("Callback recieved successfully");
            System.exit(0);
        } else {
            System.out.println("Callback NOT recieved!");
            System.exit(1);
        }

        //TODO clean up all the crap we just made
    }
}
