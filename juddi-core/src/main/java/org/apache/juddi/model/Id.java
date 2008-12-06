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
import javax.persistence.MappedSuperclass;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@MappedSuperclass
public class Id {

	protected String entityKey;

	@Column(name = "entity_key", nullable = false, length = 255)
	public String getEntityKey() {
		return this.entityKey;
	}
	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}
	
}
