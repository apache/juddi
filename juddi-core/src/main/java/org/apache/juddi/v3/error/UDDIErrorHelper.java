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
        /**
         *  E_assertionNotFound: (30000) Signifies that a particular publisher assertion cannot be identified in a save or delete operation.
         */
	public static final int E_ASSERTION_NOT_FOUND = 30000;
        /**
         *  E_authTokenExpired: (10110) Signifies that the authentication token information has timed out.
         */
	public static final int E_AUTH_TOKEN_EXPIRED = 10110;
        /**
         *  E_authTokenRequired: (10120) Signifies that an authentication token is missing or is invalid for an API call that requires authentication.
         */
	public static final int E_AUTH_TOKEN_REQUIRED = 10120;
        /**
         * E_accountLimitExceeded: (10160) Signifies that a save request exceeded the quantity limits for a given data type. Account limits are established based on the relationship between an individual publisher and an individual node. See your UDDI node’s policy for account limits for details. Other nodes in the registry MAY NOT place additional restrictions on publishing limits established by a custodial node.
         */
	public static final int E_ACCOUNT_LIMIT_EXCEEDED = 10160;
        /**
         *  E_busy: (10400) Signifies that the request cannot be processed at the current time.
         */
	public static final int E_BUSY = 10400;
        /**
         * NON UDDIv3 spec error
         */
	public static final int E_CATEGORIZATION_NOT_ALLOWED = 20100;
        /**
         * E_fatalError: (10500) Signifies that a serious technical error has occurred while processing the request.
         */
	public static final int E_FATAL_ERROR = 10500;
        /**
         *  E_invalidKeyPassed: (10210) Signifies that the uddiKey value passed did not match with any known key values.  The details on the invalid key SHOULD be included in the dispositionReport element.
         */
	public static final int E_INVALID_KEY_PASSED = 10210;
        /**
         * E_invalidProjection: (20230) Signifies that an attempt was made to save a businessEntity containing a service projection where the serviceKey does not belong to the business designated by the businessKey. The serviceKey of at least one such businessService SHOULD be included in the dispositionReport.
         */
	public static final int E_INVALID_PROJECTION = 20230;
        /**
         * NON UDDIv3 spec
         */
	public static final int E_INVALID_CATEGORY = 20000;
        /**
         *   E_invalidCompletionStatus: (30100) Signifies that one of the assertion status values passed is unrecognized.  The completion status that caused the problem SHOULD be clearly indicated in the error text.
         */
	public static final int E_INVALID_COMPLETION_STATUS = 30100;
        
	public static final int E_INVALID_URL_PASSED = 10220;
        /**
         *  E_invalidValue: (20200) This error code has multiple uses.  This error code applies to the subscription APIs and the value set APIs. It can be used to indicate that a value that was passed in a keyValue attribute did not pass validation.  This applies to checked value sets that are referenced using keyedReferences. The error text SHOULD clearly indicate the key and value combination that failed validation. It can also be used to indicate that a chunkToken supplied is invalid. This applies in both subscription and value set APIs.  The error text SHOULD clearly indicate the reason for failure.
         */
	public static final int E_INVALID_VALUE = 20200;
        /**
         *  E_invalidCombination: (40500) Signifies conflicting find qualifiers have been specified.  The find qualifiers that caused the problem SHOULD be clearly indicated in the error text.
         */
	public static final int E_INVALID_COMBINATION = 40500;
	public static final int E_KEY_RETIRED = 10310;
        /**
         * E_keyUnavailable: (40100) Signifies that the proposed key is in a partition that has already been assigned to some other publisher.
         */
	public static final int E_KEY_UNAVAILABLE = 40100;
	public static final int E_LANGUAGE_ERROR = 10060;
        /**
         *  E_messageTooLarge: (30110) Signifies that the message is too large.  The upper limit SHOULD be clearly indicated in the error text.
         */
	public static final int E_MESSAGE_TOO_LARGE = 30110;
	public static final int E_NAME_TOO_LONG = 10020;
	public static final int E_OPERATOR_MISMATCH = 10130;
	public static final int E_PUBLISHER_CANCELLED = 30220;
        /**
         *  Signifies that a subscription cannot be renewed. The request has been denied due to either node or registry policy.
         */
	public static final int E_REQUEST_DENIED = 30210;
	public static final int E_SECRET_UNKNOWN = 30230;
	public static final int E_SUCCESS = 0;
	public static final int E_TOO_MANY_OPTIONS = 10030;
	public static final int E_TRANSFER_ABORTED = 30200;
        /**
         * E_unrecognizedVersion: (10040) Signifies that the value of the namespace attribute is unsupported by the node being queried.
         */
	public static final int E_UNRECOGNIZED_VERSION = 10040;
        /**
         * E_unknownUser: (10150) Signifies that the user ID and password pair passed in a get_authToken API is not known to the UDDI node or is not valid.
         */
	public static final int E_UNKNOWN_USER = 10150;
        /**
         * E_unsupported: (10050) Signifies that the implementer does not support a feature or API.
         */
	public static final int E_UNSUPPORTED = 10050;
        /**
         *  E_userMismatch: (10140) Signifies that an attempt was made to use the publishing API to change data that is controlled by another party.
         */
	public static final int E_USER_MISMATCH = 10140;
        /**
         * E_valueNotAllowed: (20210) Signifies that a value did not pass validation because of contextual issues.  The value may be valid in some contexts, but not in the context used.  The error text MAY contain information about the contextual problem.
         */
	public static final int E_VALUE_NOT_ALLOWED = 20210;
        /**
         * E_unvalidatable: (20220) Signifies that an attempt was made to reference a value set in a keyedReference whose tModel is categorized with the unvalidatable categorization.
         */
	public static final int E_UNVALIDATABLE = 20220;
	public static final int E_REQUEST_TIMEOUT = 20240;
        /**
         * E_invalidTime: (40030) Signifies that the time period, the date/time, or the pair of date/time is invalid. The error structure signifies the condition that occurred and the error text clearly calls out the cause of the problem.
         */
	public static final int E_INVALID_TIME = 40030;
        /**
         *  E_resultSetTooLarge: (40300) Signifies that the UDDI node deems that a result set from an inquiry is too large, and requests to obtain the results are not honored, even using subsets.  The inquiry that triggered this error should be refined and re-issued.
         */
	public static final int E_RESULT_SET_TOO_LARGE = 40300;
        /**
         * E_tokenAlreadyExists: (40070) Signifies that one or more of the businessKey or tModelKey elements that identify entities to be transferred are not owned by the publisher identified by the authInfo element.  The error text SHOULD clearly indicate which entity keys caused the error.
         */
	public static final int E_TOKEN_ALREADY_EXISTS = 40070;
        /**
         *  E_transferNotAllowed: (40600) Signifies that the transfer of one or more entities has been by either the custodial node or the target node because the transfer token has expired or an attempt was made to transfer an unauthorized entity.
         */
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
