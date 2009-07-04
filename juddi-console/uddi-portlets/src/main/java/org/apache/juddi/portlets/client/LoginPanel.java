package org.apache.juddi.portlets.client;

import org.apache.juddi.portlets.client.service.SecurityResponse;
import org.apache.juddi.portlets.client.service.SecurityService;
import org.apache.juddi.portlets.client.service.SecurityServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginPanel extends FlowPanel implements ClickListener {
	
	//UI Widgets
	private Button tokenButton = new Button("Login");
	private TextBox usernameBox = new TextBox();
	private PasswordTextBox passwordBox = new PasswordTextBox();
	
	private String publisherId;
	Application parentApp = null;
	private SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);
	
	public LoginPanel(Application application) {
		super();
		this.parentApp = application;
		getElement().setId("parentApp-body");
		
		Label publisher = new Label ("Publisher:");
		publisher.setStyleName("portlet-form-field-label");
		add(publisher);
		usernameBox.setStyleName("portlet-form-input-field");
		add(usernameBox);
		
		Label password = new Label ("Password:");
		password.setStyleName("portlet-form-field-label");
		add(password);
		passwordBox.setStyleName("portlet-form-input-field");
		add(passwordBox);
		
		tokenButton.addClickListener(this);
		tokenButton.setStyleName(("portlet-form-button"));
		add(tokenButton); 
	}
	
	public void onClick(Widget sender) {
		if (sender == tokenButton) {
			getToken(usernameBox.getText(), passwordBox.getText());
		} else {
			System.err.println("undefined");
		}
	}
	/**
	 * Obtains an authenticationToken
	 * @param user
	 * @param password
	 */
	protected void getToken(String user, String password) {
		securityService.get(user, password, new AsyncCallback<SecurityResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry. " + caught.getMessage());
			}

			public void onSuccess(SecurityResponse response) {
				if (response.isSuccess()) {
					
					String token = response.getResponse();
					parentApp.setToken(token);
					if (token == null ) {
						parentApp.getLoginPanel().setVisible(true);
					} else {
						parentApp.getLoginPanel().setVisible(false);
						parentApp.getApplicationPanel().setVisible(true);
						parentApp.setUsername(response.getUsername());
						parentApp.getApplicationPanel().loadData();
					}
				} else {
					Window.alert("error: " + response.getMessage() + ". Make sure the UDDI server is up and running.");
				}
			}
		});
	}
	
	public String getPublisherId() {
		return publisherId;
	}
	
}
