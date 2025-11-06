package com.pucp.tel05.laboratorio06;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TareaDialog {

    public static void mostrarDialogCrearTarea(Context context, OnTareaCreatedCallback callback) {
        mostrarDialogEditarTarea(context, null, callback);
    }

    public static void mostrarDialogEditarTarea(Context context, Task tareaExistente, OnTareaCreatedCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        android.view.View view = android.view.LayoutInflater.from(context).inflate(R.layout.dialog_tarea, null);

        EditText etTitulo = view.findViewById(R.id.etTituloDlg);
        EditText etDescripcion = view.findViewById(R.id.etDescripcionDlg);
        EditText etFecha = view.findViewById(R.id.etFechaDlg);
        MaterialCheckBox cbEstado = view.findViewById(R.id.cbEstadoDlg);

        long[] fechaSeleccionada = {System.currentTimeMillis()};

        if (tareaExistente != null) {
            etTitulo.setText(tareaExistente.getTitulo());
            etDescripcion.setText(tareaExistente.getDescripcion());
            cbEstado.setChecked(tareaExistente.isEstado());
            fechaSeleccionada[0] = tareaExistente.getFechaLimite().toDate().getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            etFecha.setText(sdf.format(tareaExistente.getFechaLimite().toDate()));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            etFecha.setText(sdf.format(new Date(fechaSeleccionada[0])));
        }

        etFecha.setOnClickListener(v -> mostrarDatePicker(context, fechaSeleccionada, etFecha));

        builder.setView(view)
                .setTitle(tareaExistente != null ? "Editar Tarea" : "Nueva Tarea")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String titulo = etTitulo.getText().toString().trim();
                    String descripcion = etDescripcion.getText().toString().trim();
                    boolean estado = cbEstado.isChecked();

                    if (titulo.isEmpty()) {
                        Toast.makeText(context, "El título es requerido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Task tarea = new Task();
                    tarea.setTitulo(titulo);
                    tarea.setDescripcion(descripcion);
                    tarea.setFechaLimite(new Timestamp(new Date(fechaSeleccionada[0])));
                    tarea.setEstado(estado);

                    if (tareaExistente != null) {
                        tarea.setId(tareaExistente.getId());
                    }

                    callback.onTareaCreated(tarea);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private static void mostrarDatePicker(Context context, long[] fechaSeleccionada, EditText etFecha) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(fechaSeleccionada[0]);

        new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            fechaSeleccionada[0] = calendar.getTimeInMillis();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            etFecha.setText(sdf.format(new Date(fechaSeleccionada[0])));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public interface OnTareaCreatedCallback {
        void onTareaCreated(Task tarea);
    }
}



