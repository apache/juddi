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
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginPanel extends FlowPanel implements ClickListener {
	
	private Label tokenLabel = new Label("");
	private Button tokenButton = new Button("Login");
	private TextBox usernameBox = new TextBox();
	private PasswordTextBox passwordBox = new PasswordTextBox();

	UDDIBrowser browser = null;
	
	private SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);
	
	public LoginPanel(UDDIBrowser browser) {
		super();
		this.browser = browser;
		getElement().setId("browser-body");
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
		RootPanel.get("token").add(tokenLabel);  //TODO at some point we want to hide this
		
	}
	
	public Button getTokenButton() {
		return tokenButton;
	}

	public void setTokenButton(Button tokenButton) {
		this.tokenButton = tokenButton;
	}

	public SecurityServiceAsync getSecurityService() {
		return securityService;
	}

	public void setSecurityService(SecurityServiceAsync securityService) {
		this.securityService = securityService;
	}

	public Label getTokenLabel() {
		return tokenLabel;
	}

	public void setTokenLabel(Label tokenLabel) {
		this.tokenLabel = tokenLabel;
	}

	public TextBox getUsernameBox() {
		return usernameBox;
	}

	public void setUsernameBox(TextBox usernameBox) {
		this.usernameBox = usernameBox;
	}

	public PasswordTextBox getPasswordBox() {
		return passwordBox;
	}

	public void setPasswordBox(PasswordTextBox passwordBox) {
		this.passwordBox = passwordBox;
	}

	public void onClick(Widget sender) {
		if (sender == getTokenButton()) {
			getToken(getUsernameBox().getText(), getPasswordBox().getText());
			
		} else {
			System.err.println("undefined");
		}
	}
	
	protected void getToken(String user, String password) {

		getSecurityService().get(user, password, new AsyncCallback<SecurityResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry.");
			}

			public void onSuccess(SecurityResponse response) {
				if (response.isSuccess()) {
					
					String token = response.getResponse();
					browser.setToken(token);
					if (token == null ) {
						browser.getLoginPanel().setVisible(true);
					} else {
						browser.getLoginPanel().setVisible(false);
						getTokenLabel().setText("token: " + token);
						browser.getBrowsePanel().setVisible(true);
						browser.getBrowsePanel().getBusinesses("all");
					}
				} else {
					//browser.getBrowsePanel().add(browser.getLoginPanel());
					Window.alert("error: " + response.getMessage() + ". Make sure the UDDI server is up and running.");
				}
			}
		});
		
	}

	
}
