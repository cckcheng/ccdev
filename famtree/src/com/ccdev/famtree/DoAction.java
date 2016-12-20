package com.ccdev.famtree;

import javax.servlet.http.HttpServletRequest;
import javax.persistence.EntityManager;
import com.ccdev.famtree.bean.Users;

public interface DoAction {
	public String doAction(Users user,HttpServletRequest request,EntityManager em);
}


