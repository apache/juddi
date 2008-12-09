package org.apache.juddi.model;
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
 */

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Embeddable
public class TransferTokenKeyId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String transferToken;
	private int keyId;

	public TransferTokenKeyId() {
	}

	public TransferTokenKeyId(String transferToken, int keyId) {
		this.transferToken = transferToken;
		this.keyId = keyId;
	}

	@Column(name = "transfer_token", nullable = false, length = 51)
	public String getTransferToken() {
		return this.transferToken;
	}
	public void setTransferToken(String transferToken) {
		this.transferToken = transferToken;
	}

	@Column(name = "key_id", nullable = false)
	public int getKeyId() {
		return this.keyId;
	}
	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TransferTokenKeyId))
			return false;
		TransferTokenKeyId castOther = (TransferTokenKeyId) other;

		return ((this.getTransferToken() == castOther.getTransferToken()) || (this.getTransferToken() != null
				&& castOther.getTransferToken() != null && this.getTransferToken()
				.equals(castOther.getTransferToken())))
				&& (this.getKeyId() == castOther.getKeyId());
	}

	public int hashCode() {
		int result = 17;

		result = 37
				* result
				+ (getTransferToken() == null ? 0 : this.getTransferToken()
						.hashCode());
		result = 37 * result + this.getKeyId();
		return result;
	}

}
