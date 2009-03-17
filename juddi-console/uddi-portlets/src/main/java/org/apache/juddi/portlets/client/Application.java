package org.apache.juddi.portlets.client;

import java.util.Map;

import org.apache.juddi.portlets.client.SecurityService;
import org.apache.juddi.portlets.client.SecurityServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Application implements EntryPoint, ClickListener {

	Label tokenLabel = new Label("");
	Label tmodelLabel = new Label("");
	private VerticalPanel loginPanel = new VerticalPanel();
	private Button getTokenButton = new Button("getToken");
	private Button getTModelButton = new Button("getTModels");
	private TextBox usernameBox = new TextBox();
	private PasswordTextBox passwordBox = new PasswordTextBox();
	private String token = null;
	private TextBox tmodelKeyBox = new TextBox();
	private SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);
	private InquiryServiceAsync inquiryService = (InquiryServiceAsync) GWT.create(InquiryService.class);
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() { 

		loginPanel.add(new Label ("Publisher:"));
		loginPanel.add(usernameBox);
		loginPanel.add(new Label ("Password:"));
		loginPanel.add(passwordBox);
		getTokenButton.addClickListener(this);
		loginPanel.add(getTokenButton);
		
		RootPanel.get("login").add(loginPanel);
		RootPanel.get().add(tokenLabel);
		getTModelButton.addClickListener(this);
		RootPanel.get().add(tmodelKeyBox);
		RootPanel.get().add(getTModelButton);
		RootPanel.get().add(tmodelLabel);
	}

	public void onClick(Widget sender) {
		if (sender == getTokenButton) {
			String user =usernameBox.getText();
			String pw   =passwordBox.getText();
			getToken(user, pw);
		} else if (sender == getTModelButton) {
			if (token!=null) {
				getTModels(token,tmodelKeyBox.getText());
			}
		} else {
			System.err.println("undefined");
		}
	}

	private void getToken(String user, String password) {

		securityService.get(user, password, new AsyncCallback<SecurityResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry.");
			}

			public void onSuccess(SecurityResponse response) {
				if (response.isSuccess) {
					token = response.getResponse();
					tokenLabel.setText("token: " + token);
					//RootPanel.setVisible(loginPanel, false);
				} else {
					tokenLabel.setText("error: " + response.getMessage());
				}
			}
		});
	}
	
	private void getTModels(String token, String tmodelKey) {

		inquiryService.getTModelDetail(token, tmodelKey, new AsyncCallback<InquiryResponse>() 
		{
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry.");
			}

			public void onSuccess(InquiryResponse response) {
				if (response.isSuccess) {
					Map<String,String> tModelMap= response.getResponse();
					tmodelLabel.setText("tmodelMap: " + tModelMap);
					//RootPanel.setVisible(loginPanel, false);
				} else {
					tmodelLabel.setText("error: " + response.getMessage());
				}
			}
		});
	}
	
}
