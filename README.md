# 🩺 Sistema de Autorizaciones de Atención (SIAA)

**TP3 – Seminario de Práctica Profesional**  
**Licenciatura en Informática – 2025**

---

## 📘 Descripción general

El **Sistema de Autorizaciones de Atención (SIAA)** es un prototipo funcional desarrollado en **Java** que simula el circuito de gestión de solicitudes médicas dentro de una obra social u organización de salud.

El proyecto implementa un modelo orientado a objetos con capas bien definidas (**UI, Application, Domain, Persistence**) y aplica principios de **encapsulamiento, herencia, polimorfismo y abstracción**, junto con manejo de excepciones y estructuras de datos en memoria.

> ⚙️ En esta versión, los datos se manejan íntegramente en memoria (sin conexión a base de datos) con el objetivo de validar la lógica del sistema y el flujo completo de autorizaciones.

---

## 🎯 Objetivo del prototipo

- Validar la coherencia del diseño orientado a objetos mediante una primera versión funcional.  
- Probar el recorrido completo del flujo **alta → evaluación → decisión → comunicación → archivo**.  
- Implementar reglas de negocio (vigencia, topes, duplicidad) desacopladas mediante una interfaz común.  
- Asegurar la trazabilidad de acciones mediante una **bitácora interna**.  

---

## 🧩 Estructura del proyecto

```

src/
├── ui/                → Interfaz por consola (menú principal)
│    └── MenuConsola.java
│
├── application/       → Casos de uso y orquestación
│    ├── ServicioAutorizaciones.java
│    ├── ServicioAntecedentes.java
│    └── RepositorioSolicitudes.java
│
├── domain/            → Lógica de negocio, entidades y reglas
│    ├── Solicitud.java
│    ├── ItemSolicitud.java
│    ├── EntradaBitacora.java
│    ├── Usuario.java, Administrador.java, MedicoAuditor.java
│    ├── EstadoSolicitud.java
│    ├── ReglaValidacion.java
│    ├── ReglaVigencia.java
│    ├── ReglaTopes.java
│    ├── ReglaDuplicidad.java
│    └── Excepciones de dominio (ReglaNegocioException, etc.)
│
├── persistence/       → Repositorio en memoria
│    └── RepositorioEnMemoria.java
│
└── infrastructure/    → (Reservado para futuras utilidades)

````

---

## 🖥️ Entorno de ejecución

- **JDK:** 17  
- **IDE utilizado:** IntelliJ IDEA / Eclipse (opcionalmente consola)  
- **Sistema operativo probado:** Windows 10 / PowerShell  
- **Tipo de interfaz:** Menú por consola  

---

## ⚙️ Compilación y ejecución

### 🔧 Compilación (Windows PowerShell)

```powershell
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
mkdir out | Out-Null
$files = Get-ChildItem -Recurse -Filter *.java src | % { $_.FullName }
javac -encoding UTF-8 -d out $files
````

> 💡 Se usa `-encoding UTF-8` para evitar problemas con tildes y comentarios en español.

### ▶️ Ejecución

```powershell
java -cp out siaa.ui.Main
```

---

## 🧠 Flujo funcional

1. **Alta de solicitud:** carga DNI, apellido y prácticas solicitadas.
2. **Ver antecedentes:** busca solicitudes previas del mismo afiliado.
3. **Decidir:** aplica las reglas de negocio (vigencia, topes y duplicidad).
4. **Comunicar:** registra el dictamen al afiliado y lo deja en bitácora.
5. **Archivar:** cierra la solicitud y la retira de pendientes.
6. **Listar solicitudes:** muestra todas las solicitudes con su bitácora.

---

## 📄 Ejemplo de salida

```
=== SIAA - Sistema de Autorizaciones ===
1) Alta de solicitud
2) Ver antecedentes
3) Decidir
4) Comunicar resultado
5) Archivar
6) Listar solicitudes
0) Salir

Opción: 1
DNI: 29999888
Apellido: Pérez
Código práctica: 1203
Cantidad: 1
Precio unitario: 45000
--> Solicitud creada: S-00023

Opción: 3
--> Reglas OK. Monto total: 45000.0
--> Resultado: AUTORIZADA

Opción: 4
--> Comunicación registrada al afiliado.

Opción: 6
Solicitudes (1):
- S-00023 | DNI 29999888 | COMUNICADA | total=45000.0
  Bitácora:
    [2025-10-26T12:05] Alta de solicitud (usr: admin)
    [2025-10-26T12:07] Autorizada. Monto total: 45000.0 (usr: admin)
    [2025-10-26T12:08] Comunicación registrada al afiliado. (usr: admin)
```

---

## 🧩 Ejemplo de excepciones controladas

| Escenario               | Excepción                    | Resultado                             |
| ----------------------- | ---------------------------- | ------------------------------------- |
| Alta sin DNI o apellido | `SolicitudInvalidaException` | Muestra mensaje y vuelve a pedir dato |
| Vigencia vencida        | `ReglaNegocioException`      | Bloquea decisión, mensaje claro       |
| Archivar sin comunicar  | `EstadoInvalidoException`    | Rechaza transición, mantiene estado   |

---

## 🗂️ Próximos pasos

🔹 **Integrar persistencia real con MySQL**
🔹 Implementar **interfaz gráfica (Swing o JavaFX)**
🔹 Agregar módulo de reportes y estadísticas



