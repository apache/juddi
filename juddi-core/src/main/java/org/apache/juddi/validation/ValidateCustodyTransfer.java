package org.apache.juddi.validation;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.error.FatalErrorException;
import org.apache.juddi.error.ValueNotAllowedException;
import org.apache.juddi.model.UddiEntityPublisher;
import org.uddi.custody_v3.KeyBag;
import org.uddi.v3_service.DispositionReportFaultMessage;

public class ValidateCustodyTransfer extends ValidateUDDIApi {

	public void validateGetTransferToken(EntityManager em, KeyBag keyBag) throws DispositionReportFaultMessage {

		// No null input
		if (keyBag == null)
			throw new FatalErrorException(new ErrorMessage("errors.NullInput"));
		
		List<String> keyList = keyBag.getKey();
		if (keyList == null || keyList.size() == 0)
			throw new ValueNotAllowedException(new ErrorMessage("errors.keybag.NoInput"));
		
		// Test that publisher owns keys using operational info.
		for (String key : keyList) {
			
		}
	}
	
	public ValidateCustodyTransfer(UddiEntityPublisher publisher) {
		super(publisher);
	}

}
