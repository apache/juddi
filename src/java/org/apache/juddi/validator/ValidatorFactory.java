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
package org.apache.juddi.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.Loader;

/**
 * Implementation of Factory pattern used to create an implementation of
 * the org.apache.juddi.validation.Validation interface.
 *
 * The name of the Validation implementation to create is passed to the
 * getValidation method.  If a null value is passed then the default
 * Validation implementation "org.apache.juddi.validation.DefaultValidation" 
 * is created.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class ValidatorFactory
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(ValidatorFactory.class);

  // Validation property key & default implementation
  private static final String IMPL_KEY = "juddi.validation";
  private static final String DEFAULT_IMPL = "org.apache.juddi.validation.DefaultValidation";

  // the shared Validation instance
  private static Validator validation = null;

  /**
   * Returns a new instance of a ValidationFactory.
   *
   * @return Validation
   */
  public static Validator getValidation()
  {
    if (validation == null)
      validation = createValidation();
    return validation;
  }

  /**
   * Returns a new instance of a Validation.
   *
   * @return Validation
   */
  private static synchronized Validator createValidation()
  {
    if (validation != null)
      return validation;

    // grab class name of the Validation implementation to create
    String className = Config.getStringProperty(IMPL_KEY,DEFAULT_IMPL);

    // write the Validation implementation name to the log
    log.debug("Validation Implementation = " + className);

    Class implClass = null;
    try
    {
      // Use Loader to locate & load the Validation implementation
      implClass = Loader.getClassForName(className);
    }
    catch(ClassNotFoundException e)
    {
      log.error("The specified Validation class '" + className +
        "' was not found in classpath.");
      log.error(e);
    }

    try
    {
      // try to instantiate the Validation implementation
      validation = (Validator)implClass.newInstance();
    }
    catch(Exception e)
    {
      log.error("Exception while attempting to instantiate the " +
        "implementation of Validation: " + implClass.getName() +
        "\n" + e.getMessage());
      log.error(e);
    }

    return validation;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
    throws Exception
  {
      Validator validation = ValidatorFactory.getValidation();
      if (validation != null)
      {
        System.out.println("Got a Validation instance: "+validation.getClass().getName());

        if (validation.validate(new CategoryBag()))
        	System.out.println("The objct was successfully validated.");
        else
        	System.out.println("Sorry validation failed.");
      }
      else
        System.out.println("Couldn't get a Validation instance.");
  }
}