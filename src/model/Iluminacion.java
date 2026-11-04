package model;

public class Iluminacion extends Equipo {

    public Iluminacion(String codigo, String descripcion, double tarifaDiaria, boolean disponible) {
        super(codigo, descripcion, tarifaDiaria, disponible);
    }

    @Override
    public double calcularMonto(int dias) {
        double montoBase = getTarifaDiaria() * dias;
        if (dias > 5) {
            montoBase = montoBase - (montoBase * 0.10);
        }
        return montoBase;
    }

    @Override
    public String mostrarTipo() {
        return "Iluminacion";
    }

    @Override
    public void imprimirInformacion() {
        super.imprimirInformacion();
        System.out.println("Tipo: " + mostrarTipo());
        System.out.println("Beneficio: 10% de descuento si el alquiler supera los 5 dias");
    }
}