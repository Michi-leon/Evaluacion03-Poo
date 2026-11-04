package model;

public class Audio extends Equipo {

    public Audio(String codigo, String descripcion, double tarifaDiaria, boolean disponible) {
        super(codigo, descripcion, tarifaDiaria, disponible);
    }

    @Override
    public double calcularMonto(int dias) {
        int diasCobrar = dias;
        if (diasCobrar == 1) {
            diasCobrar = 2;
        }
        return getTarifaDiaria() * diasCobrar;
    }

    @Override
    public String mostrarTipo() {
        return "Audio";
    }

    @Override
    public void imprimirInformacion() {
        super.imprimirInformacion();
        System.out.println("Tipo: " + mostrarTipo());
        System.out.println("Condicion: Cobro minimo obligatorio de 2 dias");
    }
}