<jsp:include page="header.html" />

<%
   String requestName = "get_bindingDetail";
   String requestType = "inquiry";
   String requestKey = requestName+":request";
   String responseKey = requestName+":response";
   String requestTimeKey = requestName+":time";
%>

<h3><%= requestName%></h3>
<div class="link">
The <a href="uddiv2api.html#_Toc25137717" target="api">get_bindingDetail</a> API call is 
for requesting the run-time <a href="uddiv2data.html#_Toc25130769" target="api">bindingTemplate</a> 
information for the purpose of invoking a registered business API. If an error occurs while processing this 
API call, a <a href="uddiv2api.html#_Toc25137750" target="api">dispositionReport</a> element 
will be returned to the caller within a <a href="uddiv2api.html#_Toc25137756" target="api">SOAP 
Fault</a> containing information about the <a href="uddiv2api.html#_Toc25137748" target="api">error</a> that 
was encountered.
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
    <get_bindingDetail generic="2.0" xmlns="urn:uddi-org:api_v2">
      <bindingKey>*****</bindingKey>
    </get_bindingDetail>
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