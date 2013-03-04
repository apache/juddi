/*
 * Copyright 2001-2008 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.juddi.v3.auth;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.JAXBException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.config.AppConfig;
import org.apache.juddi.config.Property;
import org.apache.juddi.cryptor.Cryptor;
import org.apache.juddi.cryptor.CryptorFactory;
import org.apache.juddi.v3.error.AuthenticationException;
import org.apache.juddi.v3.error.ErrorMessage;
import org.apache.juddi.v3.error.FatalErrorException;
import org.apache.juddi.v3.error.RegistryException;
import org.apache.juddi.v3.error.UnknownUserException;

/**
 * @author Anou Manavalan
 */
public class CryptedXMLDocAuthenticator extends XMLDocAuthenticator {
	
	private Log logger = LogFactory.getLog(this.getClass());
	/**
	 * @throws IOException
	 * @throws JAXBException
	 * @throws ConfigurationException 
	 * 
	 */
	public CryptedXMLDocAuthenticator() throws JAXBException, IOException, ConfigurationException {
		super();
	}
	@Override
	protected String getFilename() throws ConfigurationException {
		return AppConfig.getConfiguration().getString(Property.JUDDI_USERSFILE, Property.DEFAULT_ENCRYPTED_XML_USERSFILE);
	}
	/**
	 *
	 */
	public String authenticate(String userID, String credential)
	throws AuthenticationException, FatalErrorException {
		preProcess(userID, credential);
		String encryptedCredential = encrypt(credential);
		return postProcess(userID, encryptedCredential);
	}
	/**
	 *
	 */
	private String encrypt(String str) throws FatalErrorException {
		try {
			Cryptor cryptor = (Cryptor) CryptorFactory.getCryptor();
			return cryptor.encrypt(str);
		} catch (InvalidKeyException e) {
			logger.error("Invalid Key Exception in crypting the password", e);
			throw new FatalErrorException(new ErrorMessage(
					"errors.auth.cryptor.InvalidKey", e.getMessage()));
		} catch (NoSuchPaddingException e) {
			logger.error("Padding Exception in crypting the password", e);
			throw new FatalErrorException(new ErrorMessage(
					"errors.auth.cryptor.Padding", e.getMessage()));
		} catch (NoSuchAlgorithmException e) {
			logger.error("Algorithm Exception in crypting the password", e);
			throw new FatalErrorException(new ErrorMessage(
					"errors.auth.cryptor.Algorithm", e.getMessage()));
		} catch (InvalidAlgorithmParameterException e) {
			logger.error("Algorithm parameter Exception in crypting the password",
					e);
			throw new FatalErrorException(new ErrorMessage(
					"errors.auth.cryptor.AlgorithmParam", e.getMessage()));
		} catch (IllegalBlockSizeException e) {
			logger.error("Block size Exception in crypting the password", e);
			throw new FatalErrorException(new ErrorMessage(
					"errors.auth.cryptor.BlockSize", e.getMessage()));
		} catch (BadPaddingException e) {
			logger.error("Bad Padding Exception in crypting the password", e);
			throw new FatalErrorException(new ErrorMessage(
					"errors.auth.cryptor.BadPadding", e.getMessage()));
		}
	}
	/**
	 * @param userID
	 * @param credential
	 * @throws RegistryException
	 */
	private void preProcess(String userID, String credential)
	throws AuthenticationException {
		// a userID must be specified.
		if (userID == null) {
			throw new UnknownUserException(new ErrorMessage(
					"errors.auth.InvalidUserId"));
		}
		// credential (password) must be specified.
		if (credential == null) {
			throw new UnknownUserException(new ErrorMessage(
			"errors.auth.InvalidCredentials"));
		}
	}
	/**
	 * @param userID
	 * @param encryptedCredential
	 * @return
	 * @throws AuthenticationException
	 */
	private String postProcess(String userID, String encryptedCredential)
	throws AuthenticationException {
		if (userTable.containsKey(userID)) {
			User user = (User) userTable.get(userID);
			if ((user.getPassword() == null)
					|| (!encryptedCredential.equals(user.getPassword()))) {
				throw new UnknownUserException(new ErrorMessage(
						"errors.auth.InvalidCredentials", userID));
			}
		} else {
			throw new UnknownUserException(new ErrorMessage(
					"errors.auth.InvalidUserId", userID));
		}
		return userID;
	}
}
