package org.uddi.api_v3.client.config;

public interface Property 
{
	public final static String UDDI_CONFIGURATION_RELOAD_DELAY  ="uddi.configuration.reload.delay";
	public final static String UDDI_INQUIRY_URL                 ="uddi.inquiry.url";
	public final static String UDDI_SECURITY_URL                ="uddi.security.url";
	public final static String UDDI_PUBLISH_URL                 ="uddi.publish.url";
	public final static String UDDI_SUBSCRIPTION_URL            ="uddi.subscription.url";
	public final static String UDDI_SUBSCRIPTION_LISTENER_URL	="uddi.subscription.listener.url";
	public final static String UDDI_PROXY_TRANSPORT             ="uddi.proxy.transport";
	
	public final static String UDDI_PROXY_FACTORY_INITIAL       ="uddi.proxy.factory.initial";
	public final static String UDDI_PROXY_PROVIDER_URL          ="uddi.proxy.provider.url";
	public final static String UDDI_PROXY_FACTORY_URL_PKS       ="uddi.proxy.factory.url.pkg";

	public final static String DEFAULT_UDDI_PROXY_TRANSPORT     ="org.uddi.api_v3.client.transport.JAXWSTransport";
}
