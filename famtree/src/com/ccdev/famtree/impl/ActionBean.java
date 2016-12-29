/*
 * Copyright (c) 2016, ccheng
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.ccdev.famtree.impl;

import java.util.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.NoResultException;

import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.DataInputStream;
import java.util.Date;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileUploadException;

import net.sf.json.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletContext;
import com.ccdev.famtree.Action;
import com.ccdev.famtree.DoAction;
import com.ccdev.famtree.Macro;
import com.ccdev.famtree.bean.*;


@Stateless
@SuppressWarnings(value = {"unchecked"})
public class ActionBean implements Action {
        @PersistenceContext(unitName = "ccdevfamily")
	protected EntityManager em;

        @Override
	public void loadConfig() {
		SystemConfig.loadConfig(em);
	}
        
	public Users login(String uname, String password, StringBuilder msg) {
		try {
			String q = "Select u FROM Users as u WHERE u.username='" + uname + "'";
			if (uname.indexOf("@") >= 0) {
				if (!Macro.PASSWORD_UNCHECK) {
					q += " and u.password=MD5('" + password + "')";
				}
			}
			q += " and (u.disabled =0 or u.disabled is null)";
			myUtil.dbg(5, q);
			Users u =(Users) em.createQuery(q).getSingleResult();
			if (u == null) return null;
			else {
//                            if (uname.indexOf("@")>=0){
//				Date last_login = u.getLastLogin();
//				Date today_date = new Date();
//				if (last_login != null){
//					long diff = today_date.getTime() - last_login.getTime();
//					myUtil.dbg(5, "diff=" + diff);
//					if ((diff/(24 * 60 * 60 * 1000)) > 180  ){
//						disableUser(u);
//						msg.append("You did not use your account over 30 days.<br>Please contact administrator!");
//					}
//				}
//                            }
			}
                        
                        if (u.getLevel() >= Macro.ADMIN_LEVEL) {
				SystemConfig.loadConfig(em);
			}
			return u;
		} catch (NoResultException e) {
			msg.append("No Result Exception");
			return null;
		} catch (Exception e) {
			msg.append("Unexpected Error");
			e.printStackTrace();
			return null;
		}
	}

	public Users findUser(Long user_id) {
                return em.find(Users.class, user_id);
	}

	public String login_success(Users user) {
                JSONObject result = new JSONObject();
                int level = user.getLevel();

                JSONObject macro = new JSONObject();
                macro.put("SYSTEM_NAME", Macro.SYSTEM_NAME);
                macro.put("VERSION", Macro.version);
                macro.put("ADMIN_LEVEL", Macro.ADMIN_LEVEL);
                macro.put("MAX_STRLEN", Macro.MAX_STRLEN);

                result.put("macro", macro);

                JSONObject userProp = new JSONObject();
                userProp.put("family_name", user.getFamilyName());
                userProp.put("given_name", user.getGivinName());
                userProp.put("fullname", myUtil.makeFullName(user));
                userProp.put("username", user.getUsername());
                userProp.put("level", user.getLevel());
                result.put("user", userProp);

                if (user.getLastLogin() == null) {
                        result.put("firstTime", true);
                }

                JSONObject modules = new JSONObject();

                if (user.getLevel() == Macro.ADMIN_LEVEL) {
                        modules.put(Macro.MODULE_NAME_BUILDER, "manager");
                        modules.put(Macro.MODULE_NAME_ADMIN, "manager");

                } else {
                        String q = "Select bit_or(g.user_mask),bit_or(g.manager_mask) from groups g" +
                                        " join group_user gu on gu.user_id=" + user.getId() + " And gu.group_id=g.id";
                        myUtil.dbg(5, q);

                        List<Object[]> rs = em.createNativeQuery(q).getResultList();
                        for(Object[] o : rs) {
                                int user_mask = myUtil.IntegerWithNullToZero(o[0]);
                                int manager_mask = myUtil.IntegerWithNullToZero(o[1]);

                                if((manager_mask & Macro.MODULE_ADMIN) > 0) {
                                        modules.put(Macro.MODULE_NAME_ADMIN, "manager");
                                } else if((user_mask & Macro.MODULE_ADMIN) > 0) {
                                        modules.put(Macro.MODULE_NAME_ADMIN, "user");
                                }
                                
                                if((manager_mask & Macro.MODULE_BUILDER) > 0) {
                                        modules.put(Macro.MODULE_NAME_BUILDER, "manager");
                                } else if((user_mask & Macro.MODULE_BUILDER) > 0) {
                                        modules.put(Macro.MODULE_NAME_BUILDER, "user");
                                }
                        }
                }

                result.put("modules", modules);

                result.put("success", "true");
                return result.toString();
	}
        
	static final Map<String, String> fun;
	static {
            fun = new HashMap();
            fun.put("Admin".toLowerCase(), "com.ccdev.famtree.impl.Admin");
            fun.put("OptPedigree".toLowerCase(), "com.ccdev.famtree.impl.OptPedigree");
        }

        public DoAction getAction(String actName) {
            if(!fun.containsKey(actName)) return null;
            try {
                Class<?> cls = Class.forName(fun.get(actName));
		return (DoAction) cls.newInstance();
	    } catch (Exception ex) {
                ex.printStackTrace();
	    }

            return null;
        }

	public String doAction(Users user, HttpServletRequest request) {
                String actName = request.getParameter("dowhat");
                if (actName == null) {
                        return myUtil.actionFail("dowhat can not be null!");
                }
                actName = actName.toLowerCase();

                DoAction act = getAction(actName);
                if (act != null) {
                        Set<String> ignoredActions = new HashSet();
//				ignoredActions.add("getUserList".toLowerCase());
//				ignoredActions.add("viewUploads".toLowerCase());
                        String action = StringFunc.TrimedString(request.getParameter("action"));
                        if (!ignoredActions.contains(action.toLowerCase())) {
                                myUtil.dumpRequest(request);
                        }

                        return act.doAction(user, request, em);
                }
                return myUtil.actionFail("Action " + actName + " is not supported!");
	}

	public void updateLastLogin(Users user){
                user.setLastLogin(new Date());
                em.merge(user);
	}

	public void disableUser(Users user){
                user.setDisabled(1);
                em.merge(user);
	}

	public String doUpload(Users user, HttpServletRequest request) {
            try{
                myUtil.dbg(5, "--> action.doUpload start -" );
                boolean isMultipart = ServletFileUpload.isMultipartContent(request);
                if (!isMultipart) {
                        myUtil.dbg(2, "not multipart ");
                        return myUtil.actionSuccess();
                }

                DiskFileItemFactory factory = new DiskFileItemFactory();
                myUtil.dbg(5, "--> after create factory -" );
                ServletFileUpload upload = new ServletFileUpload(factory);
                myUtil.dbg(5, "--> after create upload -" );

                List items = upload.parseRequest(request);
                //if (1==1) throw new FileUploadException();
                myUtil.dbg(5, "--> after upload.parseRequest");
//			Iterator iter = items.iterator();

			/*
			ProgressListener progressListener = new ProgressListener() {

				private long megaBytes = -1;

				public void update(long pBytesRead, long pContentLength, int pItems) {
					long mBytes = pBytesRead / 1000000;
					if (megaBytes == mBytes) {
						return;
					}
					megaBytes = mBytes;
					myUtil.dbg(2, "We are currently reading item " + pItems);
					if (pContentLength == -1) {
						myUtil.dbg(2, "So far, " + pBytesRead + " bytes have been read.");
					} else {
						myUtil.dbg(2, "So far, " + pBytesRead + " of " + pContentLength + " bytes have been read.");
					}
				}
			};

			upload.setProgressListener(progressListener);
			*/
                HashMap form = new HashMap();
                myUtil.dbg(5, "--> after new HashMap()");
                List<FileItem> file_items = (List<FileItem>) items;
                for (FileItem item : file_items) {
                        if (item.isFormField()) {
                                String name = item.getFieldName();
                                InputStream stream = item.getInputStream();
                                String value = Streams.asString(stream);
                                form.put(name, value);

                                myUtil.dbg(2, "field name: " + name+",value="+value);

                                stream.close();
                        }
                }

                String dowhat = StringFunc.TrimedString(form.get("dowhat"));
                myUtil.dbg(5, "dowhat: " + dowhat);
                String action = StringFunc.TrimedString(form.get("action"));
                myUtil.dbg(5, "action: " + action);


                if (dowhat.equalsIgnoreCase("OptRevision")) {

                }

                return "{success:true}";
            } catch (FileUploadException e) {
                    myUtil.dbg(5,  e.toString());
                    return myUtil.actionFail("Upload File Error, please try again.");
            } catch (Exception e) {
                    e.printStackTrace();
                    return "";
            }

	}
	private boolean isNumberOrLetter(String str){
		if (str.length()==0) return false;
		for (int i=0; i< str.length(); i++){
			if (!Character.isDigit(str.charAt(i)) ) //&& !Character.isLetter(str.charAt(i))
				return false;
		}
		return true;
	}
	public void dwfile(String dirname, String filename, String chk, HttpServletResponse response, ServletContext context, ServletOutputStream op) {
		try {
			if (chk.length() == 0) {
				op.println("File not found(checksum is null).");
				return;
			}

//			File f = new File(Macro.UPLOADFILES_PATH +  "/" + chk);
			File f = new File(Macro.UPLOADFILES_PATH + chk);
			if (!f.exists()) {
				op.println("File not found.");
				return;
			}

			String mimetype = context.getMimeType(filename);

			response.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
			response.setContentLength((int) f.length());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

			byte[] bbuf = new byte[4096];
			DataInputStream in = new DataInputStream(new FileInputStream(f));

			int length = -1;
			while ((in != null) && ((length = in.read(bbuf)) != -1)) {
				op.write(bbuf, 0, length);
			}

			in.close();
			op.flush();
			op.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void dwnfile(String filename, HttpServletResponse response, ServletContext context, ServletOutputStream op) {
		try {
			if (filename.length() == 0) {
				op.println("File not found(checksum is null).");
				return;
			}

//			File f = new File(Macro.UPLOADFILES_PATH +  "/" + chk);
			File f = new File(filename);
			if (!f.exists()) {
				op.println("File not found.");
				return;
			}
			String[] items = filename.split("/");
			String real_fname = items[items.length-1];
			String mimetype = context.getMimeType(real_fname);

			response.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
			response.setContentLength((int) f.length());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + real_fname + "\"");

			byte[] bbuf = new byte[4096];
			DataInputStream in = new DataInputStream(new FileInputStream(f));

			int length = -1;
			while ((in != null) && ((length = in.read(bbuf)) != -1)) {
				op.write(bbuf, 0, length);
			}

			in.close();
			op.flush();
			op.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void download(Users user, HttpServletRequest request, HttpServletResponse response, ServletContext context, ServletOutputStream op) {
		myUtil.log(user, request, em);
		String filetype = StringFunc.TrimedString(request.getParameter("filetype"));
		myUtil.dbg(5, filetype);
		if (filetype.equalsIgnoreCase("testlog")) {
		} else if (filetype.equalsIgnoreCase("request")){

			String archive_name = request.getParameter("archive_name");
			myUtil.dbg("archive_name=" + archive_name);
			if (archive_name.equals("")) return;

			dwnfile(archive_name, response, context, op);
		}else {
			String dirname = request.getParameter("dirname");
			String filename = request.getParameter("filename");
			String chk = StringFunc.TrimedString(request.getParameter("chk"));
			dwfile(dirname, filename, chk, response, context, op);
		}
	}

	private void doTransfer(String filename, StringBuffer sb, HttpServletResponse response, ServletContext context, ServletOutputStream op) {
		try {
			String mimetype = context.getMimeType(filename);
			//
			//  Set the response and go!
			//
			response.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
			response.setContentLength(sb.toString().getBytes().length);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

			//
			//  Stream to the requester.
			//
			int length = 4096;
			while (sb.length() > 0) {
				if (sb.length() < length) {
					length = sb.length();
				}
				op.write(sb.substring(0, length).getBytes());
				sb.delete(0, length);
			}

			op.flush();
			op.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
