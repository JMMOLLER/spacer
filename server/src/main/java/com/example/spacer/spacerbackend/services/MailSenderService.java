package com.example.spacer.spacerbackend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailSenderService {

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private EmailTemplateService emailTemplateService;

  private final MimeMessage message;
  private final MimeMessageHelper helper;

  public MailSenderService(JavaMailSender mailSender, EmailTemplateService emailTemplateService) throws MessagingException {
    this.mailSender = mailSender;
    this.emailTemplateService = emailTemplateService;
    this.message = mailSender.createMimeMessage();
    this.helper = new MimeMessageHelper(message, true);
  }

  public void sendForgotPwd(String to) throws MessagingException {
    String subject = "¿Olvidaste tu contraseña?";
    helper.setTo(to);
    helper.setSubject(subject);

    String htmlContent = emailTemplateService.getTemplateForgotPwd(subject);
    helper.setText(htmlContent, true);

    mailSender.send(message);
  }

  public void sendPwdChanged(String to) throws MessagingException {
    String subject = "¡Tu contraseña se actualizó!";

    helper.setTo(to);
    helper.setSubject(subject);

    String htmlContent = emailTemplateService.getTemplatePwdChanged(subject);
    helper.setText(htmlContent, true);

    mailSender.send(message);
  }
}
