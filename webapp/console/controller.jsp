<%@ page language="java" import="java.util.*,java.io.*,java.net.*" %>

<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%
  String requestName = request.getParameter("request_name");
  String requestType = request.getParameter("request_type");
  String submitAction = request.getParameter("submit_button");
  String resetAction = request.getParameter("reset_button");
  
  String targetPage = requestName+".jsp";
  String requestKey = requestName+":request";
  String responseKey = requestName+":response"; 
  String requestMsg = request.getParameter("soap_request");
  String responseMsg = "";

  if (resetAction != null)
  {
    requestMsg = null;
    responseMsg = null;
  }
  else if (submitAction != null) 
  {
    // if not "Reset" then the user must have clicked "Submit"
	  
	  try 
	  {
		  // Assemble the target UDDI request URL
		  String hostname = "localhost";
		  int port = 8080;
		  String path = "/juddi/"+requestType;

	    // Create a Socket
	    InetAddress addr = InetAddress.getByName(hostname);
	    Socket sock = new Socket(addr,port);
			
	    // Send the HTTP Headers
	    BufferedWriter  wr = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(),"UTF-8"));
	    wr.write("POST " + path + " HTTP/1.0\r\n");
	    wr.write("Content-Length: " + requestMsg.length() + "\r\n");
	    wr.write("Content-Type: text/xml; charset=\"utf-8\"\r\n");
	    wr.write("\r\n");
			
	    // Send the HTTP Request
	    wr.write(requestMsg);
	    wr.flush();
			
	    // Read the HTTP Response
	    BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	    String line;
	    StringBuffer message = new StringBuffer();
	    while((line = rd.readLine()) != null)
	      message.append(line+"\n");
	      
	    responseMsg = message.toString();
	   } 
	  catch (Exception e) 
	  {
	    e.printStackTrace();
	  }
	}
  
  session.setAttribute(requestKey,requestMsg);
  session.setAttribute(responseKey,responseMsg);
  
  // Redirect back to the source page
  request.getRequestDispatcher(targetPage).forward(request,response);
%>
