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

import com.ccdev.famtree.Macro;
import com.ccdev.famtree.bean.*;
import com.ccdev.printout.FamilyTreeNode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author ccheng
 */
public class ExpressTreeBuilder {
    private int langCode = Macro.LANGUAGE_ENGLISH;   // 1-Chinese Simplified, 2- chinese traditional, 5- english

    public ExpressTreeBuilder(int langCode) {
        this.langCode = langCode;
    }
    
    private StringBuilder errMsg = new StringBuilder();
    public String getError() {
      return this.errMsg.toString();
    }
    
    private int startGen = 1;
    private List<List<String>> generationMembers = new ArrayList<>();   // members in one generation
    private List<List<List<String>>> allGenerationMembers = new ArrayList<>();   // one generation per entry
    
    static final long MAX_INFILE_LENGTH = 1048576L;     // 1M
    
    static final int ERROR_INPUT_LENGTH_OVERLIMIT = 1;
    static final int ERROR_FILE_NOT_FOUND = 2;
    static final int ERROR_IO_EXCEPTION = 3;
    static final int ERROR_UNKNOWN_ENCODING = 4;
    static final int ERROR_SQL_EXCEPTION = 5;

    static final int ERROR_LINE_SHORT = 11;
    static final int ERROR_LINE_OVER = 12;

    static final int ERROR_GENERATION_FORMAT = 101;
    static final int ERROR_GENERATION_INVALID = 102;
    static final int ERROR_FIRST_GERERATION = 111;
    
    static final int ERROR_CHILDREN_SHORT = 201;
    static final int ERROR_CHILDREN_OVER = 202;

    static final int ERROR_EMPTY_LIST = 501;
    static final int ERROR_EXIST_INDIVIDUAL_NOT_LEAF = 502;

    private void setError(int errorCode) {
        this.setError(errorCode, null);
    }
    
    private void setError(int errorCode, String addInfo) {
        String msg = "Not supported language";
        switch(this.langCode) {
            case Macro.LANGUAGE_ENGLISH:// English
                msg = "Unknown";
                switch(errorCode) {
                    case ERROR_INPUT_LENGTH_OVERLIMIT:
                        msg = "Input Length Over Limit: Max. " + MAX_INFILE_LENGTH;
                        break;
                    case ERROR_FILE_NOT_FOUND:
                        msg = "File Not Found";
                        break;
                    case ERROR_IO_EXCEPTION:
                        msg = "IO Exception";
                        break;
                    case ERROR_SQL_EXCEPTION:
                        msg = "SQL Exception";
                        break;

                    case ERROR_GENERATION_FORMAT:
                        msg = "Wrong Generation Format, should be -ddd-";
                        break;
                    case ERROR_GENERATION_INVALID:
                        msg = "Invalid Generation";
                        break;
                    case ERROR_FIRST_GERERATION:
                        msg = "First Generation can not include brothers";
                        break;
                    case ERROR_LINE_SHORT:
                        msg = "Lines not enough";
                        break;
                    case ERROR_LINE_OVER:
                        msg = "Lines over limit";
                        break;

                    case ERROR_CHILDREN_SHORT:
                        msg = "Lines of children is less than the number of fathers";
                        break;
                    case ERROR_CHILDREN_OVER:
                        msg = "Lines of children is more than the number of fathers";
                        break;

                    case ERROR_EXIST_INDIVIDUAL_NOT_LEAF:
                        msg = "The leading individual exist in DB, but is not a leaf.";
                        break;
                    default:
                        msg += " " + errorCode;
                        break;
                }
                break;
            case Macro.LANGUAGE_CHINESE_SIMPLIFIED:
                msg = "未知错误";
                switch(errorCode) {
                    default:
                        msg += " " + errorCode;
                        break;
                }
                break;
        }
        
        this.errMsg.append(msg);
        if(addInfo != null) this.errMsg.append(addInfo);
    }
       
    private void setError(String msg) {
        this.errMsg.append(msg);
    }

    public boolean readFile(File inFile, List<String> lines) {
        BufferedReader br = null;
           
        // try UTF-8 first
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), Charset.forName("UTF-8")));

            int bt = br.read();
            if(bt != 0xFEFF) {
                br.reset(); // exception if not UTF-8
            }
            String s;
            while((s = br.readLine()) != null) {
                lines.add(s.trim());
            }
            return true;
        } catch (FileNotFoundException ex) {
            this.setError(ERROR_FILE_NOT_FOUND, ": " + inFile.getName());
            return false;
        } catch (IOException ex) {
            // do not return, move on and try CP936
        } finally {
            try {
                if(br != null) br.close();
            } catch (Exception e) {
            }
        }

        // try CP936
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), "CP936"));
            String s;
            while((s = br.readLine()) != null) {
                lines.add(s.trim());
            }
            return true;
        } catch (FileNotFoundException ex) {
            this.setError(ERROR_FILE_NOT_FOUND, ": " + inFile.getName());
            return false;
        } catch (IOException ex) {
//            this.setError(ERROR_IO_EXCEPTION, ": " + inFile.getName());
        } finally {
            try {
                if(br != null) br.close();
            } catch (Exception e) {
            }
        }

        this.setError(ERROR_UNKNOWN_ENCODING);
        return false;
    }

    public boolean processInput(String fileName, boolean writeDB) {
        // the input file has to be saved as UTF-8, default is ANSI for Notepad
        File inFile = new File(fileName);
        if(!inFile.exists()) {
            this.setError(ERROR_FILE_NOT_FOUND, ": " + fileName);
            return false;
        }

        if(inFile.length() > MAX_INFILE_LENGTH) {
            this.setError(ERROR_INPUT_LENGTH_OVERLIMIT);
            return false;
        }
        
        List<String> lines = new ArrayList<String>();
        if(!this.readFile(inFile, lines)) {
            return false;
        }
        
        if(!this.validateInput(lines)){
            return false;
        }
        
        if(!this.buildFamilyTree()){
            return false;
        }
  
//        if(writeDB && !this.recordFamilyTree(1)){
//            return false;
//        }
  
        return true;
    }
    
    public boolean processInput(String input, Pedigree ped, EntityManager em) {
        if(input.length() > MAX_INFILE_LENGTH) {
            this.setError(ERROR_INPUT_LENGTH_OVERLIMIT);
            return false;
        }

        List<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new StringReader(input));
        String s;
        try {
            while((s = reader.readLine()) != null) lines.add(s);
        } catch (IOException ex) {
            this.setError(ERROR_IO_EXCEPTION);
            return false;
        }
        
        if(!this.validateInput(lines)){
            return false;
        }
        
        if(!this.recordFamilyTree(ped, em)){
            return false;
        }
  
        return true;
    }
    
    private int generation(String s) {
        return Integer.parseInt(s.substring(1, s.length() - 1));
    }
    
    static final String PATTERN_GENERATION = "-[0-9]+-";
    private boolean validateInput(List<String> lines) {
        while(!lines.isEmpty() && lines.get(0).isEmpty()) lines.remove(0);  // remove empty line(s) in the beggining

        String s;
        for(int x=lines.size() - 1; x>=0; x--) {
            s = lines.get(x);
            if(!s.isEmpty()) break;
            lines.remove(x);    // remove tail empty line(s)
        }

        int totalLine = lines.size();
        if(totalLine < 2) {
            setError(ERROR_LINE_SHORT);
            return false;
        }

//        for(String a : lines) System.out.println(a);
        
        s = lines.get(0);
        if(!s.matches(PATTERN_GENERATION)){
            setError(ERROR_GENERATION_FORMAT, ": " + s);
            return false;
        }

        int curGen = this.startGen = generation(s);
        int nextGen = 0;
        int pNum = 0;

        int x = 1;
        // parse first generation
        do {
            s = lines.get(x);
            if(s.matches(PATTERN_GENERATION)) {
                nextGen = generation(s);
                if(nextGen != curGen + 1) {
                    setError(ERROR_GENERATION_INVALID, ": " + s + this.lineIndex(x));
                    return false;
                }
                this.allGenerationMembers.add(this.generationMembers);
                this.generationMembers = new ArrayList<>();
                curGen = nextGen;
                break;
            }
            
            if(siblingNumber(s) > 1) {
                setError(ERROR_FIRST_GERERATION, ": " + s);
                return false;
            }
            pNum++;
        } while(++x < totalLine);
        
        // parse the rest generations
        while(++x < totalLine) {
            int cNum = 0;
            int cLine = 0;
            do {
                s = lines.get(x);
                if(s.matches(PATTERN_GENERATION)) {
                    nextGen = this.generation(s);
                    break;
                }
                
                cLine++;
                cNum += this.siblingNumber(s);
            } while (++x < totalLine);

            if(cLine < pNum) {
                this.setError(ERROR_CHILDREN_SHORT, ": -" + curGen + "-");
                return false;
            }
            if(cLine > pNum) {
                this.setError(ERROR_CHILDREN_OVER, ": -" + curGen + "-");
                return false;
            }

            this.allGenerationMembers.add(this.generationMembers);
            
            if(x >= totalLine) break;   // finished
            
            if(nextGen != curGen + 1) {
                setError(ERROR_GENERATION_INVALID, ": " + s + lineIndex(x+1));
                return false;
            }
            this.generationMembers = new ArrayList<>();
            curGen = nextGen;
            pNum = cNum;
        }
        return true;
    }
    
    private String lineIndex(int idx) {
        switch( this.langCode) {
            case 1:
            default:
                return "(Line: " + idx + ")";
            case 2:
                return "(行号:" + idx + ")";
        }
    }
    
    private int siblingNumber(String s) {
        if(s.isEmpty() || s.matches("-+")) {
            this.generationMembers.add(null);
            return 0;
        }
        String[] ss = s.split("([,，]\\s*)|(\\s+)");
//        System.out.println("siblings: " + ss.length);
        List<String> names = new ArrayList<>();
        for(String name : ss) {
            if(name.isEmpty()) continue;
            names.add(name);
        }
        if(names.isEmpty()) {
            this.generationMembers.add(null);
            return 0;
        }
        this.generationMembers.add(names);
        return names.size();
    }

    private List<Individual> indList = new ArrayList<>();
    private boolean buildFamilyTree() {
        if(this.allGenerationMembers.isEmpty()) {
            this.setError(ERROR_EMPTY_LIST);
            return false;
        }

        List<FamilyTreeNode> tmpList = new ArrayList<>();
        List<List<String>> genMembers = this.allGenerationMembers.get(0);
        for(List<String> mm : genMembers) {
            for(String name : mm) {
                Individual ind = new Individual(name);
                ind.setGen(startGen);
                FamilyTreeNode node = new FamilyTreeNode(ind);
                tmpList.add(node);
                this.indList.add(ind);
            }
        }

        for(int x=1, total=this.allGenerationMembers.size(); x<total; x++) {
            genMembers = this.allGenerationMembers.get(x);
            for(List<String> mm : genMembers) {
                FamilyTreeNode father = tmpList.get(0);
                if(mm == null) {
                    father.setLeaf(true);
                    tmpList.remove(0);
                    continue;
                }
                for(String name : mm) {
                    Individual ind = new Individual(name);
                    ind.setGen(startGen + x);
                    FamilyTreeNode node = new FamilyTreeNode(ind);
                    node.setFather(father);
                    father.addChild(node);
                    tmpList.add(node);
                    this.indList.add(ind);
                }
                tmpList.remove(0);
            }
        }

        return true;
    }

    private boolean recordFamilyTree(Pedigree ped, EntityManager em) {
        if(this.allGenerationMembers.isEmpty()) {
            this.setError(ERROR_EMPTY_LIST);
            return false;
        }

        String cond = "pedigree_id=" + ped.getId();

        List<Individual> tmpList = new ArrayList<>();
        List<List<String>> genMembers = this.allGenerationMembers.get(0);
        for(List<String> mm : genMembers) {
            for(String name : mm) {
                Individual ind = myUtil.findIndividual(ped.getIndividualTable(), cond + " and gen=" + this.startGen
                        + " and given_name='" + StringFunc.escapeChar(name, '\'') + "'", em);
                if(ind == null) {
                    ind = new Individual(name);
                    ind.setGen(startGen);
                    Long id = recordIndividual(ped, ind, em);
                    if(id == null) {
                        this.setError(ERROR_SQL_EXCEPTION);
                        return false;
                    }
                    ind.setId(id);
                } else {
                    if(myUtil.getCountBySQL("Select count(id) from " + ped.getIndividualTable()
                            + " where father_id=" + ind.getId(), em) > 0) {
                        this.setError(ERROR_EXIST_INDIVIDUAL_NOT_LEAF);
                        return false;
                    }
                }

                tmpList.add(ind);
            }
        }

        for(int x=1, total=this.allGenerationMembers.size(); x<total; x++) {
            genMembers = this.allGenerationMembers.get(x);
            for(List<String> mm : genMembers) {
                Individual father = tmpList.remove(0);
                if(mm == null) {
                    continue;
                }

                short seq = 1;
                for(String name : mm) {
                    Individual ind = new Individual(name);
                    ind.setFatherId(father.getId());
                    ind.setGen(startGen + x);
                    ind.setSeq(seq++);
                    Long id = recordIndividual(ped, ind, em);
                    if(id == null) {
                        this.setError(ERROR_SQL_EXCEPTION);
                        return false;
                    }
                    ind.setId(id);
                    tmpList.add(ind);
                }
            }
        }
        
        return true;
    }

    private Long recordIndividual(Pedigree ped, Individual ind,  EntityManager em) {
        String q = "Insert Into " + ped.getIndividualTable() + " set family_name=?1,given_name=?2"
                + ",gen=?3,father_id=?4,seq=?5";
        Query query = em.createNativeQuery(q);
        query.setParameter(1, ped.getFamilyName());
        query.setParameter(2, ind.getGivenName());
        query.setParameter(3, ind.getGen());
        query.setParameter(4, ind.getFatherId());
        query.setParameter(5, ind.getSeq());

        int n = query.executeUpdate();
        if(n < 1) return null;
        
        q = "SELECT LAST_INSERT_ID()";
        return myUtil.getLongBySQL(q, em);
    }

}
