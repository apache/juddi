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
import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.TModelBag;
import org.apache.juddi.datatype.request.FindQualifier;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.datatype.request.FindService;
import org.apache.juddi.datatype.response.ServiceInfos;
import org.apache.juddi.datatype.response.ServiceList;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.error.NameTooLongException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.TooManyOptionsException;
import org.apache.juddi.error.UnsupportedException;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class FindServiceFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(FindServiceFunction.class);

  /**
   *
   */
  public FindServiceFunction(RegistryEngine registry)
  {
    super(registry);
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    FindService request = (FindService)regObject;
    String generic = request.getGeneric();
    String businessKey = request.getBusinessKey();
    Vector nameVector = request.getNameVector();
    CategoryBag categoryBag = request.getCategoryBag();
    TModelBag tModelBag = request.getTModelBag();
    FindQualifiers qualifiers = request.getFindQualifiers();
    int maxRows = request.getMaxRows();

    // first make sure we need to continue with this request. If
    // no arguments were passed in then we'll simply return
    // an empty ServiceList (aka "a zero match result set").
    if (((nameVector == null) || (nameVector.size() == 0))  &&
       ((categoryBag == null) || (categoryBag.size() == 0)) &&
       ((tModelBag == null)   || (tModelBag.size() == 0)))
    {
      ServiceList list = new ServiceList();
      list.setServiceInfos(new ServiceInfos());
      list.setGeneric(generic);
      list.setOperator(Config.getOperator());
      list.setTruncated(false);
      return list;
    }

    // Validate CategoryBag and (if neccessary) add TModelKey for: uddiorg:general_keywords
    if (categoryBag != null)
    {
      Vector keyedRefVector = categoryBag.getKeyedReferenceVector();
      if (keyedRefVector != null)
      {
        int vectorSize = keyedRefVector.size();
        if (vectorSize > 0)
        {
          for (int i=0; i<vectorSize; i++)
          {
            KeyedReference keyedRef = (KeyedReference)keyedRefVector.elementAt(i);
            String key = keyedRef.getTModelKey();
            
            // A null or zero-length tModelKey is treated as 
            // though the tModelKey for uddiorg:general_keywords 
            // had been specified.
            //
            if ((key == null) || (key.trim().length() == 0))
              keyedRef.setTModelKey(TModel.GENERAL_KEYWORDS_TMODEL_KEY);
          }
        }
      }
    }            
    
    // aquire a jUDDI datastore instance
    DataStore dataStore = DataStoreFactory.getDataStore();

    try
    {
      dataStore.beginTrans();

      // validate the 'name' parameters as much as possible up-front before
      // calling into the data layer for relational validation.
      if (nameVector != null)
      {
        // only allowed to specify a maximum of 5 names (implementation
        // dependent).  This value is configurable in jUDDI.
        int maxNames = Config.getMaxNameElementsAllowed();
        if ((nameVector != null) && (nameVector.size() > maxNames))
          throw new TooManyOptionsException("max_names= "+maxNames);

        // names can not exceed the maximum character length specified by the
        // UDDI specification (v2.0 specifies a max character length of 255). This
        // value is configurable in jUDDI.
        int maxNameLength = Config.getMaxNameLengthAllowed();
        for (int i=0; i<nameVector.size(); i++)
        {
          String name = ((Name)nameVector.elementAt(i)).getValue();
           if (name.length() > maxNameLength)
            throw new NameTooLongException(name+" (max="+maxNameLength+")");
        }
      }

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
              throw new UnsupportedException("findQualifier="+qValue);
          }
        }
      }

      Vector infoVector = null;
      boolean truncatedResults = false;

      // perform the search for matching business services (return only keys in requested order)
      Vector keyVector = dataStore.findService(businessKey,nameVector,categoryBag,tModelBag,qualifiers);
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

        // iterate through the business server keys fetching
        // each associated ServiceInfo in sequence.
        infoVector = new Vector(rowCount);
        for (int i=0; i<rowCount; i++)
          infoVector.addElement(dataStore.fetchServiceInfo((String)keyVector.elementAt(i)));
      }

      dataStore.commit();

      // create a new ServiceInfos instance and stuff
      // the new Vector of ServiceInfos into it.
      ServiceInfos infos = new ServiceInfos();
      infos.setServiceInfoVector(infoVector);

      // create a new ServiceList instance and stuff
      // the new serviceInfos instance into it.
      ServiceList list = new ServiceList();
      list.setGeneric(generic);
      list.setServiceInfos(infos);
      list.setOperator(Config.getOperator());
      list.setTruncated(truncatedResults);
      return list;
    }
    catch(TooManyOptionsException tmoex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.info(tmoex.getMessage());
      throw (RegistryException)tmoex;
    }
    catch(NameTooLongException ntlex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.info(ntlex.getMessage());
      throw (RegistryException)ntlex;
    }
    catch(UnsupportedException suppex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.info(suppex.getMessage());
      throw (RegistryException)suppex;
    }
    catch(RegistryException regex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.error(regex);
      throw (RegistryException)regex;
    }
    catch(Exception ex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.error(ex);
      throw new RegistryException(ex);
    }
    finally
    {
      if (dataStore != null)
        dataStore.release();
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
      TModelBag bag = new TModelBag();
      bag.addTModelKey("uuid:4C2A8920-BE0C-11D7-8920-8DA7324351E5");

      FindService request = new FindService();
      request.setGeneric("2.0");
      request.setTModelBag(bag);

      // invoke the server
      ServiceList response = (ServiceList) (new FindServiceFunction(reg).execute(request));

      System.out.println(response);
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