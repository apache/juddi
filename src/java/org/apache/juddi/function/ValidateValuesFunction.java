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
package org.apache.juddi.function;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.binding.BindingTemplates;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.request.ValidateValues;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.datatype.service.BusinessServices;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class ValidateValuesFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(SetPublisherAssertionsFunction.class);

  /**
   *
   */
  public ValidateValuesFunction(RegistryEngine registry)
  {
    super(registry);
  }

  /**
   *
   * @param obj
   * @return RegistryObject
   * @throws RegistryException
   */
  public RegistryObject execute(RegistryObject obj)
    throws RegistryException
  {
    ValidateValues request = (ValidateValues)obj;
    String generic = request.getGeneric();

    Vector businessVector = request.getBusinessEntityVector();
    if ((businessVector != null) && (businessVector.size() > 0))
      validateBusinessVector(businessVector);

    Vector serviceVector = request.getBusinessServiceVector();
    if ((serviceVector != null) && (serviceVector.size() > 0))
      validateServiceVector(serviceVector);

    Vector tModelVector = request.getTModelVector();
    if ((tModelVector != null) && (tModelVector.size() > 0))
      validateTModelVector(tModelVector);

    // We don't currently support teh validate_values
    // function so to keep things moving along let's 
    // just return a successful DispositionReport.
    
    DispositionReport dispRpt = new DispositionReport();
    dispRpt.setGeneric(generic);
    dispRpt.setOperator(Config.getOperator());
    dispRpt.addResult(new Result(Result.E_SUCCESS));
    return dispRpt;
  }

  /**
   *
   */
  private RegistryObject validateBusinessVector(Vector businessVector)
  {
    if (businessVector == null)
      return null;
    
    for (int i=0; i<businessVector.size(); i++)
    {
      BusinessEntity business = (BusinessEntity)businessVector.elementAt(i);
      
      CategoryBag catBag = business.getCategoryBag();
      if (catBag != null)
      {
        Vector refs = catBag.getKeyedReferenceVector();
        if ((refs != null) && (refs.size() > 0))
          validate(refs);
      }
    
      IdentifierBag idBag = business.getIdentifierBag();
      if (idBag != null)
      {
        Vector refs = idBag.getKeyedReferenceVector();
        if ((refs != null) && (refs.size() > 0))
          validate(refs);
      }
      
      BusinessServices services = business.getBusinessServices();
      if (services != null)
      {
        Vector serviceVector = services.getBusinessServiceVector();
        if (serviceVector != null)
          validateServiceVector(serviceVector);
      }
    }
    
    return null;
  }

  /**
   *
   */
  private RegistryObject validateServiceVector(Vector serviceVector)
  {
    if (serviceVector == null)
      return null;
    
    for (int i=0; i<serviceVector.size(); i++)
    {
      BusinessService service = (BusinessService)serviceVector.elementAt(i);
      
      CategoryBag catBag = service.getCategoryBag();
      if (catBag != null)
      {
        Vector refs = catBag.getKeyedReferenceVector();
        if ((refs != null) && (refs.size() > 0))
          validate(refs);
      }
      
      BindingTemplates templates = service.getBindingTemplates();
      if (templates != null)
      {
        Vector bindings = templates.getBindingTemplateVector();
        if (bindings != null)
          validateBindingVector(bindings);
      }
    }
    
    return null;
  }

  /**
   *
   */
  private RegistryObject validateBindingVector(Vector bindingVector)
  {
    if (bindingVector == null)
      return null;
   
    for (int i=0; i<bindingVector.size(); i++)
    {
      BindingTemplate binding = (BindingTemplate)bindingVector.elementAt(i);
    
      CategoryBag catBag = binding.getCategoryBag();
      if (catBag != null)
      {
        Vector refs = catBag.getKeyedReferenceVector();
        if ((refs != null) && (refs.size() > 0))
          validate(refs);
      }
    }
  
    return null;
  }
 
  /**
   *
   */
  private RegistryObject validateTModelVector(Vector tModelVector)
  {
    if (tModelVector == null)
      return null;
    
    for (int i=0; i<tModelVector.size(); i++)
    {
      TModel tModel = (TModel)tModelVector.elementAt(i);
      
      CategoryBag catBag = tModel.getCategoryBag();
      if (catBag != null)
      {
        Vector refs = catBag.getKeyedReferenceVector();
        if ((refs != null) && (refs.size() > 0))
          validate(refs);
      }
      
      IdentifierBag idBag = tModel.getIdentifierBag();
      if (idBag != null)
      {
        Vector refs = idBag.getKeyedReferenceVector();
        if ((refs != null) && (refs.size() > 0))
          validate(refs);
      }
    }
    
    return null;
  }
  
  /**
   * 
   * @param args
   */
  private RegistryObject validate(Vector refs)
  {
    if (refs == null)
      return null;
    
    for (int i=0; i<refs.size(); i++)
    {
      KeyedReference ref = (KeyedReference)refs.elementAt(i);
     
      // Perform the validation
    }
    
    return null;
  }

  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
  {
    // initialize the registry
    RegistryEngine reg = new RegistryEngine();
    reg.init();

    try
    {
    }
    catch (Exception ex)
    {
      // write execption to the console
      ex.printStackTrace();
    }
    finally
    {
      // destroy the registry
      reg.dispose();
    }
  }
}