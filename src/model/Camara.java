package model;

public class Camara extends Equipo {

    public Camara(String codigo, String descripcion, double tarifaDiaria, boolean disponible) {
        super(codigo, descripcion, tarifaDiaria, disponible);
    }

    @Override
    public double calcularMonto(int dias) {
        double montoBase = getTarifaDiaria() * dias;
        return montoBase + (montoBase * 0.15);
    }

    @Override
    public String mostrarTipo() {
        return "Camara";
    }

    @Override
    public void imprimirInformacion() {
        super.imprimirInformacion();
        System.out.println("Tipo: " + mostrarTipo());
        System.out.println("Seguro Obligatorio: 15%");
    }
}