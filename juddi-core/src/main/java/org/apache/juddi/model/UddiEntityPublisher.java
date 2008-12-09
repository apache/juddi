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

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Table;

import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.validation.ValidateUDDIKey;
import org.apache.juddi.keygen.KeyGenerator;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "uddi_publisher")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UddiEntityPublisher {
	

	protected String authorizedName;
	protected Set<KeyGeneratorKey> keyGeneratorKeys = new HashSet<KeyGeneratorKey>(0);

	@Id
	@Column(name = "authorized_name", nullable = false, length = 20)
	public String getAuthorizedName() {
		return this.authorizedName;
	}

	public void setAuthorizedName(String authorizedName) {
		this.authorizedName = authorizedName;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "publisher")
	@OrderBy
	public Set<KeyGeneratorKey> getKeyGeneratorKeys() {
		return this.keyGeneratorKeys;
	}
	public void setKeyGeneratorKeys(Set<KeyGeneratorKey> keyGeneratorKeys) {
		this.keyGeneratorKeys = keyGeneratorKeys;
	}
	public void addKeyGeneratorKey(String keygenTModelKey) {
		KeyGeneratorKeyId keyGenKeyId = new KeyGeneratorKeyId(this.authorizedName, this.keyGeneratorKeys.size());
		KeyGeneratorKey keyGenKey = new KeyGeneratorKey(keyGenKeyId, this, keygenTModelKey);
		keyGeneratorKeys.add(keyGenKey);
	}
	public void removeKeyGeneratorKey(String keygenTModelKey) {
		// Must use iterator to remove while iterating.
		Iterator<KeyGeneratorKey> keyGenItr = keyGeneratorKeys.iterator();
		while(keyGenItr.hasNext()) {
			KeyGeneratorKey keyGen = keyGenItr.next();
			if (keyGen.getKeygenTModelKey().equalsIgnoreCase(keygenTModelKey))
				keyGeneratorKeys.remove(keyGen);
		}
	}
	
	public boolean isOwner(UddiEntity entity){
		boolean ret = false;
		if (entity != null) {
			if (entity.retrieveAuthorizedName().equals(this.authorizedName))
				ret = true;
		}
		return ret;
	}

	public boolean isValidPublisherKey(String key) {
		if (key == null)
			return false;

		String keyPartition = key.substring(0, key.lastIndexOf(KeyGenerator.PARTITION_SEPARATOR) - 1);
		
		for (KeyGeneratorKey keyGenKey : keyGeneratorKeys) {
			String keyGenPartition = keyGenKey.getKeygenTModelKey().substring(0, key.lastIndexOf(KeyGenerator.PARTITION_SEPARATOR) - 1);
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

		// First make sure the key is a valid UDDIv3 key
		ValidateUDDIKey.validateUDDIv3KeyGeneratorKey(keygenKey);
		
		String keyGenSuffix = (KeyGenerator.PARTITION_SEPARATOR + KeyGenerator.KEYGENERATOR_SUFFIX).toUpperCase();
		if (!(keygenKey.toUpperCase().endsWith(keyGenSuffix)))
			return false;
		
		String partition = keygenKey.toUpperCase().substring(0, keygenKey.length() - keyGenSuffix.length());
		
		StringTokenizer tokenizer = new StringTokenizer(partition, KeyGenerator.PARTITION_SEPARATOR);
		// Must have 3 or more tokens as the first is the uddi scheme and the second is the domain key.
		if (tokenizer.countTokens() < 3)
			return false;
		
		Vector<DynamicQuery.Parameter> params = new Vector<DynamicQuery.Parameter>(0);
		String subPartition = "";
		for (int count = 0; tokenizer.hasMoreElements(); count++) {
			String nextToken = (String)tokenizer.nextElement();
			if (count == 0) {
				subPartition = nextToken;
			}
			else {
				subPartition = subPartition + KeyGenerator.PARTITION_SEPARATOR + nextToken;
				if (count > 1) {
					DynamicQuery.Parameter param = new DynamicQuery.Parameter("UPPER(k.keygenTModelKey)", 
																			  subPartition + DynamicQuery.WILDCARD, 
																			  DynamicQuery.PREDICATE_LIKE);
					params.add(param);
				}
			}
		}

		if (params.size() == 0)
			return false;

		DynamicQuery checkTokensQry = new DynamicQuery();
		checkTokensQry.append("select k.keygenTModelKey from KeyGeneratorKey k ");

		DynamicQuery.Parameter pubParam = new DynamicQuery.Parameter("k.id.authorizedName", 
																	 this.authorizedName, 
																	 DynamicQuery.PREDICATE_NOTEQUALS);
		checkTokensQry.WHERE().pad().appendGroupedAnd(pubParam).pad();
		checkTokensQry.AND().pad();
		checkTokensQry.appendGroupedOr(params.toArray(new DynamicQuery.Parameter[0])).pad();

		Query qry = checkTokensQry.buildJPAQuery(em);
		List<?> obj = qry.getResultList();
		// If even one of the partitions are taken by another publisher, then the key generator is unavailable
		if (obj != null && obj.size() > 0)
			return false;
		
		return true;
	}

}
