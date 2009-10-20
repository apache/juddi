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

import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.RegistryException;
import org.apache.juddi.v3.error.UDDIErrorHelper;

/**
 *   E_invalidProjection: (20230) Signifies that an attempt was made to save a businessEntity containing a service projection where the serviceKey does not 
 *   belong to the business designated by the businessKey. The serviceKey of at least one such businessService SHOULD be included in the dispositionReport.
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class InvalidProjectionException extends RegistryException {

	private static final long serialVersionUID = -2447287220774262681L;

	public InvalidProjectionException(ErrorMessage message) {
		super(message, UDDIErrorHelper.buildDispositionReport(UDDIErrorHelper.E_INVALID_PROJECTION));
	}
}
