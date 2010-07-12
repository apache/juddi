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

package org.apache.juddi.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.juddi.model.TransferTokenKey;
import org.apache.juddi.model.UddiEntity;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.query.util.DynamicQuery;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.InvalidKeyPassedException;
import org.apache.juddi.v3.error.TokenAlreadyExistsException;
import org.apache.juddi.v3.error.TransferNotAllowedException;
import org.apache.juddi.v3.error.UserMismatchException;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.uddi.custody_v3.DiscardTransferToken;
import org.uddi.custody_v3.KeyBag;
import org.uddi.custody_v3.TransferEntities;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class ValidateCustodyTransfer extends ValidateUDDIApi {

	public ValidateCustodyTransfer(UddiEntityPublisher publisher) {
		super(publisher);
	}
	
	public void validateDiscardTransferToken(EntityManager em, DiscardTransferToken body) throws DispositionReportFaultMessage {
		// No null input
		if (body == null)
			throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
		
		KeyBag keyBag = body.getKeyBag();
		
		// The call must contain at least a transfer token or keyBag
		if (body.getTransferToken() == null && keyBag == null)
			throw new FatalErrorException(new ErrorMessage("errors.discardtransfertoken.NoInput"));
		
		if (keyBag != null) {
			List<String> keyList = keyBag.getKey();
			if (keyList == null || keyList.size() == 0)
				throw new ValueNotAllowedException(new ErrorMessage("errors.keybag.NoInput"));
			
			// Test that publisher owns keys using operational info.
			int i = 0;
			for (String key : keyList) {
				// Per section 4.4: keys must be case-folded
				key = key.toLowerCase();
				keyList.set(i, key);
				
				UddiEntity uddiEntity = em.find(UddiEntity.class, key);
				
				// According to spec, it's ok if a key doesn't match any known entities, it will just be ignored.  However, the publisher must own
				// the entity in order to discard the associated token.
				if (uddiEntity != null) {
					if (!publisher.isOwner(uddiEntity))
						throw new UserMismatchException(new ErrorMessage("errors.usermismatch.InvalidOwner", key));
					
				}
			
				i++;
			}
		
		}
		
	}
	
	public void validateGetTransferToken(EntityManager em, KeyBag keyBag) throws DispositionReportFaultMessage {

		// No null input
		if (keyBag == null)
			throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
		
		List<String> keyList = keyBag.getKey();
		if (keyList == null || keyList.size() == 0)
			throw new ValueNotAllowedException(new ErrorMessage("errors.keybag.NoInput"));
		
		// Test that publisher owns keys using operational info.
		Vector<DynamicQuery.Parameter> params = new Vector<DynamicQuery.Parameter>(0);
		int i = 0;
		for (String key : keyList) {
			// Per section 4.4: keys must be case-folded
			key = key.toLowerCase();
			keyList.set(i, key);

			UddiEntity uddiEntity = em.find(UddiEntity.class, key);
			
			if (uddiEntity == null)
				throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.EntityNotFound", key));
			
			// Only BusinessEntities or TModels are allowed to be transferred
			if (!(uddiEntity instanceof org.apache.juddi.model.BusinessEntity) &&
				!(uddiEntity instanceof org.apache.juddi.model.Tmodel))
				throw new InvalidKeyPassedException(new ErrorMessage("errors.gettransfertoken.InvalidEntity", key));
			
			if (!publisher.isOwner(uddiEntity))
				throw new UserMismatchException(new ErrorMessage("errors.usermismatch.InvalidOwner", key));
			
			// Creating parameters for key-checking query
			DynamicQuery.Parameter param = new DynamicQuery.Parameter("UPPER(ttk.entityKey)", 
																	  key.toUpperCase(), 
																	  DynamicQuery.PREDICATE_EQUALS);
			params.add(param);

		}

		// Make sure keys aren't implicated in another transfer request
		DynamicQuery checkKeysQry = new DynamicQuery();
		checkKeysQry.append("select ttk.entityKey from TransferTokenKey ttk ");
		checkKeysQry.WHERE().pad().appendGroupedOr(params.toArray(new DynamicQuery.Parameter[0]));

		Query qry = checkKeysQry.buildJPAQuery(em);
		List<?> obj = qry.getResultList();
		if (obj != null && obj.size() > 0)
			throw new TokenAlreadyExistsException(new ErrorMessage("errors.gettransfertoken.KeyExists", (String)obj.get(0)));

	}
	
	public void validateTransferEntities(EntityManager em, TransferEntities body) throws DispositionReportFaultMessage {

		// No null input
		if (body == null)
			throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
		
		org.uddi.custody_v3.TransferToken apiTransferToken = body.getTransferToken();
		if (apiTransferToken == null)
			throw new FatalErrorException(new ErrorMessage("errors.transfertoken.NullInput"));
		
		KeyBag keyBag = body.getKeyBag();
		if (keyBag == null)
			throw new FatalErrorException(new ErrorMessage("errors.keybag.NullInput"));
		
		List<String> apiKeyList = keyBag.getKey();
		if (apiKeyList == null || apiKeyList.size() == 0)
			throw new ValueNotAllowedException(new ErrorMessage("errors.keybag.NoInput"));
		
		String transferTokenId = new String(apiTransferToken.getOpaqueToken());
		org.apache.juddi.model.TransferToken modelTransferToken = em.find(org.apache.juddi.model.TransferToken.class, transferTokenId);
		if (modelTransferToken == null)
			throw new TransferNotAllowedException(new ErrorMessage("errors.transferentities.TokenNotFound", transferTokenId));
		
		Date now = new Date();
		if (now.after(modelTransferToken.getExpirationDate()))
			throw new TransferNotAllowedException(new ErrorMessage("errors.transferentities.TokenExpired", transferTokenId));
		
		List<TransferTokenKey> transferKeyList = modelTransferToken.getTransferKeys();
		List<String> modelKeyList = new ArrayList<String>(0);
		if (transferKeyList != null && transferKeyList.size() > 0) {
			for (TransferTokenKey ttk : transferKeyList)
				modelKeyList.add(ttk.getEntityKey());
		}
		
		// The keys in the supplied key bag must match exactly the keys in the stored transfer and the entities must exist
		Collections.sort(apiKeyList);
		Collections.sort(modelKeyList);
		int count = 0;
		for (String key : apiKeyList) {
			// Per section 4.4: keys must be case-folded
			key = key.toLowerCase();
			apiKeyList.set(count, key);

			if (!key.equalsIgnoreCase(modelKeyList.get(count)))
				throw new TransferNotAllowedException(new ErrorMessage("errors.transferentities.KeyMismatch", key + " & " + modelKeyList.get(count)));
			
			UddiEntity uddiEntity = em.find(UddiEntity.class, key);
			if (uddiEntity == null)
				throw new InvalidKeyPassedException(new ErrorMessage("errors.invalidkey.EntityNotFound", key));
			
			count++;
		}

	}

}
