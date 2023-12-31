package com.example.spacer.spacerbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import org.thymeleaf.context.Context;

@Service
public class EmailTemplateService {

  @Autowired
  private TemplateEngine templateEngine;
  private final String urlBase = System.getenv("FRONTEND_URL") == null ? "http://localhost:3000" : System.getenv("FRONTEND_URL");

  public String getTemplateForgotPwd(String subject, String reqId){

    Context context = new Context();
    context.setVariable("subject", subject);
    context.setVariable("title", "Recuperar contraseña");
    context.setVariable("urlCode", reqId);

    return templateEngine.process("forgot-password", context);
  }

  public String getTemplatePwdChanged(String subject){

      Context context = new Context();
      context.setVariable("subject", subject);
      context.setVariable("title", "Contraseña cambiada");
      context.setVariable("urlReset", urlBase + "/pages/perfil");

      return templateEngine.process("password-changed", context);
  }

  public String getTemplateNewClient(String subject, String username){

      Context context = new Context();
      context.setVariable("subject", "Gracias por registrarte");
      context.setVariable("title", subject);
      context.setVariable("username", username);
      context.setVariable("urlPerfil", urlBase + "/pages/perfil");

      return templateEngine.process("new-client", context);
  }

}
