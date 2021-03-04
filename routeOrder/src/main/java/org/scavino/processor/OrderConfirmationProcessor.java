package org.scavino.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.support.MessageHelper;
import org.scavino.model.OrderConfirmation;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmationProcessor implements Processor  {

    private final ObjectMapper objectMapper;

    public OrderConfirmationProcessor(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    /**
     * Extract the message body as a JSON String and then convert to object
     * @param exchange setup as a InputStream
     * @throws Exception
     */
    public void process(Exchange exchange) throws Exception {
        Message message = exchange.getIn();
        String json = MessageHelper.extractBodyAsString(message);
        OrderConfirmation orderConfirmation = objectMapper.readValue(json, OrderConfirmation.class );
        message.setBody(orderConfirmation);
    }
}
