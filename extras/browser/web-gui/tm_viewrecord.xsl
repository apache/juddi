<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:uddi="urn:uddi-org:api_v2"
	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">

<xsl:template match="text()|@*"/>
<xsl:template match="/">
<html>
<head>
	<title>TModel View Record</title>
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
			<td valign="top" align="left" class="status"><xsl:value-of select="//uddi:name"/></td>
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
	          					<span class="headline">TModel View Record<p/></span>
	           					<table width="690" border="0" cellpadding="4" cellspacing="1" bgcolor="#ffffff">
								<tr>
									<td width="200" valign="top">TModel Name</td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top" bgcolor="#eeeeee"><xsl:value-of select="//uddi:name"/></td>
								</tr>
								<tr>
									<td width="200" valign="top">TModel Description</td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top" bgcolor="#eeeeee"><xsl:value-of select=".//uddi:description"/></td>
								</tr>
								<tr>
									<td width="200" valign="top">OverviewDoc URL</td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top" bgcolor="#eeeeee"><a href="{//uddi:overviewURL}"><xsl:value-of select="//uddi:overviewURL"/></a></td>
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
							<table width="690" border="0" cellpadding="4" cellspacing="1" bgcolor="#ffffff">
								<tr>
									<td><img src="images/transparent.gif" height="4"/></td>
									<td class="subheadform" colspan="2">Category Information</td>
								</tr>
								<tr>
									<td colspan="3">
										<table width="682" border="0" cellpadding="4" cellspacing="1" bgcolor="#ffffff">
											<tr>
												<td align="left" valign="top" width="300"><u>Key Name</u></td>
												<td align="left" valign="top"><u>Key Value</u></td>
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
<form action="tm_edit" method="POST">
<input type="hidden" name="inquiryURL" value="{//session/uddi/inquiryURL}" />
<input type="hidden" name="tModelKey" value="{.//uddi:tModel/@tModelKey}" />
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
				<p/>
			</td>
		</tr>
	</table>
</body>
</html>
</xsl:template>

<xsl:template match="uddi:tModelDetail">
	<xsl:choose>
		<xsl:when test="position() mod 2=1">
			<xsl:for-each select="*">
				<xsl:for-each select=".//uddi:keyedReference">
					<tr bgcolor="#EBF7FF">
						<td valign="top"><xsl:value-of select="@keyName"/></td>
						<td valign="top"><xsl:value-of select="@keyValue"/></td>
					</tr>
				</xsl:for-each>
			</xsl:for-each>
		</xsl:when>
		<xsl:otherwise>
			<xsl:for-each select="*">
				<xsl:for-each select=".//uddi:keyedReference">
					<tr>
						<td valign="top"><xsl:value-of select="@keyName"/></td>
						<td valign="top"><xsl:value-of select="@keyValue"/></td>
					</tr>
				</xsl:for-each>
			</xsl:for-each>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>