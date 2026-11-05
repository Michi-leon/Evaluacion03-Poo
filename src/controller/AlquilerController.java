package controller;
import business.AlquilerBusiness;
import model.Alquiler;
import java.sql.SQLException;

public class AlquilerController{
    private final AlquilerBusiness business;
    public AlquilerController(){
        business = new AlquilerBusiness();
    }

    public boolean registrarAlquiler(Alquiler alquiler){
        if (alquiler == null) {
            System.out.println("¡Datos de devolución inválidos!");
            return false;
        }
        if (alquiler.getCliente() == null || alquiler.getEquipo() == null){
            System.out.println("¡Debe indicar cliente y equipo!");
            return false;
        }
        if (alquiler.getDias() <= 0){
            System.out.println("¡Los días deben ser mayor a cero!");
            return false;
        }
        if (alquiler.getFechaInicio() == null){
            System.out.println("¡La fecha de inicio es obligatoria!");
            return false;
        }
        return business.registrarAlquiler(alquiler);
    }

    public boolean procesarDevolucion(
            int idAlquiler, String codigoEquipo){
        if (idAlquiler <= 0 || codigoEquipo == null || codigoEquipo.trim().isEmpty()){

            System.out.println("¡Datos de devolución inválidos!");
            return false;
        }
        return business.procesarDevolucion(
            idAlquiler, codigoEquipo
        );
    }

    public void listarPendientes(){
        try (java.sql.Connection con = service.Conexion.getConnection()){
            dao.implement.AlquilerDAOImpl dao =
                new dao.implement.AlquilerDAOImpl();
            dao.mostrarPendientesDevolucion(con);
        }
        catch (SQLException e){
            System.out.println("Error al consultar alquiler: " + e.getMessage()
            );
        }
    }
}