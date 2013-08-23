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
using org.apache.juddi.v3.client.log;
using System;
using System.Text.RegularExpressions;

namespace org.apache.juddi.v3.client.config
{

    public class TokenResolver
    {

        private static Log log = LogFactory.getLog(typeof(TokenResolver));

        public static String replaceTokens(String s, Properties properties)
        {

            if (properties == null || s == null) return s;
            s = s.Replace("\\n", " ").Replace("\\r", "").Replace(" ", "");
            /* pattern that is multi-line (?m), and looks for a pattern of
             * ${token}, in a 'reluctant' manner, by using the ? to 
             * make sure we find ALL the tokens.
             */
            //Pattern pattern = Pattern.compile("(?m)\\$\\{.*?\\}");
            //Matcher matcher = pattern.matcher(s);
            Match matcher = System.Text.RegularExpressions.Regex.Match(s, "(?m)\\$\\{.*?\\}");
            Match temp = matcher.NextMatch();
            while (temp != null)
            {
                String token = matcher.Value;
                token = token.Substring(2, token.Length - 1);
                String replacement = properties.getString(token);
                if (replacement != null)
                {
                    log.debug("Found token " + token + " and replacement value " + replacement);
                    s = s.Replace("\\$\\{" + token + "\\}", replacement);
                }
                else
                {
                    log.error("Found token " + token + " but could not obtain its value. Data: " + s);
                }
                temp = matcher.NextMatch();

            }
            log.debug("Data after token replacement: " + s);
            return s;
        }

        public static string replaceTokens(string s, uddiClientNodeProperty[] properties)
        {

            if (properties == null || s == null) return s;
            s = s.Replace("\\n", " ").Replace("\\r", "").Replace(" ", "");
            s = s.Trim();
            /* pattern that is multi-line (?m), and looks for a pattern of
             * ${token}, in a 'reluctant' manner, by using the ? to 
             * make sure we find ALL the tokens.
             */
            //Pattern pattern = Pattern.compile("(?m)\\$\\{.*?\\}");
            //Matcher matcher = pattern.matcher(s);
            Match matcher = System.Text.RegularExpressions.Regex.Match(s, "(?m)\\$\\{.*?\\}");
            // Match temp = matcher.NextMatch();
            while (matcher.Success)
            {
                String token = matcher.Value;
                token = token.Substring(2, token.Length - 3);
                String replacement = getValue(token, properties);
                if (replacement != null)
                {
                    log.debug("Found token " + token + " and replacement value " + replacement);
                    s = s.Replace("${" + token + "}", replacement);
                }
                else
                {
                    log.error("Found token " + token + " but could not obtain its value. Data: " + s);
                }
                //  matcher = System.Text.RegularExpressions.Regex.Match(s, "(?m)\\$\\{.*?\\}");
                matcher = matcher.NextMatch();

            }
            log.debug("Data after token replacement: " + s);
            return s;
        }
        static string getValue(String key, uddiClientNodeProperty[] items)
        {
            if (key == null || items == null)
                return null;
            for (int i = 0; i < items.Length; i++)
            {
                if (items[i].name.Equals(key, StringComparison.CurrentCultureIgnoreCase))
                    return items[i].value;
            }
            return null;

        }
    }

}
