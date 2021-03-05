package org.scavino.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.support.MessageHelper;
import org.scavino.model.Book;
import org.scavino.model.Translation;
import org.springframework.stereotype.Component;

@Component
public class TranslateProcessor implements Processor  {

    private final ObjectMapper objectMapper;

    public TranslateProcessor(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public void process(Exchange exchange) throws Exception {
        Message message = exchange.getIn();
        String json = MessageHelper.extractBodyAsString(message);
        Translation translation = objectMapper.readValue(json, Translation.class );

        Book book = (Book)exchange.getProperty("BOOK_ID");
        book.setTranslatedTitle(translation.getTitle());
        message.setBody(book);
    }
}
