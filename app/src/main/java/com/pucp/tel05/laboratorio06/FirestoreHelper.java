package com.pucp.tel05.laboratorio06;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Helper para validar y probar la conexión a Firestore Database.
 * Úsalo en MyApp.onCreate() o MainActivity.onCreate() para verificar que Firestore está conectado.
 */
public class FirestoreHelper {

    private static final String TAG = "FirestoreHelper";

    /**
     * Valida la conexión a Firestore escribiendo un documento de prueba.
     * Útil para verificar que Firebase y Firestore están correctamente configurados.
     */
    public static void validateFirestoreConnection() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Documento de prueba
        java.util.Map<String, Object> testData = new java.util.HashMap<>();
        testData.put("prueba", "Conexión a Firestore validada");
        testData.put("timestamp", System.currentTimeMillis());

        // Intentar escribir en Firestore
        db.collection("test")
                .document("validacion")
                .set(testData)
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "✅ Conexión a Firestore EXITOSA");
                    Log.i(TAG, "Documento de prueba escrito correctamente en: /test/validacion");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al conectar a Firestore: " + e.getMessage());
                    Log.e(TAG, "Causa: ", e);
                });
    }

    /**
     * Lee el documento de prueba para confirmar que Firestore está funcionando.
     */
    public static void readFirestoreTest() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("test")
                .document("validacion")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.i(TAG, "✅ Lectura de Firestore EXITOSA");
                        Log.i(TAG, "Datos: " + documentSnapshot.getData());
                    } else {
                        Log.w(TAG, "⚠️ El documento de prueba no existe. Ejecuta validateFirestoreConnection() primero.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ Error al leer de Firestore: " + e.getMessage());
                });
    }
}

