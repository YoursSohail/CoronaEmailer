package com.yourssohail.coronamailer.service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.yourssohail.coronamailer.config.MailCofig;
import com.yourssohail.coronamailer.model.EmailModel;
import com.yourssohail.coronamailer.repository.EmailRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

@Service
public class EmailService {

    private EmailRepository repository;
    private MailCofig mailCofig;
    private MailContentBuilder contentBuilder;

    @Autowired
    public EmailService(EmailRepository repository, MailCofig mailCofig, MailContentBuilder contentBuilder) {
        this.repository = repository;
        this.mailCofig = mailCofig;
        this.contentBuilder = contentBuilder;
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void testingScheduler(){
        scrapForCovidStats();
    }

    public void scrapForCovidStats(){
        List<EmailModel> emails = repository.findAll();

        for(EmailModel to : emails) {
            System.out.println("Sending email to: "+to.getEmail());
            WebClient webClient = new WebClient();
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);

            String url = "https://www.google.com/search?q=corona+cases+"+to.getCountry().toLowerCase();
            try {
                HtmlPage page = webClient.getPage(url);
                Document doc = Jsoup.parse(page.asXml());
                Elements main = doc.getElementsByClass("qyEGdc");

                System.out.println(">>> Main "+main);


                //Worldwide
                Element worldwide = main.get(1);
                Elements worldStats = worldwide.getElementsByClass("yeRnY sz9i9");
                String worldConfirmed = worldStats.get(0).ownText();
                String worldRecovered = worldStats.get(1).ownText();
                String worldDeaths = worldStats.get(2).ownText();

                System.out.println("WorldConfirmed - " + worldConfirmed);
                System.out.println("WorldRecovered - " + worldRecovered);
                System.out.println("WorldDeaths - " + worldDeaths);


                //India
                Element india = main.get(0);
                Elements count = india.getElementsByClass("yeRnY sz9i9");
                String confirmed = count.get(0).ownText();
                String recovered = count.get(1).ownText();
                String deaths = count.get(2).ownText();
                System.out.println("Total Confirmed India - " + confirmed);
                System.out.println("Total Recovered India - " + recovered);
                System.out.println("Total Deaths India - " + deaths);


                sendEmail(to.getEmail(),to.getCountry(),confirmed, recovered, deaths,
                        worldConfirmed, worldRecovered, worldDeaths);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendEmail(String to,String country,String confirmed, String recovered, String deaths, String worldConfirmed, String worldRecovered, String worldDeaths){
        final String from = mailCofig.getEmailFrom();



            System.out.println("Sending email to: "+to);

            String host = "smtp.gmail.com";

            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, mailCofig
                    .getPassword());
                }
            });

            session.setDebug(true);

            try {
                Message msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(from));
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                msg.setSubject("Corona Emailer : Daily Stats");
                System.out.println("Sending....");


                String content = contentBuilder.build(country,confirmed,recovered,deaths,
                        worldConfirmed,worldRecovered,worldDeaths);



                msg.setContent(
                        content,
                        "text/html; charset=UTF-8"
                );
                Transport.send(msg);
                System.out.println("Message sent !");

            } catch (MessagingException e) {
                e.printStackTrace();
            }

    }

    public List<String> getCountryList(){
        String[] countryCode = Locale.getISOCountries();
        List<String> countryList = new ArrayList<>();
        for(String c : countryCode){
            Locale obj = new Locale("",c);
            countryList.add(obj.getDisplayCountry());
        }
        return countryList;
    }


}
