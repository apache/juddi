using System;
using System.Collections.Generic;
using System.Text;
using org.uddi.apiv3;
using org.apache.juddi.apiv3;
namespace org.apache.juddi.v3.client.transport
{
   public abstract class Transport
    {
       	
	public readonly static String DEFAULT_NODE_NAME             = "default";
	
	public abstract UDDI_Inquiry_SoapBinding getUDDIInquiryService(String enpointURL);//           throws TransportException;
	public abstract UDDI_Security_SoapBinding getUDDISecurityService(String enpointURL);//         throws TransportException;
	public abstract UDDI_Publication_SoapBinding getUDDIPublishService(String enpointURL);//       throws TransportException;
	public abstract UDDI_Subscription_SoapBinding getUDDISubscriptionService(String enpointURL);// throws TransportException;
	public abstract UDDI_CustodyTransfer_SoapBinding getUDDICustodyTransferService(String enpointURL);// throws TransportException;
	public abstract UDDI_SubscriptionListener_SoapBinding getUDDISubscriptionListenerService(String enpointURL);// throws TransportException;
	public abstract JUDDIApiService getJUDDIApiService(String enpointURL);// throws TransportException;
	
	public abstract UDDI_Inquiry_SoapBinding getUDDIInquiryService();// throws TransportException {
		
	public abstract UDDI_Security_SoapBinding getUDDISecurityService();
	public abstract UDDI_Publication_SoapBinding getUDDIPublishService();

    public abstract UDDI_Subscription_SoapBinding getUDDISubscriptionService();
    public abstract UDDI_SubscriptionListener_SoapBinding getUDDISubscriptionListenerService();
    public abstract UDDI_CustodyTransfer_SoapBinding getUDDICustodyTransferService();
    public abstract JUDDIApiService getJUDDIApiService();

    }
}
