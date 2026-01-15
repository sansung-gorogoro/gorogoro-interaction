package com.example.lxp.common.messaging.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableRabbit
@EnableConfigurationProperties(MessagingProps.class)
public class RabbitConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(this::logConfirm);
        rabbitTemplate.setReturnsCallback(returned -> {
//            System.out.printf("Return callback: exchange=%s routingKey=%s replyCode=%s replyText=%s messageId=%s%n",
//                    returned.getExchange(),
//                    returned.getRoutingKey(),
//                    returned.getReplyCode(),
//                    returned.getReplyText(),
//                    returned.getMessage().getMessageProperties().getMessageId()
//            );
        });
        return rabbitTemplate;
    }

    private void logConfirm(CorrelationData correlationData, boolean ack, String cause) {
        String messageId = correlationData != null ? correlationData.getId() : "n/a";
        System.out.printf("Confirm callback: ack=%s messageId=%s cause=%s%n", ack, messageId, cause);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            CachingConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setDefaultRequeueRejected(false);
        factory.setPrefetchCount(1);
        return factory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(CachingConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Declarables topology(MessagingProps properties) {
        List<Declarable> declarables = new ArrayList<>();
        Map<String, TopicExchange> exchanges = new HashMap<>();

        // Shared DLX/DLQ
        TopicExchange dlx = ExchangeBuilder.topicExchange(properties.shared().dlx()).durable(true).build();
        declarables.add(dlx);

        // Service-specific exchanges/queues/bindings
        for (MessagingProps.Service service : properties.services().values()) {
            // Create service exchange
            TopicExchange exchange = ExchangeBuilder.topicExchange(service.exchange()).durable(true).build();
            declarables.add(exchange);
            exchanges.put(service.exchange(), exchange);

            // Create queues for this service
            for (Map.Entry<String, MessagingProps.QueueConfig> queueEntry : service.queues().entrySet()) {
                MessagingProps.QueueConfig queueConfig = queueEntry.getValue();

                // Create queue with DLX configuration
                Map<String, Object> queueArgs = new HashMap<>();
                String dlqName = queueConfig.name() + ".dlq";
                queueArgs.put("x-dead-letter-exchange", properties.shared().dlx());
                queueArgs.put("x-dead-letter-routing-key", dlqName);
                Queue queue = QueueBuilder.durable(queueConfig.name()).withArguments(queueArgs).build();
                declarables.add(queue);

                Queue dlq = QueueBuilder.durable(dlqName).build();
                declarables.add(dlq);
                declarables.add(BindingBuilder.bind(dlq).to(dlx).with(dlqName));

                // Create bindings for this queue
                String exchangeName = StringUtils.hasText(queueConfig.exchange())
                        ? queueConfig.exchange()
                        : service.exchange();
                TopicExchange bindingExchange = exchanges.get(exchangeName);
                if (bindingExchange == null) {
                    bindingExchange = ExchangeBuilder.topicExchange(exchangeName).durable(true).build();
                    exchanges.put(exchangeName, bindingExchange);
                    declarables.add(bindingExchange);
                }
                if (queueConfig.bindings() != null) {
                    for (String routingKey : queueConfig.bindings()) {
                        Binding binding = BindingBuilder.bind(queue).to(bindingExchange).with(routingKey);
                        declarables.add(binding);
                    }
                }
            }
        }

        return new Declarables(declarables);
    }
}
