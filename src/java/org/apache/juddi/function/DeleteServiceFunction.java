/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.juddi.function;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datastore.DataStore;
import org.apache.juddi.datastore.DataStoreFactory;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.request.DeleteService;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.error.InvalidKeyPassedException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UserMismatchException;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class DeleteServiceFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(DeleteServiceFunction.class);

  /**
   *
   */
  public DeleteServiceFunction()
  {
    super();
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    // extract individual parameters
    DeleteService request = (DeleteService)regObject;
    String generic = request.getGeneric();
    AuthInfo authInfo = request.getAuthInfo();
    Vector serviceKeyVector = request.getServiceKeyVector();

    // aquire a jUDDI datastore instance
    DataStoreFactory factory = DataStoreFactory.getFactory();
    DataStore dataStore = factory.acquireDataStore();

    try
    {
      dataStore.beginTrans();

      // validate authentication parameters
      Publisher publisher = getPublisher(authInfo,dataStore);
      String publisherID = publisher.getPublisherID();

      // validate request parameters
      for (int i=0; i<serviceKeyVector.size(); i++)
      {
        // grab the next key from the vector
        String serviceKey = (String)serviceKeyVector.elementAt(i);

        // check that this business server really exists.
        // If not then throw an InvalidKeyPassedException.
        if ((serviceKey == null) || (serviceKey.length() == 0) ||
            (!dataStore.isValidServiceKey(serviceKey)))
          throw new InvalidKeyPassedException("ServiceKey: "+serviceKey);

        // check to make sure that 'authorizedName' controls the
        // business entity that this server belongs to. If not
        // then throw a UserMismatchException.
        if (!dataStore.isServicePublisher(serviceKey,publisherID))
          throw new UserMismatchException("ServiceKey: "+serviceKey);
      }

      // delete the BusinessServices
      for (int i=0; i<serviceKeyVector.size(); i++)
      {
        String serviceKey = (String)serviceKeyVector.elementAt(i);
        dataStore.deleteService(serviceKey);

        log.info("Publisher '"+publisherID+"' deleted BusinessService with key: "+serviceKey);
      }

      dataStore.commit();
    }
    catch(Exception ex)
    {
      // we must rollback for *any* exception
      try { dataStore.rollback(); }
      catch(Exception e) { }

      // write to the log
      log.error(ex);

      // prep RegistryFault to throw
      if (ex instanceof RegistryException)
        throw (RegistryException)ex;
      else
        throw new RegistryException(ex);
    }
    finally
    {
      factory.releaseDataStore(dataStore);
    }

    // didn't encounter an exception so let's create
    // and return a successfull DispositionReport
    DispositionReport dispRpt = new DispositionReport();
    dispRpt.setGeneric(generic);
    dispRpt.setOperator(Config.getOperator());
    dispRpt.addResult(new Result(Result.E_SUCCESS));
    return dispRpt;
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
      // create a request
      DeleteService request = new DeleteService();

      // invoke the server
      DispositionReport response = (DispositionReport)(new DeleteServiceFunction().execute(request));
      System.out.println("errno: "+response.toString());
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