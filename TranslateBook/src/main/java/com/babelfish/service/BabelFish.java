package com.babelfish.service;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class BabelFish {
    final public Locale english = Locale.ENGLISH;
    final public Locale french = Locale.FRANCE;
    final public Locale spanish = new Locale("es", "ES");

    public String translateBookTitle(String title, Locale locale){

        // TODO Do something interesting here, like go to an actual translation API AWS, Microsoft, google

        String tranlated_title = title;
            if (locale.getLanguage().equalsIgnoreCase(english.getLanguage())){
                tranlated_title = "A " + title;
            } else if (locale.getLanguage().equalsIgnoreCase(spanish.getLanguage())) {
                tranlated_title = "El " + title + " O";
            } else if (locale.getLanguage().equalsIgnoreCase(french.getLanguage())) {
                tranlated_title = "Le " + title + " La La La";
            } else {
                tranlated_title = "Da " + title;
            }

            return tranlated_title;
    }
}
