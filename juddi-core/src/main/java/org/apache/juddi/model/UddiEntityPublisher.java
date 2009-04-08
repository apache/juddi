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

package org.apache.juddi.model;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.apache.juddi.keygen.KeyGenerator;
import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.validation.ValidateUDDIKey;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@MappedSuperclass
public class UddiEntityPublisher {
	

	protected String authorizedName;
	private List<String> keyGeneratorKeys = null;

	public UddiEntityPublisher() {
	}
	
	public UddiEntityPublisher(String authorizedName) {
		this.authorizedName = authorizedName;
	}
	
	@Id
	@Column(name = "authorized_name", nullable = false, length = 255)
	public String getAuthorizedName() {
		return this.authorizedName;
	}
	public void setAuthorizedName(String authorizedName) {
		this.authorizedName = authorizedName;
	}
	
	@Transient
	public List<String> getKeyGeneratorKeys() {
		return keyGeneratorKeys;
	}
	public void setKeyGeneratorKeys(List<String> keyGeneratorKeys) {
		this.keyGeneratorKeys = keyGeneratorKeys;
	}

	@SuppressWarnings("unchecked")
	public void populateKeyGeneratorKeys(EntityManager em) {
		DynamicQuery getKeysQuery = new DynamicQuery();
		getKeysQuery.append("select t.entityKey from Tmodel t").pad().WHERE().pad();

		DynamicQuery.Parameter pubParam = new DynamicQuery.Parameter("t.authorizedName", 
				 getAuthorizedName(), 
				 DynamicQuery.PREDICATE_EQUALS);

		DynamicQuery.Parameter keyParam = new DynamicQuery.Parameter("UPPER(t.entityKey)", 
				 (DynamicQuery.WILDCARD + KeyGenerator.KEYGENERATOR_SUFFIX).toUpperCase(), 
				 DynamicQuery.PREDICATE_LIKE);
		
		
		getKeysQuery.appendGroupedAnd(pubParam, keyParam);
		Query qry = getKeysQuery.buildJPAQuery(em);
		
		keyGeneratorKeys = qry.getResultList();
	}
	
	public boolean isOwner(UddiEntity entity){
		boolean ret = false;
		if (entity != null) {
			if (entity.getAuthorizedName().equals(getAuthorizedName()))
				ret = true;
		}
		return ret;
	}

	
	public boolean isValidPublisherKey(EntityManager em, String key) {
		if (key == null)
			return false;
		
		if (keyGeneratorKeys == null)
			populateKeyGeneratorKeys(em);
		

		String keyPartition = key.substring(0, key.lastIndexOf(KeyGenerator.PARTITION_SEPARATOR));
		
		for (String keyGenKey : keyGeneratorKeys) {
			String keyGenPartition = keyGenKey.substring(0, key.lastIndexOf(KeyGenerator.PARTITION_SEPARATOR));
			if (keyGenPartition.equalsIgnoreCase(keyPartition))
				return true;
		}
		return false;
	}
	
	/*
	 * This method will check if the given key generator key is available for this publisher.  The idea is to make sure that the key generator
	 * and all its sub-partitions are not already taken by another publisher.
	 */
	public boolean isKeyGeneratorAvailable(EntityManager em, String keygenKey) throws DispositionReportFaultMessage {

		// First make sure the key is a valid UDDIv3 key per the specification's rules
		ValidateUDDIKey.validateUDDIv3KeyGeneratorKey(keygenKey);
		
		String partition = keygenKey.toUpperCase().substring(0, keygenKey.lastIndexOf(KeyGenerator.PARTITION_SEPARATOR));
		
		StringTokenizer tokenizer = new StringTokenizer(partition, KeyGenerator.PARTITION_SEPARATOR);
		int tokenCount = tokenizer.countTokens();
		// Must have 2 or more tokens as the first is the uddi scheme and the second is the domain key.
		if (tokenCount < 2)
			return false;

		String domainPartition = (String)tokenizer.nextElement() + KeyGenerator.PARTITION_SEPARATOR + (String)tokenizer.nextElement();
		
		// If three or more tokens then we need to make sure the current publisher has the parent partitions.  For example, you can't register the 
		// uddi:domain:abc:123 key generator without having the uddi:domain and uddi:domain:abc key generators.  This implicitly checks if another
		// publisher has any of these partitions since if they do, current publisher won't have them.
		if (tokenCount > 2) {
			Vector<DynamicQuery.Parameter> params = new Vector<DynamicQuery.Parameter>(0);

			DynamicQuery.Parameter pubParam = new DynamicQuery.Parameter("t.authorizedName", 
					 getAuthorizedName(), 
					 DynamicQuery.PREDICATE_EQUALS);
			
			int requiredCount = 0;
			params.add(new DynamicQuery.Parameter("UPPER(t.entityKey)", 
					(domainPartition + KeyGenerator.PARTITION_SEPARATOR + KeyGenerator.KEYGENERATOR_SUFFIX).toUpperCase(), 
					DynamicQuery.PREDICATE_EQUALS));
			requiredCount++;
			
			String subPartition = domainPartition;
			while (tokenizer.hasMoreElements()) {
				// Don't need to add the last token as it is the proposed key generator.
				if (tokenizer.countTokens() == 1)
					break;

				String nextToken = (String)tokenizer.nextElement();
				subPartition = subPartition + KeyGenerator.PARTITION_SEPARATOR + nextToken;
				DynamicQuery.Parameter param = new DynamicQuery.Parameter("UPPER(t.entityKey)", 
						(subPartition + KeyGenerator.PARTITION_SEPARATOR + KeyGenerator.KEYGENERATOR_SUFFIX).toUpperCase(), 
						DynamicQuery.PREDICATE_EQUALS);
				params.add(param);
				requiredCount++;
			}
			
			DynamicQuery checkParentKeyQry = new DynamicQuery();
			checkParentKeyQry.append("select COUNT(t.entityKey) from Tmodel t").pad();

			checkParentKeyQry.WHERE().pad().appendGroupedAnd(pubParam);
			checkParentKeyQry.AND().pad().appendGroupedOr(params.toArray(new DynamicQuery.Parameter[0]));
			
			Query qry = checkParentKeyQry.buildJPAQuery(em);			
			Number resultCount = (Number)qry.getSingleResult();
			if (resultCount.longValue() != requiredCount)
				return false;
		}
		else {
			// If only two tokens, then a domain key generator is being checked.  A domain key generator can only be registered if no other publishers
			// own it.  For example, if trying to register the uddi:domain:abc:123 key then uddi:domain cannot be owned by another publisher.
			DynamicQuery.Parameter notPubParam = new DynamicQuery.Parameter("t.authorizedName", 
					 getAuthorizedName(), 
					 DynamicQuery.PREDICATE_NOTEQUALS);

			DynamicQuery.Parameter keyParam = new DynamicQuery.Parameter("UPPER(t.entityKey)", 
					(domainPartition + KeyGenerator.PARTITION_SEPARATOR + KeyGenerator.KEYGENERATOR_SUFFIX).toUpperCase(), 
					DynamicQuery.PREDICATE_EQUALS);
			
			DynamicQuery checkDomainKeyQry = new DynamicQuery();
			checkDomainKeyQry.append("select t.entityKey from Tmodel t").pad();
			
			checkDomainKeyQry.WHERE().pad().appendGroupedAnd(notPubParam, keyParam);

			Query qry = checkDomainKeyQry.buildJPAQuery(em);
			List<?> obj = qry.getResultList();
			// If there are results then another publisher has the domain key and therefore the key generator is unavailable
			if (obj != null && obj.size() > 0)
				return false;
		}
		
		return true;
	}

}
