package Model;

public class Cliente {
    private int id;
    private String documento;
    private String nombre;
    private String telefono;
    private String email;

    public Cliente() {}

    public Cliente(int id, String documento, String nombre, String telefono, String email) {
        this.id = id;
        this.documento = documento;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return id + " | " + documento + " | " + nombre + " | " + telefono + " | " + email;
    }
}