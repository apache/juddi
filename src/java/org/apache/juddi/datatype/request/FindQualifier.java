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
package org.apache.juddi.datatype.request;

import org.apache.juddi.datatype.RegistryObject;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class FindQualifier implements RegistryObject
{
  /**
   * Constant ...
   */
  public static final String EXACT_NAME_MATCH = "exactNameMatch";


  /**
   * Constant ...
   */
  public static final String CASE_SENSITIVE_MATCH = "caseSensitiveMatch";

  /**
   * Constant ...
   */
  public static final String OR_ALL_KEYS = "orAllKeys";

  /**
   * Constant ...
   */
  public static final String OR_LIKE_KEYS = "orLikeKeys";

  /**
   * Constant ...
   */
  public static final String AND_ALL_KEYS = "andAllKeys";

  /**
   * Constant ...
   */
  public static final String SORT_BY_NAME_ASC = "sortByNameAsc";

  /**
   * Constant ...
   */
  public static final String SORT_BY_NAME_DESC = "sortByNameDesc";

  /**
   * Constant ...
   */
  public static final String SORT_BY_DATE_ASC = "sortByDateAsc";

  /**
   * Constant ...
   */
  public static final String SORT_BY_DATE_DESC = "sortByDateDesc";

  /**
   * Constant ...
   */
  public static final String SERVICE_SUBSET = "serviceSubset";

  /**
   * Constant ...
   */
  public static final String COMBINE_CATEGORY_BAGS = "combineCategoryBags";

  String value;

  /**
   *
   */
  public FindQualifier()
  {
  }

  /**
   *
   */
  public FindQualifier(String newValue)
  {
    setValue(newValue);
  }

  /**
   *
   */
  public void setValue (String newValue)
  {
    this.value = newValue;
  }

  /**
   *
   */
  public String getValue()
  {
    return this.value;
  }
}