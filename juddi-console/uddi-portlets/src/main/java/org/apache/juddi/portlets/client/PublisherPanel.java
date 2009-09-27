package org.apache.juddi.portlets.client;

import org.apache.juddi.portlets.client.model.Publisher;
import org.apache.juddi.portlets.client.service.JUDDIApiResponse;
import org.apache.juddi.portlets.client.service.JUDDIApiService;
import org.apache.juddi.portlets.client.service.JUDDIApiServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class PublisherPanel extends FlowPanel {

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
	Publisher publisher = null;
	
	public PublisherPanel(Publisher publisher) {
		
		if (publisher==null) {
			newPublisher();
		} else {
			this.publisher = publisher;
		}
		
		FlexTable flexTable = new FlexTable();
		add(flexTable);

		Label id = new Label ("Id:");
		id.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(0, 0, id);
		idBox.setText(this.publisher.getAuthorizedName());
		idBox.setStyleName("portlet-form-input-field");
		flexTable.setWidget(0, 1, idBox);
		
		Label name = new Label ("Name:");
		name.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(1, 0, name);
		nameBox.setText(this.publisher.getPublisherName());
		nameBox.setStyleName("portlet-form-input-field");
		flexTable.setWidget(1, 1,nameBox);
		
		Label email = new Label ("Email:");
		email.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(2, 0,email);
		emailAddressBox.setText(this.publisher.getEmailAddress());
		emailAddressBox.setStyleName("portlet-form-input-field");
		flexTable.setWidget(2, 1,emailAddressBox);
		
		Label isAdmin = new Label ("Is Admin:");
		isAdmin.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(3, 0,isAdmin);
		isAdminBox.setChecked("true".equalsIgnoreCase(this.publisher.getIsAdmin()));
		isAdminBox.setStyleName("portlet-form-input-field");
		flexTable.setWidget(3, 1,isAdminBox);
		
		Label isEnabled = new Label ("Is Enabled:");
		isEnabled.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(4, 0,isEnabled);
		isEnabledBox.setChecked("true".equalsIgnoreCase(this.publisher.getIsEnabled()));
		isEnabledBox.setStyleName("portlet-form-input-field");
		flexTable.setWidget(4, 1,isEnabledBox);
		
		Label maxBusinesses = new Label ("MaxBusinesses:");
		maxBusinesses.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(5, 0,maxBusinesses);
		maxBusinessesBox.setText(String.valueOf(this.publisher.getMaxBusinesses()));
		maxBusinessesBox.setStyleName("portlet-form-input-field");
		flexTable.setWidget(5, 1,maxBusinessesBox);
		
		Label maxServicesPerBusiness = new Label ("MaxServicePerBusiness:");
		maxServicesPerBusiness.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(6, 0,maxServicesPerBusiness);
		maxServicesPerBusinessBox.setText(String.valueOf(this.publisher.getMaxServicePerBusiness()));
		maxServicesPerBusinessBox.setStyleName("portlet-form-input-field");
		flexTable.setWidget(6, 1,maxServicesPerBusinessBox);
		
		Label maxBindingsPerService = new Label ("MaxBindingsPerService:");
		maxBindingsPerService.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(7, 0,maxBindingsPerService);
		maxBindingsPerServiceBox.setText(String.valueOf(this.publisher.getMaxBindingsPerService()));
		maxBindingsPerServiceBox.setStyleName("portlet-form-input-field");
		flexTable.setWidget(7, 1,maxBindingsPerServiceBox);
		
		Label maxTModels = new Label ("MaxTModels:");
		maxTModels.setStyleName("portlet-form-field-label-right");
		flexTable.setWidget(8, 0,maxTModels);
		maxTModelBox.setText(String.valueOf(this.publisher.getMaxTModels()));
		maxTModelBox.setStyleName("portlet-form-input-field");
		flexTable.setWidget(8, 1,maxTModelBox);
		
	}
	
	protected void deletePublisher(String token){
		if (publisher!=null) {
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
	}
	
	protected void newPublisher(){
		publisher = new Publisher();
		publisher.setIsEnabled("true");
		publisher.setMaxBusinesses(100);
		publisher.setMaxServicePerBusiness(1000);
		publisher.setMaxBindingsPerService(100);
		publisher.setMaxTModels(100);
	}
	
	protected void savePublisher(String token) {
		if (publisher!=null) {
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
	
}
