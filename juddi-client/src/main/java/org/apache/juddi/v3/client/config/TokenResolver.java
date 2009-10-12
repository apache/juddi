package org.apache.juddi.v3.client.config;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class TokenResolver {
	
	private static Logger log = Logger.getLogger(TokenResolver.class);
	
	public synchronized static String replaceTokens(String string, Properties properties) {
    	
    	if (properties==null || string==null) return string;
    	string = string.replaceAll("\\n"," ").replaceAll("\\r", "");
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
