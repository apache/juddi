/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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