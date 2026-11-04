package Dao;

import Model.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl {
    private Connection con;

    public ClienteDAOImpl(Connection con) {
        this.con = con;
    }

    // Usa el índice idx_cliente_doc
    public Cliente buscarPorDocumento(String documento) {
        String sql = "SELECT id, documento, nombre, telefono, email FROM cliente WHERE documento = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar por documento: " + e.getMessage());
        }
        return null;
    }

    // Búsqueda parcial por nombre con sanitización de comodines
    public List<Cliente> buscarPorNombre(String texto) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id, documento, nombre, telefono, email FROM cliente "
                + "WHERE nombre LIKE ? ESCAPE '\\\\'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + escaparComodines(texto) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar por nombre: " + e.getMessage());
        }
        return lista;
    }

    public List<Cliente> listarClientes() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id, documento, nombre, telefono, email FROM cliente";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            System.out.println("Error al listar clientes: " + e.getMessage());
        }
        return lista;
    }

    public boolean registrar(Cliente c) {
        String sql = "INSERT INTO cliente (documento, nombre, telefono, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getDocumento());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al registrar cliente: " + e.getMessage());
            return false;
        }
    }

    // Escapa \ primero, luego % y _
    private String escaparComodines(String texto) {
        return texto.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    private Cliente mapear(ResultSet rs) throws Exception {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("documento"),
                rs.getString("nombre"),
                rs.getString("telefono"),
                rs.getString("email")
        );
    }
}