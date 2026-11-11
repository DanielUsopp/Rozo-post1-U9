# Recordatorios Citas Service

## Descripción
Repositorio del post-contenido de la Unidad 9 de Patrones de Diseño de Software. Un único proyecto Spring Boot (`recordatorios-citas-service`) que busca las citas médicas de las próximas 24 horas que todavía no tienen recordatorio y lo envía por el canal que prefiere el paciente (correo o SMS). El canal de envío es una Strategy (`ReminderChannel`), la elección del canal es un Factory Method (`ReminderChannelFactory`) y ambos llegan a `CitaReminderServiceImpl` por constructor, junto con el repositorio y el `Clock`.

## Parte 1 — Pruebas unitarias con JUnit 5 y Mockito
`CitaReminderServiceImplTest` prueba el servicio sin levantar el contexto de Spring ni una base de datos. Además de las tres pruebas del enunciado se agregaron dos escenarios: dos citas con canales distintos en la misma ejecución, y el texto exacto del mensaje que recibe el canal. `SmsReminderChannelTest` y `ReminderChannelFactoryImplTest` prueban por separado los dos colaboradores que el servicio recibe ya construidos.

## Decisiones de diseño

### Punto de decisión 1 — Mock, Stub o Fake para cada colaborador del servicio
| Colaborador | Test Double elegido | Qué necesita la prueba |
|-------------|---------------------|------------------------|
| `CitaRepository` | Mock | Controlar qué citas devuelve la consulta **y** comprobar que `save()` se llamó una vez por cita |
| `ReminderChannelFactory` / `ReminderChannel` | Mock | Comprobar qué canal se pidió y que ese canal recibió la cita |
| `Clock` | Fake (`Clock.fixed`) | Un "ahora" que no cambie entre ejecuciones |

**`CitaRepository` → Mock.** Un Stub alcanzaría para que `findByFechaHoraCitaBetweenAndRecordatorioEnviadoFalse` devuelva una lista, pero la mitad del comportamiento del servicio está en lo que hace después: marcar la cita como enviada y guardarla. Eso solo se puede confirmar con `verify(citaRepository).save(cita)` y, en el caso contrario, con `verify(citaRepository, never()).save(any())`. Cuando la prueba depende de la respuesta y también de la interacción, el colaborador es un Mock.

**`ReminderChannelFactory` / `ReminderChannel` → Mock.** Lo que se prueba es la selección de la estrategia: que una cita con `canalPreferido = EMAIL` haga que el servicio pida el canal `EMAIL` y que ese canal, y no otro, reciba la cita. Un Stub de la Factory devolvería un canal, pero no permitiría saber si el canal lo usó el servicio ni con qué datos. La prueba `citasConCanalesDistintos_pideCadaCanalALaFactory` lo deja explícito: con dos mocks de canal se verifica que cada cita llegó al suyo y que la de SMS nunca pasó por el de correo.

**`Clock` → Fake, no Mock.** `LocalDateTime.now(clock)` usa internamente `instant()` y `getZone()`; mockear `Clock` con Mockito obliga a stubbear ambos métodos y a confiar en que no se use ningún otro. `Clock.fixed(...)` ya es una implementación real del JDK con el instante congelado, así que el servicio se comporta igual que en producción y la prueba de la ventana puede comparar fechas exactas (`2026-08-10T09:00` a `2026-08-11T09:00`).

En `ReminderChannelFactoryImplTest` aparece un cuarto tipo: el `NotificadorSmsGateway` que recibe `SmsReminderChannel` es un **Dummy**. Solo completa el constructor; esa prueba compara instancias y nunca envía un mensaje.

### Punto de decisión 2 — Diagnóstico de `enviarRecordatorioUrgente` antes del refactor
La primera versión del método funciona, pero no se puede probar en aislamiento. La línea responsable es:

```java
ReminderChannel canalUrgente = new SmsReminderChannel(new TwilioNotificadorSmsGateway());
```

El canal y el gateway nacen dentro del propio método, en cada llamada. La prueba no tiene ningún punto donde entregar otro objeto: el `@Mock` que se declare en la clase de prueba nunca llega a ese `new`. Por eso el escenario que más interesa, "el proveedor de SMS falla y la cita no debe quedar marcada como enviada", no se puede escribir; lo único que se podría hacer es ejecutar el método y mirar la consola.

`enviarRecordatoriosPendientes()` no tiene ese problema porque no construye nada: recibe la Factory por constructor y la Factory entrega el canal. En la prueba ese constructor recibe un mock y se controla todo lo que pasa después. La diferencia entre los dos métodos no está en la lógica, está en quién crea el colaborador.

## Ejecutar las pruebas
```bash
mvn test
```

## Herramientas utilizadas
- Java 21, Spring Boot 3.3, Spring Data JPA, H2, Lombok
- JUnit 5, Mockito, Apache Maven, Git, GitHub
