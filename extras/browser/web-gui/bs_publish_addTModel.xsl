<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:uddi="urn:uddi-org:api_v2"
	xmlns:cinclude="http://apache.org/cocoon/include/1.0"
	xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">

<xsl:template match="text()|@*"/>
<xsl:template match="/">
<page>
	<xsl:copy-of select="//authInfo"/>
	<content>
		<cinclude:includexml>
			<cinclude:src>cocoon:/bs_publish_content</cinclude:src>
			<xsl:copy-of select="//content/cinclude:includexml/cinclude:configuration"/>
			<cinclude:parameters>
				<xsl:copy-of select="//content/cinclude:includexml/cinclude:parameters/cinclude:parameter"/>
				<xsl:apply-templates select="//content[soapenv:Envelope//uddi:serviceDetail]" mode="uddi"/>
			</cinclude:parameters>
		</cinclude:includexml>
		<xsl:copy-of select="//content[cinclude:includexml]/session"/>
	</content>
	<xsl:copy-of select="//nav"/>
	<xsl:copy-of select="//header"/>
</page>
</xsl:template>

<xsl:template match="content" mode="uddi">
<!--cinclude:parameter>
<cinclude:name="businessKey"/>
<cinclude:value="{//uddi:businessEntity/@businessKey}"/>
</cinclude:parameter-->
	<cinclude:parameter>
		<cinclude:name>service_name</cinclude:name>
		<cinclude:value><xsl:value-of select="//uddi:businessService/uddi:name"/></cinclude:value>
	</cinclude:parameter>
	<cinclude:parameter>
		<cinclude:name>service_desc</cinclude:name>
		<cinclude:value><xsl:value-of select="//uddi:description"/></cinclude:value>
	</cinclude:parameter>
	<cinclude:parameter>
		<cinclude:name>URL</cinclude:name>
		<cinclude:value><xsl:value-of select="//uddi:accessPoint"/></cinclude:value>
	</cinclude:parameter>
	<cinclude:parameter>
		<cinclude:name>URLType</cinclude:name>
		<cinclude:value><xsl:value-of select="//uddi:accessPoint/@URLType"/></cinclude:value>
	</cinclude:parameter>
	<xsl:apply-templates select="//uddi:categoryBag/*"/>
</xsl:template>

<xsl:template match="uddi:keyedReference">
	<cinclude:parameter>
		<cinclude:name>tModelKey</cinclude:name>
		<cinclude:value><xsl:value-of select="@tModelKey"/></cinclude:value>
	</cinclude:parameter>
	<cinclude:parameter>
		<cinclude:name>keyName</cinclude:name>
		<cinclude:value><xsl:value-of select="@keyName"/></cinclude:value>
	</cinclude:parameter>
	<cinclude:parameter>
		<cinclude:name>keyValue</cinclude:name>
		<cinclude:value><xsl:value-of select="@keyValue"/></cinclude:value>
	</cinclude:parameter>
</xsl:template>

</xsl:stylesheet>