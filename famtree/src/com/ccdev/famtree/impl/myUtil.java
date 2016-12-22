package com.ccdev.famtree.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.io.File;
import org.apache.commons.fileupload.FileItem;

import javax.servlet.http.HttpServletRequest;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.ccdev.famtree.bean.*;
import com.ccdev.famtree.Macro;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.persistence.NoResultException;
import net.sf.json.JSONException;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
//
@SuppressWarnings(value = {"unchecked"})
public class myUtil {

	public static final void dbg(int level, String msg) {
		if (level > Macro.DEBUG_LEVEL) {
			return;
		}
		System.out.println(msg);
	}

	public static final void dbg(String msg) {
		if (!Macro.PASSWORD_UNCHECK) {
			return;
		}
		System.out.println(msg);
	}

	public static final String actionFail(String msg) {
		try {
			JSONObject result = new JSONObject();
			result.put("success", "false");
			result.put("message", msg);
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception caught: " + e.getMessage();
		}
	}

	public static final String actionFail(String msg, int failcode) {
		try {
			JSONObject result = new JSONObject();
			result.put("success", "false");
			result.put("message", msg);
			result.put("failcode", failcode);
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception caught: " + e.getMessage();
		}
	}

	public static final String actionFail(int err_code) {
		return actionFail(Macro.ErrorMessage(err_code), Macro.FAILCODE_IGNORE);
	}

	public static final String actionFail(int err_code, String more_message) {
		return actionFail(Macro.ErrorMessage(err_code) + "<BR>" + more_message, Macro.FAILCODE_IGNORE);
	}

	public static final String actionFail(int err_code, HashMap mp) {
		try {
			JSONObject result = new JSONObject();
			result.put("success", "false");
			result.put("message", Macro.ErrorMessage(err_code));
			result.put("failcode", Macro.FAILCODE_IGNORE);

			Set<Map.Entry> entrys = mp.entrySet();
			for (Map.Entry entry : entrys) {
				result.put(entry.getKey().toString(), entry.getValue().toString());
			}
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception caught: " + e.getMessage();
		}
	}

	public static final String actionSuccess() {
		try {
			JSONObject result = new JSONObject();
			result.put("success", "true");
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception caught: " + e.getMessage();
		}
	}

	public static final String actionSuccess(JSONObject result) {
		try {
			result.put("success", "true");
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception caught: " + e.getMessage();
		}
	}

	public static final String actionSuccess(String root, JSONObject obj) {
		try {
			JSONObject result = new JSONObject();
			result.put("success", "true");
			result.put(root, obj);
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception caught: " + e.getMessage();
		}
	}

	static String actionSuccess(JSONArray ar) {
		return actionSuccess("results", ar);
	}

	static String actionSuccess(int total, JSONArray ar) {
		return actionSuccess(total, "results", ar);
	}

	public static String actionSuccess(String root, JSONArray ar) {
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put(root, ar);
			jsonObj.put("success", "true");
			return jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "JSONException: " + e.getMessage();
		}
	}


	public static String actionSuccess(int total, String root, JSONArray ar) {
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total", total);
			jsonObj.put(root, ar);
			jsonObj.put("success", "true");
			return jsonObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "JSONException: " + e.getMessage();
		}
	}

	public static final String actionSuccess(String msg) {
		try {
			JSONObject result = new JSONObject();
			result.put("success", "true");
			result.put("message", msg);
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception caught: " + e.getMessage();
		}
	}

	public static final String actionSuccess(Map mp) {
		try {
			JSONObject result = new JSONObject();
			Iterator it = mp.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				result.put(pairs.getKey().toString(), pairs.getValue());
			}

			result.put("success", "true");
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception caught: " + e.getMessage();
		}
	}

	public static void dumpRequest(HttpServletRequest request) {
		Map m = request.getParameterMap();
		Iterator it = m.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			String k = pairs.getKey().toString();
			String[] values = (String[]) pairs.getValue();
			String v = "";
			for (int i = 0; i < values.length; i++) {
				v = v + values[i] + " ";
			}
			dbg(2, k + "=" + v);
		}
	}

	public static final String loginSuccess(Users user, String msg) {
		try {
			JSONObject result = new JSONObject();
			result.put("success", "true");
			result.put("message", msg);
			result.put("role", user.getLevel());
			return result.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "Exception caught: " + e.getMessage();
		}
	}

	public static final String traceException(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter w = new PrintWriter(sw);
		e.printStackTrace(w);
		return "\nTrace Stack:\n" + sw.toString();
	}

	public static final Long LongWithNullToZero(String s) {
		if (s == null) {
			return 0L;
		}
		try {
			Long i = Long.parseLong(s.trim());
			return i;
		} catch (Exception e) {
			return 0L;
		}
	}

	public static final Long LongWithNullToZero(Object o) {
		if (o == null) {
			return 0L;
		}

		return LongWithNullToZero(o.toString());
	}

	public static final int IntegerWithNullToZero(String s) {
		if (s == null) {
			return 0;
		}
		try {
			Integer i = Integer.parseInt(s.trim());
			return i;
		} catch (Exception e) {
			return 0;
		}
	}
	public static final int HexWithNullToZero(Object o) {
		if (o == null) {
			return 0;
		}

		return HexWithNullToZero(o.toString());
	}
	public static final int HexWithNullToZero(String s) {
		if (s == null) {
			return 0;
		}
		try {
			s = s.replace("0x", "");
			Integer i = Integer.parseInt(s.trim(), 16);
			return i;
		} catch (Exception e) {
			return 0;
		}
	}

	public static final Integer GetInterger(String s) {
		if (s == null) {
			return null;
		}
		try {
			Integer i = Integer.parseInt(s.trim());
			return i;
		} catch (Exception e) {
			return null;
		}
	}

	public static final Object GetObject(String s) {
		if (s == null || s.trim().length() == 0) {
			return null;
		}
		return s;
	}

	public static final int IntegerWithNullToZero(Object s) {
		if (s == null) {
			return 0;
		}
		try {
			int i = Integer.parseInt(s.toString().trim());
			return i;
		} catch (Exception e) {
			return 0;
		}
	}

	public static final int IntegerNullToMinusOne(String s) {
		if (s == null) {
			return -1;
		}
		try {
			int i = Integer.parseInt(s.trim());
			return i;
		} catch (Exception e) {
			return 0;
		}
	}

	public static final boolean BooleanNullToFalse(String s, String match) {
		if (s == null) {
			return false;
		}
		try {
			int i = Integer.parseInt(s);
			if (i > 0) {
				return true;
			}
		} catch (Exception e) {
		}
		return s.equalsIgnoreCase(match);
	}

	public static final String uploadFile(FileItem item) {
		try {
			byte[] data = item.get();
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] md5sum = digest.digest(data);
			BigInteger bigInt = new BigInteger(1, md5sum);
			String chk = bigInt.toString(16);
			File f = new File(Macro.UPLOADFILES_PATH );
			if (!f.exists()) {
				f.mkdirs();
			}
			f = new File(Macro.UPLOADFILES_PATH + chk);
			myUtil.dbg(3, "new file=" + Macro.UPLOADFILES_PATH + chk);
			if (!f.exists()) item.write(f);
			return chk;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

    public static final ArrayList<String> getZipFiles(InputStream in)
    {
        try
        {
            ZipEntry zipentry;
            ZipInputStream zipinputstream = new ZipInputStream(in);

            zipentry = zipinputstream.getNextEntry();
			ArrayList<String> names = new ArrayList<String>();
            while (zipentry != null)
            {
                //for each entry to be extracted
                String entryName = zipentry.getName();
                dbg(2,"entryname "+entryName);
				names.add(entryName);
                zipinputstream.closeEntry();
                zipentry = zipinputstream.getNextEntry();

            }//while

            zipinputstream.close();
			return names;
        }
        catch (Exception e)
        {
//            e.printStackTrace();
			dbg(2,"Unknow Zip file format!");
			return null;
        }
    }

	public static UserLog log(Users user, HttpServletRequest request, EntityManager em) {
		String dowhat = "";
		String action = "";
		String param = "";
		Enumeration er = request.getParameterNames();
		while (er.hasMoreElements()) {
			String key = er.nextElement().toString();
			if (key.equalsIgnoreCase("action")) {
				action = request.getParameter(key);
			} else if (key.equalsIgnoreCase("dowhat")) {
				dowhat = request.getParameter(key);
			} else {
				if(key.indexOf("password") < 0){ // donot record the user's password
					param += key + ": " + request.getParameter(key) + "\n";
				}
			}
		}
		if (param.length()>Macro.MAX_STRLEN) param = param.substring(0, Macro.MAX_STRLEN -5);
		UserLog log = new UserLog(user.getUsername(), dowhat, action, param);
		em.persist(log);
		return log;
	}

	public static int haveSpecialChar(String s, char[] sp) {
		for (int i = 0; i < sp.length; i++) {
			if (s.indexOf((int) sp[i]) != -1) {
				return (int) sp[i];
			}
		}
		return 0;
	}
	public static final int getCountBySQL(String q, EntityManager em) {
		myUtil.dbg(3, q);
		Query query = em.createNativeQuery(q);
		Object total = query.getSingleResult();
		return Integer.parseInt(total.toString());
	}

	public static final Integer getIntegerBySQL(String q, EntityManager em) {
		myUtil.dbg(3, q);
		Query query = em.createNativeQuery(q);
		List<Object> rs = query.getResultList();
		if(rs.size() == 0) return null;
		Object total = rs.get(0);
		if(total == null) return null;
		return Integer.parseInt(total.toString());
	}

	public static final Long getLongBySQL(String q, EntityManager em) {
		myUtil.dbg(3, q);
		Query query = em.createNativeQuery(q);
		List<Object> rs = query.getResultList();
		if(rs.size() == 0) return null;
		Object total = rs.get(0);
		if(total == null) return null;
		return Long.parseLong(total.toString());
	}

	public static final boolean exists_check(String q, EntityManager em) {
		myUtil.dbg(3, q);
		Query query = em.createNativeQuery(q);
		Object total = query.getSingleResult();
		return (Integer.parseInt(total.toString()) > 0);
	}

	public static String doDBUpdate(String q, EntityManager em) {
		myUtil.dbg(5, q);
		try {
			em.createNativeQuery(q).executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return myUtil.actionFail(Macro.ERR_DB_UPDATE);
		}

		return myUtil.actionSuccess();
	}

	public static boolean execDBUpdate(String q, EntityManager em) {
		myUtil.dbg(5, q);
		try {
			em.createNativeQuery(q).executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static final String limitClause(HttpServletRequest request) {
		int start = myUtil.IntegerWithNullToZero(request.getParameter("start"));
		int limit = myUtil.IntegerWithNullToZero(request.getParameter("limit"));
		if(limit == 0) {
			return "";
		}

		return " Limit " + start + "," + limit;
	}

	static final void run_search(String pname) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec(pname);
	}
	public static final boolean sendEmail(String mailto, String filename, String msg, String subject) {
		String from = "famtree_admin@ccdev.com";
		return sendEmail(from, mailto, filename, msg, subject);
	}

	public static final boolean sendEmail(String from, String mailto, String filename, String msg, String subject) {
//		javax.mail.internet.InternetAddress to[] = new InternetAddress[6];
		String bodyText = msg;

		Properties properties = new Properties();
		properties.put("mail.smtp.host", "smtp.ccdev.com");
//		properties.put("mail.smtp.host", "208.91.113.81");

		properties.put("mail.smtp.port", "25");
		javax.mail.Session session = Session.getDefaultInstance(properties, null);

		try {
			javax.mail.internet.InternetAddress[] to = InternetAddress.parse(mailto, false);
			javax.mail.Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipients(Message.RecipientType.TO, to);
			message.setSubject(subject);
			message.setSentDate(new Date());

			//
			// Set the email message text.
			//
			MimeBodyPart messagePart = new MimeBodyPart();
			messagePart.setText(bodyText);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messagePart);

			//
			// Set the email attachment file
			//
			if (filename != null) {
				MimeBodyPart attachmentPart = new MimeBodyPart();
				FileDataSource fileDataSource = new FileDataSource(filename) {

					@Override
					public String getContentType() {
						return "application/octet-stream";
					}
				};
				attachmentPart.setDataHandler(new DataHandler(fileDataSource));
				attachmentPart.setFileName(StringFunc.rightSubstring(filename, "/"));
				multipart.addBodyPart(attachmentPart);
			}

			message.setContent(multipart);
			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	public static final void audit(Users user, int acttype, Long orgid, String dowhat, EntityManager em) {
		Audit audit = new Audit(orgid, dowhat, acttype, user);
		em.persist(audit);
	}

	public static final Long getMacAddress(String sn, EntityManager em) {
		String q = "select b.start_mac from bios b where b.serial='" + sn + "'"
				+ " Order by b.bios_id desc limit 1";
		return getLongBySQL(q, em);
	}

	public static String MacAddressString(Long macaddr) {
		if(macaddr == null) return "N/A";
		return String.format("%02x:%02x:%02x:%02x:%02x:%02x",
				(macaddr>>40)&0xff, (macaddr>>32)&0xff,
				(macaddr>>24)&0xff, (macaddr>>16)&0xff,
				(macaddr>>8)&0xff, macaddr&0xff);
	}

	public static boolean BooleanNullToFalse(Integer v) {
		return v != null && v > 0;
	}

	public static boolean isTimeout(Date created) {
		Calendar cld = Calendar.getInstance();
		cld.add(Calendar.HOUR, -2);	// 2 hours
		if(created.before(cld.getTime())) return true;
		return false;
	}

	public static final Object getExistRecord(String table_name, String condition, String orderby,
			EntityManager em, Class result_class) {
		String q = "select * from " + table_name;
		if(condition != null && condition.length() > 0) {
			q += " Where " + condition;
		}
		if (orderby != null && orderby.length() > 0) {
			q += " Order By " + orderby;
		}
		q += " Limit 1";
		dbg(5, q);
		try {
			Query query = em.createNativeQuery(q, result_class);
			List<Object> objs = query.getResultList();
			if (objs.size() == 0) {
				return null;
			}
			return objs.get(0);
		} catch (Exception e) {
			dbg(3, e.getMessage());
			return null;
		}
	}

	public static final List getExistRecords(String table_name, String condition, String orderBy,
			EntityManager em, Class result_class) {
		String q = "select * from " + table_name;
		if (condition != null && condition.length() > 0) {
			q += " Where " + condition;
		}
		if (orderBy != null && orderBy.length() > 0) {
			q += " Order By " + orderBy;
		}
		dbg(5, q);
		Query query = em.createNativeQuery(q, result_class);
		return query.getResultList();
	}

	public static final boolean hasPermission(Users user, int module, EntityManager em) {
		if (user.getLevel() == Macro.ADMIN_LEVEL) return true;
		String q = "Select bit_or(g.mask) from groups g join group_user gu on gu.user_id="
				+ user.getId() + " And gu.group_id=g.id";
		int mask = myUtil.getIntegerBySQL(q, em);
		return (mask & module) > 0;
	}

        public static String makeFullName(Users user) {
            String familyName = user.getFamilyName();
            String givenName = user.getGivinName();
            String fName = familyName;
            if(familyName.matches("^[\\x00-\\x7F]+$")) {
                fName = givenName + ' ' + fName;
            } else {
                fName += givenName;
            }
            return fName;
        }

	/**
	 * get formated date: "yyyy-MM-dd"
	 * @param dt
	 * @return
	 */
	private static final SimpleDateFormat fmtDate = new SimpleDateFormat("yyyy-MM-dd");
	public static final String formatDate(Date dt) {
		if (dt == null) {
			return "";
		}
		return fmtDate.format(dt);
	}

	public static final Date parseDate(String sDate) {
		if (sDate == null) {
			return null;
		}
                
                try {
        		return fmtDate.parse(sDate);
                } catch (Exception e) {
                }
                
                return null;
	}

	/**
	 * get formated time: "yyyy-MM-dd HH:mm"
	 * @param dt
	 * @return
	 */
	private static final SimpleDateFormat fmtTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final String formatTime(Date dt) {
		if (dt == null) {
			return "";
		}
		return fmtTime.format(dt);
	}

	/**
	 * get formated time: "yyyy-MM-dd HH:mm:ss"
	 * @param dt
	 * @return
	 */
	private static final SimpleDateFormat fmtFullTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final String formatFullTime(Date dt) {
		if (dt == null) {
			return "";
		}
		return fmtFullTime.format(dt);
	}

}
