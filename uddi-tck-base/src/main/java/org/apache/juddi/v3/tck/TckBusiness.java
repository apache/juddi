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
package org.apache.juddi.v3.tck;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.rmi.RemoteException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXB;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.jaxb.EntityCreator;
import org.junit.Assert;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.KeyedReferenceGroup;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class TckBusiness 
{
	final static String JOE_BUSINESS_XML        = "uddi_data/joepublisher/businessEntity.xml";
    final static String JOE_BUSINESS_KEY        = "uddi:uddi.joepublisher.com:businessone";
    final static String JOE_BUSINESS3_XML       = "uddi_data/joepublisher/businessEntity3.xml";
    final static String JOE_BUSINESS3_KEY       = "uddi:uddi.joepublisher.com:businessthree.com";
    final static String JOE_BUSINESS_MOVE_XML   = "uddi_data/joepublisher/moveBusinessService1to3.xml";
    final static String MARY_BUSINESS_XML       = "uddi_data/marypublisher/businessEntity.xml";
    final static String MARY_BUSINESS_KEY       = "uddi:uddi.marypublisher.com:marybusinessone";
    final static String SAM_BUSINESS_XML        = "uddi_data/samsyndicator/businessEntity.xml";
    final static String SAM_BUSINESS_WITHPROJECTION_XML = "uddi_data/samsyndicator/businessEntity_withProjection.xml";
    final static String SAM_BUSINESS_KEY        = "uddi:www.samco.com:samco";
    final static String COMBINE_CATBAGS_BIZ_XML = "uddi_data/joepublisher/combineCatBagsBusinessServices.xml";
    final static String COMBINE_CATBAGS_BIZ_KEY = "uddi:uddi.joepublisher.com:business01";
    public final static String RIFTSAW_BUSINESS_KEY    = "uddi:riftsaw.jboss.org:business_redhat-jboss";
    public final static String RIFTSAW_BUSINESS_XML    = "uddi_data/bpel/riftsaw/businessEntity.xml";

    final static String SIGNATURE_KEYSTORE = "uddi_data/signature/signature.jks";
    final static String SIGNATURE_KEYSTORE_TYPE = "JKS";
    final static String SIGNATURE_KEYSTORE_PASSWORD = "changeit";
    final static String SIGNATURE_KEYSTORE_ALIAS = "mykey";
    
    private Log logger = LogFactory.getLog(this.getClass());
	private UDDIPublicationPortType publication = null;
    private UDDIInquiryPortType inquiry = null;
	
	public TckBusiness(UDDIPublicationPortType publication, 
				  UDDIInquiryPortType inquiry) {
		super();
		this.publication = publication;
		this.inquiry = inquiry;
	}
	
	public void saveSamSyndicatorBusiness(String authInfoSam) {
		saveBusiness(authInfoSam, SAM_BUSINESS_XML, SAM_BUSINESS_KEY);
	}

	public void saveSamSyndicatorBusinesses(String authInfoSam, int numberOfCopies) {
		saveBusinesses(authInfoSam, SAM_BUSINESS_XML, SAM_BUSINESS_KEY, numberOfCopies);
	}
	
	public void saveSamSyndicatorBusinessWithProjection(String authInfoSam) {
		saveBusiness(authInfoSam, SAM_BUSINESS_WITHPROJECTION_XML, SAM_BUSINESS_KEY);
	}
	
	public void deleteSamSyndicatorBusiness(String authInfoSam) {
		deleteBusiness(authInfoSam, SAM_BUSINESS_XML, SAM_BUSINESS_KEY);
	}
	
	public void deleteSamSyndicatorBusinesses(String authInfoSam, int numberOfCopies) {
		deleteBusinesses(authInfoSam, SAM_BUSINESS_XML, SAM_BUSINESS_KEY, numberOfCopies);
	}
	
        public void saveJoePublisherBusinessX509Signature(String authInfoJoe) {
            saveBusiness(authInfoJoe, JOE_BUSINESS_XML, JOE_BUSINESS_KEY);
            signBusiness(authInfoJoe, JOE_BUSINESS_KEY);
            verifyBusinessSignature(authInfoJoe, JOE_BUSINESS_KEY);
        }
        
	public void saveJoePublisherBusiness(String authInfoJoe) {
		saveBusiness(authInfoJoe, JOE_BUSINESS_XML, JOE_BUSINESS_KEY, true);
    }
	
	public void saveCombineCatBagsPublisherBusiness(String authInfoJoe) {
		saveBusiness(authInfoJoe, COMBINE_CATBAGS_BIZ_XML, COMBINE_CATBAGS_BIZ_KEY);
    }
	
	public void saveJoePublisherBusiness3(String authInfoJoe) {
		saveBusiness(authInfoJoe, JOE_BUSINESS3_XML, JOE_BUSINESS3_KEY);
    }
	
	public void saveJoePublisherBusiness1to3(String authInfoJoe) {
		saveBusiness(authInfoJoe, JOE_BUSINESS_MOVE_XML, JOE_BUSINESS3_KEY);
    }
	
	public void saveMaryPublisherBusiness(String authInfoMary) {
		saveBusiness(authInfoMary, MARY_BUSINESS_XML, MARY_BUSINESS_KEY);
    }
	
	public void updateJoePublisherBusiness(String authInfoJoe) {
		updateBusiness(authInfoJoe, JOE_BUSINESS_XML, JOE_BUSINESS_KEY);
    }
	
	public void saveJoePublisherBusinesses(String authInfoJoe, int numberOfCopies) {
    	saveBusinesses(authInfoJoe, JOE_BUSINESS_XML, JOE_BUSINESS_KEY, numberOfCopies);
    }
    
	public void deleteJoePublisherBusiness(String authInfoJoe) {
    	deleteBusiness(authInfoJoe, JOE_BUSINESS_XML, JOE_BUSINESS_KEY);
    }
	
	public void deleteJoePublisherBusiness3(String authInfoJoe) {
    	deleteBusiness(authInfoJoe, JOE_BUSINESS3_XML, JOE_BUSINESS3_KEY);
    }
	
	public void deleteMaryPublisherBusiness(String authInfoMary) {
    	deleteBusiness(authInfoMary, MARY_BUSINESS_XML, MARY_BUSINESS_KEY);
    }
	
	public void deleteJoePublisherBusinesses(String authInfoJoe, int numberOfCopies) {
    	deleteBusinesses(authInfoJoe, JOE_BUSINESS_XML, JOE_BUSINESS_KEY, numberOfCopies);
    }
	
	public void checkServicesBusinessOne(int expectedNumberOfServices) {
		checkNumberOfServices(JOE_BUSINESS_KEY,expectedNumberOfServices);
	}
	
	public void checkServicesBusinessThree(int expectedNumberOfServices) {
		checkNumberOfServices(JOE_BUSINESS3_KEY,expectedNumberOfServices);
	}
	 
	public void saveBusinesses(String authInfo, String businessXML, String businessKey, int numberOfCopies) {
		try {			
			BusinessEntity beIn = (BusinessEntity)EntityCreator.buildFromDoc(businessXML, "org.uddi.api_v3");
			String businessName = beIn.getName().get(0).getValue();
			for (int i=0; i<numberOfCopies; i++) {
				SaveBusiness sb = new SaveBusiness();
				sb.setAuthInfo(authInfo);
				beIn.getName().get(0).setValue(businessName + "-" + i);
				beIn.setBusinessKey(businessKey + "-" + i);
				sb.getBusinessEntity().add(beIn);
				publication.saveBusiness(sb);
				logger.debug("Saved business with key " + businessName + "-" + i);
			}
			
		} catch(Throwable e) {
			logger.error(e.getMessage(),e);
			Assert.fail("No exception should be thrown");
		}
	}
	
	public void checkNumberOfServices(String businessKey, int expectedServices) {
		
		try {
			GetBusinessDetail gb = new GetBusinessDetail();
			gb.getBusinessKey().add(businessKey);
			BusinessDetail bd;
			bd = inquiry.getBusinessDetail(gb);
			List<BusinessEntity> beOutList = bd.getBusinessEntity();
			BusinessEntity beOut = beOutList.get(0);
			if (expectedServices > 0) {
				assertEquals(expectedServices, beOut.getBusinessServices().getBusinessService().size());
			} else {
				Assert.assertNull(beOut.getBusinessServices());
			}
		} catch (RemoteException e) {
			logger.error(e.getMessage(),e);
			Assert.fail("No exception should be thrown");
		}
	}
	
        public void signBusiness(String authInfo, String businessKey) {
            try {
                GetBusinessDetail gb = new GetBusinessDetail();
                gb.getBusinessKey().add(businessKey);
                BusinessDetail bd = inquiry.getBusinessDetail(gb);
                List<BusinessEntity> beOutList = bd.getBusinessEntity();
                BusinessEntity bizEntity = beOutList.get(0);
                bizEntity.getSignature().clear();
                BusinessEntity bizEntitySigned = signJAXBObject(bizEntity);
                        
                SaveBusiness sb = new SaveBusiness();
                sb.setAuthInfo(authInfo);
                sb.getBusinessEntity().add(bizEntitySigned);
                publication.saveBusiness(sb);
            } catch(Throwable e) {
                logger.error(e.getMessage(),e);
                Assert.fail("No exception should be thrown");
            }
        }
        
        public void verifyBusinessSignature(String authInfo, String businessKey) {
            try {
                GetBusinessDetail gb = new GetBusinessDetail();
                gb.getBusinessKey().add(businessKey);
                BusinessDetail bd = inquiry.getBusinessDetail(gb);
                List<BusinessEntity> beOutList = bd.getBusinessEntity();
                BusinessEntity bizEntity = beOutList.get(0);
                        
                boolean sigOk = verifySignedJAXBObject(bizEntity);
                assertTrue("Signature invalid!", sigOk);
            } catch(Throwable e) {
                logger.error(e.getMessage(),e);
                Assert.fail("No exception should be thrown");
            }
        }
        
        private <T> T signJAXBObject(T jaxbObj) {
            DOMResult domResult = new DOMResult();
            JAXB.marshal(jaxbObj, domResult);
            Document doc = ((Document)domResult.getNode());
            Element docElement = doc.getDocumentElement();

            try {            
                KeyStore ks = KeyStore.getInstance(SIGNATURE_KEYSTORE_TYPE);
                URL url = Thread.currentThread().getContextClassLoader().getResource(SIGNATURE_KEYSTORE);
                ks.load(url.openStream(), SIGNATURE_KEYSTORE_PASSWORD.toCharArray());
                KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry)ks.getEntry(SIGNATURE_KEYSTORE_ALIAS, new KeyStore.PasswordProtection(SIGNATURE_KEYSTORE_PASSWORD.toCharArray()));
                PrivateKey privateKey = keyEntry.getPrivateKey();
                Certificate origCert = keyEntry.getCertificate();
                PublicKey validatingKey = origCert.getPublicKey();
                TckSigningUtil.signDOM(docElement, privateKey, origCert);

                DOMSource domSource = new DOMSource(doc);
                T result = (T)JAXB.unmarshal(domSource, jaxbObj.getClass());
                return result;
            } catch (Exception e) {
                throw new RuntimeException("Signature failure due to: " + e.getMessage(), e);
            }
        }
        
        private boolean verifySignedJAXBObject(Object obj) {
            try {
                DOMResult domResult = new DOMResult();
                JAXB.marshal(obj, domResult);
                Document doc = ((Document)domResult.getNode());
                Element docElement = doc.getDocumentElement();

                KeyStore ks = KeyStore.getInstance(SIGNATURE_KEYSTORE_TYPE);
                URL url = Thread.currentThread().getContextClassLoader().getResource(SIGNATURE_KEYSTORE);
                ks.load(url.openStream(), SIGNATURE_KEYSTORE_PASSWORD.toCharArray());
                KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry)ks.getEntry(SIGNATURE_KEYSTORE_ALIAS, new KeyStore.PasswordProtection(SIGNATURE_KEYSTORE_PASSWORD.toCharArray()));
                PrivateKey privateKey = keyEntry.getPrivateKey();
                Certificate origCert = keyEntry.getCertificate();
                PublicKey validatingKey = origCert.getPublicKey();
                return TckSigningUtil.verifySignature(docElement, validatingKey);
            } catch (Exception e) {
                throw new RuntimeException (e);
            }
        }
        
        public void saveBusiness(String authInfo, String businessXML, String businessKey) {
            saveBusiness(authInfo, businessXML, businessKey, false);
        }
	public void saveBusiness(String authInfo, String businessXML, String businessKey, boolean serialize) {
		try {
			SaveBusiness sb = new SaveBusiness();
			sb.setAuthInfo(authInfo);

			BusinessEntity beIn = (BusinessEntity)EntityCreator.buildFromDoc(businessXML, "org.uddi.api_v3");
			sb.getBusinessEntity().add(beIn);
			
			publication.saveBusiness(sb);
	
			// Now get the entity and check the values
			GetBusinessDetail gb = new GetBusinessDetail();
			gb.getBusinessKey().add(businessKey);
			BusinessDetail bd = inquiry.getBusinessDetail(gb);
			List<BusinessEntity> beOutList = bd.getBusinessEntity();
			BusinessEntity beOut = beOutList.get(0);

            if (serialize) {
                JAXB.marshal(beOut, new File("target/aftersave.xml"));
            }
            
			assertEquals(beIn.getBusinessKey(), beOut.getBusinessKey());
			
			TckValidator.checkNames(beIn.getName(), beOut.getName());
			TckValidator.checkDescriptions(beIn.getDescription(), beOut.getDescription());
			TckValidator.checkDiscoveryUrls(beIn.getDiscoveryURLs(), beOut.getDiscoveryURLs());
			TckValidator.checkContacts(beIn.getContacts(), beOut.getContacts());
			TckValidator.checkCategories(beIn.getCategoryBag(), beOut.getCategoryBag());
			TckValidator.checkSignatures(beIn.getSignature(), beOut.getSignature());
		} catch(Throwable e) {
			logger.error(e.getMessage(),e);
			Assert.fail("No exception should be thrown");
		}

	}
	
	public void updateBusiness(String authInfo, String businessXML, String businessKey) {
		try {
			
			// Now get the entity and check the values
			GetBusinessDetail gb = new GetBusinessDetail();
			gb.getBusinessKey().add(businessKey);
			BusinessDetail bd = inquiry.getBusinessDetail(gb);
			List<BusinessEntity> beOutList = bd.getBusinessEntity();
			BusinessEntity beOut = beOutList.get(0);
			//We are expecting 2 services
			assertEquals(2,beOut.getBusinessServices().getBusinessService().size());
			
			//Now updating the business by adding another description
			SaveBusiness sb = new SaveBusiness();
			sb.setAuthInfo(authInfo);
			BusinessEntity beIn = beOut;
			Description desc2= new Description();
			desc2.setLang("nl");
			desc2.setValue("Omschrijving");
			beIn.getDescription().add(desc2);
			sb.getBusinessEntity().add(beIn);
			publication.saveBusiness(sb);
	
			// Now get the entity and check the values
			BusinessDetail bdnew = inquiry.getBusinessDetail(gb);
			List<BusinessEntity> beOutListNew = bdnew.getBusinessEntity();
			BusinessEntity beOutNew = beOutListNew.get(0);

			assertEquals(beIn.getBusinessKey(), beOutNew.getBusinessKey());
			// After the update we still are supposed to see two services.
			assertNotNull(beOutNew.getBusinessServices());
			assertEquals(2,beOutNew.getBusinessServices().getBusinessService().size());
			
			
		} catch(Throwable e) {
			logger.error(e.getMessage(),e);
			Assert.fail("No exception should be thrown");
		}

	}
	
	public void deleteBusinesses(String authInfo, String businessXML, String businessKey, int numberOfCopies) {
		try {
			for (int i=0; i<numberOfCopies; i++) {
				// Delete the entity and make sure it is removed
				String key = businessKey + "-" + i;
				DeleteBusiness db = new DeleteBusiness();
				db.setAuthInfo(authInfo);
				db.getBusinessKey().add(key);
				publication.deleteBusiness(db);
				logger.debug("Deleted business with key " + key);
			}
			
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("No exception should be thrown");
		}
	}
	
	
	public void deleteBusiness(String authInfo, String businessXML, String businessKey) {
		try {
			// Delete the entity and make sure it is removed
			DeleteBusiness db = new DeleteBusiness();
			db.setAuthInfo(authInfo);
			db.getBusinessKey().add(businessKey);
			publication.deleteBusiness(db);
			
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("No exception should be thrown");
		}
	}
	

}