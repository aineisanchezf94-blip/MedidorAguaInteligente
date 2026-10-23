# 💧 HydroGuard - Sistema Móvil de Medición y Control Inteligente de Consumo de Agua (IoT)

> **Aplicación Android desarrollada en Kotlin para la gestión telemétrica de consumo hídrico, facturación automatizada bajo estándares ISO de seguridad y control remoto de válvulas de agua.**

---

## 📋 Tabla de Contenidos
1. [Identificación de la Problemática](#1-identificación-de-la-problemática)
2. [Dispositivos y Sistemas Conectados](#2-dispositivos-y-sistemas-a-conectar)
3. [Objetivo de la Comunicación IoT](#3-objetivo-de-la-comunicación-entre-dispositivos)
4. [Funcionalidades Clave de la Aplicación](#4-funcionalidades-clave-de-la-aplicación-móvil)
5. [Flujo Telemétrico](#5-tipo-de-interacción-y-flujo-telemétrico)
6. [Selección de Herramientas de Desarrollo y Justificación Técnica](#6-selección-de-herramientas-de-desarrollo-y-justificación-técnica)
7. [Diseño de Interfaz de Usuario (UI) & Mockups](#7-diseño-de-la-interfaz-de-usuario-ui-y-mockups-de-la-app)
8. [Autenticación, Sesión y Seguridad en Formularios](#8-módulo-de-autenticación-gestión-de-sesión-y-seguridad-en-formularios)
9. [Instrucciones para Ejecución del Proyecto](#9-instrucciones-para-ejecución-del-proyecto)

---

## 1. Identificación de la Problemática

En la gestión tradicional del suministro de agua potable residencial y comercial se presentan tres desafíos críticos:

1. **Invisibilidad del consumo en tiempo real:** Los usuarios conocen su nivel de consumo únicamente cuando reciben la factura mensual, imposibilitando la toma de decisiones oportunas para el ahorro hídrico.
2. **Detección tardía de fugas de agua:** Fugas no visibles en tuberías internas ocasionan pérdidas de miles de litros de agua y sobrecostos financieros elevados antes de ser detectadas.
3. **Inexactitud y vulnerabilidad en la lectura manual:** Los procesos tradicionales de toma de lectura presencial son propensos a errores de digitación, fraude o demoras operativos.

> **Propuesta de Solución:**  
> **HydroGuard** es una solución móvil integrada con IoT que permite la captura de datos telemétricos, la validación bajo estándares ISO de seguridad, el procesamiento transparente de facturación por rangos y el control remoto en tiempo real de válvulas de agua ante emergencias.

---

## 2. Dispositivos y Sistemas a Conectar

Para resolver la problemática planteada, el ecosistema requiere la interconexión de los siguientes componentes:

| Dispositivo / Sistema | Tipo | Función Principal |
| :--- | :--- | :--- |
| **Medidor Ultrasónico IoT** | Sensor Telemétrico | Mide el flujo continuo ($L/min$), la presión ($Bar$) y el acumulado de consumo ($m^3$). |
| **Servomotor de Válvula Principal** | Actuador de Control | Apertura y cierre físico remoto de la tubería principal de agua. |
| **Servidor Nube / Gateway IoT** | Intermediario REST/MQTT | Procesa la comunicación bidireccional, encripta datos y gestiona tokens de verificación. |
| **Aplicación Móvil HydroGuard** | Interfaz Android | Permite la interacción del usuario, validación ISO de datos, cálculo de tarifas y control de dispositivos. |

---

## 3. Objetivo de la Comunicación entre Dispositivos

El objetivo central de la red de comunicación es establecer un **canal bidireccional seguro y en tiempo real** entre los sensores físicos, el servidor en la nube y la aplicación móvil Android:

* **Transmisión de Subida (Telemetría):** Enviar de forma constante las métricas de consumo y lecturas del medidor hacia la app para su análisis.
* **Transmisión de Bajada (Control):** Enviar órdenes de acción desde la app hacia el servomotor (ej. orden de corte inmediato al detectar una fuga crítica).
* **Seguridad y Verificación ISO:** Garantizar que cada paquete de datos viaje sanitizado y protegido con tokens de autenticidad (`SEC-XXXXXX-X`) para evitar ataques de alteración de lecturas.

---

## 4. Funcionalidades Clave de la Aplicación Móvil

La aplicación implementa 3 funcionalidades clave organizadas en ventanas independientes (`Activities`):

### A. Ventana de Métricas y Cálculo de Facturación ISO (`MetricasActivity`)
* **Captura de Datos:** Formulario estructurado para registrar el Código del Medidor, Lectura Actual ($m^3$) y Lectura Anterior ($m^3$).
* **Validación Rigurosa:** Control de errores de entrada (valores no numéricos, campos vacíos, consumos negativos o lecturas que excedan el límite físico del medidor).
* **Procesamiento de Tarifas Escaladas:** Cálculo automático basado en rangos de consumo (Base, Intermedio, Exceso), cálculo de IVA ($15\%$) y emisión de comprobante encriptado.

### B. Ventana de Catálogo de Productos y Servicios (`ServiciosActivity`)
* **Listado Interactivo:** Presentación de planes hídricos, equipos IoT y visitas técnicas de mantenimiento mediante un `RecyclerView`.
* **Procesamiento de Órdenes:** Confirmación y contratación de servicios con retroalimentación inmediata mediante cuadros de diálogo.

### C. Ventana de Alertas y Control IoT (`AlertasActivity`)
* **Centro de Alertas:** Clasificación de eventos según severidad (Crítica, Moderada, Informativa) para fugas o anomalías de presión.
* **Control Remoto Activo:** Botón directo para conmutar el estado de la Válvula Principal (ABIERTA / CERRADA) en tiempo real.

---

## 5. Tipo de Interacción y Flujo Telemétrico

```
  [ Medidor IoT (Sensor) ]  ---> (Envío de Flujo/Presión) ---> [ Servidor Nube / Gateway ]
                                                                       |
  [ Válvula (Actuador) ]   <--- (Corte Mecánico Remoto)  <---  [ App Móvil HydroGuard ]
```

---

## 6. Selección de Herramientas de Desarrollo y Justificación Técnica

### A. Entorno de Desarrollo y Lenguaje
* **Android Studio IDE:** Entorno oficial optimizado con herramientas de depuración, emulación y compilación Gradle.
* **Kotlin:** Lenguaje de desarrollo nativo oficial que garantiza *Null Safety*, reducción de código boilerplate y compatibilidad con Corrutinas asíncronas.

### B. Herramientas y Bibliotecas para Conectividad IoT y Red
* **Paho MQTT Client / HiveMQ SDK:** Protocolo ligero Pub/Sub diseñado para dispositivos IoT con bajo ancho de banda e intermitencia de red. Permite recibir métricas e instanciar comandos de corte de válvula con mínima latencia.
* **Retrofit 2 & OkHttp 3:** Cliente REST para consumo de microservicios HTTP, con soporte para interceptores de seguridad, SSL/TLS Pinning y parsing automático JSON.
* **Kotlin Coroutines & Flow:** Manejo de programación reactiva asíncrona en hilos secundarios (`Dispatchers.IO`), asegurando una interfaz fluida sin bloqueos en la UI.

### C. Autenticación de Usuarios y Seguridad
* **Firebase Authentication & Credential Manager API:** Permite el inicio de sesión seguro (OAuth 2.0 / Email), asegurando que solo los propietarios autorizados gestionen sus medidores.
* **Android KeyStore & EncryptedSharedPreferences:** Almacenamiento local cifrado de tokens JWT e ID de dispositivos, evitando filtraciones de credenciales.

### D. Almacenamiento y Gestión de Datos
* **Persistencia Local (Room Database - SQLite):** Almacenamiento offline-first de lecturas históricas y registros de alertas para consulta sin conexión a internet.
* **Sincronización en la Nube (Firebase Firestore):** Base de datos NoSQL reactiva para sincronizar en tiempo real el estado de las válvulas y las alertas críticas entre sensores y la app.

### E. Justificación Técnica y Estándares Android (MAD)
1. **Patrón de Arquitectura MVVM / Repository:** Sigue los estándares modernos de desarrollo Android recomendados por Google (Android Architecture Components), garantizando separación de responsabilidades y testabilidad.
2. **Interoperabilidad de Datos IoT:** El uso de JSON y tramas MQTT estandarizadas permite integrar diferentes modelos de sensores y servomotores sin reestructurar el código de la aplicación.
3. **Eficiencia Energética y de Red:** El protocolo MQTT combinado con corrutinas optimiza el consumo de batería del smartphone y minimiza el uso de datos móviles en transmisiones telemétricas continuas.

---

## 7. Diseño de la Interfaz de Usuario (UI) y Mockups de la App

El diseño visual se rige bajo **Material Design 3**, empleando un tema hídrico moderno con fondo azul oscuro nocturno (`#0D1424`), tarjetas elevadas (`#162035`) y acentos en verde turquesa telemétrico (`#3DDEB3`).

### A. Mapa de Navegación entre Ventanas

```
                           ┌─────────────────────────┐
                           │    LoginActivity.kt     │
                           │  (Autenticación ISO)    │
                           └────────────┬────────────┘
                                        │ (Login Exitoso)
                                        ▼
                           ┌─────────────────────────┐
                           │    MainActivity.kt      │
                           │   (Dashboard Links)     │
                           └────────────┬────────────┘
                                        │
         ┌──────────────────────────────┼──────────────────────────────┐
         │ (Intent)                     │ (Intent)                     │ (Intent)
         ▼                              ▼                              ▼
┌──────────────────┐          ┌──────────────────┐          ┌──────────────────┐
│ MetricasActivity │          │ ServiciosActivity│          │ AlertasActivity  │
│ (Formulario ISO) │          │ (Catálogo Planes)│          │ (Control IoT)    │
└──────────────────┘          └──────────────────┘          └──────────────────┘
```

---

### B. Mockup de las Pantallas Principales

#### 1. Pantalla de Autenticación (`activity_login.xml`)
```
+-------------------------------------------------------+
|                       ( Logo )                        |
|                      HydroGuard                       |
|          Autenticación Segura de Usuario IoT          |
+-------------------------------------------------------+
| INGRESO AL SISTEMA                                    |
|                                                       |
| [ Correo Electrónico: usuario@hydroguard.io         ] |
| [ Contraseña:         ••••••••••••                  ] |
| [ Token ID Medidor:   MTR-8842-IOT                  ] |
|                                                       |
| [ 🔓 Iniciar Sesión Segura                          ] |
|                                                       |
| Protegido bajo estándar ISO/IEC 27001 & OWASP MASVS   |
+-------------------------------------------------------+
```

#### 2. Pantalla Principal / Dashboard (`activity_main.xml`)
```
+-------------------------------------------------------+
|  HydroGuard                           [ Cerrar Sesión ]|
|  usuario@hydroguard.io                                |
+-------------------------------------------------------+
| ENLACES A OTRAS VENTANAS DE LA APLICACIÓN             |
|                                                       |
| +---------------------------------------------------+ |
| | 📊 Ventana: Métricas y Cálculo ISO    [ Abrir ➔ ] | |
| | Formulario de captura y validación de consumo.    | |
| +---------------------------------------------------+ |
|                                                       |
| +---------------------------------------------------+ |
| | 🛒 Ventana: Catálogo de Servicios     [ Abrir ➔ ] | |
| | Lista de productos, planes y ordenes de servicio. | |
| +---------------------------------------------------+ |
|                                                       |
| +---------------------------------------------------+ |
| | 🚨 Ventana: Alertas y Control IoT     [ Abrir ➔ ] | |
| | Monitoreo de fugas y corte de válvula remota.     | |
| +---------------------------------------------------+ |
+-------------------------------------------------------+
| [ Métricas ]          [ Servicios ]      [ Alertas ]  |
+-------------------------------------------------------+
```

#### 3. Ventana 1: Métricas y Cálculo ISO (`activity_metricas.xml`)
```
+-------------------------------------------------------+
| [ < ] Ventana: Métricas y Consumo                     |
+-------------------------------------------------------+
| FORMULARIO DE CAPTURA Y CÁLCULO ISO                   |
|                                                       |
| [ Código ID Medidor:  MTR-8842-IOT                  ] |
| [ Lectura Actual (m³): 125.50                       ] |
| [ Lectura Anterior (m³): 102.00                     ] |
|                                                       |
| [ 🔒 Procesar Consumo y Factura Segura              ] |
|                                                       |
| OPERACIONES ANALÍTICAS RÁPIDAS                        |
| +-----------------------+   +-----------------------+ |
| |  ADD (+)              |   |  SUB (-)              | |
| +-----------------------+   +-----------------------+ |
| |  MUL (x)              |   |  DIV (/)              | |
| +-----------------------+   +-----------------------+ |
|                                                       |
| RESULTADO DEL PROCESAMIENTO                           |
| $31.85                                                |
+-------------------------------------------------------+
```

---

## 8. Módulo de Autenticación, Gestión de Sesión y Seguridad en Formularios

### A. Sistema de Autenticación (`LoginActivity.kt` y `AuthManager.kt`)
* **Gestión de Sesión:** `AuthManager` gestiona el almacenamiento de credenciales y tokens en `SharedPreferences` cifradas.
* **Control de Navegación Protegida:** Al iniciar la app, `LoginActivity` redirige a `MainActivity` si ya existe una sesión guardada. Si un usuario no autenticado intenta acceder a `MainActivity`, es interceptado y redirigido automáticamente a la pantalla de login.
* **Cierre de Sesión Seguro:** Al presionar `[ Cerrar Sesión ]`, se remueven los tokens guardados y se finalizan los procesos activos.

### B. Validación Rigurosa de Datos en Formularios

| Formulario | Campo | Regla de Validación | Mensaje de Error Emitido |
| :--- | :--- | :--- | :--- |
| **Login** | Correo Electrónico | Patrón Regex de Email Válido | `Error Auth-02: Formato de correo no válido` |
| **Login** | Contraseña | Longitud mínima de 6 caracteres | `Error Auth-04: Mínimo 6 caracteres requeridos` |
| **Login** | Token Medidor | Patrón alfanumérico mayúsculas y guiones | `Error Auth-06: Formato de token inválido` |
| **Métricas** | ID Medidor | Campo obligatorio / Formato código | `Error ISO-01 / ISO-02: Código requerido` |
| **Métricas** | Lectura Actual | Número flotante positivo ($\ge 0$) $\le 99,999$ | `Error ISO-05 / ISO-06: Límite excede rango` |
| **Métricas** | Consumo Neto | Lectura Actual $\ge$ Lectura Anterior | `Error ISO-10: Lectura actual debe ser mayor` |

---

## 9. Instrucciones para Ejecución del Proyecto

### Prerrequisitos:
* **Android Studio** Hedgehog / Iguana / Jellyfish (2023.3+) o posterior.
* **JDK 11** o superior.
* **Android SDK** API 34 (Android 14) o superior.

### Pasos para Ejecutar:
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/TU_USUARIO/MedidorAguaInteligente.git
   ```
2. Abrir el proyecto en **Android Studio**.
3. Sincronizar Gradle (`Sync Project with Gradle Files`).
4. Seleccionar un emulador Android (ej. Pixel 8 API 34) o un dispositivo físico conectado mediante depuración USB.
5. Presionar **Run 'app'** (`Shift + F10`).
