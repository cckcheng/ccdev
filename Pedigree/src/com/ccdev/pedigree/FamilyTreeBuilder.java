package com.ccdev.pedigree;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ccheng
 */
public class FamilyTreeBuilder {
    public boolean processInput(String fileName) {
        List<String> lines = new ArrayList<String>();
        
        if(!this.validateInput(lines)){
            return false;
        }
        
        return true;
    }
    
    private boolean validateInput(List<String> lines) {
        return false;
    }
}
