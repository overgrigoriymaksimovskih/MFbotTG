package pro.masterfood.service;

import pro.masterfood.dto.RequestParams;

import com.rabbitmq.client.Channel;
import org.springframework.messaging.handler.annotation.Headers;
import java.util.Map;

public interface ConsumerService {

    void consumeRequestToREST(RequestParams requestParams, Channel channel, @Headers Map<String, Object> headers);
}
