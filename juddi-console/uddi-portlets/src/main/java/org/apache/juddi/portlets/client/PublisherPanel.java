package org.apache.juddi.portlets.client;

import org.apache.juddi.portlets.client.model.Publisher;
import org.apache.juddi.portlets.client.service.JUDDIApiResponse;
import org.apache.juddi.portlets.client.service.JUDDIApiService;
import org.apache.juddi.portlets.client.service.JUDDIApiServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PublisherPanel extends FlowPanel implements ClickListener {

	private JUDDIApiServiceAsync juddiApiService = (JUDDIApiServiceAsync) GWT.create(JUDDIApiService.class);
	
	private TextBox idBox = new TextBox();
	private TextBox nameBox = new TextBox();
	private TextBox emailAddressBox = new TextBox();
	private CheckBox isAdminBox = new CheckBox();
	private CheckBox isEnabledBox = new CheckBox();
	private TextBox maxBindingsPerServiceBox = new TextBox();
	private TextBox maxBusinessesBox = new TextBox();
	private TextBox maxServicesPerBusinessBox = new TextBox();
	private TextBox maxTModelBox = new TextBox();
	Button saveButton = new Button();
	Button newButton = new Button();
	Button deleteButton = new Button();
	Publisher publisher = null;
	
	public PublisherPanel(Publisher publisher) {
		
		this.publisher = publisher;
		
		Label id = new Label ("Id:");
		id.setStyleName("portlet-form-field-label");
		add(id);
		idBox.setText(publisher.getAuthorizedName());
		idBox.setStyleName("portlet-form-input-field");
		add(idBox);
		
		Label name = new Label ("Name:");
		name.setStyleName("portlet-form-field-label");
		add(name);
		nameBox.setText(publisher.getPublisherName());
		nameBox.setStyleName("portlet-form-input-field");
		add(nameBox);
		
		Label email = new Label ("Email:");
		email.setStyleName("portlet-form-field-label");
		add(email);
		emailAddressBox.setText(publisher.getEmailAddress());
		emailAddressBox.setStyleName("portlet-form-input-field");
		add(emailAddressBox);
		
		Label isAdmin = new Label ("Is Admin:");
		isAdmin.setStyleName("portlet-form-field-label");
		add(isAdmin);
		isAdminBox.setChecked("true".equalsIgnoreCase(publisher.getIsAdmin()));
		isAdminBox.setStyleName("portlet-form-input-field");
		add(isAdminBox);
		
		Label isEnabled = new Label ("Is Enabled:");
		isEnabled.setStyleName("portlet-form-field-label");
		add(isEnabled);
		isEnabledBox.setChecked("true".equalsIgnoreCase(publisher.getIsEnabled()));
		isEnabledBox.setStyleName("portlet-form-input-field");
		add(isEnabledBox);
		
		Label maxBusinesses = new Label ("MaxBusinesses:");
		maxBusinesses.setStyleName("portlet-form-field-label");
		add(maxBusinesses);
		maxBusinessesBox.setText(String.valueOf(publisher.getMaxBusinesses()));
		maxBusinessesBox.setStyleName("portlet-form-input-field");
		add(maxBusinessesBox);
		
		Label maxServicesPerBusiness = new Label ("MaxServicePerBusiness:");
		maxServicesPerBusiness.setStyleName("portlet-form-field-label");
		add(maxServicesPerBusiness);
		maxServicesPerBusinessBox.setText(String.valueOf(publisher.getMaxServicePerBusiness()));
		maxServicesPerBusinessBox.setStyleName("portlet-form-input-field");
		add(maxServicesPerBusinessBox);
		
		Label maxBindingsPerService = new Label ("MaxBindingsPerService:");
		maxBindingsPerService.setStyleName("portlet-form-field-label");
		add(maxBindingsPerService);
		maxBindingsPerServiceBox.setText(String.valueOf(publisher.getMaxBindingsPerService()));
		maxBindingsPerServiceBox.setStyleName("portlet-form-input-field");
		add(maxBindingsPerServiceBox);
		
		Label maxTModels = new Label ("MaxTModels:");
		maxTModels.setStyleName("portlet-form-field-label");
		add(maxTModels);
		maxTModelBox.setText(String.valueOf(publisher.getMaxTModels()));
		maxTModelBox.setStyleName("portlet-form-input-field");
		add(maxTModelBox);
		
		saveButton.setText("Save");
		saveButton.setStyleName(("portlet-form-button"));
		saveButton.addClickListener(this);
		add(saveButton);
		
		newButton.setText("New");
		newButton.setStyleName(("portlet-form-button"));
		newButton.addClickListener(this);
		add(newButton);
		
		deleteButton.setText("Delete");
		deleteButton.setStyleName(("portlet-form-button"));
		deleteButton.addClickListener(this);
		add(deleteButton);
		
	}
	
	private void deletePublisher(String token){
		juddiApiService.deletePublisher(token, publisher.getAuthorizedName(), new AsyncCallback<JUDDIApiResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry. " + caught.getMessage());
			}

			public void onSuccess(JUDDIApiResponse response) {
				if (response.isSuccess()) {
					JUDDIPublisher.getInstance().hidePublisher();
				} else {
					Window.alert("error: " + response.getMessage() + ". Make sure the UDDI server is up and running.");
				}
			}
		});
	}
	
	private void newPublisher(String token){
		publisher = new Publisher();
		publisher.setIsEnabled("true");
		publisher.setMaxBusinesses(100);
		publisher.setMaxServicePerBusiness(1000);
		publisher.setMaxBindingsPerService(100);
		publisher.setMaxTModels(100);
		JUDDIPublisher.getInstance().displayPublisher(publisher);
	}

	public void onClick(Widget sender) {
		String token = JUDDIPublisher.getInstance().getToken();
		if (sender == saveButton) {
			savePublisher(token);
		} else if (sender == newButton) {
			newPublisher(token);
		} else if (sender == deleteButton) {
			deletePublisher(token);
		}
		
	}
	
	private void savePublisher(String token) {
		publisher.setAuthorizedName(idBox.getText());
		publisher.setPublisherName(nameBox.getText());
		publisher.setEmailAddress(emailAddressBox.getText());
		publisher.setIsAdmin(isAdminBox.isChecked()?"true":"false");
		publisher.setIsEnabled(isEnabledBox.isChecked()?"true":"false");
		publisher.setMaxBindingsPerService(("".equals(maxServicesPerBusinessBox))?null:Integer.valueOf(maxBindingsPerServiceBox.getText()));
		publisher.setMaxBusinesses(("".equals(maxBusinessesBox.getText()))?null:Integer.valueOf(maxBusinessesBox.getText()));
		publisher.setMaxServicePerBusiness(("".equals(maxBindingsPerServiceBox.getText()))?null:Integer.valueOf(maxBindingsPerServiceBox.getText()));
		publisher.setMaxTModels(("".equals(maxTModelBox.getText()))?null:Integer.valueOf(maxTModelBox.getText()));
		
		juddiApiService.savePublisher(token, publisher, new AsyncCallback<JUDDIApiResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry. " + caught.getMessage());
			}

			public void onSuccess(JUDDIApiResponse response) {
				if (response.isSuccess()) {
					Publisher publisher = response.getPublishers().get(0);
					JUDDIPublisher.getInstance().setSelectedPublisher(publisher.getAuthorizedName());
					JUDDIPublisher.getInstance().displayPublisher(publisher);
				} else {
					Window.alert("error: " + response.getMessage() + ". Make sure the UDDI server is up and running.");
				}
			}
		}); 
	}
	
}
