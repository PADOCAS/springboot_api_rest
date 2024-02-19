package com.ldsystems.api.rest.springbootapirest.service;

import com.ldsystems.api.rest.springbootapirest.model.ConfigGeral;
import com.ldsystems.api.rest.springbootapirest.repository.ConfigGeralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EnviarEmailService {

    @Autowired
    private ConfigGeralRepository configGeralRepository;

    public void enviarEmail(String assuntoEmail, String emailDestino, String mensagemEmail) throws MessagingException {
        if (emailDestino != null
                && !emailDestino.trim().isEmpty()) {
            ConfigGeral configGeral = configGeralRepository.findUniqueConfigGeral();

            if (configGeral != null
                    && configGeral.getEmail() != null
                    && configGeral.getSenha() != null
                    && configGeral.getSmtpHost() != null
                    && configGeral.getSmtpPort() != null
                    && configGeral.getSocketPort() != null) {
                Properties properties = new Properties();
                properties.put("mail.smtp.ssl.trust", "*"); //Permissão SSL
                properties.put("mail.smtp.auth", true); //Autenticação
                properties.put("mail.smtp.starttls", true); //Autorização
                properties.put("mail.smtp.host", configGeral.getSmtpHost()); //Servidor do Google
                properties.put("mail.smtp.port", configGeral.getSmtpPort()); //Porta Servidor
                properties.put("mail.smtp.socketFactory.port", configGeral.getSocketPort()); //Porta Socket
                properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //Classe de conexão Socket

                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(configGeral.getEmail(), configGeral.getSenha());
                    }
                });

                Address[] toUser = InternetAddress.parse(emailDestino);
                Message message = new MimeMessage(session);
                // Definir o tipo de conteúdo como HTML
                message.setContent(mensagemEmail, "text/html");
                message.setFrom(new InternetAddress(configGeral.getEmail())); //E-mail Remetente
                message.setRecipients(Message.RecipientType.TO, toUser); //E-mail Destinatário
                message.setSubject(assuntoEmail);
//            message.setText(mensagemEmail);  //Só assim não fica HTML o corpo do email!

                Transport.send(message);
            } else {
                throw new MessagingException("Configuração do Servidor de E-mail não encontrada!\nVerifique com o administrador do sistema para alimentar as informações do servidor de E-mail para o sistema (public.config_geral).\n\nApós feita as configurações necessárias, tente executar a recuperação de acesso novamente.");
            }
        } else {
            throw new MessagingException("E-mail de destino inválido!\nNão foi possível enviar o E-mail de recupação.");
        }
    }
}
