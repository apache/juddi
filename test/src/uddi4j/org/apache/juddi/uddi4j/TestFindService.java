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
package org.apache.juddi.uddi4j;

import java.util.Vector;

import org.uddi4j.UDDIException;
import org.uddi4j.datatype.Name;
import org.uddi4j.datatype.business.BusinessEntity;
import org.uddi4j.response.BusinessDetail;
import org.uddi4j.response.BusinessInfo;
import org.uddi4j.response.BusinessList;
import org.uddi4j.transport.TransportException;
import org.uddi4j.util.FindQualifier;
import org.uddi4j.util.FindQualifiers;
import org.uddi4j.*;
import org.uddi4j.client.*;
import org.uddi4j.datatype.*;
import org.uddi4j.datatype.binding.*;
import org.uddi4j.datatype.business.*;
import org.uddi4j.datatype.service.*;
import org.uddi4j.datatype.tmodel.*;
import org.uddi4j.response.*;
import org.uddi4j.transport.*;
import org.uddi4j.util.*;



class TestFindService extends UDDITestBase
{
  private static final String ANIMAL_PROTOCOL_NAME="animal_protocol";
  private static final String ANIMAL_BUSINESS = "animal_business";
  private static final String ANIMAL_BUSINESS_SERVICE = "animal_business_service";
  private static final String ANIMAL_WSDL_URL = "http://localhost:8080/animal_java/services/Animal?wsdl";
  private static final String ANIMAL_SERVICE_NAME = "Animal Service";

  protected TModelDetail _animalProtocol_tModelDetail = null;
  protected BusinessEntity _animalBusinessEntity = null;
  protected boolean initOK=false;

  private boolean publish_animalProtocol() {
    boolean ret = false;
    TModel animal_tModel = new TModel();
    animal_tModel.setName(ANIMAL_PROTOCOL_NAME);
    animal_tModel.setDefaultDescriptionString("some description");
    OverviewDoc overviewdoc = new OverviewDoc();
    overviewdoc.setDefaultDescriptionString("animal protocol doc");
    overviewdoc.setOverviewURL("<url for animal doc>");
    java.util.Vector tModelVector = new Vector();
    tModelVector.add(animal_tModel);
    try
    {
      _animalProtocol_tModelDetail = proxy.save_tModel(token.getAuthInfoString(), tModelVector);
      ret = true;
    }
    catch (TransportException ex) {
      /**
        * @todo logging
       */
    }
    catch (UDDIException ex) {
      /**
        * @todo logging
       */
    }
    return ret;
  }

  private CategoryBag createCategoryBag() {
    CategoryBag catBag = new CategoryBag();
    KeyedReference kr = new KeyedReference(TModel.NAICS_TMODEL_KEY, "<FAKE NAICS NUMBER>");
    catBag.add(kr);
    return catBag;
  }

  private IdentifierBag createIdentifierBag() {
    IdentifierBag idBag = new IdentifierBag();
    KeyedReference kr = new KeyedReference(TModel.D_U_N_S_TMODEL_KEY , "<FAKE DUNS KEY>");
    idBag.add(kr);
    return idBag;
  }

  private boolean publish_animalBusiness() {
    boolean ret = false;
    java.util.Vector entities = new Vector();
    BusinessEntity be = new BusinessEntity("", ANIMAL_BUSINESS);
    IdentifierBag idBag = createIdentifierBag();
    be.setIdentifierBag(idBag);
    CategoryBag catBag = createCategoryBag();
    be.setCategoryBag(catBag);
    try {
      BusinessDetail businessDetail = proxy.save_business(token.getAuthInfoString(), entities);
      Vector businessEntities = businessDetail.getBusinessEntityVector();
      _animalBusinessEntity = (BusinessEntity)(businessEntities.elementAt(0));
      ret = true;
    }
    catch (TransportException ex) {
      /**
        * @todo logging
       */
    }
    catch (UDDIException ex) {
      /**
        * @todo logging
       */
    }
    return ret;
  }

  private AccessPoint createAccessPoint(String url) {
   AccessPoint animalAccessPoint = new AccessPoint();
   animalAccessPoint.setURLType("http");
   animalAccessPoint.setText(url);
   return animalAccessPoint;
 }

  private boolean publish_animalService(){
    boolean ret = false;
    BusinessService busService = new BusinessService();
    busService.setBusinessKey(_animalBusinessEntity.getBusinessKey());
    Name name = new Name(ANIMAL_BUSINESS_SERVICE);
    busService.setDefaultName(name);
    CategoryBag catBag = createCategoryBag();
    busService.setCategoryBag(catBag);
    BindingTemplate bindingTemplate = new BindingTemplate();
    TModelInstanceDetails tModelInstanceDetails = new TModelInstanceDetails();
    TModelInstanceInfo tModelInstanceInfo = new TModelInstanceInfo();
    tModelInstanceInfo.setTModelKey(((TModel)_animalProtocol_tModelDetail.getTModelVector().elementAt(0)).getTModelKey());
    tModelInstanceInfo.setDefaultDescriptionString("Animal Protocol tModel Instance");
    tModelInstanceDetails.add(tModelInstanceInfo);

    tModelInstanceInfo = new TModelInstanceInfo();
    tModelInstanceInfo.setTModelKey(((TModel)_animalProtocol_tModelDetail.getTModelVector().elementAt(0)).getTModelKey());

    /**
     * This creates the unique, well-defined tModel representing the service's WSDL
     */
    TModelDetail tmodelDetail = null;
    try {
      tmodelDetail = publishWSDL_tModel();
    }
    catch (TransportException ex) {
      /**
       * @todo cleanup ??
       */
      return  ret;
    }
    catch (UDDIException ex) {
      /**
       * @todo cleanup ??
       */
      return ret;
    }
    tModelInstanceInfo = new TModelInstanceInfo();
    tModelInstanceInfo.setTModelKey(((TModel)tmodelDetail.getTModelVector().elementAt(0)).getTModelKey());
    tModelInstanceInfo.setDefaultDescriptionString("Animal Service tModel instance");
    tModelInstanceDetails.add(tModelInstanceInfo);

    /**
     * Associate the set of tModels with the Service's BindingTemplate
     */
    bindingTemplate.setTModelInstanceDetails(tModelInstanceDetails);

    /**
     * The binding template needs an [ accessPoint | hostRedirector ]
     */
    AccessPoint animalAccessPoint = createAccessPoint(ANIMAL_WSDL_URL.substring(0,ANIMAL_WSDL_URL.lastIndexOf("?")));
    bindingTemplate.setAccessPoint(animalAccessPoint);
    BindingTemplates bindingTemplates = new BindingTemplates();

    bindingTemplate.setDefaultDescriptionString("SOAP Binding");
    bindingTemplates.add(bindingTemplate);

    busService.setBindingTemplates(bindingTemplates);

    // save the service
    java.util.Vector servicesVector = new Vector();
    servicesVector.add(busService);
    try {
      proxy.save_service(token.getAuthInfoString(), servicesVector);
    }
    catch(TransportException ex) {
      return ret;
    }
    catch(UDDIException ex) {
      /**
       * @todo cleanup ??
       */
      return ret;
    }

    ret = true;

    return ret;
  }

  private TModelDetail publishWSDL_tModel()
      throws TransportException, UDDIException
  {
    TModel animalWSDL_tModel = new TModel();
    /**
     * UDDI best practices recommend a specific format for the OverviewDocument
     * for this tModel.
     */
    OverviewDoc animalWSDL_overviewDoc = new OverviewDoc();
    animalWSDL_overviewDoc.setDefaultDescriptionString("WSDL source document");
    animalWSDL_overviewDoc.setOverviewURL(ANIMAL_WSDL_URL);
    animalWSDL_tModel.setOverviewDoc(animalWSDL_overviewDoc);

    KeyedReference kr = new KeyedReference("uuid-org:types","wsdlSpec");
    kr.setTModelKey(TModel.TYPES_TMODEL_KEY);
    CategoryBag catBag = new CategoryBag();
    catBag.add(kr);
    animalWSDL_tModel.setCategoryBag(catBag);

    animalWSDL_tModel.setName(ANIMAL_SERVICE_NAME);
    animalWSDL_tModel.setDefaultDescriptionString("WSDL description of Animal protocol interface");
    java.util.Vector tModelVector = new Vector();
    tModelVector.add(animalWSDL_tModel);
    TModelDetail animal_tModelDetail = proxy.save_tModel(token.getAuthInfoString(), tModelVector);
    return animal_tModelDetail;
  }


  public void setUp() {
    if (!publish_animalProtocol()) {
      return;
    }
    if (!publish_animalBusiness()) {
      return;
    }
    if (!publish_animalService()) {
      return;
    }
    initOK = true;
  }

  public void tearDown() {
  }

  public void testCases() {
  }

}
