<jsp:include page="header.html" />

<%
   String requestName = "save_business";
   String requestType = "publish";
   String requestKey = requestName+":request";
   String responseKey = requestName+":response";
   String requestTimeKey = requestName+":time";
%>

<h3><%= requestName%></h3>
<div class="link">
The save_business API call is used to save or update information about a 
complete businessEntity element.  This API has the broadest scope of all 
of the save_xx API calls in the publisher API, and can be used to make 
sweeping changes to the published information for one or more businessEntity 
elements controlled by an individual.
If any error occurs in processing this API call, a dispositionReport element 
will be returned to the caller within a SOAP Fault containing an error number 
indicating 
<a href="/uddi_errors#E_authTokenExpired">E_authTokenExpired</a>, 
<a href="/uddi_errors#E_authTokenRequired">E_authTokenRequired</a>, 
<a href="/uddi_errors#E_invalidKeyPassed">E_invalidKeyPassed</a>, 
<a href="/uddi_errors#E_invalidProjection">E_invalidProjection</a>, 
<a href="/uddi_errors#E_userMismatch">E_userMismatch</a>, 
<a href="/uddi_errors#E_invalidValue">E_invalidValue</a>, 
<a href="/uddi_errors#E_requestTimeout">E_requestTimeout</a>, 
<a href="/uddi_errors#E_valueNotAllowed">E_valueNotAllowed</a> or 
<a href="/uddi_errors#E_accountLimitExceeded">E_accountLimitExceeded</a> 
error was encountered.
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
    <save_business generic="2.0" xmlns="urn:uddi-org:api_v2">
      <authInfo>*****</authInfo>
      <businessEntity businessKey="">
        <name xml:lang="en">*****</name>
        <description xml:lang="en">*****</description>
        <contacts>
          <contact useType="*****">
            <personName>*****</personName>
            <phone>*****</phone>
            <email>*****</email>
          </contact>
        </contacts>
      </businessEntity>
    </save_business>
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