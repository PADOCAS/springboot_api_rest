package com.ldsystems.api.rest.springbootapirest.util;

public class Constants {

    public static String getUriRecuperacaoSenhaLinkForEmail() {
        //Para rodar localmente, precisamos por o IP da máquina local (base teste)
         return "http://localhost:4200/recuperar-senha";
        //Para rodar em produção:
//        return "http://192.168.0.17:8080/";
    }
}
