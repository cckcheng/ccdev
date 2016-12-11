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
    
    static final int ERROR_LINE_SHORT = 11;
    static final int ERROR_LINE_OVER = 12;
    static final int ERROR_GENERATION_FORMAT = 101;
    static final int ERROR_GENERATION_INVALID = 102;
    static final int ERROR_FIRST_GERERATION = 111;
    
    static final int ERROR_CHILDREN_SHORT = 201;
    static final int ERROR_CHILDREN_OVER = 202;

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
        lines.add("-");
        lines.add("d1,d2");
        lines.add("d3");
        lines.add("-3-");
        lines.add("-");
        lines.add("d3");
        lines.add("d3");
        
        if(!this.validateInput(lines)){
            return false;
        }
        
        return true;
    }
    
    private int generation(String s) {
        return Integer.parseInt(s.substring(1, s.length() - 1));
    }
    
    private boolean validateInput(List<String> lines) {
        int totalLine = lines.size();
        if(totalLine < 2) {
            setError(ERROR_LINE_SHORT);
            return false;
        }

        String ptnGen = "-[0-9]+-";
        String gen = lines.get(0);
        if(!gen.matches(ptnGen)){
            setError(ERROR_GENERATION_FORMAT, ": " + gen);
            return false;
        }
        int pNum = 0;
        int cNum = 0;
        int cLine = 0;
        
        int curGen = generation(gen);
        int x = 1;
        String s;
        int nextGen = 0;
        // parse first generation
        do {
            s = lines.get(x);
            if(s.matches(ptnGen)) {
                nextGen = generation(s);
                if(nextGen != curGen + 1) {
                    setError(ERROR_GENERATION_INVALID, ": " + s);
                    return false;
                }
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
            cNum = 0;
            cLine = 0;
            do {
                s = lines.get(x);
                if(s.matches(ptnGen)) {
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
            
            if(x >= totalLine) break;   // finished
            
            if(nextGen != curGen + 1) {
                setError(ERROR_GENERATION_INVALID, ": " + s + lineIndex(x+1));
                return false;
            }
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
        if(s.equals("-")) return 0;
        String[] ss = s.split("([,，]\\s*)|(\\s+)");
//        System.out.println("siblings: " + ss.length);
        return ss.length;
    }
}
