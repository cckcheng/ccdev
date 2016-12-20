/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccdev.famtree.bean;

import com.ccdev.famtree.Macro;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author myu
 */
@Entity
public class Audit implements Serializable {
	private Long id;
	private static final long serialVersionUID = 1L;
	protected String dowhat;

	public Audit() {
		this.createtime = new Date();
	}

	public Audit(Long orgid, String dowhat, int acttype,Users creator) {
		if (orgid>0) this.orgid = orgid;
		this.dowhat = dowhat;
		this.creator = creator;
		this.acttype = acttype;
		this.createtime = new Date();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Get the value of tbname
	 *
	 * @return the value of tbname
	 */
	@Column(columnDefinition = "TEXT")
	public String getDowhat() {
		return dowhat;
	}

	/**
	 * Set the value of tbname
	 *
	 * @param tbname new value of tbname
	 */
	public void setDowhat(String dowhat) {
		this.dowhat = dowhat;
	}
	protected Users creator;

	/**
	 * Get the value of creator
	 *
	 * @return the value of creator
	 */
	@ManyToOne(optional = false)
	@JoinColumn(name = "creator_id")
	public Users getCreator() {
		return creator;
	}

	/**
	 * Set the value of creator
	 *
	 * @param creator new value of creator
	 */
	public void setCreator(Users creator) {
		this.creator = creator;
	}
	protected Date createtime;

	/**
	 * Get the value of createtime
	 *
	 * @return the value of createtime
	 */
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(columnDefinition = "timestamp NULL default NULL")
	public Date getCreatetime() {
		return createtime;
	}

	/**
	 * Set the value of createtime
	 *
	 * @param createtime new value of createtime
	 */
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	protected int acttype; //ACT_RMA,ACT_ADMIN

	/**
	 * Get the value of act
	 *
	 * @return the value of act
	 */
	public int getActtype() {
		return acttype;
	}

	/**
	 * Set the value of act
	 *
	 * @param act new value of act
	 */
	public void setActtype(int acttype) {
		this.acttype = acttype;
	}
	protected Long orgid; // think about SN, so this type is string

	/**
	 * Get the value of orgid
	 *
	 * @return the value of orgid
	 */
	public Long getOrgid() {
		return orgid;
	}

	/**
	 * Set the value of orgid
	 *
	 * @param orgid new value of orgid
	 */
	public void setOrgid(Long orgid) {
		this.orgid = orgid;
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
		if (!(object instanceof Audit)) {
			return false;
		}
		Audit other = (Audit) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.ccdev.famtree.bean.Audit[id=" + id + "]";
	}
}
