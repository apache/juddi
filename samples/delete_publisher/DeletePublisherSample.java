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

import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.datatype.response.PublisherDetail;
import org.apache.juddi.proxy.RegistryProxy;
import org.apache.juddi.registry.Registry;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class DeletePublisherSample
{
  public static void main(String[] args)
  {
    Registry registry = new RegistryProxy();

    try
    {
      // execute a GetAuthToken request
      AuthToken token = registry.getAuthToken("juddi", "password");
      AuthInfo authInfo = token.getAuthInfo();

      // create a publisher with administrative privileges
      Publisher publisher = new Publisher("BlueNoteIdentifier", "Blue Note", true);
      publisher.setEnabled(true);
      // create a publisher to remove
      Publisher perisher = new Publisher("removeMe", "Remove Me", false);
      // put the Publisher objects into a Vector
      Vector vector = new Vector(2);
      vector.add(publisher);
      vector.add(perisher);

      // make the request
      PublisherDetail detail = registry.savePublisher(authInfo, vector);

      System.out.println("publishers saved = " + detail.getPublisherVector().size());

      // get an authToken using the publisher with administrative privileges
      token = registry.getAuthToken("BlueNoteIdentifier", "password");
      authInfo = token.getAuthInfo();

      // create a vector of strings containing the
      String publisherID = "removeMe";
      vector = new Vector(1);
      vector.add(publisherID);
      registry.deletePublisher(authInfo, vector);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }
}