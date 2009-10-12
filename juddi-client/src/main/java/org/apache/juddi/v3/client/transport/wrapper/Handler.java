package org.apache.juddi.v3.client.transport.wrapper;

public class Handler {
	private String methodName;
	private Class<?> parameter;

	public Handler() {
		super();
	}
	
	public Handler(String methodName, Class<?> parameter) {
		super();
		this.methodName = methodName;
		this.parameter = parameter;
	}
	
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Class<?> getParameter() {
		return parameter;
	}
	public void setParameter(Class<?> parameter) {
		this.parameter = parameter;
	}	
}
