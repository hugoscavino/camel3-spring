package org.scavino.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.scavino.model.Book;
import org.scavino.service.ValidateService;
import org.springframework.stereotype.Component;

@Component
public class ValidateProcessor implements Processor  {

    private final ObjectMapper objectMapper;
    private final ValidateService validateService;

    public ValidateProcessor(ObjectMapper objectMapper, ValidateService validateService){
        this.objectMapper = objectMapper;
        this.validateService = validateService;
    }

    public void process(Exchange exchange) throws Exception {
        Message message = exchange.getIn();
        Book book = message.getBody(Book.class);
        Book validatedBook = validateService.validateBook(book);
        message.setBody(validatedBook);
    }
}
