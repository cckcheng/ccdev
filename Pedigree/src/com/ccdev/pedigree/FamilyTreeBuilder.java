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
package com.ccdev.pedigree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ccheng
 */
public class FamilyTreeBuilder {
    private int langCode = 1;   // 1-english, 2- chinese
    private String CONFIG_FILE = "famtree.conf";

    public FamilyTreeBuilder(int langCode) {
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
    
    static final int ERROR_FILE_LENGTH_OVERLIMIT = 1;
    static final int ERROR_FILE_NOT_FOUND = 2;
    static final int ERROR_IO_EXCEPTION = 3;
    static final int ERROR_SQL_EXCEPTION = 5;

    static final int ERROR_LINE_SHORT = 11;
    static final int ERROR_LINE_OVER = 12;

    static final int ERROR_GENERATION_FORMAT = 101;
    static final int ERROR_GENERATION_INVALID = 102;
    static final int ERROR_FIRST_GERERATION = 111;
    
    static final int ERROR_CHILDREN_SHORT = 201;
    static final int ERROR_CHILDREN_OVER = 202;

    static final int ERROR_EMPTY_LIST = 501;

    private void setError(int errorCode) {
        this.setError(errorCode, null);
    }
    
    private void setError(int errorCode, String addInfo) {
        String msg = "Not supported language";
        switch(this.langCode) {
            case 1:// English
                msg = "Unknown";
                switch(errorCode) {
                    case ERROR_FILE_LENGTH_OVERLIMIT:
                        msg = "File Length Over Limit: Max. " + MAX_INFILE_LENGTH;
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
                    default:
                        msg += " " + errorCode;
                        break;
                }
                break;
            case 2:
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

    public boolean processInput(String fileName) {
        // the input file has to be saved as UTF-8, default is ANSI for Notepad
        File inFile = new File(fileName);
        if(!inFile.exists()) {
            this.setError(ERROR_FILE_NOT_FOUND, ": " + fileName);
            return false;
        }

        if(inFile.length() > MAX_INFILE_LENGTH) {
            this.setError(ERROR_FILE_LENGTH_OVERLIMIT);
            return false;
        }

        BufferedReader br;
        List<String> lines;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(inFile), Charset.forName("UTF-8")));
            if(br.read() != 0xFEFF) {
                br.reset();
            }
            lines = new ArrayList<String>();
            String s;
            while((s = br.readLine()) != null) {
                lines.add(s.trim());
            }
        } catch (FileNotFoundException ex) {
            this.setError(ERROR_FILE_NOT_FOUND, ": " + fileName);
            return false;
        } catch (IOException ex) {
            this.setError(ERROR_IO_EXCEPTION, ": " + fileName);
            return false;
        }
        
        if(!this.validateInput(lines)){
            return false;
        }
        
        if(!this.buildFamilyTree()){
            return false;
        }
  
        if(!this.recordFamilyTree(1)){
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

        List<Individual> tmpList = new ArrayList<>();
        List<List<String>> genMembers = this.allGenerationMembers.get(0);
        for(List<String> mm : genMembers) {
            for(String name : mm) {
                Individual ind = new Individual(name);
                ind.setGen(startGen);
                tmpList.add(ind);
                this.indList.add(ind);
            }
        }

        for(int x=1, total=this.allGenerationMembers.size(); x<total; x++) {
            genMembers = this.allGenerationMembers.get(x);
            for(List<String> mm : genMembers) {
                Individual father = tmpList.get(0);
                if(mm == null) {
                    father.setLeaf(true);
                    tmpList.remove(0);
                    continue;
                }
                for(String name : mm) {
                    Individual ind = new Individual(name);
                    ind.setFather(father);
                    ind.setGen(startGen + x);
                    father.addChild(ind);
                    tmpList.add(ind);
                    this.indList.add(ind);
                }
                tmpList.remove(0);
            }
        }

        return true;
    }

    private boolean recordFamilyTree(long pedigreeId) {
        if(this.allGenerationMembers.isEmpty()) {
            this.setError(ERROR_EMPTY_LIST);
            return false;
        }

        Common c = new Common();
        Properties conf = c.loadConfigFile(CONFIG_FILE);
        if(c.hasError()) {
            this.setError(c.getError());
            return false;
        }

        Connection conn = c.getMysqlConnection(conf);
        if(c.hasError()) {
            this.setError(c.getError());
            return false;
        }

        Statement st = null;
        try {
            st = conn.createStatement();
            
            ResultSet rs = st.executeQuery("Select count(*) from individual where pedigree_id=");

            List<Individual> tmpList = new ArrayList<>();
            List<List<String>> genMembers = this.allGenerationMembers.get(0);
            for(List<String> mm : genMembers) {
                for(String name : mm) {
                    Individual ind = new Individual(name);
                    ind.setGen(startGen);
                    Long id = recordIndividual(ind, pedigreeId, st);
                    if(id == null) {
                        this.setError(ERROR_SQL_EXCEPTION);
                        return false;
                    }
                    ind.setId(id);
                    tmpList.add(ind);
                    this.indList.add(ind);
                }
            }

            for(int x=1, total=this.allGenerationMembers.size(); x<total; x++) {
                genMembers = this.allGenerationMembers.get(x);
                for(List<String> mm : genMembers) {
                    Individual father = tmpList.get(0);
                    if(mm == null) {
                        father.setLeaf(true);
                        tmpList.remove(0);
                        continue;
                    }
                    
                    int seq = 1;
                    for(String name : mm) {
                        Individual ind = new Individual(name);
                        ind.setFather(father);
                        ind.setGen(startGen + x);
                        ind.setSeq(seq++);
                        Long id = recordIndividual(ind, pedigreeId, st);
                        if(id == null) {
                            this.setError(ERROR_SQL_EXCEPTION);
                            return false;
                        }
                        ind.setId(id);
                        father.addChild(ind);
                        tmpList.add(ind);
                        this.indList.add(ind);
                    }
                    tmpList.remove(0);
                }
            }
        
        } catch (SQLException ex) {
            this.setError(ERROR_SQL_EXCEPTION);
            return false;
        } finally {      
            try {
                if(st != null) st.close();
                if(conn != null) conn.close();
            } catch (SQLException ex) {
            }
        }

        return true;
    }

    private Long recordIndividual(Individual ind, long pedigreeId, Statement st) throws SQLException {
        String q = "Insert Into individual set given_name=" + ind.getName();
        q += ",set gen=" + ind.getGen();
        q += ",set family_name=(select family_name from pedigree where id=" + pedigreeId + ")";
        Individual father = ind.getFather();
        if(father != null) {
            q += ",set father_id=" + father.getId();
            q += ",set seq=" + ind.getSeq();
        }
        int num = st.executeUpdate(q, Statement.RETURN_GENERATED_KEYS);
        if(num != 1) return null;
        
        return st.getGeneratedKeys().getLong(1);
    }

    public void printAll() {
        printNode(this.indList.get(0));
    }
    
    private void printNode(Individual node) {
        if(node == null) return;
        
        System.out.println(node.getName() + " #" + node.getGen());
        if(node.isLeaf() || node.getChildren().isEmpty()) {
            printSibling(node);
        } else {
            printNode(node.getChildren().get(0));
        }
    }
    
    private void printSibling(Individual node) {
        if(node == null) return;
        
        Individual sibling = node.getNextSibling();
        if(sibling != null) {
            printNode(sibling); 
        } else {
            printSibling(node.getFather()); 
        }
    }
}
