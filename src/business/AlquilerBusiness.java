package business;

import model.Alquiler;
import model.Equipo;
import dao.interfaces.AlquilerDao;
import dao.interfaces.EquipoDao;
import dao.implement.AlquilerDAOImpl;
import dao.implement.EquipoDaoImpl;
import service.Conexion;

import java.sql.Connection;
import java.sql.SQLException;

public class AlquilerBusiness {
    private final AlquilerDao alquilerDAO = new AlquilerDAOImpl();
    private final EquipoDao equipoDAO = new EquipoDaoImpl();

    public boolean registrarAlquiler(Alquiler alquiler) {
        Connection con = null;
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false); 

            Equipo equipo = equipoDAO.buscarPorCodigo(con, alquiler.getEquipo().getCodigo());
            if (equipo == null) {
                throw new SQLException("El equipo especificado no existe.");
            }
            if (!equipo.isDisponible()) {
                throw new SQLException("El equipo no esta disponible para alquiler.");
            }

            alquilerDAO.registrarAlquiler(con, alquiler);
            equipoDAO.cambiarDisponibilidad(con, equipo.getCodigo(), false);

            con.commit();
            System.out.println("Alquiler registrado exitosamente. Equipo reservado.");
            return true;

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                    System.err.println("Transaccion revertida (Rollback). Error: " + e.getMessage());
                } catch (SQLException ex) {
                    System.err.println("Error en rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexion: " + e.getMessage());
                }
            }
        }
    }

    public boolean procesarDevolucion(int idAlquiler, String codigoEquipo) {
        Connection con = null;
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false);

            alquilerDAO.marcarComoDevuelto(con, idAlquiler);
            equipoDAO.cambiarDisponibilidad(con, codigoEquipo, true);

            con.commit(); 
            System.out.println("Devolucion procesada exitosamente. Equipo liberado.");
            return true;

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                    System.err.println("Transaccion revertida en devolucion: " + e.getMessage());
                } catch (SQLException ex) {
                    System.err.println("Error en rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexion: " + e.getMessage());
                }
            }
        }
    }
}