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
package org.apache.juddi.i18n;

import java.util.ListResourceBundle;

import org.apache.juddi.datatype.response.Result;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class RegistryResources extends ListResourceBundle
{
  static final Object[][] CONTENTS =
  {
    {"30000", new Result(30000,"E_assertionNotFound",       "A particular publisher assertion cannot be identified in a save or delete operation.")},
    {"10110", new Result(10110,"E_authTokenExpired",        "Authentication token information has timed out.")},
    {"10120", new Result(10120,"E_authTokenRequired",       "An invalid authentication token was passed to an API call that requires authentication.")},
    {"10160", new Result(10160,"E_accountLimitExceeded",    "Authentication token information has timed out.")},
    {"10400", new Result(10400,"E_busy",                    "The request cannot be processed at the current time.")},
    {"20100", new Result(20100,"E_categorizationNotAllowed","The data provided does not conform to the restrictions placed on the category used.")},
    {"10500", new Result(10500,"E_fatalError",              "A serious technical exception has occurred while processing the request.")},
    {"10210", new Result(10210,"E_invalidKeyPassed",        "The uuid_key value passed did not match with any known key values.")},
    {"20230", new Result(20230,"E_invalidProjection",       "An attempt was made to save a business entity containing a service projection that does not match the business service being projected.")},
    {"20000", new Result(20000,"E_invalidCategory",         "The given keyValue did not correspond to a category within the taxonomy identified by the tModelKey.")},
    {"30100", new Result(30100,"E_invalidCompletionStatus", "One of the assertion status values passed is unrecognized.")},
    {"10220", new Result(10220,"E_invalidURLPassed",        "An exception occurred during processing of a save server involving accessing data from a remote URL.")},
    {"20200", new Result(20200,"E_invalidValue",            "A value that was passed in a keyValue attribute did not pass validation. This applies to checked categorizations, identifiers and other validated code lists.")},
    {"10310", new Result(10310,"E_keyRetired",              "A uuid_key value passed has been removed from the registry.")},
    {"10060", new Result(10060,"E_languageError",           "An exception was detected while processing elements that were annotated with xml:lang qualifiers.")},
    {"30110", new Result(30110,"E_messageTooLarge",         "The message is too large.")},
    {"10020", new Result(10020,"E_nameTooLong",             "The partial name value passed exceeds the maximum name length designated by the policy of an implementation or Operator Site.")},
    {"10130", new Result(10130,"E_operatorMismatch",        "An attempt was made to use the publishing API to change data that is mastered at another Operator Site.")},
    {"30220", new Result(30220,"E_publisherCancelled",      "The target publisher cancelled the custody transfer.")},
    {"30210", new Result(30210,"E_requestDenied",           "A custody transfer request has been refused.")},
    {"30230", new Result(30230,"E_secretUnknown",           "The target publisher was unable to match the shared secret and the five (5) attempt limit was exhausted. The target publisher automatically cancelled the transfer operation.")},
    {"10030", new Result(10030,"E_tooManyOptions",          "Incompatible arguments were passed.")},
    {"30200", new Result(30200,"E_transferAborted",         "Signifies that a custody transfer request will not succeed.")},
    {"10040", new Result(10040,"E_unrecognizedVersion",     "The value of the generic attribute passed is unsupported by the Operator Instance being queried.")},
    {"10150", new Result(10150,"E_unknownUser",             "The user ID and password pair passed in a get_authToken message is not known to the Operator Site or is not valid.")},
    {"10050", new Result(10050,"E_unsupported",             "The implementor does not support a feature or API.")},
    {"10140", new Result(10140,"E_userMismatch",            "An attempt was made to use the publishing API to change data that is controlled by another party.")},
    {"20210", new Result(20210,"E_valueNotAllowed",         "A value did not pass validation because of contextual issues. The value may be valid in some contexts, but not in the contextused.")},
    {"20220", new Result(20220,"E_unvalidatable",           "")},
    {"20240", new Result(20240,"E_requestTimeout",          "The request could not be carried out because a needed web service, such as validate_values, did not respond in a reasonable amount of time.")}
  };

  /**
   * @see java.util.ListResourceBundle#getContents()
   */
  protected Object[][] getContents()
  {
    return CONTENTS;
  }
}