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

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.request.AddPublisherAssertions;
import org.apache.juddi.datatype.request.DeleteBinding;
import org.apache.juddi.datatype.request.DeleteBusiness;
import org.apache.juddi.datatype.request.DeletePublisher;
import org.apache.juddi.datatype.request.DeletePublisherAssertions;
import org.apache.juddi.datatype.request.DeleteService;
import org.apache.juddi.datatype.request.DeleteTModel;
import org.apache.juddi.datatype.request.DiscardAuthToken;
import org.apache.juddi.datatype.request.FindBinding;
import org.apache.juddi.datatype.request.FindBusiness;
import org.apache.juddi.datatype.request.FindPublisher;
import org.apache.juddi.datatype.request.FindRelatedBusinesses;
import org.apache.juddi.datatype.request.FindService;
import org.apache.juddi.datatype.request.FindTModel;
import org.apache.juddi.datatype.request.GetAssertionStatusReport;
import org.apache.juddi.datatype.request.GetAuthToken;
import org.apache.juddi.datatype.request.GetBindingDetail;
import org.apache.juddi.datatype.request.GetBusinessDetail;
import org.apache.juddi.datatype.request.GetBusinessDetailExt;
import org.apache.juddi.datatype.request.GetPublisherAssertions;
import org.apache.juddi.datatype.request.GetPublisherDetail;
import org.apache.juddi.datatype.request.GetRegisteredInfo;
import org.apache.juddi.datatype.request.GetServiceDetail;
import org.apache.juddi.datatype.request.GetTModelDetail;
import org.apache.juddi.datatype.request.SaveBinding;
import org.apache.juddi.datatype.request.SaveBusiness;
import org.apache.juddi.datatype.request.SavePublisher;
import org.apache.juddi.datatype.request.SaveService;
import org.apache.juddi.datatype.request.SaveTModel;
import org.apache.juddi.datatype.request.SetPublisherAssertions;
import org.apache.juddi.datatype.request.ValidateValues;
import org.apache.juddi.registry.RegistryEngine;

/**
 * Holds a static HashMap linking the string representation of operations to
 * instantances of the appropriate maker class (BusinessDetail to
 * BusinessDetailHandler). Returns a reference to an instance of a maker object.
 * HandlerMaker follows the Singleton pattern (GoF p.127).  Use getInstance
 * instead of the 'new' operator to get an instance of this class.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class FunctionMaker
{
  // private reference to the logger
  private static Log log = LogFactory.getLog(FunctionMaker.class);

  // table of shared Function instances
  private HashMap functions = null;

  /**
   *
   */
  public FunctionMaker(RegistryEngine registry)
  {
    functions = new HashMap();
    IFunction function = null;

//    function = new AddPublisherAssertionsFunction();
//    functions.put(AddPublisherAssertions.class.getName(),function);
//
//    function = new DeleteBindingFunction();
//    functions.put(DeleteBinding.class.getName(),function);
//
//    function = new DeleteBusinessFunction();
//    functions.put(DeleteBusiness.class.getName(),function);
//
//    function = new DeletePublisherAssertionsFunction();
//    functions.put(DeletePublisherAssertions.class.getName(),function);
//
//    function = new DeletePublisherFunction();
//    functions.put(DeletePublisher.class.getName(),function);
//
//    function = new DeleteServiceFunction();
//    functions.put(DeleteService.class.getName(),function);
//
//    function = new DeleteTModelFunction();
//    functions.put(DeleteTModel.class.getName(),function);
//
//    function = new DiscardAuthTokenFunction();
//    functions.put(DiscardAuthToken.class.getName(),function);
//
//    function = new FindBindingFunction();
//    functions.put(FindBinding.class.getName(),function);
//
//    function = new FindBusinessFunction();
//    functions.put(FindBusiness.class.getName(),function);
//
//    function = new FindPublisherFunction();
//    functions.put(FindPublisher.class.getName(),function);
//
//    function = new FindRelatedBusinessesFunction();
//    functions.put(FindRelatedBusinesses.class.getName(),function);
//
//    function = new FindServiceFunction();
//    functions.put(FindService.class.getName(),function);
//
//    function = new FindTModelFunction();
//    functions.put(FindTModel.class.getName(),function);
//
//    function = new GetAssertionStatusReportFunction();
//    functions.put(GetAssertionStatusReport.class.getName(),function);
//
//    function = new GetAuthTokenFunction();
//    functions.put(GetAuthToken.class.getName(),function);
//
//    function = new GetBindingDetailFunction();
//    functions.put(GetBindingDetail.class.getName(),function);
//
//    function = new GetBusinessDetailFunction();
//    functions.put(GetBusinessDetail.class.getName(),function);
//
//    function = new GetBusinessDetailExtFunction();
//    functions.put(GetBusinessDetailExt.class.getName(),function);
//
//    function = new GetPublisherAssertionsFunction();
//    functions.put(GetPublisherAssertions.class.getName(),function);
//
//    function = new GetPublisherDetailFunction();
//    functions.put(GetPublisherDetail.class.getName(),function);
//
//    function = new GetRegisteredInfoFunction();
//    functions.put(GetRegisteredInfo.class.getName(),function);
//
//    function = new GetServiceDetailFunction();
//    functions.put(GetServiceDetail.class.getName(),function);
//
//    function = new GetTModelDetailFunction();
//    functions.put(GetTModelDetail.class.getName(),function);
//
//    function = new SaveBindingFunction();
//    functions.put(SaveBinding.class.getName(),function);
//
//    function = new SaveBusinessFunction();
//    functions.put(SaveBusiness.class.getName(),function);
//
//    function = new SavePublisherFunction();
//    functions.put(SavePublisher.class.getName(),function);
//
//    function = new SaveServiceFunction();
//    functions.put(SaveService.class.getName(),function);
//
//    function = new SaveTModelFunction();
//    functions.put(SaveTModel.class.getName(),function);
//
//    function = new SetPublisherAssertionsFunction();
//    functions.put(SetPublisherAssertions.class.getName(),function);
//
//    function = new ValidateValuesFunction();
//    functions.put(ValidateValues.class.getName(),function);
//
//    /** **/
    
    function = new AddPublisherAssertionsFunction(registry);
    functions.put(AddPublisherAssertions.class.getName(),function);

    function = new DeleteBindingFunction(registry);
    functions.put(DeleteBinding.class.getName(),function);

    function = new DeleteBusinessFunction(registry);
    functions.put(DeleteBusiness.class.getName(),function);

    function = new DeletePublisherAssertionsFunction(registry);
    functions.put(DeletePublisherAssertions.class.getName(),function);

    function = new DeletePublisherFunction(registry);
    functions.put(DeletePublisher.class.getName(),function);

    function = new DeleteServiceFunction(registry);
    functions.put(DeleteService.class.getName(),function);

    function = new DeleteTModelFunction(registry);
    functions.put(DeleteTModel.class.getName(),function);

    function = new DiscardAuthTokenFunction(registry);
    functions.put(DiscardAuthToken.class.getName(),function);

    function = new FindBindingFunction(registry);
    functions.put(FindBinding.class.getName(),function);

    function = new FindBusinessFunction(registry);
    functions.put(FindBusiness.class.getName(),function);

    function = new FindPublisherFunction(registry);
    functions.put(FindPublisher.class.getName(),function);

    function = new FindRelatedBusinessesFunction(registry);
    functions.put(FindRelatedBusinesses.class.getName(),function);

    function = new FindServiceFunction(registry);
    functions.put(FindService.class.getName(),function);

    function = new FindTModelFunction(registry);
    functions.put(FindTModel.class.getName(),function);

    function = new GetAssertionStatusReportFunction(registry);
    functions.put(GetAssertionStatusReport.class.getName(),function);

    function = new GetAuthTokenFunction(registry);
    functions.put(GetAuthToken.class.getName(),function);

    function = new GetBindingDetailFunction(registry);
    functions.put(GetBindingDetail.class.getName(),function);

    function = new GetBusinessDetailFunction(registry);
    functions.put(GetBusinessDetail.class.getName(),function);

    function = new GetBusinessDetailExtFunction(registry);
    functions.put(GetBusinessDetailExt.class.getName(),function);

    function = new GetPublisherAssertionsFunction(registry);
    functions.put(GetPublisherAssertions.class.getName(),function);

    function = new GetPublisherDetailFunction(registry);
    functions.put(GetPublisherDetail.class.getName(),function);

    function = new GetRegisteredInfoFunction(registry);
    functions.put(GetRegisteredInfo.class.getName(),function);

    function = new GetServiceDetailFunction(registry);
    functions.put(GetServiceDetail.class.getName(),function);

    function = new GetTModelDetailFunction(registry);
    functions.put(GetTModelDetail.class.getName(),function);

    function = new SaveBindingFunction(registry);
    functions.put(SaveBinding.class.getName(),function);

    function = new SaveBusinessFunction(registry);
    functions.put(SaveBusiness.class.getName(),function);

    function = new SavePublisherFunction(registry);
    functions.put(SavePublisher.class.getName(),function);

    function = new SaveServiceFunction(registry);
    functions.put(SaveService.class.getName(),function);

    function = new SaveTModelFunction(registry);
    functions.put(SaveTModel.class.getName(),function);

    function = new SetPublisherAssertionsFunction(registry);
    functions.put(SetPublisherAssertions.class.getName(),function);

    function = new ValidateValuesFunction(registry);
    functions.put(ValidateValues.class.getName(),function);
  }

  /**
   *
   */
  public final IFunction lookup(String className)
  {
    IFunction function = (IFunction)functions.get(className);
    if (function == null)
      log.error("can't find function for class name \"" + className + "\"");

    return function;
  }
}