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
package com.ccdev.famtree.impl;

import com.ccdev.famtree.DoAction;
import com.ccdev.famtree.Macro;
import com.ccdev.famtree.bean.*;
import java.util.List;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 * @author ccheng
 */
@SuppressWarnings(value = {"unchecked"})
public class OptPedigree implements DoAction {
    private UserLog log = null;

    @Override
    public String doAction(Users user, HttpServletRequest request, EntityManager em) {
        String result = "";
        String act = request.getParameter("action");

        if (act.equalsIgnoreCase("getPedigreeList")) {
                result = getPedigreeList(user, request, em);
        } else if (act.equalsIgnoreCase("manageUsers")) {
                this.log = myUtil.log(user, request, em);
                result = manageUsers(user, request, em);
        } else {
                result = myUtil.actionFail("unknown action:" + act, Macro.FAILCODE_IGNORE);
        }

        if (this.log != null) {
                this.log.setResult(result);
                em.merge(this.log);
        }
        return result;
    }

    private String manageUsers(Users user, HttpServletRequest request, EntityManager em) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private String getPedigreeList(Users user, HttpServletRequest request, EntityManager em) {
        JSONArray ar = new JSONArray();
        String q = "Select * From pedigree where creator_id=" + user.getId();
        List<Pedigree> rs = em.createNativeQuery(q, Pedigree.class).getResultList();
        if(rs.isEmpty()) return myUtil.actionSuccess(ar);
        
        for(Pedigree o : rs) {
            JSONObject obj = new JSONObject();
            obj.put("id", o.getId());
            obj.put("pedigree_name", o.getPedigreeName());
            obj.put("family_name", o.getFamilyName());
            obj.put("created", myUtil.formatTime(o.getCreateTime()));
            ar.put(obj);
        }
        return myUtil.actionSuccess(ar);
    }
    
}
