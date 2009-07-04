package org.apache.juddi.portlets.client;

import org.apache.juddi.portlets.client.model.Publisher;
import org.apache.juddi.portlets.client.service.JUDDIApiResponse;
import org.apache.juddi.portlets.client.service.JUDDIApiService;
import org.apache.juddi.portlets.client.service.JUDDIApiServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class PublisherListPanel extends FlowPanel implements ApplicationPanel {

	private JUDDIApiServiceAsync juddiApiService = (JUDDIApiServiceAsync) GWT.create(JUDDIApiService.class);
	Application parentApp = null;
	
	public PublisherListPanel(Application parentApp) {
		this.parentApp = parentApp;
	}
		
	public void loadData() {
		getPublishers();
	}
	
	/**
	 * Obtains an authenticationToken
	 * @param user
	 * @param password
	 */
	protected void getPublishers() {
		juddiApiService.getPublishers(parentApp.getToken(), parentApp.getUsername(),  new AsyncCallback<JUDDIApiResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry. " + caught.getMessage());
			}

			public void onSuccess(JUDDIApiResponse response) {
				if (response.isSuccess()) {
					for (Publisher publisher : response.getPublishers()) {
						Label name = new Label(publisher.getAuthorizedName() + " - " + publisher.getPublisherName());
						name.setStyleName("portlet-form-field-label");
						add(name);
					}
				} else {
					Window.alert("error: " + response.getMessage() + ". Make sure the UDDI server is up and running.");
				}
			}
		});
	}
	
}
