<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:uddi="urn:uddi-org:api_v2">

<xsl:template match="text()|@*"/>
<xsl:template match="/">
<html>
<head>
	<title>Business Entity Search</title>
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
<form action="be_searchresult" method="POST">
<input type="hidden" name="inquiryURL" value="{//inquiryURL}" />
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#336699">
					<tr>
						<td bgcolor="ffffff"><img src="images/transparent.gif" height="4"/><br/>
<!-- Headline-->
	          					<span class="headline">Business Entity Search<p/></span>
	           					<table width="690" border="0" cellpadding="4" cellspacing="1" bgcolor="#ffffff">
	  							<tr>
									<td width="200" valign="top">Name</td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top"><input type="text" name="query_name" value="%" class="input200"/></td>
								</tr>
								<tr>
									<td width="200" valign="top"><img src="images/transparent.gif" width="10"/></td>
									<td width="10"><img src="images/transparent.gif" width="10"/></td>
									<td width="480" valign="top">
										<select name="findQualifier" class="select40">
											<option value="orAllKeys" selected="">OR</option>
											<option value="andAllKeys">AND</option>
										</select>
									</td>
								</tr>
<xsl:if test="//session/businessEntitySearch/categoryBag/keyedReference">
	  							<tr>
									<td valign="top">Selected Categories</td>
								</tr>
								<tr>
									<td>
										<table width="100%" border="0" cellpadding="0" cellspacing="2" bgcolor="#ffffff">
											<tr>
												<td width="205" valign="top"><u>Name</u></td>
												<td width="465" valign="top"><u>Value</u></td>
												<td width="20" valign="top"></td>
											</tr>
<xsl:apply-templates select="//session/businessEntitySearch/categoryBag/keyedReference"/>
										</table>
									</td>
								</tr>
</xsl:if>
							</table>
						</td>
					</tr>
				</table>
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#ffffff">
					<tr>
						<td bgcolor="ffffff"><img src="images/transparent.gif" height="4"/><br/>
							<table width="690" border="0" cellpadding="0" cellspacing="0" bgcolor="#ffffff">
								<tr>
<!--Button-->
									<td valign="top" align="right"><input type="submit" value="Search" class="button"/></td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
</form>
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#336699">
					<tr>
						<td bgcolor="ffffff">
							<table width="690" border="0" cellpadding="0" cellspacing="2" bgcolor="#ffffff">
								<tr>
									<td width="200"><img src="images/transparent.gif" height="4"/></td>
									<td colspan="3" class="subheadform" align="left">Add TModel</td>
								</tr>
								<tr>
									<td width="200" height="1"><img src="images/transparent.gif" width="200" height="1"/></td>
									<td width="10"><img src="images/transparent.gif" width="10" height="1"/></td>
									<td width="460"><img src="images/transparent.gif" width="10" height="1"/></td>
									<td width="20"><img src="images/transparent.gif" width="10" height="1"/></td>
								</tr>
<form action="be_selectTModel" method="post">
<input type="hidden" name="inquiryURL" value="{//session/uddi/inquiryURL}"/>
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
			</td>
		</tr>
	</table>
</body>
</html>
</xsl:template>

<xsl:template match="//session//keyedReference">
	<tr>
<input type="hidden" name="tModelKey" value="{@tModelKey}"/>
<input type="hidden" name="keyValue" value="{@keyValue}"/>
<input type="hidden" name="keyName" value="{@keyName}"/>
		<td width="205" valign="top"><xsl:value-of select="@keyName"/></td>
		<td width="465" valign="top"><xsl:value-of select="@keyValue"/></td>
		<td width="20" valign="top"><img src="images/transparent.gif" width="1"/></td>
	</tr>
</xsl:template>

</xsl:stylesheet>