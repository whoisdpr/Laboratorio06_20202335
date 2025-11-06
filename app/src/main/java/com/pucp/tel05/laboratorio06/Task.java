package com.pucp.tel05.laboratorio06;

import java.util.Objects;

/**
 * Modelo simple para una tarea. Mantiene un constructor vacío requerido por Firebase Realtime Database
 * / Cloud Firestore deserializers, además de constructores de conveniencia y setters encadenables.
 */
public class Task {

    private String id;
    private String titulo;
    private String descripcion;
    private long fechaLimite; // epoch millis
    private boolean estado;   // false: pendiente, true: completada

    // Required by Firebase deserialization
    public Task() {
    }

    // Convenience constructor (sin id) para crear nuevas tareas antes de asignar un id
    public Task(String titulo, String descripcion, long fechaLimite, boolean estado) {
        this(null, titulo, descripcion, fechaLimite, estado);
    }

    // Full constructor
    public Task(String id, String titulo, String descripcion, long fechaLimite, boolean estado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
    }

    // Getters
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public long getFechaLimite() { return fechaLimite; }
    public boolean isEstado() { return estado; }

    // Fluent setters (devuelven this para encadenar llamadas)
    public Task setId(String id) { this.id = id; return this; }
    public Task setTitulo(String titulo) { this.titulo = titulo; return this; }
    public Task setDescripcion(String descripcion) { this.descripcion = descripcion; return this; }
    public Task setFechaLimite(long fechaLimite) { this.fechaLimite = fechaLimite; return this; }
    public Task setEstado(boolean estado) { this.estado = estado; return this; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return fechaLimite == task.fechaLimite && estado == task.estado && Objects.equals(id, task.id) && Objects.equals(titulo, task.titulo) && Objects.equals(descripcion, task.descripcion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titulo, descripcion, fechaLimite, estado);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaLimite=" + fechaLimite +
                ", estado=" + estado +
                '}';
    }
}

