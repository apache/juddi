<jsp:include page="header.html" />

<%
   String requestName = "delete_publisher";
   String requestType = "admin";
   String requestKey = requestName+":request";
   String responseKey = requestName+":response";
   String requestTimeKey = requestName+":time";
%>

<h3><%= requestName%></h3>
<div class="link">
The delete_publisher API call causes one or more instances of publisher data 
to be deleted from the jUDDI registry. 
If any error occurs in processing this API call, a dispositionReport element 
will be returned to the caller within a SOAP Fault containing an error number 
indicating an
<a href="/uddi_errors#E_authTokenExpired">E_authTokenExpired</a> or 
<a href="/uddi_errors#E_authTokenRequired">E_authTokenRequired</a> error was 
encountered.
</div>

<form method="post" action="controller.jsp">
<textarea class=msgs id=soap_request name=soap_request rows=15 cols=75 wrap=off><%
String requestMessage = (String)session.getAttribute(requestKey);
if (requestMessage != null) {
  out.print(requestMessage);
} else { %>
<?xml version="1.0" encoding="utf-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
  <soapenv:Body>
    <delete_publisher generic="2.0" xmlns="urn:juddi-org:api_v2">
      <authInfo>*****</authInfo>
      <publisherID>*****</publisherID>
    </delete_publisher>
  </soapenv:Body>
</soapenv:Envelope>
<% } %>
</textarea>

<%
String requestTime = (String)session.getAttribute(requestTimeKey);
if (requestTime == null) {
  requestTime = "0";
} %>
<table cellpadding="4" width="100%">
<tr>
<td>
<input type="hidden" name="request_name" value=<%=requestName%>>
<input type="hidden" name="request_type" value=<%=requestType%>>
<input type="submit" name="submit_button" value="Submit">
<input type="submit" name="reset_button" value="Reset">
</td>
<td align="right">
Time: <strong><%= requestTime%></strong> milliseconds
</td>
</tr>
</table>

<textarea class=msgs id=soap_response name=soap_response rows=20 cols=75 wrap=off><%
String responseMessage = (String)session.getAttribute(responseKey);
if (responseMessage != null) {
  out.print(responseMessage);
} %>
</textarea>
</form>

<jsp:include page="footer.html" />