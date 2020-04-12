package com.yourssohail.coronamailer.repository;

import com.yourssohail.coronamailer.model.EmailModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<EmailModel,Integer> {
}
