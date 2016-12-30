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
package com.ccdev.printout;

import com.ccdev.famtree.bean.Individual;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ccheng
 */
public class FamilyTreeNode {
    private Individual individual;
    private FamilyTreeNode father;
    
    private boolean leaf = false;
    private List<FamilyTreeNode> children = new ArrayList<>();;

    public FamilyTreeNode(Individual ind) {
        this.individual = ind;
    }

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    public FamilyTreeNode getFather() {
        return father;
    }

    public void setFather(FamilyTreeNode father) {
        this.father = father;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public void addChild(FamilyTreeNode child) {
        this.children.add(child);
    }
    
    public List<FamilyTreeNode> getChildren() {
        return this.children;
    }
    
    public FamilyTreeNode getFirstChild() {
        for(FamilyTreeNode node : this.children) {
            return node;
        }
        
        return null;
    }
    
    public FamilyTreeNode getNextSibling() {
        if(this.father == null) return null;

        boolean found = false;
        for(FamilyTreeNode node : this.father.children) {
            if(found) return node;
            if(node.equals(this)) found = true;
        }

        return null;
    }

    public FamilyTreeNode getPreviousSibling() {
        if(this.father == null) return null;

        FamilyTreeNode lastNode = null;
        for(FamilyTreeNode node : this.father.children) {
            if(node.equals(this)) break;
            lastNode = node;
        }

        return lastNode;
    }

    public boolean isRoot() {
        return this.father == null;
    }
    
    public int getLevel() {
        return this.individual.getGen() - 1;
    }
}
