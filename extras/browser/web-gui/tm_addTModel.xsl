<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:uddi="urn:uddi-org:api_v2"
	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">

<xsl:param name="addTModel">leer</xsl:param>
<xsl:param name="TModels">leer</xsl:param>
<xsl:param name="inquiryURL" select="//session/uddi/inquiryURL"/>
<xsl:param name="publishURL" select="//session/uddi/publishURL"/>
<xsl:param name="tModelKey" select="//soapenv:Body/uddi:tModelDetail/uddi:tModel/@tModelKey"/>

<xsl:template match="text()|@*"/>
<xsl:template match="/">
<html>
<head>
	<title>tModel Add Category</title>
	<link href="styles.css" type="text/css" rel="stylesheet"/>
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
<!--START content-->
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#336699">
					<tr>
						<td bgcolor="ffffff">
<!-- Headline-->
	          					<span class="headline">tModel Add Category<p/></span>
	           					<table width="690" border="0" cellpadding="4" cellspacing="1" bgcolor="#ffffff">
	           						<tr>
									<td width="200" height="1"><img src="images/transparent.gif" width="200" height="1"/></td>
									<td width="10"><img src="images/transparent.gif" width="10" height="1"/></td>
									<td width="480"><img src="images/transparent.gif" width="10" height="1"/></td>
								</tr>
	  							<tr>
									<td width="200" valign="top">tModel Name</td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top" bgcolor="#eeeeee"><xsl:value-of select="//uddi:name"/></td>
								</tr>
								<tr>
									<td width="200" valign="top">Description</td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top" bgcolor="#eeeeee"><xsl:value-of select="//uddi:description"/></td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
				<p/>
<!-- Added TModels -->
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#336699">
					<tr>
						<td bgcolor="ffffff">
							<table width="690" border="0" cellpadding="0" cellspacing="2" bgcolor="#ffffff">
								<tr>
									<td><img src="images/transparent.gif" height="4"/></td>
									<td colspan="2" class="subheadform">TModels</td>
								</tr>
	           						<tr>
									<td width="200" height="1"><img src="images/transparent.gif" width="200" height="1"/></td>
									<td width="10"><img src="images/transparent.gif" width="10" height="1"/></td>
									<td width="480"><img src="images/transparent.gif" width="460" height="1"/></td>
								</tr>
								<tr>
									<td colspan="3">
										<table width="100%" border="0" cellpadding="0" cellspacing="2" bgcolor="#ffffff">
											<tr>
												<td width="205" valign="top"><u>Name</u></td>
												<td width="465" valign="top"><u>Value</u></td>
												<td width="20" valign="top"></td>
											</tr>
<xsl:apply-templates select="//uddi:tModelInfo"/>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
				<p/>
				<a href="javascript:history.back();">back</a><p/>
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
<form action="tm_publish_addTModel" method="post">
<input type="hidden" name="tModelKey" value="{$tModelKey}"/>
<input type="hidden" name="inquiryURL" value="{$inquiryURL}"/>
<input type="hidden" name="publishURL" value="{$publishURL}"/>
<input type="hidden" name="pipeline" value="tm_publish"/>
<input type="hidden" name="catTModelKey" value="{@tModelKey}"/>
				<td width="205" valign="top">
					<xsl:choose>
						<xsl:when test="starts-with(@tModelKey,'uuid:A035A07C-F362-44DD-8F95-E2B134BF43B4')">
							<input type="text" name="catKeyName" value="{uddi:name}" class="input120"/>
						</xsl:when>
						<xsl:otherwise>
							<input type="hidden" name="catKeyName" value="{uddi:name}"/>
							<xsl:value-of select="uddi:name"/>
						</xsl:otherwise>
					</xsl:choose>
				</td>
				<td width="465" valign="top"><input type="text" value="" name="catKeyValue" class="input120"/></td>
				<td width="20" valign="top"><input type="image" src="images/bu_select.gif"/></td>
</form>
			</tr>
		</xsl:when>
		<xsl:otherwise>
			<tr>
<form action="tm_publish_addTModel" method="post">
<input type="hidden" name="tModelKey" value="{$tModelKey}"/>
<input type="hidden" name="inquiryURL" value="{$inquiryURL}"/>
<input type="hidden" name="publishURL" value="{$publishURL}"/>
<input type="hidden" name="pipeline" value="tm_publish"/>
<input type="hidden" name="catTModelKey" value="{@tModelKey}"/>
				<td width="205" valign="top">
					<xsl:choose>
						<xsl:when test="starts-with(@tModelKey,'uuid:A035A07C-F362-44DD-8F95-E2B134BF43B4')">
							<input type="text" name="catKeyName" value="{uddi:name}" class="input120"/>
						</xsl:when>
						<xsl:otherwise>
							<input type="hidden" name="catKeyName" value="{uddi:name}"/>
							<xsl:value-of select="uddi:name"/>
						</xsl:otherwise>
					</xsl:choose>
				</td>
				<td width="465" valign="top"><input type="text" value="" name="catKeyValue" class="input120"/></td>
				<td width="20" valign="top"><input type="image" src="images/bu_select.gif"/></td>
</form>
			</tr>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>
</xsl:stylesheet>
