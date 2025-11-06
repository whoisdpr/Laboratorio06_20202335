package com.pucp.tel05.laboratorio06;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Repositorio para operaciones CRUD de Tareas en Firestore.
 * Maneja: crear, leer, actualizar y eliminar tareas.
 */
public class TareaRepository {

    private static final String TAG = "TareaRepository";
    private static final String COLLECTION_TAREAS = "tareas";
    private final FirebaseFirestore db;

    public TareaRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Obtener todas las tareas de Firestore.
     * @param callback Retorna lista de tareas
     */
    public void obtenerTodasLasTareas(@NonNull OnTareasLoadedCallback callback) {
        db.collection(COLLECTION_TAREAS)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Task> tareas = new ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Task tarea = doc.toObject(Task.class);
                        if (tarea != null) {
                            tarea.setId(doc.getId()); // Asignar ID del documento
                            tareas.add(tarea);
                        }
                    }
                    Log.i(TAG, "✅ Tareas cargadas: " + tareas.size());
                    callback.onSuccess(tareas);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al obtener tareas: " + e.getMessage());
                    callback.onError(e.getMessage());
                });
    }

    /**
     * Crear una nueva tarea en Firestore.
     * @param tarea Objeto tarea a guardar
     * @param callback Callback con ID de la tarea creada
     */
    public void crearTarea(@NonNull Task tarea, @NonNull OnTareaOperationCallback callback) {
        String tareaId = UUID.randomUUID().toString();
        tarea.setId(tareaId);

        Map<String, Object> tareaMap = new HashMap<>();
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

    /**
     * Actualizar una tarea existente.
     * @param tarea Objeto tarea con cambios
     * @param callback Callback de operación
     */
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

    /**
     * Eliminar una tarea de Firestore.
     * @param tareaId ID de la tarea a eliminar
     * @param callback Callback de operación
     */
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

    /**
     * Callback para operaciones de carga de tareas.
     */
    public interface OnTareasLoadedCallback {
        void onSuccess(List<Task> tareas);
        void onError(String errorMessage);
    }

    /**
     * Callback para operaciones CRUD de una tarea.
     */
    public interface OnTareaOperationCallback {
        void onSuccess(String message);
        void onError(String errorMessage);
    }
}

