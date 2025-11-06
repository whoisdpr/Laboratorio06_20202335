package com.pucp.tel05.laboratorio06;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TareaRepository {

    private static final String TAG = "TareaRepository";
    private static final String COLLECTION_TAREAS = "tareas";
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private ListenerRegistration tareasListener;

    public TareaRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    private String getCurrentUserId() {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        }
        return null;
    }

    public void obtenerTodasLasTareas(@NonNull OnTareasLoadedCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        if (tareasListener != null) {
            tareasListener.remove();
        }

        tareasListener = db.collection(COLLECTION_TAREAS)
                .whereEqualTo("userId", userId)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "❌ Error al escuchar tareas: " + e.getMessage());
                        callback.onError(e.getMessage());
                        return;
                    }

                    if (querySnapshot != null) {
                        List<Task> tareas = new ArrayList<>();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            Task tarea = doc.toObject(Task.class);
                            if (tarea != null) {
                                tarea.setId(doc.getId());
                                tareas.add(tarea);
                            }
                        }
                        Log.i(TAG, "✅ Tareas cargadas (tiempo real): " + tareas.size());
                        callback.onSuccess(tareas);
                    }
                });
    }

    public void detenerEscucha() {
        if (tareasListener != null) {
            tareasListener.remove();
            tareasListener = null;
            Log.i(TAG, "ℹ️ Escucha de tareas detenida");
        }
    }

    public void crearTarea(@NonNull Task tarea, @NonNull OnTareaOperationCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        String tareaId = UUID.randomUUID().toString();
        tarea.setId(tareaId);
        tarea.setUserId(userId);

        Map<String, Object> tareaMap = new HashMap<>();
        tareaMap.put("userId", userId);
        tareaMap.put("titulo", tarea.getTitulo());
        tareaMap.put("descripcion", tarea.getDescripcion());
        tareaMap.put("fechaLimite", tarea.getFechaLimite());
        tareaMap.put("estado", tarea.isEstado());

        db.collection(COLLECTION_TAREAS)
                .document(tareaId)
                .set(tareaMap)
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "✅ Tarea creada: " + tareaId);
                    callback.onSuccess("Tarea registrada correctamente");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al crear tarea: " + e.getMessage());
                    callback.onError("Error: " + e.getMessage());
                });
    }

    public void actualizarTarea(@NonNull Task tarea, @NonNull OnTareaOperationCallback callback) {
        if (tarea.getId() == null || tarea.getId().isEmpty()) {
            callback.onError("ID de tarea inválido");
            return;
        }

        Map<String, Object> tareaMap = new HashMap<>();
        tareaMap.put("titulo", tarea.getTitulo());
        tareaMap.put("descripcion", tarea.getDescripcion());
        tareaMap.put("fechaLimite", tarea.getFechaLimite());
        tareaMap.put("estado", tarea.isEstado());

        db.collection(COLLECTION_TAREAS)
                .document(tarea.getId())
                .update(tareaMap)
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "✅ Tarea actualizada: " + tarea.getId());
                    callback.onSuccess("Tarea modificada correctamente");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al actualizar tarea: " + e.getMessage());
                    callback.onError("Error: " + e.getMessage());
                });
    }

    public void eliminarTarea(@NonNull String tareaId, @NonNull OnTareaOperationCallback callback) {
        db.collection(COLLECTION_TAREAS)
                .document(tareaId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "✅ Tarea eliminada: " + tareaId);
                    callback.onSuccess("Tarea eliminada correctamente");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al eliminar tarea: " + e.getMessage());
                    callback.onError("Error: " + e.getMessage());
                });
    }

    public interface OnTareasLoadedCallback {
        void onSuccess(List<Task> tareas);
        void onError(String errorMessage);
    }

    public interface OnTareaOperationCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
    }
}




