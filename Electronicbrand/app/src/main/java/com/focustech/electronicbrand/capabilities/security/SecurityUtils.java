package com.focustech.electronicbrand.capabilities.security;

import com.focustech.electronicbrand.util.GeneralUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <加解密实现类>
 *
 * @author caoyinfei
 * @version [版本号, 2016/6/12]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class SecurityUtils {
    /**
     * md5加密
     * @param str
     * @return
     */
    public final static String get32MD5Str(String str)
    {
        if (GeneralUtils.isNullOrZeroLenght(str))
        {
            return str;
        }
        MessageDigest messageDigest = null;
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException e)
        {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++)
        {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }
}
