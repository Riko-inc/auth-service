package org.example.authservice.utils;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.authservice.domain.events.GenericEventMessage;
import org.example.authservice.domain.events.StringEvent;
import org.modelmapper.ModelMapper;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class KafkaEventPublisherAspect {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final ModelMapper modelMapper;

    // Обработка DTO событий
    @Around("@annotation(publishDtoEvent)")
    public Object handleDtoEvent(ProceedingJoinPoint joinPoint,
                                 PublishDtoEvent publishDtoEvent) throws Throwable {
        Object result = joinPoint.proceed();

        Object payload = convertToPayload(result, publishDtoEvent.payloadClass());

        kafkaTemplate.send(
                publishDtoEvent.topic(),
                new GenericEventMessage<>(
                        publishDtoEvent.eventType(),
                        payload
                )
        );

        return result;
    }

    // Обработка String событий
    @Around("@annotation(publishStringEvent)")
    public Object handleStringEvent(ProceedingJoinPoint joinPoint,
                                    PublishStringEvent publishStringEvent) throws Throwable {
        Object result = joinPoint.proceed();

        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("result", result);
        context.setVariable("args", joinPoint.getArgs());

        String payload = parser.parseExpression(publishStringEvent.payloadExpression())
                .getValue(context, String.class);

        kafkaTemplate.send(
                publishStringEvent.topic(),
                StringEvent.builder()
                        .eventType(publishStringEvent.eventType())
                        .payload(payload)
                        .build()
        );

        return result;
    }

    private Object convertToPayload(Object source, Class<?> targetClass) {
        if (source == null) return null;
        if (targetClass.isInstance(source)) return source;
        return modelMapper.map(source, targetClass);
    }
}