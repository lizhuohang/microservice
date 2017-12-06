package com.lzh.micro.framework.utils;

import org.apache.commons.lang.StringUtils;

/**
 * 类型转换
 * Created by lizhuohang on 17/8/14.
 */
public class CastUtil {
    public static String castString(Object obj) {
        return CastUtil.castString(obj, StringUtils.EMPTY);
    }

    public static String castString(Object obj, String defaultValue) {
        return obj != null ? String.valueOf(obj) : defaultValue;
    }

    public static double castDouble(Object obj) {
        return CastUtil.castDouble(obj, 0);
    }

    public static double castDouble(Object obj, double defaultValue) {
        Double doubleValue = defaultValue;
        if (obj != null) {
            String strValue = castString(obj, defaultValue + StringUtils.EMPTY);
            try {
                doubleValue = Double.parseDouble(strValue);
            } catch (Exception e) {
                doubleValue = defaultValue;
            }
        }
        return doubleValue;
    }

    public static long castLong(Object obj) {
        return CastUtil.castLong(obj, 0);
    }

    public static long castLong(Object obj, long defaultValue) {
        Long longValue = defaultValue;
        if (obj != null) {
            String strValue = castString(obj, defaultValue + StringUtils.EMPTY);
            try {
                longValue = Long.parseLong(strValue);
            } catch (Exception e) {
                longValue = defaultValue;
            }
        }
        return longValue;
    }

    public static int castInt(Object obj) {
        return CastUtil.castInt(obj, 0);
    }

    public static int castInt(Object obj, int defaultValue) {
        Integer intValue = defaultValue;
        if (obj != null) {
            String strValue = castString(obj, defaultValue + StringUtils.EMPTY);
            try {
                intValue = Integer.parseInt(strValue);
            } catch (Exception e) {
                intValue = defaultValue;
            }
        }
        return intValue;
    }


    public static boolean castBoolean(Object obj) {
        return CastUtil.castBoolean(obj, Boolean.FALSE);
    }

    public static boolean castBoolean(Object obj, boolean defaultValue) {
        Boolean booleanValue = defaultValue;
        if (obj != null) {
            booleanValue = Boolean.parseBoolean(castString(obj));
        }
        return booleanValue;
    }

}
