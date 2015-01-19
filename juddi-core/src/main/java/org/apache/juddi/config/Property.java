/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
package org.apache.juddi.config;

import org.apache.juddi.subscription.SubscriptionNotifier;
import org.apache.juddi.v3.auth.CryptedXMLDocAuthenticator;
import org.apache.juddi.v3.auth.LdapExpandedAuthenticator;
import org.apache.juddi.v3.auth.LdapSimpleAuthenticator;
import org.apache.juddi.v3.auth.MD5XMLDocAuthenticator;

/**
 * This defines constants used for accessing information from jUDDI's
 * juddiv3.xml file
 *
 * @author various
 */
public interface Property {

        public final static String JUDDI_BASE_URL = "juddi.server.baseurl";
        public final static String JUDDI_BASE_URL_SECURE = "juddi.server.baseurlsecure";

        public final static String JUDDI_ROOT_PUBLISHER = "juddi.root.publisher";
        public final static String JUDDI_LOAD_INSTALL_DATA = "juddi.load.install.data";
        public final static String JUDDI_PERSISTENCEUNIT_NAME = "juddi.persistenceunit.name";
        public final static String JUDDI_CONFIGURATION_RELOAD_DELAY = "juddi.configuration.reload.delay";

        /**
         * This is not used in the code base
         */
    //public final static String JUDDI_LOCALE = "juddi.locale";
    //public final static String JUDDI_OPERATOR_EMAIL_ADDRESS = "juddi.operatorEmailAddress";
    //public final static String JUDDI_MAX_LENGTH = "juddi.maxNameLength";
        //public final static String JUDDI_MAX_NAME_ELEMENTS = "juddi.maxNameElementsAllowed";
        public final static String JUDDI_MAX_BUSINESSES_PER_PUBLISHER = "juddi.maxBusinessesPerPublisher";
        public final static String JUDDI_MAX_SERVICES_PER_BUSINESS = "juddi.maxServicesPerBusiness";
        public final static String JUDDI_MAX_BINDINGS_PER_SERVICE = "juddi.maxBindingsPerService";
        public final static String JUDDI_MAX_TMODELS_PER_PUBLISHER = "juddi.maxTModelsPerPublisher";
        public final static String JUDDI_CRYPTOR = "juddi.cryptor";
        public final static String JUDDI_KEYGENERATOR = "juddi.keygenerator";
        /**
         * used by the Jboss authenticator
         */
        public final static String JUDDI_SECURITY_DOMAIN = "juddi.auth.securityDomain";
        public final static String JUDDI_USERSFILE = "juddi.auth.usersfile";
        public final static String JUDDI_MAX_ROWS = "juddi.maxRows";
        public final static String JUDDI_MAX_IN_CLAUSE = "juddi.maxInClause";
        public final static String JUDDI_ROOT_PARTITION = "juddi.root.partition";
        /**
         * This is the business id that all of the UDDI services on this node
         * will be attached too (generally as defined in the install_data)
         * JUDDI-645
         */
        public final static String JUDDI_NODE_ROOT_BUSINESS = "juddi.root.businessId";
        /**
         * this is the unique identifier of this uddi service provider,
         * primarily used for clustered setups with the replication api
         * JUDDI-645
         */
        public final static String JUDDI_NODE_ID = "juddi.nodeId";
        public final static String JUDDI_TRANSFER_EXPIRATION_DAYS = "juddi.transfer.expiration.days";
        /**
         * identifies whether or not authentication is required for the Inquiry
         * endpoint
         */
        public final static String JUDDI_AUTHENTICATE_INQUIRY = "juddi.auth.Inquiry";
        public final static String JUDDI_AUTH_TOKEN_EXPIRATION = "juddi.auth.token.Expiration";
        public final static String JUDDI_AUTH_TOKEN_TIMEOUT = "juddi.auth.token.Timeout";

        /**
         * when set, auth tokens can only be used from the IP address they were
         * issued to.
         *
         */
        public final static String JUDDI_AUTH_TOKEN_ENFORCE_SAME_IP = "juddi.auth.token.enforceSameIPRule";

        /**
         * Whether not the token is used with each transition, default should be
         * true
         */
        public final static String JUDDI_AUTHENTICATOR_USE_TOKEN = "juddi.auth.authenticator[@useAuthToken]";

        /**
         * This points to the class of the authenticator
         */
        public final static String JUDDI_AUTHENTICATOR = "juddi.auth.authenticator.class";
        /**
         * @see LdapSimpleAuthenticator
         */
        public final static String JUDDI_AUTHENTICATOR_URL = "juddi.auth.authenticator.url";
        /**
         * @see LdapSimpleAuthenticator
         */
        public final static String JUDDI_AUTHENTICATOR_INITIAL_CONTEXT = "juddi.auth.authenticator.initialcontext";
        /**
         * @see LdapSimpleAuthenticator
         */
        public final static String JUDDI_AUTHENTICATOR_STYLE = "juddi.auth.authenticator.style";
        /**
         * @see LdapExpandedAuthenticator
         */
        public final static String JUDDI_AUTHENTICATOR_LDAP_EXPANDED_STR = "juddi.auth.authenticator.ldapexp";

        /**
         * if enabled, tmodels must exist before using them binding templates
         * must exist before a subscription can be made access point hosting
         * redirector/binding template must exist before it can be made
         *
         * @since 3.1.5
         */
        public final static String JUDDI_ENFORCE_REFERENTIAL_INTEGRITY = "juddi.validation.enforceReferentialIntegrity";
        public final static String JUDDI_SUBSCRIPTION_EXPIRATION_DAYS = "juddi.subscription.expiration.days";
        public final static String JUDDI_SUBSCRIPTION_NOTIFICATION = "juddi.subscription.notification";
        public final static String JUDDI_SUBSCRIPTION_CHUNKEXPIRATION_MINUTES = "juddi.subscription.chunkexpiration.minutes";
        public final static String JUDDI_SUBSCRIPTION_MAXENTITIES = "juddi.subscription.maxentities";

        /*
         * These are not yet used
         public final static Strin   g JUDDI_SUBSCRIPTION_TRUSTSTORE_TYPE="juddi.subscription.truststore.type";
         public final static String JUDDI_SUBSCRIPTION_TRUSTSTORE_FILE="juddi.subscription.truststore.filename";
         public final static String JUDDI_SUBSCRIPTION_TRUSTSTORE_PASSWORD="juddi.subscription.truststore.password";
         public final static String JUDDI_SUBSCRIPTION_TRUSTSTORE_ENCRYPTED="juddi.subscription.truststore.password[@isPasswordEncrypted]";
         public final static String JUDDI_SUBSCRIPTION_TRUSTSTORE_CRYPTOPROVIDER="juddi.subscription.truststore.password[@cryptoProvider]";
    
    
         public final static String JUDDI_SUBSCRIPTION_KEYSTORE_TYPE="juddi.subscription.keystore.type";
         public final static String JUDDI_SUBSCRIPTION_KEYSTORE_FILE="juddi.subscription.keystore.filename";
         public final static String JUDDI_SUBSCRIPTION_KEYSTORE_PASSWORD="juddi.subscription.keystore.password";
         public final static String JUDDI_SUBSCRIPTION_KEYALIAS="juddi.subscription.keystore.alias";
         public final static String JUDDI_SUBSCRIPTION_KEYPASSWORD="juddi.subscription.keystore.keypassword";
         public final static String JUDDI_SUBSCRIPTION_KEYPASSWORD_ENCRYPTED="juddi.subscription.keystore.keypassword[@isPasswordEncrypted]";
         public final static String JUDDI_SUBSCRIPTION_KEYPASSWORD_CRYPTOPROVIDER="juddi.subscription.keypassword.password[@cryptoProvider]";
         public final static String JUDDI_SUBSCRIPTION_KEYSTORE_ENCRYPTED="juddi.subscription.keystore.password[@isPasswordEncrypted]";
         public final static String JUDDI_SUBSCRIPTION_KEYSTORE_CRYPTOPROVIDER="juddi.subscription.keystore.password[@cryptoProvider]";
         */
        public final static String JUDDI_NOTIFICATION_START_BUFFER = "juddi.notification.start.buffer";
        public final static String JUDDI_NOTIFICATION_INTERVAL = "juddi.notification.interval";
        /**
         * default value = 1000
         *
         * @see SubscriptionNotifier
         */
        public final static String JUDDI_NOTIFICATION_ACCEPTABLE_LAGTIME = "juddi.notification.acceptableLagtime";
        /**
         * maximum delivery count
         *
         * @see SubscriptionNotifier
         */
        public final static String JUDDI_NOTIFICATION_MAX_TRIES = "juddi.notification.maxTries";
        public final static String JUDDI_NOTIFICATION_LIST_RESET_INTERVAL = "juddi.notification.maxTriesResetInterval";
        /**
         * send an auth token with the result set? default is false
         *
         * @see SubscriptionNotifier
         * @since 3.2
         */
        public final static String JUDDI_NOTIFICATION_SENDAUTHTOKEN = "juddi.notification.sendAuthTokenWithResultList";
        public final static String JUDDI_JNDI_REGISTRATION = "juddi.jndi.registration";
        public final static String JUDDI_RMI_PORT = "juddi.rmi.port";
        public final static String JUDDI_RMI_REGISTRATION = "juddi.rmi.registration";
        public final static String JUDDI_RMI_REGISTRY_PORT = "juddi.rmi.registry.port";

        public final static String JUDDI_EMAIL_PREFIX = "juddi.mail.smtp.prefix";
        public final static String JUDDI_EMAIL_FROM = "juddi.mail.smtp.from";
        public final static String DEFAULT_JUDDI_EMAIL_PREFIX = "juddi.";
        public final static String DEFAULT_CRYPTOR = "org.apache.juddi.v3.client.cryptor.DefaultCryptor";
        public final static String DEFAULT_USERSFILE = "juddi-users.properties";
        public final static String DEFAULT_XML_USERSFILE = "juddi-users.xml";
        /**
         * @see CryptedXMLDocAuthenticator
         */
        public final static String DEFAULT_ENCRYPTED_XML_USERSFILE = "juddi-users-encrypted.xml";
        /**
         * @see MD5XMLDocAuthenticator
         */
        public final static String DEFAULT_HASHED_XML_USERSFILE = "juddi-users-hashed.xml";
        public final static String DEFAULT_SECURITY_DOMAIN = "java:/jaas/other";
        public final static boolean DEFAULT_LOAD_INSTALL_DATA = true;
        public final static String DEFAULT_BASE_URL = "http://localhost:8080/juddiv3";
        public final static String DEFAULT_BASE_URL_SECURE = "https://localhost:8443/juddiv3";
        /* Allowing the the user to override jpa persistence properties in the juddi.properties file */
        public final static String PERSISTENCE_PROVIDER = "persistenceProvider";
        public final static String DATASOURCE = "hibernate.connection.datasource";
        public final static String HBM_DDL_AUTO = "hibernate.hbm2ddl.auto";
        public final static String DEFAULT_SCHEMA = "hibernate.default_schema";
        public final static String HIBERNATE_DIALECT = "hibernate.dialect";
        /**
         * @since 3.3 FUTURE USE
         */
        public final static String JUDDI_ACCESS_CONTROL_PROVIDER = "juddi.accessControlProvider";
        /**
         * @since 3.2, used for Apache Commons Configuration XML config file
         */
        public static final String ENCRYPTED_ATTRIBUTE = "[@encrypted]";

        /**
         * Used for HTTP Header based authentication for web proxies
         *
         * @since 3.2.1
         */
        public static final String JUDDI_AUTHENTICATOR_HTTP_HEADER_NAME = "juddi.auth.authenticator.header";

        /**
         * Records inquiry find* requests to disk, sans auth token
         *
         * @since 3.2.1
         */
        public final static String JUDDI_LOGGING_FindApiCalls = "juddi.logging.logInquirySearchPayloads";
        /**
         * The UDDI v3 spec specifically calls for supporting this, however it
         * creates significant performance problems in jUDDI when there are a
         * large number of business and services. Defaults to true if not
         * defined
         *
         * @since 3.3
         */
        public static String JUDDI_ENABLE_FIND_BUSINESS_TMODEL_BAG_FILTERING = "juddi.preformance.enableFindBusinessTModelBagFiltering";
        /**
         * When set to true, juddi with reject publish requests when at least
         * one digitally signed entity cannot be cryptographically validated
         * JUDDI-862
         * 
         * Note: this is a prefix
         *
         * @since 3.3
         */
        public static String JUDDI_REJECT_ENTITIES_WITH_INVALID_SIG_PREFIX = "juddi.validation.rejectInvalidSignatures.";
        /**
         * @since 3.3.
         */
        public static String JUDDI_REJECT_ENTITIES_WITH_INVALID_SIG_ENABLE = "juddi.validation.rejectInvalidSignatures.enable";
}
