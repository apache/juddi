#!/bin/sh
KEYSTORE=src/main/keystore/signing-jar.keystore
keytool -genkey -alias applet -keystore $KEYSTORE -storepass applet -keypass applet -dname "CN=developer, OU=group 3, O=com.example, L=Somewhere, ST=Germany, C=DE"
keytool -selfcert -alias applet -keystore $KEYSTORE -storepass applet -keypass applet
