package model;

import java.time.LocalDate;

public class Alquiler {
    private int id;
    private Cliente cliente;
    private Equipo equipo;
    private LocalDate fechaInicio;
    private int dias;
    private double montoTotal;
    private boolean devuelto;

    public Alquiler() {}

    public Alquiler(int id, Cliente cliente, Equipo equipo, LocalDate fechaInicio, int dias, double montoTotal, boolean devuelto) {
        this.id = id;
        this.cliente = cliente;
        this.equipo = equipo;
        this.fechaInicio = fechaInicio;
        this.dias = dias;
        this.montoTotal = montoTotal;
        this.devuelto = devuelto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdAlquiler() { return id; }
    public void setIdAlquiler(int idAlquiler) { this.id = idAlquiler; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Equipo getEquipo() { return equipo; }
    public void setEquipo(Equipo equipo) { this.equipo = equipo; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public int getDias() { return dias; }
    public void setDias(int dias) { this.dias = dias; }

    public double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }

    public double getMontoCobrado() { return montoTotal; }
    public void setMontoCobrado(double montoCobrado) { this.montoTotal = montoCobrado; }

    public boolean isDevuelto() { return devuelto; }
    public void setDevuelto(boolean devuelto) { this.devuelto = devuelto; }

    public void mostrarInformacion() {
        System.out.println("Id del alquiler: " + id);
        System.out.println("Cliente: " + cliente.getNombreRazonSocial());
        System.out.println("Equipo: " + equipo.getDescripcion());
        System.out.println("Fecha de inicio: " + fechaInicio);
        System.out.println("Dias: " + dias);
        System.out.println("Monto total: S/ " + montoTotal);
        System.out.println("Devuelto: " + (devuelto ? "Si" : "No"));
    }
}