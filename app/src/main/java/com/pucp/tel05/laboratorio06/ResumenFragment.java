package com.pucp.tel05.laboratorio06;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pucp.tel05.laboratorio06.databinding.FragmentResumenBinding;

import java.util.List;

public class ResumenFragment extends Fragment {

    private FragmentResumenBinding binding;
    private TareaRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentResumenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new TareaRepository();
        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        repository.obtenerTodasLasTareas(new TareaRepository.OnTareasLoadedCallback() {
            @Override
            public void onSuccess(List<Task> tareas) {
                actualizarUI(tareas);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarUI(List<Task> tareas) {
        int totalTareas = tareas.size();
        int tareasCompletadas = 0;
        int tareasPendientes = 0;

        for (Task tarea : tareas) {
            if (tarea.isEstado()) {
                tareasCompletadas++;
            } else {
                tareasPendientes++;
            }
        }

        binding.tvTotalTareas.setText(String.valueOf(totalTareas));
        binding.tvTareasCompletadas.setText(String.valueOf(tareasCompletadas));
        binding.tvTareasPendientes.setText(String.valueOf(tareasPendientes));

        if (totalTareas > 0) {
            int porcentajeCompletadas = (tareasCompletadas * 100) / totalTareas;
            int porcentajePendientes = (tareasPendientes * 100) / totalTareas;

            binding.tvPorcentajeCompletadas.setText(porcentajeCompletadas + "%");
            binding.tvPorcentajePendientes.setText(porcentajePendientes + "%");

            binding.pbCompletadas.setProgress(porcentajeCompletadas);
            binding.pbPendientes.setProgress(porcentajePendientes);
        } else {
            binding.tvPorcentajeCompletadas.setText("0%");
            binding.tvPorcentajePendientes.setText("0%");
            binding.pbCompletadas.setProgress(0);
            binding.pbPendientes.setProgress(0);
        }
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



