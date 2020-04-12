package com.yourssohail.coronamailer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailContentBuilder {

    private TemplateEngine templateEngine;

    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String build(String countryName,String countryConfirmed,String countryRecovered,String countryDeaths,
                        String worldwideConfirmed,String worldwideRecovered,String worldwideDeaths){

        Context context = new Context();
        context.setVariable("countryName",countryName);
        context.setVariable("countryConfirmed",countryConfirmed);
        context.setVariable("countryRecovered",countryRecovered);
        context.setVariable("countryDeaths",countryDeaths);
        context.setVariable("worldwideConfirmed",worldwideConfirmed);
        context.setVariable("worldwideRecovered",worldwideRecovered);
        context.setVariable("worldwideDeaths",worldwideDeaths);
        return templateEngine.process("email",context);
    }
}
