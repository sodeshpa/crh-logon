/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils;

import java.io.Serializable;

/**
 * @author AdNovum Informatik AG
 * 
 * NOTE: this is a *COPY* of the class
 * 
 *     ch.nevis.jcan.optrace.common.request.TransferContext
 *     
 * Even though the class-description of that class says that the API
 * is 'not supposed to change', it does change sometimes. This has 
 * caused me troubles in the past and I don't see any advantage in
 * making our project dependent on such a volatile claim, while the
 * simplest solution is to maintain this class ourself. After all,
 * there is no magic in here, and logon becomes much less tightly
 * coupled with optrace as a result of keeping it here. (I 
 * previously used TransferContext from optrace which caused me to
 * put optrace into each and every subproject's classpath.)
 */
public class TransferContext implements Serializable {
	private Long userId;
	private String principal = null;
	private String tid = null;
	private boolean remote = false;

	public TransferContext() {
	}

	public TransferContext(Long userId, String principal, String tid, boolean remote) {
		super();
		this.userId = userId;
		this.principal = principal;
		this.tid = tid;
		this.remote = remote;
	}

	public String getId() {
		return tid;
	}
	
	public void setId(String tid) {
		this.tid = tid;
	}
	
	public String getPrincipal() {
		return principal;
	}
	
	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public boolean isRemote() {
		return remote;
	}

	public void setRemote(boolean remote) {
		this.remote = remote;
	}
}
