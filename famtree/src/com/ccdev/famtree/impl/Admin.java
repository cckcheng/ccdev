package com.ccdev.famtree.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.ccdev.famtree.bean.*;
import com.ccdev.famtree.DoAction;
import com.ccdev.famtree.Macro;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@SuppressWarnings(value = {"unchecked"})
public class Admin implements DoAction {

	private UserLog log = null;
	private Random generator = new Random(System.currentTimeMillis());
	private static final String charset = "01234567890abcdefghijklmnopqrstuvwxyz";

	public String doAction(UserTbl user, HttpServletRequest request, EntityManager em) {
		String result = "";
		String act = request.getParameter("action");

		if (act.equalsIgnoreCase("getUserList")) {
			result = getUserList(user, request, em);
		} else if (act.equalsIgnoreCase("getUser")) {
			result = getUser(user, request, em);
		} else if (act.equalsIgnoreCase("getUserByUsername")) {
			result = getUserByUsername(user, request, em);
		} else if (act.equalsIgnoreCase("editUser")) {
			log = myUtil.log(user, request, em);
			result = editUser(user, request, em);
		} else if (act.equalsIgnoreCase("addUser")) {
			log = myUtil.log(user, request, em);
			result = addUser(user, request, em);
		} else if (act.equalsIgnoreCase("getGroupList")) {
			result = getGroupList(user, request, em);
		} else if (act.equalsIgnoreCase("getGroupsByUser")) {
			result = getGroupsByUser(user, request, em);
		} else if (act.equalsIgnoreCase("getUsersByGroup")) {
			result = getUsersByGroup(user, request, em);
		} else if (act.equalsIgnoreCase("getGroup")) {
			result = getGroup(user, request, em);
		} else if (act.equalsIgnoreCase("addGroup")) {
			log = myUtil.log(user, request, em);
			result = addGroup(user, request, em);
		} else if (act.equalsIgnoreCase("editGroup")) {
			log = myUtil.log(user, request, em);
			result = editGroup(user, request, em);
		} else if (act.equalsIgnoreCase("removeGroup")) {
			log = myUtil.log(user, request, em);
			result = removeGroup(user, request, em);
		} else if (act.equalsIgnoreCase("changePassword")) {
			log = myUtil.log(user, request, em);
			result = changePassword(user, request, em);
		} else if (act.equalsIgnoreCase("resetPassword")) {
			log = myUtil.log(user, request, em);
			result = resetPassword(user, request, em);
		} else if (act.equalsIgnoreCase("removeUser")) {
			log = myUtil.log(user, request, em);
			result = removeUser(user, request, em);
		} else {
			result = myUtil.actionFail("unknown action:" + act, Macro.FAILCODE_IGNORE);
		}

		if (this.log != null) {
			this.log.setResult(result);
			em.merge(this.log);
		}
		return result;
	}

	private String getUser(UserTbl user, HttpServletRequest request, EntityManager em) {
		long id = myUtil.LongWithNullToZero(request.getParameter("id"));
		if (id == 0) {
			return myUtil.actionFail("Unknow Group ID");
		}

		UserTbl u = em.find(UserTbl.class, id);
		if (null == u) {
			return myUtil.actionFail("Unknow id:" + id);
		}
		JSONObject result1 = new JSONObject();
		result1.put("success", "true");

		JSONObject Obj = new JSONObject();

		Obj.put("id", u.getUserId());
		Obj.put("username", u.getUsername());
		Obj.put("name", u.getFullname());
		Obj.put("level", u.getLevel());
		JSONArray array_ingroups = new JSONArray();
		Collection<GroupTbl> groups =  u.getGroupTblCollection();
		for (GroupTbl g : groups) {
			JSONObject gObj = new JSONObject();
			gObj.put("id", g.getGroupId());
			gObj.put("name", g.getGroupname());
			array_ingroups.put(gObj);
		}
		Obj.put("inusers", array_ingroups);

		String q = "select * from group_tbl where group_id not in ";
		q += "(select group_id from group_user where user_id=" + id + ") and (disabled = 0 or disabled is null) order by groupname";
		Query query = em.createNativeQuery(q, GroupTbl.class);
		myUtil.dbg(5,q);
		ArrayList<GroupTbl> outgroups = (ArrayList<GroupTbl>) query.getResultList();
		JSONArray array_outgroups = new JSONArray();
		for (GroupTbl g : outgroups) {
			JSONObject uObj = new JSONObject();
			uObj.put("id", g.getGroupId());
			uObj.put("name", g.getGroupname());
			array_outgroups.put(uObj);
		}
		Obj.put("outusers", array_outgroups);
		result1.put("user", Obj);
		return result1.toString();
	}

	private JSONArray getUserList(int display_disabled, int shorted, EntityManager em) {
		String q = "select * from user_tbl where user_id>0 order by fullname";
		Query query = em.createNativeQuery(q, UserTbl.class);
		myUtil.dbg(5, q);
		ArrayList<UserTbl> users = (ArrayList<UserTbl>) query.getResultList();
		JSONArray user_list = new JSONArray();
		for (UserTbl user : users) {
			if ((display_disabled != 1) && (user.getDisabled()!=null) && (user.getDisabled()==1)) {
				continue;
			}
			JSONObject uObj = new JSONObject();
			uObj.put("id", user.getUserId());
			uObj.put("username", user.getUsername());
			if (shorted != 1) {
				uObj.put("level", user.getLevel());
				uObj.put("disabled", user.getDisabled()==null?0:user.getDisabled());
				uObj.put("onused", user.on_using(em));
				uObj.put("name", user.getFullname());
			}else{
				uObj.put("name", user.getFullname() + "(" + user.getUsername() + ")");
			}
			user_list.put(uObj);
		}
		return user_list;
	}

	private JSONArray getUserByUsername(String username, int display_disabled, int shorted, EntityManager em) {
		String q = "select * from user_tbl where user_id>0 and (username like '" + username + "'";
		q+=" or fullname like '"+username+"') order by fullname";
		Query query = em.createNativeQuery(q, UserTbl.class);
		ArrayList<UserTbl> users = (ArrayList<UserTbl>) query.getResultList();
		JSONArray user_list = new JSONArray();
		for (UserTbl user : users) {
			if ((display_disabled != 1) && (user.getDisabled()!=null) && (user.getDisabled()==1)) {
				continue;
			}
			JSONObject uObj = new JSONObject();
			uObj.put("id", user.getUserId());
			uObj.put("username", user.getUsername());
			if (shorted != 1) {
				uObj.put("level", user.getLevel());
				uObj.put("disabled", (user.getDisabled()==null)?0:user.getDisabled());
				uObj.put("onused", user.on_using(em));
				uObj.put("name", user.getFullname());
			}else{
				uObj.put("name", user.getFullname() + "(" + user.getUsername() + ")");
			}
			user_list.put(uObj);
		}
		return user_list;
	}

	private String getUserList(UserTbl user, HttpServletRequest request, EntityManager em) {
		int disabled = myUtil.IntegerWithNullToZero(request.getParameter("disabled"));
		int shorted = myUtil.IntegerWithNullToZero(request.getParameter("shorted"));
		JSONObject Obj = new JSONObject();
		Obj.put("users", getUserList(disabled, shorted, em));
		return Obj.toString();
	}
	private String getUserByUsername(UserTbl user, HttpServletRequest request, EntityManager em) {
		int disabled = myUtil.IntegerWithNullToZero(request.getParameter("disabled"));
		int shorted = myUtil.IntegerWithNullToZero(request.getParameter("shorted"));
		String username = StringFunc.TrimedString(request.getParameter("username"));
		JSONObject Obj = new JSONObject();
		Obj.put("users", getUserByUsername(username, disabled, shorted, em));
		return Obj.toString();
	}

	private String getGroupsByUser(UserTbl user, HttpServletRequest request, EntityManager em) {
		int id = myUtil.IntegerWithNullToZero(request.getParameter("id"));
		if (id == 0) {
			return myUtil.actionFail("Unknow ID");
		}
		JSONArray ar = new JSONArray();
		JSONObject ret_obj = new JSONObject();

		String q= "select a.group_id,a.groupname from group_tbl a join group_user b on a.group_id=b.group_id where b.user_id =" +id;
		Query query=em.createNativeQuery(q);
		List<Object[]> platforms = em.createNativeQuery(q).getResultList();
		for (Object[] o : platforms) {
			JSONObject obj = new JSONObject();
			obj.put("id", o[0]);
			obj.put("name", o[1]);
			ar.put(obj);
		}
		ret_obj.put("results", ar);
		return myUtil.actionSuccess(ret_obj);
	}


	private String addUser(UserTbl user, HttpServletRequest request, EntityManager em) {
		if (!have_permission(user, request, em)) {
			return myUtil.actionFail("Permission Denied!", Macro.FAILCODE_IGNORE);
		}
		String username = StringFunc.TrimedString(request.getParameter("username"));
		String name = StringFunc.TrimedString(request.getParameter("name"));

		if (username == null || username.length()==0) {
			return myUtil.actionFail("There are no display name!", Macro.FAILCODE_IGNORE);
		}
		if (myUtil.exists_check("select count(*) from user_tbl where username='" + username + "'", em)) {
			return myUtil.actionFail("login Username:" + username + " exists!", Macro.FAILCODE_IGNORE);
		}
		if (username.length() > Macro.MAX_STRLEN) {
			return myUtil.actionFail("The username maxium length is " + Macro.MAX_STRLEN, Macro.FAILCODE_IGNORE);
		}

		char[] spcs = {'\'', '\\', '%'};
		if (myUtil.haveSpecialChar(username, spcs) != 0) {
			return myUtil.actionFail("UserTbl name hace spcial charater[',%,\\]!", Macro.FAILCODE_IGNORE);
		}

		if (name == null || name.length()==0) {
			return myUtil.actionFail("There are no login name!");
		}
		if (name.length() > Macro.MAX_STRLEN) {
			return myUtil.actionFail("There name maxium length is " + Macro.MAX_STRLEN);
		}
		if (myUtil.haveSpecialChar(name, spcs) != 0) {
			return myUtil.actionFail("Name hace spcial charater[',%,\\]!", Macro.FAILCODE_IGNORE);
		}

		String level_s = request.getParameter("level");
		int level = Macro.OTHER_LEVEL;
		if (null != level_s && level_s.equals("on")) {
			level = Macro.ADMIN_LEVEL;
			if (user.getLevel() != Macro.ADMIN_LEVEL)
				return myUtil.actionFail("Permission deny to set administrator!", Macro.FAILCODE_IGNORE);
		}

		String password = generatePassword(8);

		int inused = 0;
		if(username.indexOf("@") >= 0){
			inused = -1;
		} else {
			if (!sendNewFortineterEmail( username ,  name))
				return myUtil.actionFail("Username Error!", Macro.FAILCODE_IGNORE);

		}

		String q="insert into user_tbl(username, password, fullname, level, inused, disabled)" +
				" values('" + username + "',MD5('" + password + "'),'" + name + "'," + level + "," + inused + ",0)";
		if(!myUtil.execDBUpdate(q, em)) {
			return myUtil.actionFail(Macro.ERR_DB_UPDATE);
		}
		q = "select last_insert_id()";
		Query qy = em.createNativeQuery(q);
		Object last_id = qy.getSingleResult();
		long new_id = Long.parseLong(last_id.toString());
		myUtil.dbg(5, "new_id=" + new_id);

		if(username.indexOf("@") > 0){
			if(!this.sendWelcomeEmail(request, username, name, password)){
				return myUtil.actionFail(Macro.ERR_SEND_EMAIL);
			}
		}

		String groups = request.getParameter("groups");
		if (groups != null && (!groups.equals(""))) {
			q= "insert into group_user (user_id,group_id) select " + new_id + ", group_id  from group_tbl where group_id in ("+groups+")";
			myUtil.dbg(3, q);
			qy =  em.createNativeQuery(q);
			qy.executeUpdate();
		}

		String dowhat = "add User:" + name+"level:"+level;
		myUtil.audit(user, Macro.ACT_ADMIN, 0L, dowhat, em);

		return myUtil.actionSuccess();
	}

	private String editUser(UserTbl user, HttpServletRequest request, EntityManager em) {
		if (!have_permission(user, request, em)) {
			return myUtil.actionFail("Permission Denied!", Macro.FAILCODE_IGNORE);
		}

		int disabled = myUtil.IntegerNullToMinusOne(request.getParameter("disabled"));
		Long id = myUtil.LongWithNullToZero(request.getParameter("id"));
		if (id == 0) {
			return myUtil.actionFail("missing id");
		}
		String username = StringFunc.TrimedString(request.getParameter("username"));

		String level_s = request.getParameter("level");
		int level = Macro.OTHER_LEVEL;
		if (null != level_s && level_s.equals("on")) {
			level = Macro.ADMIN_LEVEL;
			if (user.getLevel() != Macro.ADMIN_LEVEL)
				return myUtil.actionFail("Permission deny to set administrator!", Macro.FAILCODE_IGNORE);
		}

		UserTbl u = em.find(UserTbl.class, id);
		if (null == u) {
			return myUtil.actionFail("Unknow id:" + id);
		}

		char[] spcs = {'\'', '\\', '%'};

		if (username != null && username.length()>0) {
			if (myUtil.exists_check("Select count(*) from user_tbl where username='" + username + "' and user_id!=" + id, em)) {
				return myUtil.actionFail("Login username:" + username + " exists!", Macro.FAILCODE_IGNORE);
			}

			if (myUtil.haveSpecialChar(username, spcs) != 0) {
				return myUtil.actionFail("UserTbl name has special character[',%,\\]!", Macro.FAILCODE_IGNORE);
			}
			u.setUsername(username);
		}

		String name = StringFunc.TrimedString(request.getParameter("name"));

		if (name != null && name.length()>0) {

			if (myUtil.haveSpecialChar(name, spcs) != 0) {
				return myUtil.actionFail("Name has special character[',%,\\]!", Macro.FAILCODE_IGNORE);
			}
			u.setFullname(name);
		}

		if (level > 0) {
			if (level == Macro.ADMIN_LEVEL && user.getLevel() != Macro.ADMIN_LEVEL)
				return myUtil.actionFail("Permission deny to set administrator!", Macro.FAILCODE_IGNORE);
			u.setLevel(level);
		}

		if (disabled > -1 && disabled < 2) {
			int original_disabled = (u.getDisabled()==null)?0: u.getDisabled();
			if (disabled == 0 && original_disabled == 1){
				u.setLastLogin(new Date());
			}
			u.setDisabled(disabled);

		}

		String q= "delete from group_user where user_id ="+id;
		myUtil.dbg(3, q);
		Query query =  em.createNativeQuery(q);
		query.executeUpdate();

		String groups = request.getParameter("groups");
		if (groups != null && (!groups.equals(""))) {
			q= "insert into group_user (user_id,group_id) select " + id + ", group_id  from group_tbl where group_id in ("+groups+")";
			myUtil.dbg(3, q);
			query =  em.createNativeQuery(q);
			query.executeUpdate();
		}
		String dowhat = "edit User:" + name+" level:"+level;
		em.merge(u);
		myUtil.audit(user, Macro.ACT_ADMIN, 0L, dowhat, em);

		return myUtil.actionSuccess();
	}

	private String removeUser(UserTbl user, HttpServletRequest request, EntityManager em) {
		if (!have_permission(user, request, em)) {
			return myUtil.actionFail("Permission Denied!", Macro.FAILCODE_IGNORE);
		}
		Long id = myUtil.LongWithNullToZero(request.getParameter("id"));
		if (id == 0) {
			return myUtil.actionFail("missing id");
		}
		UserTbl u = em.find(UserTbl.class, id);
		if (null == u) {
			return myUtil.actionFail("Unknow id:" + id);
		}
		if (u.on_using(em)) return myUtil.actionFail("The user is used by other object", Macro.FAILCODE_IGNORE);
		String dowhat = "remove User:" + u.getUsername();
		em.remove(u);
		myUtil.audit(user, Macro.ACT_ADMIN, 0L, dowhat, em);

		return myUtil.actionSuccess();
	}

	private String changePassword(UserTbl user, HttpServletRequest request, EntityManager em) {

		Long id = user.getUserId();
		myUtil.dbg(5, "" +id);

		String oldPass =request.getParameter("oldpassword");
		if (oldPass != null) {
			String q = "select count(*) from user_tbl where password = MD5('" + oldPass + "') and user_id=" +id;
			if(!myUtil.exists_check(q, em)) {
				return myUtil.actionFail("Password not right!", Macro.FAILCODE_IGNORE);
			}
		}

		String newPass = request.getParameter("newpassword");
		String q = "update user_tbl set password=MD5('" + newPass + "'),inused=0 where user_id=" + id;
		if(user.getInused() < 0) user.setInused(0);
		return myUtil.doDBUpdate(q, em);
	}
	private String resetPassword(UserTbl user, HttpServletRequest request, EntityManager em) {
		if (!have_permission(user, request, em)) {
			return myUtil.actionFail("Permission Denied!", Macro.FAILCODE_IGNORE);
		}
		Long id = myUtil.LongWithNullToZero(request.getParameter("id"));
		if (id == 0) {
			return myUtil.actionFail("missing id");
		}
		UserTbl u = em.find(UserTbl.class, id);
		if (null == u) {
			return myUtil.actionFail("Unknow id:" + id);
		}
		String username = u.getUsername();
		String name = u.getFullname();
		String password = generatePassword(8);

		int inused = -1;
		if(username.indexOf("@") < 0){
			inused = 0;
		}
		String q = "update user_tbl set password=MD5('" + password + "'),inused=" + inused + " ,last_login=null" +" where user_id=" + id;
		if(!myUtil.execDBUpdate(q, em)) {
			return myUtil.actionFail(Macro.ERR_DB_UPDATE);
		}
		if(username.indexOf("@") > 0){
			if(!this.sendWelcomeEmail(request, username, name, password)){
				return myUtil.actionFail(Macro.ERR_SEND_EMAIL);
			}
		}
		return myUtil.actionSuccess();
	}
	private boolean sendNewFortineterEmail(String mailto, String fullname){
		String subject = "Account Creation at Fortinet Vendor Portal (For Fortinet Internal User)";
		String url = Macro.PUBLIC_DOMAIN;
		String msg = "Dear " + fullname + ":\n\n"
				+ "An account has been setup for you at Fortinet's Vendor Portal."
				+ " You can access the website: " + url
				+ "\nBy using username:" + mailto + " and with your Fortinet password\n\n"
				+ "Please do not reply to this email.\n\n"
				+ "Thank you.\n\n"
				+ "Fortinet Vendor Portal Admin";

		if (myUtil.sendEmail("noreply"+ Macro.EMAIL_AFFIX, mailto + Macro.EMAIL_AFFIX, null, msg, subject)) return true;
		else return false;
	}

	private boolean sendWelcomeEmail(HttpServletRequest request, String mailto, String fullname, String password){
		String subject = "Account Creation at Fortinet Vendor Portal";
//		String url = StringFunc.getSubPathTrimRight(request.getRequestURL().toString());
		String url = Macro.PUBLIC_DOMAIN;
		String msg = "Dear " + fullname + ":\n\n"
				+ "An account has been setup for you at Fortinet's Vendor Portal."
				+ "  Please click the following link to change your password:\n\n"
				+ url + "/loginServlet?user=" + mailto + "&password=" + password + "\n\n"
				+ "Once you change your password, you can login to the system at:\n\n " + url + "\n\n"
				+ "Please do not reply to this email.\n\n"
				+ "Thank you.\n\n"
				+ "Fortinet Vendor Portal Admin";

		EmailThread eThread = new EmailThread("vendor-admin" + Macro.EMAIL_AFFIX, mailto, null, msg, subject);
		eThread.start();
		return true;
	}

	private String generatePassword(int length){
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<length; i++) {
			int pos = generator.nextInt(charset.length());
			sb.append(charset.charAt(pos));
		}
		return sb.toString();
	}
	private boolean have_permission(UserTbl user, HttpServletRequest request, EntityManager em) {
		if (user.getLevel() == Macro.ADMIN_LEVEL) {
			return true;
		}
		UserTbl usr = em.find(UserTbl.class, user.getUserId());

		Collection<GroupTbl> groups =  usr.getGroupTblCollection();
		for (GroupTbl g : groups) {
			if ((g.getUserMask() & Macro.MODULE_ADMIN)>0) return true;
		}

		return false;
	}

	private JSONArray getGroups(int display_disabled, int shorted, EntityManager em) {

		String q = "select * from group_tbl order by groupname";
                myUtil.dbg(5, q);
		Query query = em.createNativeQuery(q, GroupTbl.class);
		ArrayList<GroupTbl> outgroups = (ArrayList<GroupTbl>) query.getResultList();
		JSONArray group_list = new JSONArray();
		for (GroupTbl group : outgroups) {
			if ((display_disabled != 1) && (group.getDisabled()==1)) {
				continue;
			}
			JSONObject grpObj = new JSONObject();
			String gname = group.getGroupname();
			grpObj.put("id", group.getGroupId());
			grpObj.put("name", gname);
			if (shorted != 1) {
				grpObj.put("descript", group.getDescription());
				grpObj.put("user_mask", group.getUserMask());
				grpObj.put("manager_mask", group.getManagerMask());
				grpObj.put("disabled", group.getDisabled());
				grpObj.put("on_using", group.on_using(em) ? 1 : 0);
			}
			group_list.put(grpObj);
		}

		return group_list;
	}

	private String getGroupList(UserTbl user, HttpServletRequest request, EntityManager em) {
		int disabled = myUtil.IntegerWithNullToZero(request.getParameter("disabled"));
		int shorted = myUtil.IntegerWithNullToZero(request.getParameter("shorted"));
		JSONObject Obj = new JSONObject();
		Obj.put("groups", getGroups(disabled, shorted, em));
		return Obj.toString();
	}


	private String getUsersByGroup(UserTbl user, HttpServletRequest request, EntityManager em) {
		int id = myUtil.IntegerWithNullToZero(request.getParameter("id"));
		if (id == 0) {
			return myUtil.actionFail("Unknow ID");
		}
		JSONArray ar = new JSONArray();
		JSONObject ret_obj = new JSONObject();

		String q= "select a.user_id,a.username from user_tbl a join group_user b on a.user_id=b.user_id where b.group_id =" +id;
		Query query=em.createNativeQuery(q);
		//List<Object[]> platforms = em.createNativeQuery(q).getResultList();
		List<Object[]> platforms = query.getResultList();
		for (Object[] o : platforms) {
			JSONObject obj = new JSONObject();
			obj.put("id", o[0]);
			obj.put("name", o[1]);
			ar.put(obj);
		}
		ret_obj.put("results", ar);
		return myUtil.actionSuccess(ret_obj);
	}


	private String getGroup(UserTbl user, HttpServletRequest request, EntityManager em) {
		long id = myUtil.LongWithNullToZero(request.getParameter("id"));
		if (id == 0) {
			return myUtil.actionFail("Unknow Group ID");
		}

		GroupTbl g = em.find(GroupTbl.class, id);
		if (null == g) {
			return myUtil.actionFail("Unknow id:" + id);
		}

		JSONObject result1 = new JSONObject();
		result1.put("success", "true");

		JSONObject Obj = new JSONObject();

		Obj.put("id", g.getGroupId());
		Obj.put("name", g.getDescription());
		Obj.put("description", g.getDescription());
		JSONArray array_inusers = new JSONArray();
		String q = "select a.* from user_tbl a join group_user b on a.user_id= b.user_id where b.group_id ="+id;
		Query query = em.createNativeQuery(q, UserTbl.class);
		ArrayList<UserTbl> users = (ArrayList<UserTbl>) query.getResultList();
		for (UserTbl u : users) {
			JSONObject uObj = new JSONObject();
			uObj.put("id", u.getUserId());
			uObj.put("name", u.getFullname() + " (" + u.getUsername() + ")");
			array_inusers.put(uObj);
		}
		Obj.put("inusers", array_inusers);

		q = "select * from user_tbl where user_id not in ";
		q += "(select user_id from group_user where group_id=" + id + ") and (disabled=0 or disabled is null) order by fullname";
		myUtil.dbg(3, q);
		query = em.createNativeQuery(q, UserTbl.class);
		ArrayList<UserTbl> outusers = (ArrayList<UserTbl>) query.getResultList();
		JSONArray array_outusers = new JSONArray();
		for (UserTbl u : outusers) {
			JSONObject uObj = new JSONObject();
			uObj.put("id", u.getUserId());
			uObj.put("name", u.getFullname() + " (" + u.getUsername() + ")");
			array_outusers.put(uObj);
		}
		Obj.put("outusers", array_outusers);
		result1.put("group", Obj);
		return result1.toString();
	}

	private String addGroup(UserTbl user, HttpServletRequest request, EntityManager em) {
		String name = StringFunc.TrimedString(request.getParameter("name"));
		if (name == null || name.length()==0) {
			return myUtil.actionFail("There are no display name!");
		}
		if (name.length() > Macro.MAX_STRLEN) {
			return myUtil.actionFail("The group name maxium length is " + Macro.MAX_STRLEN);
		}

		char[] spcs = {'\'', '\\', '%'};
		if (myUtil.haveSpecialChar(name, spcs) != 0) {
			return myUtil.actionFail("Group name hace spcial charater[',%,\\]!", Macro.FAILCODE_IGNORE);
		}


		if (myUtil.exists_check("select count(*) from group_tbl where groupname='" + name + "'", em)) {
			return myUtil.actionFail("name:" + name + " exists!", Macro.FAILCODE_IGNORE);
		}

		if (!have_permission(user, request, em)) {
			return myUtil.actionFail("Permission Denied!", Macro.FAILCODE_IGNORE);
		}

		String descript = request.getParameter("descript");
		if (descript != null && descript.length() > Macro.MAX_STRLEN) {
			return myUtil.actionFail("The group descript maxium length is " + Macro.MAX_STRLEN);
		}

		GroupTbl g = new GroupTbl(name.trim(), descript);

		em.persist(g);

		long g_id = g.getGroupId();

		int manager_mask = myUtil.IntegerNullToMinusOne(request.getParameter("manager_mask"));
		if (manager_mask>=0) g.setManagerMask(manager_mask);
		int user_mask = myUtil.IntegerNullToMinusOne(request.getParameter("user_mask"));
		if (user_mask>=0) g.setUserMask(user_mask);
		em.merge(g);

		String users = request.getParameter("users");
		if (users != null && (!users.equals(""))) {
			String q= "insert into group_user (user_id,group_id) select user_id,"+g_id+" from user_tbl where user_id in ("+users+")";
			myUtil.dbg(3, q);
			Query query =  em.createNativeQuery(q);
			query.executeUpdate();
		}

		String dowhat = "add Group:" + name+"Users:"+users;
		myUtil.audit(user, Macro.ACT_ADMIN, 0L, dowhat, em);

		JSONObject result = new JSONObject();
		result.put("id", g_id);
		result.put("success", "true");
		return result.toString();


	}

	private String editGroup(UserTbl user, HttpServletRequest request, EntityManager em) {
		Long id = myUtil.LongWithNullToZero(request.getParameter("id"));
		String name = StringFunc.TrimedString(request.getParameter("name"));
		String desc = request.getParameter("descript");
		int disabled = myUtil.IntegerNullToMinusOne(request.getParameter("disabled"));
		int user_mask = myUtil.IntegerNullToMinusOne(request.getParameter("user_mask"));
		int manager_mask = myUtil.IntegerNullToMinusOne(request.getParameter("manager_mask"));
		if (id == 0) {
			return myUtil.actionFail("Unknow Group ID");
		}


		GroupTbl g = em.find(GroupTbl.class, id);
		if (null == g) {
			return myUtil.actionFail("Unknow id:" + id);
		}


		if (!have_permission(user, request, em)) {
			return myUtil.actionFail("Permission Denied!", Macro.FAILCODE_IGNORE);
		}

		if (name != null && name.length()>0 && !name.trim().equals(g.getGroupname())) {

			char[] spcs = {'\'', '\\', '%'};
			if (myUtil.haveSpecialChar(name, spcs) != 0) {
				return myUtil.actionFail("Group name hace spcial charater[',%,\\]!", Macro.FAILCODE_IGNORE);
			}
			if (myUtil.exists_check("select count(*) from group_tbl where groupname='" + name + "' and group_id!=" + id, em)) {
				return myUtil.actionFail("Group name:" + name + " exists!", Macro.FAILCODE_IGNORE);
			}
			if (name.length() > Macro.MAX_STRLEN) {
				return myUtil.actionFail("The group name maxium length is " + Macro.MAX_STRLEN);
			}
			g.setGroupname(name);
		}

		if (null != desc) {
			g.setDescription(desc);
		}

		if (disabled ==0 || disabled==1) g.setDisabled(disabled);
		if (manager_mask>=0) g.setManagerMask(manager_mask);
		if (user_mask>=0) g.setUserMask(user_mask);
		em.merge(g);

		String q= "delete from group_user where group_id ="+id;
		myUtil.dbg(3, q);
		Query query =  em.createNativeQuery(q);
		query.executeUpdate();

		String users = request.getParameter("users");
		if (users != null && (!users.equals(""))) {
			q= "insert into group_user (user_id,group_id) select user_id,"+id+" from user_tbl where user_id in ("+users+")";
			myUtil.dbg(3, q);
			query =  em.createNativeQuery(q);
			query.executeUpdate();
		}

		String dowhat = "edit Group:" + name+"Users:"+users;
		myUtil.audit(user, Macro.ACT_ADMIN, 0L, dowhat, em);

		return myUtil.actionSuccess();
	}

	private String removeGroup(UserTbl user, HttpServletRequest request, EntityManager em) {

		Long id = myUtil.LongWithNullToZero(request.getParameter("id"));
		if (id == 0) {
			return myUtil.actionFail("mission id");
		}
		GroupTbl g = em.find(GroupTbl.class, id);
		if (null == g) {
			return myUtil.actionFail("Unknow id:" + id);
		}
		if (g.on_using(em)) return myUtil.actionFail("Group is used by other object", Macro.FAILCODE_IGNORE);
		if (!have_permission(user, request, em)) {
			return myUtil.actionFail("Permission Denied!", Macro.FAILCODE_IGNORE);
		}
		String dowhat = "remove Group:" + g.getGroupname();

		em.remove(g);

		myUtil.audit(user, Macro.ACT_ADMIN, 0L, dowhat, em);

		return myUtil.actionSuccess();
	}

}


