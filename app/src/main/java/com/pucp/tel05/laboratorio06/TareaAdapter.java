package com.pucp.tel05.laboratorio06;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.pucp.tel05.laboratorio06.databinding.ItemTareaBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    private List<Task> tareas;
    private TareaRepository repository;
    private OnTareaEditListener editListener;

    public interface OnTareaEditListener {
        void onEditTarea(Task tarea);
        void onTareaChanged();
    }

    public TareaAdapter(List<Task> tareas, TareaRepository repository, OnTareaEditListener editListener) {
        this.tareas = tareas;
        this.repository = repository;
        this.editListener = editListener;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTareaBinding binding = ItemTareaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TareaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Task tarea = tareas.get(position);
        holder.bind(tarea);
    }

    @Override
    public int getItemCount() {
        return tareas.size();
    }

    public void actualizarLista(List<Task> nuevasTareas) {
        tareas.clear();
        tareas.addAll(nuevasTareas);
        notifyDataSetChanged();
    }

    class TareaViewHolder extends RecyclerView.ViewHolder {
        private final ItemTareaBinding binding;

        TareaViewHolder(@NonNull ItemTareaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Task tarea) {
            binding.tvTitulo.setText(tarea.getTitulo());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fecha = tarea.getFechaLimite() != null ? tarea.getFechaLimite().toDate() : new Date();
            String fechaFormato = sdf.format(fecha);
            binding.tvFecha.setText("📅 Fecha: " + fechaFormato);

            String estado = tarea.isEstado() ? "✅ Completada" : "⏳ Pendiente";
            binding.tvEstado.setText(estado);

            binding.btnEditar.setOnClickListener(v -> {
                if (editListener != null) {
                    editListener.onEditTarea(tarea);
                }
            });

            binding.btnEliminar.setOnClickListener(v -> confirmarEliminar(tarea));

            binding.getRoot().setOnClickListener(v -> {
                tarea.setEstado(!tarea.isEstado());
                repository.actualizarTarea(tarea, new TareaRepository.OnTareaOperationCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(itemView.getContext(), message, Toast.LENGTH_SHORT).show();
                        bind(tarea);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(itemView.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        private void confirmarEliminar(Task tarea) {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle("Eliminar Tarea")
                    .setMessage("¿Estás seguro de que deseas eliminar esta tarea?\n" + tarea.getTitulo())
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        repository.eliminarTarea(tarea.getId(), new TareaRepository.OnTareaOperationCallback() {
                            @Override
                            public void onSuccess(String message) {
                                Toast.makeText(itemView.getContext(), message, Toast.LENGTH_SHORT).show();
                                if (editListener != null) {
                                    editListener.onTareaChanged();
                                }
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(itemView.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        }
    }
}

