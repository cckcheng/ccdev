package com.ccdev.famtree;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.ccdev.famtree.impl.myUtil;
import com.ccdev.famtree.impl.StringFunc;
import com.ccdev.famtree.bean.*;
import java.util.Date;
import java.util.Enumeration;
import javax.ejb.EJB;

@SuppressWarnings("serial")
public class actionServlet extends HttpServlet {
    @EJB(name = "java:app/biosgen/ActionBean!services.ejb.Action")
    private Action action;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		HttpSession session = request.getSession(true);
		if (session == null) {
			response.getWriter().write(myUtil.actionFail("Time out", Macro.FAILCODE_TIMEOUT));
			return;
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/json; charset=utf-8");
		PrintWriter out = response.getWriter();
		HttpSession session = null;
		UserTbl user = null;
		boolean trustConnection = myUtil.BooleanNullToFalse(request.getHeader("trustConnection"), "true");
		if(!trustConnection) {
			session = request.getSession(false);
			if (session == null) {
				out.write(myUtil.actionFail("Session time out", Macro.FAILCODE_TIMEOUT));
				return;
			}

			user = (UserTbl) session.getAttribute("user");
			if (user == null) {
				out.write(myUtil.actionFail("Time out", Macro.FAILCODE_TIMEOUT));
				return;
			}

			Date d = new Date();
			System.out.println("\n***Version: " + Macro.version + "; User: " + user.getUsername() + " (" +
					user.getFullname() + ") @ " + d.toString());

		}

		ServletConfig config = this.getServletConfig();

		try {
			InitialContext ctx = new InitialContext();
//			Action action = (Action) ctx.lookup("famtree/ActionBean/local");

			if(trustConnection) {
				Long user_id = myUtil.LongWithNullToZero(request.getHeader("user_id"));
				if(user_id == 0L || (user = action.findUser(user_id)) == null) {
					out.write("Invalid User");
					return;
				}
			}
			String res = action.doAction(user, request);
			out.write(res);

		} catch (Exception e) {
			e.printStackTrace();
			out.write(myUtil.actionFail("System Error 001!") + myUtil.traceException(e));
			return;
		}

	}
}
