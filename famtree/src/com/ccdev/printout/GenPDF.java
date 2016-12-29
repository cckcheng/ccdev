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
import com.ccdev.famtree.bean.Pedigree;
import com.ccdev.famtree.impl.myUtil;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Colin Cheng
 */
public class GenPDF {
    private StringBuilder err = new StringBuilder();
    
    private int genPerPage = 5;
    private FamilyTreeNode root;
    private Pedigree pedigree;
    
    private String indTable;
    private final EntityManager em;
    public GenPDF(EntityManager em) {
        this.em = em;
    }

    public String getError() {
        return this.err.toString();
    }
    
    public boolean printOut(Pedigree ped, Individual rootInd, File outFile) {
        this.pedigree = ped;
        this.root = new FamilyTreeNode(rootInd);
        
        this.indTable = ped.getIndividualTable();

        TreeToPDF toPDF = new TreeToPDF(this, root, null);
        if(ped.getPrintLayout() != null) toPDF.parseConfigStr("layout=" + ped.getPrintLayout());
        if(!toPDF.generatePDF(outFile.getName())) {
            this.err.append(toPDF.getErrorMessage());
            return false;
        }

        String q = "Insert into pedigree_printout (pedigree_id,content,print_time) values(?1,compress(?2),now())"
                + " ON DUPLICATE KEY UPDATE content=compress(?2),print_time=now()";
        try {
            Query query = em.createNativeQuery(q);
            query.setParameter(1, ped.getId());
            query.setParameter(2, Files.readAllBytes(outFile.toPath()));
            query.executeUpdate();

            outFile.delete();
        } catch (Exception ex) {
            ex.printStackTrace();
            this.err.append(ex.getMessage());
            return false;
        }

        return true;
    }
    
    public boolean loadTree(FamilyTreeNode node, boolean first) {
        List<FamilyTreeNode> curList = new ArrayList<>();
        curList.add(node);
        for(int i=(first ? 1 : 0); i<genPerPage; i++) {
            List<FamilyTreeNode> nextList = new ArrayList<>();
            for(FamilyTreeNode nd : curList) {
                int nChild = loadChildren(nd);
                if(nChild == 0) continue;
                nextList.addAll(nd.getChildren());
            }
            if(nextList.isEmpty()) {
                curList.clear();
                break;
            }
            curList = nextList;
        }
        
        for(FamilyTreeNode nd : curList) {
            checkChildren(nd);
        }
        return true;
    }

    private int loadChildren(FamilyTreeNode node) {
        String q = "Select " + myUtil.INDIVIDUAL_FIELDS + " From " + this.indTable
                + " where father_id=" + node.getIndividual().getId()
                + " order by seq,id";
        List<Object[]> rs = em.createNativeQuery(q).getResultList();
        if(rs.isEmpty()) {
            node.setLeaf(true);
            return 0;
        }
        
        int total = 0;
        for(Object[] o : rs) {
            FamilyTreeNode nd = new FamilyTreeNode(myUtil.toIndividual(o));
            nd.setFather(node);
            node.addChild(nd);
            total++;
        }
        
        return total;
    }
    
    private int checkChildren(FamilyTreeNode node) {
        String q = "Select count(id) From " + this.indTable
                + " where father_id=" + node.getIndividual().getId();
        
        int total = myUtil.getCountBySQL(q, em);
        node.setLeaf(total == 0);
        
        return total;
    }
    
    /**
     * @param args the command line arguments
     */
    /*
    public static void main(String[] args) throws UnsupportedEncodingException,
			DocumentException, FileNotFoundException, IOException {
		if(args.length < 1) {
			System.out.println("Usage: java GenPDF gedcomFile -p params");
			System.exit(1);
		}

		String gedcomName = args[0];
		int idx = gedcomName.lastIndexOf(".");
		if(idx<0) {
			System.out.println("Invalid gedcomFile");
			System.exit(2);
		}
		if(!gedcomName.substring(idx).equalsIgnoreCase(".ged")) {
			System.out.println("Invalid gedcomFile");
			System.exit(2);
		}
		FamilyTree famTree = new FamilyTree();
		if(args.length > 2) {
			String param = URLDecoder.decode(args[2], "UTF-8");
			int x0 = param.indexOf("gedkey=") + 7;
			int x1 = param.indexOf("&", x0);
			if(x1 < x0) param = param.substring(x0);
			else param = param.substring(x0, x1);
			String[] pp = param.split(",");
			for(String p : pp) {
				famTree.addGedKey(p);
			}
		}
		famTree.buildFamilyTree(gedcomName);
		if(famTree.hasError()) {
			System.out.println(famTree.getErrorMessage());
			if(famTree.isFatal()) System.exit(-1);
		}
//		famTree.printTree();	// for test

		TreeToPDF toPDF = new TreeToPDF(famTree.getRoot(), famTree.getIndividualList());

		if(args.length > 1) {
			if(args[1].equalsIgnoreCase("-p")) {
				if(args.length > 2) {
//					System.out.println(URLEncoder.encode(args[2], "UTF-8"));
					String param = URLDecoder.decode(args[2], "UTF-8");
//					System.out.println(param);
					toPDF.parseConfigStr(param);
				}
			} else if(!toPDF.parseConfig(args[1])) {
				System.out.println(toPDF.getErrorMessage());
				System.exit(3);
			}
		}

		String fname = gedcomName.substring(0, idx);
		toPDF.generatePDF(fname + ".pdf");
//		toPDF.testPDF();
		if(toPDF.hasError()) {
			System.out.println(toPDF.getErrorMessage());
			System.exit(-1);
		}
    }*/
}
