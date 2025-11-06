package com.pucp.tel05.laboratorio06;

import com.google.firebase.Timestamp;

import java.util.Objects;

public class Task {

    private String id;
    private String userId;
    private String titulo;
    private String descripcion;
    private Timestamp fechaLimite;
    private boolean estado;

    public Task() {
    }

    public Task(String titulo, String descripcion, Timestamp fechaLimite, boolean estado) {
        this(null, null, titulo, descripcion, fechaLimite, estado);
    }

    public Task(String id, String userId, String titulo, String descripcion, Timestamp fechaLimite, boolean estado) {
        this.id = id;
        this.userId = userId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public Timestamp getFechaLimite() { return fechaLimite; }
    public boolean isEstado() { return estado; }

    public Task setId(String id) { this.id = id; return this; }
    public Task setUserId(String userId) { this.userId = userId; return this; }
    public Task setTitulo(String titulo) { this.titulo = titulo; return this; }
    public Task setDescripcion(String descripcion) { this.descripcion = descripcion; return this; }
    public Task setFechaLimite(Timestamp fechaLimite) { this.fechaLimite = fechaLimite; return this; }
    public Task setEstado(boolean estado) { this.estado = estado; return this; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return estado == task.estado && Objects.equals(id, task.id) && Objects.equals(userId, task.userId) && Objects.equals(titulo, task.titulo) && Objects.equals(descripcion, task.descripcion) && Objects.equals(fechaLimite, task.fechaLimite);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, titulo, descripcion, fechaLimite, estado);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaLimite=" + fechaLimite +
                ", estado=" + estado +
                '}';
    }
}




