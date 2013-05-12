package org.apache.juddi.v3.client;

import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.OverviewURL;
import org.uddi.api_v3.TModel;

/**
 * This file was borrowed from juddi-core's constants file with modifications
 *
 * @author Alex O'Ree
 */
public interface UDDIConstants {

    /**
     * andAllKeys: this changes the behavior for identifierBag to AND keys
     * rather than OR them. This is already the default for categoryBag and
     * tModelBag.
     */
    public static final String AND_ALL_KEYS = "andAllKeys";
    /**
     * andAllKeys: this changes the behavior for identifierBag to AND keys
     * rather than OR them. This is already the default for categoryBag and
     * tModelBag.
     */
    public static final String AND_ALL_KEYS_TMODEL = "uddi:uddi.org:findqualifier:andallkeys";
    /**
     * approximateMatch: signifies that wildcard search behavior is desired.
     * This behavior is defined by the uddi-org:approximatematch:SQL99 tModel
     * and means "approximate matching as defined for the character like
     * predicate in ISO/IEC9075-2:1999(E) Section 8.5 like predicate, where the
     * percent sign (%) indicates any number of characters and an underscore (_)
     * indicates any single character. The backslash character (\) is used as an
     * escape character for the percent sign, underscore and backslash
     * characters. This find qualifier adjusts the matching behavior for name,
     * keyValue and keyName (where applicable).
     */
    public static final String APPROXIMATE_MATCH = "approximateMatch";
    /**
     * approximateMatch: signifies that wildcard search behavior is desired.
     * This behavior is defined by the uddi-org:approximatematch:SQL99 tModel
     * and means "approximate matching as defined for the character like
     * predicate in ISO/IEC9075-2:1999(E) Section 8.5 like predicate, where the
     * percent sign (%) indicates any number of characters and an underscore (_)
     * indicates any single character. The backslash character (\) is used as an
     * escape character for the percent sign, underscore and backslash
     * characters. This find qualifier adjusts the matching behavior for name,
     * keyValue and keyName (where applicable).
     */
    public static final String APPROXIMATE_MATCH_TMODEL = "uddi:uddi.org:findqualifier:approximatematch";
    /**
     * binarySort: this qualifier allows for greater speed in sorting. It causes
     * a binary sort by name, as represented in Unicode codepoints.
     */
    public static final String BINARY_SORT = "binarySort";
    /**
     * binarySort: this qualifier allows for greater speed in sorting. It causes
     * a binary sort by name, as represented in Unicode codepoints.
     */
    public static final String BINARY_SORT_TMODEL = "uddi:uddi.org:sortorder:binarysort";
    /**
     * bindingSubset: this is used in the find_business API or the find_service
     * API and is used only in conjunction with a categoryBag argument. It
     * causes the component of the search that involves categorization to use
     * only the categoryBag elements from contained bindingTemplate elements
     * within the registered data and ignores any entries found in the
     * categoryBag which are not direct descendent elements of registered
     * businessEntity elements or businessService elements. The resulting
     * businessList or serviceList return those businesses or services that
     * matched based on this modified behavior, in conjunction with any other
     * search arguments provided. Additionally, in the case of the returned
     * businessList from a find_business, the contained serviceInfos elements
     * will only reflect summary data (in a serviceInfo element) for those
     * services (contained or referenced) that contained a binding that matched
     * on one of the supplied categoryBag arguments.
     */
    public static final String BINDING_SUBSET = "bindingSubset";
    /**
     * bindingSubset: this is used in the find_business API or the find_service
     * API and is used only in conjunction with a categoryBag argument. It
     * causes the component of the search that involves categorization to use
     * only the categoryBag elements from contained bindingTemplate elements
     * within the registered data and ignores any entries found in the
     * categoryBag which are not direct descendent elements of registered
     * businessEntity elements or businessService elements. The resulting
     * businessList or serviceList return those businesses or services that
     * matched based on this modified behavior, in conjunction with any other
     * search arguments provided. Additionally, in the case of the returned
     * businessList from a find_business, the contained serviceInfos elements
     * will only reflect summary data (in a serviceInfo element) for those
     * services (contained or referenced) that contained a binding that matched
     * on one of the supplied categoryBag arguments.
     */
    public static final String BINDING_SUBSET_TMODEL = "uddi:uddi.org:findqualifier:bindingsubset";
    /**
     * caseInsensitiveSort: signifies that the result set should be sorted
     * without regard to case. This overrides the default case sensitive sorting
     * behavior.
     */
    public static final String CASE_INSENSITIVE_SORT = "caseInsensitiveSort";
    /**
     * caseInsensitiveSort: signifies that the result set should be sorted
     * without regard to case. This overrides the default case sensitive sorting
     * behavior.
     */
    public static final String CASE_INSENSITIVE_SORT_TMODEL = "uddi:uddi.org:findqualifier:caseinsensitivesort";
    /**
     * caseInsensitiveMatch: signifies that the matching behavior for name,
     * keyValue and keyName (where applicable) should be performed without
     * regard to case.
     */
    public static final String CASE_INSENSITIVE_MATCH = "caseInsensitiveMatch";
    /**
     * caseInsensitiveMatch: signifies that the matching behavior for name,
     * keyValue and keyName (where applicable) should be performed without
     * regard to case.
     */
    public static final String CASE_INSENSITIVE_MATCH_TMODEL = "uddi:uddi.org:findqualifier:caseinsensitivematch";
    /**
     * caseSensitiveSort: signifies that the result set should be sorted with
     * regard to case. This is the default behavior.
     */
    public static final String CASE_SENSITIVE_SORT = "caseSensitiveSort";
    /**
     * caseSensitiveSort: signifies that the result set should be sorted with
     * regard to case. This is the default behavior.
     */
    public static final String CASE_SENSITIVE_SORT_TMODEL = "uddi:uddi.org:findqualifier:casesensitivesort";
    /**
     * caseSensitiveMatch: signifies that the matching behavior for name,
     * keyValue and keyName (where applicable) should be performed with regard
     * to case. This is the default behavior.
     */
    public static final String CASE_SENSITIVE_MATCH = "caseSensitiveMatch";
    /**
     * caseSensitiveMatch: signifies that the matching behavior for name,
     * keyValue and keyName (where applicable) should be performed with regard
     * to case. This is the default behavior.
     */
    public static final String CASE_SENSITIVE_MATCH_TMODEL = "uddi:uddi.org:findqualifier:casesensitivematch";
    /**
     * combineCategoryBags: this may only be used in the find_business and
     * find_service calls. In the case of find_business, this qualifier makes
     * the categoryBag entries for the full businessEntity element behave as
     * though all categoryBag elements found at the businessEntity level and in
     * all contained or referenced businessService elements and bindingTemplate
     * elements were combined. Searching for a category will yield a positive
     * match on a registered business if any of the categoryBag elements
     * contained within the full businessEntity element (including the
     * categoryBag elements within contained or referenced businessService
     * elements or bindingTemplate elements) contains the filter criteria. In
     * the case of find_service, this qualifier makes the categoryBag entries
     * for the full businessService element behave as though all categoryBag
     * elements found at the businessService level and in all contained or
     * referenced elements in the bindingTemplate elements were combined.
     * Searching for a category will yield a positive match on a registered
     * service if any of the categoryBag elements contained within the full
     * businessService element (including the categoryBag elements within
     * contained or referenced bindingTemplate elements) contains the filter
     * criteria. This find qualifier does not cause the keyedReferences in
     * categoryBags to be combined with the keyedReferences in
     * keyedReferenceGroups in categoryBags when performing the comparison. The
     * keyedReferences are combined with each other, and the
     * keyedReferenceGroups are combined with each other.
     */
    public static final String COMBINE_CATEGORY_BAGS = "combineCategoryBags";
    /**
     * combineCategoryBags: this may only be used in the find_business and
     * find_service calls. In the case of find_business, this qualifier makes
     * the categoryBag entries for the full businessEntity element behave as
     * though all categoryBag elements found at the businessEntity level and in
     * all contained or referenced businessService elements and bindingTemplate
     * elements were combined. Searching for a category will yield a positive
     * match on a registered business if any of the categoryBag elements
     * contained within the full businessEntity element (including the
     * categoryBag elements within contained or referenced businessService
     * elements or bindingTemplate elements) contains the filter criteria. In
     * the case of find_service, this qualifier makes the categoryBag entries
     * for the full businessService element behave as though all categoryBag
     * elements found at the businessService level and in all contained or
     * referenced elements in the bindingTemplate elements were combined.
     * Searching for a category will yield a positive match on a registered
     * service if any of the categoryBag elements contained within the full
     * businessService element (including the categoryBag elements within
     * contained or referenced bindingTemplate elements) contains the filter
     * criteria. This find qualifier does not cause the keyedReferences in
     * categoryBags to be combined with the keyedReferences in
     * keyedReferenceGroups in categoryBags when performing the comparison. The
     * keyedReferences are combined with each other, and the
     * keyedReferenceGroups are combined with each other.
     */
    public static final String COMBINE_CATEGORY_BAGS_TMODEL = "uddi:uddi.org:findqualifier:combinecategorybags";
    /**
     * diacriticInsensitiveMatch: signifies that matching behavior for name,
     * keyValue and keyName (where applicable) should be performed without
     * regard to diacritics. Support for this findQualifier by nodes is
     * OPTIONAL.
     */
    public static final String DIACRITIC_INSENSITIVE_MATCH = "diacriticInsensitiveMatch";
    /**
     * diacriticInsensitiveMatch: signifies that matching behavior for name,
     * keyValue and keyName (where applicable) should be performed without
     * regard to diacritics. Support for this findQualifier by nodes is
     * OPTIONAL.
     */
    public static final String DIACRITIC_INSENSITIVE_MATCH_TMODEL = "uddi:uddi.org:findqualifier:diacriticsinsensitivematch";
    /**
     * diacriticSensitiveMatch: signifies that the matching behavior for name,
     * keyValue and keyName (where applicable) should be performed with regard
     * to diacritics. This is the default behavior.
     */
    public static final String DIACRITIC_SENSITIVE_MATCH = "diacriticSensitiveMatch";
    /**
     * diacriticSensitiveMatch: signifies that the matching behavior for name,
     * keyValue and keyName (where applicable) should be performed with regard
     * to diacritics. This is the default behavior.
     */
    public static final String DIACRITIC_SENSITIVE_MATCH_TMODEL = "uddi:uddi.org:findqualifier:diacriticssensitivematch";
    /**
     * exactMatch: signifies that only entries with names, keyValues and
     * keyNames (where applicable) that exactly match the name argument passed
     * in, after normalization, will be returned. This qualifier is sensitive to
     * case and diacritics where applicable. This qualifier represents the
     * default behavior.
     */
    public static final String EXACT_MATCH = "exactMatch";
    /**
     * exactMatch: signifies that only entries with names, keyValues and
     * keyNames (where applicable) that exactly match the name argument passed
     * in, after normalization, will be returned. This qualifier is sensitive to
     * case and diacritics where applicable. This qualifier represents the
     * default behavior.
     */
    public static final String EXACT_MATCH_TMODEL = "uddi:uddi.org:findqualifier:exactmatch";
    /**
     * signaturePresent: this is used with any find_xx API to restrict the
     * result set to entities which either contain an XML Digital Signature
     * element, or are contained in an entity which contains one. The Signature
     * element is retrieved using a get_xx API call and SHOULD be verified by
     * the client. A UDDI node may or may not verify the signature and therefore
     * use of this find qualifier, or the presence of a Signature element SHOULD
     * only be for the refinement of the result set from the find_xx API and
     * SHOULD not be used as a verification mechanism by UDDI clients.
     */
    public static final String SIGNATURE_PRESENT = "signaturePresent";
    /**
     * signaturePresent: this is used with any find_xx API to restrict the
     * result set to entities which either contain an XML Digital Signature
     * element, or are contained in an entity which contains one. The Signature
     * element is retrieved using a get_xx API call and SHOULD be verified by
     * the client. A UDDI node may or may not verify the signature and therefore
     * use of this find qualifier, or the presence of a Signature element SHOULD
     * only be for the refinement of the result set from the find_xx API and
     * SHOULD not be used as a verification mechanism by UDDI clients.
     */
    public static final String SIGNATURE_PRESENT_TMODEL = "uddi:uddi.org:findqualifier:signaturepresent";
    /**
     * orAllKeys: this changes the behavior for tModelBag and categoryBag to OR
     * the keys within a bag, rather than to AND them. Using this findQualifier
     * with both a categoryBag and a tModelBag, will cause all of the keys in
     * BOTH the categoryBag and the tModelBag to be OR’d together rather than
     * AND’d. It is not possible to OR the categories and retain the default AND
     * behavior of the tModels. The behavior of keyedReferenceGroups in a
     * categoryBag is analogous to that of individual keyedReferences, that is,
     * the complete categoryBag is changed to OR the keys.
     */
    public static final String OR_ALL_KEYS = "orAllKeys";
    /**
     * orAllKeys: this changes the behavior for tModelBag and categoryBag to OR
     * the keys within a bag, rather than to AND them. Using this findQualifier
     * with both a categoryBag and a tModelBag, will cause all of the keys in
     * BOTH the categoryBag and the tModelBag to be OR’d together rather than
     * AND’d. It is not possible to OR the categories and retain the default AND
     * behavior of the tModels. The behavior of keyedReferenceGroups in a
     * categoryBag is analogous to that of individual keyedReferences, that is,
     * the complete categoryBag is changed to OR the keys.
     */
    public static final String OR_ALL_KEYS_TMODEL = "uddi:uddi.org:findqualifier:orallkeys";
    /**
     * orLikeKeys: when a bag container (i.e. categoryBag or identifierBag)
     * contains multiple keyedReference elements, any keyedReference filters
     * that come from the same namespace (e.g. have the same tModelKey value)
     * are OR’d together rather than AND’d. For example "find any of these four
     * values from this namespace, and any of these two values from this
     * namespace". The behavior of keyedReferenceGroups is analogous to that of
     * keyedReferences, that is, keyedReferenceGroups that have the same
     * tModelKey value are OR’d together rather than AND’d.
     */
    public static final String OR_LIKE_KEYS = "orLikeKeys";
    /**
     * orLikeKeys: when a bag container (i.e. categoryBag or identifierBag)
     * contains multiple keyedReference elements, any keyedReference filters
     * that come from the same namespace (e.g. have the same tModelKey value)
     * are OR’d together rather than AND’d. For example "find any of these four
     * values from this namespace, and any of these two values from this
     * namespace". The behavior of keyedReferenceGroups is analogous to that of
     * keyedReferences, that is, keyedReferenceGroups that have the same
     * tModelKey value are OR’d together rather than AND’d.
     */
    public static final String OR_LIKE_KEYS_TMODEL = "uddi:uddi.org:findqualifier:orlikekeys";
    /**
     * serviceSubset: this is used only with the find_business API and is used
     * only in conjunction with a categoryBag argument. It causes the component
     * of the search that involves categorization to use only the categoryBag
     * elements from contained or referenced businessService elements within the
     * registered data and ignores any entries found in the categoryBag which
     * are not direct descendent elements of registered businessEntity elements.
     * The resulting businessList structure contains those businesses that
     * matched based on this modified behavior, in conjunction with any other
     * search arguments provided. Additionally, the contained serviceInfos
     * elements will only reflect summary data (in a serviceInfo element) for
     * those services (contained or referenced) that matched on one of the
     * supplied categoryBag arguments.
     */
    public static final String SERVICE_SUBSET = "serviceSubset";
    /**
     * serviceSubset: this is used only with the find_business API and is used
     * only in conjunction with a categoryBag argument. It causes the component
     * of the search that involves categorization to use only the categoryBag
     * elements from contained or referenced businessService elements within the
     * registered data and ignores any entries found in the categoryBag which
     * are not direct descendent elements of registered businessEntity elements.
     * The resulting businessList structure contains those businesses that
     * matched based on this modified behavior, in conjunction with any other
     * search arguments provided. Additionally, the contained serviceInfos
     * elements will only reflect summary data (in a serviceInfo element) for
     * those services (contained or referenced) that matched on one of the
     * supplied categoryBag arguments.
     */
    public static final String SERVICE_SUBSET_TMODEL = "uddi:uddi.org:findqualifier:servicesubset";
    /**
     * sortByNameAsc: causes the result set returned by a find_xx or get_xx
     * inquiry APIs to be sorted on the name field in ascending order. This sort
     * is applied prior to any truncation of result sets. It is only applicable
     * to queries that return a name element in the top-most detail level of the
     * result set and if no conflicting sort qualifier is specified, this is the
     * default sorting direction. This findQualifier takes precedence over
     * sortByDateAsc and sortByDateDesc qualifiers, but if a sortByDateXxx
     * findQualifier is used without a sortByNameXxx qualifier, sorting is
     * performed based on date with or without regard to name.
     */
    public static final String SORT_BY_NAME_ASC = "sortByNameAsc";
    /**
     * sortByNameAsc: causes the result set returned by a find_xx or get_xx
     * inquiry APIs to be sorted on the name field in ascending order. This sort
     * is applied prior to any truncation of result sets. It is only applicable
     * to queries that return a name element in the top-most detail level of the
     * result set and if no conflicting sort qualifier is specified, this is the
     * default sorting direction. This findQualifier takes precedence over
     * sortByDateAsc and sortByDateDesc qualifiers, but if a sortByDateXxx
     * findQualifier is used without a sortByNameXxx qualifier, sorting is
     * performed based on date with or without regard to name.
     */
    public static final String SORT_BY_NAME_ASC_TMODEL = "uddi:uddi.org:findqualifier:sortbynameasc";
    /**
     * sortByNameDesc: causes the result set returned by a find_xx or get_xx
     * inquiry call to be sorted on the name field in descending order. This
     * sort is applied prior to any truncation of result sets. It is only
     * applicable to queries that return a name element in the top-most detail
     * level of the result set. This is the reverse of the default sorting
     * direction. This findQualifier takes precedence over sortByDateAsc and
     * sortByDateDesc qualifiers but if a sortByDateXxx findQualifier is used
     * without a sortByNameXxx qualifier, sorting is performed based on date
     * with or without regard to name.
     */
    public static final String SORT_BY_NAME_DESC = "sortByNameDesc";
    /**
     * sortByNameDesc: causes the result set returned by a find_xx or get_xx
     * inquiry call to be sorted on the name field in descending order. This
     * sort is applied prior to any truncation of result sets. It is only
     * applicable to queries that return a name element in the top-most detail
     * level of the result set. This is the reverse of the default sorting
     * direction. This findQualifier takes precedence over sortByDateAsc and
     * sortByDateDesc qualifiers but if a sortByDateXxx findQualifier is used
     * without a sortByNameXxx qualifier, sorting is performed based on date
     * with or without regard to name.
     */
    public static final String SORT_BY_NAME_DESC_TMODEL = "uddi:uddi.org:findqualifier:sortbynamedesc";
    /**
     * sortByDateAsc: causes the result set returned by a find_xx or get_xx
     * inquiry call to be sorted based on the most recent date when each entity,
     * or any entities they contain, were last updated, in ascending
     * chronological order (oldest are returned first). When names are included
     * in the result set returned, this find qualifier may also be used in
     * conjunction with either the sortByNameAsc or sortByNameDesc
     * findQualifiers. When so combined, the date-based sort is secondary to the
     * name-based sort (results are sorted within name by date, oldest to
     * newest).
     */
    public static final String SORT_BY_DATE_ASC = "sortByDateAsc";
    /**
     * sortByDateAsc: causes the result set returned by a find_xx or get_xx
     * inquiry call to be sorted based on the most recent date when each entity,
     * or any entities they contain, were last updated, in ascending
     * chronological order (oldest are returned first). When names are included
     * in the result set returned, this find qualifier may also be used in
     * conjunction with either the sortByNameAsc or sortByNameDesc
     * findQualifiers. When so combined, the date-based sort is secondary to the
     * name-based sort (results are sorted within name by date, oldest to
     * newest).
     */
    public static final String SORT_BY_DATE_ASC_TMODEL = "uddi:uddi.org:findqualifier:sortbydateasc";
    /**
     * sortByDateDesc: causes the result set returned by a find_xx or get_xx
     * inquiry call to be sorted based on the most recent date when each entity,
     * or any entities they contain, were last updated, in descending
     * chronological order (most recently changed are returned first. When names
     * are included in the result set returned, this find qualifier may also be
     * used in conjunction with either the sortByNameAsc or sortByNameDesc find
     * qualifiers. When so combined, the date-based sort is secondary to the
     * name-based sort (results are sorted within name by date, newest to
     * oldest).
     */
    public static final String SORT_BY_DATE_DESC = "sortByDateDesc";
    /**
     * sortByDateDesc: causes the result set returned by a find_xx or get_xx
     * inquiry call to be sorted based on the most recent date when each entity,
     * or any entities they contain, were last updated, in descending
     * chronological order (most recently changed are returned first. When names
     * are included in the result set returned, this find qualifier may also be
     * used in conjunction with either the sortByNameAsc or sortByNameDesc find
     * qualifiers. When so combined, the date-based sort is secondary to the
     * name-based sort (results are sorted within name by date, newest to
     * oldest).
     */
    public static final String SORT_BY_DATE_DESC_TMODEL = "uddi:uddi.org:findqualifier:sortbydatedesc";
    /**
     * suppressProjectedServices: signifies that service projections MUST NOT be
     * returned by the find_service or find_business APIs with which this
     * findQualifier is associated. This findQualifier is automatically enabled
     * by default whenever find_service is used without a businessKey.
     */
    public static final String SUPPRESS_PROJECTED_SERVICES = "suppressProjectedServices";
    /**
     * suppressProjectedServices: signifies that service projections MUST NOT be
     * returned by the find_service or find_business APIs with which this
     * findQualifier is associated. This findQualifier is automatically enabled
     * by default whenever find_service is used without a businessKey.
     */
    public static final String SUPPRESS_PROJECTED_SERVICES_TMODEL = "uddi:uddi.org:findqualifier:suppressprojectedservices";
    /**
     * UTS-10: this is used to cause sorting of results based on the Unicode
     * Collation Algorithm on elements normalized according to Unicode
     * Normalization Form C. When this qualifier is referenced, a sort is
     * performed according to the Unicode Collation Element Table in conjunction
     * with the Unicode Collation Algorithm on the name field, normalized using
     * Unicode Normalization Form C. Support of this findQualifier by nodes is
     * OPTIONAL.
     */
    public static final String UTS_10 = "UTS-10";
    /**
     * UTS-10: this is used to cause sorting of results based on the Unicode
     * Collation Algorithm on elements normalized according to Unicode
     * Normalization Form C. When this qualifier is referenced, a sort is
     * performed according to the Unicode Collation Element Table in conjunction
     * with the Unicode Collation Algorithm on the name field, normalized using
     * Unicode Normalization Form C. Support of this findQualifier by nodes is
     * OPTIONAL.
     */
    public static final String UTS_10_TMODEL = "uddi:uddi.org:sortorder:uts-10";
    /**
     * This is the Wild card field used by UDDI, represents any number of
     * characters Wildcards, when they are allowed, may occur at any position in
     * the string of characters that constitutes the argument value and may
     * occur more than once. Wildcards are denoted with a percent sign (%) to
     * indicate any value for any number of characters and an underscore (_) to
     * indicate any value for a single character. The backslash character (\) is
     * used as an escape character for the percent sign, underscore and
     * backslash characters. Use of the "exactMatch" findQualifier will cause
     * wildcard characters to be interpreted literally, and as such should not
     * also be combined with the escape character.
     */
    public static String WILDCARD = "%";
    /**
     * Presents any SINGLE character<br> Wildcards, when they are allowed, may
     * occur at any position in the string of characters that constitutes the
     * argument value and may occur more than once. Wildcards are denoted with a
     * percent sign (%) to indicate any value for any number of characters and
     * an underscore (_) to indicate any value for a single character. The
     * backslash character (\) is used as an escape character for the percent
     * sign, underscore and backslash characters. Use of the "exactMatch"
     * findQualifier will cause wildcard characters to be interpreted literally,
     * and as such should not also be combined with the escape character.
     */
    public static String WILDCARD_CHAR = "_";
    /**
     * unchecked: Marking a tModel with this categorization asserts that it
     * represents a value set or category group system whose use, through
     * keyedReferences, is not checked.
     */
    public static String TMODEL_GENERAL_KEYWORDS = "uddi:uddi.org:categorization:general_keywords";
    /**
     * An unchecked value set is one that allows unrestricted references to its
     * values. A UDDI registry is REQUIRED to have a policy to differentiate
     * between unchecked value sets and checked value sets. UDDI registries MUST
     * allow unchecked value sets to be referred to in keyedReferences. tModels
     * that represent unchecked value sets SHOULD be categorized with the
     * unchecked value from the uddi-org:types category system.
     */
    public static String UNCHECKED = "uddi-org:types:unchecked";
    /**
     * checked: Marking a tModel with this categorization asserts that it
     * represents a value set or category group system whose use, through
     * keyedReferences, may be checked. Registry, and possibly node policy
     * determines when and how a checked value set is supported.
     */
    public static String CHECKED = "uddi-org:types:checked";
    /**
     * uncacheable: Marking a tModel with this categorization asserts that it
     * represents a checked value set or category group system whose values must
     * not be cached for validation. The validation algorithm for a supported
     * uncacheable checked value set must be specified and associated with the
     * tModel marked with this categorization and may consider contextual
     * criteria involving the entity associated with the reference.
     */
    public static String UNCACHEABLE = "uddi-org:types:uncacheable";
    /**
     * Basic types of business relationships
     */
    public static String RELATIONSHIPS = "uddi:uddi.org:relationships";
    /**
     * "uddi:uddi.org:keygenerator" keyGenerator: Marking a tModel with this
     * categorization designates it as one whose tModelKey identifies a key
     * generator partition that can be used by its owner to derive and assign
     * other entity keys. This categorization is reserved for key generator
     * tModels. Any attempt to use this categorization for something other than
     * a key generator tModel will fail with E_valueNotAllowed returned.
     */
    public static String KEY_GENERATOR_TMODEL = "uddi:uddi.org:keygenerator";
    /**
     * "uddi.org:keygenerator" keyGenerator: Marking a tModel with this
     * categorization designates it as one whose tModelKey identifies a key
     * generator partition that can be used by its owner to derive and assign
     * other entity keys. This categorization is reserved for key generator
     * tModels. Any attempt to use this categorization for something other than
     * a key generator tModel will fail with E_valueNotAllowed returned.
     */
    public static String KEY_GENERATOR = "uddi.org:keygenerator";
    /**
     * "keyGenerator" keyGenerator: Marking a tModel with this categorization
     * designates it as one whose tModelKey identifies a key generator partition
     * that can be used by its owner to derive and assign other entity keys.
     * This categorization is reserved for key generator tModels. Any attempt to
     * use this categorization for something other than a key generator tModel
     * will fail with E_valueNotAllowed returned.
     */
    public static String KEY_GENERATOR_VALUE = "keyGenerator";
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
