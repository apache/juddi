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
package org.apache.juddi.handler;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.Address;
import org.apache.juddi.datatype.AddressLine;
import org.apache.juddi.datatype.BindingKey;
import org.apache.juddi.datatype.BusinessKey;
import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.DiscoveryURL;
import org.apache.juddi.datatype.DiscoveryURLs;
import org.apache.juddi.datatype.Email;
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.OverviewDoc;
import org.apache.juddi.datatype.OverviewURL;
import org.apache.juddi.datatype.PersonName;
import org.apache.juddi.datatype.Phone;
import org.apache.juddi.datatype.ServiceKey;
import org.apache.juddi.datatype.SharedRelationships;
import org.apache.juddi.datatype.TModelBag;
import org.apache.juddi.datatype.TModelKey;
import org.apache.juddi.datatype.UploadRegister;
import org.apache.juddi.datatype.assertion.PublisherAssertion;
import org.apache.juddi.datatype.binding.AccessPoint;
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.binding.BindingTemplates;
import org.apache.juddi.datatype.binding.HostingRedirector;
import org.apache.juddi.datatype.binding.InstanceDetails;
import org.apache.juddi.datatype.binding.InstanceParms;
import org.apache.juddi.datatype.binding.TModelInstanceDetails;
import org.apache.juddi.datatype.binding.TModelInstanceInfo;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.business.BusinessEntityExt;
import org.apache.juddi.datatype.business.Contact;
import org.apache.juddi.datatype.business.Contacts;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.datatype.publisher.PublisherID;
import org.apache.juddi.datatype.request.AddPublisherAssertions;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.request.DeleteBinding;
import org.apache.juddi.datatype.request.DeleteBusiness;
import org.apache.juddi.datatype.request.DeletePublisher;
import org.apache.juddi.datatype.request.DeletePublisherAssertions;
import org.apache.juddi.datatype.request.DeleteService;
import org.apache.juddi.datatype.request.DeleteTModel;
import org.apache.juddi.datatype.request.DiscardAuthToken;
import org.apache.juddi.datatype.request.FindBinding;
import org.apache.juddi.datatype.request.FindBusiness;
import org.apache.juddi.datatype.request.FindPublisher;
import org.apache.juddi.datatype.request.FindQualifier;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.datatype.request.FindRelatedBusinesses;
import org.apache.juddi.datatype.request.FindService;
import org.apache.juddi.datatype.request.FindTModel;
import org.apache.juddi.datatype.request.GetAssertionStatusReport;
import org.apache.juddi.datatype.request.GetAuthToken;
import org.apache.juddi.datatype.request.GetBindingDetail;
import org.apache.juddi.datatype.request.GetBusinessDetail;
import org.apache.juddi.datatype.request.GetBusinessDetailExt;
import org.apache.juddi.datatype.request.GetPublisherAssertions;
import org.apache.juddi.datatype.request.GetPublisherDetail;
import org.apache.juddi.datatype.request.GetRegisteredInfo;
import org.apache.juddi.datatype.request.GetRegistryInfo;
import org.apache.juddi.datatype.request.GetServiceDetail;
import org.apache.juddi.datatype.request.GetTModelDetail;
import org.apache.juddi.datatype.request.SaveBinding;
import org.apache.juddi.datatype.request.SaveBusiness;
import org.apache.juddi.datatype.request.SavePublisher;
import org.apache.juddi.datatype.request.SaveService;
import org.apache.juddi.datatype.request.SaveTModel;
import org.apache.juddi.datatype.request.SetPublisherAssertions;
import org.apache.juddi.datatype.request.ValidateValues;
import org.apache.juddi.datatype.response.AssertionStatusItem;
import org.apache.juddi.datatype.response.AssertionStatusReport;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.datatype.response.BindingDetail;
import org.apache.juddi.datatype.response.BusinessDetail;
import org.apache.juddi.datatype.response.BusinessDetailExt;
import org.apache.juddi.datatype.response.BusinessInfo;
import org.apache.juddi.datatype.response.BusinessInfos;
import org.apache.juddi.datatype.response.BusinessList;
import org.apache.juddi.datatype.response.CompletionStatus;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.ErrInfo;
import org.apache.juddi.datatype.response.KeysOwned;
import org.apache.juddi.datatype.response.Property;
import org.apache.juddi.datatype.response.PublisherAssertions;
import org.apache.juddi.datatype.response.PublisherDetail;
import org.apache.juddi.datatype.response.PublisherInfo;
import org.apache.juddi.datatype.response.PublisherInfos;
import org.apache.juddi.datatype.response.PublisherList;
import org.apache.juddi.datatype.response.RegisteredInfo;
import org.apache.juddi.datatype.response.RegistryInfo;
import org.apache.juddi.datatype.response.RelatedBusinessInfo;
import org.apache.juddi.datatype.response.RelatedBusinessInfos;
import org.apache.juddi.datatype.response.RelatedBusinessesList;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.datatype.response.ServiceDetail;
import org.apache.juddi.datatype.response.ServiceInfo;
import org.apache.juddi.datatype.response.ServiceInfos;
import org.apache.juddi.datatype.response.ServiceList;
import org.apache.juddi.datatype.response.TModelDetail;
import org.apache.juddi.datatype.response.TModelInfo;
import org.apache.juddi.datatype.response.TModelInfos;
import org.apache.juddi.datatype.response.TModelList;
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.datatype.service.BusinessServices;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.error.RegistryException;

/**
 * Holds a static HashMap linking the string representation of operations to
 * instantances of the appropriate maker class (BusinessDetail to
 * BusinessDetailHandler). Returns a reference to an instance of a maker object.
 * HandlerMaker follows the Singleton pattern (GoF p.127).  Use getInstance
 * instead of the 'new' operator to get an instance of this class.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class HandlerMaker
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(HandlerMaker.class);

  // singleton HandlerMaker
  private static HandlerMaker maker = null;

  // static table of Handlers
  private HashMap handlers = null;

  /**
   *
   */
  private HandlerMaker()
  {
    handlers = new HashMap();
    AbstractHandler handler = null;

    handler = new AccessPointHandler(this);
    handlers.put(AccessPoint.class.getName().toLowerCase(),handler);
    handlers.put(AccessPointHandler.TAG_NAME.toLowerCase(),handler);

    handler = new AddPublisherAssertionsHandler(this);
    handlers.put(AddPublisherAssertions.class.getName().toLowerCase(),handler);
    handlers.put(AddPublisherAssertionsHandler.TAG_NAME.toLowerCase(),handler);

    handler = new AddressHandler(this);
    handlers.put(Address.class.getName().toLowerCase(),handler);
    handlers.put(AddressHandler.TAG_NAME.toLowerCase(),handler);

    handler = new AddressLineHandler(this);
    handlers.put(AddressLine.class.getName().toLowerCase(),handler);
    handlers.put(AddressLineHandler.TAG_NAME.toLowerCase(),handler);

    handler = new AssertionStatusItemHandler(this);
    handlers.put(AssertionStatusItem.class.getName().toLowerCase(),handler);
    handlers.put(AssertionStatusItemHandler.TAG_NAME.toLowerCase(),handler);

    handler = new AssertionStatusReportHandler(this);
    handlers.put(AssertionStatusReport.class.getName().toLowerCase(),handler);
    handlers.put(AssertionStatusReportHandler.TAG_NAME.toLowerCase(),handler);

    handler = new AuthInfoHandler(this);
    handlers.put(AuthInfo.class.getName().toLowerCase(),handler);
    handlers.put(AuthInfoHandler.TAG_NAME.toLowerCase(),handler);

    handler = new AuthTokenHandler(this);
    handlers.put(AuthToken.class.getName().toLowerCase(),handler);
    handlers.put(AuthTokenHandler.TAG_NAME.toLowerCase(),handler);

    handler = new BindingDetailHandler(this);
    handlers.put(BindingDetail.class.getName().toLowerCase(),handler);
    handlers.put(BindingDetailHandler.TAG_NAME.toLowerCase(),handler);

    handler = new BindingKeyHandler(this);
    handlers.put(BindingKey.class.getName().toLowerCase(),handler);
    handlers.put(BindingKeyHandler.TAG_NAME.toLowerCase(),handler);

    handler = new BindingTemplateHandler(this);
    handlers.put(BindingTemplate.class.getName().toLowerCase(),handler);
    handlers.put(BindingTemplateHandler.TAG_NAME.toLowerCase(),handler);

    handler = new BindingTemplatesHandler(this);
    handlers.put(BindingTemplates.class.getName().toLowerCase(),handler);
    handlers.put(BindingTemplatesHandler.TAG_NAME.toLowerCase(),handler);

    handler = new BusinessDetailHandler(this);
    handlers.put(BusinessDetail.class.getName().toLowerCase(),handler);
    handlers.put(BusinessDetailHandler.TAG_NAME.toLowerCase(),handler);

    handler = new BusinessDetailExtHandler(this);
    handlers.put(BusinessDetailExt.class.getName().toLowerCase(),handler);
    handlers.put(BusinessDetailExtHandler.TAG_NAME.toLowerCase(),handler);

    handler = new BusinessEntityHandler(this);
    handlers.put(BusinessEntity.class.getName().toLowerCase(),handler);
    handlers.put(BusinessEntityHandler.TAG_NAME.toLowerCase(),handler);

    handler = new BusinessEntityExtHandler(this);
    handlers.put(BusinessEntityExt.class.getName().toLowerCase(),handler);
    handlers.put(BusinessEntityExtHandler.TAG_NAME.toLowerCase(),handler);

    handler = new BusinessInfoHandler(this);
    handlers.put(BusinessInfo.class.getName().toLowerCase(),handler);
    handlers.put(BusinessInfoHandler.TAG_NAME.toLowerCase(),handler);

    handler = new BusinessInfosHandler(this);
    handlers.put(BusinessInfos.class.getName().toLowerCase(),handler);
    handlers.put(BusinessInfosHandler.TAG_NAME.toLowerCase(),handler);

    handler = new BusinessKeyHandler(this);
    handlers.put(BusinessKey.class.getName().toLowerCase(),handler);
    handlers.put(BusinessKeyHandler.TAG_NAME.toLowerCase(),handler);

    handler = new BusinessListHandler(this);
    handlers.put(BusinessList.class.getName().toLowerCase(),handler);
    handlers.put(BusinessListHandler.TAG_NAME.toLowerCase(),handler);

    handler = new BusinessServiceHandler(this);
    handlers.put(BusinessService.class.getName().toLowerCase(),handler);
    handlers.put(BusinessServiceHandler.TAG_NAME.toLowerCase(),handler);

    handler = new BusinessServicesHandler(this);
    handlers.put(BusinessServices.class.getName().toLowerCase(),handler);
    handlers.put(BusinessServicesHandler.TAG_NAME.toLowerCase(),handler);

    handler = new CategoryBagHandler(this);
    handlers.put(CategoryBag.class.getName().toLowerCase(),handler);
    handlers.put(CategoryBagHandler.TAG_NAME.toLowerCase(),handler);

    handler = new CompletionStatusHandler(this);
    handlers.put(CompletionStatus.class.getName().toLowerCase(),handler);
    handlers.put(CompletionStatusHandler.TAG_NAME.toLowerCase(),handler);

    handler = new ContactHandler(this);
    handlers.put(Contact.class.getName().toLowerCase(),handler);
    handlers.put(ContactHandler.TAG_NAME.toLowerCase(),handler);

    handler = new ContactsHandler(this);
    handlers.put(Contacts.class.getName().toLowerCase(),handler);
    handlers.put(ContactsHandler.TAG_NAME.toLowerCase(),handler);

    handler = new DeleteBindingHandler(this);
    handlers.put(DeleteBinding.class.getName().toLowerCase(),handler);
    handlers.put(DeleteBindingHandler.TAG_NAME.toLowerCase(),handler);

    handler = new DeleteBusinessHandler(this);
    handlers.put(DeleteBusiness.class.getName().toLowerCase(),handler);
    handlers.put(DeleteBusinessHandler.TAG_NAME.toLowerCase(),handler);

    handler = new DeletePublisherAssertionsHandler(this);
    handlers.put(DeletePublisherAssertions.class.getName().toLowerCase(),handler);
    handlers.put(DeletePublisherAssertionsHandler.TAG_NAME.toLowerCase(),handler);

    handler = new DeletePublisherHandler(this);
    handlers.put(DeletePublisher.class.getName().toLowerCase(),handler);
    handlers.put(DeletePublisherHandler.TAG_NAME.toLowerCase(),handler);

    handler = new DeleteServiceHandler(this);
    handlers.put(DeleteService.class.getName().toLowerCase(),handler);
    handlers.put(DeleteServiceHandler.TAG_NAME.toLowerCase(),handler);

    handler = new DeleteTModelHandler(this);
    handlers.put(DeleteTModel.class.getName().toLowerCase(),handler);
    handlers.put(DeleteTModelHandler.TAG_NAME.toLowerCase(),handler);

    handler = new DescriptionHandler(this);
    handlers.put(Description.class.getName().toLowerCase(),handler);
    handlers.put(DescriptionHandler.TAG_NAME.toLowerCase(),handler);

    handler = new DiscardAuthTokenHandler(this);
    handlers.put(DiscardAuthToken.class.getName().toLowerCase(),handler);
    handlers.put(DiscardAuthTokenHandler.TAG_NAME.toLowerCase(),handler);

    handler = new DiscoveryURLHandler(this);
    handlers.put(DiscoveryURL.class.getName().toLowerCase(),handler);
    handlers.put(DiscoveryURLHandler.TAG_NAME.toLowerCase(),handler);

    handler = new DiscoveryURLsHandler(this);
    handlers.put(DiscoveryURLs.class.getName().toLowerCase(),handler);
    handlers.put(DiscoveryURLsHandler.TAG_NAME.toLowerCase(),handler);

    handler = new DispositionReportHandler(this);
    handlers.put(DispositionReport.class.getName().toLowerCase(),handler);
    handlers.put(DispositionReportHandler.TAG_NAME.toLowerCase(),handler);

    handler = new EmailHandler(this);
    handlers.put(Email.class.getName().toLowerCase(),handler);
    handlers.put(EmailHandler.TAG_NAME.toLowerCase(),handler);

    handler = new ErrInfoHandler(this);
    handlers.put(ErrInfo.class.getName().toLowerCase(),handler);
    handlers.put(ErrInfoHandler.TAG_NAME.toLowerCase(),handler);

    handler = new FindBindingHandler(this);
    handlers.put(FindBinding.class.getName().toLowerCase(),handler);
    handlers.put(FindBindingHandler.TAG_NAME.toLowerCase(),handler);

    handler = new FindBusinessHandler(this);
    handlers.put(FindBusiness.class.getName().toLowerCase(),handler);
    handlers.put(FindBusinessHandler.TAG_NAME.toLowerCase(),handler);

    handler = new FindPublisherHandler(this);
    handlers.put(FindPublisher.class.getName().toLowerCase(),handler);
    handlers.put(FindPublisherHandler.TAG_NAME.toLowerCase(),handler);

    handler = new FindQualifierHandler(this);
    handlers.put(FindQualifier.class.getName().toLowerCase(),handler);
    handlers.put(FindQualifierHandler.TAG_NAME.toLowerCase(),handler);

    handler = new FindQualifiersHandler(this);
    handlers.put(FindQualifiers.class.getName().toLowerCase(),handler);
    handlers.put(FindQualifiersHandler.TAG_NAME.toLowerCase(),handler);

    handler = new FindRelatedBusinessesHandler(this);
    handlers.put(FindRelatedBusinesses.class.getName().toLowerCase(),handler);
    handlers.put(FindRelatedBusinessesHandler.TAG_NAME.toLowerCase(),handler);

    handler = new FindServiceHandler(this);
    handlers.put(FindService.class.getName().toLowerCase(),handler);
    handlers.put(FindServiceHandler.TAG_NAME.toLowerCase(),handler);

    handler = new FindTModelHandler(this);
    handlers.put(FindTModel.class.getName().toLowerCase(),handler);
    handlers.put(FindTModelHandler.TAG_NAME.toLowerCase(),handler);

    handler = new GetAssertionStatusReportHandler(this);
    handlers.put(GetAssertionStatusReport.class.getName().toLowerCase(),handler);
    handlers.put(GetAssertionStatusReportHandler.TAG_NAME.toLowerCase(),handler);

    handler = new GetAuthTokenHandler(this);
    handlers.put(GetAuthToken.class.getName().toLowerCase(),handler);
    handlers.put(GetAuthTokenHandler.TAG_NAME.toLowerCase(),handler);

    handler = new GetBindingDetailHandler(this);
    handlers.put(GetBindingDetail.class.getName().toLowerCase(),handler);
    handlers.put(GetBindingDetailHandler.TAG_NAME.toLowerCase(),handler);

    handler = new GetBusinessDetailHandler(this);
    handlers.put(GetBusinessDetail.class.getName().toLowerCase(),handler);
    handlers.put(GetBusinessDetailHandler.TAG_NAME.toLowerCase(),handler);

    handler = new GetBusinessDetailExtHandler(this);
    handlers.put(GetBusinessDetailExt.class.getName().toLowerCase(),handler);
    handlers.put(GetBusinessDetailExtHandler.TAG_NAME.toLowerCase(),handler);

    handler = new GetPublisherAssertionsHandler(this);
    handlers.put(GetPublisherAssertions.class.getName().toLowerCase(),handler);
    handlers.put(GetPublisherAssertionsHandler.TAG_NAME.toLowerCase(),handler);

    handler = new GetPublisherDetailHandler(this);
    handlers.put(GetPublisherDetail.class.getName().toLowerCase(),handler);
    handlers.put(GetPublisherDetailHandler.TAG_NAME.toLowerCase(),handler);

    handler = new GetRegisteredInfoHandler(this);
    handlers.put(GetRegisteredInfo.class.getName().toLowerCase(),handler);
    handlers.put(GetRegisteredInfoHandler.TAG_NAME.toLowerCase(),handler);

    handler = new GetRegistryInfoHandler(this);
    handlers.put(GetRegistryInfo.class.getName().toLowerCase(),handler);
    handlers.put(GetRegistryInfoHandler.TAG_NAME.toLowerCase(),handler);

    handler = new GetServiceDetailHandler(this);
    handlers.put(GetServiceDetail.class.getName().toLowerCase(),handler);
    handlers.put(GetServiceDetailHandler.TAG_NAME.toLowerCase(),handler);

    handler = new GetTModelDetailHandler(this);
    handlers.put(GetTModelDetail.class.getName().toLowerCase(),handler);
    handlers.put(GetTModelDetailHandler.TAG_NAME.toLowerCase(),handler);

    handler = new HostingRedirectorHandler(this);
    handlers.put(HostingRedirector.class.getName().toLowerCase(),handler);
    handlers.put(HostingRedirectorHandler.TAG_NAME.toLowerCase(),handler);

    handler = new IdentifierBagHandler(this);
    handlers.put(IdentifierBag.class.getName().toLowerCase(),handler);
    handlers.put(IdentifierBagHandler.TAG_NAME.toLowerCase(),handler);

    handler = new InstanceDetailsHandler(this);
    handlers.put(InstanceDetails.class.getName().toLowerCase(),handler);
    handlers.put(InstanceDetailsHandler.TAG_NAME.toLowerCase(),handler);

    handler = new InstanceParmsHandler(this);
    handlers.put(InstanceParms.class.getName().toLowerCase(),handler);
    handlers.put(InstanceParmsHandler.TAG_NAME.toLowerCase(),handler);

    handler = new KeyedReferenceHandler(this);
    handlers.put(KeyedReference.class.getName().toLowerCase(),handler);
    handlers.put(KeyedReferenceHandler.TAG_NAME.toLowerCase(),handler);

    handler = new KeysOwnedHandler(this);
    handlers.put(KeysOwned.class.getName().toLowerCase(),handler);
    handlers.put(KeysOwnedHandler.TAG_NAME.toLowerCase(),handler);

    handler = new NameHandler(this);
    handlers.put(Name.class.getName().toLowerCase(),handler);
    handlers.put(NameHandler.TAG_NAME.toLowerCase(),handler);

    handler = new OverviewDocHandler(this);
    handlers.put(OverviewDoc.class.getName().toLowerCase(),handler);
    handlers.put(OverviewDocHandler.TAG_NAME.toLowerCase(),handler);

    handler = new OverviewURLHandler(this);
    handlers.put(OverviewURL.class.getName().toLowerCase(),handler);
    handlers.put(OverviewURLHandler.TAG_NAME.toLowerCase(),handler);

    handler = new PersonNameHandler(this);
    handlers.put(PersonName.class.getName().toLowerCase(),handler);
    handlers.put(PersonNameHandler.TAG_NAME.toLowerCase(),handler);

    handler = new PhoneHandler(this);
    handlers.put(Phone.class.getName().toLowerCase(),handler);
    handlers.put(PhoneHandler.TAG_NAME.toLowerCase(),handler);

    handler = new PropertyHandler(this);
    handlers.put(Property.class.getName().toLowerCase(),handler);
    handlers.put(PropertyHandler.TAG_NAME.toLowerCase(),handler);

    handler = new PublisherAssertionHandler(this);
    handlers.put(PublisherAssertion.class.getName().toLowerCase(),handler);
    handlers.put(PublisherAssertionHandler.TAG_NAME.toLowerCase(),handler);

    handler = new PublisherAssertionsHandler(this);
    handlers.put(PublisherAssertions.class.getName().toLowerCase(),handler);
    handlers.put(PublisherAssertionsHandler.TAG_NAME.toLowerCase(),handler);

    handler = new PublisherDetailHandler(this);
    handlers.put(PublisherDetail.class.getName().toLowerCase(),handler);
    handlers.put(PublisherDetailHandler.TAG_NAME.toLowerCase(),handler);

    handler = new PublisherHandler(this);
    handlers.put(Publisher.class.getName().toLowerCase(),handler);
    handlers.put(PublisherHandler.TAG_NAME.toLowerCase(),handler);

    handler = new PublisherIDHandler(this);
    handlers.put(PublisherID.class.getName().toLowerCase(),handler);
    handlers.put(PublisherIDHandler.TAG_NAME.toLowerCase(),handler);

    handler = new PublisherInfoHandler(this);
    handlers.put(PublisherInfo.class.getName().toLowerCase(),handler);
    handlers.put(PublisherInfoHandler.TAG_NAME.toLowerCase(),handler);

    handler = new PublisherInfosHandler(this);
    handlers.put(PublisherInfos.class.getName().toLowerCase(),handler);
    handlers.put(PublisherInfosHandler.TAG_NAME.toLowerCase(),handler);

    handler = new PublisherListHandler(this);
    handlers.put(PublisherList.class.getName().toLowerCase(),handler);
    handlers.put(PublisherListHandler.TAG_NAME.toLowerCase(),handler);

    handler = new RegisteredInfoHandler(this);
    handlers.put(RegisteredInfo.class.getName().toLowerCase(),handler);
    handlers.put(RegisteredInfoHandler.TAG_NAME.toLowerCase(),handler);

    handler = new RegistryInfoHandler(this);
    handlers.put(RegistryInfo.class.getName().toLowerCase(),handler);
    handlers.put(RegistryInfoHandler.TAG_NAME.toLowerCase(),handler);

    handler = new RelatedBusinessInfoHandler(this);
    handlers.put(RelatedBusinessInfo.class.getName().toLowerCase(),handler);
    handlers.put(RelatedBusinessInfoHandler.TAG_NAME.toLowerCase(),handler);

    handler = new RelatedBusinessInfosHandler(this);
    handlers.put(RelatedBusinessInfos.class.getName().toLowerCase(),handler);
    handlers.put(RelatedBusinessInfosHandler.TAG_NAME.toLowerCase(),handler);

    handler = new RelatedBusinessesListHandler(this);
    handlers.put(RelatedBusinessesList.class.getName().toLowerCase(),handler);
    handlers.put(RelatedBusinessesListHandler.TAG_NAME.toLowerCase(),handler);

    handler = new ResultHandler(this);
    handlers.put(Result.class.getName().toLowerCase(),handler);
    handlers.put(ResultHandler.TAG_NAME.toLowerCase(),handler);

    handler = new SaveBindingHandler(this);
    handlers.put(SaveBinding.class.getName().toLowerCase(),handler);
    handlers.put(SaveBindingHandler.TAG_NAME.toLowerCase(),handler);

    handler = new SaveBusinessHandler(this);
    handlers.put(SaveBusiness.class.getName().toLowerCase(),handler);
    handlers.put(SaveBusinessHandler.TAG_NAME.toLowerCase(),handler);

    handler = new SavePublisherHandler(this);
    handlers.put(SavePublisher.class.getName().toLowerCase(),handler);
    handlers.put(SavePublisherHandler.TAG_NAME.toLowerCase(),handler);

    handler = new SaveServiceHandler(this);
    handlers.put(SaveService.class.getName().toLowerCase(),handler);
    handlers.put(SaveServiceHandler.TAG_NAME.toLowerCase(),handler);

    handler = new SaveTModelHandler(this);
    handlers.put(SaveTModel.class.getName().toLowerCase(),handler);
    handlers.put(SaveTModelHandler.TAG_NAME.toLowerCase(),handler);

    handler = new ServiceDetailHandler(this);
    handlers.put(ServiceDetail.class.getName().toLowerCase(),handler);
    handlers.put(ServiceDetailHandler.TAG_NAME.toLowerCase(),handler);

    handler = new ServiceInfoHandler(this);
    handlers.put(ServiceInfo.class.getName().toLowerCase(),handler);
    handlers.put(ServiceInfoHandler.TAG_NAME.toLowerCase(),handler);

    handler = new ServiceInfosHandler(this);
    handlers.put(ServiceInfos.class.getName().toLowerCase(),handler);
    handlers.put(ServiceInfosHandler.TAG_NAME.toLowerCase(),handler);

    handler = new ServiceKeyHandler(this);
    handlers.put(ServiceKey.class.getName().toLowerCase(),handler);
    handlers.put(ServiceKeyHandler.TAG_NAME.toLowerCase(),handler);

    handler = new ServiceListHandler(this);
    handlers.put(ServiceList.class.getName().toLowerCase(),handler);
    handlers.put(ServiceListHandler.TAG_NAME.toLowerCase(),handler);

    handler = new SetPublisherAssertionsHandler(this);
    handlers.put(SetPublisherAssertions.class.getName().toLowerCase(),handler);
    handlers.put(SetPublisherAssertionsHandler.TAG_NAME.toLowerCase(),handler);

    handler = new SharedRelationshipsHandler(this);
    handlers.put(SharedRelationships.class.getName().toLowerCase(),handler);
    handlers.put(SharedRelationshipsHandler.TAG_NAME.toLowerCase(),handler);

    handler = new TModelHandler(this);
    handlers.put(TModel.class.getName().toLowerCase(),handler);
    handlers.put(TModelHandler.TAG_NAME.toLowerCase(),handler);

    handler = new TModelBagHandler(this);
    handlers.put(TModelBag.class.getName().toLowerCase(),handler);
    handlers.put(TModelBagHandler.TAG_NAME.toLowerCase(),handler);

    handler = new TModelDetailHandler(this);
    handlers.put(TModelDetail.class.getName().toLowerCase(),handler);
    handlers.put(TModelDetailHandler.TAG_NAME.toLowerCase(),handler);

    handler = new TModelInfoHandler(this);
    handlers.put(TModelInfo.class.getName().toLowerCase(),handler);
    handlers.put(TModelInfoHandler.TAG_NAME.toLowerCase(),handler);

    handler = new TModelInfosHandler(this);
    handlers.put(TModelInfos.class.getName().toLowerCase(),handler);
    handlers.put(TModelInfosHandler.TAG_NAME.toLowerCase(),handler);

    handler = new TModelInstanceDetailsHandler(this);
    handlers.put(TModelInstanceDetails.class.getName().toLowerCase(),handler);
    handlers.put(TModelInstanceDetailsHandler.TAG_NAME.toLowerCase(),handler);

    handler = new TModelInstanceInfoHandler(this);
    handlers.put(TModelInstanceInfo.class.getName().toLowerCase(),handler);
    handlers.put(TModelInstanceInfoHandler.TAG_NAME.toLowerCase(),handler);

    handler = new TModelKeyHandler(this);
    handlers.put(TModelKey.class.getName().toLowerCase(),handler);
    handlers.put(TModelKeyHandler.TAG_NAME.toLowerCase(),handler);

    handler = new TModelListHandler(this);
    handlers.put(TModelList.class.getName().toLowerCase(),handler);
    handlers.put(TModelListHandler.TAG_NAME.toLowerCase(),handler);

    handler = new UploadRegisterHandler(this);
    handlers.put(UploadRegister.class.getName().toLowerCase(),handler);
    handlers.put(UploadRegisterHandler.TAG_NAME.toLowerCase(),handler);

    handler = new ValidateValuesHandler(this);
    handlers.put(ValidateValues.class.getName().toLowerCase(),handler);
    handlers.put(ValidateValuesHandler.TAG_NAME.toLowerCase(),handler);

    handler = new RegistryExceptionHandler(this);
    handlers.put(RegistryException.class.getName().toLowerCase(),handler);
    handlers.put(RegistryExceptionHandler.TAG_NAME.toLowerCase(),handler);
  }

  /**
   *
   */
  public static HandlerMaker getInstance()
  {
    if (maker == null)
      maker = createInstance();

    return maker;
  }

  /**
   *
   */
  private static synchronized HandlerMaker createInstance()
  {
    if (maker == null)
      maker = new HandlerMaker();

    return maker;
  }

  /**
   *
   */
  public final AbstractHandler lookup(String elementName)
  {
    if (elementName == null)
    {
      log.error("Element lookup attempted using a null " +
        "element name");
      return null;
    }

    // element name keys are stored in lower
    // case only so we don't need to be concerned
    // about inconsistencies in SOAP providers

    String key = elementName.toLowerCase();

    AbstractHandler handler = (AbstractHandler)handlers.get(key);
    if (handler == null)
      log.error("can't find a handler for element type \"" +
        elementName + "\" using key: "+key);

    return handler;
  }
}
