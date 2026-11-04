package dao.interfaces;

import model.Equipo;
import java.sql.Connection;

public interface EquipoDao extends GenericDao<Equipo, String> {
    void mostrarFacturacionPorTipo();
    boolean cambiarDisponibilidad(Connection cn, String codigo, boolean disponible);
}
