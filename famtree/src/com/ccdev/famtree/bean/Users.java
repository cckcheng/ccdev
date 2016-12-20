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
@Table(name = "users")
public class Users implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Long id;
	@Column(name = "disabled")
	private Integer disabled;
	@Basic(optional = false)
	@Column(name = "level")
	private int level;
	@Basic(optional = false)
	@Column(name = "family_name")
	private String familyName;
	@Basic(optional = false)
	@Column(name = "given_name")
	private String givinName;
	@Column(name = "password")
	private String password;
	@Basic(optional = false)
	@Column(name = "username")
	private String username;
	@Column(name = "last_login")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLogin;
	@Column(name = "temp_pass")
	private String tempPassword;
	@Column(name = "temppass_expire")
	@Temporal(TemporalType.TIMESTAMP)
	private Date temppassExpire;

	@JoinTable(name = "group_user", joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "group_id", referencedColumnName = "id")})
	@ManyToMany
	private Collection<Groups> groupCollection;

	public Users() {
	}

	public Long getId() {
		return id;
	}

	public void setUserId(Long id) {
		this.id = id;
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

        public String getFamilyName() {
            return familyName;
        }

        public void setFamilyName(String familyName) {
            this.familyName = familyName;
        }

        public String getGivinName() {
            return givinName;
        }

        public void setGivinName(String givinName) {
            this.givinName = givinName;
        }

        public Date getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(Date lastLogin) {
            this.lastLogin = lastLogin;
        }

        public String getTempPassword() {
            return tempPassword;
        }

        public void setTempPassword(String tempPassword) {
            this.tempPassword = tempPassword;
        }

        public Date getTemppassExpire() {
            return temppassExpire;
        }

        public void setTemppassExpire(Date temppassExpire) {
            this.temppassExpire = temppassExpire;
        }

	public Collection<Groups> getGroupCollection() {
		return groupCollection;
	}

	public void setGroupCollection(Collection<Groups> groupCollection) {
		this.groupCollection = groupCollection;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Users)) {
			return false;
		}
		Users other = (Users) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.ccdev.famtree.bean.Users[id=" + id + "]";
	}
	 public boolean on_using(EntityManager em) {
		return false;
	}
}
