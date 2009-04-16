package org.apache.juddi.portlets.client;

import org.apache.juddi.portlets.client.model.Service;
import org.apache.juddi.portlets.client.service.InquiryResponse;
import org.apache.juddi.portlets.client.service.InquiryService;
import org.apache.juddi.portlets.client.service.InquiryServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class DetailPanel extends FlowPanel {

	private InquiryServiceAsync inquiryService = (InquiryServiceAsync) GWT.create(InquiryService.class); 
	private UDDIBrowser browser = null;

	public DetailPanel(UDDIBrowser browser) {
		this.browser = browser;
		Label business = new Label("Business");
		business.setStyleName("portlet-form-field-label");
		this.add(business);
	}

	public void displayService(String key) {
		System.out.println("Going to fetch the Service and Display it");
		getService(browser.getToken(), key);

	}

	private void getService(String token, String serviceKey) {

		inquiryService.getServiceDetail(token, serviceKey, new AsyncCallback<InquiryResponse>() {
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry.");
			}

			public void onSuccess(InquiryResponse response) {
				if (response.isSuccess()) {
					Service service = response.getService();
					//Build a table
				} else {
					Window.alert("error: " + response.getMessage());
				}
			}
		});
	}

}
