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
import org.apache.juddi.datatype.request.SaveTModel;
import org.apache.juddi.datatype.response.TModelDetail;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.error.InvalidKeyPassedException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnsupportedException;
import org.apache.juddi.error.UserMismatchException;
import org.apache.juddi.util.Config;
import org.apache.juddi.uuidgen.UUIDGen;
import org.apache.juddi.uuidgen.UUIDGenFactory;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class SaveTModelFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(SaveTModelFunction.class);

  /**
   *
   */
  public SaveTModelFunction()
  {
    super();
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    SaveTModel request = (SaveTModel)regObject;
    String generic = request.getGeneric();
    AuthInfo authInfo = request.getAuthInfo();
    Vector tModelVector = request.getTModelVector();
    Vector uploadRegVector = request.getUploadRegisterVector();
    UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();

    // UploadRegistry functionality is not currently supported.
    if ((uploadRegVector != null) && (uploadRegVector.size() > 0))
      throw new UnsupportedException("Saving TModels via an" +
        "UploadRegistry is not supported.");

    // aquire a jUDDI datastore instance
    DataStoreFactory dataFactory = DataStoreFactory.getFactory();
    DataStore dataStore = dataFactory.acquireDataStore();

    try
    {
      dataStore.beginTrans();

      // validate authentication parameters
      Publisher publisher = getPublisher(authInfo,dataStore);
      String publisherID = publisher.getPublisherID();
      String authorizedName = publisher.getName();

      // validate request parameters
      for (int i=0; i<tModelVector.size(); i++)
      {
        // move the TModel into a form we can work with easily
        TModel tModel = (TModel)tModelVector.elementAt(i);
        String tModelKey = tModel.getTModelKey();

        // If a TModelKey was specified then make sure it's a valid one.
        if (((tModelKey != null) && (tModelKey.length() > 0)) && (!dataStore.isValidTModelKey(tModelKey)))
          throw new InvalidKeyPassedException("TModelKey: "+tModelKey);

        // If a TModelKey was specified then make sure 'publisherID' controls it.
        if (((tModelKey != null) && (tModelKey.length() > 0)) && !dataStore.isTModelPublisher(tModelKey,publisherID))
          throw new UserMismatchException("TModelKey: "+tModelKey);
      }

      for (int i=0; i<tModelVector.size(); i++)
      {
        // move the TModel into a form we can work with easily
        TModel tModel = (TModel)tModelVector.elementAt(i);
        String tModelKey = tModel.getTModelKey();

        // If the new TModel has a TModelKey then it must already exists
        // so delete the old one. It a TModelKey isn't specified then
        // this is a new TModel so create a new TModelKey for it.
        if ((tModelKey != null) && (tModelKey.length() > 0))
          dataStore.deleteTModel(tModelKey);
        else
          tModel.setTModelKey("uuid:"+uuidgen.uuidgen());

        // Everything checks out so let's save it. First store
        // 'authorizedName' and 'operator' values in each TModel.
        tModel.setAuthorizedName(authorizedName);
        tModel.setOperator(org.apache.juddi.util.Config.getOperator());
        dataStore.saveTModel(tModel,publisherID);
      }

      dataStore.commit();

      TModelDetail detail = new TModelDetail();
      detail.setGeneric(generic);
      detail.setOperator(Config.getOperator());
      detail.setTruncated(false);
      detail.setTModelVector(tModelVector);
      return detail;
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
      dataFactory.releaseDataStore(dataStore);
    }
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


