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
import org.apache.juddi.datastore.DataStore;
import org.apache.juddi.datastore.DataStoreFactory;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.TModelBag;
import org.apache.juddi.datatype.request.FindBinding;
import org.apache.juddi.datatype.request.FindQualifier;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.datatype.response.BindingDetail;
import org.apache.juddi.error.InvalidKeyPassedException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnsupportedException;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class FindBindingFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(FindBindingFunction.class);

  /**
   *
   */
  public FindBindingFunction(RegistryEngine registry)
  {
    super(registry);
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    // extract individual parameters
    FindBinding request = (FindBinding)regObject;
    String generic = request.getGeneric();
    String serviceKey = request.getServiceKey();
    TModelBag tModelBag = request.getTModelBag();
    FindQualifiers qualifiers = request.getFindQualifiers();
    int maxRows = request.getMaxRows();

    // first make sure we need to continue with this request. If
    // no arguments were passed in then we'll simply return
    // an empty ServiceList (aka "a zero match result set").
    if ((tModelBag == null) || (tModelBag.size() == 0))
    {
      BindingDetail detail = new BindingDetail();
      detail.setGeneric(generic);
      detail.setBindingTemplateVector(null);
      detail.setOperator(Config.getOperator());
      detail.setTruncated(false);
      return detail;
    }

    // aquire a jUDDI datastore instance
    DataStoreFactory factory = DataStoreFactory.getFactory();
    DataStore dataStore = factory.acquireDataStore();

    try
    {
      dataStore.beginTrans();

      // a find_binding request MUST include a service_key attribute
      if ((serviceKey == null) || (serviceKey.length() == 0))
        throw new InvalidKeyPassedException("ServiceKey = "+serviceKey);

      // validate the 'qualifiers' parameter as much as possible up-front before
      // calling into the data layer for relational validation.
      if (qualifiers != null)
      {
        Vector qVector = qualifiers.getFindQualifierVector();
        if ((qVector!=null) && (qVector.size() > 0))
        {
          for (int i=0; i<qVector.size(); i++)
          {
            FindQualifier qualifier = (FindQualifier)qVector.elementAt(i);
            String qValue = qualifier.getValue();

            if ((!qValue.equals(FindQualifier.EXACT_NAME_MATCH)) &&
                (!qValue.equals(FindQualifier.CASE_SENSITIVE_MATCH)) &&
                (!qValue.equals(FindQualifier.OR_ALL_KEYS)) &&
                (!qValue.equals(FindQualifier.OR_LIKE_KEYS)) &&
                (!qValue.equals(FindQualifier.AND_ALL_KEYS)) &&
                (!qValue.equals(FindQualifier.SORT_BY_NAME_ASC)) &&
                (!qValue.equals(FindQualifier.SORT_BY_NAME_DESC)) &&
                (!qValue.equals(FindQualifier.SORT_BY_DATE_ASC)) &&
                (!qValue.equals(FindQualifier.SORT_BY_DATE_DESC)) &&
                (!qValue.equals(FindQualifier.SERVICE_SUBSET)) &&
                (!qValue.equals(FindQualifier.COMBINE_CATEGORY_BAGS)))
              throw new UnsupportedException("FindQualifier: "+qValue);
          }
        }
      }

      Vector bindingVector = null;
      boolean truncatedResults = false;

      // perform the search for matching binding templates (return only keys in requested order)
      Vector keyVector = dataStore.findBinding(serviceKey,tModelBag,qualifiers);
      if ((keyVector != null) && (keyVector.size() > 0))
      {
        // if a maxRows value has been specified and it's less than
        // the number of rows we are about to return then only return
        // maxRows specified.
        int rowCount = keyVector.size();
        if ((maxRows > 0) && (maxRows < rowCount))
        {
          rowCount = maxRows;
          truncatedResults = true;
        }

        // iterate through the binding templates keys fetching
        // each associated BindingTemplate in sequence.
        bindingVector = new Vector(rowCount);
        for (int i=0; i<rowCount; i++)
          bindingVector.addElement(dataStore.fetchBinding((String)keyVector.elementAt(i)));
      }

      dataStore.commit();

      // create a new BindingDetail instance and stuff
      // the new bindingTemplateVector into it.
      BindingDetail detail = new BindingDetail();
      detail.setBindingTemplateVector(bindingVector);
      detail.setGeneric(generic);
      detail.setOperator(Config.getOperator());
      detail.setTruncated(truncatedResults);
      return detail;
    }
    catch(Exception ex)
    {
      // we must rollback for *any* exception
      try { dataStore.rollback(); }
      catch(Exception e) { }

      // write to the log
      log.error(ex);

      // prep UDDIException to throw
      if (ex instanceof RegistryException)
        throw (RegistryException)ex;
      else
        throw new RegistryException(ex);
    }
    finally
    {
      factory.releaseDataStore(dataStore);
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