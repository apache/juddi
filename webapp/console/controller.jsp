<%@ page language="java" 
         import="java.io.BufferedReader,
                 java.io.InputStreamReader,
                 java.io.PrintWriter,
                 java.net.URL,
                 java.net.URLConnection" %>

<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%
  // Access the UDDI registry on localhost
  final String HTTP_PROXY_HOST = null;
  final String HTTP_PROXY_PORT = null;
  final URL INQUIRY_URL = new URL("http://localhost:8080/juddi/inquiry");
  final URL PUBLISH_URL = new URL("http://localhost:8080/juddi/publish");
  final URL ADMIN_URL =   new URL("http://localhost:8080/juddi/admin");
  
  // Access a remote UDDI registry
  //final String HTTP_PROXY_HOST = "proxy";
  //final String HTTP_PROXY_PORT = "80";
  //final URL INQUIRY_URL = new URL("http://[host]:[port]/juddi/inquiry");
  //final URL PUBLISH_URL = new URL("http://[host]:[port]/juddi/publish");
  //final URL ADMIN_URL =   new URL("http://[host]:[port]/juddi/admin");

  // Pull input parameters from the HTTP request
  String requestName = request.getParameter("request_name");
  String requestType = request.getParameter("request_type");
  String validateAction = request.getParameter("validate_button");
  String submitAction = request.getParameter("submit_button");
  String resetAction = request.getParameter("reset_button");
  String requestMsg = request.getParameter("soap_request");
  
  // Initialize the Session keys, target page and response message
  String requestKey = requestName+":request";
  String responseKey = requestName+":response"; 
  String requestTimeKey = requestName+":time";
  String targetPage = requestName+".jsp";  
  String responseMsg = null;
  
  // Initialize the response time variables
  long startTime = 0;
  long endTime = 0;
  long totalTime = 0;

  // Determine which action the user selected.
  if (validateAction != null)
  {
    // If user clicked the "Validate" button then check 
    // that the request XML is well-formed and validate
    // it against the UDDI v2.0 XML Schema.

    responseMsg = "Validation is not implemented yet.";
  }
  else if (resetAction != null)
  {
    // If user clicked the "Reset" button then initialize 
    // the request & response values to null.

    requestMsg = null;
    responseMsg = null;
  }
  else if (submitAction != null) 
  {
    // If the user didn't select the "Reset" then they 
    // must have clicked the "Submit" button.
    
    requestMsg = requestMsg.toString().trim();
    
    try 
    {
      // If HTTP proxy values are specified then use them
      if ((HTTP_PROXY_HOST != null) && (HTTP_PROXY_PORT != null)) {
        System.setProperty("http.proxyHost",HTTP_PROXY_HOST);      
        System.setProperty("http.proxyPort",HTTP_PROXY_PORT);
      }
      else {
      // In case they were specified but you no longer need them
        System.setProperty("http.proxyHost","");      
        System.setProperty("http.proxyPort","");
      }

      // Determine which endpoint the request should use
      URL targetURL = null;
      if (requestType.equals("publish"))
        targetURL = PUBLISH_URL;
      else if (requestType.equals("inquiry"))
        targetURL = INQUIRY_URL;
      else if (requestType.equals("admin"))
        targetURL = ADMIN_URL;
      
      // Declare & initialize the UDDI server properties
      String hostname = targetURL.getHost();
      int port = targetURL.getPort();
      String path = targetURL.getPath();
      
      // Default to port 80 if one wasn't specified
      if (port < 0)
        port = 80;

      // Start the clock
      startTime = System.currentTimeMillis();
      
      // Create HTTP Connection
      URLConnection connection = targetURL.openConnection();
      connection.setDoOutput(true);
      connection.setRequestProperty("SOAPAction","");
      PrintWriter writer = new PrintWriter(connection.getOutputStream());

      // Send the HTTP Request
      writer.write(requestMsg);
      writer.close();
      
      // Read the HTTP Response
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); 
      StringBuffer msg = new StringBuffer();           
      String line;
      while((line = reader.readLine()) != null)
        msg.append(line+"\n");
      
      // Convert reponse to String and trim whitespace
      responseMsg = msg.toString().trim();
      
      // Close the HTTP Connection
      reader.close();

      // Stop the clock & calculate time
      endTime = System.currentTimeMillis();
      totalTime = endTime - startTime;
     } 
    catch (Exception e) 
    {
      e.printStackTrace();
    }    
  }
  
  // Set new values into the session
  session.setAttribute(requestKey,requestMsg);
  session.setAttribute(responseKey,responseMsg);
  session.setAttribute(requestTimeKey,String.valueOf(totalTime).trim());
  
  // Redirect back to the source page
  request.getRequestDispatcher(targetPage).forward(request,response);
%>
