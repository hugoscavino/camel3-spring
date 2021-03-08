package org.scavino.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.scavino.model.Book;
import org.scavino.model.OrderConfirmation;
import org.springframework.stereotype.Component;

@Component
public class PrintBookProcessor implements Processor  {

    private final ObjectMapper objectMapper;

    public PrintBookProcessor(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public void process(Exchange exchange) throws Exception {
        Message message = exchange.getIn();
        OrderConfirmation orderConfirmation = exchange.getIn().getBody(OrderConfirmation.class);
        Book book = (Book)exchange.getProperty("BOOK_ID");
        orderConfirmation.setBook(book);
        message.setBody(orderConfirmation);
    }
}
