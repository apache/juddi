<jsp:include page="header.html" />

<%
   String requestName = "delete_tModel";
   String requestType = "publish";
   String requestKey = requestName+":request";
   String responseKey = requestName+":response";
   String requestTimeKey = requestName+":time";
%>

<h3><%= requestName%></h3>
<div class="link">
The <a href="uddiv2api.html#_Toc25137736" target="doc">delete_tModel</a> API call is used to logically delete 
one or more <a href="uddiv2data.html#_Toc25130775" target="doc">tModel</a> structures. Logical deletion hides 
the deleted tModels from <a href="uddiv2api.html#_Toc25137716" target="doc">find_tModel</a> result sets but 
does not physically delete them. Deleting an already deleted <a href="uddiv2data.html#_Toc25130775" target="doc">tModel</a> 
has no effect. If an error occurs while processing this API call, a 
<a href="uddiv2api.html#_Toc25137750" target="doc">dispositionReport</a> element will be returned to the caller 
within a <a href="uddiv2api.html#_Toc25137756" target="doc">SOAP Fault</a> containing information about the 
<a href="uddiv2api.html#_Toc25137748" target="doc">error</a> that was encountered.
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
    <delete_tModel generic="2.0" xmlns="urn:uddi-org:api_v2">
      <authInfo>***</authInfo>
      <tModelKey>***</tModelKey>
    </delete_tModel>
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
<input type="submit" name="validate_button" value="Validate">
<input type="submit" name="submit_button" value="Submit">
<input type="submit" name="reset_button" value="Reset">
</td>
<td align="right">
Time: <strong><%= requestTime%></strong> milliseconds
</td>
</tr>
</table>

<textarea class=msgs id=soap_response name=soap_response rows=25 cols=75 wrap=off><%
String responseMessage = (String)session.getAttribute(responseKey);
if (responseMessage != null) {
  out.print(responseMessage);
} %>
</textarea>
</form>

<jsp:include page="footer.html" />