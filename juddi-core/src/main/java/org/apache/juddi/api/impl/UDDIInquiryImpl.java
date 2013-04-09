/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
 *
 */

package org.apache.juddi.api.impl;

import java.util.List;
import java.util.UUID;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.util.InquiryQuery;
import org.apache.juddi.api.util.PublicationQuery;
import org.apache.juddi.api.util.QueryStatus;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.config.Property;
import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.model.TempKey;
import org.apache.juddi.query.util.FindQualifiers;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.InvalidKeyPassedException;
import org.apache.juddi.validation.ValidateInquiry;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.FindRelatedBusinesses;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetOperationalInfo;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.OperationalInfos;
import org.uddi.api_v3.RelatedBusinessesList;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.ServiceList;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelList;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;


/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@WebService(serviceName="UDDIInquiryService",   
			endpointInterface="org.uddi.v3_service.UDDIInquiryPortType",
			targetNamespace = "urn:uddi-org:v3_service")
public class UDDIInquiryImpl extends AuthenticatedService implements UDDIInquiryPortType {


    private static Log log = LogFactory.getLog(UDDIInquiryImpl.class);
    private UDDIServiceCounter serviceCounter;
        
    public UDDIInquiryImpl() {
        super();
        serviceCounter = ServiceCounterLifecycleResource.getServiceCounter(this.getClass());
    }
	
    public BindingDetail findBinding(FindBinding body)
			throws DispositionReportFaultMessage {
                long startTime = System.nanoTime();
                try {
                    new ValidateInquiry(null).validateFindBinding(body);
                } catch (DispositionReportFaultMessage drfm) {
                    long procTime = System.nanoTime() - startTime;
                    serviceCounter.update(InquiryQuery.FIND_BINDING, QueryStatus.FAILED, procTime);                      
                    throw drfm;
                }

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			if (isAuthenticated())
				this.getEntityPublisher(em, body.getAuthInfo());

			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());

			List<?> keysFound = InquiryHelper.findBinding(body, findQualifiers, em);

			if (keysFound.size() == 0) {
			    if (body.getServiceKey() != null) {
			        // Check that we were passed a valid serviceKey per
        			// 5.1.12.4 of the UDDI v3 spec
        			String serviceKey = body.getServiceKey();
        			org.apache.juddi.model.BusinessService modelBusinessService = null;
        			try {
        				em.find(org.apache.juddi.model.BusinessService.class, serviceKey);
        			} catch (ClassCastException e) {}
	                if (modelBusinessService == null)
	                    throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.ServiceNotFound", serviceKey));

			    }
			}
			BindingDetail result = InquiryHelper.getBindingDetailFromKeys(body, findQualifiers, em, keysFound);
			tx.rollback();
                        long procTime = System.nanoTime() - startTime;
                        serviceCounter.update(InquiryQuery.FIND_BINDING, QueryStatus.SUCCESS, procTime);                      

			return result;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}

	
    public BusinessList findBusiness(FindBusiness body)
			throws DispositionReportFaultMessage {
                long startTime = System.nanoTime();
                try {
                    new ValidateInquiry(null).validateFindBusiness(body);
                } catch (DispositionReportFaultMessage drfm) {
                    long procTime = System.nanoTime() - startTime;
                    serviceCounter.update(InquiryQuery.FIND_BUSINESS, QueryStatus.FAILED, procTime);                      
                    throw drfm;
                }

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			if (isAuthenticated())
				this.getEntityPublisher(em, body.getAuthInfo());

			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());

			List<?> keysFound = InquiryHelper.findBusiness(body, findQualifiers, em);

			BusinessList result = InquiryHelper.getBusinessListFromKeys(body, findQualifiers, em, keysFound);

			tx.rollback();
                        long procTime = System.nanoTime() - startTime;
                        serviceCounter.update(InquiryQuery.FIND_BUSINESS, QueryStatus.SUCCESS, procTime);                      

			return result;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}

	
    public RelatedBusinessesList findRelatedBusinesses(FindRelatedBusinesses body)
			throws DispositionReportFaultMessage {
                long startTime = System.nanoTime();
                try {
                    new ValidateInquiry(null).validateFindRelatedBusinesses(body, false);
                } catch (DispositionReportFaultMessage drfm) {
                    long procTime = System.nanoTime() - startTime;
                    serviceCounter.update(InquiryQuery.FIND_RELATEDBUSINESSES, QueryStatus.FAILED, procTime);                      
                    throw drfm;
                }

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			if (isAuthenticated())
				this.getEntityPublisher(em, body.getAuthInfo());

			// TODO: findQualifiers aren't really used for this call, except maybe for sorting.  Sorting must be done in Java due to the retrieval method used.  Right now
			// no sorting is performed.
			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());

			RelatedBusinessesList result = InquiryHelper.getRelatedBusinessesList(body, em);

			tx.rollback();
                        long procTime = System.nanoTime() - startTime;
                        serviceCounter.update(InquiryQuery.FIND_RELATEDBUSINESSES, QueryStatus.SUCCESS, procTime);                      

			return result;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}

	
    public ServiceList findService(FindService body)
			throws DispositionReportFaultMessage {
                long startTime = System.nanoTime();
                try {
                    new ValidateInquiry(null).validateFindService(body);
                } catch (DispositionReportFaultMessage drfm) {
                    long procTime = System.nanoTime() - startTime;
                    serviceCounter.update(InquiryQuery.FIND_SERVICE, QueryStatus.FAILED, procTime);                      
                    throw drfm;
                }

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			if (isAuthenticated())
				this.getEntityPublisher(em, body.getAuthInfo());

			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());

			List<?> keysFound = InquiryHelper.findService(body, findQualifiers, em);

		        if (keysFound.size() == 0) {
		            if (body.getBusinessKey() != null) {
		                // Check that we were passed a valid businessKey per
    	                // 5.1.12.4 of the UDDI v3 spec
    	                String businessKey = body.getBusinessKey();
    	                org.apache.juddi.model.BusinessEntity modelBusinessEntity = null;
    	                try {
    	                	modelBusinessEntity = em.find(org.apache.juddi.model.BusinessEntity.class, businessKey);
    	                } catch (ClassCastException e) {}
    	                if (modelBusinessEntity == null) {
    	                    throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.BusinessNotFound", businessKey));
    	                }
		            }
		        }

			ServiceList result = InquiryHelper.getServiceListFromKeys(body, findQualifiers, em, keysFound);

			tx.rollback();
                        long procTime = System.nanoTime() - startTime;
                        serviceCounter.update(InquiryQuery.FIND_SERVICE, QueryStatus.SUCCESS, procTime);                      

			return result;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}

	
    public TModelList findTModel(FindTModel body)
			throws DispositionReportFaultMessage {
                long startTime = System.nanoTime();
                try {
                    new ValidateInquiry(null).validateFindTModel(body, false);
                } catch (DispositionReportFaultMessage drfm) {
                    long procTime = System.nanoTime() - startTime;
                    serviceCounter.update(InquiryQuery.FIND_TMODEL, QueryStatus.FAILED, procTime);                      
                    throw drfm;
                }
                    
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			if (isAuthenticated())
				this.getEntityPublisher(em, body.getAuthInfo());

			org.apache.juddi.query.util.FindQualifiers findQualifiers = new org.apache.juddi.query.util.FindQualifiers();
			findQualifiers.mapApiFindQualifiers(body.getFindQualifiers());

			List<?> keysFound = InquiryHelper.findTModel(body, findQualifiers, em);

			TModelList result = InquiryHelper.getTModelListFromKeys(body, findQualifiers, em, keysFound);

			tx.rollback();
                        long procTime = System.nanoTime() - startTime;
                        serviceCounter.update(InquiryQuery.FIND_TMODEL, QueryStatus.SUCCESS, procTime);                      

			return result;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}

	
    public BindingDetail getBindingDetail(GetBindingDetail body)
			throws DispositionReportFaultMessage {
                long startTime = System.nanoTime();
                try {
                    new ValidateInquiry(null).validateGetBindingDetail(body);
                } catch (DispositionReportFaultMessage drfm) {
                    long procTime = System.nanoTime() - startTime;
                    serviceCounter.update(InquiryQuery.FIND_TMODEL, QueryStatus.FAILED, procTime);                      
                    throw drfm;
                }

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			if (isAuthenticated())
				this.getEntityPublisher(em, body.getAuthInfo());

			BindingDetail result = new BindingDetail();

			List<String> bindingKeyList = body.getBindingKey();
			for (String bindingKey : bindingKeyList) {
				org.apache.juddi.model.BindingTemplate modelBindingTemplate = null;
				try {
					modelBindingTemplate = em.find(org.apache.juddi.model.BindingTemplate.class, bindingKey);
				} catch (ClassCastException e) {}
				if (modelBindingTemplate == null)
					throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.BindingTemplateNotFound", bindingKey));

				org.uddi.api_v3.BindingTemplate apiBindingTemplate = new org.uddi.api_v3.BindingTemplate();

				MappingModelToApi.mapBindingTemplate(modelBindingTemplate, apiBindingTemplate);

				result.getBindingTemplate().add(apiBindingTemplate);
			}

			tx.commit();
                        long procTime = System.nanoTime() - startTime;
                        serviceCounter.update(InquiryQuery.GET_BINDINGDETAIL, QueryStatus.SUCCESS, procTime);                      

			return result;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}

	
    public BusinessDetail getBusinessDetail(GetBusinessDetail body)
			throws DispositionReportFaultMessage {
                long startTime = System.nanoTime();
                try {
                    new ValidateInquiry(null).validateGetBusinessDetail(body);
                } catch (DispositionReportFaultMessage drfm) {
                    long procTime = System.nanoTime() - startTime;
                    serviceCounter.update(InquiryQuery.GET_BUSINESSDETAIL, QueryStatus.FAILED, procTime);                      
                    throw drfm;
                }

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			if (isAuthenticated())
				this.getEntityPublisher(em, body.getAuthInfo());

			BusinessDetail result = new BusinessDetail();

			List<String> businessKeyList = body.getBusinessKey();
			for (String businessKey : businessKeyList) {
				org.apache.juddi.model.BusinessEntity modelBusinessEntity = null;
				try {
					modelBusinessEntity = em.find(org.apache.juddi.model.BusinessEntity.class, businessKey);
				} catch (ClassCastException e) {}
				if (modelBusinessEntity == null)
					throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.BusinessNotFound", businessKey));

				org.uddi.api_v3.BusinessEntity apiBusinessEntity = new org.uddi.api_v3.BusinessEntity();

				MappingModelToApi.mapBusinessEntity(modelBusinessEntity, apiBusinessEntity);

				result.getBusinessEntity().add(apiBusinessEntity);
			}

			tx.commit();
                        long procTime = System.nanoTime() - startTime;
                        serviceCounter.update(InquiryQuery.GET_BUSINESSDETAIL, QueryStatus.SUCCESS, procTime);                      

			return result;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}

	
    public OperationalInfos getOperationalInfo(GetOperationalInfo body)
			throws DispositionReportFaultMessage {
                long startTime = System.nanoTime();
                try {
                    new ValidateInquiry(null).validateGetOperationalInfo(body);
                } catch (DispositionReportFaultMessage drfm) {
                    long procTime = System.nanoTime() - startTime;
                    serviceCounter.update(InquiryQuery.GET_OPERATIONALINFO, QueryStatus.FAILED, procTime);                      
                    throw drfm;
                }

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			if (isAuthenticated())
				this.getEntityPublisher(em, body.getAuthInfo());

			OperationalInfos result = new OperationalInfos();

			List<String> entityKeyList = body.getEntityKey();
			for (String entityKey : entityKeyList) {
				org.apache.juddi.model.UddiEntity modelUddiEntity = null;
				try {
					modelUddiEntity = em.find(org.apache.juddi.model.UddiEntity.class, entityKey);
				} catch (ClassCastException e) {}
				if (modelUddiEntity == null)
					throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.EntityNotFound", entityKey));

				org.uddi.api_v3.OperationalInfo apiOperationalInfo = new org.uddi.api_v3.OperationalInfo();

				MappingModelToApi.mapOperationalInfo(modelUddiEntity, apiOperationalInfo);

				result.getOperationalInfo().add(apiOperationalInfo);
			}

			tx.commit();
                        long procTime = System.nanoTime() - startTime;
                        serviceCounter.update(InquiryQuery.GET_OPERATIONALINFO, QueryStatus.SUCCESS, procTime);                      

			return result;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}

	
    public ServiceDetail getServiceDetail(GetServiceDetail body)
			throws DispositionReportFaultMessage {
        long startTime = System.nanoTime();
        try {
            new ValidateInquiry(null).validateGetServiceDetail(body);
        } catch (DispositionReportFaultMessage drfm) {
            long procTime = System.nanoTime() - startTime;
            serviceCounter.update(InquiryQuery.GET_SERVICEDETAIL, QueryStatus.FAILED, procTime);                      
            throw drfm;
        }

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			if (isAuthenticated())
				this.getEntityPublisher(em, body.getAuthInfo());

			ServiceDetail result = new ServiceDetail();

			List<String> serviceKeyList = body.getServiceKey();
			for (String serviceKey : serviceKeyList) {
				org.apache.juddi.model.BusinessService modelBusinessService = null;
				try {
					modelBusinessService = em.find(org.apache.juddi.model.BusinessService.class, serviceKey);
				} catch (ClassCastException e){}
				if (modelBusinessService == null)
					throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.ServiceNotFound", serviceKey));

				org.uddi.api_v3.BusinessService apiBusinessService = new org.uddi.api_v3.BusinessService();

				MappingModelToApi.mapBusinessService(modelBusinessService, apiBusinessService);

				result.getBusinessService().add(apiBusinessService);
			}

			tx.commit();
                        long procTime = System.nanoTime() - startTime;
                        serviceCounter.update(InquiryQuery.GET_SERVICEDETAIL, QueryStatus.SUCCESS, procTime);                      

			return result;

		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}

	
    public TModelDetail getTModelDetail(GetTModelDetail body)
			throws DispositionReportFaultMessage {
                long startTime = System.nanoTime();
                try {
                    new ValidateInquiry(null).validateGetTModelDetail(body);
                } catch (DispositionReportFaultMessage drfm) {
                    long procTime = System.nanoTime() - startTime;
                    serviceCounter.update(InquiryQuery.GET_TMODELDETAIL, QueryStatus.FAILED, procTime);                      
                    throw drfm;
                }
                    
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			if (isAuthenticated())
				this.getEntityPublisher(em, body.getAuthInfo());

			TModelDetail result = new TModelDetail();

			List<String> tmodelKeyList = body.getTModelKey();
			for (String tmodelKey : tmodelKeyList) {
				org.apache.juddi.model.Tmodel modelTModel = null;
				try {
					modelTModel = em.find(org.apache.juddi.model.Tmodel.class, tmodelKey);
				} catch (ClassCastException e) {}
				if (modelTModel == null)
					throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.TModelNotFound", tmodelKey));

				org.uddi.api_v3.TModel apiTModel = new org.uddi.api_v3.TModel();

				MappingModelToApi.mapTModel(modelTModel, apiTModel);

				result.getTModel().add(apiTModel);
			}

			tx.commit();
                        long procTime = System.nanoTime() - startTime;
                        serviceCounter.update(InquiryQuery.GET_TMODELDETAIL, QueryStatus.SUCCESS, procTime);                      

			return result;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			em.close();
		}
	}

	private boolean isAuthenticated() {
		boolean result = false;
		try {
			result = AppConfig.getConfiguration().getBoolean(Property.JUDDI_AUTHENTICATE_INQUIRY);
		} catch (ConfigurationException e) {
			log.error("Configuration exception occurred retrieving: " + Property.JUDDI_AUTHENTICATE_INQUIRY, e);
		}
		return result;
	}

}
