<jsp:include page="header.html" />

<%
   String requestName = "save_service";
   String requestType = "publish";
   String requestKey = requestName+":request";
   String responseKey = requestName+":response";
   String requestTimeKey = requestName+":time";
%>

<h3><%= requestName%></h3>
<div class="link">
The save_service API call adds or updates one or more businessService elements.
If any error occurs in processing this API call, a dispositionReport element 
will be returned to the caller within a SOAP Fault containing an error number 
indicating 
<a href="/uddi_errors#E_authTokenExpired">E_authTokenExpired</a>, 
<a href="/uddi_errors#E_authTokenRequired">E_authTokenRequired</a>, 
<a href="/uddi_errors#E_invalidKeyPassed">E_invalidKeyPassed</a>, 
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
    <save_service generic="2.0" xmlns="urn:uddie-org:api_v2">
      <authInfo>*****</authInfo>
      <businessService businessKey="*****" serviceKey="">
        <name xml:lang="en">*****</name>
        <description xml:lang="en">*****</description>
        <bindingTemplates>
          <bindingTemplate bindingKey="">
            <accessPoint URLType="http">*****</accessPoint>
            <tModelInstanceDetails>
              <tModelInstanceInfo tModelKey="*****">
                <instanceDetails>
                  <overviewDoc>
                    <overviewURL>*****</overviewURL>
                  </overviewDoc>
                </instanceDetails>
              </tModelInstanceInfo>
            </tModelInstanceDetails>
          </bindingTemplate>
        </bindingTemplates>
      </businessService>
    </save_service>
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