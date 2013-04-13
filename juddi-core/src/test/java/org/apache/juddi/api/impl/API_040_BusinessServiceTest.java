/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.api.impl;

import java.rmi.RemoteException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.Registry;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.v3.tck.Property;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckBusinessService;
import org.apache.juddi.v3.tck.TckFindEntity;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class API_040_BusinessServiceTest 
{
	private static Log logger                        = LogFactory.getLog(API_040_BusinessServiceTest.class);
	
	private static API_010_PublisherTest api010      = new API_010_PublisherTest();
	private static TckTModel tckTModel               = new TckTModel(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBusiness tckBusiness           = new TckBusiness(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	private static TckBusinessService tckBusinessService  = new TckBusinessService(new UDDIPublicationImpl(), new UDDIInquiryImpl());
	
	private static String authInfoJoe                = null;
	private static String authInfoSam                = null;
	
	@BeforeClass
	public static void setup() throws ConfigurationException {
		Registry.start();
		logger.debug("Getting auth tokens..");
		try {
			api010.saveJoePublisher();
			api010.saveSamSyndicator();
			UDDISecurityPortType security      = new UDDISecurityImpl();
			authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(),  TckPublisher.getJoePassword());
			authInfoSam = TckSecurity.getAuthToken(security, TckPublisher.getSamPublisherId(),  TckPublisher.getSamPassword());
		} catch (RemoteException e) {
			logger.error(e.getMessage(), e);
			Assert.fail("Could not obtain authInfo token.");
		}
	}

	@AfterClass
	public static void stopRegistry() throws ConfigurationException {
		Registry.stop();
	}
	
	@Test
	public void joepublisher() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusinessService.saveJoePublisherService(authInfoJoe);
			tckBusinessService.deleteJoePublisherService(authInfoJoe);
		} finally {
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}
	
	@Test
	public void joepublisher2Services() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusinessService.saveJoePublisherService(authInfoJoe);
			tckBusinessService.saveJoePublisherService2(authInfoJoe);
			tckBusinessService.deleteJoePublisherService(authInfoJoe);
			tckBusinessService.deleteJoePublisherService2(authInfoJoe);
		} finally {
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}
	
	@Test
	public void joepublisher2UpdateBusiness() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusinessService.saveJoePublisherService(authInfoJoe);
			tckBusinessService.saveJoePublisherService2(authInfoJoe);
			tckBusiness.updateJoePublisherBusiness(authInfoJoe);
			tckBusinessService.deleteJoePublisherService(authInfoJoe);
			tckBusinessService.deleteJoePublisherService2(authInfoJoe);
		} finally {
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}
	/**
	 * 5.2.16.3 paragraph 4
	 * Data contained within businessEntity structures can be rearranged with 
	 * this API call. This can be done by redefining parent container relationships 
	 * for other registered information. For instance, if a new businessEntity 
	 * is saved with information about a businessService that is registered 
	 * already as part of a different businessEntity, this results in the 
	 * businessService being moved from its current container to the new businessEntity.	
	 * This condition occurs when the businessKey of the businessService being 
	 * saved matches the businessKey of the businessEntity being saved. 
	 * An attempt to delete or move a businessService in this manner by 
	 * a party who is not the publisher of the businessService MUST be 
	 * rejected with an error E_userMismatch.
	 */
	@Test
	public void joepublisherMoveBusinessService() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusinessService.saveJoePublisherService(authInfoJoe);
			tckBusiness.checkServicesBusinessOne(1);
			tckBusiness.saveJoePublisherBusiness3(authInfoJoe);
			//check that this business has no services
			tckBusiness.checkServicesBusinessThree(0);
			//Now move the service from one to three
			tckBusiness.saveJoePublisherBusiness1to3(authInfoJoe);
			tckBusiness.checkServicesBusinessOne(0);
			tckBusiness.checkServicesBusinessThree(1);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
			tckBusinessService.deleteJoePublisherService(authInfoJoe);
			tckBusiness.deleteJoePublisherBusiness3(authInfoJoe);
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}
	
	@Test @Ignore
	public void combineCategoryBagsFindServices() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveCombineCatBagsPublisherBusiness(authInfoJoe);
			
			PersistenceManager pm = new PersistenceManager();
			EntityManager em = pm.getEntityManager();
			
			String sql = "select bs.entityKey from BusinessService bs ," +
					" ServiceCategoryBag ServiceCategory_ , " +
					"KeyedReference KeyedRefere_0 , KeyedReference KeyedRefere_1 " +
					"where ( bs.entityKey = ServiceCategory_.businessService.entityKey and " +
					" ( ServiceCategory_.id = KeyedRefere_0.categoryBag.id  ))";
			String sql3 = "select bs.entityKey from BusinessService bs UNION select bs.entityKey from BusinessService bs";
			
			String sql2 = "select bs.entityKey, categoryBag.id from BusinessService bs, " +
	        " BindingTemplate bt,  " +
			" BindingCategoryBag categoryBag  ," +
			"KeyedReference KeyedRefere_0, KeyedReference KeyedRefere_1 " +
			"where  categoryBag.id = bt.categoryBag.id and " +
			"bt.businessService.entityKey = bs.entityKey and " +
			"( (KeyedRefere_0.categoryBag.id=categoryBag.id and KeyedRefere_0.tmodelKeyRef = 'uddi:uddi.joepublisher.com:tmodel01' and KeyedRefere_0.keyValue = 'value-z') or " +
			"(KeyedRefere_1.categoryBag.id=categoryBag.id and KeyedRefere_1.tmodelKeyRef = 'uddi:uddi.joepublisher.com:tmodel02' and KeyedRefere_1.keyValue = 'value-x') ) " +
			" group by bs.entityKey, categoryBag.id ";
			
			String sql1 = "select bs.entityKey from BusinessService bs ," +
			        " BindingTemplate bt, ServiceCategoryBag ServiceCategory_ , " +
					" BindingCategoryBag BindingCategory_  , " +
					"KeyedReference KeyedRefere_0, KeyedReference KeyedRefere_1 " +
					"where ( ((bt.businessService.entityKey = bs.entityKey and bt.entityKey = BindingCategory_.bindingTemplate.entityKey and " +
					" BindingCategory_.id = KeyedRefere_0.categoryBag.id) AND (bs.entityKey = ServiceCategory_.businessService.entityKey and ServiceCategory_.id = KeyedRefere_0.categoryBag.id)" +
					") and KeyedRefere_0.categoryBag.id = KeyedRefere_1.categoryBag.id and ( " +
					"(KeyedRefere_0.tmodelKeyRef = 'uddi:uddi.joepublisher.com:tmodel01' and KeyedRefere_0.keyValue = 'value-z') and " +
					"(KeyedRefere_1.tmodelKeyRef = 'uddi:uddi.joepublisher.com:tmodel02' and KeyedRefere_1.keyValue = 'value-x') ) ) ";
			Query qry = em.createQuery(sql2);
			List result = qry.getResultList();
			System.out.println(result);
			
			//tckFindEntity.findService_CombinedCatBag();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} 
	}
	
	@Test
	public void samsyndicator() {
		try {
			// For sam's service projection, joe's information must be loaded
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusinessService.saveJoePublisherService(authInfoJoe);
			
			tckTModel.saveSamSyndicatorTmodel(authInfoSam);
			tckBusiness.saveSamSyndicatorBusinessWithProjection(authInfoSam);
			tckBusinessService.saveSamSyndicatorService(authInfoSam);
			tckBusinessService.deleteSamSyndicatorService(authInfoSam);
		} finally {
			tckBusiness.deleteSamSyndicatorBusiness(authInfoSam);
			tckTModel.deleteSamSyndicatorTmodel(authInfoSam);
			
			tckBusinessService.deleteJoePublisherService(authInfoJoe);
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}

}
