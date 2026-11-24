/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Named
public class TestCDIBean {

	@PersistenceContext
	private EntityManager em;
	
	public TestCDIBean() {
	}
	
	public String hello(String text) {
		if(em == null) {
			throw new RuntimeException("EntityManager is not injected!");
		}
		return text + " with entityManager properties: " + em.getProperties();
	}
}
