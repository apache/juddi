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
package org.apache.juddi.datatype.response;

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.i18n.RegistryResourceBundle;

/**
 * Used in response DispositionReport.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class Result implements RegistryObject
{
  public static final int E_ASSERTION_NOT_FOUND = 30000;
  public static final int E_AUTH_TOKEN_EXPIRED = 10110;
  public static final int E_AUTH_TOKEN_REQUIRED = 10120;
  public static final int E_ACCOUNT_LIMIT_EXCEEDED = 10160;
  public static final int E_BUSY = 10400;
  public static final int E_CATEGORIZATION_NOT_ALLOWED = 20100;
  public static final int E_FATAL_ERROR = 10500;
  public static final int E_INVALID_KEY_PASSED = 10210;
  public static final int E_INVALID_PROJECTION = 20230;
  public static final int E_INVALID_CATEGORY = 20000;
  public static final int E_INVALID_COMPLETION_STATUS = 30100;
  public static final int E_INVALID_URL_PASSED = 10220;
  public static final int E_INVALID_VALUE = 20200;
  public static final int E_KEY_RETIRED = 10310;
  public static final int E_LANGUAGE_ERROR = 10060;
  public static final int E_MESSAGE_TOO_LARGE = 30110;
  public static final int E_NAME_TOO_LONG = 10020;
  public static final int E_OPERATOR_MISMATCH = 10130;
  public static final int E_PUBLISHER_CANCELLED = 30220;
  public static final int E_REQUEST_DENIED = 30210;
  public static final int E_SECRET_UNKNOWN = 30230;
  public static final int E_SUCCESS = 0;
  public static final int E_TOO_MANY_OPTIONS = 10030;
  public static final int E_TRANSFER_ABORTED = 30200;
  public static final int E_UNRECOGNIZED_VERSION = 10040;
  public static final int E_UNKNOWN_USER = 10150;
  public static final int E_UNSUPPORTED = 10050;
  public static final int E_USER_MISMATCH = 10140;
  public static final int E_VALUE_NOT_ALLOWED = 20210;
  public static final int E_UNVALIDATABLE = 20220;
  public static final int E_REQUEST_TIMEOUT = 20240;
  public static final int E_INVALID_TIME = 40030;
  public static final int E_RESULT_SET_TOO_LARGE = 40300;
  
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
   * Sets the exception code of this ErrInfo to the given value.
   * @param errcode The new code number for this ErrInfo.
   */
  public void setErrCode(String errCode)
  {
    if (this.errInfo == null)
      errInfo = new ErrInfo();
    errInfo.setErrCode(errCode);
  }

  /**
   * Returns the exception code of this ErrInfo.
   * @return The exception code of this ErrInfo.
   */
  public String getErrCode()
  {
    if (this.errInfo == null)
      return null;
    else
      return errInfo.getErrCode();
  }

  /**
   * Sets the exception message of this ErrInfo to the given value.
   * @param msg The new exception message for this ErrInfo.
   */
  public void setErrText(String errText)
  {
    if (this.errInfo == null)
      errInfo = new ErrInfo();
    errInfo.setErrMsg(errText);
  }

  /**
   * Returns the error text of this ErrInfo.
   * @return The error text of this ErrInfo.
   */
  public String getErrText()
  {
    if (this.errInfo == null)
      return null;
    else
      return errInfo.getErrMsg();
  }

  /**
   *
   */
  public String toString()
  {
    if (errInfo == null)
      return String.valueOf(errno); 
    
    StringBuffer buffer = new StringBuffer();
    
    String errCode = errInfo.getErrCode();
    if (errCode != null)
    {
      buffer.append(errCode);
      buffer.append(" ");
    }
    
    buffer.append("(");
    buffer.append(errno);
    buffer.append(") ");
    
    String errText = errInfo.getErrMsg();
    if (errText != null)
    {
      buffer.append(errText);
      buffer.append(" ");
    }
    
    return buffer.toString();
  }

  /**
   *
   */
  public static final String lookupErrCode(int errno)
  {
    switch (errno)
    {
      case E_ACCOUNT_LIMIT_EXCEEDED     : return "E_accountLimitExceeded";
      case E_ASSERTION_NOT_FOUND        : return "E_assertionNotFound"; 
      case E_AUTH_TOKEN_EXPIRED         : return "E_authTokenExpired";
      case E_AUTH_TOKEN_REQUIRED        : return "E_authTokenRequired";
      case E_BUSY                       : return "E_busy";
      case E_CATEGORIZATION_NOT_ALLOWED : return "E_categorizationNotAllowed";
      case E_FATAL_ERROR                : return "E_fatalError";
      case E_INVALID_CATEGORY           : return "E_invalidCategory";
      case E_INVALID_COMPLETION_STATUS  : return "E_invalidCompletionStatus";
      case E_INVALID_KEY_PASSED         : return "E_invalidKeyPassed";
      case E_INVALID_PROJECTION         : return "E_invalidProjection";
      case E_INVALID_TIME               : return "E_invalidTime";
      case E_INVALID_URL_PASSED         : return "E_invalidURLPassed";
      case E_INVALID_VALUE              : return "E_invalidValue";
      case E_KEY_RETIRED                : return "E_keyRetired";
      case E_LANGUAGE_ERROR             : return "E_languageError";
      case E_MESSAGE_TOO_LARGE          : return "E_messageTooLarge";
      case E_NAME_TOO_LONG              : return "E_nameTooLong";
      case E_OPERATOR_MISMATCH          : return "E_operatorMismatch";
      case E_PUBLISHER_CANCELLED        : return "E_publisherCancelled";
      case E_REQUEST_DENIED             : return "E_requestDenied";
      case E_REQUEST_TIMEOUT            : return "E_requestTimeout";
      case E_RESULT_SET_TOO_LARGE       : return "E_resultSetTooLarge";
      case E_SECRET_UNKNOWN             : return "E_secretUnknown";
      case E_SUCCESS                    : return "E_success";
      case E_TOO_MANY_OPTIONS           : return "E_tooManyOptions";
      case E_TRANSFER_ABORTED           : return "E_transferAborted";
      case E_UNKNOWN_USER               : return "E_unknownUser";
      case E_UNRECOGNIZED_VERSION       : return "E_unrecognizedVersion";
      case E_UNSUPPORTED                : return "E_unsupported";
      case E_UNVALIDATABLE              : return "E_unvalidatable";
      case E_USER_MISMATCH              : return "E_userMismatch";
      case E_VALUE_NOT_ALLOWED          : return "E_valueNotAllowed";
      default                           : return null;
    }
  }  
  
  /**
   *
   */
  public static final String lookupErrText(int errno)
  {
    String errCode = lookupErrCode(errno);
    if (errCode == null)
      return null;
    
    return RegistryResourceBundle.getString(errCode);
  }    
}