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

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@MappedSuperclass
public abstract class UddiEntityPublisher {

	protected String publisherId;

	@Id
	@Column(name = "publisher_id", nullable = false, length = 20)
	public String getPublisherId() {
		return this.publisherId;
	}

	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}
	
	
	public boolean isOwner(UddiEntity entity){
		boolean ret = false;
		if (entity != null) {
			if (entity.retrievePublisherId().equals(this.publisherId))
				ret = true;
		}
		return ret;
	}
}
