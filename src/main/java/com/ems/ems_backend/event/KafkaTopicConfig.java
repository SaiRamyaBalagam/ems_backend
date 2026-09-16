package com.ems.ems_backend.event;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic employeeEventsTopic() {
        return TopicBuilder.name("employee-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic leaveEventsTopic() {
        return TopicBuilder.name("leave-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic salaryEventsTopic() {
        return TopicBuilder.name("salary-events")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic attendanceEventsTopic() {
        return TopicBuilder.name("attendance-events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
