package com.example.spacer.spacerbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import org.thymeleaf.context.Context;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class EmailTemplateService {

  @Autowired
  private TemplateEngine templateEngine;

  public String getTemplateForgotPwd(String subject){

    Context context = new Context();
    context.setVariable("subject", subject);
    context.setVariable("title", "Recuperar contraseña");
    context.setVariable("urlReset", "https://spacer-ecommerce.vercel.app/reset-password/");

    return templateEngine.process("forgot-password", context);
  }

  public String getTemplatePwdChanged(String subject){

      Context context = new Context();
      context.setVariable("subject", subject);
      context.setVariable("title", "Contraseña cambiada");
      context.setVariable("urlReset", "https://spacer-ecommerce.vercel.app/pages/perfil/");

      return templateEngine.process("password-changed", context);
  }

}
