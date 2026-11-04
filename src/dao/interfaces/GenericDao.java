package dao.interfaces;

import java.util.List;

public interface GenericDao<T, K> {
    boolean insertar(T entidad);
    List<T> listarTodos();
    T buscarPorId(K id);
    boolean actualizar(T entidad);
    boolean eliminar(K id);
}