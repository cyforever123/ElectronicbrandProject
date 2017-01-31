package com.focustech.electronicbrand.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <通用工具类>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/6]
 * @see [相关类/方法]
 * @since [V1]
 */
public final class GeneralUtils {

    /**
     * 判断对象是否为null , 为null返回true,否则返回false
     *
     * @param obj 被判断的对象
     * @return boolean
     */
    public static boolean isNull(Object obj) {
        return (null == obj) ? true : false;
    }

    /**
     * 判断对象是否为null , 为null返回false,否则返回true
     *
     * @param obj 被判断的对象
     * @return boolean
     */
    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    /**
     * 判断字符串是否为null或者0长度，字符串在判断长度时，先去除前后的空格,空或者0长度返回true,否则返回false
     *
     * @param str 被判断的字符串
     * @return boolean
     */
    public static boolean isNullOrZeroLenght(String str) {
        return (null == str || "".equals(str.trim())) ? true : false;
    }

    /**
     * 判断字符串是否为null或者0长度，字符串在判断长度时，先去除前后的空格,空或者0长度返回false,否则返回true
     *
     * @param str 被判断的字符串
     * @return boolean
     */
    public static boolean isNotNullOrZeroLenght(String str) {
        return !isNullOrZeroLenght(str);
    }

    /**
     * 判断集合对象是否为null或者0大小 , 为空或0大小返回true ,否则返回false
     *
     * @param c collection 集合接口
     * @return boolean 布尔值
     * @see [类、类#方法、类#成员]
     */
    public static boolean isNullOrZeroSize(Collection<? extends Object> c) {
        return isNull(c) || c.isEmpty();
    }

    /**
     * 判断集合对象是否为null或者0大小 , 为空或0大小返回false, 否则返回true
     *
     * @param c collection 集合接口
     * @return boolean 布尔值
     * @see [类、类#方法、类#成员]
     */
    public static boolean isNotNullOrZeroSize(Collection<? extends Object> c) {
        return !isNullOrZeroSize(c);
    }

    /**
     * 判断数字类型是否为null或者0，如果是返回true，否则返回false
     *
     * @param number 被判断的数字
     * @return boolean
     */
    public static boolean isNullOrZero(Number number) {
        if (GeneralUtils.isNotNull(number)) {
            return (number.intValue() != 0) ? false : true;
        }
        return true;
    }

    /**
     * 判断数字类型是否不为null或者0，如果是返回true，否则返回false
     *
     * @param number 被判断的数字
     * @return boolean
     */
    public static boolean isNotNullOrZero(Number number) {
        return !isNullOrZero(number);
    }

    /**
     * <获取当前日期 格式 yyyyMMddHHmmss> <功能详细描述>
     *
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String getRightNowDateString() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(date);
    }

    /**
     * <获取当前时间 格式yyyyMMddHHmmss> <功能详细描述>
     *
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static Date getRightNowDateTime() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            return dateFormat.parse(dateFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <保留两位有效数字> <功能详细描述>
     *
     * @param num String
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String retained2SignificantFigures(String num) {
        return new BigDecimal(num).setScale(2, RoundingMode.HALF_UP).toString();
    }

    /**
     * <减法运算并保留两位有效数字> <功能详细描述>
     *
     * @param subt1 String
     * @param subt2 String
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String subtract(String subt1, String subt2) {
        BigDecimal sub1 = new BigDecimal(subt1);
        BigDecimal sub2 = new BigDecimal(subt2);
        BigDecimal result = sub1.subtract(sub2);
        result = result.setScale(2, RoundingMode.HALF_UP);
        return result.toString();
    }

    /**
     * <加法运算并保留两位有效数字> <功能详细描述>
     *
     * @param addend1
     * @param addend2
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String add(String addend1, String addend2) {
        BigDecimal add1 = new BigDecimal(addend1);
        BigDecimal add2 = new BigDecimal(addend2);
        BigDecimal result = add1.add(add2);
        result = result.setScale(2, RoundingMode.HALF_UP);
        return result.toString();
    }

    /**
     * <乘法运算并保留两位有效数字> <功能详细描述>
     *
     * @param factor1 String
     * @param factor2 String
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String multiply(String factor1, String factor2) {
        BigDecimal fac1 = new BigDecimal(factor1);
        BigDecimal fac2 = new BigDecimal(factor2);
        BigDecimal result = fac1.multiply(fac2);
        result = result.setScale(2, RoundingMode.HALF_UP);
        return result.toString();
    }

    /**
     * <除法运算并保留两位有效数字> <功能详细描述>
     *
     * @param divisor1 String
     * @param divisor2 String
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String divide(String divisor1, String divisor2) {
        BigDecimal div1 = new BigDecimal(divisor1);
        BigDecimal div2 = new BigDecimal(divisor2);
        BigDecimal result = div1.divide(div2, 2, RoundingMode.HALF_UP);
        return result.toString();
    }

    /**
     * <除法运算并保留两位有效数字> <功能详细描述>
     *
     * @param divisor1 String
     * @param divisor2 String
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String dividePoint1(String divisor1, String divisor2) {
        BigDecimal div1 = new BigDecimal(divisor1);
        BigDecimal div2 = new BigDecimal(divisor2);
        BigDecimal result = div1.divide(div2, 1, RoundingMode.HALF_UP);
        return result.toString();
    }

    /**
     * <将YYYYMMDDHHmmss 转换为 YYYY-MM-DD> <功能详细描述>
     *
     * @param str
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String splitTodate(String str) {
        if (isNullOrZeroLenght(str) || str.length() != 14) {
            return str;
        }

        String strs = "";
        strs = str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8);
        return strs;
    }

    /**
     * <将YYYYMMDDHHmmss 转换为 YYYY-MM-DD hh:mm> <功能详细描述>
     *
     * @param str
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String splitToMinute(String str) {
        if (isNullOrZeroLenght(str) || str.length() != 14) {
            return str;
        }

        String strs = "";
        strs =
                str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8) + " " + str.substring(8, 10)
                        + ":" + str.substring(10, 12);
        return strs;
    }

    /**
     * <将YYYY-MM-DD 转换为 YYYYMMDD> <功能详细描述>
     *
     * @param str
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String splitToMinuteNoLine(String str) {
        if (!str.contains("-")) {
            return str;
        }
        String strs = "";
        String[] strsArr = str.split("-");
        if (strsArr[1].length() == 1) {
            strsArr[1] = "0" + strsArr[1];
        }

        if (strsArr[2].length() == 1) {
            strsArr[2] = "0" + strsArr[2];
        }

        strs = strsArr[0] + strsArr[1] + strsArr[2];
        return strs;
    }

    /**
     * <将YYYYMMDDHHmmss 转换为 YYYY-MM-DD hh:mm:ss> <功能详细描述>
     *
     * @param str
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String splitToSecond(String str) {
        if (isNullOrZeroLenght(str) || str.length() != 14) {
            return str;
        }

        String strs = "";
        strs =
                str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8) + " " + str.substring(8, 10)
                        + ":" + str.substring(10, 12) + ":" + str.substring(12, 14);
        return strs;
    }

    /**
     * <将YYYYMMDDHHmmss 转换为 YY-MM-DD hh:mm:ss> <功能详细描述>
     *
     * @param str
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String splitToYear(String str) {
        if (isNullOrZeroLenght(str) || str.length() != 14) {
            return str;
        }

        String strs = "";
        strs =
                str.substring(2, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8) + " " + str.substring(8, 10)
                        + ":" + str.substring(10, 12) + ":" + str.substring(12, 14);
        return strs;
    }


    /**
     * 获取版本信息
     *
     * @return
     * @throws Exception
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (NameNotFoundException e) {
        }
        return "";
    }

    /**
     * <邮箱判断>
     * <功能详细描述>
     *
     * @param email
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isEmail(String email) {
        String str =
                "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * <手机号码判断>
     *
     * @param tel
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isTel(String tel) {
        String str = "^[0-9]{11}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(tel);
        return m.matches();
    }

    /**
     * <邮编判断>
     * <功能详细描述>
     *
     * @param post
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isPost(String post) {
        String patrn = "^[1-9][0-9]{5}$";
        Pattern p = Pattern.compile(patrn);
        Matcher m = p.matcher(post);
        return m.matches();
    }

    /**
     * <密码规则判断>
     * <功能详细描述>
     *
     * @param password
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean IsPassword(String password) {
        String str = "^[A-Za-z0-9_]{6,20}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    /**
     * <密码位数判断>
     * <功能详细描述>
     *
     * @param password
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean IsPasswordDigit(String password) {
        String str = "^[^ ]{6,20}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    /**
     * <密码位数判断>
     * <功能详细描述>
     *
     * @param certificate
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean Iscertificate(String certificate) {
        String str = "[0-9]{17}([0-9]|[xX])";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(certificate);
        return m.matches();
    }

    /**
     * <获取imei>
     * <功能详细描述>
     *
     * @param context
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * http://stackoverflow.com/questions/3495890/how-can-i-put-a-listview-into-a-scrollview-without-it-collapsing/3495908#3495908
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildrenExtend(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    // 去除textview的排版问题
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
        }
        return versionName;
    }

}
