/*
 * Copyright 2013 The Apache Software Foundation.
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

package org.apache.juddi.security;

import java.util.List;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessInfo;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.TModel;

/**
 * Provides an interface for a pluggable Fine Grained Access Control mechanism for jUDDI
 * @author Alex O'Ree
 * @since 3.2
 * @see AccessLevel
 */
public interface IAccessControl {

    public List<BusinessService> FilterServices(String username, List<BusinessService> services);
    
    public List<BusinessEntity> FilterBusinesses(String username, List<BusinessEntity> business);
    
    public List<BusinessInfo> FilterBusinessInfo(String username, List<BusinessInfo> business);
    
    
    public List<TModel> FilterTModels(String username, List<TModel> tmodels);
    
    public List<BindingTemplate> FilterBindingTemplates(String username, List<BindingTemplate> bindings);
    
    public boolean HasAccess(AccessLevel level, String username, List<String> keys);
    
    public void AssertAccess(AccessLevel level, String username, List<String> keys);
    /**
     * return true if and only if the specified username has administrative rights for this instance of jUDDI
     * @param username
     * @return
     * @throws IllegalArgumentException 
     */
    public boolean IsAdmin(String username) throws IllegalArgumentException;
}
