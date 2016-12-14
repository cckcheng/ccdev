package com.ccdev.famtree;

import javax.naming.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Date;

import org.apache.log4j.PropertyConfigurator;

import com.ccdev.famtree.bean.UserTbl;
import com.ccdev.famtree.impl.StringFunc;
import com.ccdev.famtree.impl.myUtil;
import java.util.Hashtable;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

@SuppressWarnings("serial")
public class loginServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ServletConfig config = getServletConfig();
		String isRelativePosition = config.getInitParameter("relativePosition");
		String logConfiguration = config.getInitParameter("logConfiguration");
		String root = "";
		if (isRelativePosition.equals("true")) {
			root = config.getServletContext().getRealPath("/");
		}
		PropertyConfigurator.configure(root + logConfiguration);

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html>");

		String uname = request.getParameter("user");
		String passwd = request.getParameter("password");
		String url = StringFunc.getSubPathTrimRight(request.getRequestURL().toString());
		if (uname.indexOf("@")<0){
			out.println("<head>");
			out.println("<meta http-equiv=\"REFRESH\" content=\"0;url=" + url + "\"></HEAD>");
			out.println("<body></body></html>");
			return;
		}

		try {
			HttpSession session = request.getSession(true);
			InitialContext ctx = new InitialContext();
			Action action = (Action) ctx.lookup("famtree/ActionBean/local");
			StringBuilder msg = new StringBuilder();
			UserTbl user = action.login(uname, passwd, msg);
			if (user == null) {
				return;
			}else if (msg.length()>0){
				return;
			}

			if(user != null && user.getInused() < 0) {	// only for the first time users, otherwiss direct to login page
				session.setAttribute("user", user);
				session.setAttribute("config", config);
				Date d = new Date();
				session.setAttribute("time", d.getTime());
				action.updateLastLogin(user);
//				Cookie cookie = new Cookie("role", "" + user.getLevel());
//				response.addCookie(cookie);
			}
			out.println("<head>");
			out.println("<meta http-equiv=\"REFRESH\" content=\"0;url=" + url + "\"></HEAD>");
			out.println("<body></body></html>");
		} catch (Exception e) {
			e.printStackTrace();
			out.println("<body>");
			out.println("System Error 001!");
			out.println("</body></html>");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		ServletConfig config = getServletConfig();
		String isRelativePosition = config.getInitParameter("relativePosition");
		String logConfiguration = config.getInitParameter("logConfiguration");
		String root = "";
		if (isRelativePosition.equals("true")) {
			root = config.getServletContext().getRealPath("/");
		}
		PropertyConfigurator.configure(root + logConfiguration);

		response.setContentType("text/json; charset=gb2312");
		PrintWriter out = response.getWriter();
		String act = request.getParameter("act");
		if (act == null || act.equals("logout")) {
			myUtil.dbg(5, "=============>logout<=========");
			HttpSession session = request.getSession(true);
			if (session != null) {
				myUtil.dbg(5, "=============>logout: invalid session<=========");
				session.invalidate();
			}
			out.write(myUtil.actionSuccess());
			return;

		}
		if (act.equals("getglobal")) {

			try {
				HttpSession session = request.getSession(true);
				InitialContext ctx = new InitialContext();
				Action action = (Action) ctx.lookup("famtree/ActionBean/local");
				if (session == null) {
					out.write(myUtil.actionFail("Time out", Macro.FAILCODE_TIMEOUT));
					return;
				}
				UserTbl user = (UserTbl) session.getAttribute("user");
				if (user == null) {
					out.write(myUtil.actionFail("System error", Macro.FAILCODE_TIMEOUT));
					return;
				}
				String rt = action.login_success(user);
				out.write(rt);
				return;
			} catch (Exception e) {
				e.printStackTrace();
				out.write(myUtil.actionFail("System Error 001!"));
				return;
			}

		}

		String uname = request.getParameter("user");
		String passwd = request.getParameter("password");

		String errMsg = "User name or password error!";
		if (uname.indexOf("@")<0){
			if (!LDAP(uname, passwd, config)) {
				out.write(myUtil.actionFail(errMsg));
				return;
			}
			errMsg = "'" + uname + "' is not in current famtree user list.<br>Please contact administrator!";
		}

		try {
			HttpSession session = request.getSession(true);
			InitialContext ctx = new InitialContext();
			Action action = (Action) ctx.lookup("famtree/ActionBean/local");
			StringBuilder msg = new StringBuilder();
			UserTbl user = action.login(uname, passwd, msg);
			if (user == null) {
				out.write(myUtil.actionFail(errMsg));
				return;
			}else if (msg.length()>0){
				out.write(myUtil.actionFail(msg.toString()));
				return;
			}
			
			action.updateLastLogin(user);
			session.setAttribute("user", user);
			session.setAttribute("config", config);
			Date d = new Date();
			session.setAttribute("time", d.getTime());
//			Cookie cookie = new Cookie("role", "" + user.getLevel());
//			response.addCookie(cookie);
			String rt = action.login_success(user);
			out.write(rt);
			return;

		} catch (Exception e) {
			e.printStackTrace();
			out.write(myUtil.actionFail("System Error 001!"));
			return;
		}
	}

	private static final boolean LDAP(String user, String password, ServletConfig config) {
		if (Macro.PASSWORD_UNCHECK) {
			return true;
		}
		String ldap_url = config.getInitParameter("url");
		String ldap_authentication = config.getInitParameter("authentication");
		String ldap_principal = config.getInitParameter("principal");

		myUtil.dbg(6, "url=" + ldap_url);
		myUtil.dbg(6, "auth=" + ldap_authentication);
		myUtil.dbg(6, "principal=" + ldap_principal);

		myUtil.dbg(5, user + "-------------using ldap--------" + new Date());
		Hashtable<String, String> env = new Hashtable<String, String>(11);
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldap_url);
		env.put(Context.SECURITY_AUTHENTICATION, ldap_authentication);
		env.put(Context.SECURITY_PRINCIPAL, "uid=" + user + ", " + ldap_principal);
		env.put(Context.SECURITY_CREDENTIALS, password);
		try {
			DirContext ctx = new InitialDirContext(env);
			ctx.close();
			return true;
		} catch (AuthenticationException e) {
			return false;
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		}
	}
}
