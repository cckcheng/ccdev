package com.ccdev.famtree;

public class Macro {
	static final public String SYSTEM_NAME = "Fortinet Sample";
	static final public String PUBLIC_DOMAIN = "https://vendor.ccdev.com/portal";

	static public final String ALERT_PREFIX = Macro.SYSTEM_NAME + " Alert - ";

	// define module name and mask associated to Group Priviledge mask
	static final public String MODULE_NAME_REQUEST = "Request";
	static final public int MODULE_REQUEST = 0x1;
	static final public String MODULE_NAME_UPGRADE = "Upgrade";
	static final public int MODULE_UPGRADE = 0x2;
	static final public String MODULE_NAME_REWORK = "Rework";
	static final public int MODULE_REWORK = 0x4;
	static final public String MODULE_NAME_PLATFORM = "Platform";
	static final public int MODULE_PLATFORM = 0x8;
	static final public String MODULE_NAME_CUSTOM_SEARCH = "CustomSearch";
	static final public int MODULE_CUSTOM_SEARCH = 0x10;
	static final public String MODULE_NAME_PO = "PO";
	static final public int MODULE_PO = 0x20;
	static final public String MODULE_NAME_NPI = "NPI";
	static final public int MODULE_NPI = 0x40;

	static final public String MODULE_NAME_ADMIN = "Admin";
	static final public int MODULE_ADMIN = 0x800;


	static final public boolean PASSWORD_UNCHECK = true;
	static public int DEBUG_LEVEL = 5;
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

	static public final String EMAIL_AFFIX = "@ccdev.com";

	static final public int SUCCESS = 0;

	static final public String REPORT_BUG = "Please report this problem to developers";

	static final public int MAX_NAMELEN = 32;
	static final public int MAX_SNLEN = 16;
	static final public int MAX_SHORTSTRLEN = 47;
	static final public int MAX_COMMENT = 200; // task comment length
	static final public int MAX_VALUE_LEN = 2048;	// max length of string value
	static final public int MAX_STRLEN = 255;
	static final public int OSVER_STRLEN = 64;
	static final public int HQIP_STRLEN = 64;
	static final public int PN_STRLEN = 16;
	static final public int TESTPKG_STRLEN = 64;
	static final public int DBNAME_STRLEN = 10;
	static final public int BIOS_STRLEN = 8;
	static final public int HW_STRLEN = 16;
	static final public int PCBASSY_STRLEN = 20;
	static final public int WO_STRLEN = 20;
	static final public int PO_STRLEN = 20;
	static final public int BUILDNUMBER_STRLEN = 6;

	static final public int ACT_ADMIN = 2;

	static final public int GUEST_LEVEL = 1;
	static final public int TESTER_LEVEL = 2;
	static final public int MANAGER_LEVEL = 3;

	static final public int FAILCODE_TIMEOUT = -1;
	static final public int FAILCODE_IGNORE = -2;

	// Error codes of common
	static final public int ERR_PERMISSION_DENY = -1;
	static final public int ERR_PATH_LENGTH_OVERLIMIT = -2;
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
	final public static int ERR_LOST_MANUFACTURER = -203;

	final public static int ERR_PARAM_BATCHUPGRADE = -300;
	final public static int ERR_UPLOAD_REQUIRED = -301;
	final public static int ERR_ROM_VERSION_REQUIRED = -302;
	final public static int ERR_ROM_VERSION_NOMATCH = -303;

	final public static int MAX_DISPLAY_LIMIT = 1048576; // 1M
	static final public int SN_MASK_LENGTH = 6;
	static final public int SN_SEQUENCE_LENGTH = 5;
	static final public int SN_YEAR_IDX = 9;
	static final public int SN_YEAR_LENGTH = 2;

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
			case Macro.ERR_LOST_MANUFACTURER:
				msg = "External user manufacture field empty.";
				break;
			case Macro.ERR_UPLOAD_REQUIRED:
				msg = "Please upload the image file.";
				break;
			case Macro.ERR_ROM_VERSION_REQUIRED:
				msg = "Please input rom version.";
				break;
			case Macro.ERR_OBJECT_NOT_FOUND:
				msg = "Related object NOT found.";
				break;
			case Macro.ERR_ROM_VERSION_NOMATCH:
				msg = "Rom version not match. ";
				break;

			default:
				msg = "Unknown error: " + err_code;
		}

		return msg;
	}

}
