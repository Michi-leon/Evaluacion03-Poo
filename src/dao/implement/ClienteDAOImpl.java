package dao.implement;

import dao.interfaces.ClienteDao;
import model.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDao {

    @Override
    public Cliente buscarPorDocumento(Connection con, String documento) {
        String sql = "SELECT documento, nombre_razon_social, telefono FROM clientes WHERE documento = ?";
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

    @Override
    public List<Cliente> buscarPorNombre(Connection con, String texto) {
        List<Cliente> lista = new ArrayList<>();
        if (texto == null || texto.trim().isEmpty()) return lista;
        String sql = "SELECT documento, nombre_razon_social, telefono FROM clientes WHERE nombre_razon_social LIKE ? ESCAPE '\\\\'";
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

    @Override
    public List<Cliente> listarClientes(Connection con) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT documento, nombre_razon_social, telefono FROM clientes";
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

    @Override
    public boolean registrar(Connection con, Cliente c) {
        String sql = "INSERT INTO clientes (documento, nombre_razon_social, telefono) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getDocumento());
            ps.setString(2, c.getNombreRazonSocial());
            ps.setString(3, c.getTelefono());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al registrar cliente: " + e.getMessage());
            return false;
        }
    }

    private String escaparComodines(String texto) {
        return texto.replace("\\\\", "\\\\\\\\").replace("%", "\\\\%").replace("_", "\\\\_");
    }

    private Cliente mapear(ResultSet rs) throws Exception {
        return new Cliente(
                rs.getString("documento"),
                rs.getString("nombre_razon_social"),
                rs.getString("telefono")
        );
    }
}