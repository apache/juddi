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

package org.apache.juddi.validation;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.UDDIService;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.TModel;
import org.uddi.v3_service.UDDIValueSetValidationPortType;
import org.uddi.vs_v3.ValidateValues;

/**
 *
 * @author Alex O'Ree
 */
public class ValidateValuesFromWebService {

        static final Log log = LogFactory.getLog(ValidateValuesFromWebService.class);
        public static void Validate(String url, Object tm) throws ValueNotAllowedException{
                UDDIService svc = new UDDIService();
                UDDIValueSetValidationPortType vsv = svc.getUDDIValueSetValidationPort();
                ((BindingProvider)vsv).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
                ValidateValues req = new ValidateValues();
                //req.getTModel().add(tm);
                //TODO finish this
                try {
                        DispositionReport validateValues = vsv.validateValues(req);
                } catch (Exception ex) {
                        log.warn(ex);
                        ValueNotAllowedException x = new ValueNotAllowedException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", ex.getMessage()));
                } 
        }
        
        
}
