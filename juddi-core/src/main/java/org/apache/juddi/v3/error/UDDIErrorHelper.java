/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
 *
 */

package org.apache.juddi.v3.error;

import org.apache.juddi.config.ResourceConfig;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.Result;
import org.uddi.api_v3.ErrInfo;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class UDDIErrorHelper {

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
	public static final int E_INVALID_COMBINATION = 40500;
	public static final int E_KEY_RETIRED = 10310;
	public static final int E_KEY_UNAVAILABLE = 40100;
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
	public static final int E_TOKEN_ALREADY_EXISTS = 40070;
	public static final int E_TRANSFER_NOT_ALLOWED = 40600;

	public static final String lookupErrCode(int errno) {
		switch (errno) {
			case E_ACCOUNT_LIMIT_EXCEEDED     : return DispositionReport.E_ACCOUNT_LIMIT_EXCEEDED;
			case E_ASSERTION_NOT_FOUND        : return DispositionReport.E_ASSERTION_NOT_FOUND; 
			case E_AUTH_TOKEN_EXPIRED         : return DispositionReport.E_AUTH_TOKEN_EXPIRED;
			case E_AUTH_TOKEN_REQUIRED        : return DispositionReport.E_AUTH_TOKEN_REQUIRED;
			case E_BUSY                       : return DispositionReport.E_BUSY;
			case E_CATEGORIZATION_NOT_ALLOWED : return DispositionReport.E_CATEGORIZATION_NOT_ALLOWED;
			case E_FATAL_ERROR                : return DispositionReport.E_FATAL_ERROR;
			case E_INVALID_COMBINATION        : return DispositionReport.E_INVALID_COMBINATION;
			case E_INVALID_CATEGORY           : return DispositionReport.E_INVALID_CATEGORY;
			case E_INVALID_COMPLETION_STATUS  : return DispositionReport.E_INVALID_COMPLETION_STATUS;
			case E_INVALID_KEY_PASSED         : return DispositionReport.E_INVALID_KEY_PASSED;
			case E_KEY_UNAVAILABLE         	  : return DispositionReport.E_KEY_UNAVAILABLE;
			case E_INVALID_PROJECTION         : return DispositionReport.E_INVALID_PROJECTION;
			case E_INVALID_TIME               : return DispositionReport.E_INVALID_TIME;
			case E_INVALID_URL_PASSED         : return DispositionReport.E_INVALID_URL_PASSED;
			case E_INVALID_VALUE              : return DispositionReport.E_INVALID_VALUE;
			case E_KEY_RETIRED                : return DispositionReport.E_KEY_RETIRED;
			case E_LANGUAGE_ERROR             : return DispositionReport.E_LANGUAGE_ERROR;
			case E_MESSAGE_TOO_LARGE          : return DispositionReport.E_MESSAGE_TOO_LARGE;
			case E_NAME_TOO_LONG              : return DispositionReport.E_NAME_TOO_LONG;
			case E_OPERATOR_MISMATCH          : return DispositionReport.E_OPERATOR_MISMATCH;
			case E_PUBLISHER_CANCELLED        : return DispositionReport.E_PUBLISHER_CANCELLED;
			case E_REQUEST_DENIED             : return DispositionReport.E_REQUEST_DENIED;
			case E_REQUEST_TIMEOUT            : return DispositionReport.E_REQUEST_TIMEOUT;
			case E_RESULT_SET_TOO_LARGE       : return DispositionReport.E_RESULT_SET_TOO_LARGE;
			case E_SECRET_UNKNOWN             : return DispositionReport.E_SECRET_UNKNOWN;
			case E_SUCCESS                    : return DispositionReport.E_SUCCESS;
			case E_TOO_MANY_OPTIONS           : return DispositionReport.E_TOO_MANY_OPTIONS;
			case E_TRANSFER_ABORTED           : return DispositionReport.E_TRANSFER_ABORTED;
			case E_UNKNOWN_USER               : return DispositionReport.E_UNKNOWN_USER;
			case E_UNRECOGNIZED_VERSION       : return DispositionReport.E_UNRECOGNIZED_VERSION;
			case E_UNSUPPORTED                : return DispositionReport.E_UNSUPPORTED;
			case E_UNVALIDATABLE              : return DispositionReport.E_UNVALIDATABLE;
			case E_USER_MISMATCH              : return DispositionReport.E_USER_MISMATCH;
			case E_VALUE_NOT_ALLOWED          : return DispositionReport.E_VALUE_NOT_ALLOWED;
			case E_TOKEN_ALREADY_EXISTS       : return DispositionReport.E_TOKEN_ALREADY_EXISTS;
			case E_TRANSFER_NOT_ALLOWED       : return DispositionReport.E_TRANSFER_NOT_ALLOWED;
			default                           : return null;
		}
	}  

	public static final String lookupErrText(int errno) {
		String errCode = lookupErrCode(errno);
		if (errCode == null)
			return null;
		return ResourceConfig.getGlobalMessage(errCode);
	}    

	public static final DispositionReport buildDispositionReport(int errNo) {
		DispositionReport dr = new DispositionReport();
		Result res = new Result();
		res.setErrno(errNo);
		
		ErrInfo ei = new ErrInfo();
		ei.setErrCode(lookupErrCode(errNo));
		ei.setValue(lookupErrText(errNo));
		
		res.setErrInfo(ei);
		
		dr.getResult().add(res);
			  
		return dr;
	}
}
