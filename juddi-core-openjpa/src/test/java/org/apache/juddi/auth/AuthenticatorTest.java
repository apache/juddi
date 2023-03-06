/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.auth;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidKeyException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.impl.API_010_PublisherTest;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.v3.client.cryptor.AES128Cryptor;
import org.apache.juddi.v3.client.cryptor.AES256Cryptor;
import org.apache.juddi.v3.client.cryptor.Cryptor;
import org.apache.juddi.cryptor.CryptorFactory;
import org.apache.juddi.v3.client.cryptor.DefaultCryptor;
import org.apache.juddi.v3.client.cryptor.TripleDESCrytor;
import org.apache.juddi.v3.auth.Authenticator;
import org.apache.juddi.v3.auth.CryptedXMLDocAuthenticator;
import org.apache.juddi.v3.auth.JUDDIAuthenticator;
import org.apache.juddi.v3.auth.JuddiUsers;
import org.apache.juddi.v3.auth.MD5XMLDocAuthenticator;
import org.apache.juddi.v3.auth.User;
import org.apache.juddi.v3.auth.XMLDocAuthenticator;
import org.apache.juddi.v3.error.AuthenticationException;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.UnknownUserException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class AuthenticatorTest 
{
	private Log logger = LogFactory.getLog(this.getClass());
	/**
	 * The DefaultAuthenticator is basically a pass-through.
	 * @throws ConfigurationException
	 */
	@Test
	public void testDefaultAuthenticator()
	{
            System.out.println("testDefaultAuthenticator");
		Authenticator auth = new JUDDIAuthenticator();
		try {
			API_010_PublisherTest api010 = new API_010_PublisherTest();
			api010.saveJoePublisher();
			api010.saveSamSyndicator();

			auth.authenticate("joepublisher","password");
			auth.authenticate("ssyndicator","badpass");
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
	}
	@Test
	public void testCreateJuddiUsers() throws Exception
	{
            System.out.println("testCreateJuddiUsers");
		try {
			JuddiUsers juddiUsers = new JuddiUsers();
			juddiUsers.getUser().add(new User("anou_mana","password"));
			juddiUsers.getUser().add(new User("bozo","clown"));
			juddiUsers.getUser().add(new User("sviens","password"));
			
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(juddiUsers.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(juddiUsers, writer);
			logger.info("\n" +  writer.toString());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
	}
	/**
	 * The credentials will be read from the juddi-users.xml in the test/resources directory 
	 * of this module. We're expecting the following authentication requests to succeed.
	 */
	@Test
	public void testXMLDocAuthenticator() 
	{
            System.out.println("testXMLDocAuthenticator");
		try {
			Authenticator auth = new XMLDocAuthenticator();
			auth.authenticate("anou_mana","password");
			auth.authenticate("bozo","clown");
			auth.authenticate("sviens","password");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
	}
	/**
	 * The credentials will be read from the juddi-users.xml in the test/resources directory 
	 * of this module. We're expecting the following authentication request to fail due
	 * to a bad password.
	 * 
	 * @throws AuthenticationException
	 * @throws FatalErrorException
	 * @throws IOException 
	 * @throws JAXBException 
	 */
	@Test(expected=UnknownUserException.class) 
	public void testBadXMLDocAuthenticator() throws Exception
	{
            System.out.println("testBadXMLDocAuthenticator");
		Authenticator auth = new XMLDocAuthenticator();
		auth.authenticate("anou_mana","badpass");
	}
	@Test
	public void testCreateJuddiUsersEncrypted() throws Exception
	{
            System.out.println("testCreateJuddiUsersEncrypted");
		try {
			Cryptor cryptor = CryptorFactory.getCryptor();
			JuddiUsers juddiUsers = new JuddiUsers();
			juddiUsers.getUser().add(new User("anou_mana",cryptor.encrypt("password")));
			juddiUsers.getUser().add(new User("bozo",cryptor.encrypt("clown")));
			juddiUsers.getUser().add(new User("sviens",cryptor.encrypt("password")));
			
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(juddiUsers.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(juddiUsers, writer);
			logger.info("\n" +  writer.toString());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
	}
	/**
	 * The credentials will be read from the juddi-users.xml in the test/resources directory 
	 * of this module. We're expecting the following authentication requests to succeed.
	 */
	@Test
	public void testCryptedXMLDocAuthenticator() 
	{
            System.out.println("testCryptedXMLDocAuthenticator");
		try {
			Authenticator auth = new CryptedXMLDocAuthenticator();
			auth.authenticate("anou_mana","password");
			auth.authenticate("bozo","clown");
			auth.authenticate("sviens","password");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
	}
	/**
	 * The credentials will be read from the juddi-users-encrypted.xml in the test/resources directory 
	 * of this module. We're expecting the following authentication request to fail due
	 * to a bad password.
	 * 
	 * @throws AuthenticationException
	 * @throws FatalErrorException
	 * @throws IOException 
	 * @throws JAXBException 
	 */
	@Test(expected=UnknownUserException.class) 
	public void testBadCryptedXMLDocAuthenticator() throws Exception
                
	{
            System.out.println("testBadCryptedXMLDocAuthenticator");
 		Authenticator auth = new CryptedXMLDocAuthenticator();
		auth.authenticate("anou_mana","badpass");
	}
        
        
        @Test
	public void testMD5XMLDocAuthenticator() 
	{
            System.out.println("testMD5XMLDocAuthenticator");
		try {
			Authenticator auth = new MD5XMLDocAuthenticator();
			auth.authenticate("anou_mana","password");
			auth.authenticate("bozo","clown");
			auth.authenticate("sviens","password");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
	}
        
        
        
        @Test(expected=UnknownUserException.class) 
	public void testBadMD5XMLDocAuthenticator() throws Exception
	{
		Authenticator auth = new MD5XMLDocAuthenticator();
		auth.authenticate("anou_mana","badpass");
	}
        
        
        @Test
	public void testAES128Cryptor() 
	{
            System.out.println("testAES128Cryptor");
		try {
			Cryptor auth = new AES128Cryptor();
                        String encrypt = auth.encrypt("test");
                        Assert.assertNotNull(encrypt);
                        Assert.assertNotSame(encrypt, "test");
                        String test=auth.decrypt(encrypt);
                        Assert.assertEquals(test, "test");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
	}
        
        
        @Test
	public void testTripleDESCryptor() 
	{
            System.out.println("testTripleDESCryptor");
		try {
			Cryptor auth = new TripleDESCrytor();
                        String encrypt = auth.encrypt("test");
                        Assert.assertNotNull(encrypt);
                        Assert.assertNotSame(encrypt, "test");
                        String test=auth.decrypt(encrypt);
                        Assert.assertEquals(test, "test");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
	}
        
        
        @Test
	public void testDefaultCryptor() 
	{
            System.out.println("testDefaultCryptor");
		try {
			Cryptor auth = new DefaultCryptor();
                        String encrypt = auth.encrypt("test");
                        Assert.assertNotNull(encrypt);
                        Assert.assertNotSame(encrypt, "test");
                        String test=auth.decrypt(encrypt);
                        Assert.assertEquals(test, "test");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
	}
        
        
        @Test
	public void testAES256Cryptor() 
	{
                System.out.println("testAES256Cryptor");
		try {
			Cryptor auth = new AES256Cryptor();
                        String encrypt = auth.encrypt("test");
                        Assert.assertNotNull(encrypt);
                        Assert.assertNotSame(encrypt, "test");
                        String test=auth.decrypt(encrypt);
                        Assert.assertEquals(test, "test");
                }
                catch (InvalidKeyException e)
                {
                    logger.error("Hey, you're probably using the Oracle JRE without the Unlimited Strength Java Crypto Extensions installed. AES256 won't work until you download and install it", e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
	}
        
         @Test
	public void testDecryptFromConfigXML_InMemory() 
	{
                System.out.println("testDecryptFromConfigXML_InMemory");
		try {
                    Configuration config =AppConfig.getConfiguration();
                    
			Cryptor auth = new AES128Cryptor();
                        String encrypt = auth.encrypt("test");
                        Assert.assertNotNull(encrypt);
                        Assert.assertNotSame(encrypt, "test");
                        
                        //add to the config
                        config.addProperty("testDecryptFromConfigXML", encrypt);
                        config.addProperty("testDecryptFromConfigXML"+ Property.ENCRYPTED_ATTRIBUTE, "true");
                        
                        //retrieve it
                        String pwd = config.getString("testDecryptFromConfigXML");
                        Assert.assertNotNull(pwd);
                        //test for encryption
                        if (config.getBoolean("testDecryptFromConfigXML" + Property.ENCRYPTED_ATTRIBUTE, false))
                        {
                            String test=auth.decrypt(pwd);
                            Assert.assertEquals(test, "test");
                        }
                        else
                        {
                            Assert.fail("config reports that the setting is not encrypted");
                       }
                }
                catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
	}
         
        @Test
	public void testDecryptFromConfigXML_Disk_Default() 
	{
                System.out.println("testDecryptFromConfigXML_Disk_Default");
		try {
                    File f = new File(".");
                    System.out.println("Current working dir is " + f.getAbsolutePath());
                    System.setProperty(AppConfig.JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY,f.getAbsolutePath() + "/src/test/resources/juddiv3-enc-default.xml");
                    AppConfig.reloadConfig();
                    Configuration config =AppConfig.getConfiguration();
                    
			Cryptor auth = new DefaultCryptor();
                        
                        //retrieve it
                        String pwd = config.getString("juddi.mail.smtp.password");
                        Assert.assertNotNull(pwd);
                        //test for encryption
                        if (config.getBoolean("juddi.mail.smtp.password" + Property.ENCRYPTED_ATTRIBUTE, false))
                        {
                            String test=auth.decrypt(pwd);
                            Assert.assertEquals(test, "password");
                        }
                        else
                        {
                            Assert.fail("config reports that the setting is not encrypted");
                       }
                }
                catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
	}
        
        
        @Test
	public void testDecryptFromConfigXML_Disk_3DES() 
	{
                System.out.println("testDecryptFromConfigXML_Disk_3DES");
		try {
                    File f = new File(".");
                    System.out.println("Current working dir is " + f.getAbsolutePath());
                    System.setProperty(AppConfig.JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY, f.getAbsolutePath() +"/src/test/resources/juddiv3-enc-3des.xml");
                    AppConfig.reloadConfig();
                    Configuration config =AppConfig.getConfiguration();
                    
			Cryptor auth = new TripleDESCrytor();
                        
                        //retrieve it
                        String pwd = config.getString("juddi.mail.smtp.password");
                        Assert.assertNotNull(pwd);
                        //test for encryption
                        if (config.getBoolean("juddi.mail.smtp.password" + Property.ENCRYPTED_ATTRIBUTE, false))
                        {
                            String test=auth.decrypt(pwd);
                            Assert.assertEquals(test, "password");
                        }
                        else
                        {
                            Assert.fail("config reports that the setting is not encrypted");
                       }
                }
                catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
	}
        
        
         @Test
	public void testDecryptFromConfigXML_Disk_AES128() 
	{
                System.out.println("testDecryptFromConfigXML_Disk_AES128");
		try {
                    File f = new File(".");
                    System.out.println("Current working dir is " + f.getAbsolutePath());
                    
                    System.setProperty(AppConfig.JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY, f.getAbsolutePath() +"/src/test/resources/juddiv3-enc-aes128.xml");
                    AppConfig.reloadConfig();
                    Configuration config =AppConfig.getConfiguration();
                    
			Cryptor auth = new AES128Cryptor();
                        
                        //retrieve it
                        String pwd = config.getString("juddi.mail.smtp.password");
                        Assert.assertNotNull(pwd);
                        //test for encryption
                        if (config.getBoolean("juddi.mail.smtp.password" + Property.ENCRYPTED_ATTRIBUTE, false))
                        {
                            String test=auth.decrypt(pwd);
                            Assert.assertEquals(test, "password");
                        }
                        else
                        {
                            Assert.fail("config reports that the setting is not encrypted");
                       }
                }
                catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
	}
         
         
         
         @Test
	public void testDecryptFromConfigXML_Disk_AES256() 
	{
                System.out.println("testDecryptFromConfigXML_Disk_AES256");
		try {
                    File f = new File(".");
                    System.out.println("Current working dir is " + f.getAbsolutePath());
                    System.setProperty(AppConfig.JUDDI_CONFIGURATION_FILE_SYSTEM_PROPERTY, f.getAbsolutePath() + "/src/test/resources/juddiv3-enc-aes256.xml");
                    AppConfig.reloadConfig();
                    Configuration config =AppConfig.getConfiguration();
                    
			Cryptor auth = new AES256Cryptor();
                        
                        //retrieve it
                        String pwd = config.getString("juddi.mail.smtp.password");
                        Assert.assertNotNull(pwd);
                        //test for encryption
                        if (config.getBoolean("juddi.mail.smtp.password" + Property.ENCRYPTED_ATTRIBUTE, false))
                        {
                            String test=auth.decrypt(pwd);
                            Assert.assertEquals(test, "password");
                        }
                        else
                        {
                            Assert.fail("config reports that the setting is not encrypted");
                       }
                } catch (InvalidKeyException e)
                {
                    logger.error("Hey, you're probably using the Oracle JRE without the Unlimited Strength Java Crypto Extensions installed. AES256 won't work until you download and install it", e);
		}
                catch (Exception e) {
			logger.error(e.getMessage(),e);
			Assert.fail("unexpected");
		}
	}

}
