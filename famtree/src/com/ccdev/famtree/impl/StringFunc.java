/*
 * StringFunc
 *	include some common string processing functions
 */
package com.ccdev.famtree.impl;

import com.ccdev.famtree.Macro;
import java.util.Set;
import java.util.regex.Pattern;

/**
 *
 * @author Colin Cheng
 */
public class StringFunc {

	/**
	 * double the special char appeared in the source string
	 *  e.g., escapeChar("abcd a", 'a') will return "aabcd aa"
	 * @param src
	 * @param c
	 * @return
	 */
	static String escapeChar(Object src, char c) {
		if(src == null) return "";
		String a = String.valueOf(c);
		return src.toString().replaceAll(a, a + a);
	}

	static String escapeChar(String src, char c) {
		if(src == null) return "";
		String a = String.valueOf(c);
		return src.replaceAll(a, a + a);
	}

	static boolean invalidSN(String s, String pattern) {
		if(s.length() != Macro.MAX_SNLEN) return true;
		return !s.matches(pattern);
//		return false;
	}

	/**
	 * retrieveInteger
	 *		get the number from a string
	 *		e.g. "build00123a" -> 123
	 */
	static Integer retrieveInteger(String str) {
		Pattern reg = Pattern.compile("[\\D]+");
		String[] s = reg.split(str, 3);
		if (s.length == 0) {
			return null;
		}

		Integer num = null;
		try {
			if (s[0].length() == 0) {
				if (s.length >= 2) {
					num = Integer.parseInt(s[1]);
				}
			} else {
				num = Integer.parseInt(s[0]);
			}
		} catch (Exception e) {
		}
		return num;
	}

	public static final String TrimedString(String s) {
		if (s == null) {
			return "";
		}
		return s.trim();
	}

	public static final String TrimedString(Object s) {
		if (s == null) {
			return "";
		}
		return s.toString().trim();
	}

	public static String TrimedString(Object obj, String opt) {
		String str = TrimedString(obj);
		if (str.length() == 0) {
			return opt;
		}
		return str;
	}

	/*****************************************
	 * getJointString()
	 *		Trim "[1,2,3]" to "1,2,3"
	 * ***************************************/
	public static String getJointString(String str) {
		int len = str.length();
		if (len > 0) {
			str = str.substring(1, len - 1);
		}
		return str.replace(" ", "");
	}

	/*****************************************
	 * getJointString()
	 *		convert [a,b,c] to 'a','b','c'
	 * ***************************************/
	public static String getJointString(Set<String> strings) {
		StringBuffer str = new StringBuffer();
		for (String s : strings) {
			str.append(",'").append(s).append("'");
		}
		if (str.length() > 0) {
			return str.substring(1);
		}
		return "";
	}

	/*****************************************
	 * convertStringList()
	 *		convert "a,b,c" to "'a','b','c'"
	 * ***************************************/
	public static String convertStringList(String str) {
		return "'" + str.replaceAll(",", "','") + "'";
	}

	public static final String ellipsisString(String value, int length) {
		int len = value.length();
		if (len > length) {
			return value.substring(0, length - 3) + "...";
		}
		return value;
	}

	public static final String leftSubstring(String value, String delim) {
		int idx = value.indexOf(delim);
		if(idx < 0) return value;
		return value.substring(0, idx);
	}

	public static final String rightSubstring(String value, String delim) {
		int idx = value.lastIndexOf(delim);
		if(idx < 0) return value;
		return value.substring(idx+1);
	}

	/**************************************
	 * Replace all the ', _ and % appeared in str with \' \% \_
	 * So it will work properly for SQL queries
	 * @param str
	 * @return
	 */
	public static final String escapedString(String str){
		StringBuffer s = new StringBuffer(str);
		for(int i=str.length()-1; i>=0; i--) {
			char c = str.charAt(i);
			if(c == '\\') {
				s.insert(i, "\\\\\\");
			} else if(isSpecialChar(c)) {
				s.insert(i, '\\');
			}
		}
		return s.toString();
	}

	/*****************************************
	 * getSubPathTrimLeft()
	 *		Trim "/A/B/C/" to "B/C"
	 * ***************************************/
	public static String getSubPathTrimLeft(String spath) {
		String path = spath;
		int idx = path.indexOf("/", 1);
		if (idx > 0) {
			path = path.substring(idx + 1);
		}
		return path;
	}

	/*****************************************
	 * getSubPathTrimRight()
	 *		Trim "/A/B/C/" to "/A/B"
	 * ***************************************/
	public static String getSubPathTrimRight(String spath) {
		String path = spath;
		int idx = path.lastIndexOf("/", path.length() - 2);
		if (idx > 0) {
			path = path.substring(0, idx);
		}
		return path;
	}

	public static final String escapedString(String str, char ch){
		StringBuffer s = new StringBuffer(str);
		for(int i=str.length()-1; i>=0; i--) {
			char c = str.charAt(i);
			if(c == '\\' || c == ch) {
				s.insert(i, '\\');
			}
		}
		return s.toString();
	}

	public static final boolean isSpecialChar(char c) {
		char[] sp = {'%', '_', '\''};
		boolean ret = false;
		for(int i=0; i<sp.length; i++){
			if(sp[i] == c) {
				ret = true;
				break;
			}
		}
		return ret;
	}
}


