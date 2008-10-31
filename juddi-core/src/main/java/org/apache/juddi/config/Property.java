package org.apache.juddi.config;

public interface Property 
{
	public final static String JUDDI_CONFIGURATION_RELOAD_DELAY  ="juddi.configuration.reload.delay";
	public final static String JUDDI_LOCALE                      ="juddi.locale";
	public final static String JUDDI_OPERATOR_EMAIL_ADDRESS      ="juddi.operatorEmailAddress";
	public final static String JUDDI_MAX_LENGTH                  ="juddi.maxNameLength";
	public final static String JUDDI_MAX_NAME_ELEMENTS           ="juddi.maxNameElementsAllowed";
	public final static String JUDDI_MAX_BUSINESSES_PER_PUBLISHER="juddi.maxBusinessesPerPublisher";
	public final static String JUDDI_MAX_SERVICES_PER_BUSINESS   ="juddi.maxServicesPerBusiness";
	public final static String JUDDI_MAX_BINDINGS_PER_SERVICE    ="juddi.maxBindingsPerService";
	public final static String JUDDI_MAX_TMODELS_PER_PUBLISHER   ="juddi.maxTModelsPerPublisher";
	public final static String JUDDI_AUTHENTICATOR               ="juddi.authenticator";
	public final static String JUDDI_UUID_GENERATOR              ="juddi.uuidgen";
	public final static String JUDDI_UUID_COMMAND                ="juddi.uuidgenCommand";
	public final static String JUDDI_CRYPTOR                     ="juddi.cryptor";
	public final static String JUDDI_VALIDATOR                   ="juddi.validator";
	public final static String JUDDI_USERSFILE                   ="juddi.usersfile";
}
