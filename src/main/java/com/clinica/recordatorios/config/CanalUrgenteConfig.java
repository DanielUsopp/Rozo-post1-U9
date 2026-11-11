package com.clinica.recordatorios.config;

import com.clinica.recordatorios.channel.ReminderChannel;
import com.clinica.recordatorios.channel.SmsReminderChannel;
import com.clinica.recordatorios.channel.gateway.NotificadorSmsGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CanalUrgenteConfig {

    // Único lugar del proyecto donde se construye el canal urgente: el
    // composition root, fuera de la lógica de negocio del servicio.
    @Bean
    public ReminderChannel canalUrgente(NotificadorSmsGateway gateway) {
        return new SmsReminderChannel(gateway);
    }
}
