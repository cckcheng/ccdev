package com.ccdev.famtree;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletContext;
import com.ccdev.famtree.bean.*;

public interface Action {
	public Users login(String uname, String password, StringBuilder msg);
	public Users findUser(Long user_id);
	public String login_success(Users user);
	public String doAction(Users user,HttpServletRequest request);
	public String doUpload(Users suer,HttpServletRequest request);
	public void disableUser(Users user);
	public void updateLastLogin(Users user);
	public void download(Users suer,HttpServletRequest request,HttpServletResponse response,ServletContext context,ServletOutputStream op);
        public void loadConfig();
}

