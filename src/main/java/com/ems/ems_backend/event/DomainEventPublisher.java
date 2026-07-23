package com.ems.ems_backend.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Forwards any EmsEvent to Kafka only after the originating DB transaction
 * has committed, so consumers never react to a change that got rolled back.
 * One listener handles every event type — each event knows its own topic/key.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DomainEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDomainEvent(EmsEvent event) {
        log.info("Publishing {} to {} (key={})", event.getClass().getSimpleName(), event.topic(), event.key());
        kafkaTemplate.send(event.topic(), event.key(), event);
    }
}
