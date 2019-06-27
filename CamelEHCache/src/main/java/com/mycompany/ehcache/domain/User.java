package com.mycompany.ehcache.domain;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class User implements Serializable {
	
	private static final long serialVersionUID = -7768952755794187106L;
	private static final Logger logger = Logger.getLogger(User.class);

	private String name;
	private String firstname;
	private String alias;
	
	
	public User(String name, String firstname) {
		super();
		this.name = name;
		this.firstname = firstname;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", firstname=" + firstname + "]";
	}

}
