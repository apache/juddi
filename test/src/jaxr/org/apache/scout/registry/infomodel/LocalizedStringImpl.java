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

package org.apache.scout.registry.infomodel;

import java.util.Locale;

import javax.xml.registry.JAXRException;

/**
 * Implements JAXR Interface.
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class LocalizedStringImpl 
implements javax.xml.registry.infomodel.LocalizedString {
          
      public static final String DEFAULT_CHARSET_NAME = "US-ASCII";
          
      private String charset = DEFAULT_CHARSET_NAME;
    
      private Locale locale = null;
      private String value = "";
          
    /** Creates a new instance of LocalizedStringImpl */
    public LocalizedStringImpl() {
    }
    public LocalizedStringImpl(String str ) throws JAXRException{
        setLocale( Locale.getDefault());
        setValue( str);
    }
    public LocalizedStringImpl(Locale locale, String str ) 
    throws JAXRException{
        setLocale( locale);
        setValue( str);
    }
    
    public String getCharsetName() 
    throws JAXRException {
        return charset;
    }
    
    public Locale getLocale() 
    throws JAXRException {
        return locale;
    }
    
    public String getValue() 
    throws JAXRException {
        return value;
    }
    
    public void setCharsetName(String str) 
    throws JAXRException {
        this.charset = str;
    }
    
    public void setLocale(java.util.Locale locale) 
    throws JAXRException {
        this.locale = locale;
    }
    
    public void setValue(String str) 
    throws JAXRException {
        this.value = str;
    }
    
}
