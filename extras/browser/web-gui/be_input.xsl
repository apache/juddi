<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:uddi="urn:uddi-org:api_v2"
	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
<xsl:param name="operation">create</xsl:param>
<xsl:param name="headline">Business Entity Input</xsl:param>
<xsl:param name="TModels">leer</xsl:param>

<xsl:template match="text()|@*"/>
<xsl:template match="/">
<html>
<head>
	<title>Business Entity Detail</title>
	<link href="styles.css" type="text/css" rel="stylesheet"/>
	<SCRIPT LANGUAGE="JavaScript">
		function DoCheck(){
			if(document.be_input.business_name.value == ""){
       	 		alert("Please fill in the Business Entity Name!");
        			document.be_input.business_name.focus();
			return false;
			}
			if(document.be_input.contact_person_name.value == ""){
				alert("Please fill in the Contact Name!");
				document.be_input.contact_person_name.focus();
			return false;
			}
<!--			
			if(document.be_input.email.value == ""){
				alert("Please fill in the Contact Email!");
				document.be_input.email.focus();
			return false;
			}
			if(document.be_input.email.value != ""){
			//alert("EMAIL");
				if(document.be_input.email.value.indexOf("@") == -1 || document.be_input.email.value.indexOf(".") == -1){
	       	 	alert("Please insert a valid E-Mail-Adress!");
				document.be_input.email.focus();
			return false;
				}
			}
-->
		return true;
		}
	</SCRIPT>
</head>
<body leftmargin="0" topmargin="0" marginheight="0" marginwidth="0">
	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><img src="images/transparent.gif" width="170" height="105" border="0"/></td>
			<td>
<!--EMBED header-->
<xsl:copy-of select="//header/*"/>
			</td>
		</tr>
<!-- STATUS -->
		<tr>
			<td><img src="images/transparent.gif" height="18"/></td>
			<td valign="top" align="left" class="status">
<xsl:value-of select="//session/uddi/name"/>
<xsl:if test="//uddi:name"> / <xsl:value-of select="//uddi:name"/></xsl:if>
			</td>
		</tr>
		<tr>
			<td valign="top" align="left">
				<table border="0" cellpadding="0" cellspacing="1">
					<tr>
						<td width="170">
<!-- EMBED navigation -->
<xsl:copy-of select="//nav/*"/>
						</td>
					</tr>
				</table>
			</td>
			<td valign="top" align="left">
<!--START content-->
<form action="be_publish" method="post" name="be_input" OnSubmit="return DoCheck();">
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#336699">
					<tr>
						<td bgcolor="ffffff"><img src="images/transparent.gif" height="4"/><br/>
<!-- Headline-->
							<span class="headline">
								<xsl:choose>
									<xsl:when test="starts-with($headline,'Business Entity Input')">
										Business Entity Input
									</xsl:when>
									<xsl:otherwise>
										Business Entity Edit
									</xsl:otherwise>
								</xsl:choose><p/>
							</span>
							<table width="690" border="0" cellpadding="4" cellspacing="1" bgcolor="#ffffff">
<!--input type="hidden" name="method" value="publish_business"/>
<input type="hidden" name="publisher_id" value="juddi"/>
<input type="hidden" name="publisher_password" value="juddi"/>
<input type="hidden" name="resultformat" value="xml"/>
<input type="hidden" name="contact_usetype" value="business contact"/>
<input type="hidden" name="address_usetype" value="business"/-->
<input type="hidden" name="publishURL" value="{//session/uddi/publishURL}"/>
<input type="hidden" name="pipeline" value="be_publish"/>
							<xsl:if test="//uddi:businessEntity/@businessKey">
								<input type="hidden" name="businessKey" value="{//uddi:businessEntity/@businessKey}"/>
							</xsl:if>
								<tr>
									<td width="200" height="1"><img src="images/transparent.gif" width="200" height="1"/></td>
									<td width="10"><img src="images/transparent.gif" width="10" height="1"/></td>
									<td width="480"><img src="images/transparent.gif" width="10" height="1"/></td>
								</tr>
								<tr>
									<td width="200" valign="top">Business Entity Name</td>
									<td width="10" align="left" class="required">* </td>
									<td width="480" valign="top"><input type="text" name="business_name" value="{//uddi:businessEntity/uddi:name}" class="input200"/></td>
								</tr>
								<tr>
									<td width="200" valign="top">Description</td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top"><input type="text" name="default_description" value="{//uddi:description}" class="input200"/></td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
				<p/>
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#336699">
					<tr>
						<td bgcolor="ffffff">
							<table width="690" border="0" cellpadding="4" cellspacing="1" bgcolor="#ffffff">
								<tr>
									<td><img src="images/transparent.gif" height="4"/></td>
<!--Contact Information-->
									<td colspan="2" class="subheadform">Contact Information</td>
								</tr>
								<tr>
									<td width="200" height="1"><img src="images/transparent.gif" width="200" height="1"/></td>
									<td width="10"><img src="images/transparent.gif" width="10" height="1"/></td>
									<td width="480"><img src="images/transparent.gif" width="10" height="1"/></td>
								</tr>
								<tr>
									<td width="200" valign="top">Name</td>
									<td width="10" align="left" class="required">* </td>
									<td width="480" valign="top"><input type="text" name="contact_person_name" value="{//uddi:personName}" class="input200"/></td>
								</tr>
								<tr>
									<td width="200" valign="top">Address</td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top"><input type="text" name="addressline1" value="{//uddi:address/uddi:addressLine[1]}" class="input200"/></td>
								</tr>
								<tr>
									<td width="200" height="1"><img src="images/transparent.gif" width="200" height="1"/></td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top"><input type="text" name="addressline2" value="{//uddi:address/uddi:addressLine[2]}" class="input200"/></td>
								</tr>
								<tr>
									<td width="200" height="1"><img src="images/transparent.gif" width="200" height="1"/></td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top"><input type="text" name="addressline3" value="{//uddi:address/uddi:addressLine[3]}" class="input200"/></td>
								</tr>
								<tr>
									<td width="200" height="1"><img src="images/transparent.gif" width="200" height="1"/></td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top"><input type="text" name="addressline4" value="{//uddi:address/uddi:addressLine[4]}" class="input200"/></td>
								</tr>
								<tr>
									<td width="200" valign="top">Phone</td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top">	<input type="text" name="phone_number" value="{//uddi:phone}" class="input200"/></td>
								</tr>
								<tr>
									<td width="200" valign="top">email</td>
									<td width="10" align="left" class="required">* </td>
									<td width="480" valign="top">	<input type="text" name="email" value="{//uddi:email}" class="input200"/></td>
								</tr>
								<tr>
									<td width="200" valign="top"><img src="images/transparent.gif" width="10"/></td>
									<td width="10" align="left" class="required">* </td>
									<td width="480" valign="top" class="required">required</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
				<p/>
<!--list TModel-->
<xsl:if test="//soapenv:Envelope//uddi:categoryBag/uddi:keyedReference">
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#336699">
					<tr>
						<td bgcolor="ffffff">
							<table width="690" border="0" cellpadding="0" cellspacing="2" bgcolor="#ffffff">
								<tr>
									<td width="200"><img src="images/transparent.gif" height="4" width="200"/></td>
									<td colspan="3" class="subheadform">TModels</td>
								</tr>
								<tr>
									<td width="200" height="1"><img src="images/transparent.gif" width="200" height="1"/></td>
									<td width="10"><img src="images/transparent.gif" width="10" height="1"/></td>
									<td width="460"><img src="images/transparent.gif" width="10" height="1"/></td>
									<td width="20"><img src="images/transparent.gif" width="20" height="1"/></td>
								</tr>
								<tr>
									<td colspan="4">
										<table width="686" border="0" cellpadding="0" cellspacing="2" bgcolor="#ffffff">
											<tr>
												<td width="400" valign="top"><u>Name</u></td>
												<td width="270" valign="top"><u>Value</u></td>
												<td valign="top"><u>remove</u></td>
											</tr>
<xsl:for-each select="//soapenv:Envelope//uddi:categoryBag/uddi:keyedReference">
											<tr>
<input type="hidden" name="tModelKey" value="{@tModelKey}"/>
												<td width="200">
												<xsl:choose>
													<xsl:when test="starts-with(@tModelKey,'uuid:A035A07C-F362-44DD-8F95-E2B134BF43B4')">
														<input type="text" name="keyName" value="{@keyName}" class="input100"/>
													</xsl:when>
													<xsl:otherwise>
<input type="hidden" name="keyName" value="{@keyName}"/>
														<xsl:value-of select="@keyName"/>
													</xsl:otherwise>
												</xsl:choose>
												</td>
												<td><input type="text" name="keyValue" value="{@keyValue}" class="input100"/></td>
												<td align="right">
													<input type="checkbox" name="TM_remove">
													<xsl:attribute name="value"><xsl:number value="position()"/></xsl:attribute>
													</input>
												</td>
											</tr>
</xsl:for-each>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
</xsl:if>
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#ffffff">
					<tr>
						<td bgcolor="ffffff">
							<table width="690" border="0" cellpadding="0" cellspacing="0" bgcolor="#ffffff">
								<tr>
<!--Button-->
									<td valign="top" align="right">
									<xsl:choose>
										<xsl:when test="starts-with($operation,'create')">
											<input type="submit" value="Create" class="button"/>
										</xsl:when>
										<xsl:otherwise>
											<input type="submit" value="Update" class="button"/>
										</xsl:otherwise>
									</xsl:choose>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
</form>
				<p/>
				<xsl:choose>
					<xsl:when test="starts-with($TModels,'leer')"></xsl:when>
				<xsl:otherwise>
<!--list TModel-->
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#336699">
					<tr>
						<td bgcolor="ffffff">
							<table width="690" border="0" cellpadding="0" cellspacing="2" bgcolor="#ffffff">
								<tr>
									<td width="200">
<img src="images/transparent.gif" height="4"/>
</td>
									<td colspan="3" class="subheadform" align="left">Add TModel</td>
								</tr>
								<tr>
									<td width="200" height="1"><img src="images/transparent.gif" width="200" height="1"/></td>
									<td width="10"><img src="images/transparent.gif" width="10" height="1"/></td>
									<td width="460"><img src="images/transparent.gif" width="10" height="1"/></td>
									<td width="20"><img src="images/transparent.gif" width="10" height="1"/></td>
								</tr>
<form action="be_addTModel" method="post">
<input type="hidden" name="inquiryURL" value="{//session/uddi/inquiryURL}"/>
<input type="hidden" name="businessKey" value="{//uddi:businessEntity/@businessKey}"/>
								<tr>
									<td width="200">Search</td>
									<td width="10" align="right"><img src="images/transparent.gif" width="10"/></td>
									<td width="460"><input type="text" name="query" value="%" class="input200"/></td>
									<td width="20"><input type="image" value="Search" alt="Search TModel" src="images/bu_search.gif"/></td>
								</tr>
</form>
							</table>
						</td>
					</tr>
				</table>
				</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</table>
</body>
</html>
</xsl:template>

<xsl:template match="uddi:tModelInfo">
	<xsl:choose>
		<xsl:when test="position() mod 2=1">
			<tr bgcolor="#EBF7FF">
				<td width="205" valign="top"><xsl:value-of select="uddi:name"/></td>
				<td width="465" valign="top"><xsl:value-of select="@tModelKey"/></td>
				<td width="20" valign="top"><input type="image" src="images/bu_remove.gif" alt="Remove TModel"/></td>
			</tr>
		</xsl:when>
		<xsl:otherwise>
			<tr>
				<td width="205" valign="top">	<xsl:value-of select="uddi:name"/></td>
				<td width="465" valign="top"><xsl:value-of select="@tModelKey"/></td>
				<td width="20" valign="top"><input type="image" src="images/bu_remove.gif" alt="Remove TModel"/></td>
			</tr>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="//soapenv:Envelope//uddi:categoryBag/uddi:keyedReference">
	<xsl:choose>
		<xsl:when test="position() mod 2=1">
			<tr bgcolor="#EBF7FF">
<input type="hidden" name="tModelKey" value="{@tModelKey}"/>
				<td width="200">
				<xsl:choose>
					<xsl:when test="starts-with(@tModelKey,'uuid:A035A07C-F362-44DD-8F95-E2B134BF43B4')">
						<xsl:value-of select="@keyName"/>
					</xsl:when>
					<xsl:otherwise>
<input type="hidden" name="keyName" value="{@keyName}"/>
						<xsl:value-of select="@keyName"/>
					</xsl:otherwise>
				</xsl:choose>
				</td>
				<td><xsl:value-of select="@keyValue"/></td>
				<td align="right" bgcolor="#ffffff"><input type="image" name="TM_remove" src="images/bu_remove.gif" value="{@keyValue}"/></td>
			</tr>
		</xsl:when>
		<xsl:otherwise>
			<tr>
<input type="hidden" name="tModelKey" value="{@tModelKey}"/>
				<td width="200">
				<xsl:choose>
					<xsl:when test="starts-with(@tModelKey,'uuid:A035A07C-F362-44DD-8F95-E2B134BF43B4')">
						<xsl:value-of select="@keyName"/>
					</xsl:when>
					<xsl:otherwise>
<input type="hidden" name="keyName" value="{@keyName}"/>
						<xsl:value-of select="@keyName"/>
					</xsl:otherwise>
				</xsl:choose>
				</td>
				<td><xsl:value-of select="@keyValue"/></td>
				<td align="right"><input type="checkbox" name="TM_remove" value="{@keyValue}"/></td>
			</tr>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
