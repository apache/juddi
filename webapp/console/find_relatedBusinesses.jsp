<jsp:include page="header.html" />

<%
   String requestName = "find_relatedBusinesses";
   String requestType = "inquiry";
   String requestKey = requestName+":request";
   String responseKey = requestName+":response";
   String requestTimeKey = requestName+":time";
%>

<h3><%= requestName%></h3>
<div class="link">
The <a href="uddiv2api.html#_Toc25137714" target="doc">find_relatedBusinesses</a> API call returns
a <a href="uddiv2data.html#_Toc25130808" target="doc">relatedBusinessesList</a> message that contains 
zero or more <a href="uddiv2data.html#_Toc25130808" target="doc">relatedBusinessInfo</a> structures matching the criteria specified in the argument list. If 
an error occurs while processing this API call, a <a href="uddiv2api.html#_Toc25137750" target="doc">dispositionReport</a> 
element will be returned to the caller within a <a href="uddiv2api.html#_Toc25137756" target="doc">SOAP 
Fault</a> containing information about the <a href="uddiv2api.html#_Toc25137748" target="doc">error</a> that 
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
    <find_relatedBusinesses generic="2.0" xmlns="urn:uddi-org:api_v2">
      <businessKey>*****</businessKey>
      <keyedReference tModelKey="******" keyName="******" keyValue="******" />
    </find_relatedBusinesses>
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