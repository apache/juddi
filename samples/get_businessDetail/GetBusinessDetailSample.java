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

import java.util.Vector;

import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.response.BusinessDetail;
import org.apache.juddi.proxy.RegistryProxy;
import org.apache.juddi.registry.Registry;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class GetBusinessDetailSample
{
  public static void main(String[] args)
  {
    Registry registry = new RegistryProxy();

    try
    {
      Vector businessKeys = new Vector();
      businessKeys.add("51042720-B629-11D7-A720-E7A2DAC9BD96");

      BusinessDetail detail = registry.getBusinessDetail(businessKeys);
      Vector vector = detail.getBusinessEntityVector();

      for (int i=0; i<vector.size(); i++)
      {
        BusinessEntity business = (BusinessEntity)vector.elementAt(i);
        System.out.println("Authorized Name = "+business.getAuthorizedName());
      }
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }
}