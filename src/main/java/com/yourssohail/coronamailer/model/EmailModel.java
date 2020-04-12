package com.yourssohail.coronamailer.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "emails")
public class EmailModel {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Email
   private String email;

    public EmailModel() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "EmailModel{" +
                "id=" + id +
                ", email='" + email + '\'' +
                '}';
    }
}
