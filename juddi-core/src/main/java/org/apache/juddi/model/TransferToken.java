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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "j3_transfer_token")
public class TransferToken implements java.io.Serializable{

	private static final long serialVersionUID = -7361461730400118274L;
	private String transferToken;
	private Date expirationDate;
	protected List<TransferTokenKey> transferKeys = new ArrayList<TransferTokenKey>(0);
	
	public TransferToken() {
	}

	public TransferToken(String transferToken, Date expirationDate,
			List<TransferTokenKey> transferKeys) {
		this.transferToken = transferToken;
		this.expirationDate = expirationDate;
		this.transferKeys = transferKeys;
	}

	@Id
	@Column(name = "transfer_token", nullable = false, length = 51)
	public String getTransferToken() {
		return transferToken;
	}
	public void setTransferToken(String transferToken) {
		this.transferToken = transferToken;
	}

    @Column(name="expiration_date", nullable = false, updatable = false)
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "transferToken")
	@OrderBy
	public List<TransferTokenKey> getTransferKeys() {
		return transferKeys;
	}
	public void setTransferKeys(List<TransferTokenKey> transferKeys) {
		this.transferKeys = transferKeys;
	}
	

}
