<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:fo="http://www.w3.org/1999/XSL/Format" 
	xmlns:uddi="urn:uddi-org:api_v2" 
	xmlns:cinclude="http://apache.org/cocoon/include/1.0" 
	xmlns:session="http://apache.org/cocoon/session/1.0">

<xsl:template match="/">
	<authInfo><xsl:copy-of select="//uddi:authInfo/text()"/></authInfo>
	<content>
	<cinclude:includexml>
		<cinclude:src>cocoon:/<xsl:value-of select="//pipeline/text()"/>_content</cinclude:src>
		<cinclude:configuration>
			<cinclude:parameter>
				<cinclude:name>method</cinclude:name>
				<cinclude:value>POST</cinclude:value>
			</cinclude:parameter>
		</cinclude:configuration>
		<cinclude:parameters>
<!--xsl:for-each select="//cinclude:parameters/cinclude:parameter">
<xsl:copy-of select="."/>
</xsl:for-each-->
			<cinclude:parameter>
				<cinclude:name>authInfo</cinclude:name>
				<cinclude:value><xsl:copy-of select="//uddi:authInfo/text()"/></cinclude:value>
			</cinclude:parameter>
		</cinclude:parameters>
	</cinclude:includexml>
	<xsl:copy-of select="//session"/>
	</content>
</xsl:template>

</xsl:stylesheet>