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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.ValidValues;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.mapping.MappingModelToApi;
import org.apache.juddi.model.Tmodel;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.ValueNotAllowedException;
import org.uddi.api_v3.TModel;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class ValidateValueSetValidation extends ValidateUDDIApi {

        private static Log log = LogFactory.getLog(ValidateValueSetValidation.class);

        public ValidateValueSetValidation(UddiEntityPublisher publisher) {
                super(publisher);
        }

        /**
         * called from jUDDI API SetAllValidValues
         *
         * @param values
         */
        public void validateSetAllValidValues(List<ValidValues> values) throws ValueNotAllowedException {
                if (values == null || values.isEmpty()) {
                        throw new ValueNotAllowedException(new ErrorMessage("errors.NullInput"));
                }

                for (int i = 0; i < values.size(); i++) {
                        String key = values.get(i).getTModekKey();
                        if (key == null || key.trim().length() == 0) {
                                throw new ValueNotAllowedException(new ErrorMessage("errors.NullInput", "tModel key"));
                        }
                        //ensure tmodel exists
                        Tmodel tm = GetTModel_MODEL_IfExists(values.get(i).getTModekKey());
                        
                        if (tm == null) {
                                throw new ValueNotAllowedException(new ErrorMessage("errors.invalidkey.TModelNotFound", key));
                        }
                        //ensure caller owns the tModel
                        if (this.publisher.isOwner(tm)) {
                                throw new ValueNotAllowedException(new ErrorMessage("errors.usermismatch.InvalidOwner", key));
                        }

                        //if we have no values, it may be to simply unset any values

                        /*//validate that we have values
                         if (values.get(i).getValue() == null || values.get(i).getValue().isEmpty()) {
                         throw new ValueNotAllowedException(new ErrorMessage("errors.NullInput", "value[]"));
                         }
                         //and that they aren't empty
                         for (int k = 0; k < values.get(i).getValue().size(); k++) {
                         if (values.get(i).getValue().get(k) == null || values.get(i).getValue().get(k).trim().length() == 0) {
                         throw new ValueNotAllowedException(new ErrorMessage("errors.NullInput", "value[" + i + "].value"));
                         }
                         }*/
                }
        }

        /**
         * return the publisher
         *
         * @param tmodelKey
         * @return
         * @throws ValueNotAllowedException
         */
        public static TModel GetTModel_API_IfExists(String tmodelKey) throws ValueNotAllowedException {
                EntityManager em = PersistenceManager.getEntityManager();

                TModel apitmodel = null;
                if (em == null) {
                        //this is normally the Install class firing up
                        log.warn(new ErrorMessage("errors.tmodel.ReferentialIntegrityNullEM"));
                        return null;
                } else {


                        EntityTransaction tx = em.getTransaction();
                        try {
                                Tmodel modelTModel = null;
                                tx.begin();
                                modelTModel = em.find(org.apache.juddi.model.Tmodel.class, tmodelKey);
                                if (modelTModel != null) {
                                        apitmodel = new TModel();
                                        try {
                                                MappingModelToApi.mapTModel(modelTModel, apitmodel);
                                        } catch (DispositionReportFaultMessage ex) {
                                                log.warn(ex);
                                                apitmodel=null;
                                        }


                                }
                                tx.commit();
                        } finally {
                                if (tx.isActive()) {
                                        tx.rollback();
                                }
                                em.close();
                        }

                }
                return apitmodel;
        }
        
        
        public static Tmodel GetTModel_MODEL_IfExists(String tmodelKey) throws ValueNotAllowedException {
                EntityManager em = PersistenceManager.getEntityManager();

                Tmodel model = null;
                if (em == null) {
                        //this is normally the Install class firing up
                        log.warn(new ErrorMessage("errors.tmodel.ReferentialIntegrityNullEM"));
                        return null;
                } else {


                        EntityTransaction tx = em.getTransaction();
                        try {
                                
                                tx.begin();
                                model = em.find(org.apache.juddi.model.Tmodel.class, tmodelKey);
                                tx.commit();
                        } finally {
                                if (tx.isActive()) {
                                        tx.rollback();
                                }
                                em.close();
                        }

                }
                return model;
        }
}
