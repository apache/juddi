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
import org.apache.juddi.datatype.DiscoveryURL;
import org.apache.juddi.datatype.DiscoveryURLs;
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.TModelBag;
import org.apache.juddi.datatype.request.FindBusiness;
import org.apache.juddi.datatype.request.FindQualifier;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.datatype.response.BusinessInfos;
import org.apache.juddi.datatype.response.BusinessList;
import org.apache.juddi.error.NameTooLongException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.TooManyOptionsException;
import org.apache.juddi.error.UnsupportedException;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * "This [FindBusiness] API call returns a businessList on success. This
 * structure contains information about each matching business, and
 * summaries of the businessServices exposed by the individual businesses.
 * If a tModelBag was used in the search, the resulting serviceInfos
 * structure will only reflect data for the businessServices that
 * actually contained a matching bindingTemplate. In the event that
 * no matches were located for the specified criteria, a businessList
 * structure with zero businessInfo structures is returned. If no
 * arguments are passed, a zero-match result set will be returned."
 *
 * "In the event of a large number of matches, (as determined by each
 * Operator Site), or if the number of matches exceeds the value of the
 * 'maxRows' attribute, the Operator Site will truncate the result set.
 * If this occurs, the businessList will contain the 'truncated' attribute
 * with the value 'true'".
 *
 * From UDDI Version 2 Programmers API Specification (Pg. 18)
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class FindBusinessFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(FindBusinessFunction.class);

  /**
   *
   */
  public FindBusinessFunction(RegistryEngine registry)
  {
    super(registry);
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    FindBusiness request = (FindBusiness) regObject;
    String generic = request.getGeneric();
    Vector nameVector = request.getNameVector();
    DiscoveryURLs discoveryURLs = request.getDiscoveryURLs();
    IdentifierBag identifierBag = request.getIdentifierBag();
    CategoryBag categoryBag = request.getCategoryBag();
    TModelBag tModelBag = request.getTModelBag();
    FindQualifiers qualifiers = request.getFindQualifiers();
    int maxRows = request.getMaxRows();

    // make sure we need to continue with this request. If
    // no arguments were passed in then we'll simply return
    // an empty BusinessList (aka "a zero match result set").
    if (((nameVector == null) || (nameVector.size() == 0))
      && ((discoveryURLs == null) || (discoveryURLs.size() == 0))
      && ((identifierBag == null) || (identifierBag.size() == 0))
      && ((categoryBag == null) || (categoryBag.size() == 0))
      && ((tModelBag == null) || (tModelBag.size() == 0)))
    {
      BusinessList list = new BusinessList();
      list.setGeneric(generic);
      list.setBusinessInfos(new BusinessInfos());
      list.setOperator(Config.getOperator());
      list.setTruncated(false);
      return list;
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
          throw new TooManyOptionsException("max=" + maxNames);

        // names can not exceed the maximum character length specified by the
        // UDDI specification (v2.0 specifies a max character length of 255). This
        // value is configurable in jUDDI.
        int maxNameLength = Config.getMaxNameLengthAllowed();
        for (int i = 0; i < nameVector.size(); i++)
        {
          String name = ((Name) nameVector.elementAt(i)).getValue();
          if (name.length() > maxNameLength)
            throw new NameTooLongException(name + 
            		"(max=" + maxNameLength + ")");
        }
      }

      // validate the 'qualifiers' parameter as much as possible up-front before
      // calling into the data layer for relational validation.
      if (qualifiers != null)
      {
        Vector qVector = qualifiers.getFindQualifierVector();
        if ((qVector!=null) && (qVector.size() > 0))
        {
          for (int i = 0; i < qVector.size(); i++)
          {
            FindQualifier qualifier = (FindQualifier) qVector.elementAt(i);
            String qValue = qualifier.getValue();

            if ((!qValue.equals(FindQualifier.EXACT_NAME_MATCH))
              && (!qValue.equals(FindQualifier.CASE_SENSITIVE_MATCH))
              && (!qValue.equals(FindQualifier.OR_ALL_KEYS))
              && (!qValue.equals(FindQualifier.OR_LIKE_KEYS))
              && (!qValue.equals(FindQualifier.AND_ALL_KEYS))
              && (!qValue.equals(FindQualifier.SORT_BY_NAME_ASC))
              && (!qValue.equals(FindQualifier.SORT_BY_NAME_DESC))
              && (!qValue.equals(FindQualifier.SORT_BY_DATE_ASC))
              && (!qValue.equals(FindQualifier.SORT_BY_DATE_DESC))
              && (!qValue.equals(FindQualifier.SERVICE_SUBSET))
              && (!qValue
                .equals(FindQualifier.COMBINE_CATEGORY_BAGS)))
              throw new UnsupportedException(
                "findQualifier=" + qValue);
          }
        }
      }

      Vector infoVector = null;
      boolean truncatedResults = false;

      // perform the search for matching business entities (returns only business keys in the order requested)
      Vector keyVector =
        dataStore.findBusiness(
          nameVector,
          discoveryURLs,
          identifierBag,
          categoryBag,
          tModelBag,
          qualifiers);
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

        // iterate through the business entity keys fetching
        // each associated BusinessInfo in sequence.
        infoVector = new Vector(rowCount);
        for (int i = 0; i < rowCount; i++)
          infoVector.addElement(
            dataStore.fetchBusinessInfo(
              (String) keyVector.elementAt(i)));
      }

      dataStore.commit();

      // create a new BusinessInfos instance and stuff
      // the new Vector of BusinessInfos into it.
      BusinessInfos infos = new BusinessInfos();
      infos.setBusinessInfoVector(infoVector);

      // create a new BusinessList instance and stuff
      // the new businessInfos instance into it.
      BusinessList list = new BusinessList();
      list.setBusinessInfos(infos);
      list.setGeneric(generic);
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
      // create a request
      Vector nameVector = new Vector(5);
      nameVector.addElement(new Name("InflexionPoint"));
      nameVector.addElement(new Name("SteveViens.com"));
      nameVector.addElement(new Name("Liberty Mutual"));
      nameVector.addElement(new Name("Bowstreet"));
      nameVector.addElement(new Name("CMGi"));
      //nameVector.addElement(new Name("BusinessName #6 (1 over the maximum)"));

      Vector qualifierVector = new Vector(1);
      qualifierVector.add(
        new FindQualifier(FindQualifier.EXACT_NAME_MATCH));
      //qualifierVector.add(new FindQualifier("anInvalidFindQualifier"));

      FindQualifiers qualifiers = new FindQualifiers();
      qualifiers.setFindQualifierVector(qualifierVector);

      Vector categoryVector = new Vector();
      categoryVector.addElement(new KeyedReference("name1", "value1"));
      categoryVector.addElement(new KeyedReference("name2", "value2"));
      categoryVector.addElement(new KeyedReference("name3", "value3"));

      CategoryBag categoryBag = new CategoryBag();
      categoryBag.setKeyedReferenceVector(categoryVector);

      Vector identifierVector = new Vector();
      identifierVector.addElement(new KeyedReference("name1", "value1"));
      identifierVector.addElement(new KeyedReference("name1", "value1"));
      identifierVector.addElement(new KeyedReference("name1", "value1"));

      IdentifierBag identifierBag = new IdentifierBag();
      identifierBag.setKeyedReferenceVector(identifierVector);

      Vector tModelKeyVector = new Vector();
      tModelKeyVector.addElement(
        new String("6240b6f0-d4dd-4091-851b-d59fedbd0491"));
      tModelKeyVector.addElement(
        new String("ee0a154b-43ed-47be-b24f-878ab2956a31"));

      TModelBag tModelBag = new TModelBag();
      tModelBag.setTModelKeyVector(tModelKeyVector);

      Vector discoveryURLVector = new Vector();
      discoveryURLVector.addElement(new DiscoveryURL());
      discoveryURLVector.addElement(new DiscoveryURL());
      discoveryURLVector.addElement(new DiscoveryURL());

      DiscoveryURLs discoveryURLs = new DiscoveryURLs();
      discoveryURLs.setDiscoveryURLVector(discoveryURLVector);

      FindBusiness request = new FindBusiness();
      request.setNameVector(nameVector);
      request.setMaxRows(10);
      request.setCategoryBag(categoryBag);
      request.setIdentifierBag(identifierBag);
      request.setTModelBag(tModelBag);
      request.setDiscoveryURLs(discoveryURLs);
      request.setFindQualifiers(qualifiers);

      // invoke the server
      BusinessList response =
        (BusinessList) (new FindBusinessFunction(reg).execute(request));
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