package com.ccdev.famtree;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.naming.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ccdev.famtree.bean.Users;
import com.ccdev.famtree.impl.myUtil;
import javax.ejb.EJB;

@SuppressWarnings("serial")
public class uploadServlet extends HttpServlet {
    @EJB(name = "java:app/biosgen/ActionBean!services.ejb.Action")
    private Action action;

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		//String uname = request.getParameter("username");
		ServletContext context = getServletConfig().getServletContext();
		response.setContentType("text/html; charset=gb2312");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(false);
		if (session == null) {
			out.write(myUtil.actionFail("Time out", Macro.FAILCODE_TIMEOUT));
			return;
		}
		Users user = (Users) session.getAttribute("user");
		if (user == null) {
			out.write(myUtil.actionFail("Time out", Macro.FAILCODE_TIMEOUT));
			return;
		}
		myUtil.dbg(5, "--> uploadServlet start - " + user.getUsername());

		session.setAttribute("time", new Date().getTime());
		try {
			InitialContext ctx = new InitialContext();
			myUtil.dbg(5, "--> after new InitialContext -" );
//			Action action = (Action) ctx.lookup("famtree/ActionBean/local");
			myUtil.dbg(5, "--> before action.doUpload -" );
			String res = action.doUpload(user, request);
			//out.write("{success:true}");
			out.write(res);

		} catch (Exception e) {
			e.printStackTrace();
			out.write(myUtil.actionFail("System Error 001!") + myUtil.traceException(e));
			return;
		}

	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uname = request.getParameter("username");

		ServletContext context = getServletConfig().getServletContext();
		ServletOutputStream op = response.getOutputStream();
		HttpSession session = request.getSession(false);
		if (session == null) {
			op.print(myUtil.actionFail("Time out", Macro.FAILCODE_TIMEOUT));
			return;
		}
		Users user = (Users) session.getAttribute("user");
		if (user == null) {
			op.print(myUtil.actionFail("Time out", Macro.FAILCODE_TIMEOUT));
			return;
		}

		session.setAttribute("time", new Date().getTime());
		try {
			InitialContext ctx = new InitialContext();
//			Action action = (Action) ctx.lookup("famtree/ActionBean/local");

			action.download(user, request, response, context, op);
			return;

		} catch (Exception e) {
			e.printStackTrace();
			op.print(myUtil.actionFail("System Error 001!") + myUtil.traceException(e));
			return;
		}

	}
}
