package org.apache.juddi.v3.client;

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
}
