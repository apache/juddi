package org.apache.juddi.validation;

import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.error.FatalErrorException;
import org.apache.juddi.model.UddiEntityPublisher;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.v3_service.DispositionReportFaultMessage;

public class ValidateCustodyTransfer {
	public static void unsupportedAPICall() throws DispositionReportFaultMessage {
		throw new FatalErrorException(new ErrorMessage("This API is not supported in this release of jUDDI"));
	}
}
