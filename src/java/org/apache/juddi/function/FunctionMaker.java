/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(FunctionMaker.class);

  // shared FunctionMaker
  private static FunctionMaker maker = null;

  // table of shared Function instances
  private HashMap functions = null;

  /**
   *
   */
  private FunctionMaker()
  {
    functions = new HashMap();
    IFunction function = null;

    function = new AddPublisherAssertionsFunction();
    functions.put(AddPublisherAssertions.class.getName(),function);

    function = new DeleteBindingFunction();
    functions.put(DeleteBinding.class.getName(),function);

    function = new DeleteBusinessFunction();
    functions.put(DeleteBusiness.class.getName(),function);

    function = new DeletePublisherAssertionsFunction();
    functions.put(DeletePublisherAssertions.class.getName(),function);

    function = new DeletePublisherFunction();
    functions.put(DeletePublisher.class.getName(),function);

    function = new DeleteServiceFunction();
    functions.put(DeleteService.class.getName(),function);

    function = new DeleteTModelFunction();
    functions.put(DeleteTModel.class.getName(),function);

    function = new DiscardAuthTokenFunction();
    functions.put(DiscardAuthToken.class.getName(),function);

    function = new FindBindingFunction();
    functions.put(FindBinding.class.getName(),function);

    function = new FindBusinessFunction();
    functions.put(FindBusiness.class.getName(),function);

    function = new FindPublisherFunction();
    functions.put(FindPublisher.class.getName(),function);

    function = new FindRelatedBusinessesFunction();
    functions.put(FindRelatedBusinesses.class.getName(),function);

    function = new FindServiceFunction();
    functions.put(FindService.class.getName(),function);

    function = new FindTModelFunction();
    functions.put(FindTModel.class.getName(),function);

    function = new GetAssertionStatusReportFunction();
    functions.put(GetAssertionStatusReport.class.getName(),function);

    function = new GetAuthTokenFunction();
    functions.put(GetAuthToken.class.getName(),function);

    function = new GetBindingDetailFunction();
    functions.put(GetBindingDetail.class.getName(),function);

    function = new GetBusinessDetailFunction();
    functions.put(GetBusinessDetail.class.getName(),function);

    function = new GetBusinessDetailExtFunction();
    functions.put(GetBusinessDetailExt.class.getName(),function);

    function = new GetPublisherAssertionsFunction();
    functions.put(GetPublisherAssertions.class.getName(),function);

    function = new GetPublisherDetailFunction();
    functions.put(GetPublisherDetail.class.getName(),function);

    function = new GetRegisteredInfoFunction();
    functions.put(GetRegisteredInfo.class.getName(),function);

    function = new GetServiceDetailFunction();
    functions.put(GetServiceDetail.class.getName(),function);

    function = new GetTModelDetailFunction();
    functions.put(GetTModelDetail.class.getName(),function);

    function = new SaveBindingFunction();
    functions.put(SaveBinding.class.getName(),function);

    function = new SaveBusinessFunction();
    functions.put(SaveBusiness.class.getName(),function);

    function = new SavePublisherFunction();
    functions.put(SavePublisher.class.getName(),function);

    function = new SaveServiceFunction();
    functions.put(SaveService.class.getName(),function);

    function = new SaveTModelFunction();
    functions.put(SaveTModel.class.getName(),function);

    function = new SetPublisherAssertionsFunction();
    functions.put(SetPublisherAssertions.class.getName(),function);

    function = new ValidateValuesFunction();
    functions.put(ValidateValues.class.getName(),function);
  }

  /**
   *
   */
  public static FunctionMaker getInstance()
  {
    if (maker == null)
      maker = createInstance();

    return maker;
  }

  /**
   *
   */
  private static synchronized FunctionMaker createInstance()
  {
    if (maker == null)
      maker = new FunctionMaker();

    return maker;
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