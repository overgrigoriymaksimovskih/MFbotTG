package pro.masterfood.service.impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import pro.masterfood.controller.UpdateController;
import pro.masterfood.service.AnswerConsumer;

//@Component
//public class AnswerConsumerImpl implements AnswerConsumer {
//    private final UpdateController updateController;
//    public AnswerConsumerImpl(UpdateController updateController) {
//        this.updateController = updateController;
//    }
//    @Override
//    @RabbitListener(queues = "${spring.rabbitmq.queues.answer-message}")
//    public void consume(SendMessage sendMessage) {
//        updateController.setView(sendMessage);
//    }
//}

@Component
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateController updateController;
    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }
    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.answer-message}")
    public void consume(SendMessage sendMessage) {
        System.out.println("AnswerConsumerImpl.consume() called with message: " + sendMessage);
        if (sendMessage.getReplyMarkup() != null) {
            System.out.println("AnswerConsumerImpl.consume() Keyboard: " + sendMessage.getReplyMarkup().toString());
        } else {
            System.out.println("AnswerConsumerImpl.consume(): no keyboard in sendMessage");
        }
        updateController.setView(sendMessage);
    }
}
