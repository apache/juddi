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

import org.apache.juddi.datatype.response.TModelInfo;
import org.apache.juddi.datatype.response.TModelInfos;
import org.apache.juddi.datatype.response.TModelList;
import org.apache.juddi.proxy.RegistryProxy;
import org.apache.juddi.registry.IRegistry;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class FindTModelSample
{
  public static void main(String[] args)
  {
    IRegistry registry = new RegistryProxy();

    try
    {
      TModelList list = registry.findTModel("uddi-org",null,null,null,0);
      TModelInfos infos = list.getTModelInfos();
      Vector vector = infos.getTModelInfoVector();

      for (int i=0; i<vector.size(); i++)
      {
        TModelInfo info = (TModelInfo)vector.elementAt(i);
        System.out.println(info.getNameValue());
      }
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }
}