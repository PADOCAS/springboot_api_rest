package com.ldsystems.api.rest.springbootapirest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Classe auxiliar para carregar qualquer bean junto com o Spring, através dessa classe podemos acessar qualquer Bean sem precisar injetar diretamente na classe!
 */
@Component
public class ApplicationContextLoad implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextLoad.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
