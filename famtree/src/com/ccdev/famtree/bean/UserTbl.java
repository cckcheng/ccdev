/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ccdev.famtree.bean;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

import com.ccdev.famtree.impl.myUtil;
import javax.persistence.EntityManager;

/**
 *
 * @author Colin Cheng
 */
@Entity
@Table(name = "user_tbl")
@NamedQueries({@NamedQuery(name = "UserTbl.findAll", query = "SELECT u FROM UserTbl u")})
public class UserTbl implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "user_id")
	private Long userId;
	@Column(name = "disabled")
	private Integer disabled;
	@Basic(optional = false)
	@Column(name = "level")
	private int level;
	@Basic(optional = false)
	@Column(name = "fullname")
	private String fullname;
	@Column(name = "password")
	private String password;
	@Basic(optional = false)
	@Column(name = "username")
	private String username;
	@Column(name = "last_login")
	@Temporal(TemporalType.TIMESTAMP)
	private Date last_login;
	@Column(name = "inused")
	private Integer inused;
	@JoinTable(name = "group_user", joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "group_id", referencedColumnName = "group_id")})
	@ManyToMany
	private Collection<GroupTbl> groupTblCollection;


	public UserTbl() {
	}

	public UserTbl(Long userId) {
		this.userId = userId;
	}

	public UserTbl(Long userId, int level, String fullname, String username) {
		this.userId = userId;
		this.level = level;
		this.fullname = fullname;
		this.username = username;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getDisabled() {
		return disabled;
	}

	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getInused() {
		return inused;
	}

	public void setInused(Integer inused) {
		this.inused = inused;
	}

	public Date getLastLogin() {
		return last_login;
	}

	public void setLastLogin(Date last_login) {
		this.last_login = last_login;
	}

	public Collection<GroupTbl> getGroupTblCollection() {
		return groupTblCollection;
	}

	public void setGroupTblCollection(Collection<GroupTbl> groupTblCollection) {
		this.groupTblCollection = groupTblCollection;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (userId != null ? userId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof UserTbl)) {
			return false;
		}
		UserTbl other = (UserTbl) object;
		if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.ccdev.famtree.bean.UserTbl[userId=" + userId + "]";
	}
	 public boolean on_using(EntityManager em) {
		return false;
	}
}
