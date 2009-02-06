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

import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.error.FatalErrorException;
import org.apache.juddi.model.BindingTemplate;
import org.apache.juddi.model.BusinessEntity;
import org.apache.juddi.model.BusinessService;
import org.apache.juddi.model.TransferTokenKey;
import org.apache.juddi.model.UddiEntity;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.query.PersistenceManager;
import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.validation.ValidateCustodyTransfer;
import org.uddi.custody_v3.DiscardTransferToken;
import org.uddi.custody_v3.KeyBag;
import org.uddi.custody_v3.TransferEntities;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDICustodyTransferPortType;

@WebService(serviceName="UDDICustodyTransferService", 
			endpointInterface="org.uddi.v3_service.UDDICustodyTransferPortType",
			targetNamespace = "urn:uddi-org:custody_v3_portType")
public class UDDICustodyTransferImpl extends AuthenticatedService implements UDDICustodyTransferPortType {

	public static final String TRANSFER_TOKEN_PREFIX = "transfertoken:";
	public static final int DEFAULT_TRANSFEREXPIRATION_DAYS = 3;

	@SuppressWarnings("unchecked")
	public void discardTransferToken(DiscardTransferToken body)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
		
		new ValidateCustodyTransfer(publisher).validateDiscardTransferToken(em, body);

		org.uddi.custody_v3.TransferToken apiTransferToken = body.getTransferToken();
		if (apiTransferToken != null) {
			String transferTokenId = new String(apiTransferToken.getOpaqueToken());
			org.apache.juddi.model.TransferToken modelTransferToken = em.find(org.apache.juddi.model.TransferToken.class, transferTokenId);
			if (modelTransferToken != null)
				em.remove(modelTransferToken);
		}
		
		KeyBag keyBag = body.getKeyBag();
		if (keyBag != null) {
			List<String> keyList = keyBag.getKey();
			Vector<DynamicQuery.Parameter> params = new Vector<DynamicQuery.Parameter>(0);
			for (String key : keyList) {
				// Creating parameters for key-checking query
				DynamicQuery.Parameter param = new DynamicQuery.Parameter("UPPER(ttk.entityKey)", 
																		  key.toUpperCase(), 
																		  DynamicQuery.PREDICATE_EQUALS);
				
				params.add(param);
			}

			// Find the associated transfer tokens and remove them.
			DynamicQuery getTokensQry = new DynamicQuery();
			getTokensQry.append("select distinct ttk.transferToken from TransferTokenKey ttk").pad();
			getTokensQry.WHERE().pad().appendGroupedOr(params.toArray(new DynamicQuery.Parameter[0]));

			Query qry = getTokensQry.buildJPAQuery(em);
			List<org.apache.juddi.model.TransferToken> tokensToDelete = qry.getResultList();
			if (tokensToDelete != null && tokensToDelete.size() > 0) {
				for (org.apache.juddi.model.TransferToken tt : tokensToDelete)
					em.remove(tt);
			}
		}

		tx.commit();
		em.close();
	}

	public void getTransferToken(String authInfo, KeyBag keyBag,
			Holder<String> nodeID, Holder<XMLGregorianCalendar> expirationTime,
			Holder<byte[]> opaqueToken) throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		UddiEntityPublisher publisher = this.getEntityPublisher(em, authInfo);
		
		new ValidateCustodyTransfer(publisher).validateGetTransferToken(em, keyBag);

		int transferExpirationDays = DEFAULT_TRANSFEREXPIRATION_DAYS;
		try { 
			transferExpirationDays = AppConfig.getConfiguration().getInt(Property.JUDDI_TRANSFER_EXPIRATION_DAYS);
			// For output
			nodeID.value = AppConfig.getConfiguration().getString(Property.JUDDI_NODE_ID);
		}
		catch(ConfigurationException ce) 
		{ throw new FatalErrorException(new ErrorMessage("errors.configuration.Retrieval"));}

		String transferKey = TRANSFER_TOKEN_PREFIX + UUID.randomUUID();
		org.apache.juddi.model.TransferToken transferToken = new org.apache.juddi.model.TransferToken();
		transferToken.setTransferToken(transferKey);
		// For output
		opaqueToken.value = transferKey.getBytes();
		
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(gc.getTimeInMillis() + transferExpirationDays * 24 * 60 * 60 * 1000);
		transferToken.setExpirationDate(gc.getTime());

		try { 
			DatatypeFactory df = DatatypeFactory.newInstance();
			// For output
			expirationTime.value = df.newXMLGregorianCalendar(gc);
		}
		catch(DatatypeConfigurationException ce) 
		{ throw new FatalErrorException(new ErrorMessage("errors.Unspecified"));}

		List<String> keyList = keyBag.getKey();
		for (String key : keyList) {
			TransferTokenKey tokenKey = new TransferTokenKey(transferToken, key);
			transferToken.getTransferKeys().add(tokenKey);
		}
		
		em.persist(transferToken);
		
		tx.commit();
		em.close();
	}

	public void transferEntities(TransferEntities body)
			throws DispositionReportFaultMessage {

		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		UddiEntityPublisher publisher = this.getEntityPublisher(em, body.getAuthInfo());
		
		new ValidateCustodyTransfer(publisher).validateTransferEntities(em, body);

		// Once validated, the ownership transfer is as simple as switching the publisher
		KeyBag keyBag = body.getKeyBag();
		List<String> keyList = keyBag.getKey();
		for (String key : keyList) {
			UddiEntity uddiEntity = em.find(UddiEntity.class, key);
			uddiEntity.setAuthorizedName(publisher.getAuthorizedName());
			
			if (uddiEntity instanceof BusinessEntity) {
				BusinessEntity be = (BusinessEntity)uddiEntity;
				
				List<BusinessService> bsList = be.getBusinessServices();
				for (BusinessService bs : bsList) {
					bs.setAuthorizedName(publisher.getAuthorizedName());
					
					List<BindingTemplate> btList = bs.getBindingTemplates();
					for (BindingTemplate bt : btList)
						bt.setAuthorizedName(publisher.getAuthorizedName());
				}
			}
		}

		// After transfer is finished, the token can be removed
		org.uddi.custody_v3.TransferToken apiTransferToken = body.getTransferToken();
		String transferTokenId = new String(apiTransferToken.getOpaqueToken());
		org.apache.juddi.model.TransferToken modelTransferToken = em.find(org.apache.juddi.model.TransferToken.class, transferTokenId);
		em.remove(modelTransferToken);
		
		tx.commit();
		em.close();

	}
}
