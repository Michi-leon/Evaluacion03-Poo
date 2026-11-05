package dao.interfaces;
import java.sql.Connection;
import java.sql.SQLException;
import model.Alquiler;

public interface AlquilerDao {
    boolean registrarAlquiler(Connection cn, Alquiler alquiler)
            throws SQLException;
    boolean marcarComoDevuelto(Connection cn, int idAlquiler)
            throws SQLException;
    void mostrarPendientesDevolucion(Connection cn)
            throws SQLException;
}