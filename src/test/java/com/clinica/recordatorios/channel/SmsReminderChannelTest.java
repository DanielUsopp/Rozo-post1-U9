package com.clinica.recordatorios.channel;

import com.clinica.recordatorios.channel.gateway.NotificadorSmsGateway;
import com.clinica.recordatorios.domain.CanalNotificacion;
import com.clinica.recordatorios.domain.Cita;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmsReminderChannelTest {

    @Mock
    private NotificadorSmsGateway gateway;

    @InjectMocks
    private SmsReminderChannel canalSms;

    @Test
    void enviar_citaConTelefono_delegaEnElGatewayConElContactoDelPaciente() {
        Cita cita = new Cita(7L, "Elena Castro", "3157778899",
            LocalDateTime.of(2026, 8, 10, 14, 0), CanalNotificacion.SMS, false);

        canalSms.enviar(cita, "Recordatorio de prueba");

        verify(gateway, times(1)).enviarSms("3157778899", "Recordatorio de prueba");
        verifyNoMoreInteractions(gateway);
    }
}
