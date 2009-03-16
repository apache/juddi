package org.apache.juddi.portlets.client;

import java.io.Serializable;

public class SecurityResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;
	boolean isSuccess;
	String errorCode;
	String response;
	
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}

}
