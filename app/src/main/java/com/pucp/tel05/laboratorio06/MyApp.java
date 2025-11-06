package com.pucp.tel05.laboratorio06;

import android.app.Application;
import android.util.Log;

// Optional Firebase initialization class. In most setups the google-services plugin
// initializes Firebase automatically when google-services.json is present. You can
// uncomment FirebaseApp.initializeApp(this) if you need to force initialization here.
public class MyApp extends Application {

    private static final String TAG = "MyApp";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Aplicación iniciada");

        // If you need to explicitly initialize Firebase (usually not necessary when
        // google-services.json is present and the google services plugin is applied),
        // uncomment the next line and add the Firebase SDK import:
        // com.google.firebase.FirebaseApp.initializeApp(this);

        Log.i(TAG, "Firebase initialization left to google-services plugin (if present).");

        // Validar conexión a Firestore (solo si google-services.json está disponible)
        try {
            FirestoreHelper.validateFirestoreConnection();
            Log.i(TAG, "Validación de Firestore iniciada - revisa Logcat para confirmar conexión");
        } catch (Exception e) {
            Log.w(TAG, "No se pudo iniciar validación de Firestore: " + e.getMessage());
            Log.w(TAG, "Posible causa: google-services.json no configurado o Firebase no inicializado");
        }
    }
}
