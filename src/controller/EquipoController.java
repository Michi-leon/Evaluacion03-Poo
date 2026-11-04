package controller;

import dao.interfaces.EquipoDao;
import dao.implement.EquipoDaoImpl;
import model.Equipo;
import model.Camara;
import model.Iluminacion;
import model.Audio;

public class EquipoController {
    private final EquipoDao equipoDAO = new EquipoDaoImpl();

    //REGISTRAR
    public void registrar(String codigo, String descripcion, double tarifa, int tipo) {
        if (codigo == null || codigo.equals("")) {
            System.out.println("El codigo ingresado no es valido.");
            return;
        }

        if (equipoDAO.buscarPorId(codigo) != null) {
            System.out.println("El codigo de equipo ya esta registrado.");
            return;
        }

        Equipo e = null;
        if (tipo == 1) e = new Camara(codigo, descripcion, tarifa, true);
        if (tipo == 2) e = new Iluminacion(codigo, descripcion, tarifa, true);
        if (tipo == 3) e = new Audio(codigo, descripcion, tarifa, true);

        if (e != null) {
            equipoDAO.insertar(e);
        } else {
            System.out.println("Tipo de equipo no valido.");
        }
    }

    //MOSTRAR
    public void mostrarTodos() {
        java.util.List<Equipo> lista = equipoDAO.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("No se han registrado equipos en la base de datos.");
        } else {
            for (Equipo e : lista) {
                e.imprimirInformacion();
                System.out.println();
            }
        }
    }

    //ACTUALIZAR
    public void modificarEquipo(String codigo, String nuevaDesc, double nuevaTarif, boolean disp) {
        Equipo e = equipoDAO.buscarPorId(codigo);
        if (e != null) {
            e.setDescripcion(nuevaDesc);
            e.setTarifaDiaria(nuevaTarif);
            e.setDisponible(disp);
            equipoDAO.actualizar(e);
        } else {
            System.out.println("No se encontro el equipo seleccionado.");
        }
    }

    //ELIMINAR
    public void eliminarEquipo(String codigo) {
        if (codigo == null || codigo.equals("")) {
            System.out.println("El codigo ingresado no es valido.");
            return;
        }
        equipoDAO.eliminar(codigo);
    }

    //REPORTE
    public void mostrarFacturacion() {
        equipoDAO.mostrarFacturacionPorTipo();
    }
}