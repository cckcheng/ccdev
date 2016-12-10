package com.ccdev.pedigree;

/**
 *
 * @author ccheng
 */
public class Pedigree {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FamilyTreeBuilder ftb = new FamilyTreeBuilder();
        if(ftb.processInput("cheng.txt")) {
            System.out.println("Well Done");
        } else {
            System.out.println("Failed");
        }
    }
    
}
