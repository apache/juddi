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
package org.apache.juddi.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class RegistryResourceBundle
{
  private static final String BASE_MESSAGE_BUNDLE = "org.apache.juddi.i18n.MessagesBundle";
  
  private static ResourceBundle bundle = null;
  
  public static String getString(String key)
  {
    return getBundle().getString(key);
  }
  
  private static ResourceBundle getBundle()
  {
    if (bundle == null)
      bundle = createBundle();
    return bundle;
  }
  
  private static synchronized ResourceBundle createBundle()
  {
    if (bundle != null)
      return bundle;
    
    String language = Config.getStringProperty(RegistryEngine.PROPNAME_I18N_LANGUAGE_CODE);   
    if ((language == null) || (language.trim().length() == 0))
      language = RegistryEngine.DEFAULT_I18N_LANGUAGE_CODE;
    
    String country = Config.getStringProperty(RegistryEngine.PROPNAME_I18N_COUNTRY_CODE);
    if ((country == null) || (country.trim().length() == 0))
      country = RegistryEngine.DEFAULT_I18N_COUNTRY_CODE;
    
    bundle = ResourceBundle.getBundle(
        BASE_MESSAGE_BUNDLE,
        new Locale(language,country));
    
    return bundle;
  }
  
  
  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
  {
    System.out.println(RegistryResourceBundle.getString("E_authTokenRequired"));
  }
}