<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:uddi="urn:uddi-org:api_v2"
	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">

<xsl:param name="operation">create</xsl:param>
<xsl:param name="headline">TModel Input</xsl:param>
<xsl:param name="TModels">leer</xsl:param>

<xsl:template match="text()|@*"/>
<xsl:template match="/">
<html>
<head>
	<title>TModel Input</title>
	<link href="styles.css" type="text/css" rel="stylesheet"/>
	<SCRIPT LANGUAGE="JavaScript">
		function DoCheck(){
			if(document.tm_input.keyName.value == ""){
       	 		alert("Please fill in the TModel Name!");
        			document.tm_input.keyName.focus();
			return false;
			}
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
			<td valign="top" align="left" class="status"><xsl:value-of select="//session/uddi/name"/></td>
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
<form action="tm_publish" method="post" name="tm_input" OnSubmit="return DoCheck();">
<input type="hidden" name="publishURL" value="{//session/uddi/publishURL}" />
<input type="hidden" name="tModelKey" value="{.//uddi:tModel/@tModelKey}" />
<input type="hidden" name="pipeline" value="tm_publish" />
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#336699">
					<tr>
						<td bgcolor="ffffff"><img src="images/transparent.gif" height="4"/><br/>
<!-- Headline-->
	          					<span class="headline">
								<xsl:choose>
									<xsl:when test="starts-with($headline,'TModel Input')">TModel Input</xsl:when>
									<xsl:otherwise>TModel Edit</xsl:otherwise>
								</xsl:choose><p/>
	          					</span>
	           					<table width="690" border="0" cellpadding="4" cellspacing="1" bgcolor="#ffffff">
	  							<tr>
									<td width="200" valign="top">TModel Name</td>
									<td width="10" align="left" class="required">* </td>
									<td width="480" valign="top">	<input name="keyName" value="{//uddi:name}" class="input200"/></td>
								</tr>
								<tr>
									<td width="200" valign="top">TModel Description</td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top">	<input name="keyDesc" value="{//uddi:description}" class="input200"/></td>
								</tr>
								<tr>
									<td width="200" valign="top">OverviewDoc URL</td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top">	<input name="url" value="{//uddi:overviewURL}" class="input200"/></td>
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
<!--Category Information-->
<xsl:if test="//soapenv:Envelope//uddi:categoryBag/uddi:keyedReference">
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#336699">
					<tr>
						<td bgcolor="ffffff"><img src="images/transparent.gif" height="4"/><br/>
							<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#ffffff">
								<tr>
									<td><img src="images/transparent.gif" width="10" height="4"/></td>
									<td class="subheadform">Category Information</td>
								</tr>
								<tr>
									<td colspan="2">
										<table width="680" border="0" cellpadding="0" cellspacing="1" bgcolor="#ffffff">
											<tr>
												<td align="left" valign="top" width="200"><u>Key Name</u></td>
												<td align="left" valign="top" width="120"><u>Key Value</u></td>
												<td align="right" valign="top" class="input">remove</td>
											</tr>
<xsl:apply-templates/>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
</xsl:if>
				<table width="690" border="0" cellpadding="0" cellspacing="0" bgcolor="#ffffff">
					<tr>
						<td bgcolor="ffffff"><img src="images/transparent.gif" height="4"/><br/>
							<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#ffffff">
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
<form action="tm_addTModel" method="post">
<input type="hidden" name="inquiryURL" value="{//session/uddi/inquiryURL}"/>
<input type="hidden" name="tModelKey" value="{.//uddi:tModel/@tModelKey}"/>
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
				<p/>
				<a href="javascript:history.back();">back</a>
				<p/>
			</td>
		</tr>
	</table>
</body>
</html>
</xsl:template>

<xsl:template match="//soapenv:Envelope//uddi:categoryBag/uddi:keyedReference">
	<xsl:choose>
		<xsl:when test="position() mod 2=1">
			<tr>
<input type="hidden" name="catTModelKey" value="{@tModelKey}"/>
				<td width="200">
					<xsl:choose>
						<xsl:when test="starts-with(@tModelKey,'uuid:A035A07C-F362-44DD-8F95-E2B134BF43B4')">
							<input type="text" name="catKeyName" value="{@keyName}" class="input100"/>
						</xsl:when>
						<xsl:otherwise>
<input type="hidden" name="catKeyName" value="{@keyName}"/>
							<xsl:value-of select="@keyName"/>
						</xsl:otherwise>
					</xsl:choose>
				</td>
				<td><input type="text" name="catKeyValue" value="{@keyValue}" class="input100"/></td>
				<td align="right">
					<input type="checkbox" name="TM_remove">
						<xsl:attribute name="value"><xsl:number value="position()"/></xsl:attribute>
					</input>
				</td>
			</tr>
		</xsl:when>
		<xsl:otherwise>
			<tr>
<input type="hidden" name="catTModelKey" value="{@tModelKey}"/>
				<td width="200">
					<xsl:choose>
						<xsl:when test="starts-with(@tModelKey,'uuid:A035A07C-F362-44DD-8F95-E2B134BF43B4')">
							<input type="text" name="catKeyName" value="{@keyName}" class="input100"/>
						</xsl:when>
						<xsl:otherwise>
<input type="hidden" name="catKeyName" value="{@keyName}"/>
							<xsl:value-of select="@keyName"/>
						</xsl:otherwise>
					</xsl:choose>
				</td>
				<td><input type="text" name="catKeyValue" value="{@keyValue}" class="input100"/></td>
				<td align="right">
					<input type="checkbox" name="TM_remove">
						<xsl:attribute name="value"><xsl:number value="position()"/></xsl:attribute>
					</input>
				</td>
			</tr>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>