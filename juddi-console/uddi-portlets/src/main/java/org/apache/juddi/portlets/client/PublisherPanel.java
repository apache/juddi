package org.apache.juddi.portlets.client;

import org.apache.juddi.portlets.client.service.JUDDIApiService;
import org.apache.juddi.portlets.client.service.JUDDIApiServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;

public class PublisherPanel extends FlowPanel implements ApplicationPanel {

	private JUDDIApiServiceAsync juddiApiService = (JUDDIApiServiceAsync) GWT.create(JUDDIApiService.class);
	Application parentApp = null;
	
//	private TextBox publisherNameBox = new TextBox();
//	private TextBox authorizedNameBox = new TextBox();
//	private TextBox emailAddressBox = new TextBox();
//	private TextBox isAdminBox = new TextBox();
//	private TextBox isEnabledBox = new TextBox();
//	private TextBox maxBindingsPerServiceBox = new TextBox();
//	private TextBox maxBusinesses = new TextBox();
//	private TextBox maxServicesPerBusinessBox = new TextBox();
	
	public PublisherPanel(Application parentApp) {
		this.parentApp = parentApp;
		getElement().setId("parentApp-body");
		
		
		
		
//		Label publisherName = new Label ("Publisher Name:");
//		publisherName.setStyleName("portlet-form-field-label");
//		add(publisherName);
//		publisherNameBox.setStyleName("portlet-form-input-field");
//		add(publisherNameBox);
	}
		
		
	
	public void loadData() {
		getPublishers();
	}
	
	protected void getPublishers() {

		
	}
	
}
