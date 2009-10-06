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

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "j3_service_projection")
public class ServiceProjection implements java.io.Serializable {
		
		private static final long serialVersionUID = -8404899558507142913L;
		@EmbeddedId
		private ServiceProjectionId id = new ServiceProjectionId();
		@ManyToOne
		@JoinColumn(name = "business_key", insertable = false, updatable = false)
		private BusinessEntity businessEntity;
		@ManyToOne
		@JoinColumn(name = "service_key", insertable = false, updatable = false)
		private BusinessService businessService;
		
		public ServiceProjection() {
		}
		
		public ServiceProjection(BusinessEntity businessEntity, BusinessService businessService) {
			this.businessEntity = businessEntity;
			this.businessService = businessService;
			
			this.id.businessKey = businessEntity.entityKey;
			this.id.serviceKey = businessService.entityKey;
			
			businessEntity.getServiceProjections().add(this);
			businessService.getProjectingBusinesses().add(this);
		}

		public ServiceProjectionId getId() {
			return id;
		}
		public void setId(ServiceProjectionId id) {
			this.id = id;
		}

		public BusinessEntity getBusinessEntity() {
			return businessEntity;
		}
		public void setBusinessEntity(BusinessEntity businessEntity) {
			this.businessEntity = businessEntity;
		}

		public BusinessService getBusinessService() {
			return businessService;
		}
		public void setBusinessService(BusinessService businessService) {
			this.businessService = businessService;
		}
		
}
