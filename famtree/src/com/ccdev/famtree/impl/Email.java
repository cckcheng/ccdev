/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ccdev.famtree.impl;

import com.ccdev.famtree.Macro;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *	All email related functions
 *
 * @author Colin Cheng
 */
@SuppressWarnings(value = {"unchecked"})
public class Email {
	static public void send(String from, String to, String subject, String msg, String attachFile) {
		EmailThread emailThread = new EmailThread(from, to, attachFile, msg, subject);
		emailThread.start();
	}

	static public void alertAdmin(String subject, String msg, EntityManager em) {
		myUtil.dbg(1, subject + "\n" + msg);
		if(Macro.PASSWORD_UNCHECK) return;
		send("noreply" + Macro.EMAIL_AFFIX, adminEmails(em), subject, msg, null);
	}

	static public String adminEmails(EntityManager em) {
//		if(Macro.PASSWORD_UNCHECK) return "ccheng" + Macro.EMAIL_AFFIX;

		String q = "Select username from user_tbl Where level=" + Macro.ADMIN_LEVEL;
		List<Object> rs = em.createNativeQuery(q).getResultList();
		StringBuilder sb = new StringBuilder();
		for(Object o : rs) {
			String username = StringFunc.TrimedString(o);
			if(username.length() == 0) continue;
			sb.append(",").append(username);
			if(!username.contains("@")) {
				sb.append(Macro.EMAIL_AFFIX);
			}
		}
		
		if(sb.length() > 0) return sb.substring(1);
		// should not happen, 1 admin must exist
		myUtil.dbg(1, "No Admin!!!");
		return "";
	}
}
