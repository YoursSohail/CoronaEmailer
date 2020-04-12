package com.yourssohail.coronamailer.controller;

import com.yourssohail.coronamailer.model.EmailModel;
import com.yourssohail.coronamailer.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class EmailController {

    @Autowired
    private EmailRepository repository;

    @GetMapping()
    public String showForm(Model model){
        EmailModel email = new EmailModel();
        model.addAttribute("emailobj",email);
        return "form";
    }

    @PostMapping("/subscribe")
    public String emailRegistration(@ModelAttribute("emailobj") EmailModel email){
        System.out.println(email);
        //repository.save(email);
        return "success";
    }
}
