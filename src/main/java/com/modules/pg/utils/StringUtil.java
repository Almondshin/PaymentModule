package com.modules.pg.utils;

public class StringUtil {
	public static String isNull(Object obj) {
		if (obj == null) {
			return "";
		} else if (obj instanceof String) {
			if( "null".equals((String)obj)) {
				return "";
			}else {
				return (String) obj;
			}
		} else {
			return String.valueOf(obj);
		}
	}
}
