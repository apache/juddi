/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

package org.apache.scout.registry;

/**
 * Deals with the FindQualifier in both JAXR and UDDI4J
 * @author Anil Saldhana  <anil@apache.org>
 */
public class FindQualifierImpl 
extends org.uddi4j.util.FindQualifier
implements javax.xml.registry.FindQualifier {
     public static final String  AND_ALL_KEYS = "AND_ALL_KEYS";
           
     public static  final String CASE_SENSITIVE_MATCH = "CASE_SENSITIVE_MATCH";
           
     public static  final String COMBINE_CLASSIFICATIONS="COMBINE_CLASSIFICATIONS";
           
     public static  final String EXACT_NAME_MATCH="EXACT_NAME_MATCH";
           
     public static  final String OR_ALL_KEYS = "OR_ALL_KEYS";
           
     public static  final String OR_LIKE_KEYS="OR_LIKE_KEYS";
           
     public static  final String SERVICE_SUBSET="SERVICE_SUBSET";
           
     public static  final String SORT_BY_DATE_ASC="SORT_BY_DATE_ASC";
           
     public static  final String SORT_BY_DATE_DESC="SORT_BY_DATE_DESC";
           
     public static  final String SORT_BY_NAME_ASC="SORT_BY_NAME_ASC";
           
     public static  final String SORT_BY_NAME_DESC="SORT_BY_NAME_DESC";
           
     public static  final String SOUNDEX="SOUNDEX";
           
    
    /** Creates a new instance of FindQualifierImpl */
    public FindQualifierImpl() {
     
    }
    
    public String getUDDI4JFindQualifier(String str) {
      if( str != null && str.equals(AND_ALL_KEYS) ) 
          return super.andAllKeys;
      if( str != null && str.equals(CASE_SENSITIVE_MATCH) ) 
          return super.caseSensitiveMatch  ;
           
      if( str != null && str.equals(COMBINE_CLASSIFICATIONS) ) 
          return super.combineCategoryBags ;
           
       if( str != null && str.equals(EXACT_NAME_MATCH) )
         return super.exactNameMatch ;
           
      if( str != null && str.equals(OR_ALL_KEYS) ) return super.orAllKeys ;
           
      if( str != null && str.equals(OR_LIKE_KEYS) ) return super.orLikeKeys;
           
      if( str != null && str.equals(SERVICE_SUBSET) ) 
          return super.serviceSubset ;
           
      if( str != null && str.equals(SORT_BY_DATE_ASC) ) 
          return super.sortByDateAsc ;
           
      if( str != null && str.equals(SORT_BY_DATE_DESC) ) 
          return super.sortByDateDesc ;
           
      if( str != null && str.equals(SORT_BY_NAME_ASC) ) 
          return super.sortByNameAsc ;
           
      if( str != null && str.equals(SORT_BY_NAME_DESC) ) 
          return super.sortByNameDesc ;
           
      //if( str != null && str.equals(SOUNDEX) ) return super.soundex;
       if( str != null && str.equals(SOUNDEX) ) return "";
      
      return ""; //Worst Case
    }
    
}//end class
