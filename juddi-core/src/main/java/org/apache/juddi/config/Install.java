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

package org.apache.juddi.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.api.datatype.Publisher;
import org.apache.juddi.api.impl.UDDIInquiryImpl;
import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.error.FatalErrorException;
import org.apache.juddi.error.InvalidKeyPassedException;
import org.apache.juddi.error.KeyUnavailableException;
import org.apache.juddi.error.ValueNotAllowedException;
import org.apache.juddi.keygen.KeyGenerator;
import org.apache.juddi.mapping.MappingApiToModel;
import org.apache.juddi.model.KeyGeneratorKey;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.query.PersistenceManager;
import org.apache.juddi.validation.ValidatePublish;
import org.apache.juddi.validation.ValidateUDDIKey;
import org.apache.log4j.Logger;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class Install {

	public static final String FILE_ROOT_BUSINESSENTITY = "root_BusinessEntity.xml";
	public static final String FILE_ROOT_PUBLISHER = "root_Publisher.xml";
	public static final String FILE_ROOT_TMODELKEYGEN = "root_tModelKeyGen.xml";
	public static final String FILE_UDDI_PUBLISHER = "UDDI_Publisher.xml";
	public static final String FILE_UDDI_TMODELS = "UDDI_tModels.xml";
	public static final String FILE_JOE_PUBLISHER = "joepublisher_Publisher.xml";
	public static final String FILE_SSYNDICATOR = "ssyndicator_Publisher.xml";
	
	public static final String FILE_PERSISTENCE = "persistence.xml";
	public static final String JUDDI_INSTALL_DATA_DIR = "juddi_install_data/";
	public static Logger log = Logger.getLogger(Install.class);

	public static void install() throws JAXBException, DispositionReportFaultMessage, IOException {
		install(JUDDI_INSTALL_DATA_DIR, null, false);
	}
	
	public static void install(String srcDir, String userPartition, boolean reloadConfig) throws JAXBException, DispositionReportFaultMessage, IOException {
		if (srcDir != null) {
			if (srcDir.endsWith("\\") || srcDir.endsWith("/")) {
				// Do nothing
			}
			else 
				srcDir = srcDir + "\\";
		}
		else
			srcDir = "";
				
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		
		UddiEntityPublisher rootPublisher = null;
		UddiEntityPublisher uddiPublisher = null;
		try {
			tx.begin();
	
			if (alreadyInstalled(em))
				throw new FatalErrorException(new ErrorMessage("errors.install.AlreadyInstalled"));
			
			TModel rootTModelKeyGen = (TModel)buildEntityFromDoc(JUDDI_INSTALL_DATA_DIR + FILE_ROOT_TMODELKEYGEN, "org.uddi.api_v3");
			org.uddi.api_v3.BusinessEntity rootBusinessEntity = (org.uddi.api_v3.BusinessEntity)buildEntityFromDoc(srcDir + FILE_ROOT_BUSINESSENTITY, "org.uddi.api_v3");
			
			String rootPartition = getRootPartition(rootTModelKeyGen, userPartition);
			String nodeId = getNodeId(rootBusinessEntity.getBusinessKey(), rootPartition);
			
			rootPublisher = installPublisher(em, JUDDI_INSTALL_DATA_DIR + FILE_ROOT_PUBLISHER);
			uddiPublisher = installPublisher(em, JUDDI_INSTALL_DATA_DIR + FILE_UDDI_PUBLISHER);
			//Inserting 2 test publishers
			installPublisher(em, JUDDI_INSTALL_DATA_DIR + FILE_JOE_PUBLISHER);
			installPublisher(em, JUDDI_INSTALL_DATA_DIR + FILE_SSYNDICATOR);

			installRootPublisherKeyGen(em, rootTModelKeyGen, rootPartition, rootPublisher, nodeId);

			rootBusinessEntity.setBusinessKey(nodeId);
			installRootBusinessEntity(em, rootBusinessEntity, rootPublisher);

			installUDDITModels(em, JUDDI_INSTALL_DATA_DIR + FILE_UDDI_TMODELS, uddiPublisher, nodeId);
			
			tx.commit();
		}
		catch(DispositionReportFaultMessage dr) {
			log .error(dr.getMessage(),dr);
			tx.rollback();
			throw dr;
		} 
		catch (JAXBException je) {
			log .error(je.getMessage(),je);
			tx.rollback();
			throw je;
		} 
		catch (IOException ie) {
			log .error(ie.getMessage(),ie);
			tx.rollback();
			throw ie;
		} 
		finally {
			if (em.isOpen()) {
				em.close();
			}
		}

		// Now that all necessary persistent entities are loaded, the configuration must be reloaded to be sure all properties are set.
		if (reloadConfig) {
			try { AppConfig.reloadConfig(); } catch (ConfigurationException ce) { log.error(ce.getMessage(), ce); }
		}
		
	}

	public static void uninstall() {
		// Close the open emf, open a new one with Persistence.create...(String, Map) and overwrite the property that handles the table 
		// generation. The persistence.xml file will have to be read in to determine which property
		// to overwrite.  The property will be specific to the provider.  
		// Hibernate:  <property name="hibernate.hbm2ddl.auto" value="update"/> ->use "create-drop" or just "drop"?
		// OpenJPA: openjpa.jdbc.SynchronizeMappings=buildSchema(SchemaAction='add,deleteTableContents')
		// etc...(find more)
		// Then close this emf.  Question:  is the original emf reusable or will closing it cause problems?
		
	}
	
	public static boolean alreadyInstalled() {
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		boolean result = alreadyInstalled(em);
		
		tx.commit();
		em.close();
		
		return result;
	}

	public static boolean alreadyInstalled(EntityManager em) {
		
		UddiEntityPublisher publisher = em.find(UddiEntityPublisher.class, Constants.ROOT_PUBLISHER);
		if (publisher != null)
			return true;

		publisher = em.find(UddiEntityPublisher.class, Constants.UDDI_PUBLISHER);
		if (publisher != null)
			return true;
		
		return false;
	}
	
	public static String getRootPartition(TModel rootTModelKeyGen, String userPartition) throws JAXBException, IOException, DispositionReportFaultMessage {
		String result = rootTModelKeyGen.getTModelKey().substring(0, rootTModelKeyGen.getTModelKey().lastIndexOf(KeyGenerator.PARTITION_SEPARATOR));
		
		if (userPartition != null && userPartition.length() > 0) {
			// A root partition was provided by the user.  Must validate it.  The first component should be a domain key and the any following
			// tokens should be a valid KSS.
			userPartition = userPartition.trim();
			if (userPartition.endsWith(KeyGenerator.PARTITION_SEPARATOR) || userPartition.startsWith(KeyGenerator.PARTITION_SEPARATOR))
				throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.MalformedKey", userPartition));
			
			StringTokenizer tokenizer = new StringTokenizer(userPartition.toLowerCase(), KeyGenerator.PARTITION_SEPARATOR);
			for(int count = 0; tokenizer.hasMoreTokens(); count++) {
				String nextToken = tokenizer.nextToken();

				if (count == 0) {
					if(!ValidateUDDIKey.isValidDomainKey(nextToken))
						throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.MalformedKey", userPartition));
				}
				else {
					if (!ValidateUDDIKey.isValidKSS(nextToken))
						throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.MalformedKey", userPartition));
				}
			}
			// If the user-supplied root partition checks out, we can use that.
			result = KeyGenerator.UDDI_SCHEME + KeyGenerator.PARTITION_SEPARATOR + userPartition;
		}
		return result;
	}
	
	public static String getNodeId(String userNodeId, String rootPartition) throws DispositionReportFaultMessage {

		String result = userNodeId;
		if (result == null || result.length() == 0) {
			result = rootPartition + KeyGenerator.PARTITION_SEPARATOR + UUID.randomUUID();
		}
		else {
			ValidateUDDIKey.validateUDDIv3Key(result, rootPartition);
			String keyPartition = result.substring(0, result.lastIndexOf(KeyGenerator.PARTITION_SEPARATOR));
			if (!rootPartition.equalsIgnoreCase(keyPartition))
				throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.BadPartition", userNodeId));
		}
		return result;
	}
	
	public static org.uddi.api_v3.BusinessEntity getNodeBusinessEntity(String businessKey) throws DispositionReportFaultMessage {
		UDDIInquiryImpl inquiry = new UDDIInquiryImpl();
		
		org.uddi.api_v3.GetBusinessDetail gbd = new org.uddi.api_v3.GetBusinessDetail();
		gbd.getBusinessKey().add(businessKey);
		
		org.uddi.api_v3.BusinessDetail bd = inquiry.getBusinessDetail(gbd);
		if (bd != null) {
			List<org.uddi.api_v3.BusinessEntity> beList = bd.getBusinessEntity();
			if (beList != null && beList.size() > 0)
				return beList.get(0);
		}

		return new org.uddi.api_v3.BusinessEntity();
	}
	
	
	private static String installRootBusinessEntity(EntityManager em, org.uddi.api_v3.BusinessEntity rootBusinessEntity, UddiEntityPublisher rootPublisher) 
	throws JAXBException, DispositionReportFaultMessage, IOException {
		
		validateRootBusinessEntity(rootBusinessEntity, rootPublisher);
		
		org.apache.juddi.model.BusinessEntity modelBusinessEntity = new org.apache.juddi.model.BusinessEntity();
		MappingApiToModel.mapBusinessEntity(rootBusinessEntity, modelBusinessEntity);
		
		modelBusinessEntity.setPublisher(rootPublisher);
		
		Date now = new Date();
		modelBusinessEntity.setCreated(now);
		modelBusinessEntity.setModified(now);
		modelBusinessEntity.setModifiedIncludingChildren(now);
		modelBusinessEntity.setNodeId(modelBusinessEntity.getEntityKey());
		
		for (org.apache.juddi.model.BusinessService service : modelBusinessEntity.getBusinessServices()) {
			service.setPublisher(rootPublisher);
			
			service.setCreated(now);
			service.setModified(now);
			service.setModifiedIncludingChildren(now);
			service.setNodeId(modelBusinessEntity.getEntityKey());
			
			for (org.apache.juddi.model.BindingTemplate binding : service.getBindingTemplates()) {
				binding.setPublisher(rootPublisher);
				
				binding.setCreated(now);
				binding.setModified(now);
				binding.setModifiedIncludingChildren(now);
				binding.setNodeId(modelBusinessEntity.getEntityKey());
			}
		}
		
		
		em.persist(modelBusinessEntity);
		
		return modelBusinessEntity.getEntityKey();

	}
	
	// A watered down version of ValidatePublish's validateBusinessEntity, designed for the specific condition that this is run upon the initial
	// jUDDI install.
	private static void validateRootBusinessEntity(org.uddi.api_v3.BusinessEntity businessEntity, UddiEntityPublisher rootPublisher) 
	throws DispositionReportFaultMessage {

		// A supplied businessService can't be null
		if (businessEntity == null)
			throw new ValueNotAllowedException(new ErrorMessage("errors.businessentity.NullInput"));
		
		String rootPartition = rootPublisher.getKeyGeneratorKeys().iterator().next().getKeygenTModelKey();
		rootPartition = rootPartition.substring(0, rootPartition.lastIndexOf(KeyGenerator.PARTITION_SEPARATOR));
		
		// The business key should already be set to the previously calculated and validated nodeId.  This validation is unnecessary but kept for 
		// symmetry with the other entity validations.
		String entityKey = businessEntity.getBusinessKey();
		if (entityKey == null || entityKey.length() == 0) {
			entityKey = rootPartition + KeyGenerator.PARTITION_SEPARATOR + UUID.randomUUID();
			businessEntity.setBusinessKey(entityKey);
		}
		else {
			ValidateUDDIKey.validateUDDIv3Key(entityKey, rootPartition);
			if (!rootPublisher.isValidPublisherKey(entityKey))
				throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.BadPartition", entityKey));
		}

		ValidatePublish validatePublish = new ValidatePublish(rootPublisher);
		
		validatePublish.validateNames(businessEntity.getName());
		validatePublish.validateDiscoveryUrls(businessEntity.getDiscoveryURLs());
		validatePublish.validateContacts(businessEntity.getContacts());
		validatePublish.validateCategoryBag(businessEntity.getCategoryBag());
		validatePublish.validateIdentifierBag(businessEntity.getIdentifierBag());

		org.uddi.api_v3.BusinessServices businessServices = businessEntity.getBusinessServices();
		if (businessServices != null) {
			List<org.uddi.api_v3.BusinessService> businessServiceList = businessServices.getBusinessService();
			if (businessServiceList == null || businessServiceList.size() == 0)
				throw new ValueNotAllowedException(new ErrorMessage("errors.businessservices.NoInput"));
			
			for (org.uddi.api_v3.BusinessService businessService : businessServiceList) {
				validateRootBusinessService(businessService, businessEntity, rootPublisher);
			}
		}

	}
	
	// A watered down version of ValidatePublish's validateBusinessService, designed for the specific condition that this is run upon the initial
	// jUDDI install.
	private static void validateRootBusinessService(org.uddi.api_v3.BusinessService businessService, org.uddi.api_v3.BusinessEntity parent, UddiEntityPublisher rootPublisher) 
	throws DispositionReportFaultMessage {

		// A supplied businessService can't be null
		if (businessService == null)
			throw new ValueNotAllowedException(new ErrorMessage("errors.businessservice.NullInput"));
	
		// A business key doesn't have to be provided, but if it is, it should match the parent business's key
		String parentKey = businessService.getBusinessKey();
		if (parentKey != null && parentKey.length()> 0) {
			if (!parentKey.equalsIgnoreCase(parent.getBusinessKey()))
				throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.ParentBusinessNotFound", parentKey));
		}
		
		String rootPartition = rootPublisher.getKeyGeneratorKeys().iterator().next().getKeygenTModelKey();
		rootPartition = rootPartition.substring(0, rootPartition.lastIndexOf(KeyGenerator.PARTITION_SEPARATOR));

		// Retrieve the service's passed key
		String entityKey = businessService.getServiceKey();
		if (entityKey == null || entityKey.length() == 0) {
			entityKey = rootPartition + KeyGenerator.PARTITION_SEPARATOR + UUID.randomUUID();
			businessService.setServiceKey(entityKey);
		}
		else {
			ValidateUDDIKey.validateUDDIv3Key(entityKey, rootPartition);
			if (!rootPublisher.isValidPublisherKey(entityKey))
				throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.BadPartition", entityKey));
		}
		
		ValidatePublish validatePublish = new ValidatePublish(rootPublisher);
		
		validatePublish.validateNames(businessService.getName());
		validatePublish.validateCategoryBag(businessService.getCategoryBag());

		org.uddi.api_v3.BindingTemplates bindingTemplates = businessService.getBindingTemplates();
		if (bindingTemplates != null) {
			List<org.uddi.api_v3.BindingTemplate> bindingTemplateList = bindingTemplates.getBindingTemplate();
			if (bindingTemplateList == null || bindingTemplateList.size() == 0)
				throw new ValueNotAllowedException(new ErrorMessage("errors.bindingtemplates.NoInput"));
			
			for (org.uddi.api_v3.BindingTemplate bindingTemplate : bindingTemplateList) {
				validateRootBindingTemplate(bindingTemplate, businessService, rootPublisher);
			}
		}
	}

	// A watered down version of ValidatePublish's validatBindingTemplate, designed for the specific condition that this is run upon the initial
	// jUDDI install.
	private static void validateRootBindingTemplate(org.uddi.api_v3.BindingTemplate bindingTemplate, org.uddi.api_v3.BusinessService parent, UddiEntityPublisher rootPublisher) 
	throws DispositionReportFaultMessage {

		// A supplied businessService can't be null
		if (bindingTemplate == null)
			throw new ValueNotAllowedException(new ErrorMessage("errors.bindingtemplate.NullInput"));
	
		// A service key doesn't have to be provided, but if it is, it should match the parent service's key
		String parentKey = bindingTemplate.getServiceKey();
		if (parentKey != null && parentKey.length()> 0) {
			if (!parentKey.equalsIgnoreCase(parent.getServiceKey()))
				throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.ParentServiceNotFound", parentKey));
		}
		
		String rootPartition = rootPublisher.getKeyGeneratorKeys().iterator().next().getKeygenTModelKey();
		rootPartition = rootPartition.substring(0, rootPartition.lastIndexOf(KeyGenerator.PARTITION_SEPARATOR));

		// Retrieve the service's passed key
		String entityKey = bindingTemplate.getBindingKey();
		if (entityKey == null || entityKey.length() == 0) {
			entityKey = rootPartition + KeyGenerator.PARTITION_SEPARATOR + UUID.randomUUID();
			bindingTemplate.setBindingKey(entityKey);
		}
		else {
			ValidateUDDIKey.validateUDDIv3Key(entityKey, rootPartition);
			if (!rootPublisher.isValidPublisherKey(entityKey))
				throw new KeyUnavailableException(new ErrorMessage("errors.keyunavailable.BadPartition", entityKey));
		}
		
		ValidatePublish validatePublish = new ValidatePublish(rootPublisher);
		
		validatePublish.validateCategoryBag(bindingTemplate.getCategoryBag());
		validatePublish.validateTModelInstanceDetails(bindingTemplate.getTModelInstanceDetails());

	}
	
	
	
	private static void installUDDITModels(EntityManager em, String resource, UddiEntityPublisher publisher, String nodeId) 
		throws JAXBException, DispositionReportFaultMessage, IOException {
		SaveTModel apiSaveTModel = (SaveTModel)buildEntityFromDoc(resource, "org.uddi.api_v3");
		installTModels(em, apiSaveTModel.getTModel(), publisher, nodeId);
		
	}
	
	private static UddiEntityPublisher installPublisher(EntityManager em, String resource) 
		throws JAXBException, DispositionReportFaultMessage, IOException {
		Publisher apiPub = (Publisher)buildEntityFromDoc(resource, "org.apache.juddi.api.datatype");
		org.apache.juddi.model.Publisher modelPub = new org.apache.juddi.model.Publisher();
		MappingApiToModel.mapPublisher(apiPub, modelPub);
		em.persist(modelPub);
		return modelPub;
	}
	
	private static void installTModels(EntityManager em, List<org.uddi.api_v3.TModel> apiTModelList, UddiEntityPublisher publisher, String nodeId) throws DispositionReportFaultMessage {
		if (apiTModelList != null) {
			for (org.uddi.api_v3.TModel apiTModel : apiTModelList) {
				String tModelKey = apiTModel.getTModelKey();

				if (tModelKey.toUpperCase().endsWith(KeyGenerator.KEYGENERATOR_SUFFIX.toUpperCase())) {
					installPublisherKeyGen(em, apiTModel, publisher, nodeId);
				}
				else {
					org.apache.juddi.model.Tmodel modelTModel = new org.apache.juddi.model.Tmodel();
					MappingApiToModel.mapTModel(apiTModel, modelTModel);

					modelTModel.setPublisher(publisher);
					
					Date now = new Date();
					modelTModel.setCreated(now);
					modelTModel.setModified(now);
					modelTModel.setModifiedIncludingChildren(now);
					modelTModel.setNodeId(nodeId);
					
					em.persist(modelTModel);
				}
				
			}
		}
		
	}

	private static void installRootPublisherKeyGen(EntityManager em, TModel rootTModelKeyGen, String rootPartition, UddiEntityPublisher publisher, String nodeId) 
		throws DispositionReportFaultMessage {
		
		rootTModelKeyGen.setTModelKey(rootPartition + KeyGenerator.PARTITION_SEPARATOR + KeyGenerator.KEYGENERATOR_SUFFIX);
		
		installPublisherKeyGen(em, rootTModelKeyGen, publisher, nodeId);
	}

	private static void installPublisherKeyGen(EntityManager em, TModel apiTModel, UddiEntityPublisher publisher, String nodeId) throws DispositionReportFaultMessage {

		org.apache.juddi.model.Tmodel modelTModel = new org.apache.juddi.model.Tmodel();
		MappingApiToModel.mapTModel(apiTModel, modelTModel);
		
		modelTModel.setPublisher(publisher);

		Date now = new Date();
		modelTModel.setCreated(now);
		modelTModel.setModified(now);
		modelTModel.setModifiedIncludingChildren(now);
		modelTModel.setNodeId(nodeId);
		
		em.persist(modelTModel);
		
		KeyGeneratorKey keyGenKey = new KeyGeneratorKey();
		keyGenKey.setPublisher(publisher);
		keyGenKey.setKeygenTModelKey(modelTModel.getEntityKey());
		publisher.getKeyGeneratorKeys().add(keyGenKey);
		
	}
	
	private static Object buildEntityFromDoc(String resource, String thePackage) throws JAXBException, IOException {
		InputStream resourceStream = null;
		
		URL url = Thread.currentThread().getContextClassLoader().getResource(resource);
		if (url != null)
			resourceStream = url.openStream();
		
		if (resourceStream == null) {
			resourceStream = new FileInputStream(new File(resource));
		}
		
		JAXBContext jc = JAXBContext.newInstance(thePackage);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Object obj = ((JAXBElement<?>)unmarshaller.unmarshal(resourceStream)).getValue();
		return obj;
	}

}
