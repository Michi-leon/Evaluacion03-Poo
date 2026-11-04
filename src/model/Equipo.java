package model;

public abstract class Equipo {
    private String codigo;
    private String descripcion;
    private double tarifaDiaria;
    private boolean disponible;

    public Equipo(String codigo, String descripcion, double tarifaDiaria, boolean disponible) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.tarifaDiaria = tarifaDiaria;
        this.disponible = disponible;
    }

    public abstract double calcularMonto(int dias);
    public abstract String mostrarTipo();

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getTarifaDiaria() { return tarifaDiaria; }
    public void setTarifaDiaria(double tarifaDiaria) { this.tarifaDiaria = tarifaDiaria; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public void imprimirInformacion() {
        System.out.println("Codigo: " + codigo);
        System.out.println("Descripcion: " + descripcion);
        System.out.println("Tarifa Diaria: S/ " + tarifaDiaria);
        System.out.println("Disponible: " + (disponible ? "SI" : "NO"));
    }
}