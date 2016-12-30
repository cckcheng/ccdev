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

	public String doAction(Users user, HttpServletRequest request, EntityManager em) {
		String result = "";
		String act = request.getParameter("action");

		if (act.equalsIgnoreCase("getUserList")) {
			result = getUserList(user, request, em);
		} else if (act.equalsIgnoreCase("getModules")) {
			result = getModules(user, request, em);
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

	private String getUser(Users user, HttpServletRequest request, EntityManager em) {
		long id = myUtil.LongWithNullToZero(request.getParameter("id"));
		if (id == 0) {
			return myUtil.actionFail("Unknown ID");
		}

		Users u = em.find(Users.class, id);
		if (null == u) {
			return myUtil.actionFail("Unknown id:" + id);
		}
		JSONObject result1 = new JSONObject();
		result1.put("success", "true");

		JSONObject Obj = new JSONObject();

		Obj.put("id", u.getId());
		Obj.put("username", u.getUsername());
		Obj.put("family_name", u.getFamilyName());
		Obj.put("given_name", u.getGivinName());
		Obj.put("level", u.getLevel());
		JSONArray array_ingroups = new JSONArray();
		Collection<Groups> groups =  u.getGroupCollection();
		for (Groups g : groups) {
			JSONObject gObj = new JSONObject();
			gObj.put("id", g.getId());
			gObj.put("name", g.getGroupname());
			array_ingroups.put(gObj);
		}
		Obj.put("inusers", array_ingroups);

		String q = "select * from groups where id not in ";
		q += "(select group_id from group_user where user_id=" + id + ") and (disabled = 0 or disabled is null) order by groupname";
		Query query = em.createNativeQuery(q, Groups.class);
		myUtil.dbg(5,q);
		ArrayList<Groups> outgroups = (ArrayList<Groups>) query.getResultList();
		JSONArray array_outgroups = new JSONArray();
		for (Groups g : outgroups) {
			JSONObject uObj = new JSONObject();
			uObj.put("id", g.getId());
			uObj.put("name", g.getGroupname());
			array_outgroups.put(uObj);
		}
		Obj.put("outusers", array_outgroups);
		result1.put("user", Obj);
		return result1.toString();
	}

	private JSONArray getUserList(int display_disabled, int shorted, EntityManager em) {
		String q = "select * from users where id>0 order by family_name,given_name";
		Query query = em.createNativeQuery(q, Users.class);
		myUtil.dbg(5, q);
		ArrayList<Users> users = (ArrayList<Users>) query.getResultList();
		JSONArray user_list = new JSONArray();
		for (Users user : users) {
			if ((display_disabled != 1) && (user.getDisabled()!=null) && (user.getDisabled()==1)) {
				continue;
			}
			JSONObject uObj = new JSONObject();
			uObj.put("id", user.getId());
			uObj.put("username", user.getUsername());
			if (shorted != 1) {
				uObj.put("level", user.getLevel());
				uObj.put("disabled", user.getDisabled()==null?0:user.getDisabled());
                                uObj.put("family_name", user.getFamilyName());
                                uObj.put("given_name", user.getGivinName());
				uObj.put("name", myUtil.makeFullName(user));
			}else{
				uObj.put("name", myUtil.makeFullName(user));
			}
			user_list.put(uObj);
		}
		return user_list;
	}

	private JSONArray getUserByUsername(String username, int display_disabled, int shorted, EntityManager em) {
		String q = "select * from users where id>0 and (username like '" + username + "'";
		q+=" or given_name like '"+username+"') order by family_name,given_name";
		Query query = em.createNativeQuery(q, Users.class);
		ArrayList<Users> users = (ArrayList<Users>) query.getResultList();
		JSONArray user_list = new JSONArray();
		for (Users user : users) {
			if ((display_disabled != 1) && (user.getDisabled()!=null) && (user.getDisabled()==1)) {
				continue;
			}
			JSONObject uObj = new JSONObject();
			uObj.put("id", user.getId());
			uObj.put("username", user.getUsername());
			if (shorted != 1) {
				uObj.put("level", user.getLevel());
				uObj.put("disabled", (user.getDisabled()==null)?0:user.getDisabled());
				uObj.put("onused", user.on_using(em));
                                uObj.put("family_name", user.getFamilyName());
                                uObj.put("given_name", user.getGivinName());
			}else{
				uObj.put("name", myUtil.makeFullName(user));
			}
			user_list.put(uObj);
		}
		return user_list;
	}

	private String getUserList(Users user, HttpServletRequest request, EntityManager em) {
		int disabled = myUtil.IntegerWithNullToZero(request.getParameter("disabled"));
		int shorted = myUtil.IntegerWithNullToZero(request.getParameter("shorted"));
		JSONObject Obj = new JSONObject();
		Obj.put("users", getUserList(disabled, shorted, em));
		return Obj.toString();
	}
	private String getUserByUsername(Users user, HttpServletRequest request, EntityManager em) {
		int disabled = myUtil.IntegerWithNullToZero(request.getParameter("disabled"));
		int shorted = myUtil.IntegerWithNullToZero(request.getParameter("shorted"));
		String username = StringFunc.TrimedString(request.getParameter("username"));
		JSONObject Obj = new JSONObject();
		Obj.put("users", getUserByUsername(username, disabled, shorted, em));
		return Obj.toString();
	}

	private String getGroupsByUser(Users user, HttpServletRequest request, EntityManager em) {
		int id = myUtil.IntegerWithNullToZero(request.getParameter("id"));
		if (id == 0) {
			return myUtil.actionFail("Unknown ID");
		}
		JSONArray ar = new JSONArray();
		JSONObject ret_obj = new JSONObject();

		String q= "select a.id,a.groupname from groups a join group_user b on a.id=b.group_id where b.user_id =" +id;
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

	private String addUser(Users user, HttpServletRequest request, EntityManager em) {
		if (!have_permission(user, request, em)) {
			return myUtil.actionFail("Permission Denied!", Macro.FAILCODE_IGNORE);
		}
		String username = StringFunc.TrimedString(request.getParameter("username"));
		String familyName = StringFunc.TrimedString(request.getParameter("familyName"));
		String givenName = StringFunc.TrimedString(request.getParameter("givenName"));

		if (username == null || username.length()==0) {
			return myUtil.actionFail("There are no display name!", Macro.FAILCODE_IGNORE);
		}
		if (myUtil.exists_check("select count(*) from users where username='" + username + "'", em)) {
			return myUtil.actionFail("login Username:" + username + " exists!", Macro.FAILCODE_IGNORE);
		}
		if (username.length() > Macro.MAX_STRLEN) {
			return myUtil.actionFail("The username maxium length is " + Macro.MAX_STRLEN, Macro.FAILCODE_IGNORE);
		}

		char[] spcs = {'\'', '\\', '%'};
		if (myUtil.haveSpecialChar(username, spcs) != 0) {
			return myUtil.actionFail("Username hace spcial charater[',%,\\]!", Macro.FAILCODE_IGNORE);
		}

		if (familyName == null || familyName.length()==0) {
			return myUtil.actionFail("There are no family name!");
		}
		if (familyName.length() > Macro.MAX_STRLEN) {
			return myUtil.actionFail("There name maxium length is " + Macro.MAX_STRLEN);
		}
		if (myUtil.haveSpecialChar(familyName, spcs) != 0) {
			return myUtil.actionFail("Name hace spcial charater[',%,\\]!", Macro.FAILCODE_IGNORE);
		}

		if (givenName == null || givenName.length()==0) {
			return myUtil.actionFail("There are no family name!");
		}
		if (givenName.length() > Macro.MAX_STRLEN) {
			return myUtil.actionFail("There name maxium length is " + Macro.MAX_STRLEN);
		}
		if (myUtil.haveSpecialChar(givenName, spcs) != 0) {
			return myUtil.actionFail("Name hace spcial charater[',%,\\]!", Macro.FAILCODE_IGNORE);
		}

		String level_s = request.getParameter("level");
		int level = Macro.OTHER_LEVEL;
		if (null != level_s && level_s.equals("on")) {
			level = Macro.ADMIN_LEVEL;
			if (user.getLevel() < Macro.ADMIN_LEVEL)
				return myUtil.actionFail("Permission deny to set administrator!", Macro.FAILCODE_IGNORE);
		}

		String password = generatePassword(8);

		String q="insert into users(username, password, family_name, given_name, level, disabled)" +
				" values('" + username + "',MD5('" + password + "'),'" + familyName + "','" + givenName + "'," + level + ",0)";
		if(!myUtil.execDBUpdate(q, em)) {
			return myUtil.actionFail(Macro.ERR_DB_UPDATE);
		}
		q = "select last_insert_id()";
		Query qy = em.createNativeQuery(q);
		Object last_id = qy.getSingleResult();
		long new_id = Long.parseLong(last_id.toString());
		myUtil.dbg(5, "new_id=" + new_id);

		if(username.indexOf("@") > 0){
			if(!this.sendWelcomeEmail(request, username, familyName + givenName, password)){
				return myUtil.actionFail(Macro.ERR_SEND_EMAIL);
			}
		}

		String groups = request.getParameter("groups");
		if (groups != null && (!groups.equals(""))) {
			q= "insert into group_user (user_id,group_id) select " + new_id + ", id  from groups where id in ("+groups+")";
			myUtil.dbg(3, q);
			qy =  em.createNativeQuery(q);
			qy.executeUpdate();
		}

		String dowhat = "add User:" + username +"level:"+level;
		myUtil.audit(user, Macro.ACT_ADMIN, 0L, dowhat, em);

		return myUtil.actionSuccess();
	}

	private String editUser(Users user, HttpServletRequest request, EntityManager em) {
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
			if (user.getLevel() < Macro.ADMIN_LEVEL)
				return myUtil.actionFail("Permission deny to set administrator!", Macro.FAILCODE_IGNORE);
		}

		Users u = em.find(Users.class, id);
		if (null == u) {
			return myUtil.actionFail("Unknown id:" + id);
		}

		char[] spcs = {'\'', '\\', '%'};

		if (username != null && username.length()>0) {
			if (myUtil.exists_check("Select count(*) from users where username='" + username + "' and id!=" + id, em)) {
				return myUtil.actionFail("Login username:" + username + " exists!", Macro.FAILCODE_IGNORE);
			}

			if (myUtil.haveSpecialChar(username, spcs) != 0) {
				return myUtil.actionFail("username has special character[',%,\\]!", Macro.FAILCODE_IGNORE);
			}
			u.setUsername(username);
		}

		String familyName = StringFunc.TrimedString(request.getParameter("familyName"));
		if (familyName != null && !familyName.isEmpty()) {
			if (myUtil.haveSpecialChar(familyName, spcs) != 0) {
				return myUtil.actionFail("familyName has special character[',%,\\]!", Macro.FAILCODE_IGNORE);
			}
			u.setFamilyName(familyName);
		}

		String givenName = StringFunc.TrimedString(request.getParameter("givenName"));
		if (givenName != null && !givenName.isEmpty()) {
			if (myUtil.haveSpecialChar(givenName, spcs) != 0) {
				return myUtil.actionFail("givenName has special character[',%,\\]!", Macro.FAILCODE_IGNORE);
			}
			u.setGivinName(givenName);
		}

		if (level > 0) {
			if (level == Macro.ADMIN_LEVEL && user.getLevel() < Macro.ADMIN_LEVEL)
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
			q= "insert into group_user (user_id,group_id) select " + id + ", id  from groups where id in ("+groups+")";
			myUtil.dbg(3, q);
			query =  em.createNativeQuery(q);
			query.executeUpdate();
		}
		String dowhat = "edit User:" + username +" level:"+level;
		em.merge(u);
		myUtil.audit(user, Macro.ACT_ADMIN, 0L, dowhat, em);

		return myUtil.actionSuccess();
	}

	private String removeUser(Users user, HttpServletRequest request, EntityManager em) {
		if (!have_permission(user, request, em)) {
			return myUtil.actionFail("Permission Denied!", Macro.FAILCODE_IGNORE);
		}
		Long id = myUtil.LongWithNullToZero(request.getParameter("id"));
		if (id == 0) {
			return myUtil.actionFail("missing id");
		}
		Users u = em.find(Users.class, id);
		if (null == u) {
			return myUtil.actionFail("Unknown id:" + id);
		}
		if (u.on_using(em)) return myUtil.actionFail("The user is used by other object", Macro.FAILCODE_IGNORE);
		String dowhat = "remove User:" + u.getUsername();
		em.remove(u);
		myUtil.audit(user, Macro.ACT_ADMIN, 0L, dowhat, em);

		return myUtil.actionSuccess();
	}

	private String changePassword(Users user, HttpServletRequest request, EntityManager em) {
		Long id = user.getId();
		myUtil.dbg(5, "" +id);

		String oldPass =request.getParameter("oldpassword");
		if (oldPass != null) {
			String q = "select count(*) from users where password = MD5('" + oldPass + "') and id=" +id;
			if(!myUtil.exists_check(q, em)) {
				return myUtil.actionFail("Password not right!", Macro.FAILCODE_IGNORE);
			}
		}

		String newPass = request.getParameter("newpassword");
		String q = "update users set password=MD5('" + newPass + "') where id=" + id;
		return myUtil.doDBUpdate(q, em);
	}

	private String resetPassword(Users user, HttpServletRequest request, EntityManager em) {
		if (!have_permission(user, request, em)) {
			return myUtil.actionFail("Permission Denied!", Macro.FAILCODE_IGNORE);
		}
		Long id = myUtil.LongWithNullToZero(request.getParameter("id"));
		if (id == 0) {
			return myUtil.actionFail("missing id");
		}
		Users u = em.find(Users.class, id);
		if (null == u) {
			return myUtil.actionFail("Unknown id:" + id);
		}
		String username = u.getUsername();
		String name = myUtil.makeFullName(u);
		String password = generatePassword(8);

		String q = "update users set password=MD5('" + password + "'),last_login=null" +" where id=" + id;
		if(!myUtil.execDBUpdate(q, em)) {
			return myUtil.actionFail(Macro.ERR_DB_UPDATE);
		}
                if(!this.sendWelcomeEmail(request, username, name, password)){
                        return myUtil.actionFail(Macro.ERR_SEND_EMAIL);
                }
		return myUtil.actionSuccess();
	}

	private boolean sendWelcomeEmail(HttpServletRequest request, String mailto, String fullname, String password){
		String subject = "Account Creation at " + Macro.SYSTEM_NAME;
//		String url = StringFunc.getSubPathTrimRight(request.getRequestURL().toString());
		String url = Macro.PUBLIC_DOMAIN;
		String msg = "Dear " + fullname + ":\n\n"
				+ "An account has been setup for you at " + Macro.SYSTEM_NAME + ".\n\n"
				+ " Please click the following link to change your password:\n\n"
				+ url + "/loginServlet?user=" + mailto + "&password=" + password + "\n\n"
				+ "Once you change your password, you can login to the system at:\n\n " + url + "\n\n"
				+ "Please do not reply to this email.\n\n"
				+ "Thank you.\n\n"
				+ Macro.SYSTEM_NAME + " Admin";

		EmailThread eThread = new EmailThread("admin" + Macro.EMAIL_AFFIX, mailto, null, msg, subject);
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

	private boolean have_permission(Users user, HttpServletRequest request, EntityManager em) {
		if (user.getLevel() >= Macro.ADMIN_LEVEL) {
			return true;
		}
		Users usr = em.find(Users.class, user.getId());

		Collection<Groups> groups =  usr.getGroupCollection();
		for (Groups g : groups) {
			if ((g.getUserMask() & Macro.MODULE_ADMIN)>0) return true;
		}

		return false;
	}

	private JSONArray getGroups(int display_disabled, int shorted, EntityManager em) {
		String q = "select * from groups order by groupname";
                myUtil.dbg(5, q);
		Query query = em.createNativeQuery(q, Groups.class);
		ArrayList<Groups> outgroups = (ArrayList<Groups>) query.getResultList();
		JSONArray group_list = new JSONArray();
		for (Groups group : outgroups) {
			if ((display_disabled != 1) && (group.getDisabled()==1)) {
				continue;
			}
			JSONObject grpObj = new JSONObject();
			String gname = group.getGroupname();
			grpObj.put("id", group.getId());
			grpObj.put("name", gname);
			if (shorted != 1) {
				grpObj.put("descript", group.getDescription());
				grpObj.put("user_mask", getMaskList(group.getUserMask()));
				grpObj.put("manager_mask", getMaskList(group.getManagerMask()));
				grpObj.put("disabled", group.getDisabled());
				grpObj.put("on_using", group.on_using(em) ? 1 : 0);
			}
			group_list.put(grpObj);
		}

		return group_list;
	}
        
        private String getMaskList(Integer iMask) {
            String str = "";
            if(iMask != null && iMask > 0) {
                int mk = 0x1;
                while(mk <= 0x8000) {
                    if((iMask & mk) > 0) str += "," + mk;
                    mk <<= 1;
                }
            }
            if(str.isEmpty()) return "";
            return str.substring(1);
        }

	private String getGroupList(Users user, HttpServletRequest request, EntityManager em) {
		int disabled = myUtil.IntegerWithNullToZero(request.getParameter("disabled"));
		int shorted = myUtil.IntegerWithNullToZero(request.getParameter("shorted"));
		JSONObject Obj = new JSONObject();
		Obj.put("groups", getGroups(disabled, shorted, em));
		return Obj.toString();
	}


	private String getUsersByGroup(Users user, HttpServletRequest request, EntityManager em) {
		int id = myUtil.IntegerWithNullToZero(request.getParameter("id"));
		if (id == 0) {
			return myUtil.actionFail("Unknown ID");
		}
		JSONArray ar = new JSONArray();
		JSONObject ret_obj = new JSONObject();

		String q= "select a.id,a.username from users a join group_user b on a.id=b.user_id where b.group_id =" +id;
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

	private String getGroup(Users user, HttpServletRequest request, EntityManager em) {
		long id = myUtil.LongWithNullToZero(request.getParameter("id"));
		if (id == 0) {
			return myUtil.actionFail("Unknown Group ID");
		}

		Groups g = em.find(Groups.class, id);
		if (null == g) {
			return myUtil.actionFail("Unknown id:" + id);
		}

		JSONObject result1 = new JSONObject();
		result1.put("success", "true");

		JSONObject Obj = new JSONObject();

		Obj.put("id", g.getId());
		Obj.put("name", g.getDescription());
		Obj.put("description", g.getDescription());
                
		JSONArray array_inusers = new JSONArray();
		String q = "select a.* from users a join group_user b on a.id= b.user_id where b.group_id ="+id;
		Query query = em.createNativeQuery(q, Users.class);
		ArrayList<Users> users = (ArrayList<Users>) query.getResultList();
		for (Users u : users) {
			JSONObject uObj = new JSONObject();
			uObj.put("id", u.getId());
			uObj.put("name", myUtil.makeFullName(u));
			array_inusers.put(uObj);
		}
		Obj.put("inusers", array_inusers);

		q = "select * from users where id not in ";
		q += "(select user_id from group_user where group_id=" + id + ") and (disabled=0 or disabled is null) order by family_name,given_name";
		myUtil.dbg(3, q);
		query = em.createNativeQuery(q, Users.class);
		ArrayList<Users> outusers = (ArrayList<Users>) query.getResultList();
		JSONArray array_outusers = new JSONArray();
		for (Users u : outusers) {
			JSONObject uObj = new JSONObject();
			uObj.put("id", u.getId());
			uObj.put("name", myUtil.makeFullName(u));
			array_outusers.put(uObj);
		}
		Obj.put("outusers", array_outusers);
		result1.put("group", Obj);
		return result1.toString();
	}

	private String addGroup(Users user, HttpServletRequest request, EntityManager em) {
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


		if (myUtil.exists_check("select count(*) from groups where groupname='" + name + "'", em)) {
			return myUtil.actionFail("name:" + name + " exists!", Macro.FAILCODE_IGNORE);
		}

		if (!have_permission(user, request, em)) {
			return myUtil.actionFail("Permission Denied!", Macro.FAILCODE_IGNORE);
		}

		String descript = request.getParameter("descript");
		if (descript != null && descript.length() > Macro.MAX_STRLEN) {
			return myUtil.actionFail("The group descript maxium length is " + Macro.MAX_STRLEN);
		}

		Groups g = new Groups(name.trim(), descript);

		em.persist(g);

		long g_id = g.getId();

//		int manager_mask = myUtil.IntegerNullToMinusOne(request.getParameter("manager_mask"));
//		if (manager_mask>=0) g.setManagerMask(manager_mask);
		int user_mask = myUtil.IntegerNullToMinusOne(request.getParameter("mask"));
		if (user_mask>=0) g.setUserMask(user_mask);
		em.merge(g);

		String users = request.getParameter("users");
		if (users != null && (!users.equals(""))) {
			String q= "insert into group_user (user_id,group_id) select id,"+g_id+" from users where id in ("+users+")";
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

	private String editGroup(Users user, HttpServletRequest request, EntityManager em) {
		Long id = myUtil.LongWithNullToZero(request.getParameter("id"));
		String name = StringFunc.TrimedString(request.getParameter("name"));
		String desc = request.getParameter("descript");
		int disabled = myUtil.IntegerNullToMinusOne(request.getParameter("disabled"));
		int user_mask = myUtil.IntegerNullToMinusOne(request.getParameter("mask"));
		int manager_mask = myUtil.IntegerNullToMinusOne(request.getParameter("manager_mask"));
		if (id == 0) {
			return myUtil.actionFail("Unknown Group ID");
		}

		Groups g = em.find(Groups.class, id);
		if (null == g) {
			return myUtil.actionFail("Unknown id:" + id);
		}


		if (!have_permission(user, request, em)) {
			return myUtil.actionFail("Permission Denied!", Macro.FAILCODE_IGNORE);
		}

		if (name != null && name.length()>0 && !name.trim().equals(g.getGroupname())) {

			char[] spcs = {'\'', '\\', '%'};
			if (myUtil.haveSpecialChar(name, spcs) != 0) {
				return myUtil.actionFail("Group name hace spcial charater[',%,\\]!", Macro.FAILCODE_IGNORE);
			}
			if (myUtil.exists_check("select count(*) from groups where groupname='" + name + "' and id!=" + id, em)) {
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
			q= "insert into group_user (user_id,group_id) select id,"+id+" from users where id in ("+users+")";
			myUtil.dbg(3, q);
			query =  em.createNativeQuery(q);
			query.executeUpdate();
		}

		String dowhat = "edit Group:" + name+"Users:"+users;
		myUtil.audit(user, Macro.ACT_ADMIN, 0L, dowhat, em);

		return myUtil.actionSuccess();
	}

	private String removeGroup(Users user, HttpServletRequest request, EntityManager em) {
		Long id = myUtil.LongWithNullToZero(request.getParameter("id"));
		if (id == 0) {
			return myUtil.actionFail("mission id");
		}
		Groups g = em.find(Groups.class, id);
		if (null == g) {
			return myUtil.actionFail("Unknown id:" + id);
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

        private String getModules(Users user, HttpServletRequest request, EntityManager em) {
            JSONArray ar = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("id", Macro.MODULE_BUILDER);
            obj.put("name", Macro.MODULE_NAME_BUILDER);
            ar.put(obj);

            obj = new JSONObject();
            obj.put("id", Macro.MODULE_ADMIN);
            obj.put("name", Macro.MODULE_NAME_ADMIN);
            ar.put(obj);
            
            return myUtil.actionSuccess(ar);
        }

}


