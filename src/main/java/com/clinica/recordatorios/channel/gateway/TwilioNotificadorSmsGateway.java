package com.clinica.recordatorios.channel.gateway;

import org.springframework.stereotype.Component;

@Component
public class TwilioNotificadorSmsGateway implements NotificadorSmsGateway {

    @Override
    public void enviarSms(String numeroDestino, String texto) {
        // En producción sería la llamada HTTP al proveedor de SMS. Se simula
        // para que el laboratorio no dependa de credenciales ni de red.
        System.out.printf("[SMS a %s] %s%n", numeroDestino, texto);
    }
}
