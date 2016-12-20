/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ccdev.famtree.bean;

import com.ccdev.famtree.impl.myUtil;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Colin Cheng
 */
@Entity
@Table(name = "groups")
public class Groups implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Long id;
	@Column(name = "description")
	private String description;
	@Basic(optional = false)
	@Column(name = "disabled")
	private int disabled;
	@Basic(optional = false)
	@Column(name = "groupname")
	private String groupname;
	@Column(name = "user_mask")
	private Integer userMask;	// bit array, indicate the module(s) is/are permitted (user priviledge), see Macro for module info
	@Column(name = "manager_mask")
	private Integer managerMask;	// bit array, indicate the module(s) is/are permitted (manager priviledge), see Macro for module info
	@ManyToMany(mappedBy = "groupCollection")
	private Collection<Users> userCollection;

	public Groups() {
	}

        public Groups(String name, String descript) {
            this.groupname = name;
            this.description = descript;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDisabled() {
		return disabled;
	}

	public void setDisabled(int disabled) {
		this.disabled = disabled;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public Integer getUserMask() {
		return userMask;
	}

	public void setUserMask(Integer userMask) {
		this.userMask = userMask;
	}

	public Integer getManagerMask() {
		return managerMask;
	}

	public void setManagerMask(Integer managerMask) {
		this.managerMask = managerMask;
	}

	public Collection<Users> getUserCollection() {
		return userCollection;
	}

	public void setUserCollection(Collection<Users> userCollection) {
		this.userCollection = userCollection;
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
		if (!(object instanceof Groups)) {
			return false;
		}
		Groups other = (Groups) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.ccdev.famtree.bean.Groups[id=" + id + "]";
	}
	 public boolean on_using(EntityManager em) {
		return false;
	}
}
