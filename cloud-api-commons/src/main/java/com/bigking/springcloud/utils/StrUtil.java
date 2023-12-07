package com.bigking.springcloud.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author devwang
 */
public class StrUtil {
    private static final Log LOG = LogFactory.getLog(StrUtil.class);
    private static final String REGEXP = "\n regExp: ";
    private static final String UTF8 = "UTF-8";
    private StrUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * judge if str contains str2/str3...
     */
    public static boolean like(Object str, Object... str2) {
        return StrUtil.indexOf(str, str2) != -1;
    }

    public static Integer indexOf(Object str, Object... str2) {
        if (str == null || str2 == null || str2.length == 0) {
            return -1;
        } else {
            for (int i = 0; i < str2.length; i++) {
                if (str2[i] == null) {
                    return -1;
                } else if ((str + "").contains(str2[i] + "")) {
                    return i + 1;
                }
            }
            return -1;
        }
    }

    public static String substring(String str, int idx, Object... str2) throws Exception {
        if (str == null || str2 == null || str2.length == 0) {
            return str;
        } else {
            int i = StrUtil.indexOf(str, str2);
            if (i == -1) {
                return str.substring(idx);
            } else {
                return StrUtil.substring(str.substring(0, str.indexOf(str2[i - 1].toString())), idx, str2);
            }
        }
    }

    public static String replaceAll(String source, String oldString, String newString) {
        StringBuilder output = new StringBuilder();
        int lengthOfSource = source.length();
        int lengthOfOld = oldString.length();
        int posStart = 0;
        int pos; //
        while ((pos = source.indexOf(oldString, posStart)) >= 0) {
            output.append(source, posStart, pos);
            output.append(newString);
            posStart = pos + lengthOfOld;
        }
        if (posStart < lengthOfSource) {
            output.append(source.substring(posStart));
        }
        return output.toString();
    }

    public static String replaceAll(String str, String regex, int flags, String replacement) {
        try {
            return Pattern.compile(regex, flags).matcher(str).replaceAll(replacement);
        } catch (Exception e) {
            LOG.error(e, e);
        }
        return str;
    }

    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
    }

    public static boolean isEmpty(String s) {
        return s == null || "".equals(s) || "NULL".equalsIgnoreCase(s) || "undefined".equalsIgnoreCase(s);
    }

    public static String checkFileName4Linux(String s) {
        if (null == s) {
            return "";
        }

        s = s.replaceAll(" ", "");

        return s;
    }

    public static boolean isEmpty(Object s) {
        return s == null || StrUtil.isEmpty(s.toString());
    }

    public static String toUTF8(String s) {
        try {
            return new String(toISO(s), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] toISO(String s) {
        try {
            return s.getBytes(StandardCharsets.ISO_8859_1);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public static List<String> getSplitStrColl(String str, String splitToken) {
        List<String> retPar = new ArrayList<>();

        if (null == str || str.equals("")) {
            return retPar;
        }
        String[] strArray = str.split(splitToken);
        for (String emtArray : strArray) {
            retPar.add(emtArray);
        }
        return retPar;
    }

    public static List<String> getSplitStrColl(String str) {
        return getSplitStrColl(str, ",");
    }

    public static String getSplitStr(Collection<String> list, String splitToken) {
        StringBuilder retPar = new StringBuilder();

        if (null == list || list.isEmpty()) {
            return retPar.toString();
        }
        for (String str : list) {
            retPar.append(str + splitToken);
        }

        retPar.delete(retPar.length() - splitToken.length(), retPar.length());

        return retPar.toString();
    }

    public static String getSplitStr(Collection<String> list) {
        return getSplitStr(list, ",");
    }

    public static String uuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }

    public static String splitString(String str, int len, String elide) {
        if (str == null) {
            return "";
        }
        byte[] strByte = str.getBytes();
        int strLen = strByte.length;
        int elideLen = (elide.trim().length() == 0) ? 0 : elide.getBytes().length;
        if (len >= strLen || len < 1) {
            return str;
        }
        if (len - elideLen > 0) {
            len = len - elideLen;
        }
        int count = 0;
        for (int i = 0; i < len; i++) {
            int value = (int) strByte[i];
            if (value < 0) {
                count++;
            }
        }
        if (count % 2 != 0) {
            len = (len == 1) ? len + 1 : len - 1;
        }
        return new String(strByte, 0, len) + elide.trim();
    }

    public static String getFirstCharUpperCases(String str) {
        if (StrUtil.isEmpty(str))
            return "";

        StringBuilder retPar = new StringBuilder();
        String[] strArray = str.split(" ");
        for (String sourceStr : strArray) {
            if (0 == retPar.length()) {
                retPar.append(getFirstCharUpperCase(sourceStr));
            } else {
                retPar.append(" ");
                retPar.append(getFirstCharUpperCase(sourceStr));
            }
        }

        return retPar.toString();
    }

    public static String getFirstCharUpperCase(String str) {
        if (StrUtil.isEmpty(str))
            return "";

        StringBuilder stringBuilder = new StringBuilder();

        String firstChar = String.valueOf(str.charAt(0));

        stringBuilder.append(firstChar.toUpperCase());
        if (str.length() > 1) {
            stringBuilder.append(str.substring(1));
        }
        return stringBuilder.toString();
    }

    public static String getFirstCharLowerCase(String str) {
        if (StrUtil.isEmpty(str))
            return "";

        StringBuilder stringBuilder = new StringBuilder();

        String firstChar = String.valueOf(str.charAt(0));

        stringBuilder.append(firstChar.toLowerCase());
        if (str.length() > 1) {
            stringBuilder.append(str.substring(1));
        }
        return stringBuilder.toString();
    }

    public static boolean isNumeric(String str) {
        if (StrUtil.isEmpty(str))
            return false;

        Pattern pattern = Pattern.compile("[0-9[-]]*");
        return pattern.matcher(str).matches();
    }

    public static boolean isAllNumeric(String str) {
        if (StrUtil.isEmpty(str))
            return false;

        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static boolean isIP(String str) {
        if (StrUtil.isEmpty(str))
            return false;

        Pattern pattern = Pattern.compile("(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})");
        return pattern.matcher(str).matches();
    }

    public static String getIP(String str) {
        if (StrUtil.isEmpty(str))
            return "";

        return getRegExpString(str, "(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})", 1);
    }

    public static String urlDecode(String str, String enc) {
        try {
            str = str.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            str = str.replaceAll("\\+", "%2B");
            return URLDecoder.decode(str, enc);
        } catch (Exception e) {
            LOG.error(e, e);
            return str;
        }
    }

    public static String urlDecode(String str) {
        return urlDecode(str, UTF8);
    }

    public static boolean getBitValueByIndex(String str, int index) {
        BigInteger trainnumber = new BigInteger(str);
        String strBit = trainnumber.toString(2);
        boolean result = false;
        if (getBitLength(str, index)) {
            char value = strBit.charAt(strBit.length() - index - 1);
            result = "1".equals(String.valueOf(value));
        }
        return result;
    }

    public static boolean getBitValueByRightIndex(String str, int index) {
        BigInteger trainnumber = new BigInteger(str);
        String strBit = trainnumber.toString(2);
        boolean result = false;
        if (getBitLength(str, index)) {
            char value = strBit.charAt(index);
            result = "1".equals(String.valueOf(value));
        }
        return result;
    }

    public static boolean getBitLength(String str, int index) {
        BigInteger trainnumber = new BigInteger(str);
        String strBit = trainnumber.toString(2);
        return (strBit.length() >= index + 1);
    }

    public static String getRegExpString(String sourceStr, String regExp, int groupNum) {
        try {
            Pattern p = Pattern.compile(regExp, Pattern.MULTILINE | Pattern.DOTALL);
            Matcher m = p.matcher(sourceStr);
            if (m.find())
                return m.group(groupNum);
            else
                return "";
        } catch (Exception e) {
            LOG.debug(sourceStr + REGEXP + regExp + " groupNum: " + groupNum);
            return "";
        }
    }

    public static String getRegExpString(String str, String pattern) {
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    public static boolean isMatchRegExp(String sourceStr, String regExp) {
        try {
            Pattern p = Pattern.compile(regExp);
            Matcher m = p.matcher(sourceStr);
            return m.matches();
        } catch (Exception e) {
            LOG.debug(sourceStr + REGEXP + regExp);
            return false;
        }
    }

    public static boolean isFindRegExp(String sourceStr, String regExp) {
        try {
            Pattern p = Pattern.compile(regExp);
            Matcher m = p.matcher(sourceStr);
            return m.find();
        } catch (Exception e) {
            LOG.debug(sourceStr + REGEXP + regExp);
            return false;
        }
    }

    public static String specialChar4Csv(Object obj) {
        if (obj == null) {
            return "";
        }

        String handleStr = obj.toString();

        handleStr = handleStr.replaceAll("\"", "\"\"");
        if (handleStr.indexOf(',', 1) > 0 || handleStr.indexOf('\"', 1) > 0) {
            handleStr = "\"" + handleStr + "\"";
        }

        return handleStr;
    }

    public static String decode4WbxClientLog(Object str) {
        if (str == null) {
            return "";
        }

        String retPar = str.toString();
        try {
            String encodeName = UTF8;
            retPar = URLDecoder.decode(str.toString(), encodeName);
            retPar = new String(retPar.getBytes(encodeName), encodeName);
        } catch (Exception e) {
            return retPar;
        }

        return retPar;
    }

    /**
     * Divide the collection according to the specified size, and divide the
     * collection into n parts according to the specified number
     *
     * @param list
     * @param len
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int len) {
        if (list == null || list.isEmpty() || len < 1) {
            return null;
        }
        List<List<T>> result = new ArrayList<>();
        int size = list.size();
        int count = (size + len - 1) / len;
        for (int i = 0; i < count; i++) {
            List<T> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }
}
