package com.bigking.springcloud.utils.kafka;

import org.apache.kafka.common.serialization.Serdes;

import java.util.Map;

public class StringKafkaDeliver extends KafkaDeliver<String, String> {

    public StringKafkaDeliver(Map<String, Object> configs) {
        super(configs, Serdes.String().serializer(), Serdes.String().serializer());
    }
}
