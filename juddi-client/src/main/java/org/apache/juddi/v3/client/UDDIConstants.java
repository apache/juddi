package org.apache.juddi.v3.client;

/**
 * This file was borrowed from juddi-core's constants file with modifications
 *
 * @author Alex O'Ree
 */
public interface UDDIConstants {

    public static final String AND_ALL_KEYS = "andAllKeys";
    public static final String AND_ALL_KEYS_TMODEL = "uddi:uddi.org:findqualifier:andallkeys";
    public static final String APPROXIMATE_MATCH = "approximateMatch";
    public static final String APPROXIMATE_MATCH_TMODEL = "uddi:uddi.org:findqualifier:approximatematch";
    public static final String BINARY_SORT = "binarySort";
    public static final String BINARY_SORT_TMODEL = "uddi:uddi.org:sortorder:binarysort";
    public static final String BINDING_SUBSET = "bindingSubset";
    public static final String BINDING_SUBSET_TMODEL = "uddi:uddi.org:findqualifier:bindingsubset";
    public static final String CASE_INSENSITIVE_SORT = "caseInsensitiveSort";
    public static final String CASE_INSENSITIVE_SORT_TMODEL = "uddi:uddi.org:findqualifier:caseinsensitivesort";
    public static final String CASE_INSENSITIVE_MATCH = "caseInsensitiveMatch";
    public static final String CASE_INSENSITIVE_MATCH_TMODEL = "uddi:uddi.org:findqualifier:caseinsensitivematch";
    public static final String CASE_SENSITIVE_SORT = "caseSensitiveSort";
    public static final String CASE_SENSITIVE_SORT_TMODEL = "uddi:uddi.org:findqualifier:casesensitivesort";
    public static final String CASE_SENSITIVE_MATCH = "caseSensitiveMatch";
    public static final String CASE_SENSITIVE_MATCH_TMODEL = "uddi:uddi.org:findqualifier:casesensitivematch";
    public static final String COMBINE_CATEGORY_BAGS = "combineCategoryBags";
    public static final String COMBINE_CATEGORY_BAGS_TMODEL = "uddi:uddi.org:findqualifier:combinecategorybags";
    public static final String DIACRITIC_INSENSITIVE_MATCH = "diacriticInsensitiveMatch";
    public static final String DIACRITIC_INSENSITIVE_MATCH_TMODEL = "uddi:uddi.org:findqualifier:diacriticsinsensitivematch";
    public static final String DIACRITIC_SENSITIVE_MATCH = "diacriticSensitiveMatch";
    public static final String DIACRITIC_SENSITIVE_MATCH_TMODEL = "uddi:uddi.org:findqualifier:diacriticssensitivematch";
    public static final String EXACT_MATCH = "exactMatch";
    public static final String EXACT_MATCH_TMODEL = "uddi:uddi.org:findqualifier:exactmatch";
    public static final String SIGNATURE_PRESENT = "signaturePresent";
    public static final String SIGNATURE_PRESENT_TMODEL = "uddi:uddi.org:findqualifier:signaturepresent";
    public static final String OR_ALL_KEYS = "orAllKeys";
    public static final String OR_ALL_KEYS_TMODEL = "uddi:uddi.org:findqualifier:orallkeys";
    public static final String OR_LIKE_KEYS = "orLikeKeys";
    public static final String OR_LIKE_KEYS_TMODEL = "uddi:uddi.org:findqualifier:orlikekeys";
    public static final String SERVICE_SUBSET = "serviceSubset";
    public static final String SERVICE_SUBSET_TMODEL = "uddi:uddi.org:findqualifier:servicesubset";
    public static final String SORT_BY_NAME_ASC = "sortByNameAsc";
    public static final String SORT_BY_NAME_ASC_TMODEL = "uddi:uddi.org:findqualifier:sortbynameasc";
    public static final String SORT_BY_NAME_DESC = "sortByNameDesc";
    public static final String SORT_BY_NAME_DESC_TMODEL = "uddi:uddi.org:findqualifier:sortbynamedesc";
    public static final String SORT_BY_DATE_ASC = "sortByDateAsc";
    public static final String SORT_BY_DATE_ASC_TMODEL = "uddi:uddi.org:findqualifier:sortbydateasc";
    public static final String SORT_BY_DATE_DESC = "sortByDateDesc";
    public static final String SORT_BY_DATE_DESC_TMODEL = "uddi:uddi.org:findqualifier:sortbydatedesc";
    public static final String SUPPRESS_PROJECTED_SERVICES = "suppressProjectedServices";
    public static final String SUPPRESS_PROJECTED_SERVICES_TMODEL = "uddi:uddi.org:findqualifier:suppressprojectedservices";
    public static final String UTS_10 = "UTS-10";
    public static final String UTS_10_TMODEL = "uddi:uddi.org:sortorder:uts-10";
    public static String CLAUSE_WHERE = "where";
    public static String CLAUSE_GROUPBY = "group by";
    public static String CLAUSE_ORDERBY = "order by";
    public static String CLAUSE_HAVING = "having";
    public static String OPERATOR_OR = "or";
    public static String OPERATOR_AND = "and";
    public static String PREDICATE_EQUALS = "=";
    public static String PREDICATE_NOTEQUALS = "<>";
    public static String PREDICATE_LIKE = "like";
    public static String PREDICATE_IN = "in";
    public static String PREDICATE_GREATERTHAN = ">";
    public static String PREDICATE_LESSTHAN = "<";
    public static String SORT_ASC = "asc";
    public static String SORT_DESC = "desc";
    public static String WILDCARD = "%";
    /**
     * Category system consisting of namespace identifiers and the keywords
     * associated with the namespaces.
     */
    public static String TMODEL_GENERAL_KEYWORDS = "uddi:uddi.org:categorization:general_keywords";
    public static String UNCHECKED = "uddi-org:types:unchecked";
    public static String CHECKED = "uddi-org:types:checked";
    public static String UNCACHEABLE = "uddi-org:types:uncacheable";
    /**
     * Basic types of business relationships
     */
    public static String RELATIONSHIPS = "uddi:uddi.org:relationships";
    public static String KEY_GENERATOR = "uddi:uddi.org:keygenerator";
    /**
     * Identifier system used to point to the UDDI entity, using UDDI keys, that
     * is the logical replacement for the one in which isReplacedBy is used.
     */
    public static String IS_REPLACED_BY = "uddi:uddi.org:identifier:isreplacedby";
    /**
     * Category system used to point a value set or category group system tModel
     * to associated value set Web service implementations.
     */
    public static String IS_VALIDATED_BY = "uddi:uddi.org:categorization:validatedby";
    /**
     * Category system for referring tModels to other tModels for the purpose of
     * reuse.
     */
    public static String IS_DERVIVED_FROM = "uddi:uddi.org:categorization:derivedfrom";
    /**
     * Category system used to declare that a value set uses entity keys as
     * valid values.
     */
    public static String ENTITY_KEY_VALUES = "uddi:uddi.org:categorization:entitykeyvalues";
    /**
     * Category system used to point to the businessEntity associated with the
     * publisher of the tModel.
     */
    public static String OWNING_BUSINESS = "uddi:uddi.org:categorization:owningbusiness";
    /**
     * Secure Sockets Layer Version 3.0 with Server Authentication
     */
    public static String PROTOCOL_SSLv3 = "uddi:uddi.org:protocol:serverauthenticatedssl3";
    /**
     * Secure Sockets Layer Version 3.0 with Mutual Authentication
     */
    public static String PROTOCOL_SSLv3_CLIENT_CERT = "uddi:uddi.org:protocol:mutualauthenticatedssl3";
    /**
     * A Web service that uses HTTP transport -
     */
    public static String TRANSPORT_HTTP = "uddi:uddi.org:transport:http";
    /**
     * E-mail based Web service
     */
    public static String TRANSPORT_EMAIL = "uddi:uddi.org:transport:smtp";
    /**
     * File Transfer Protocol (FTP) based Web service
     */
    public static String TRANSPORT_FTP = "uddi:uddi.org:transport:ftp";
    /**
     * Fax-based Web service
     */
    public static String TRANSPORT_FAX = "uddi:uddi.org:transport:fax";
    /**
     * Telephone based service
     */
    public static String TRANSPORT_POTS = "uddi:uddi.org:transport:telephone";
    /**
     * Java RMI based service registered to the Java Registry
     */
    public static String TRANSPORT_RMI = "uddi:uddi.org:transport:rmi";
    /**
     * A Java RMI based service registered to a JNDI Registry
     */
    public static String TRANSPORT_JNDI_RMI = "uddi:uddi.org:transport:jndi-rmi";
    /**
     * A Web service that uses the AMQP transport
     */
    public static String TRANSPORT_AMQP = "uddi:uddi.org:transport:amqp";
    /**
     * A Web service that uses the OMG DDS transport
     */
    public static String TRANSPORT_OMGDDS = "uddi:uddi.org:transport:omgdds";
    /**
     * A Web service that uses UDP
     */
    public static String TRANSPORT_UDP = "uddi:uddi.org:transport:udp";
    /**
     * A Web service that uses the JMS API
     */
    public static String TRANSPORT_JMS = "uddi:uddi.org:transport:jms";
    /**
     * A tModel that represents the Representational State Transfer
     * architectural style
     */
    public static String PROTOCOL_REST = "uddi:uddi.org:protocol:rest";
    /**
     * A tModel that represents the SOAP 1.1 protocol
     */
    public static String PROTOCOL_SOAP = "uddi:uddi.org:protocol:soap";
    /**
     * A tModel that represents the SOAP 1.2 protocol
     */
    public static String PROTOCOL_SOAP12 = "uddi:uddi.org:protocol:soap12";
}
