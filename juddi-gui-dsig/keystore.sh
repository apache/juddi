#!/bin/sh
KEYSTORE=src/main/keystore/signing-jar.keystore
keytool -genkey -alias applet -keystore $KEYSTORE -storepass applet -keypass applet -dname "CN=developer, OU=group 3, O=org.apache.juddi, L=Boston, ST=United States of America, C=USA"
keytool -selfcert -alias applet -keystore $KEYSTORE -storepass applet -keypass applet
