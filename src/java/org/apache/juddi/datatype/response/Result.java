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
package org.apache.juddi.datatype.response;

import org.apache.juddi.datatype.RegistryObject;

/**
 * Used in response DispositionReport.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class Result implements RegistryObject
{
  // Signifies that the authentication token information has timed out.
  public static final int E_ASSERTION_NOT_FOUND = 30000;
  public static final String E_ASSERTION_NOT_FOUND_CODE = "E_assertionNotFound";
  public static final String E_ASSERTION_NOT_FOUND_MSG = "A particular publisher " +
    "assertion cannot be identified in a save or delete operation.";

  // Signifies that the authentication token information has timed out.
  public static final int E_AUTH_TOKEN_EXPIRED = 10110;
  public static final String E_AUTH_TOKEN_EXPIRED_CODE = "E_authTokenExpired";
  public static final String E_AUTH_TOKEN_EXPIRED_MSG = "Authentication token " +
    "information has timed out.";

  // Signifies that an invalid authentication token was passed to an API call
  // that requires authentication.
  public static final int E_AUTH_TOKEN_REQUIRED = 10120;
  public static final String E_AUTH_TOKEN_REQUIRED_CODE = "E_authTokenRequired";
  public static final String E_AUTH_TOKEN_REQUIRED_MSG = "An invalid authentication " +
    "token was passed to an API call that requires authentication.";

  // Signifies that a save request exceeded the quantity limits for a given
  // structure type. See "Structure Limits" in Appendix D for details.
  public static final int E_ACCOUNT_LIMIT_EXCEEDED = 10160;
  public static final String E_ACCOUNT_LIMIT_EXCEEDED_CODE = "E_accountLimitExceeded";
  public static final String E_ACCOUNT_LIMIT_EXCEEDED_MSG = "Authentication token " +
    "information has timed out.";

  // Signifies that the request cannot be processed at the current time.
  public static final int E_BUSY = 10400;
  public static final String E_BUSY_CODE = "E_busy";
  public static final String E_BUSY_MSG = "The request cannot be processed at " +
    "the current time.";

  // Restrictions have been placed by the on the types of information that can
  // categorized within a specific taxonomy. The data provided does not conform
  // to the restrictions placed on the category used. Used with cateborization
  // only.
  public static final int E_CATEGORIZATION_NOT_ALLOWED = 20100;
  public static final String E_CATEGORIZATION_NOT_ALLOWED_CODE = "E_categorizationNotAllowed";
  public static final String E_CATEGORIZATION_NOT_ALLOWED_MSG = "The data " +
    "provided does not conform to the restrictions placed on the category used.";

  // Signifies that a serious technical exception has occurred while processing the
  // request.
  public static final int E_FATAL_ERROR = 10500;
  public static final String E_FATAL_ERROR_CODE = "E_fatalError";
  public static final String E_FATAL_ERROR_MSG = "A serious technical exception " +
    "has occurred while processing the request.";

  // Signifies that the uuid_key value passed did not match with any known key
  // values. The details on the invalid key will be included in the
  // dispositionReport structure.
  public static final int E_INVALID_KEY_PASSED = 10210;
  public static final String E_INVALID_KEY_PASSED_CODE = "E_invalidKeyPassed";
  public static final String E_INVALID_KEY_PASSED_MSG = "The uuid_key value " +
    "passed did not match with any known key values.";

  // Signifies that the authentication token information has timed out.
  public static final int E_INVALID_PROJECTION = 20230;
  public static final String E_INVALID_PROJECTION_CODE = "E_invalidProjection";
  public static final String E_INVALID_PROJECTION_MSG = "An attempt was made " +
    "to save a business entity containing a service projection that does not " +
    "match the business service being projected.";

  // Signifies that the given keyValue did not correspond to a category within
  // the taxonomy identified by the tModelKey. Used with categorization only.
  public static final int E_INVALID_CATEGORY = 20000;
  public static final String E_INVALID_CATEGORY_CODE = "E_invalidCategory";
  public static final String E_INVALID_CATEGORY_MSG = "The given keyValue did " +
    "not correspond to a category within the taxonomy identified by the tModelKey.";

  // Signifies that the authentication token information has timed out.
  // NOTE: The UDDI specification indicates that the errno value should be 30100
  // but this value conflicts with the E_MESSAGE_TOO_LARGE errno. The value for
  // E_MESSAGE_TOO_LARGE errno has been changed to 30101. This is the same approach
  // Idoox (now Systinet) took.
  public static final int E_INVALID_COMPLETION_STATUS = 30100;
  public static final String E_INVALID_COMPLETION_STATUS_CODE = "E_invalidCompletionStatus";
  public static final String E_INVALID_COMPLETION_STATUS_MSG = "One of the " +
    "assertion status values passed is unrecognized.";

  // Signifies that an exception occurred during processing of a save server
  // involving accessing data from a remote URL. The details of the HTTP Get
  // report will be included in the dispositionReport structure.
  public static final int E_INVALID_URL_PASSED = 10220;
  public static final String E_INVALID_URL_PASSED_CODE = "E_invalidURLPassed";
  public static final String E_INVALID_URL_PASSED_MSG = "An exception occurred " +
    "during processing of a save server involving accessing data from a remote URL.";

  // Signifies that the authentication token information has timed out.
  public static final int E_INVALID_VALUE = 20200;
  public static final String E_INVALID_VALUE_CODE = "E_invalidValue";
  public static final String E_INVALID_VALUE_MSG = "A value that was passed " +
    "in a keyValue attribute did not pass validation. This applies to checked " +
    "categorizations, identifiers and other validated code lists.";

  // Signifies that a uuid_key value passed has been removed from the registry.
  // While the key was once valid as an accessor, and is still possibly valid,
  // the publisher has removed the information referenced by the uuid_key passed.
  public static final int E_KEY_RETIRED = 10310;
  public static final String E_KEY_RETIRED_CODE = "E_keyRetired";
  public static final String E_KEY_RETIRED_MSG = "A uuid_key value passed has " +
    "been removed from the registry.";

  // Signifies that an exception was detected while processing elements that were
  // annotated with xml:lang qualifiers. Presently, only the description element
  // supports xml:lang qualifiacations.
  public static final int E_LANGUAGE_ERROR = 10060;
  public static final String E_LANGUAGE_ERROR_CODE = "E_languageError";
  public static final String E_LANGUAGE_ERROR_MSG = "An exception was detected " +
    "while processing elements that were annotated with xml:lang qualifiers.";

  // Signifies that the authentication token information has timed out.
  // NOTE: The UDDI specification indicates that the errno value should be 30100
  // but this value conflicts with the E_INVALID_COMPLETION_STATUS. The v2.0
  // Errata document corrects this exception by using 30110. This has been changed.
  public static final int E_MESSAGE_TOO_LARGE = 30110;
  public static final String E_MESSAGE_TOO_LARGE_CODE = "E_messageTooLarge";
  public static final String E_MESSAGE_TOO_LARGE_MSG = "The message is too large.";

  // Signifies that the partial name value passed exceeds the maximum name
  // length designated by the policy of an implementation or Operator Site.
  public static final int E_NAME_TOO_LONG = 10020;
  public static final String E_NAME_TOO_LONG_CODE = "E_nameTooLong";
  public static final String E_NAME_TOO_LONG_MSG = "The partial name value " +
    "passed exceeds the maximum name length designated by the policy of an " +
    "implementation or Operator Site.";

  // Signifies that an attempt was made to use the publishing API to change data
  // that is mastered at another Operator Site. This exception is only relevant to the
  // public Operator Sites and does not apply to other UDDI compatible registries.
  public static final int E_OPERATOR_MISMATCH = 10130;
  public static final String E_OPERATOR_MISMATCH_CODE = "E_operatorMismatch";
  public static final String E_OPERATOR_MISMATCH_MSG = "An attempt was made " +
    "to use the publishing API to change data that is mastered at another Operator " +
    "Site.";

  // Signifies that the authentication token information has timed out.
  public static final int E_PUBLISHER_CANCELLED = 30220;
  public static final String E_PUBLISHER_CANCELLED_CODE = "E_publisherCancelled";
  public static final String E_PUBLISHER_CANCELLED_MSG = "The target publisher " +
    "cancelled the custody transfer.";

  // Signifies that the authentication token information has timed out.
  public static final int E_REQUEST_DENIED = 30210;
  public static final String E_REQUEST_DENIED_CODE = "E_requestDenied";
  public static final String E_REQUEST_DENIED_MSG = "A custody transfer " +
    "request has been refused.";

  // Signifies that the authentication token information has timed out.
  public static final int E_SECRET_UNKNOWN = 30230;
  public static final String E_SECRET_UNKNOWN_CODE = "E_secretUnknown";
  public static final String E_SECRET_UNKNOWN_MSG = "The target publisher " +
    "was unable to match the shared secret and the five (5) attempt limit was " +
    "exhausted. The target publisher automatically cancelled the transfer operation.";

  // Signifies no failure occurred. This return code is used with the
  // dispositionReport for reporting results from requests with no natural
  // response document.
  public static final int E_SUCCESS = 0;
  public static final String E_SUCCESS_CODE = "E_success";
  public static final String E_SUCCESS_MSG = null;

  // Signifies that incompatible arguments were passed.
  public static final int E_TOO_MANY_OPTIONS = 10030;
  public static final String E_TOO_MANY_OPTIONS_CODE = "E_tooManyOptions";
  public static final String E_TOO_MANY_OPTIONS_MSG = "Incompatible arguments " +
    "were passed.";

  // Signifies that the authentication token information has timed out.
  public static final int E_TRANSFER_ABORTED = 30200;
  public static final String E_TRANSFER_ABORTED_CODE = "E_transferAborted";
  public static final String E_TRANSFER_ABORTED_MSG = "Signifies that a " +
    "custody transfer request will not succeed.";

  // Signifies that the value of the generic attribute passed is unsupported by
  // the Operator Instance being queried.
  public static final int E_UNRECOGNIZED_VERSION = 10040;
  public static final String E_UNRECOGNIZED_VERSION_CODE = "E_unrecognizedVersion";
  public static final String E_UNRECOGNIZED_VERSION_MSG = "The value of the " +
    "generic attribute passed is unsupported by the Operator Instance being queried.";

  // Signifies that the user ID and password pair passed in a get_authToken
  // message is not known to the Operator Site or is not valid.
  public static final int E_UNKNOWN_USER = 10150;
  public static final String E_UNKNOWN_USER_CODE = "E_unknownUser";
  public static final String E_UNKNOWN_USER_MSG = "The user ID and password " +
    "pair passed in a get_authToken message is not known to the Operator Site " +
    "or is not valid.";

  // Signifies that the implementor does not support a feature or API.
  public static final int E_UNSUPPORTED = 10050;
  public static final String E_UNSUPPORTED_CODE = "E_unsupported";
  public static final String E_UNSUPPORTED_MSG = "The implementor does not " +
    "support a feature or API.";

  // Signifies that an attempt was made to use the publishing API to change data
  // that is controlled by another party. In certain cases, E_operatorMismatch
  // takes precedence in reporting an exception.
  public static final int E_USER_MISMATCH = 10140;
  public static final String E_USER_MISMATCH_CODE = "E_userMismatch";
  public static final String E_USER_MISMATCH_MSG = "An attempt was made to " +
    "use the publishing API to change data that is controlled by another party.";

  // Signifies that the authentication token information has timed out.
  public static final int E_VALUE_NOT_ALLOWED = 20210;
  public static final String E_VALUE_NOT_ALLOWED_CODE = "E_valueNotAllowed";
  public static final String E_VALUE_NOT_ALLOWED_MSG = "A value did not " +
    "pass validation because of contextual issues. The value may be valid in " +
    "some contexts, but not in the contextused.";

  // The specification incorrectly omits the exception code E_unvalidatable,
  // numerical value 20220, from the list of valid exception codes. This has
  // been corrected.
  public static final int E_UNVALIDATABLE = 20220;
  public static final String E_VUNVALIDATABLE_CODE = "E_unvalidatable";
  public static final String E_UNVALIDATABLE_MSG = "";

  // Signifies that the request could not be carried out because a needed
  // web service, such as validate_values, did not respond in a reasonable
  // amount of time.
  public static final int E_REQUEST_TIMEOUT = 20240;
  public static final String E_REQUEST_TIMEOUT_CODE = "E_requestTimeout";
  public static final String E_REQUEST_TIMEOUT_MSG = "The request could not " +
    "be carried out because a needed web service, such as validate_values, " +
    "did not respond in a reasonable amount of time.";

  public static final int E_MONITOR_FAILED = 20260;
  public static final String E_MONITOR_FAILED_CODE = "E_monitorFailed";
  public static final String E_MONITOR_FAILED_MSG = "Failure in the monitor";

  int errno;
  ErrInfo errInfo;

  /**
   *
   */
  public Result()
  {
  }

  /**
   *
   */
  public Result(int errno)
  {
    setErrno(errno);
  }

  /**
   *
   */
  public Result(int errno,ErrInfo errInfo)
  {
    this.errno = errno;
    this.errInfo = errInfo;
  }

  /**
   *
   */
  public Result(int errno,String errCode,String errMsg)
  {
    this.errno = errno;
    this.errInfo = new ErrInfo(errCode,errMsg);
  }

  /**
   * Sets the exception number of this disposition report to the given value.
   * @param nmbr The new exception number for this disposition report.
   */
  public void setErrno(int nmbr)
  {
    this.errno = nmbr;
  }

  /**
   * Sets the exception number of this disposition report to the given value.
   * @param nmbr The new exception number for this disposition report.
   */
  public void setErrno(String nmbr)
  {
    Integer integer = new Integer(nmbr);
    this.errno = integer.intValue();
  }

  /**
   * Returns the exception number of this disposition report.
   * @return The exception number of this disposition report.
   */
  public int getErrno()
  {
    return this.errno;
  }

  /**
   *
   */
  public void setErrInfo(ErrInfo info)
  {
    this.errInfo = info;
  }

  /**
   *
   */
  public ErrInfo getErrInfo()
  {
    return this.errInfo;
  }

  /**
   *
   */
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("org.apache.juddi.datatype.response.Result.errno=");
    buffer.append(this.errno);
    buffer.append("\n");
    buffer.append(this.errInfo);

    return buffer.toString();
  }

  // test driver
  public static void main(String[] args)
  {
    Result obj = new Result();
    obj.setErrno(123);

    ErrInfo errInfo = new ErrInfo();
    errInfo.setErrCode("abc");
    errInfo.setErrMsg("def");

    obj.setErrInfo(errInfo);

    System.out.println(obj);
  }
}