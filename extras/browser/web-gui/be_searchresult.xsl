<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:fo="http://www.w3.org/1999/XSL/Format" 
	xmlns:uddi="urn:uddi-org:api_v2">
	
<xsl:template match="text()|@*"/>
<xsl:template match="/">
<html>
<head>
	<title>Business Entity Query result</title>
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
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#336699">
					<tr>
						<td bgcolor="ffffff"><img src="images/transparent.gif" height="4"/><br/>
	          					<span class="headline">Business Entity Query result<p/></span>
	           					<table width="690" border="0" cellpadding="4" cellspacing="1" bgcolor="#ffffff">
								<tr>
	              						<td class="head" bgcolor="#999999" width="320">Name</td>
	               						<td class="head" bgcolor="#999999" width="320">Description</td>
							             <td bgcolor="#999999"><img src="images/transparent.gif" height="4"/></td>
								</tr>
<xsl:apply-templates/>
							</table>
						</td>
					</tr>
				</table>
				<br/>
					<a href="javascript:history.back();">back</a>
				<p/>
			</td>
		</tr>
	</table>
</body>
</html>
</xsl:template>

<xsl:template match="uddi:businessInfo">
	<xsl:choose>
		<xsl:when test="position() mod 2=1">
			<tr bgcolor="#EBF7FF">
<form action="be_viewrecord" method="post">
<input type="hidden" name="businessKey" value="{@businessKey}" />
<input type="hidden" name="inquiryURL" value="{//session/uddi/inquiryURL}"/>
				<td valign="top" align="left"><xsl:value-of select="uddi:name"/></td>
				<td valign="top" align="left"><xsl:value-of select="uddi:description"/></td>
				<td valign="top" align="left"><input type="image" src="images/bu_info.gif" alt="Service Infos" align="right"/></td>
</form>
			</tr>
		</xsl:when>
		<xsl:otherwise>
			<tr bgcolor="#ffffff">
<form action="be_viewrecord" method="post">
<input type="hidden" name="businessKey" value="{@businessKey}" />
<input type="hidden" name="inquiryURL" value="{//session/uddi/inquiryURL}"/>
				<td valign="top" align="left"><xsl:value-of select="uddi:name"/></td>
				<td valign="top" align="left"><xsl:value-of select="uddi:description"/></td>
				<td valign="top" align="left"><input type="image" src="images/bu_info.gif" alt="Service Infos" align="right"/></td>
</form>
			</tr>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>