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
package org.apache.juddi.function;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.request.ValidateValues;
import org.apache.juddi.error.RegistryException;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class ValidateValuesFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(SetPublisherAssertionsFunction.class);

  /**
   *
   */
  public ValidateValuesFunction()
  {
    super();
  }

  /**
   *
   * @param RegistryObject
   * @return RegistryObject
   * @throws RegistryException
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    ValidateValues request = (ValidateValues)regObject;
    String generic = request.getGeneric();

    Vector businessVector = request.getBusinessEntityVector();
    if ((businessVector != null) && (businessVector.size() > 0))
      validateBusinessVector(businessVector);

    Vector serviceVector = request.getBusinessServiceVector();
    if ((serviceVector != null) && (serviceVector.size() > 0))
      validateServiceVector(serviceVector);

    Vector tModelVector = request.getTModelVector();
    if ((tModelVector != null) && (tModelVector.size() > 0))
      validateTModelVector(tModelVector);

    return null;
  }

  /**
   *
   */
  private RegistryObject validateBusinessVector(Vector businessVector)
  {
    if (businessVector != null)
      return null;
    else
      return null;
  }

  /**
   *
   */
  private RegistryObject validateServiceVector(Vector serviceVector)
  {
    if (serviceVector != null)
      return null;
    else
      return null;
  }

  /**
   *
   */
  private RegistryObject validateTModelVector(Vector tModelVector)
  {
    if (tModelVector != null)
      return null;
    else
      return null;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
  {
    // initialize the registry
    org.apache.juddi.registry.RegistryEngine reg = org.apache.juddi.registry.RegistryEngine.getInstance();
    reg.init();

    try
    {
    }
    catch (Exception ex)
    {
      // write execption to the console
      ex.printStackTrace();
    }
    finally
    {
      // destroy the registry
      reg.dispose();
    }
  }
}