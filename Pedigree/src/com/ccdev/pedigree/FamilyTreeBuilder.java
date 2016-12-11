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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ccheng
 */
public class FamilyTreeBuilder {
    private int langCode = 1;   // 1-english, 2- chinese

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
        List<String> lines = new ArrayList<String>();
        lines.add("-1-");
        lines.add("a1");
        lines.add("-2-");
        lines.add("b1,b2");
        lines.add("-3-");
        lines.add("c1");
        lines.add("c2,c3");
        lines.add("-4-");
        lines.add("--");
        lines.add("d1,d2");
        lines.add("d3");
        lines.add("-5-");
        lines.add("--");
        lines.add("e1");
        lines.add("-");
        lines.add("-6-");
        
        if(!this.validateInput(lines)){
            return false;
        }
        
        if(!this.buildFamilyTree()){
            return false;
        }
        
        return true;
    }
    
    private int generation(String s) {
        return Integer.parseInt(s.substring(1, s.length() - 1));
    }
    
    static final String PATTERN_GENERATION = "-[0-9]+-";
    private boolean validateInput(List<String> lines) {
        int totalLine = lines.size();
        if(totalLine < 2) {
            setError(ERROR_LINE_SHORT);
            return false;
        }

        String gen = lines.get(0);
        if(!gen.matches(PATTERN_GENERATION)){
            setError(ERROR_GENERATION_FORMAT, ": " + gen);
            return false;
        }

        int curGen = generation(gen);
        this.startGen = curGen;

        String s;
        int x = 1;
        int nextGen = 0;
        int pNum = 0;
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
                tmpList.add(ind);
                this.indList.add(ind);
            }
        }
        
        for(int x=1, total=this.allGenerationMembers.size(); x<total; x++) {
            genMembers = this.allGenerationMembers.get(x);
            for(List<String> mm : genMembers) {
                if(mm == null) {
                    tmpList.remove(0);
                    continue;
                }
                for(String name : mm) {
                    Individual ind = new Individual(name);
                    ind.setFather(tmpList.get(0));
                    tmpList.add(ind);
                    this.indList.add(ind);
                }
                tmpList.remove(0);
            }
        }
        return true;
    }
}
