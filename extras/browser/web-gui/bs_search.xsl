<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:uddi="urn:uddi-org:api_v2">

<xsl:template match="text()|@*"/>
<xsl:template match="/">
<html>
<head>
	<title>Business Service Search</title>
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
<form action="bs_searchresult">
<input type="hidden" name="inquiryURL" value="{//inquiryURL}"/>
				<table width="690" border="0" cellpadding="0" cellspacing="1" bgcolor="#336699">
					<tr>
						<td bgcolor="ffffff"><img src="images/transparent.gif" height="4"/><br/>
<!-- Headline-->
	          					<span class="headline">BusinessService Search<p/></span>
	           					<table width="690" border="0" cellpadding="4" cellspacing="1" bgcolor="#ffffff">
								<tr>
									<td width="200" valign="top">Business Service Name</td>
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
<xsl:if test="//session/businessServiceSearch/categoryBag/keyedReference">
								<tr>
									<td colspan="3">
										<table width="686" border="0" cellpadding="0" cellspacing="2" bgcolor="#ffffff">
											<tr>
												<td width="400" valign="top"><u>Name</u></td>
												<td width="286" valign="top"><u>Value</u></td>
											</tr>
<xsl:apply-templates select="//session/businessServiceSearch/categoryBag/keyedReference"/>
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
<!--END Form-->
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
<form action="bs_selectTModel" method="post">
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

<xsl:template match="//session/businessServiceSearch/categoryBag/keyedReference">
	<xsl:choose>
		<xsl:when test="position() mod 2=1">
			<tr bgcolor="#EBF7FF">
<input type="hidden" name="tModelKey" value="{@tModelKey}"/>
<input type="hidden" name="keyValue" value="{@keyValue}"/>
<input type="hidden" name="keyName" value="{@keyName}"/>
				<td valign="top"><xsl:value-of select="@keyName"/></td>
				<td valign="top"><xsl:value-of select="@keyValue"/></td>
			</tr>
		</xsl:when>
		<xsl:otherwise>
			<tr bgcolor="#ffffff">
<input type="hidden" name="tModelKey" value="{@tModelKey}"/>
<input type="hidden" name="keyValue" value="{@keyValue}"/>
<input type="hidden" name="keyName" value="{@keyName}"/>
				<td valign="top"><xsl:value-of select="@keyName"/></td>
				<td valign="top"><xsl:value-of select="@keyValue"/></td>
			</tr>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>