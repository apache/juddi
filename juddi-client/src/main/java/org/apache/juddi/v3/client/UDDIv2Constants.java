/*
 * Copyright 2014 The Apache Software Foundation.
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
 */
package org.apache.juddi.v3.client;

/**
 * UDDI v2 Constants, such as supported find qualifiers.<br><br> Taken from
 * <a
 * href="http://uddi.org/pubs/ProgrammersAPI-V2.04-Published-20020719.htm#_Toc25137775">http://uddi.org/pubs/ProgrammersAPI-V2.04-Published-20020719.htm#_Toc25137775</a>
 *
 * @author Alex O'Ree
 */
public class UDDIv2Constants {

     /**
      * signifies that lexical-order – i.e., leftmost in left-to-right languages
      * – name match behavior should be overridden. When this behavior is
      * specified, only entries that exactly match the entry passed in the name
      * argument will be returned. Applies to: find_business, find_service, and
      * find_tModel.
      */
     public static final String exactNameMatch = "exactNameMatch";
     /**
      * signifies that the default case-insensitive behavior of a name match
      * should be overridden. When this behavior is specified, case is relevant
      * in the search results and only entries that match the case of the value
      * passed in the name argument will be returned. Applies to: find_business,
      * find_service, and find_tModel.
      */
     public static final String caseSensitiveMatch = "caseSensitiveMatch";
     /**
      * signifies that the result returned by a find_xx inquiry call should be
      * sorted on the name field in ascending alphabetic sort order. When there
      * is more than one name field, the sort uses the first of them. This sort
      * is applied prior to any truncation of result sets. Only applicable on
      * queries that return a name element in the topmost detail level of the
      * result set. If no conflicting sort qualifier is specified, this is the
      * default sort order for inquiries that return name values at this topmost
      * detail level. Applies to: find_business, find_relatedBusinesses,
      * find_service, and find_tModel.
      */
     public static final String sortByNameAsc = "sortByNameAsc";
     /**
      * signifies that the result returned by a find_xx inquiry call should be
      * sorted on the name field in descending alphabetic sort order. When there
      * is more than one name field, the sort uses the first of them. This sort
      * is applied prior to any truncation of result sets. Only applicable on
      * queries that return a name element in the topmost detail level of the
      * result set. This is the reverse of the default sort order for this kind
      * of result. Applies to: find_business, find_relatedBusinesses,
      * find_service, and find_tModel.
      */
     public static final String sortByNameDesc = "sortByNameDesc";
     /**
      * signifies that the result returned by a find_xx inquiry call should be
      * sorted based on the date last updated in ascending chronological sort
      * order (earliest returns first). If no conflicting sort qualifier is
      * specified, this is the default sort order for all result sets. Applies
      * to: find_binding, find_business, find_relatedBusinesses, find_service,
      * and find_tModel.
      */
     public static final String sortByDateAsc = "sortByDateAsc";
     /**
      * signifies that the result returned by a find_xx inquiry call should be
      * sorted based on the date last updated in descending chronological sort
      * order (most recent change returns first). Sort qualifiers involving date
      * are secondary in precedence to the sortByName qualifiers. This causes
      * sortByName elements to be sorted within name by date, newest to oldest.
      * Applies to: find_binding, find_business, find_relatedBusinesses,
      * find_service, and find_tModel.
      */
     public static final String sortByDateDesc = "sortByDateDesc";
     /**
      * when a bag container contains multiple keyedReference elements (i.e.,
      * categoryBag or identifierBag), any keyedReference filters that come from
      * the same namespace (e.g. have the same tModelKey value) are OR’d
      * together rather than AND’d. This allows one to say “any of these four
      * values from this namespace, and any of these two values from this
      * namespace”. Applies to: find_business, find_service, and find_tModel.
      */
     public static final String orLikeKeys = "orLikeKeys";
     /**
      * this changes the behavior for tModelBag and categoryBag to OR keys
      * rather than AND them. This qualifier negates any AND treatment as well
      * as the effect of orLikeKeys. Applies to: find_binding, find_business,
      * find_service, and find_tModel.
      */
     public static final String orAllKeys = "orAllKeys";
     /**
      * this is only used in the find_business message. This qualifier makes the
      * categoryBag entries for the full businessEntity element behave as though
      * all categoryBag elements found at the businessEntity level and in all
      * contained or referenced businessService elements were combined.
      * Searching for a category will yield a positive match on a registered
      * business if any of the categoryBag elements contained within the full
      * businessEntity element (including the categoryBag elements within
      * contained or referenced businessService elements) contains the filter
      * criteria. Applies to: find_business.
      */
     public static final String combineCategoryBags = "combineCategoryBags";
     /**
      * this is used only in the find_business message. This qualifier is used
      * in only in conjunction with a passed categoryBag argument and causes the
      * component of the search that involves categorization to use only the
      * categoryBag elements from contained or referenced businessService
      * elements within the registered data, and ignores any entries found in
      * the categoryBag direct descendent element of registered businessEntity
      * elements. The resulting businessList message will return those
      * businesses that matched based on this modified behavior, in conjunction
      * with any other search arguments provided. Additionally, the contained
      * serviceInfos elements will only reflect summary data (in a serviceInfo
      * element) for those services (contained or referenced) that matched on
      * one of the supplied categoryBag arguments. Applies to: find_business.
      */
     public static final String serviceSubset = "serviceSubset";
     /**
      * this changes the behavior for identifierBag to AND keys rather than OR
      * them. Applies to: find_business and find_tModel.
      */
     public static final String andAllKeys = "andAllKeys";
}
