package org.apache.juddi.portlets.server.service;

import java.security.Principal;

import org.apache.catalina.User;

public class CatalinaUser {

	public String getPassword(Principal user) {
		User catalineUser = (User) user;
		return catalineUser.getPassword();
	}
}
