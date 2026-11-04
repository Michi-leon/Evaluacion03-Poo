package dao.implement;

import dao.interfaces.EquipoDao;
import model.Equipo;
import model.Camara;
import model.Iluminacion;
import model.Audio;
import services.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoDaoImpl implements EquipoDao {

    //REGISTRAR
    @Override
    public boolean insertar(Equipo e) {
        String queryMadre = "INSERT INTO equipos (codigo, descripcion, tarifa_diaria, disponible) VALUES (?, ?, ?, ?)";
        int filasMadre = 0;
        int filasHija = 0;
        Connection cn = null;

        try {
            cn = DatabaseConnector.getConnection();
            cn.setAutoCommit(false);

            PreparedStatement psP = cn.prepareStatement(queryMadre);
            psP.setString(1, e.getCodigo());
            psP.setString(2, e.getDescripcion());
            psP.setDouble(3, e.getTarifaDiaria());
            psP.setBoolean(4, e.isDisponible());
            filasMadre = psP.executeUpdate();
            psP.close();

            if (e.mostrarTipo().equals("Camara")) {
                String queryCamara = "INSERT INTO equipos_camara (codigo) VALUES (?)";
                PreparedStatement psC = cn.prepareStatement(queryCamara);
                psC.setString(1, e.getCodigo());
                filasHija = psC.executeUpdate();
                psC.close();
            } else if (e.mostrarTipo().equals("Iluminacion")) {
                String queryIlum = "INSERT INTO equipos_iluminacion (codigo) VALUES (?)";
                PreparedStatement psI = cn.prepareStatement(queryIlum);
                psI.setString(1, e.getCodigo());
                filasHija = psI.executeUpdate();
                psI.close();
            } else if (e.mostrarTipo().equals("Audio")) {
                String queryAudio = "INSERT INTO equipos_audio (codigo) VALUES (?)";
                PreparedStatement psA = cn.prepareStatement(queryAudio);
                psA.setString(1, e.getCodigo());
                filasHija = psA.executeUpdate();
                psA.close();
            }

            if (filasMadre > 0 && filasHija > 0) {
                cn.commit();
                System.out.println("Se registro correctamente el equipo.");
                return true;
            } else {
                cn.rollback();
                return false;
            }
        } catch (SQLException error) {
            System.out.println("Error al insertar equipo: " + error.getMessage());
            try { if (cn != null) cn.rollback(); } catch (SQLException ex) { System.out.println(ex.getMessage()); }
            return false;
        } finally {
            try { if (cn != null) cn.setAutoCommit(true); } catch (SQLException error) { System.out.println(error.getMessage()); }
        }
    }

    //BUSCAR
    @Override
    public Equipo buscarPorId(String idBuscar) {
        String queryPadre = "SELECT codigo, descripcion, tarifa_diaria, disponible FROM equipos WHERE codigo = ?";
        try {
            Connection cn = DatabaseConnector.getConnection();
            PreparedStatement ps = cn.prepareStatement(queryPadre);
            ps.setString(1, idBuscar);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String desc = rs.getString("descripcion");
                double tarifa = rs.getDouble("tarifa_diaria");
                boolean disp = rs.getBoolean("disponible");

                String queryC = "SELECT codigo FROM equipos_camara WHERE codigo = ?";
                PreparedStatement psC = cn.prepareStatement(queryC);
                psC.setString(1, idBuscar);
                ResultSet rsC = psC.executeQuery();
                if (rsC.next()) {
                    Camara cam = new Camara(idBuscar, desc, tarifa, disp);
                    rsC.close(); psC.close(); rs.close(); ps.close();
                    return cam;
                }
                rsC.close(); psC.close();

                String queryI = "SELECT codigo FROM equipos_iluminacion WHERE codigo = ?";
                PreparedStatement psI = cn.prepareStatement(queryI);
                psI.setString(1, idBuscar);
                ResultSet rsI = psI.executeQuery();
                if (rsI.next()) {
                    Iluminacion ilu = new Iluminacion(idBuscar, desc, tarifa, disp);
                    rsI.close(); psI.close(); rs.close(); ps.close();
                    return ilu;
                }
                rsI.close(); psI.close();

                Audio aud = new Audio(idBuscar, desc, tarifa, disp);
                rs.close(); ps.close();
                return aud;
            }
            rs.close(); ps.close();
        } catch (SQLException error) {
            System.out.println("Error al buscar: " + error.getMessage());
        }
        return null;
    }

    //LISTAR
    @Override
    public List<Equipo> listarTodos() {
        List<Equipo> lista = new ArrayList<>();
        String query = "SELECT codigo FROM equipos";
        try {
            Connection cn = DatabaseConnector.getConnection();
            PreparedStatement ps = cn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String codigo = rs.getString("codigo");
                // Reutilizamos el buscarPorId secuencial para armar la lista polimórfica de forma limpia
                Equipo eq = buscarPorId(codigo);
                if (eq != null) {
                    lista.add(eq);
                }
            }
            rs.close(); ps.close();
        } catch (SQLException error) {
            System.out.println("Error al listar: " + error.getMessage());
        }
        return lista;
    }

    //ACTUALIZAR
    @Override
    public boolean actualizar(Equipo e) {
        String sql = "UPDATE equipos SET descripcion = ?, tarifa_diaria = ?, disponible = ? WHERE codigo = ?";
        Connection cn = null;
        try {
            cn = DatabaseConnector.getConnection();
            cn.setAutoCommit(false);

            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, e.getDescripcion());
            ps.setDouble(2, e.getTarifaDiaria());
            ps.setBoolean(3, e.isDisponible());
            ps.setString(4, e.getCodigo());

            int filasAfectadas = ps.executeUpdate();
            ps.close();

            if (filasAfectadas > 0) {
                cn.commit(); 
                System.out.println("Se actualizo correctamente el equipo.");
                return true;
            } else {
                cn.rollback();
                return false;
            }
        } catch (SQLException error) {
            System.out.println("Error al actualizar: " + error.getMessage());
            try { if (cn != null) cn.rollback(); } catch (SQLException ex) { System.out.println(ex.getMessage()); }
            return false;
        } finally {
            try { if (cn != null) cn.setAutoCommit(true); } catch (SQLException error) { System.out.println(error.getMessage()); }
        }
    }

    //ELIMINAR
    @Override
    public boolean eliminar(String id) {
        int filasHija = 0;
        int filasMadre = 0;
        Connection cn = null;
        
        try {
            cn = DatabaseConnector.getConnection();
            cn.setAutoCommit(false); 

            Equipo e = buscarPorId(id);
            if (e == null) {
                System.out.println("No se encontro el equipo seleccionado.");
                cn.rollback();
                return false;
            }

            if (e.mostrarTipo().equals("Camara")) {
                String sqlC = "DELETE FROM equipos_camara WHERE codigo = ?";
                PreparedStatement psC = cn.prepareStatement(sqlC);
                psC.setString(1, id);
                filasHija = psC.executeUpdate();
                psC.close();
            } else if (e.mostrarTipo().equals("Iluminacion")) {
                String sqlI = "DELETE FROM equipos_iluminacion WHERE codigo = ?";
                PreparedStatement psI = cn.prepareStatement(sqlI);
                psI.setString(1, id);
                filasHija = psI.executeUpdate();
                psI.close();
            } else if (e.mostrarTipo().equals("Audio")) {
                String sqlA = "DELETE FROM equipos_audio WHERE codigo = ?";
                PreparedStatement psA = cn.prepareStatement(sqlA);
                psA.setString(1, id);
                filasHija = psA.executeUpdate();
                psA.close();
            }

            String sqlMadre = "DELETE FROM equipos WHERE codigo = ?";
            PreparedStatement psM = cn.prepareStatement(sqlMadre);
            psM.setString(1, id);
            filasMadre = psM.executeUpdate();
            psM.close();

            if (filasMadre > 0 && filasHija > 0) {
                cn.commit();
                System.out.println("Se elimino correctamente el equipo.");
                return true;
            } else {
                cn.rollback();
                return false;
            }
        } catch (SQLException error) {
            System.out.println("Error al eliminar: " + error.getMessage());
            try { if (cn != null) cn.rollback(); } catch (SQLException ex) { System.out.println(ex.getMessage()); }
            return false;
        } finally {
            try { if (cn != null) cn.setAutoCommit(true); } catch (SQLException error) { System.out.println(error.getMessage()); }
        }
    }

    //ACTUALIZAR DISPONIBILIDAD
    @Override
    public boolean cambiarDisponibilidad(Connection cn, String codigo, boolean disponible) {
        String sql = "UPDATE equipos SET disponible = ? WHERE codigo = ?";
        try {
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setBoolean(1, disponible);
            ps.setString(2, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException error) {
            System.out.println("Error al cambiar disponibilidad: " + error.getMessage());
            return false;
        }
    }

    // REPORTE
    @Override
    public void mostrarFacturacionPorTipo() {
        String query = "SELECT 'CAMARA' AS tipo, SUM(a.monto_cobrado) AS total FROM alquileres a INNER JOIN equipos_camara h ON a.codigo_equipo = h.codigo " +
                       "UNION ALL " +
                       "SELECT 'ILUMINACION' AS tipo, SUM(a.monto_cobrado) AS total FROM alquileres a INNER JOIN equipos_iluminacion h ON a.codigo_equipo = h.codigo " +
                       "UNION ALL " +
                       "SELECT 'AUDIO' AS tipo, SUM(a.monto_cobrado) AS total FROM alquileres a INNER JOIN equipos_audio h ON a.codigo_equipo = h.codigo";
        try {
            Connection cn = DatabaseConnector.getConnection();
            PreparedStatement ps = cn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- TOTAL FACTURADO POR TIPO DE EQUIPO ---");
            while (rs.next()) {
                double total = rs.getDouble("total");
                System.out.println(rs.getString("tipo") + " - S/ " + total);
            }
            rs.close(); ps.close();
        } catch (SQLException error) {
            System.out.println("Error al generar reporte: " + error.getMessage());
        }
    }
}