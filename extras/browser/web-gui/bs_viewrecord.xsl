<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:uddi="urn:uddi-org:api_v2"
	xmlns:session="http://apache.org/cocoon/session/1.0"
	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">

<xsl:template match="text()|@*"/>
<xsl:template match="/">
<html>
	<session:mergexml context="mycontext" path="/session/">
		<businessService>
			<name><xsl:value-of select="//uddi:businessService/uddi:name"/></name>
			<serviceKey><xsl:value-of select="//uddi:businessService/@serviceKey"/></serviceKey>
		</businessService>
	</session:mergexml>
<head>
	<title>Business Service viewRecord</title>
	<link href="styles.css" type="text/css" rel="stylesheet"/>
</head>
<body leftmargin="0" topmargin="0" marginheight="0" marginwidth="0">
<!--session:getxml context="mycontext" path="/"/-->
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
			<td valign="top" align="left" class="status"><xsl:value-of select="//session/uddi/name"/>
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
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#336699">
					<tr>
						<td bgcolor="ffffff"><img src="images/transparent.gif" height="4"/><br/>
<!-- Headline-->
	          					<span class="headline">Business Service viewRecord<p/></span>
	           					<table width="690" border="0" cellpadding="4" cellspacing="1" bgcolor="#ffffff">
	  							<tr>
									<td width="200" valign="top">Business Service Name</td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top" bgcolor="#eeeeee" class="td"><xsl:value-of select="//uddi:businessService/uddi:name"/></td>
								</tr>
								<tr>
									<td width="200" valign="top">Business Service Description</td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top" bgcolor="#eeeeee" class="td"><xsl:value-of select="//uddi:businessService/uddi:description"/></td>
								</tr>
								<tr>
									<td width="200" valign="top">Access Point</td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top" bgcolor="#eeeeee" class="td"><xsl:value-of select="//uddi:accessPoint"/></td>
								</tr>
								<tr>
									<td width="200" valign="top">URL Type</td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top" bgcolor="#eeeeee" class="td"><xsl:value-of select="//uddi:accessPoint/@URLType"/></td>
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
							<table width="688" border="0" cellpadding="0" cellspacing="1" bgcolor="#ffffff">
								<tr>
									<td width="200"><img src="images/transparent.gif" height="4" width="200"/></td>
									<td colspan="3" class="subheadform">TModels</td>
								</tr>
								<tr>
									<td width="200" height="1"><img src="images/transparent.gif" width="200" height="1"/></td>
									<td width="10"><img src="images/transparent.gif" width="10" height="1"/></td>
									<td width="458"><img src="images/transparent.gif" width="10" height="1"/></td>
									<td width="20"><img src="images/transparent.gif" width="20" height="1"/></td>
								</tr>
								<tr bgcolor="#EBF7FF">
									<td colspan="4">
										<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#ffffff">
											<tr>
												<td width="400" valign="top"><u>Name</u></td>
												<td width="270" valign="top"><u>Value</u>	</td>
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
				<p/>
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#ffffff">
					<tr>
						<td bgcolor="ffffff">
							<table width="690" border="0" cellpadding="0" cellspacing="2" bgcolor="#ffffff">
								<tr>
<!--Button-->
									<td valign="top" align="right">
<form action="bs_edit" method="POST">
<input type="hidden" name="inquiryURL" value="{//session/uddi/inquiryURL}" />
<input type="hidden" name="serviceKey" value="{//uddi:businessService/@serviceKey}" />
										<input type="submit" value="Edit" class="button"/>
</form>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
				<p/>
				<a href="javascript:history.back();">back</a>
				<br/>
			</td>
		</tr>
	</table>
</body>
</html>
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
			</tr>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>