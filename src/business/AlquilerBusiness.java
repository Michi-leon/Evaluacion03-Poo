package business;

import model.Alquiler;
import model.Equipo;
import dao.interfaces.AlquilerDAO;
import dao.interfaces.EquipoDAO;
import dao.impl.AlquilerDAOImpl;
import dao.impl.EquipoDAOImpl;
import service.Conexion;

import java.sql.Connection;
import java.sql.SQLException;

public class AlquilerBusiness {
    private final AlquilerDAO alquilerDAO = new AlquilerDAOImpl();
    private final EquipoDAO equipoDAO = new EquipoDAOImpl();

    public boolean registrarAlquiler(Alquiler alquiler) {
        Connection con = null;
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false); 

            Equipo equipo = equipoDAO.buscarPorCodigo(con, alquiler.getEquipo().getCodigo());
            if (equipo == null) {
                throw new SQLException("EL EQUIPO CON CODIGO" + alquiler.getEquipo().getCodigo() + "NO EXISTE");
            }
            if (!equipo.isDisponible()) {
                throw new SQLException("EQUIPO NO DISPONIBLE.");
            }

            alquilerDAO.registrarAlquiler(con, alquiler);

            equipoDAO.actualizarEstadoDisponibilidad(con, equipo.getCodigo(), false);

            con.commit();
            System.out.println("ALQUILER REGISTRADO EQUIPO BLOQUEADO EXITOSAMENTE");
            return true;

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                    System.err.println("TRANSACCION REVERTIDA - ROLLBACK OCURRIO UN ERROR : " + e.getMessage());
                } catch (SQLException ex) {
                    System.err.println("ERROR AL REALIZAR ROLLBACK" + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    System.err.println("ERROR DE CONEXION " + e.getMessage());
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

            
            equipoDAO.actualizarEstadoDisponibilidad(con, codigoEquipo, true);

            con.commit(); 
            System.out.println("DEVOLUCION PROCESADA EQUIPO LIBERADO");
            return true;

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                    System.err.println("TRANSACCION REVERTIDA ERROR EN DEVOLUCION " + e.getMessage());
                } catch (SQLException ex) {
                    System.err.println("ERROR EN EL ROLLBACK " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    System.err.println("ERROR DE CONEXION" + e.getMessage());
                }
            }
        }
    }
}
