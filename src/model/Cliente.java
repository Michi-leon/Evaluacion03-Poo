package model;

public class Cliente {
    private String documento;
    private String nombreRazonSocial;
    private String telefono;

    public Cliente() {}

    public Cliente(String documento, String nombreRazonSocial, String telefono) {
        this.documento = documento;
        this.nombreRazonSocial = nombreRazonSocial;
        this.telefono = telefono;
    }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getNombreRazonSocial() { return nombreRazonSocial; }
    public void setNombreRazonSocial(String nombreRazonSocial) { this.nombreRazonSocial = nombreRazonSocial; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return documento + " | " + nombreRazonSocial + " | " + telefono;
    }
}