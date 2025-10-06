package com.ftn.sbnz.service.configuration;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {

    @Autowired
    private KieContainer kieContainer;

    @Bean
    @Qualifier("cepKsession")
    public KieSession cepKsession() {
        KieSession cepSession = kieContainer.newKieSession("cepKsession");
        new Thread(cepSession::fireUntilHalt).start();
        return cepSession;
    }
}