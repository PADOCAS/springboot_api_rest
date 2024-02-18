package com.ldsystems.api.rest.springbootapirest.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EnviarEmailService {

    public void enviarEmail(String assuntoEmail, String emailDestino, String mensagemEmail) throws MessagingException {
        if (emailDestino != null
                && !emailDestino.trim().isEmpty()) {
            Properties properties = new Properties();
            properties.put("mail.smtp.ssl.trust", "*"); //Permissão SSL
            properties.put("mail.smtp.auth", true); //Autenticação
            properties.put("mail.smtp.starttls", true); //Autorização
            properties.put("mail.smtp.host", "smtp.gmail.com"); //Servidor do Google
            properties.put("mail.smtp.port", "465"); //Porta Servidor
            properties.put("mail.smtp.socketFactory.port", "465"); //Porta Socket
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //Classe de conexão Socket

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("ldsystems.atendimento@gmail.com", "cvvm razg otxo syfe");
                }
            });

            Address[] toUser = InternetAddress.parse(emailDestino);
            Message message = new MimeMessage(session);
            // Definir o tipo de conteúdo como HTML
            message.setContent(mensagemEmail, "text/html");
            message.setFrom(new InternetAddress("ldsystems.atendimento@gmail.com")); //E-mail Remetente
            message.setRecipients(Message.RecipientType.TO, toUser); //E-mail Destinatário
            message.setSubject(assuntoEmail);
//            message.setText(mensagemEmail);  //Só assim não fica HTML o corpo do email!

            Transport.send(message);
        } else {
            throw new MessagingException("E-mail de destino inválido!\nNão foi possível enviar o E-mail de recupação.");
        }
    }
}
