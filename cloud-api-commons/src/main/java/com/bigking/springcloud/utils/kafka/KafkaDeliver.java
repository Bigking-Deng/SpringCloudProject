package com.bigking.springcloud.utils.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

public class KafkaDeliver<K, V> {

    private final KafkaProducer<K, V> kafkaProducer;

    public KafkaDeliver(Map<String, Object> configs) {
        this(configs, null, null);
    }

    public KafkaDeliver(Map<String, Object> configs, Serializer<K> kSerializer, Serializer<V> vSerializer) {
        kafkaProducer = new KafkaProducer<>(configs, kSerializer, vSerializer);
    }

    public void sendAsync(String topic, V value) {
        sendAsync(topic, null, value);
    }

    public void sendAsync(String topic, K key, V value) {
        sendAsync(topic, key, value, null);
    }

    public void sendAsync(String topic, K key, V value, BiConsumer<ProducerRecord<K, V>, Exception> callback) {
        ProducerRecord<K, V> producerRecord = new ProducerRecord<>(topic, key, value);
        kafkaProducer.send(producerRecord, (metadata, exception) -> {
            if (callback != null) {
                callback.accept(producerRecord, exception);
            }
        });
    }

    public RecordMetadata sendSync(String topic, V value, long timeInMillis) throws InterruptedException,
            ExecutionException, TimeoutException {
        return sendSync(topic, null, value, timeInMillis);
    }

    public RecordMetadata sendSync(String topic, K key, V value, long timeInMillis) throws InterruptedException,
            ExecutionException, TimeoutException {
        ProducerRecord<K, V> producerRecord = new ProducerRecord<>(topic, key, value);
        Future<RecordMetadata> future = kafkaProducer.send(producerRecord);
        return future.get(timeInMillis, TimeUnit.MILLISECONDS);
    }
}
