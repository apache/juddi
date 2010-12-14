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

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api.impl.API_010_PublisherTest;
import org.apache.juddi.cryptor.Cryptor;
import org.apache.juddi.cryptor.CryptorFactory;
import org.apache.juddi.v3.auth.Authenticator;
import org.apache.juddi.v3.auth.CryptedXMLDocAuthenticator;
import org.apache.juddi.v3.auth.JUDDIAuthenticator;
import org.apache.juddi.v3.auth.JuddiUsers;
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
		Authenticator auth = new XMLDocAuthenticator();
		auth.authenticate("anou_mana","badpass");
	}
	@Test
	public void testCreateJuddiUsersEncrypted() throws Exception
	{
		try {
			Cryptor cryptor = (Cryptor) CryptorFactory.getCryptor();
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
		Authenticator auth = new CryptedXMLDocAuthenticator();
		auth.authenticate("anou_mana","badpass");
	}
}
