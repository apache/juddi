/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
package org.apache.juddi.validator;

import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.error.RegistryException; 

/**
 * @author Steve Viens (sviens@apache.org)
 */
public interface Validator 
{
  /**
   *
   * @param object
   * @return A DispositionReport containing the validation results
   * @throws RegistryException
   */
  boolean validate(CategoryBag bag)
    throws RegistryException;

  /**
   *
   * @param object
   * @return A DispositionReport containing the validation results
   * @throws RegistryException
   */
  boolean validate(IdentifierBag bag)
    throws RegistryException;

  /**
   *
   * @param object
   * @return A DispositionReport containing the validation results
   * @throws RegistryException
   */
  boolean validate(KeyedReference ref)
    throws RegistryException;
}
