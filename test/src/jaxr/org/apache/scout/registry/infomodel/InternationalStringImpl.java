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
import  javax.xml.registry.*;
import  javax.xml.registry.infomodel.*;

import java.util.*;

/**
 * For futher details, look into the JAXR API Javadoc.
 * @author Anil Saldhana  <anil@apache.org>
 */
public class InternationalStringImpl implements InternationalString {
   /**
    * Maintains an Hashmap of locale to string value
    */ 
   private HashMap map = new HashMap();
    
    /** Creates a new instance of InternationalStringImpl */
    public InternationalStringImpl() {
    }
    
    public InternationalStringImpl( String str ) throws JAXRException {
        try{
            LocalizedString localizedString = new LocalizedStringImpl( str);
            addLocalizedString( localizedString );
         }catch( Throwable t){
            throw new JAXRException( t.getMessage());
        }
    }
    public InternationalStringImpl( Locale locale, String str) 
    throws  JAXRException {
        try{
            LocalizedStringImpl localizedString = 
                    new LocalizedStringImpl( locale, str);
            addLocalizedString( localizedString );
        }catch( Throwable t){
            t.printStackTrace();
            throw new JAXRException( t.getMessage());
        }
    }
    
    public void addLocalizedString( LocalizedString localizedString) 
    throws  JAXRException {
        Locale locale = localizedString.getLocale();
        String country = locale.getCountry();
        String lang = locale.getLanguage();
        map.put(lang+"_"+country, localizedString);
    }
    
    public void addLocalizedStrings(Collection collection) 
    throws  JAXRException {
    }
    
    public  LocalizedString getLocalizedString( Locale locale, String charset) 
    throws  JAXRException {
        String country = locale.getCountry();
        String lang = locale.getLanguage();
        return (LocalizedString)map.get(lang+"_"+country) ;
    }
    
    public Collection getLocalizedStrings() throws  JAXRException {
        return map.values();
    }
    
    public String getValue() throws  JAXRException {
        return getValue( Locale.getDefault());
    }
    
    public String getValue( Locale locale) throws  JAXRException {
        String country = locale.getCountry();
        String lang = locale.getLanguage();
        LocalizedString locstr =  (LocalizedString)map.get(lang+"_"+country) ;
        return locstr.getValue();
    }
    
    public void removeLocalizedString( LocalizedString localizedString) 
    throws  JAXRException {
        Locale locale = localizedString.getLocale();
        String country = locale.getCountry();
        String lang = locale.getLanguage();
        map.remove(lang+"_"+country) ;
    }
    
    public void removeLocalizedStrings(Collection collection) 
    throws  JAXRException {
        Iterator iter = collection.iterator();
        while( iter.hasNext()){
            removeLocalizedString( (LocalizedString)iter.next());
        }
    }
    
    public void setValue(String str) throws  JAXRException {
        Locale def = null;
        try{
            def = Locale.getDefault();
            setValue( def, str);
        }catch( Exception e){
            throw new JAXRException( "Apache JAXR Impl::", e);
        }
        
    }
    
    public void setValue( Locale locale, String str) 
    throws  JAXRException {
        addLocalizedString(new LocalizedStringImpl(locale,str));
    }
    
}
