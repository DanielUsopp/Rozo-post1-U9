package com.clinica.recordatorios.channel;

import com.clinica.recordatorios.channel.gateway.NotificadorSmsGateway;
import com.clinica.recordatorios.domain.CanalNotificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

class ReminderChannelFactoryImplTest {

    private EmailReminderChannel canalEmail;
    private SmsReminderChannel canalSms;
    private ReminderChannelFactoryImpl factory;

    @BeforeEach
    void setUp() {
        canalEmail = new EmailReminderChannel();
        // Dummy: el gateway solo completa el constructor, esta prueba no envía nada.
        canalSms = new SmsReminderChannel(mock(NotificadorSmsGateway.class));
        factory = new ReminderChannelFactoryImpl(canalEmail, canalSms);
    }

    @Test
    void obtenerCanal_email_retornaElCanalDeCorreo() {
        assertSame(canalEmail, factory.obtenerCanal(CanalNotificacion.EMAIL));
    }

    @Test
    void obtenerCanal_sms_retornaElCanalDeSms() {
        assertSame(canalSms, factory.obtenerCanal(CanalNotificacion.SMS));
    }
}
