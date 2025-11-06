# Asociación de Tareas a Usuario Autenticado

## 📋 Descripción del Cambio

Se implementó un sistema de asociación de tareas al usuario autenticado en Firebase. Ahora cada tarea está vinculada al usuario que la crea, garantizando que:

- ✅ Cada usuario solo vea sus propias tareas
- ✅ Las tareas se filtran automáticamente por `userId`
- ✅ Se previene el acceso a tareas de otros usuarios
- ✅ La sincronización en tiempo real solo trae tareas del usuario actual

---

## 🔧 Cambios Técnicos

### 1️⃣ Modificación de `Task.java`

**Se añadió el campo `userId`:**
```java
private String userId;
```

**Nuevos métodos:**
- `getUserId()` - Getter para userId
- `setUserId(String userId)` - Setter fluent para userId

**Constructores actualizados:**
- Constructor completo ahora incluye `userId` como parámetro
- Constructor de conveniencia también actualizado

**Métodos equals() y hashCode():**
- Se incluye `userId` en la comparación de igualdad
- Se incluye `userId` en el cálculo del hash

### 2️⃣ Modificación de `TareaRepository.java`

**Se añadió `FirebaseAuth`:**
```java
private final FirebaseAuth auth;
```

**Nuevo método privado `getCurrentUserId()`:**
```java
private String getCurrentUserId() {
    if (auth.getCurrentUser() != null) {
        return auth.getCurrentUser().getUid();
    }
    return null;
}
```
- Obtiene el ID del usuario autenticado actualmente
- Retorna `null` si no hay usuario autenticado

**Modificación de `obtenerTodasLasTareas()`:**
```java
tareasListener = db.collection(COLLECTION_TAREAS)
        .whereEqualTo("userId", userId)  // FILTRADO POR USUARIO
        .addSnapshotListener((querySnapshot, e) -> { ... });
```
- Ahora filtra solo tareas del usuario actual usando `whereEqualTo("userId", userId)`
- Verifica que el usuario esté autenticado antes de hacer la consulta
- Retorna error "Usuario no autenticado" si no hay sesión activa

**Modificación de `crearTarea()`:**
```java
tareaMap.put("userId", userId);  // NUEVO
tareaMap.put("titulo", tarea.getTitulo());
// ... resto de campos
```
- Automáticamente asigna el `userId` actual al crear una tarea
- No requiere que el usuario proporcione este dato

**Métodos `actualizarTarea()` y `eliminarTarea()`:**
- Se mantienen igual (no cambian el `userId`)
- El filtrado se realiza solo en lectura

---

## 🔐 Flujo de Seguridad

### Creación de Tarea
```
Usuario autenticado (UID: abc123)
    ↓
creaRTarea()
    ↓
getCurrentUserId() → "abc123"
    ↓
Task guardada en Firestore con userId = "abc123"
```

### Lectura de Tareas
```
Usuario autenticado (UID: abc123)
    ↓
obtenerTodasLasTareas()
    ↓
getCurrentUserId() → "abc123"
    ↓
Firestore query: .whereEqualTo("userId", "abc123")
    ↓
Solo tareas del usuario abc123 se cargan
    ↓
Usuarios abc456, xyz789, etc. no ven estas tareas
```

### Eliminación de Tarea
```
Usuario intenta eliminar tarea (pero sin validación adicional en el cliente)
    ↓
Document se elimina de Firestore
    ↓
(⚠️ Se recomienda validar en reglas de Firestore)
```

---

## ⚠️ Consideraciones Importantes

### Validación en Firestore Rules (Recomendado)

Para mayor seguridad, se debe añadir a las reglas de Firestore:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /tareas/{document=**} {
      allow read: if request.auth.uid == resource.data.userId;
      allow create: if request.auth.uid == request.resource.data.userId;
      allow update: if request.auth.uid == resource.data.userId;
      allow delete: if request.auth.uid == resource.data.userId;
    }
  }
}
```

Esto previene:
- Que un usuario lea tareas de otro usuario (aunque intente manipular la app)
- Que un usuario cree tareas asignadas a otro usuario
- Que un usuario modifique o elimine tareas que no le pertenecen

---

## 📊 Estructura en Firestore

**Antes:**
```
tareas/
├── tarea-uuid-1/
│   ├── titulo: "Ir al mercado"
│   ├── descripcion: "..."
│   ├── fechaLimite: Timestamp
│   └── estado: false
├── tarea-uuid-2/
│   └── ...
```

**Después:**
```
tareas/
├── tarea-uuid-1/
│   ├── userId: "firebase-uid-usuario1"  ← NUEVO
│   ├── titulo: "Ir al mercado"
│   ├── descripcion: "..."
│   ├── fechaLimite: Timestamp
│   └── estado: false
├── tarea-uuid-2/
│   ├── userId: "firebase-uid-usuario2"  ← NUEVO
│   ├── titulo: "Hacer informe"
│   └── ...
```

---

## 🧪 Prueba Manual

1. **Login Usuario A** (ej: usuario@email.com)
   - Crea 3 tareas: "Tarea 1", "Tarea 2", "Tarea 3"
   - Verifica que aparezcan en la lista

2. **Logout**

3. **Login Usuario B** (ej: otro@email.com)
   - Crea 2 tareas: "Tarea X", "Tarea Y"
   - Verifica que SOLO aparezcan sus tareas
   - No debe ver "Tarea 1", "Tarea 2", "Tarea 3"

4. **Logout**

5. **Login Usuario A nuevamente**
   - Verifica que solo aparezcan "Tarea 1", "Tarea 2", "Tarea 3"
   - No debe ver "Tarea X", "Tarea Y"

---

## 📝 Archivos Modificados

- ✅ `app/src/main/java/com/pucp/tel05/laboratorio06/Task.java`
- ✅ `app/src/main/java/com/pucp/tel05/laboratorio06/TareaRepository.java`

## 🔄 Archivos Compatibles (Sin cambios necesarios)

- ✅ `TareaDialog.java` - Automáticamente asigna userId
- ✅ `TareaAdapter.java` - Solo muestra lo que retorna el repositorio
- ✅ `TareasFragment.java` - Funciona con el nuevo filtrado

---

## ✅ Compilación

```
BUILD SUCCESSFUL in 4s
100 actionable tasks: 30 executed, 70 up-to-date
```

Sin errores de compilación.

---

**Implementado en:** November 6, 2025

**Nota:** La autenticación de usuarios ya estaba implementada en la app. Este cambio solo vincula las tareas al usuario autenticado.

