/*
 * Copyright 2014 The Apache Software Foundation.
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
package org.apache.juddi.validation.vsv;

import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.apache.juddi.config.PersistenceManager;
import org.apache.juddi.model.Tmodel;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.InvalidValueException;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.PublisherAssertion;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInstanceInfo;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * UDDI provides a mechanism that may be used by publishers to tag their
 * businessEntities and tModels with information that identifies them according
 * to any number of identification systems. This tModel represents an identifier
 * system that may be used to identify the tModel or businessEntity that
 * logically replaces the tModel or businessEntity in which it is used. This
 * version of the isReplacedBy identifier system replaces the prior version of
 * this identifier system by providing a means for referring to replacement
 * entities that have version 3 format keys.
 *
 * It is often desirable to gracefully retire a tModel in favor of a
 * replacement. For example, when a Web service definition is replaced by an
 * incompatible version, the publisher of the specification may wish to leave
 * the tModel for the existing definition in place so that existing uses will
 * not be disturbed, while at the same time making it clear that there is a
 * replacement available. The UDDI isReplacedBy identifier system, coupled with
 * the behavior of UDDI with respect to obsolete tModels, fills this need by
 * allowing the obsolete tModel to point to its replacement. See Section 5.2.11
 * delete_tModel.
 *
 * The isReplacedBy identifier system exists in prior versions of UDDI.
 * keyedReferences that refer to this original isReplacedBy identifier system
 * contain entity keys in the version 1 and 2 formats (as UUIDs with the uuid or
 * no scheme prefix). When accessed using a prior version API in a multi-version
 * registry, the older isReplacedBy identifier system yields valid references to
 * businessEntity or tModel keys that are in the format of the prior version,
 * and thus remain valid. When viewed using the version 3 UDDI API these same
 * references to the earlier isReplacedBy identifier system contain invalid
 * version 3 format keys. A new version of this identifier system is required to
 * be able to reference the set of values defined by version 3 format keys.
 * 
* <p class="MsoBodyText">The keyValues in keyedReferences that refer to this
 * tModel must be tModelKeys or businessKeys. Such a keyValue specifies the
 * entity that is the replacement for the entity in which the keyedReference
 * appears. The above and further validation requirements are as follows:</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in">a.<span
 * style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>In the case where a
 * reference is made from an obsolete business entity the following validation
 * rules apply:</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.25in;text-indent:-.25in">1.<span
 * style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>reference to a new
 * business entity; this is a valid operation</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.25in;text-indent:-.25in">2.<span
 * style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>reference to self;
 * this is invalid</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.25in;text-indent:-.25in">3.<span
 * style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>reference to a
 * service, binding or tModel; this is an invalid operation given that the
 * entity being pointed to must be a business</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.25in;text-indent:-.25in">4.<span
 * style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>reference to another
 * publisher’s business; this is a valid operation; no ownership check is
 * made</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.25in;text-indent:-.25in">5.<span
 * style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>reference to another
 * publisher’s service, binding or tModel; this is an invalid operation because
 * of a.3 above</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.25in;text-indent:-.25in">6.<span
 * style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>reference to invalid
 * keys; this is an invalid operation; a key must be valid.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in">b.<span
 * style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * </span>In the case where a reference is made from an obsolete tModel the
 * following validation rules apply:</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.25in;text-indent:-.25in">1.<span
 * style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>reference to a new
 * tModel; this is a valid operation</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.25in;text-indent:-.25in">2.<span
 * style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>reference to self;
 * this is invalid</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.25in;text-indent:-.25in">3.<span
 * style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>reference to a
 * service, binding or business; this is an invalid operation given that the
 * entity being pointed to must be a tModel</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.25in;text-indent:-.25in">4.<span
 * style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>reference to another
 * publisher’s tModel; this is a valid operation; no ownership check is made</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.25in;text-indent:-.25in">5.<span
 * style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>reference to another
 * publisher’s service, binding or business; this is an invalid operation
 * because of b.3 above</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.25in;text-indent:-.25in">6.<span
 * style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>reference to invalid
 * keys; this is an invalid operation; a key must be valid.</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.25in;text-indent:-.25in">7.<span
 * style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>reference to a
 * hidden tModel; this is a valid operation</p>
 *
 * <p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in">c.<span
 * style="font:7.0pt &quot;Times New
 * Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </span>Adding isReplacedBy
 * to a service’s or binding’s category bag: this is a semantically wrong
 * operation and will be rejected. </p>
 *
 * <p class="MsoBodyText">When returning an error encountered in the above,
 * E_invalidValue will be returned to indicate that a value that was passed in a
 * keyValue attribute did not pass validation.</p>
 *
 * <p class="MsoBodyText">While this validation is intended at save time,
 * references to replacing business entities may become invalid if (A) the
 * business is deleted and (B) in V3 the business is deleted and then the key is
 * re-used for a different entity. As such, in a replicating registry, nodes
 * processing changeRecords related to business entities or tModels that refer
 * to (now) invalid or missing business or tModels entity keys respectively,
 * MUST NOT raise replication errors.</p>
 *
 * @author Alex O'Ree
 */
public class Uddiuddiorgidentifierisreplacedby implements ValueSetValidator {

        public String getMyKey(){
                return "uddi:uddi.org:identifier:isreplacedby";
        }

        @Override
        public void validateValuesBindingTemplate(List<BindingTemplate> items, String xpath) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }

                for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getCategoryBag() != null) {
                                AbstractSimpleValidator.validateKeyNotPresentKeyRef(items.get(i).getCategoryBag().getKeyedReference(), getMyKey(), "binding");
                                AbstractSimpleValidator.validateKeyNotPresentKeyRefGrp(items.get(i).getCategoryBag().getKeyedReferenceGroup(), getMyKey(), "binding");
                        }
                        if (items.get(i).getTModelInstanceDetails() != null) {
                                for (int k = 0; k < items.get(i).getTModelInstanceDetails().getTModelInstanceInfo().size(); k++) {
                                        if (items.get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k) != null) {
                                                if (getMyKey().equalsIgnoreCase(items.get(i).getTModelInstanceDetails().getTModelInstanceInfo().get(k).getTModelKey())) {
                                                        throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "not allowed on binding templates"));
                                                }
                                        }
                                }
                        }
                }
        }

        @Override
        public void validateValuesBusinessEntity(List<BusinessEntity> items) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                //   In the case where a reference is made from an obsolete tModel the following validation rules apply:
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                try {
                        tx.begin();
                        for (int i = 0; i < items.size(); i++) {
                                if (items.get(i).getCategoryBag() != null) {
                                        for (int k = 0; k < items.get(i).getCategoryBag().getKeyedReference().size(); k++) {
                                                if (getMyKey().equalsIgnoreCase(items.get(i).getCategoryBag().getKeyedReference().get(k).getTModelKey())) {
                                                        //reference to self; this is invalid
                                                        if (items.get(i).getCategoryBag().getKeyedReference().get(k).getTModelKey().equalsIgnoreCase(items.get(i).getBusinessKey())) {
                                                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Referenced key " + items.get(i).getCategoryBag().getKeyedReference().get(k).getKeyValue() + " can't reference itself"));
                                                        }

                                                        try {
                                                                org.apache.juddi.model.BusinessEntity    find = em.find(org.apache.juddi.model.BusinessEntity.class, items.get(i).getCategoryBag().getKeyedReference().get(k).getKeyValue());
                                                                if (find == null) {
                                                                        // reference to a new tModel; this is a valid operation
                                                                        if (!ContainsBusinessKey(items, items.get(i).getCategoryBag().getKeyedReference().get(k).getKeyValue())) {
                                                                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Referenced key " + items.get(i).getCategoryBag().getKeyedReference().get(k).getKeyValue() + " does not exist"));
                                                                        }
                                                                }
                                                        } catch (ClassCastException c) {
                                                                // reference to a service, binding or business; this is an invalid operation given that the entity being pointed to must be a tModel
                                                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Referenced key " + items.get(i).getCategoryBag().getKeyedReference().get(k).getKeyValue() + " must be a business"));

                                                        }
                                                }
                                        }
                                }
                                if (items.get(i).getIdentifierBag() != null) {
                                        for (int k = 0; k < items.get(i).getIdentifierBag().getKeyedReference().size(); k++) {
                                                if (getMyKey().equalsIgnoreCase(items.get(i).getIdentifierBag().getKeyedReference().get(k).getTModelKey())) {
                                                        //reference to self; this is invalid
                                                        if (items.get(i).getIdentifierBag().getKeyedReference().get(k).getTModelKey().equalsIgnoreCase(items.get(i).getBusinessKey())) {
                                                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Referenced key " + items.get(i).getIdentifierBag().getKeyedReference().get(k).getKeyValue() + " can't reference itself"));
                                                        }

                                                        try {
                                                                org.apache.juddi.model.BusinessEntity find = em.find(org.apache.juddi.model.BusinessEntity.class, items.get(i).getIdentifierBag().getKeyedReference().get(k).getKeyValue());
                                                                if (find == null) {
                                                                        // reference to a new tModel; this is a valid operation
                                                                        if (!ContainsBusinessKey(items, items.get(i).getIdentifierBag().getKeyedReference().get(k).getKeyValue())) {
                                                                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Referenced key " + items.get(i).getIdentifierBag().getKeyedReference().get(k).getKeyValue() + " does not exist"));
                                                                        }
                                                                }
                                                        } catch (ClassCastException c) {
                                                                // reference to a service, binding or business; this is an invalid operation given that the entity being pointed to must be a tModel
                                                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Referenced key " + items.get(i).getIdentifierBag().getKeyedReference().get(k).getKeyValue() + " must be a business"));

                                                        }
                                                }
                                        }
                                }
                        }
                        tx.commit();
                } catch (DispositionReportFaultMessage d) {
                        throw d;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        @Override
        public void validateValuesBusinessService(List<BusinessService> items, String xpath) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getCategoryBag() != null) {
                                AbstractSimpleValidator.validateKeyNotPresentKeyRef(items.get(i).getCategoryBag().getKeyedReference(), getMyKey(), "service");
                                AbstractSimpleValidator.validateKeyNotPresentKeyRefGrp(items.get(i).getCategoryBag().getKeyedReferenceGroup(), getMyKey(), "service");
                        }
                        if (items.get(i).getBindingTemplates() != null) {
                                validateValuesBindingTemplate(items.get(i).getBindingTemplates().getBindingTemplate(), xpath + xpath + "businessService(" + i + ").identifierBag.");
                        }
                }
        }

        @Override
        public void validateValuesPublisherAssertion(List<PublisherAssertion> items) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                for (int i = 0; i < items.size(); i++) {
                        AbstractSimpleValidator.validateKeyNotPresentKeyRef(items.get(i).getKeyedReference(), getMyKey(), "publisherAssertion");
                }
        }

        private void validatedValuesKeyRef(List<KeyedReference> items, String xpath) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                String err = "";
                for (int i = 0; i < items.size(); i++) {
                        List<String> validValues = getValidValues();
                        if (validValues != null) {
                                //ok we have some work to do
                                boolean valid = false;
                                for (int k = 0; k < validValues.size(); k++) {
                                        if (validValues.get(i).equals(items.get(i).getKeyValue())) {
                                                valid = true;
                                        }
                                }
                                if (!valid) {
                                        err += xpath + "keyedReference(" + i + ") ";
                                }
                        }
                }
                if (err.length() > 0) {
                        throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", err));
                }
        }

        @Override
        public void validateTmodelInstanceDetails(List<TModelInstanceInfo> tModelInstanceInfo, String xpath) throws DispositionReportFaultMessage {
                if (tModelInstanceInfo == null) {
                        return;
                }
                for (int k = 0; k < tModelInstanceInfo.size(); k++) {
                        if (getMyKey().equalsIgnoreCase(tModelInstanceInfo.get(k).getTModelKey())) {
                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "not allowed on tModel instance info"));
                        }
                }
        }

        @Override
        public void validateValuesTModel(List<TModel> items) throws DispositionReportFaultMessage {
                if (items == null) {
                        return;
                }
                //   In the case where a reference is made from an obsolete tModel the following validation rules apply:
                EntityManager em = PersistenceManager.getEntityManager();
                EntityTransaction tx = em.getTransaction();
                
                try {
                        tx.begin();
                        for (int i = 0; i < items.size(); i++) {
                                if (items.get(i).getCategoryBag() != null) {
                                        for (int k = 0; k < items.get(i).getCategoryBag().getKeyedReference().size(); k++) {
                                                if (getMyKey().equalsIgnoreCase(items.get(i).getCategoryBag().getKeyedReference().get(k).getTModelKey())) {
                                                        //reference to self; this is invalid
                                                        if (items.get(i).getCategoryBag().getKeyedReference().get(k).getTModelKey().equalsIgnoreCase(items.get(i).getTModelKey())) {
                                                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Referenced key " + items.get(i).getCategoryBag().getKeyedReference().get(k).getKeyValue() + " can't reference itself"));
                                                        }

                                                        Tmodel find = null;
                                                        try {
                                                                find = em.find(org.apache.juddi.model.Tmodel.class, items.get(i).getCategoryBag().getKeyedReference().get(k).getKeyValue());

                                                        } catch (ClassCastException c) {
                                                                // reference to a service, binding or business; this is an invalid operation given that the entity being pointed to must be a tModel
                                                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Referenced key " + items.get(i).getCategoryBag().getKeyedReference().get(k).getKeyValue() + " must be a tModel"));

                                                        }
                                                        if (find == null) {
                                                                // reference to a new tModel; this is a valid operation
                                                                if (!ContainsKey(items, items.get(i).getIdentifierBag().getKeyedReference().get(k).getKeyValue())) {
                                                                        throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Referenced key " + items.get(i).getCategoryBag().getKeyedReference().get(k).getKeyValue() + " does not exist"));
                                                                }
                                                        }
                                                }
                                        }
                                }
                                if (items.get(i).getIdentifierBag() != null) {
                                        for (int k = 0; k < items.get(i).getIdentifierBag().getKeyedReference().size(); k++) {
                                                if (getMyKey().equalsIgnoreCase(items.get(i).getIdentifierBag().getKeyedReference().get(k).getTModelKey())) {
                                                        //reference to self; this is invalid
                                                        if (items.get(i).getIdentifierBag().getKeyedReference().get(k).getTModelKey().equalsIgnoreCase(items.get(i).getTModelKey())) {
                                                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Referenced key " + items.get(i).getIdentifierBag().getKeyedReference().get(k).getKeyValue() + " can't reference itself"));
                                                        }
                                                        Tmodel find = null;
                                                        try {
                                                                find = em.find(org.apache.juddi.model.Tmodel.class, items.get(i).getIdentifierBag().getKeyedReference().get(k).getKeyValue());

                                                        } catch (ClassCastException c) {
                                                                // reference to a service, binding or business; this is an invalid operation given that the entity being pointed to must be a tModel
                                                                throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Referenced key " + items.get(i).getIdentifierBag().getKeyedReference().get(k).getKeyValue() + " must be a tModel"));

                                                        }
                                                        if (find == null) {
                                                                // reference to a new tModel; this is a valid operation
                                                                if (!ContainsKey(items, items.get(i).getIdentifierBag().getKeyedReference().get(k).getKeyValue())) {
                                                                        throw new InvalidValueException(new ErrorMessage("errors.valuesetvalidation.invalidcontent", "Referenced key " + items.get(i).getIdentifierBag().getKeyedReference().get(k).getKeyValue() + " does not exist"));
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                        tx.commit();
                } catch (DispositionReportFaultMessage d) {
                        throw d;
                } finally {
                        if (tx.isActive()) {
                                tx.rollback();
                        }
                        em.close();
                }
        }

        @Override
        public List<String> getValidValues() {
                return Collections.EMPTY_LIST;
        }

        private boolean ContainsKey(List<TModel> items, String keyValue) {
                for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getTModelKey().equalsIgnoreCase(keyValue)) {
                                return true;
                        }
                }
                return false;
        }

        private boolean ContainsBusinessKey(List<BusinessEntity> items, String keyValue) {
                for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getBusinessKey().equalsIgnoreCase(keyValue)) {
                                return true;
                        }
                }
                return false;
        }

}
