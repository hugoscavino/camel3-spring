package org.scavino.model;

import java.util.StringJoiner;

public class Translation {
    private String originalTitle;
    private String title;
    private String body;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Translation.class.getSimpleName() + "[", "]")
                .add("title='" + title + "'")
                .add("body='" + body + "'")
                .toString();
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }
}
