package com.babelfish.controller;

import com.babelfish.model.Translation;
import com.babelfish.service.BabelFish;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
public class TranslateController {

    final private BabelFish babelFish;

    public TranslateController(BabelFish babelFish) {
        this.babelFish = babelFish;
    }

    @GetMapping("/translate")
    @ResponseBody
    public Translation translateTitle(
                            @RequestParam(name = "title", required = true) String title,
                            @RequestParam(name = "language", required = true) String language,
                            @RequestParam(name = "body", required = false) String body) {
        System.out.println("Original Title :" + title + " to be translated to " + language);
        Translation translation = new Translation();
        String translateBookTitle = babelFish.translateBookTitle(title, new Locale(language));
        translation.setTitle(translateBookTitle);
        translation.setOriginalTitle(title);
        System.out.println("Translation :" + translation);
        return  translation;
    }

}
