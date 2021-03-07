package org.scavino.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Converter;
import org.apache.camel.TypeConverters;
import org.scavino.model.Book;
import org.scavino.model.OrderConfirmation;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Converter
public class BookTypeConverter implements TypeConverters {

    private final ObjectMapper mapper;

    public BookTypeConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Converter
    public InputStream bookToInputStream(Book source) {
        try {
            byte[] decodedBytes = mapper.writeValueAsBytes(source);
            InputStream is = new ByteArrayInputStream(decodedBytes);
            return is;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Converter
    public Book inputStreamToBook(InputStream source) {
        try {
            return mapper.readValue(source, Book.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Converter
    public InputStream orderConfirmationToInputStream(OrderConfirmation source) {
        try {
            byte[] decodedBytes = mapper.writeValueAsBytes(source);
            InputStream is = new ByteArrayInputStream(decodedBytes);
            return is;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Converter
    public OrderConfirmation inputStreamToOrderConfirmation(InputStream source) {
        try {
            return mapper.readValue(source, OrderConfirmation.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
