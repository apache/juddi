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
package org.apache.juddi.error;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class ErrorUtil
{
    static final String buildErrorText(int nmbr,String code,String text,String msg)
    {
        StringBuffer errorText = new StringBuffer();
        errorText.append(code);
        errorText.append(" (");
        errorText.append(nmbr);
        errorText.append(") ");
        errorText.append(text);
        
        if ((msg != null) && (msg.trim().length() > 0))
        {
          errorText.append(" ");
          errorText.append(msg);
        }
        
        return errorText.toString();
    }
}