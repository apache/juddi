<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:uddi="urn:uddi-org:api_v2">
<xsl:template match="page">
<html>
<head>
	<title>Start UDDI</title>
	<link href="styles.css" type="text/css" rel="stylesheet"/>
</head>
<body leftmargin="0" topmargin="0" marginheight="0" marginwidth="0">
	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><img src="images/transparent.gif" width="170" height="105" border="0"/></td>
			<td>
<!--EMBED header-->
<xsl:copy-of select="header/*"/>
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
<xsl:copy-of select="nav/*"/>
						</td>
					</tr>
				</table>
			</td>
			<td valign="top" align="left">
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#336699">
					<tr>
						<td bgcolor="ffffff"><img src="images/transparent.gif" height="4"/><br/>
<!-- Headline-->
							<span class="headline">Start UDDI<p/></span>
							<table border="0" cellpadding="4" cellspacing="1" bgcolor="#ffffff">
								<tr>
									<td>
									<p>
									The available web interface is a limited UDDI client implemented using Apache Cocoon. The interface has been designed to ease the use for UDDI novice users. In particular, the number of input fields of the Business Entity, Business Service, and tModel has been limited as much as possible. Further, the assignment of WSDL documents to a Business Service is treated slightly different to the specification by adding it as a tModel to the categoryBag of the service.</p>
									<p>
									Please feel free to test the interface and report comments to
									<i>andreas.wombach AT computer.org</i>.</p>
									</td>
								</tr>
								<tr>
									<td><img src="images/transparent.gif" height="1"/></td>
								</tr>
								<tr>
									<td>
									<p>The options provided by this interface are the following:
										<ul>
											<li>Sepciy UDDI: here to can specify the information required to access the UDDI via Web Services Interface</li>
											<li>Business Entity: here a new Business entity can be created or an already existing one can be found and afterwards can be updated or can be used as a basis for inserting Business Services</li>
											<li>Business Service: without a Business Entity being specified, services can only be retrieved, otherwise they can also be edited</li>
											<li>TModel: creation of new TModels or finding TModels to be modified - also the <i>uddi-org:general_keywords</i> is supported</li>
										</ul>
										The current GUI assumes user juddi and passwort juddi as proposed by the jUDDI instaltation guide.
									</p>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</body>
</html>
</xsl:template>

</xsl:stylesheet>