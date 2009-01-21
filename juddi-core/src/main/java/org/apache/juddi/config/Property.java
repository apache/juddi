package org.apache.juddi.config;

public interface Property 
{
	
	public final static String JUDDI_AUTHENTICATOR               ="juddi.authenticator";
	public final static String JUDDI_CONFIGURATION_RELOAD_DELAY  ="juddi.configuration.reload.delay";
	public final static String JUDDI_LOCALE                      ="juddi.locale";
	public final static String JUDDI_OPERATOR_EMAIL_ADDRESS      ="juddi.operatorEmailAddress";
	public final static String JUDDI_MAX_LENGTH                  ="juddi.maxNameLength";
	public final static String JUDDI_MAX_NAME_ELEMENTS           ="juddi.maxNameElementsAllowed";
	public final static String JUDDI_MAX_BUSINESSES_PER_PUBLISHER="juddi.maxBusinessesPerPublisher";
	public final static String JUDDI_MAX_SERVICES_PER_BUSINESS   ="juddi.maxServicesPerBusiness";
	public final static String JUDDI_MAX_BINDINGS_PER_SERVICE    ="juddi.maxBindingsPerService";
	public final static String JUDDI_MAX_TMODELS_PER_PUBLISHER   ="juddi.maxTModelsPerPublisher";
	public final static String JUDDI_CRYPTOR                     ="juddi.cryptor";
	public final static String JUDDI_KEYGENERATOR                ="juddi.keygenerator";
	public final static String JUDDI_VALIDATOR                   ="juddi.validator";
	public final static String JUDDI_SECURITY_DOMAIN             ="juddi.securityDomain";
	public final static String JUDDI_USERSFILE                   ="juddi.usersfile";
	public final static String JUDDI_MAX_ROWS                    ="juddi.maxRows";
	public final static String JUDDI_MAX_IN_CLAUSE               ="juddi.maxInClause";
	public final static String JUDDI_ROOT_PARTITION              ="juddi.rootPartition";
	public final static String JUDDI_NODE_ID                     ="juddi.nodeId";
	public final static String JUDDI_TRANSFER_EXPIRATION_DAYS    ="juddi.transfer.expiration.days";
	
	
	public final static String DEFAULT_CRYPTOR                   ="org.apache.juddi.cryptor.DefaultCryptor";
	public final static String DEFAULT_USERSFILE                 ="juddi-users.properties";
	public final static String DEFAULT_XML_USERSFILE             ="juddi-users.xml";
	public final static String DEFAULT_ENCRYPTED_XML_USERSFILE   ="juddi-users-encrypted.xml";
	public final static String DEFAULT_SECURITY_DOMAIN           ="java:/jaas/other";
	
}
