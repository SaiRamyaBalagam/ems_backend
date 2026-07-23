package com.ems.ems_backend.event;

/**
 * Marker for every EMS domain event, so a single publisher can forward
 * any of them to the right Kafka topic without a switch statement per type.
 */
public interface EmsEvent {
    String topic();

    String key();
}
