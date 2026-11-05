package dao.implement;

import dao.interfaces.EquipoDao;
import model.*;
import service.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoDaoImpl implements EquipoDao {

    @Override
    public boolean insertar(Equipo e) {
        String query = "INSERT INTO equipos (codigo, descripcion, tarifa_diaria, disponible, tipo_equipo) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, e.getCodigo());
            ps.setString(2, e.getDescripcion());
            ps.setDouble(3, e.getTarifaDiaria());
            ps.setBoolean(4, e.isDisponible());
            ps.setString(5, e.mostrarTipo());
            return ps.executeUpdate() > 0;
        } catch (SQLException error) {
            System.out.println("Error al insertar equipo: " + error.getMessage());
            return false;
        }
    }

    @Override
    public Equipo buscarPorId(String idBuscar) {
        try (Connection cn = Conexion.getConnection()) {
            return buscarPorCodigo(cn, idBuscar);
        } catch (SQLException error) {
            System.out.println("Error al conectar: " + error.getMessage());
        }
        return null;
    }

    @Override
    public Equipo buscarPorCodigo(Connection cn, String codigo) {
        String query = "SELECT codigo, descripcion, tarifa_diaria, disponible, tipo_equipo FROM equipos WHERE codigo = ?";
        try (PreparedStatement ps = cn.prepareStatement(query)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String desc = rs.getString("descripcion");
                    double tarifa = rs.getDouble("tarifa_diaria");
                    boolean disp = rs.getBoolean("disponible");
                    String tipo = rs.getString("tipo_equipo");

                    switch (tipo.toUpperCase()) {
                        case "CAMARA": return new Camara(codigo, desc, tarifa, disp);
                        case "ILUMINACION": return new Iluminacion(codigo, desc, tarifa, disp);
                        case "AUDIO": return new Audio(codigo, desc, tarifa, disp);
                        default: return null;
                    }
                }
            }
        } catch (SQLException error) {
            System.out.println("Error al buscar por codigo: " + error.getMessage());
        }
        return null;
    }

    @Override
    public List<Equipo> listarTodos() {
        List<Equipo> lista = new ArrayList<>();
        String query = "SELECT codigo FROM equipos";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Equipo eq = buscarPorId(rs.getString("codigo"));
                if (eq != null) lista.add(eq);
            }
        } catch (SQLException error) {
            System.out.println("Error al listar equipos: " + error.getMessage());
        }
        return lista;
    }

    @Override
    public boolean actualizar(Equipo e) {
        String sql = "UPDATE equipos SET descripcion = ?, tarifa_diaria = ?, disponible = ?, tipo_equipo = ? WHERE codigo = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, e.getDescripcion());
            ps.setDouble(2, e.getTarifaDiaria());
            ps.setBoolean(3, e.isDisponible());
            ps.setString(4, e.mostrarTipo());
            ps.setString(5, e.getCodigo());
            return ps.executeUpdate() > 0;
        } catch (SQLException error) {
            System.out.println("Error al actualizar equipo: " + error.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(String id) {
        String sql = "DELETE FROM equipos WHERE codigo = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException error) {
            System.out.println("Error al eliminar equipo: " + error.getMessage());
            return false;
        }
    }

    @Override
    public boolean cambiarDisponibilidad(Connection cn, String codigo, boolean disponible) {
        String sql = "UPDATE equipos SET disponible = ? WHERE codigo = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBoolean(1, disponible);
            ps.setString(2, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException error) {
            System.out.println("Error al cambiar disponibilidad: " + error.getMessage());
            return false;
        }
    }

    @Override
    public void mostrarFacturacionPorTipo(Connection cn) {
        String sql = "SELECT e.tipo_equipo, COALESCE(SUM(a.monto_cobrado), 0) AS total " +
                     "FROM (SELECT 'CAMARA' AS tipo_equipo, 1 AS orden UNION ALL " +
                     "      SELECT 'ILUMINACION', 2 UNION ALL " +
                     "      SELECT 'AUDIO', 3) e " +
                     "LEFT JOIN equipos eq ON e.tipo_equipo = eq.tipo_equipo " +
                     "LEFT JOIN alquileres a ON eq.codigo = a.codigo_equipo " +
                     "GROUP BY e.tipo_equipo, e.orden ORDER BY e.orden";
        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n--- FACTURACION A FIN DE MES POR TIPO DE EQUIPO ---");
            while (rs.next()) {
                System.out.printf("Tipo: %-12s | Total: S/ %10.2f\n",
                    rs.getString("tipo_equipo"), rs.getDouble("total"));
            }
        } catch (SQLException error) {
            System.out.println("Error al generar reporte: " + error.getMessage());
        }
    }
}