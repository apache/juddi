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
package org.apache.juddi.datatype.response;

import java.util.Vector;

import org.apache.juddi.datatype.RegistryObject;

/**
 * "This structure is used to report the outcome of message processing and
 *  to report errors discovered during processing. This message contains one
 *  or more result structures. A special case - success - contains only one
 *  result structure with the special errno attribute value of E_success (0)."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class DispositionReport implements RegistryObject
{
  String generic;
  String operator;
  Vector resultVector;

  /**
   * Constructs a new initialized DispositionReport instance.
   */
  public DispositionReport()
  {
  }

  /**
   *
   * @param genericValue
   */
  public void setGeneric(String genericValue)
  {
    this.generic = genericValue;
  }

  /**
   *
   * @return String UDDI generic value.
   */
  public String getGeneric()
  {
    return this.generic;
  }

  /**
   *
   */
  public void setOperator(String operator)
  {
    this.operator = operator;
  }

  /**
   *
   */
  public String getOperator()
  {
     return this.operator;
  }

  /**
   *
   */
  public void addResult(Result result)
  {
    if (this.resultVector == null)
      this.resultVector = new Vector();
    this.resultVector.add(result);
  }

  /**
   *
   */
  public void setResultVector(Vector results)
  {
    this.resultVector = results;
  }

  /**
   *
   */
  public Vector getResultVector()
  {
    return this.resultVector;
  }

  /**
   *
   */
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("org.apache.juddi.datatype.response.DispositionReport.operator=");
    buffer.append(this.operator);
    buffer.append("\n");

    if (this.resultVector != null)
      for (int i=0; i<this.resultVector.size(); i++)
        buffer.append(this.resultVector.elementAt(i));

    return buffer.toString();
  }

  // test driver
  public static void main(String[] args)
  {
    DispositionReport obj = new DispositionReport();
    obj.setGeneric("2.0");
    obj.setOperator("jUDDI.org");

    ErrInfo errInfo = new ErrInfo();
    errInfo.setErrCode("abc");
    errInfo.setErrMsg("def");

    Result result = new Result();
    result.setErrInfo(errInfo);
    result.setErrno(123);

    obj.addResult(result);

    errInfo = new ErrInfo();
    errInfo.setErrCode("ghi");
    errInfo.setErrMsg("jkl");

    result = new Result();
    result.setErrInfo(errInfo);
    result.setErrno(456);

    obj.addResult(result);

    errInfo = new ErrInfo();
    errInfo.setErrCode("mno");
    errInfo.setErrMsg("pqr");

    result = new Result();
    result.setErrInfo(errInfo);
    result.setErrno(789);

    obj.addResult(result);

    System.out.println(obj);
  }
}