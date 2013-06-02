/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uddi.examples;

import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.*;
import org.uddi.custody_v3.KeyBag;
import org.uddi.custody_v3.TransferEntities;
import org.uddi.custody_v3.TransferToken;
import org.uddi.v3_service.UDDICustodyTransferPortType;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 * This provides an example of how to transfer custody of a business from one user to another on the same UDDI node. All child objects are also transfer
 * @author Alex
 */
public class UddiCustodyTransfer {

    private static UDDISecurityPortType security = null;
    private static UDDIPublicationPortType publish = null;
    private static UDDIInquiryPortType uddiInquiryService = null;
    private static UDDISubscriptionPortType uddiSubscriptionService = null;
    private static UDDICustodyTransferPortType custodyTransferPortType = null;
    boolean callbackRecieved = false;

    public UddiCustodyTransfer() {
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

            publish = transport.getUDDIPublishService();
            uddiInquiryService = transport.getUDDIInquiryService();
            uddiSubscriptionService = transport.getUDDISubscriptionService();
            custodyTransferPortType = transport.getUDDICustodyTransferService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws Exception {
        UddiCustodyTransfer sp = new UddiCustodyTransfer();
        sp.Transfer();
    }

    private void Transfer() throws Exception {

        DatatypeFactory df = DatatypeFactory.newInstance();
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTimeInMillis(System.currentTimeMillis());
        XMLGregorianCalendar xcal = df.newXMLGregorianCalendar(gcal);

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
        myBusName.setValue("UDDI's Business" + " " + xcal.toString());
        myBusEntity.getName().add(myBusName);
        myBusEntity.setBusinessServices(new BusinessServices());
        myBusEntity.getBusinessServices().getBusinessService().add(CreateBusinessService("UDDI"));
        SaveBusiness sb = new SaveBusiness();
        sb.getBusinessEntity().add(myBusEntity);
        sb.setAuthInfo(uddiAuthToken.getAuthInfo());
        BusinessDetail bd = publish.saveBusiness(sb);

        String keyUddiBiz = bd.getBusinessEntity().get(0).getBusinessKey();

        //create a business for the ROOT user
        myBusEntity = new BusinessEntity();
        myBusName = new Name();
        myBusName.setLang("en");
        myBusName.setValue("Root's Business" + " " + xcal.toString());
        myBusEntity.getName().add(myBusName);
        myBusEntity.setBusinessServices(new BusinessServices());
        myBusEntity.getBusinessServices().getBusinessService().add(CreateBusinessService("root"));
        sb = new SaveBusiness();
        sb.getBusinessEntity().add(myBusEntity);
        sb.setAuthInfo(rootAuthToken.getAuthInfo());
        bd = publish.saveBusiness(sb);

        String keyRootBiz = bd.getBusinessEntity().get(0).getBusinessKey();

        
        //Create a transfer token from ROOT to UDDI
        KeyBag kb = new KeyBag();
        kb.getKey().add(keyRootBiz);
        Holder<String> nodeidOUT = new Holder<String>();
        Holder<XMLGregorianCalendar> expiresOUT = new Holder<XMLGregorianCalendar>();
        Holder<byte[]> tokenOUT = new Holder<byte[]>();
        custodyTransferPortType.getTransferToken(rootAuthToken.getAuthInfo(), kb, nodeidOUT, expiresOUT, tokenOUT);

        //The magic part happens here, the user ROOT needs to give the user UDDI the token information out of band
        //in practice, all values must match exactly
        
        //UDDI now accepts the transfer
        TransferEntities te = new TransferEntities();
        te.setAuthInfo(uddiAuthToken.getAuthInfo());
        te.setKeyBag(kb);
        TransferToken tt = new TransferToken();
        tt.setExpirationTime(expiresOUT.value);
        tt.setNodeID(nodeidOUT.value);
        tt.setOpaqueToken(tokenOUT.value);
        te.setTransferToken(tt);
        custodyTransferPortType.transferEntities(te);

        //confirm the transfer
        GetOperationalInfo go = new GetOperationalInfo();
        go.setAuthInfo(rootAuthToken.getAuthInfo());
        go.getEntityKey().add(keyRootBiz);
        go.getEntityKey().add(keyUddiBiz);
        OperationalInfos operationalInfo = uddiInquiryService.getOperationalInfo(go);
        boolean ok = false;
        for (int i = 0; i < operationalInfo.getOperationalInfo().size(); i++) {
            if (operationalInfo.getOperationalInfo().get(i).getEntityKey().equalsIgnoreCase(keyRootBiz)) {
                if (operationalInfo.getOperationalInfo().get(i).getAuthorizedName().equalsIgnoreCase("root")) {
                    //no suprise here
                }
            } else if (operationalInfo.getOperationalInfo().get(i).getEntityKey().equalsIgnoreCase(keyUddiBiz)) {
                if (operationalInfo.getOperationalInfo().get(i).getAuthorizedName().equalsIgnoreCase("uddi")) {
                    //success
                    ok = true;
                }
            } else {
                System.out.println("unexpected key");
            }
        }

        System.out.println("Transfer " + (ok ? "success" : " failed"));
    }

    private BusinessService CreateBusinessService(String user) {
        BusinessService bs = new BusinessService();
        bs.getName().add(new Name());
        bs.getName().get(0).setValue(user + "'s callback endpoint");
        bs.setBindingTemplates(new BindingTemplates());
        BindingTemplate bt = new BindingTemplate();
        bt.setAccessPoint(new AccessPoint());
        bt.getAccessPoint().setValue("http://localhost:9999/" + user);
        bt.getAccessPoint().setUseType("endPoint");
        bs.getBindingTemplates().getBindingTemplate().add(bt);
        return bs;
    }
}
