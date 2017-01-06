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
import com.ccdev.famtree.printout.GenPDF;
import com.ccdev.famtree.printout.TreeToPDF;
import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
        } else if (act.equalsIgnoreCase("printOut")) {
                result = printOut(user, request, em);
        } else if (act.equalsIgnoreCase("getIndividualList")) {
                result = getIndividualList(user, request, em);
        } else if (act.equalsIgnoreCase("manageUsers")) {
                this.log = myUtil.log(user, request, em);
                result = manageUsers(user, request, em);
        } else if (act.equalsIgnoreCase("batchImport")) {
                this.log = myUtil.log(user, request, em);
                result = batchImport(user, request, em);
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
        return myUtil.actionSuccess();
    }

    private String getPedigreeList(Users user, HttpServletRequest request, EntityManager em) {
        JSONArray ar = new JSONArray();
        String cond = "creator_id=" + user.getId();
        int total = myUtil.getCountBySQL("Select count(*) from pedigree where " + cond, em);
        if(total == 0) return myUtil.actionSuccess(ar);

        String q = "Select * From pedigree where " + cond + " order by id desc";
        q += myUtil.limitClause(request);

        List<Pedigree> rs = em.createNativeQuery(q, Pedigree.class).getResultList();
        for(Pedigree o : rs) {
            JSONObject obj = new JSONObject();
            obj.put("id", o.getId());
            obj.put("pedigree_name", o.getPedigreeName());
            obj.put("family_name", o.getFamilyName());
            obj.put("created", myUtil.formatTime(o.getCreateTime()));
            obj.put("modified", myUtil.formatTime(o.getModifyTime()));
            ar.put(obj);
        }
        return myUtil.actionSuccess(total, ar);
    }

    private String getIndividualList(Users user, HttpServletRequest request, EntityManager em) {
        if(!myUtil.hasPermission(user, Macro.MODULE_BUILDER, em)) return myUtil.actionFail(Macro.ERR_PERMISSION_DENY);

        long pedId = myUtil.LongWithNullToZero(request.getParameter("id"));
        if(pedId == 0L) return myUtil.actionFail(Macro.ERR_PARAM_REQUIRED);
        Pedigree ped = em.find(Pedigree.class, pedId);
        if(ped == null) return myUtil.actionFail(Macro.ERR_SYSTEM);

        if(!allowView(user, ped, em)) return myUtil.actionFail(Macro.ERR_PERMISSION_DENY);
        
        String cond = "pedigree_id=" + pedId + makeFilter(request);
        String qCount = "Select Count(*) from " + ped.getIndividualTable();
        String q = "Select " + myUtil.INDIVIDUAL_FIELDS + " from " + ped.getIndividualTable();
        if(!cond.isEmpty()) {
            qCount += " Where " + cond;
            q += " Where " + cond;
        }
        
        JSONArray ar = new JSONArray();
        int total = myUtil.getCountBySQL(qCount, em);
        if(total == 0) return myUtil.actionSuccess(total, ar);

        String sort = StringFunc.TrimedString(request.getParameter("sort"));
        String direction = StringFunc.TrimedString(request.getParameter("dir"));
        if(sort.isEmpty()) {
            sort = "id";
        } else if(sort.equalsIgnoreCase("given_name")) {
            sort = "convert(given_name using gbk)"; // order by Pinyin
        }
        if(direction.isEmpty()) sort = "DESC";
        q += " order by " + sort + " " + direction + myUtil.limitClause(request);
        
        List<Object[]> rs = em.createNativeQuery(q).getResultList();
        for(Object[] o : rs) {
            JSONObject obj = new JSONObject();
            Individual ind = myUtil.toIndividual(o);
            obj.put("id", ind.getId());
            obj.put("given_name", ind.getGivenName());
            obj.put("gen", ind.getGen());
            if(ind.hasInfo()) {
                String info = "";
                for(String s : ind.getInfo()) info += s + "\n";
                obj.put("info", info);
            }
            ar.put(obj);
        }
        return myUtil.actionSuccess(total, ar);
    }
    
    private String makeFilter(HttpServletRequest request) {
        String and = " and ";
        StringBuilder sb = new StringBuilder();
        
        String givenName = StringFunc.TrimedString(request.getParameter("given_name"));
        if(!givenName.isEmpty()) {
            sb.append(and).append("given_name like '%").append(StringFunc.escapedString(givenName)).append("%'");
        }
        
        String info = StringFunc.TrimedString(request.getParameter("info"));
        if(!info.isEmpty()) {
            sb.append(and).append("info like '%").append(StringFunc.escapedString(info)).append("%'");
        }
        
        int startGen = myUtil.IntegerWithNullToZero(request.getParameter("start_gen"));
        if(startGen > 0) {
            sb.append(and).append("gen>=").append(startGen);
        }
        int endGen = myUtil.IntegerWithNullToZero(request.getParameter("end_gen"));
        if(endGen > 0) {
            sb.append(and).append("gen<=").append(endGen);
        }
//        if(sb.length() > 0) return sb.substring(and.length());    // keep the and
        return sb.toString();
    }

    private String printOut(Users user, HttpServletRequest request, EntityManager em) {
        if(!myUtil.hasPermission(user, Macro.MODULE_BUILDER, em)) return myUtil.actionFail(Macro.ERR_PERMISSION_DENY);

        long pedId = myUtil.LongWithNullToZero(request.getParameter("id"));
        if(pedId == 0L) return myUtil.actionFail(Macro.ERR_PARAM_REQUIRED);
        Pedigree ped = em.find(Pedigree.class, pedId);
        if(ped == null) return myUtil.actionFail(Macro.ERR_SYSTEM);

        if(!allowView(user, ped, em)) return myUtil.actionFail(Macro.ERR_PERMISSION_DENY);
      
        Long rootIndId = ped.getRootIndividualId();
        Individual rootInd = null;
        if(rootIndId != null) {
            rootInd = myUtil.findIndividual(ped.getIndividualTable(), ped.getRootIndividualId(), em);
            if(rootInd == null) return myUtil.actionFail(Macro.ERR_SYSTEM);
        } else {
            if(myUtil.getCountBySQL("Select count(id) from " + ped.getIndividualTable()
                    + " Where pedigree_id=" + pedId, em) < 1) {
                return myUtil.actionFail("Empty Pedigree", Macro.FAILCODE_IGNORE);
            }
            rootInd = myUtil.findIndividual(ped.getIndividualTable(), "pedigree_id=" + pedId + " and father_id is null", em);
            if(rootInd == null) return myUtil.actionFail(Macro.ERR_SYSTEM);
        }

        String outFilename = "ped-" + ped.getId() + ".pdf";
        File outFile = new File(TreeToPDF.OUTPUT_DIR + outFilename);
        if(outFile.exists()) {
            return myUtil.actionFail("Processing, please wait...", Macro.FAILCODE_IGNORE);
        }

        try {
            InitialContext initialContext = new InitialContext();
            Executor eb = (Executor) initialContext.lookup("java:module/async_executor");
            final Pedigree fPed = ped;
            final Individual fRootInd = rootInd;
            final File fOutFile = outFile;
            eb.execute( new Runnable() {
                @Override
                public void run() {
                    GenPDF proc = new GenPDF(em);
                    if(proc.printOut(fPed, fRootInd, fOutFile)) {
                        // send success notification email
                    } else {
                        // send fail alert email
                    }
                }
            } );
        } catch (NamingException ex) {
            ex.printStackTrace();
            return myUtil.actionFail(Macro.ERR_SYSTEM);
        }
        return myUtil.actionSuccess();
    }

    public boolean allowModify(Users user, Pedigree ped, EntityManager em) {
        if(user.getId() == ped.getCreatorId()) return true;
        return myUtil.getCountBySQL("select count(*) from pedigree_users where pedigree_id=" + ped.getId()
                + " and user_id=" + user.getId() + " and privilege>1", em) > 0;
    }

    public boolean allowView(Users user, Pedigree ped, EntityManager em) {
        if(user.getId() == ped.getCreatorId()) return true;
        return myUtil.getCountBySQL("select count(*) from pedigree_users where pedigree_id=" + ped.getId()
                + " and user_id=" + user.getId() + " and privilege>=1", em) > 0;
    }

    private String batchImport(Users user, HttpServletRequest request, EntityManager em) {
        if(!myUtil.hasPermission(user, Macro.MODULE_BUILDER, em)) return myUtil.actionFail(Macro.ERR_PERMISSION_DENY);

        long pedId = myUtil.LongWithNullToZero(request.getParameter("id"));
        String input = StringFunc.TrimedString(request.getParameter("input"));
        if(pedId == 0L) return myUtil.actionFail(Macro.ERR_PARAM_REQUIRED);
        Pedigree ped = em.find(Pedigree.class, pedId);
        if(ped == null) return myUtil.actionFail(Macro.ERR_SYSTEM);
        
        if(!allowModify(user, ped, em)) return myUtil.actionFail(Macro.ERR_PERMISSION_DENY);
        
        ExpressTreeBuilder proc = new ExpressTreeBuilder(SystemConfig.getIntConfig(SystemConfig.LANGUAGE_CODE));
        if(!proc.processInput(input, ped, em)) {
            return myUtil.actionFail(proc.getError(), Macro.FAILCODE_IGNORE);
        }

        return myUtil.actionSuccess();
    }
    
}
