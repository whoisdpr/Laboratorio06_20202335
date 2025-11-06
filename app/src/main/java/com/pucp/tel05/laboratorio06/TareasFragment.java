package com.pucp.tel05.laboratorio06;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pucp.tel05.laboratorio06.databinding.FragmentTareasBinding;

import java.util.ArrayList;
import java.util.List;

public class TareasFragment extends Fragment implements TareaAdapter.OnTareaEditListener {

    private FragmentTareasBinding binding;
    private TareaRepository repository;
    private TareaAdapter adapter;
    private List<Task> tareas = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTareasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new TareaRepository();

        binding.rvTareas.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TareaAdapter(tareas, repository, this);
        binding.rvTareas.setAdapter(adapter);

        binding.btnAddTask.setOnClickListener(v -> agregarNuevaTarea());

        cargarTareas();
    }

    private void cargarTareas() {
        repository.obtenerTodasLasTareas(new TareaRepository.OnTareasLoadedCallback() {
            @Override
            public void onSuccess(List<Task> tareasCargadas) {
                tareas.clear();
                tareas.addAll(tareasCargadas);
                adapter.notifyDataSetChanged();

                if (tareas.isEmpty()) {
                    binding.tvEmptyState.setVisibility(View.VISIBLE);
                    binding.rvTareas.setVisibility(View.GONE);
                } else {
                    binding.tvEmptyState.setVisibility(View.GONE);
                    binding.rvTareas.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void agregarNuevaTarea() {
        TareaDialog.mostrarDialogCrearTarea(requireContext(), tarea -> {
            repository.crearTarea(tarea, new TareaRepository.OnTareaOperationCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    cargarTareas();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onEditTarea(Task tarea) {
        TareaDialog.mostrarDialogEditarTarea(requireContext(), tarea, tareaEditada -> {
            repository.actualizarTarea(tareaEditada, new TareaRepository.OnTareaOperationCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    cargarTareas();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onTareaChanged() {
        cargarTareas();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (repository != null) {
            repository.detenerEscucha();
        }
        binding = null;
    }
}
