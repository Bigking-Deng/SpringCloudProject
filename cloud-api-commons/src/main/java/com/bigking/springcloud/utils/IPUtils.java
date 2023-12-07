package com.bigking.springcloud.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 
 * @author devwang
 *
 */
public class IPUtils {

	private IPUtils(){}

	private static final Log LOG = LogFactory.getLog(IPUtils.class);

	// IpV4 regular expression,determine if it's legal.
	private static final String IPV4_REGEX = "((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})";

	private static final String IPV4_SEGMENT_REGEX = "((\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3})\\/(\\d{1}|[0-2]{1}\\d{1}|3[0-2])";

	/**
	 * get real ip
	 * 
	 * @return ip
	 */
	public static String getRealIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
			if (ip.indexOf(",") != -1) {
				ip = ip.split(",")[0];
			}
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
			LOG.debug("Proxy-Client-IP ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
			LOG.debug("WL-Proxy-Client-IP ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
			LOG.debug("HTTP_CLIENT_IP ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			LOG.debug("HTTP_X_FORWARDED_FOR ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
			LOG.debug("X-Real-IP ip: " + ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			LOG.debug("getRemoteAddr ip: " + ip);
		}
		return ip;
	}

	public static boolean ipV4Validate(String ipv4) {
		return ipv4Validate(ipv4, IPV4_REGEX);
	}

	public static boolean ipV4SegmentValidate(String ipv4Segment) {
		return ipv4SegmentValidate(ipv4Segment, IPV4_SEGMENT_REGEX);
	}

	public static boolean ipV4SegmentValidate(String allowedIp, String IP) {
		return ipV4SegmentValidate(allowedIp) && isInSegment(IP, allowedIp);
	}

	private static boolean ipv4Validate(String addr, String regex) {
		if (addr == null) {
			return false;
		} else {
			return Pattern.matches(regex, addr.trim());
		}
	}

	private static boolean ipv4SegmentValidate(String addrSegment, String regex) {
		if (addrSegment == null) {
			return false;
		} else {
			return Pattern.matches(regex, addrSegment.trim());
		}
	}

	public static boolean isInSegment(String ip, String segment) {
		String[] ipBlocks = ip.split("\\.");
		int binaryIp = (Integer.parseInt(ipBlocks[0]) << 24) | (Integer.parseInt(ipBlocks[1]) << 16)
				| (Integer.parseInt(ipBlocks[2]) << 8) | Integer.parseInt(ipBlocks[3]);

		int type = Integer.parseInt(segment.replaceAll(".*/", ""));
		int mask = 0xFFFFFFFF << (32 - type);
		String segmentIp = segment.replaceAll("/.*", "");
		String[] segmentIpBlocks = segmentIp.split("\\.");
		int binarySegmentIp = (Integer.parseInt(segmentIpBlocks[0]) << 24)
				| (Integer.parseInt(segmentIpBlocks[1]) << 16) | (Integer.parseInt(segmentIpBlocks[2]) << 8)
				| Integer.parseInt(segmentIpBlocks[3]);

		return (binaryIp & mask) == (binarySegmentIp & mask);
	}

	public static String getIpByHostname(String hostname) throws UnknownHostException {
		InetAddress address = InetAddress.getByName(hostname);
		String ip = address.getHostAddress();
		LOG.debug("For host " + hostname + ", the IP is " + ip);
		return ip;
	}

	public static String getLocalHostName() throws UnknownHostException {
		InetAddress address = InetAddress.getLocalHost();
		return address.getHostName();
	}

	public static List<String> parseIpMaskRange(String ip, String mask){
		List<String> list = new LinkedList<>();
		if ("32".equals(mask)) {
			list.add(ip);
		}else{
			String startIp=getBeginIpStr(ip, mask);
			String endIp=getEndIpStr(ip, mask);
			if (!"31".equals(mask)) {
				String subStart=startIp.split("\\.")[0]+"."+startIp.split("\\.")[1]+"."+startIp.split("\\.")[2]+".";
				String subEnd=endIp.split("\\.")[0]+"."+endIp.split("\\.")[1]+"."+endIp.split("\\.")[2]+".";
				startIp=subStart+(Integer.valueOf(startIp.split("\\.")[3])+1);
				endIp=subEnd+(Integer.valueOf(endIp.split("\\.")[3])-1);
			}
			list=parseIpRange(startIp, endIp);
		}
		return list;
	}

	public static String getBeginIpStr(String ip, String maskBit)
	{
		return getIpFromLong(getBeginIpLong(ip, maskBit));
	}

	public static String getEndIpStr(String ip, String maskBit)
	{
		return getIpFromLong(getEndIpLong(ip, maskBit));
	}

	public static Long getEndIpLong(String ip, String maskBit)
	{
		return getBeginIpLong(ip, maskBit)
				+ ~getIpFromString(getMaskByMaskBit(maskBit));
	}

	public static String getIpFromLong(Long ip)
	{
		String s1 = String.valueOf((ip & 4278190080L) / 16777216L);
		String s2 = String.valueOf((ip & 16711680L) / 65536L);
		String s3 = String.valueOf((ip & 65280L) / 256L);
		String s4 = String.valueOf(ip & 255L);
		return s1 + "." + s2 + "." + s3 + "." + s4;
	}

	public static Long getBeginIpLong(String ip, String maskBit)
	{
		return getIpFromString(ip) & getIpFromString(getMaskByMaskBit(maskBit));
	}

	public static Long getIpFromString(String ip)
	{
		Long ipLong = 0L;
		String ipTemp = ip;
		ipLong = ipLong * 256
				+ Long.parseLong(ipTemp.substring(0, ipTemp.indexOf(".")));
		ipTemp = ipTemp.substring(ipTemp.indexOf(".") + 1, ipTemp.length());
		ipLong = ipLong * 256
				+ Long.parseLong(ipTemp.substring(0, ipTemp.indexOf(".")));
		ipTemp = ipTemp.substring(ipTemp.indexOf(".") + 1, ipTemp.length());
		ipLong = ipLong * 256
				+ Long.parseLong(ipTemp.substring(0, ipTemp.indexOf(".")));
		ipTemp = ipTemp.substring(ipTemp.indexOf(".") + 1, ipTemp.length());
		ipLong = ipLong * 256 + Long.parseLong(ipTemp);
		return ipLong;
	}


	public static String getMaskByMaskBit(String maskBit)
	{
		return StringUtils.isEmpty(maskBit) ? "error, maskBit is null !"
				: maskBitMap().get(maskBit);
	}

	private static Map<String, String> maskBitMap()
	{
		Map<String, String> maskBit = new HashMap<>();
		maskBit.put("1", "128.0.0.0");
		maskBit.put("2", "192.0.0.0");
		maskBit.put("3", "224.0.0.0");
		maskBit.put("4", "240.0.0.0");
		maskBit.put("5", "248.0.0.0");
		maskBit.put("6", "252.0.0.0");
		maskBit.put("7", "254.0.0.0");
		maskBit.put("8", "255.0.0.0");
		maskBit.put("9", "255.128.0.0");
		maskBit.put("10", "255.192.0.0");
		maskBit.put("11", "255.224.0.0");
		maskBit.put("12", "255.240.0.0");
		maskBit.put("13", "255.248.0.0");
		maskBit.put("14", "255.252.0.0");
		maskBit.put("15", "255.254.0.0");
		maskBit.put("16", "255.255.0.0");
		maskBit.put("17", "255.255.128.0");
		maskBit.put("18", "255.255.192.0");
		maskBit.put("19", "255.255.224.0");
		maskBit.put("20", "255.255.240.0");
		maskBit.put("21", "255.255.248.0");
		maskBit.put("22", "255.255.252.0");
		maskBit.put("23", "255.255.254.0");
		maskBit.put("24", "255.255.255.0");
		maskBit.put("25", "255.255.255.128");
		maskBit.put("26", "255.255.255.192");
		maskBit.put("27", "255.255.255.224");
		maskBit.put("28", "255.255.255.240");
		maskBit.put("29", "255.255.255.248");
		maskBit.put("30", "255.255.255.252");
		maskBit.put("31", "255.255.255.254");
		maskBit.put("32", "255.255.255.255");
		return maskBit;
	}

	public static List<String> parseIpRange(String ipfrom, String ipto) {
		List<String> ips = new LinkedList<>();
		String[] ipfromd = ipfrom.split("\\.");
		String[] iptod = ipto.split("\\.");
		int[] int_ipf = new int[4];
		int[] int_ipt = new int[4];
		for (int i = 0; i < 4; i++) {
			int_ipf[i] = Integer.parseInt(ipfromd[i]);
			int_ipt[i] = Integer.parseInt(iptod[i]);
		}
		for (int A = int_ipf[0]; A <= int_ipt[0]; A++) {
			for (int B = (A == int_ipf[0] ? int_ipf[1] : 0); B <= (A == int_ipt[0] ? int_ipt[1]
					: 255); B++) {
				for (int C = (B == int_ipf[1] ? int_ipf[2] : 0); C <= (B == int_ipt[1] ? int_ipt[2]
						: 255); C++) {
					for (int D = (C == int_ipf[2] ? int_ipf[3] : 0); D <= (C == int_ipt[2] ? int_ipt[3]
							: 255); D++) {
						ips.add(new String(A + "." + B + "." + C + "." + D));
					}
				}
			}
		}
		return ips;
	}
}