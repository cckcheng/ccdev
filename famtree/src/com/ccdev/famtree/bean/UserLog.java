/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ccdev.famtree.bean;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Colin Cheng
 */
@Entity
@Table(name = "user_log")
@NamedQueries({@NamedQuery(name = "UserLog.findAll", query = "SELECT u FROM UserLog u")})
public class UserLog implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "userlog_id")
	private Long userlogId;
	@Column(name = "actiontime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date actiontime;
	@Column(name = "info")
	private String info;
	@Column(name = "result")
	private String result;
	@Column(name = "action")
	private String action;
	@Column(name = "dowhat")
	private String dowhat;
	@Column(name = "username")
	private String username;

	public UserLog() {
	}

	public UserLog(Long userlogId) {
		this.userlogId = userlogId;
	}

	public UserLog(String username, String dowhat, String action, String info) {
		this.username = username;
		this.dowhat = dowhat;
		this.action = action;
		this.info = info;
		this.actiontime = new Date();
	}

	public Long getUserlogId() {
		return userlogId;
	}

	public void setUserlogId(Long userlogId) {
		this.userlogId = userlogId;
	}

	public Date getActiontime() {
		return actiontime;
	}

	public void setActiontime(Date actiontime) {
		this.actiontime = actiontime;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDowhat() {
		return dowhat;
	}

	public void setDowhat(String dowhat) {
		this.dowhat = dowhat;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (userlogId != null ? userlogId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof UserLog)) {
			return false;
		}
		UserLog other = (UserLog) object;
		if ((this.userlogId == null && other.userlogId != null) || (this.userlogId != null && !this.userlogId.equals(other.userlogId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.ccdev.famtree.bean.UserLog[userlogId=" + userlogId + "]";
	}

}
