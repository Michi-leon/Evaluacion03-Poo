package Business;

import Dao.ClienteDao;
import Model.Cliente;
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
        return dao.buscarPorDocumento(documento.trim());
    }

    public List<Cliente> buscarPorNombre(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            System.out.println("Ingrese un texto para buscar.");
            return new ArrayList<>();
        }
        return dao.buscarPorNombre(texto.trim());
    }

    public boolean registrar(Cliente c) {
        if (c.getDocumento() == null || c.getDocumento().trim().isEmpty()
                || c.getNombre() == null || c.getNombre().trim().isEmpty()) {
            System.out.println("Documento y nombre son obligatorios.");
            return false;
        }
        if (dao.buscarPorDocumento(c.getDocumento().trim()) != null) {
            System.out.println("Ya existe un cliente con ese documento.");
            return false;
        }
        return dao.registrar(c);
    }

    public List<Cliente> listar() {
        return dao.listarClientes();
    }
}