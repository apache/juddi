package org.apache.juddi.portlets.client;

import org.apache.juddi.portlets.client.GetTokenService;
import org.apache.juddi.portlets.client.GetTokenServiceAsync;

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
import com.google.gwt.user.client.ui.Widget;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Application implements EntryPoint, ClickListener {

	Label tokenLabel = new Label("");
	private Button getTokenButton = new Button("getToken");
	private TextBox usernameBox = new TextBox();
	private PasswordTextBox passwordBox = new PasswordTextBox();
	private String token = null;
	private GetTokenServiceAsync tokenService = (GetTokenServiceAsync) GWT.create(GetTokenService.class);
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() { 
  
     RootPanel.get().add(new Label ("Publisher:"));
     RootPanel.get().add(usernameBox);
     RootPanel.get().add(new Label ("Password:"));
     RootPanel.get().add(passwordBox);
     getTokenButton.addClickListener(this);
     RootPanel.get().add(getTokenButton);
     RootPanel.get().add(tokenLabel);
  }
  
  public void onClick(Widget sender) {
		if (sender == getTokenButton) {
			String user =usernameBox.getText();
			String pw   =passwordBox.getText();
			getToken(user, pw);
		} else {
			System.err.println("undefined");
		}
	}

	private String getToken(String user, String password) {

	   tokenService.get(user, password, new AsyncCallback<String>() 
	   {
			public void onFailure(Throwable caught) {
				Window.alert("Could not connect to the UDDI registry.");
			}

			public void onSuccess(String result) {
				System.out.println(result);
				token = result;
				tokenLabel.setText("Token=" + token);
			}
		});
		return token;

	}
}
