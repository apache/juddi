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

<<<<<<< GetAssertionStatusReportSample.java
import org.apache.juddi.proxy.RegistryProxy;
import org.apache.juddi.registry.IRegistry;
=======
import org.apache.juddi.proxy.RegistryProxy;
import org.apache.juddi.registry.Registry;
>>>>>>> 1.3

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class GetAssertionStatusReportSample
{
  public static void main(String[] args)
  {
<<<<<<< GetAssertionStatusReportSample.java
    IRegistry registry = new RegistryProxy();
=======
    Registry registry = new RegistryProxy();
>>>>>>> 1.3

    try
    {
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }
}