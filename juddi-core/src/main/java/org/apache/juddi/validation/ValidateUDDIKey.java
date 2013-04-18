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

package org.apache.juddi.validation;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.juddi.keygen.KeyGenerator;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.InvalidKeyPassedException;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
public class ValidateUDDIKey {

	public static void validateUDDIv3Key(String key) throws DispositionReportFaultMessage {
		if (key == null)
			throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.NullKey"));
		
		if (! key.contains(KeyGenerator.PARTITION_SEPARATOR)) return; //v2 style key; no other validation rules apply
		
		String keyToTest = key.trim();
		if (keyToTest.endsWith(KeyGenerator.PARTITION_SEPARATOR))
			throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.MalformedKey", key));

		StringTokenizer tokenizer = new StringTokenizer(key.toLowerCase(), KeyGenerator.PARTITION_SEPARATOR);
		
		for(int count = 0; tokenizer.hasMoreTokens(); count++) {
			String nextToken = tokenizer.nextToken();

			if (count == 0) {
				if (!ValidateUDDIKey.isValidUDDIScheme(nextToken))
					throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.MalformedKey", key));
			}
			else if (count == 1) {
				if(!ValidateUDDIKey.isValidDomainKey(nextToken))
					throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.MalformedKey", key));
			}
			else {
				if (!isValidKSS(nextToken))
					throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.MalformedKey", key));
			}
		}
	}
	
	public static void validateUDDIv3KeyGeneratorKey(String key) throws DispositionReportFaultMessage {
		if (key == null)
			throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.NullKeys"));
		
		if ( !(key.toUpperCase().endsWith(KeyGenerator.KEYGENERATOR_SUFFIX.toUpperCase())) )
			throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.KeyGenSuffix", key));
		
		validateUDDIv3Key(key.substring(0, key.lastIndexOf(KeyGenerator.PARTITION_SEPARATOR)));
	}
	
	public static void validateUDDIv3KeyGeneratorTModel(org.uddi.api_v3.TModel tModel) throws DispositionReportFaultMessage {
		if (tModel == null)
			throw new ValueNotAllowedException(new ErrorMessage("errors.tmodel.NullInput"));
		
		validateUDDIv3KeyGeneratorKey(tModel.getTModelKey());

		// A key generator key should have exactly one category and it's key value should be 'keyGenerator'
		org.uddi.api_v3.CategoryBag categories = tModel.getCategoryBag();
		if (categories != null) {
			List<org.uddi.api_v3.KeyedReference> elems = categories.getKeyedReference();
			if (elems != null && elems.size() == 1) {
				org.uddi.api_v3.KeyedReference elem = elems.get(0);
				if (elem != null) {
					if (elem != null && elem instanceof org.uddi.api_v3.KeyedReference) {
						String keyedValue = ((org.uddi.api_v3.KeyedReference)elem).getKeyValue();
						if (keyedValue != null) {
							if (keyedValue.equalsIgnoreCase(KeyGenerator.KEYGENERATOR_SUFFIX))
								return;
						}
					}
				}
			}
		}

		throw new ValueNotAllowedException(new ErrorMessage("errors.tmodel.keygenerator.BadCategory"));
	}
	
	public static boolean isValidUDDIScheme(String token) {
		if (token == null)
			return false;
		
		if (!KeyGenerator.UDDI_SCHEME.equalsIgnoreCase(token))
			return false;

		return true;

	}
	
	public static boolean isValidDomainKey(String token) {
		if(token.indexOf("..") != -1)
			return false;

		StringTokenizer tokenizer = new StringTokenizer(token, ".");
		int tokensCount = tokenizer.countTokens();
		for(int i = 0; tokenizer.hasMoreTokens(); i++) {
			String domainPart = tokenizer.nextToken();
			if(i == tokensCount - 1) {
				if(!isValidTopLabel(domainPart)) {
					return false;
				}
			} else if(!isValidDomainLabel(domainPart)) {
				return false;
			}
		}
		return true;
	}
	

	private static boolean isValidDomainLabel(String token) {
		char[] chars = token.toCharArray();
		for(int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if(c != '-' && !Character.isLetterOrDigit(c)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isValidTopLabel(String token) {
		return Character.isLetter(token.charAt(0)) && (token.length() == 1 || isValidDomainLabel(token.substring(1)));
	}
	
	public static boolean isValidKSS(String token) {
		if (token.length() == 0)
			return false;

		// The key generator suffix is a reserved word and cannot be found in any part of the KSS 
		if (token.equalsIgnoreCase(KeyGenerator.KEYGENERATOR_SUFFIX))
			return false;

		return true;
	}
}
