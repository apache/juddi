/*
 * Copyright 2001-2010 The Apache Software Foundation.
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
package org.apache.juddi.v3.client.config;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TokenResolver {
	
	private static Log log = LogFactory.getLog(TokenResolver.class);
	
	public synchronized static String replaceTokens(String string, Properties properties) {
    	
    	if (properties==null || string==null) return string;
    	string = string.replaceAll("\\n"," ").replaceAll("\\r", "").replaceAll(" ","");
		/* pattern that is multi-line (?m), and looks for a pattern of
		 * ${token}, in a 'reluctant' manner, by using the ? to 
		 * make sure we find ALL the tokens.
		 */
		Pattern pattern = Pattern.compile("(?m)\\$\\{.*?\\}");
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            String token = matcher.group();
            token = token.substring(2,token.length()-1);
            String replacement = properties.getProperty(token);
            if (replacement!=null) {
            	log.debug("Found token " + token + " and replacement value " + replacement);
            	string = string.replaceAll("\\$\\{" + token + "\\}", replacement);
            } else {
            	log.error("Found token " + token + " but could not obtain its value. Data: " + string);
            }
        }
        log.debug("Data after token replacement: " + string);
        return string;
	}
}
