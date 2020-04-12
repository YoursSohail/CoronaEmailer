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
import java.util.List;
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

    @Scheduled(cron = "0 41 16 * * ?")
    public void testingScheduler(){
        scrapForCovidStats();
    }

    public void scrapForCovidStats(){
        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        String url = "https://www.google.com/search?sa=X&sxsrf=ALeKk01KTM8iFFllA-uXvU4GjGrlcWQdbw:1586533651619&q=COVID-19&biw=1366&bih=673";
        try {
            HtmlPage page = webClient.getPage(url);
            Document doc = Jsoup.parse(page.asXml());
            Elements main = doc.getElementsByClass("qyEGdc");

            //Worldwide
            Element worldwide = main.get(2);
            Elements worldStats = worldwide.getElementsByClass("yeRnY sz9i9");
            String worldConfirmed = worldStats.get(0).ownText();
            String worldRecovered = worldStats.get(1).ownText();
            String worldDeaths = worldStats.get(2).ownText();

            System.out.println("WorldConfirmed - "+worldConfirmed);
            System.out.println("WorldRecovered - "+worldRecovered);
            System.out.println("WorldDeaths - "+worldDeaths);


            //India
            Element india = main.get(1);
            Elements count = india.getElementsByClass("yeRnY sz9i9");
            String confirmed = count.get(0).ownText();
            String recovered = count.get(1).ownText();
            String deaths = count.get(2).ownText();
            System.out.println("Total Confirmed India - "+confirmed);
            System.out.println("Total Recovered India - "+recovered);
            System.out.println("Total Deaths India - "+deaths);


            sendEmail(confirmed,recovered,deaths,
                    worldConfirmed,worldRecovered,worldDeaths);




        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendEmail(String confirmed, String recovered, String deaths, String worldConfirmed, String worldRecovered, String worldDeaths){
        final String from = mailCofig.getEmailFrom();

        List<EmailModel> emails = repository.findAll();
        System.out.println("All emails: "+emails);
        for(EmailModel to : emails) {

            System.out.println("Sending email to: "+to.getEmail());

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
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to.getEmail()));
                msg.setSubject("Testing Covid Emailer");
                System.out.println("Sending....");


                /*String content = "<p>Total Confirmed India - " + confirmed + "</p>" +
                        "<p>Total Recovered India - " + recovered + "</p>" + "<p>Total Deaths India - " +
                        deaths + "</p>"+"<hr>"+
                        "<p>Total Confirmed Worldwide - " + worldConfirmed + "</p>" +
                        "<p>Total Recovered Worldwide - " + worldRecovered + "</p>" +
                        "<p>Total Deaths Worldwide - "+ worldDeaths;*/

                String content = contentBuilder.build(confirmed,recovered,deaths,
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
    }


}
