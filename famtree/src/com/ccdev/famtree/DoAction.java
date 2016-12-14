package com.ccdev.famtree;

import javax.servlet.http.HttpServletRequest;
import javax.persistence.EntityManager;
import com.ccdev.famtree.bean.UserTbl;

public interface DoAction {
	public String doAction(UserTbl user,HttpServletRequest request,EntityManager em);
}


