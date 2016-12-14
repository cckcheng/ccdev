package com.ccdev.famtree;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletContext;
import com.ccdev.famtree.bean.*;

public interface Action {
	public UserTbl login(String uname, String password, StringBuilder msg);
	public UserTbl findUser(Long user_id);
	public String login_success(UserTbl user);
	public String doAction(UserTbl user,HttpServletRequest request);
	public String doUpload(UserTbl suer,HttpServletRequest request);
	public void disableUser(UserTbl user);
	public void updateLastLogin(UserTbl user);
	public void download(UserTbl suer,HttpServletRequest request,HttpServletResponse response,ServletContext context,ServletOutputStream op);
}

