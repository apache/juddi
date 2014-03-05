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

import java.util.ArrayList;
import java.util.List;

/**
 * There are several value sets in UDDI that have entity keys as valid values,
 * and other such value sets may be defined. Special handling of the keyValue
 * values is required for these value sets to ensure that they can be used by
 * applications using any version of the UDDI API. By categorizing a value set
 * with this tModel the publisher of the value set indicates that entity keys
 * form the valid values of the value set. This allows a UDDI implementation to
 * map entity keys between versions as is done with all other uses of entity
 * keys.
 *
 * If keys of only one type are valid for a particular value set, then that
 * value set should have a single keyedReference relating to this tModel and the
 * keyValue should contain the type of entity key that is valid, for example
 * "tModelKey". If multiple types of key are valid, as in the case of
 * uddi-org:isReplacedBy, then multiple keyedReferences can be used, one for
 * each type of key. If any type of key is valid then a single keyedReference
 * should be used with a keyValue of "entityKey".
 *
 * A value set categorized with this tModel SHOULD be treated as an internally
 * checked value set, whether or not it is also categorized as checked.
 *
 * If the entity key supplied as the keyValue in a keyedReference relating to
 * such a value set is not a valid entity key, or is the key of an entity of a
 * type not supported by the particular value set, then the error E_invalidValue
 * MAY be returned.
 *
 * Value sets may require additional validation, and this additional validation
 * MAY be performed before or after the validation of the key itself, therefore
 * a different error MAY be returned if one of these additional validation steps
 * fails before the validation of the key itself.
 *
 * If an entityKeyValue value set is updated to remove all of the keyedReference
 * elements referring to the "Entity Key Values" category system, a normative
 * mapping behavior to update the keyValue of any existing references to the
 * entityKeyValue value set is unspecified. Any new references or updates to
 * existing references using keyedReference elements pointing to the tModel that
 * formerly represented the entityKeyValue value set will be treated as a normal
 * value set, where the keyValue is a string.  *
 * Further, if a tModel is updated to add at least one keyedReference element
 * referring to the "Entity Key Values" category system, a normative mapping
 * behavior to update the keyValue of any existing references to the
 * entityKeyValue value set is unspecified. Any new references or updates to
 * existing references using keyedReference elements pointing to the tModel that
 * formerly represented the value set will be case folded and validated as an
 * entityKeyValue value set, where the keyValue is verified to be an existing
 * and appropriate entityKey.  *
 * In inquiry, the treatment of the keyValue is determined by the state of the
 * value set tModel at the time of the inquiry. If the keyValue in an inquiry is
 * contained in a keyedReference referring to the "Entity Key Value" set tModel,
 * the keyValue must be case folded as part of the inquiry.
 *
 * @author Alex O'Ree
 */
public class Uddiuddiorgcategorizationentitykeyvalues extends AbstractSimpleValidator {

        @Override
        public List<String> getValidValues() {
                List<String> ret = new ArrayList<String>();
                ret.add("entityKey");
                ret.add("businessKey");
                ret.add("tModelKey");
                ret.add("serviceKey");
                ret.add("bindingKey");
                ret.add("subscriptionKey");
                return ret;
        }

}
