package org.apache.juddi.subscription.notify;

import java.rmi.RemoteException;

import org.uddi.api_v3.DispositionReport;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;

public interface Notifier {

	public DispositionReport notifySubscriptionListener(NotifySubscriptionListener body) 
		throws DispositionReportFaultMessage, RemoteException;
	
}
