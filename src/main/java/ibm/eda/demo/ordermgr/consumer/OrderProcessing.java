package ibm.eda.demo.ordermgr.consumer;

import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Singleton;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import ibm.eda.demo.ordermgr.infra.events.OrderEvent;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

@Singleton
public class OrderProcessing {
    public static ConcurrentHashMap<String, OrderMetric> aggregates = new ConcurrentHashMap<String, OrderMetric>();

    @Incoming("order-stream") 
    @Outgoing("metrics")                   
    public Message<OrderMetric> process(Message<OrderEvent> msg) {
        System.out.println("-Message Received! from kafka-");
        OrderEvent orderEvent = msg.getPayload();
        System.out.println(orderEvent.toString());
        if ( ! aggregates.containsKey(orderEvent.getStatus())) {
            OrderMetric metric = new OrderMetric(orderEvent.getStatus(), 1);
            aggregates.put(metric.orderState, metric);
            msg.ack();
            return Message.of(metric).addMetadata(OutgoingKafkaRecordMetadata.<String>builder()
			.withKey(metric.orderState).build());
            
        } else {
            OrderMetric metric = aggregates.get(orderEvent.getStatus());
            metric.updateCount(1);
            System.out.println(metric.toString());
            aggregates.put(metric.orderState, metric);
            msg.ack();
            return Message.of(metric).addMetadata(OutgoingKafkaRecordMetadata.<String>builder()
			.withKey(metric.orderState).build());
        }
        
    }
}
