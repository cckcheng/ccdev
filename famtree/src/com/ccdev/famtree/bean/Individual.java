/*
 * Copyright (c) 2016, ccheng
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.ccdev.famtree.bean;

import com.ccdev.famtree.impl.myUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * this is a non-entity class
 * @author ccheng
 */
public class Individual implements Serializable, Comparable {

    private Long id;
    private String givenName;
    private String familyName;
    private Short gender;
    private String alias;
    private Date birth;
    private Date death;
    private Long fatherId;
    private Long motherId;
    private long pedigreeId;
    private Short seq;
    private Integer gen;

    public Individual() {
    }

    public Individual(Long id) {
        this.id = id;
    }

    public Individual(String givenName) {
        this.givenName = givenName;
    }

    public Individual(Long id, String givenName, long pedigreeId) {
        this.id = id;
        this.givenName = givenName;
        this.pedigreeId = pedigreeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public Short getGender() {
        return gender;
    }

    public void setGender(Short gender) {
        this.gender = gender;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Date getDeath() {
        return death;
    }

    public void setDeath(Date death) {
        this.death = death;
    }

    public Long getFatherId() {
        return fatherId;
    }

    public void setFatherId(Long fatherId) {
        this.fatherId = fatherId;
    }

    public Long getMotherId() {
        return motherId;
    }

    public void setMotherId(Long motherId) {
        this.motherId = motherId;
    }

    public long getPedigreeId() {
        return pedigreeId;
    }

    public void setPedigreeId(long pedigreeId) {
        this.pedigreeId = pedigreeId;
    }

    public Short getSeq() {
        return seq;
    }

    public void setSeq(Short seq) {
        this.seq = seq;
    }

    public Integer getGen() {
        return gen;
    }

    public void setGen(Integer gen) {
        this.gen = gen;
    }

    public String getPrintName(String rootFamilyName) {
        if(rootFamilyName == null || rootFamilyName.equalsIgnoreCase(this.familyName)) return this.givenName;
        return myUtil.makeFullName(familyName, givenName);
    }

    public boolean hasSpouse() {
        // need modify
        return false;
    }
    
    public String getSpouseName() {
        // need modify
        return "";
    }
    
    private List<String> notes = new ArrayList<>();

    public boolean hasNote() {
        return !this.notes.isEmpty();
    }

    public List<String> getNotes() {
        return this.notes;
    }

    public void addNote(String note) {
        this.notes.add(note);
    }
    
    private List<String> infos = new ArrayList<>();
    public boolean hasInfo() {
        return !this.infos.isEmpty();
    }

    public List<String> getInfo() {
        return this.infos;
    }
    
    public void addInfo(String info) {
        this.infos.add(info);
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
        if (!(object instanceof Individual)) {
            return false;
        }
        Individual other = (Individual) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.ccdev.famtree.bean.Individual[ id=" + id + " ]";
    }

    @Override
    public int compareTo(Object obj) {
            if (obj == null) {
                    return -1;
            }
            if (getClass() != obj.getClass()) {
                    return -1;
            }
            final Individual other = (Individual) obj;
            return this.getGivenName().compareToIgnoreCase(other.getGivenName());
    }

}
