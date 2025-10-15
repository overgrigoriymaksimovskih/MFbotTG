package pro.masterfood.service;

import pro.masterfood.dto.RequestParams;
import com.rabbitmq.client.Channel;

public interface ConsumerService {

    void consumeRequestToREST(RequestParams requestParams);
    void handleAnswerFromNodeToRestForOneS(String message);
}
