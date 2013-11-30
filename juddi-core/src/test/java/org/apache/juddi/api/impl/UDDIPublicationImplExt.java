/*
 * Copyright 2013 The Apache Software Foundation.
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
package org.apache.juddi.api.impl;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.ws.WebServiceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.mapping.MappingApiToModel;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.validation.ValidatePublish;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * This class is for testing purposes only and enables you to override the
 * requestor's ip address
 *
 * @author Alex O'Ree
 */
public class UDDIPublicationImplExt extends UDDIPublicationImpl {

        /**
         * used for unit tests only
         *
         * @param ctx
         */
        protected UDDIPublicationImplExt(WebServiceContext ctx) {
                super();
                this.ctx = ctx;
        }
        Log log = LogFactory.getLog(UDDIPublicationImplExt.class);

        private String nodeId="";
        public BusinessDetail saveBusinessFudge(SaveBusiness body, String nodeID)
                throws DispositionReportFaultMessage {

                if (!body.getBusinessEntity().isEmpty()) {
                        log.debug("Inbound save business Fudger request for key " + body.getBusinessEntity().get(0).getBusinessKey());
                }
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();

                        UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());

                        ValidatePublish validator = new ValidatePublish(publisher);
                        validator.validateSaveBusiness(em, body, null);

                        BusinessDetail result = new BusinessDetail();

                        List<org.uddi.api_v3.BusinessEntity> apiBusinessEntityList = body.getBusinessEntity();
                        for (org.uddi.api_v3.BusinessEntity apiBusinessEntity : apiBusinessEntityList) {

                                org.apache.juddi.model.BusinessEntity modelBusinessEntity = new org.apache.juddi.model.BusinessEntity();

                                
                                MappingApiToModel.mapBusinessEntity(apiBusinessEntity, modelBusinessEntity);
                                nodeId = nodeID;

                                setOperationalInfo(em, modelBusinessEntity, publisher);

                                em.persist(modelBusinessEntity);

                                result.getBusinessEntity().add(apiBusinessEntity);
                        }

                        //check how many business this publisher owns.
                        validator.validateSaveBusinessMax(em);

                        tx.commit();

                        return result;
                } catch (DispositionReportFaultMessage drfm) {

                        throw drfm;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }
        
        private void setOperationalInfo(EntityManager em, org.apache.juddi.model.BusinessEntity uddiEntity, UddiEntityPublisher publisher) throws DispositionReportFaultMessage {

		uddiEntity.setAuthorizedName(publisher.getAuthorizedName());

		Date now = new Date();
		uddiEntity.setModified(now);
		uddiEntity.setModifiedIncludingChildren(now);


		uddiEntity.setNodeId(nodeId);
		
		org.apache.juddi.model.BusinessEntity existingUddiEntity = em.find(uddiEntity.getClass(), uddiEntity.getEntityKey());
		if (existingUddiEntity != null)
			uddiEntity.setCreated(existingUddiEntity.getCreated());
		else
			uddiEntity.setCreated(now);
		
		List<org.apache.juddi.model.BusinessService> serviceList = uddiEntity.getBusinessServices();
		for (org.apache.juddi.model.BusinessService service : serviceList)
			setOperationalInfo(em, service, publisher, true);
		
		
		if (existingUddiEntity != null)
			em.remove(existingUddiEntity);
		
	}

	private void setOperationalInfo(EntityManager em, org.apache.juddi.model.BusinessService uddiEntity, UddiEntityPublisher publisher, boolean isChild) throws DispositionReportFaultMessage {

		uddiEntity.setAuthorizedName(publisher.getAuthorizedName());

		Date now = new Date();
		uddiEntity.setModified(now);
		uddiEntity.setModifiedIncludingChildren(now);
		
		if(!isChild) {
			org.apache.juddi.model.BusinessEntity parent = em.find(org.apache.juddi.model.BusinessEntity.class, uddiEntity.getBusinessEntity().getEntityKey());
			parent.setModifiedIncludingChildren(now);
			em.persist(parent);
		}

		uddiEntity.setNodeId(nodeId);
		
		org.apache.juddi.model.BusinessService existingUddiEntity = em.find(uddiEntity.getClass(), uddiEntity.getEntityKey());
		if (existingUddiEntity != null) {
			uddiEntity.setCreated(existingUddiEntity.getCreated());
		}
		else
			uddiEntity.setCreated(now);
		
		List<org.apache.juddi.model.BindingTemplate> bindingList = uddiEntity.getBindingTemplates();
		for (org.apache.juddi.model.BindingTemplate binding : bindingList)
			setOperationalInfo(em, binding, publisher, true);
		
		
		if (existingUddiEntity != null)
			em.remove(existingUddiEntity);
		
	}

	private void setOperationalInfo(EntityManager em, org.apache.juddi.model.BindingTemplate uddiEntity, UddiEntityPublisher publisher, boolean isChild) throws DispositionReportFaultMessage {

		uddiEntity.setAuthorizedName(publisher.getAuthorizedName());

		Date now = new Date();
		uddiEntity.setModified(now);
		uddiEntity.setModifiedIncludingChildren(now);

		if(!isChild) {
			org.apache.juddi.model.BusinessService parent = em.find(org.apache.juddi.model.BusinessService.class, uddiEntity.getBusinessService().getEntityKey());
			parent.setModifiedIncludingChildren(now);
			em.persist(parent);
			
			// JUDDI-421:  now the businessEntity parent will have it's modifiedIncludingChildren set
			org.apache.juddi.model.BusinessEntity businessParent = em.find(org.apache.juddi.model.BusinessEntity.class, parent.getBusinessEntity().getEntityKey());
			businessParent.setModifiedIncludingChildren(now);
			em.persist(businessParent);
		}

		uddiEntity.setNodeId(nodeId);
		
		org.apache.juddi.model.BindingTemplate existingUddiEntity = em.find(uddiEntity.getClass(), uddiEntity.getEntityKey());
		if (existingUddiEntity != null)
			uddiEntity.setCreated(existingUddiEntity.getCreated());
		else
			uddiEntity.setCreated(now);
		
		if (existingUddiEntity != null)
			em.remove(existingUddiEntity);
		
	}
	
	private void setOperationalInfo(EntityManager em, org.apache.juddi.model.Tmodel uddiEntity, UddiEntityPublisher publisher) throws DispositionReportFaultMessage {

		uddiEntity.setAuthorizedName(publisher.getAuthorizedName());
		
		Date now = new Date();
		uddiEntity.setModified(now);
		uddiEntity.setModifiedIncludingChildren(now);

		uddiEntity.setNodeId(nodeId);
		
		org.apache.juddi.model.Tmodel existingUddiEntity = em.find(uddiEntity.getClass(), uddiEntity.getEntityKey());
		if (existingUddiEntity != null)
			uddiEntity.setCreated(existingUddiEntity.getCreated());
		else
			uddiEntity.setCreated(now);
		
		if (existingUddiEntity != null)
			em.remove(existingUddiEntity);
		
	}

}
