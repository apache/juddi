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
package org.apache.juddi.handler;

import org.apache.juddi.IRegistry;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.ErrInfo;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author anou_mana@apache.org
 */
public class  DispositionReportHandlerTests extends HandlerTestCase
{
	private static final String TEST_ID = "juddi.handler.DeletePublisher.test";
	private  DispositionReportHandler handler = null;

  public  DispositionReportHandlerTests(String arg0)
  {
    super(arg0);
  }

  public static void main(String[] args)
  {
    junit.textui.TestRunner.run( DispositionReportHandlerTests.class);
  }

  public void setUp()
  {
		HandlerMaker maker = HandlerMaker.getInstance();
		handler = ( DispositionReportHandler)maker.lookup( DispositionReportHandler.TAG_NAME);
  }

	private RegistryObject getRegistryObject()
	{

		ErrInfo errInfo = new ErrInfo();
		errInfo.setErrCode(Result.E_ACCOUNT_LIMIT_EXCEEDED_CODE);
		errInfo.setErrMsg(Result.E_ACCOUNT_LIMIT_EXCEEDED_MSG);

		Result result = new Result();
		result.setErrno(Result.E_ACCOUNT_LIMIT_EXCEEDED);
		result.setErrInfo(errInfo);

		ErrInfo errInfo2 = new ErrInfo();
		errInfo2.setErrCode(Result.E_SUCCESS_CODE);
		errInfo2.setErrMsg(Result.E_SUCCESS_MSG);

		Result result2 = new Result();
		result2.setErrno(Result.E_SUCCESS);
		result2.setErrInfo(errInfo2);

		DispositionReport object = new  DispositionReport();
		object.setGeneric(IRegistry.UDDI_V2_GENERIC);
		object.setOperator("jUDDI.org");
		object.addResult(result);
		object.addResult(result2);

		return object;

	}

	private Element getMarshalledElement(RegistryObject regObject)
	{
		Element parent = XMLUtils.newRootElement();
		Element child = null;

		if(regObject == null)
			regObject = this.getRegistryObject();

		handler.marshal(regObject,parent);
		child = (Element)parent.getFirstChild();
		parent.removeChild(child);

		return child;
	}

	public void testMarshal()
	{
		Element child = getMarshalledElement(null);

		String marshalledString = this.getXMLString(child);

		assertNotNull("Marshalled  DispositionReport ", marshalledString);

	}

	public void testUnMarshal()
	{

		Element child = getMarshalledElement(null);
		RegistryObject regObject = handler.unmarshal(child);

		assertNotNull("UnMarshalled  DispositionReport ", regObject);

	}

  public void testMarshUnMarshal()
  {
		Element child = getMarshalledElement(null);

		String marshalledString = this.getXMLString(child);

		assertNotNull("Marshalled  DispositionReport ", marshalledString);

		RegistryObject regObject = handler.unmarshal(child);

		child = getMarshalledElement(regObject);

		String unMarshalledString = this.getXMLString(child);

		assertNotNull("Unmarshalled  DispositionReport ", unMarshalledString);

		boolean equals = marshalledString.equals(unMarshalledString);

		assertEquals("Expected result: ", marshalledString, unMarshalledString );
  }

}
