package org.scavino.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.support.MessageHelper;
import org.springframework.stereotype.Component;

@Component
public class PrintBodyAsStringProcessor  implements Processor {

    public void process(Exchange exchange) throws Exception {
        Message message = exchange.getIn();
        String json = MessageHelper.extractBodyAsString(message);
        System.out.println(json);
    }
}
