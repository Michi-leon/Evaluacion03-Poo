package dao.interfaces;

import model.Equipo;
import java.sql.Connection;

public interface EquipoDao extends GenericDao<Equipo, String> {
    void mostrarFacturacionPorTipo(Connection con);
    boolean cambiarDisponibilidad(Connection con, String codigo, boolean disponible);
    Equipo buscarPorCodigo(Connection con, String codigo);
    void mostrarFacturacionPorTipo();
}