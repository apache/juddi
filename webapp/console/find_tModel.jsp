<jsp:include page="header.html" />

<%
   String requestName = "find_tModel";
   String requestType = "inquiry";
   String requestKey = requestName+":request";
   String responseKey = requestName+":response";
   String requestTimeKey = requestName+":time";
%>

<h3><%= requestName%></h3>
<div class="link">
This find_tModel API call is for locating a list of tModel entries that 
match a set of specific criteria. The response will be a list of abbreviated 
information about registered tModel data that matches the criteria 
specified.  This result will be returned in a tModelList message.
If any error occurs in processing this API call, a dispositionReport element 
will be returned to the caller within a SOAP Fault containing an error number 
indicating that an
<a href="/uddi_errors#E_unsupported">E_unsupported</a> error was encountered.
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
    <find_tModel generic="2.0" xmlns="urn:uddi-org:api_v2">
      <name>*****</name>
      <identifierBag>
        <keyedReference tModelKey="******" keyName="******" keyValue="******" />
      </identifierBag>
      <categoryBag>
        <keyedReference tModelKey="*****" keyName="*****" keyValue="*****" />
      </categoryBag>
    </find_tModel>
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