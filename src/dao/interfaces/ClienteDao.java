package dao.interfaces;

import model.Cliente;
import java.sql.Connection;
import java.util.List;

public interface ClienteDao {
    Cliente buscarPorDocumento(Connection con, String documento);
    List<Cliente> buscarPorNombre(Connection con, String texto);
    List<Cliente> listarClientes(Connection con);
    boolean registrar(Connection con, Cliente c);
}