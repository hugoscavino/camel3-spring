package com.merchant.model;

import java.math.BigDecimal;
import java.util.StringJoiner;

/**
 * Simple Book Pojo add more attributes as required
 */
public class Book {

    private Long id;
    private String title;
    private String translatedTitle;
    private String isbn;
    private BigDecimal price;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(String translatedTitle) {
        this.translatedTitle = translatedTitle;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Book.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("title='" + title + "'")
                .add("translatedTitle='" + translatedTitle + "'")
                .add("isbn='" + isbn + "'")
                .add("price=" + price)
                .toString();
    }
}
