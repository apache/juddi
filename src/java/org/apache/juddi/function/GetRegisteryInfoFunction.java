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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.request.GetRegistryInfo;
import org.apache.juddi.datatype.response.RegistryInfo;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.Release;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class GetRegisteryInfoFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(GetRegisteryInfoFunction.class);

  /**
   *
   */
  public GetRegisteryInfoFunction(RegistryEngine registry)
  {
    super(registry);
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    GetRegistryInfo request = (GetRegistryInfo)regObject;
    String generic = request.getGeneric();

    try
    {
      // create a new RegisteryInfo instance and 
      // add Registry name/value properties to it.

      RegistryInfo info = new RegistryInfo();
      info.setGeneric(generic);
      info.setOperator(Config.getOperator());
      info.addProperty("operatorName",Config.getStringProperty("juddi.operatorName"));
      info.addProperty("operatorEmailAddress",Config.getStringProperty("juddi.operatorEmailAddress"));
      info.addProperty("registryVersion",Release.getRegistryVersion());
      info.addProperty("registryLastModified",Release.getLastModified());
      info.addProperty("uddiVersion",Release.getUDDIVersion());
      
      return info;
    }
    catch(Exception ex)
    {
      log.error(ex);
      throw new RegistryException(ex);
    }
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
  {
    // initialize the registry
    RegistryEngine reg = new RegistryEngine();
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