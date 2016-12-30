package com.ccdev.famtree;

public class Macro {
	static final public String SYSTEM_NAME = "Family Tree Builder";
	static final public String PUBLIC_DOMAIN = "https://famtree.ccdev.app/famtree";

        static final public int LANGUAGE_CHINESE_SIMPLIFIED = 1;
        static final public int LANGUAGE_CHINESE_TRADITIONAL = 2;
        static final public int LANGUAGE_ENGLISH = 5;

	static public final String ALERT_PREFIX = Macro.SYSTEM_NAME + " Alert - ";

	// define module name and mask associated to Group Priviledge mask
	static final public String MODULE_NAME_BUILDER = "builder";
	static final public int MODULE_BUILDER = 0x1;

	static final public String MODULE_NAME_ADMIN = "Admin";
	static final public int MODULE_ADMIN = 0x800;


	static final public boolean PASSWORD_UNCHECK = true;
//	static public int DEBUG_LEVEL = 5;  // move to System Config
	static final public String version = "1.029";
	static public int SESSION_TIMEOUT = 1000 * 60 * 15;
	static final public String UPLOADFILES_PATH = "/famtree_upload/";

	static final public int ADMIN_LEVEL = 50;
	static final public int OTHER_LEVEL = 5;
	static final public int USER_DISABLED = 0;
	static final public int PM_LEVEL = 20;
	static final public int PAGE_SIZE = 50;
	static final public int AUTH_LDAP = 1;
	static final public int AUTH_LOCAL = 0;

	static public final String EMAIL_AFFIX = "@ccdev.app";

	static final public int SUCCESS = 0;

	static final public String REPORT_BUG = "Please report this problem to developers";

	static final public int MAX_NAMELEN = 32;
	static final public int MAX_SHORTSTRLEN = 47;
	static final public int MAX_VALUE_LEN = 2048;
	static final public int MAX_STRLEN = 255;

	static final public int ACT_ADMIN = 2;

	static final public int GUEST_LEVEL = 1;
	static final public int TESTER_LEVEL = 2;
	static final public int MANAGER_LEVEL = 3;

	static final public int FAILCODE_TIMEOUT = -1;
	static final public int FAILCODE_IGNORE = -2;

	// Error codes of common
	static final public int ERR_PERMISSION_DENY = -1;
	static final public int ERR_NAME_LENGTH_OVERLIMIT = -3;
	static final public int ERR_NAME_INVALID_CHARACTOR = -4;
	static final public int ERR_OBJECT_NOT_FOUND = -5;

	final public static int ERR_SYSTEM = -100;
	final public static int ERR_JDBC_QUERY = -101;
	final public static int ERR_UNKNOW_ACTION = -102;
	final public static int ERR_DB_UPDATE = -103;
	final public static int ERR_DB_QUERY = -104;

	final public static int ERR_PARAM_REQUIRED = -105;

	static final public int ERR_SEND_MESSAGE = -201;
	final public static int ERR_SEND_EMAIL = -202;

	final public static int MAX_DISPLAY_LIMIT = 1048576; // 1M

	public static final String ErrorMessage(int err_code) {
		String msg = "";

		switch(err_code){
			case Macro.ERR_SEND_MESSAGE:
				msg = "Failed to send message!";
				break;
			case Macro.ERR_SEND_EMAIL:
				msg = "Failed to send email!";
				break;

			case Macro.ERR_SYSTEM:
				msg = "System Error!" +
						"<BR>Please refresh your browser and try again later." +
						"<BR>...OR contact the system administrator.";
				break;
			case Macro.ERR_PARAM_REQUIRED:
				msg = "Missing parameter(s).";
				break;
			case Macro.ERR_JDBC_QUERY:
				msg = "JDBC query error!";
				break;
			case Macro.ERR_DB_UPDATE:
			case Macro.ERR_DB_QUERY:
				msg = "Database error!";
				break;
			case Macro.ERR_UNKNOW_ACTION:
				msg = "Unknown action!";
				break;

			case Macro.ERR_PERMISSION_DENY:
				msg = "Permission Denied";
				break;
			case Macro.ERR_NAME_INVALID_CHARACTOR:
				msg = "The name connot contain charactor /, \\ or '.";
				break;
			case Macro.ERR_OBJECT_NOT_FOUND:
				msg = "Related object NOT found.";
				break;

			default:
				msg = "Unknown error: " + err_code;
		}

		return msg;
	}

}
