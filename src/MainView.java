import business.AlquilerBusiness;
import model.*;
import dao.implement.ClienteDAOImpl;
import dao.implement.EquipoDaoImpl;
import service.Conexion;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Scanner;

public class MainView {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int opcionPrincipal = 0;
        do {
            System.out.println("SISTEMA DE GESTION DE ALQUILERES V0");
            System.out.println("1. GESTION DE CLIENTES");
            System.out.println("2. GESTION DE EQUIPOS");
            System.out.println("3. SALIR DEL SISTEMA");
            System.out.print("SELECCIONE UNA OPCION: ");

            if (scanner.hasNextInt()) {
                opcionPrincipal = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("POR FAVOR, INGRESE UN NUMERO VALIDO.");
                scanner.next();
                continue;
            }

            switch (opcionPrincipal) {
                case 1:
                    menuClientes();
                    break;
                case 2:
                    menuEquipos();
                    break;
                case 3:
                    System.out.println("SALIENDO DEL SISTEMA.");
                    break;
                default:
                    System.out.println("OPCION NO VALIDA.");
            }
        } while (opcionPrincipal != 3);
        scanner.close();
    }

    private static void menuClientes() {
        int op = 0;
        ClienteDAOImpl clienteDAO = new ClienteDAOImpl();
        do {
            System.out.println("MODULO DE CLIENTES");
            System.out.println("1. BUSCAR CLIENTE POR DOCUMENTO");
            System.out.println("2. BUSCAR CLIENTES POR NOMBRE");
            System.out.println("3. VOLVER AL MENU PRINCIPAL");
            System.out.print("SELECCIONE UNA OPCION: ");
            if (scanner.hasNextInt()) {
                op = scanner.nextInt();
                scanner.nextLine();
            } else {
                scanner.next();
                continue;
            }

            try (Connection con = Conexion.getConnection()) {
                switch (op) {
                    case 1:
                        System.out.print("INGRESE DOCUMENTO: ");
                        String doc = scanner.nextLine();
                        Cliente c = clienteDAO.buscarPorDocumento(con, doc);
                        if (c != null) {
                            System.out.println("CLIENTE ENCONTRADO NOMBRE: " + c.getNombreRazonSocial() + " | TELEFONO: " + c.getTelefono());
                        } else {
                            System.out.println("CLIENTE NO ENCONTRADO.");
                        }
                        break;
                    case 2:
                        System.out.print("INGRESE TEXTO A BUSCAR: ");
                        String texto = scanner.nextLine();
                        var lista = clienteDAO.buscarPorNombre(con, texto);
                        System.out.println("RESULTADOS ENCONTRADOS: " + lista.size());
                        for (Cliente cli : lista) {
                            System.out.println("- [" + cli.getDocumento() + "] " + cli.getNombreRazonSocial() + " (" + cli.getTelefono() + ")");
                        }
                        break;
                    case 3:
                        System.out.println("REGRESANDO.");
                        break;
                    default:
                        System.out.println("OPCION INVALIDA.");
                }
            } catch (Exception e) {
                System.err.println("ERROR EN MODULO CLIENTES: " + e.getMessage());
            }
        } while (op != 3);
    }

    private static void menuEquipos() {
        int op = 0;
        EquipoDaoImpl equipoDAO = new EquipoDaoImpl();
        do {
            System.out.println("MODULO DE EQUIPOS");
            System.out.println("1. BUSCAR EQUIPO POR CODIGO");
            System.out.println("2. REPORTE DE FACTURACION POR TIPO");
            System.out.println("3. VOLVER AL MENU PRINCIPAL");
            System.out.print("SELECCIONE UNA OPCION: ");
            if (scanner.hasNextInt()) {
                op = scanner.nextInt();
                scanner.nextLine();
            } else {
                scanner.next();
                continue;
            }

            try (Connection con = Conexion.getConnection()) {
                switch (op) {
                    case 1:
                        System.out.print("INGRESE CODIGO DEL EQUIPO: ");
                        String codigo = scanner.nextLine();
                        Equipo eq = equipoDAO.buscarPorCodigo(con, codigo);
                        if (eq != null) {
                            System.out.println("EQUIPO: " + eq.getDescripcion() + " | TIPO: " + eq.mostrarTipo() + " | DISPONIBLE: " + (eq.isDisponible() ? "SI" : "NO"));
                        } else {
                            System.out.println("EQUIPO NO ENCONTRADO.");
                        }
                        break;
                    case 2:
                        equipoDAO.mostrarFacturacionPorTipo(con);
                        break;
                    case 3:
                        System.out.println("REGRESANDO.");
                        break;
                    default:
                        System.out.println("OPCION INVALIDA.");
                }
            } catch (Exception e) {
                System.err.println("ERROR EN MODULO EQUIPOS: " + e.getMessage());
            }
        } while (op != 3);
    }
}