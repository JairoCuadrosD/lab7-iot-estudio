# Lab 7 IoT — versión de estudio

Registro de horas de estudio 2.0: app Android (Java + Firebase) que consulta a un backend de microservicios Spring Boot (Eureka + OpenFeign).

```
app-android ──HTTP──▶ avance-service :8002 ──Feign("curso-service")──▶ curso-service :8001
                              │                                              │
                              └────────── ambos se registran en ─────────────┘
                                          eureka-server :8761
```

## Estructura

| Carpeta | Qué es |
|---|---|
| `eureka-server/` | Servidor de descubrimiento (`@EnableEurekaServer`) |
| `curso-service/` | `GET /curso/{codigo}` con el catálogo en un `Map` |
| `avance-service/` | `GET /avance/{codigo}/{horas}`; pide los créditos a curso-service vía `@FeignClient` |
| `app-android/` | Login FirebaseUI, CRUD en Firestore, evidencia en Storage, gráficos y "Evaluar mi avance" |

## 1. Levantar el backend (en este orden)

Requiere JDK 17 y Maven, o abrir cada carpeta en IntelliJ y darle Run.

```bash
cd eureka-server && mvn spring-boot:run
```
```bash
cd curso-service && mvn spring-boot:run
```
```bash
cd avance-service && mvn spring-boot:run
```

Comprobar:
- http://localhost:8761 → deben aparecer `CURSO-SERVICE` y `AVANCE-SERVICE`
- http://localhost:8001/curso/1TEL05
- http://localhost:8002/avance/1TEL05/7.5 → `estado: "PENDIENTE"`
- http://localhost:8002/avance/1TEL03/9 → `estado: "AL DÍA"`

> Si avance-service da 503 justo al arrancar, espera ~30 s: Eureka tarda un poco en propagar el registro.

## 2. Configurar Firebase

1. Crear proyecto en Firebase y una app Android con paquete `pe.edu.pucp.lab7`.
2. Descargar `google-services.json` a `app-android/app/` (está en `.gitignore`).
3. Activar **Authentication → Correo/contraseña**, **Firestore** y **Storage**.
4. Reglas sugeridas (cada usuario solo ve lo suyo):

Firestore:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{db}/documents {
    match /usuarios/{uid}/sesiones/{id} {
      allow read, write: if request.auth != null && request.auth.uid == uid;
    }
  }
}
```

Storage:
```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /evidencias/{uid}/{archivo} {
      allow read, write: if request.auth != null && request.auth.uid == uid;
    }
  }
}
```

## 3. Correr la app

Abrir `app-android/` en Android Studio y ejecutar en el emulador. La app llama a `http://10.0.2.2:8002/` (= localhost de la PC visto desde el emulador). En un celular físico, cambiar `BASE_URL` en `RetrofitClient.java` por la IP LAN de la PC.

## Ideas clave para estudiar

**Eureka / Feign**
- `spring.application.name` es el "nombre de contacto" del servicio en Eureka.
- `@FeignClient(name = "curso-service")` **no** lleva URL: el LoadBalancer pregunta a Eureka por las instancias y elige una.
- Por eso puedes levantar curso-service en otro puerto y todo sigue funcionando.

**Firestore** (`usuarios/{uid}/sesiones/{id}`)
- Colgar las sesiones bajo el `uid` hace que el filtro por usuario sea la propia ruta.
- `addSnapshotListener` = lista en tiempo real; se quita en `onStop()`.
- Al editar se reutiliza la `evidenciaUrl` cargada si no se elige otra foto → no se pierde.

**Storage** (`FormSesionActivity.subirImagenYGuardar`)
- Para una sesión nueva se genera el id con `document()` **antes** de guardar, porque la ruta `evidencias/{uid}/{idSesion}.jpg` lo necesita.
- `putFile(...).continueWithTask(getDownloadUrl)` encadena subida → URL → `set()` en Firestore.

**Retrofit**
- `enqueue()` corre en segundo plano y devuelve el callback en el hilo principal.
- `onFailure` (sin red / backend caído) → Toast "Servicio no disponible", la app no se cae.

## Archivos para leer primero

1. [`AvanceController.java`](avance-service/src/main/java/pe/edu/pucp/lab7/avance/AvanceController.java) + [`CursoClient.java`](avance-service/src/main/java/pe/edu/pucp/lab7/avance/CursoClient.java)
2. [`FormSesionActivity.java`](app-android/app/src/main/java/pe/edu/pucp/lab7/FormSesionActivity.java)
3. [`GraficosActivity.java`](app-android/app/src/main/java/pe/edu/pucp/lab7/GraficosActivity.java)
