package dao.implement;

import dao.interfaces.AlquilerDao;
import model.Alquiler;
import java.sql.*;

public class AlquilerDAOImpl implements AlquilerDao {

    @Override
    public boolean registrarAlquiler(Connection cn, Alquiler alquiler) throws SQLException {
        String sql = "INSERT INTO alquileres (documento_cliente, codigo_equipo, fecha_inicio, dias, monto_cobrado, devuelto) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, alquiler.getCliente().getDocumento());
            ps.setString(2, alquiler.getEquipo().getCodigo());
            ps.setDate(3, Date.valueOf(alquiler.getFechaInicio()));
            ps.setInt(4, alquiler.getDias());
            ps.setDouble(5, alquiler.getMontoTotal());
            ps.setBoolean(6, false);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean marcarComoDevuelto(Connection cn, int idAlquiler) throws SQLException {
        String sql = "UPDATE alquileres SET devuelto = TRUE WHERE id_alquiler = ? AND devuelto = FALSE";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idAlquiler);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public void mostrarPendientesDevolucion(Connection cn) throws SQLException {
        String sql = "SELECT a.id_alquiler, c.nombre_razon_social AS cliente, e.descripcion AS equipo, " +
                     "a.fecha_inicio, a.dias, a.monto_cobrado " +
                     "FROM alquileres a " +
                     "INNER JOIN clientes c ON a.documento_cliente = c.documento " +
                     "INNER JOIN equipos e ON a.codigo_equipo = e.codigo " +
                     "WHERE a.devuelto = FALSE " +
                     "ORDER BY a.fecha_inicio";

        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            boolean hayResultados = false;
            System.out.println("---------- ALQUILERES PENDIENTES ----------");
            while (rs.next()) {
                hayResultados = true;
                System.out.println("------------------------------");
                System.out.println("ID ALQUILER: " + rs.getInt("id_alquiler"));
                System.out.println("CLIENTE:     " + rs.getString("cliente"));
                System.out.println("EQUIPO:      " + rs.getString("equipo"));
                System.out.println("FECHA:       " + rs.getDate("fecha_inicio"));
                System.out.println("DIAS:        " + rs.getInt("dias"));
                System.out.println("MONTO: S/    " + rs.getDouble("monto_cobrado"));
            }
            if (!hayResultados) {
                System.out.println("¡No hay alquileres pendientes!");
            }
        }
    }
}