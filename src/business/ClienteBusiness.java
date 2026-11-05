package business;

import dao.interfaces.ClienteDao;
import model.Cliente;
import service.Conexion;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ClienteBusiness {
    private ClienteDao dao;

    public ClienteBusiness(ClienteDao dao) {
        this.dao = dao;
    }

    public Cliente buscarPorDocumento(String documento) {
        if (documento == null || documento.trim().isEmpty()) {
            System.out.println("El documento no puede estar vacío.");
            return null;
        }
        try (Connection con = Conexion.getConnection()) {
            return dao.buscarPorDocumento(con, documento.trim());
        } catch (Exception e) {
            System.out.println("Error de conexion: " + e.getMessage());
            return null;
        }
    }

    public List<Cliente> buscarPorNombre(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            System.out.println("Ingrese un texto para buscar.");
            return new ArrayList<>();
        }
        try (Connection con = Conexion.getConnection()) {
            return dao.buscarPorNombre(con, texto.trim());
        } catch (Exception e) {
            System.out.println("Error de conexion: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean registrar(Cliente c) {
        if (c.getDocumento() == null || c.getDocumento().trim().isEmpty()
                || c.getNombreRazonSocial() == null || c.getNombreRazonSocial().trim().isEmpty()) {
            System.out.println("Documento y nombre son obligatorios.");
            return false;
        }
        try (Connection con = Conexion.getConnection()) {
            if (dao.buscarPorDocumento(con, c.getDocumento().trim()) != null) {
                System.out.println("Ya existe un cliente con ese documento.");
                return false;
            }
            return dao.registrar(con, c);
        } catch (Exception e) {
            System.out.println("Error de conexion: " + e.getMessage());
            return false;
        }
    }

    public List<Cliente> listar() {
        try (Connection con = Conexion.getConnection()) {
            return dao.listarClientes(con);
        } catch (Exception e) {
            System.out.println("Error de conexion: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}