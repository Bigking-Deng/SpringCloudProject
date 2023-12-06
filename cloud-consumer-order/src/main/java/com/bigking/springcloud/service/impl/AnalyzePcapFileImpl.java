package com.bigking.springcloud.service.impl;

//import com.bigking.springcloud.service.AnalyzePcapFileService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.EtherType;
import org.pcap4j.packet.namednumber.IpNumber;
import org.springframework.stereotype.Service;

import java.io.EOFException;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service
public class AnalyzePcapFileImpl{

    private static List<String> patternFieldList = Arrays.asList("Call-ID: (.*)", "From: (.*)", "To: (.*)");

    private static final int count = 100000;

    public static Map<String, Object> analyzePcapFile(String filePath) {
        PcapHandle handle=null;
        List<JSONObject> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        int packetNum = 0;
        try {
            handle = Pcaps.openOffline(filePath);
            for(int i=0; i<count; i++){

                System.out.println(i);
                Packet packet = handle.getNextPacketEx();
                packetNum = i+1;
                EthernetPacket ethernetPacket = packet.get(EthernetPacket.class);
                if(ethernetPacket.getHeader().getType() == EtherType.IPV4){
                    IpV4Packet ipV4Packet = ethernetPacket.get(IpV4Packet.class);
                    if(ipV4Packet.getHeader().getProtocol()== IpNumber.UDP){
                        UdpPacket udpPacket = ipV4Packet.get(UdpPacket.class);

                        String payload = new String(udpPacket.getPayload().getRawData());
                        if(payload.contains("sender_information")){
                            JSONObject jsonObject = JSONObject.parseObject(payload);
                            list.add(jsonObject);
                            System.out.println(jsonObject);
                        }
                        //所有quality的数据list
                        map.put("sender_information_list", list);

                        //这些quality的list数据需要有对应的额外标签，处理从原来string中提取需要的标签和值也返回回去
                        for(String patternStr: patternFieldList){
                            String fieldName = patternStr.split(":")[0];
                            if(map.containsKey(fieldName)) continue;
                            Pattern pattern = Pattern.compile(patternStr);
                            Matcher matcher = pattern.matcher(payload);
                            String value = "";
                            while(matcher.find()){
                               value = matcher.group(1);
                                System.out.println(value);
                            }
                            map.put(fieldName, value);
                        }
                    }
                }
            }

        } catch (PcapNativeException | TimeoutException | NotOpenException e) {
            e.printStackTrace();
        } catch (EOFException e){
           log.info(String.format("Reach the end of the Pcap file and the total packet size is %d", packetNum));
        }
        handle.close();
        return map;

    }

    public static void main(String[] args) {
        Map<String, Object> map = analyzePcapFile("/Users/haordeng/Proj_Code/SpringCloudProject/cloud-consumer-order/src/main/resources/example.pcap");
        System.out.println(map.size());
    }
}
