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