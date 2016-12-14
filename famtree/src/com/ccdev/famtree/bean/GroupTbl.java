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
@Table(name = "group_tbl")
@NamedQueries({@NamedQuery(name = "GroupTbl.findAll", query = "SELECT g FROM GroupTbl g")})
public class GroupTbl implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "group_id")
	private Long groupId;
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
	@ManyToMany(mappedBy = "groupTblCollection")
	private Collection<UserTbl> userTblCollection;

	public GroupTbl() {
	}

	public GroupTbl(Long groupId) {
		this.groupId = groupId;
	}

	public GroupTbl(String groupname,String desc) {
		this.groupname = groupname;
		this.description = desc;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
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

	public Collection<UserTbl> getUserTblCollection() {
		return userTblCollection;
	}

	public void setUserTblCollection(Collection<UserTbl> userTblCollection) {
		this.userTblCollection = userTblCollection;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (groupId != null ? groupId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof GroupTbl)) {
			return false;
		}
		GroupTbl other = (GroupTbl) object;
		if ((this.groupId == null && other.groupId != null) || (this.groupId != null && !this.groupId.equals(other.groupId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.ccdev.famtree.bean.GroupTbl[groupId=" + groupId + "]";
	}
	 public boolean on_using(EntityManager em) {
		return false;
	}
}
