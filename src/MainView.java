import business.AlquilerBusiness;
import model.*;
import dao.implement.ClienteDAOImpl;
import dao.implement.EquipoDaoImpl;
import dao.implement.AlquilerDAOImpl;
import service.Conexion;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Scanner;

public class MainView {
    private static final Scanner scanner = new Scanner(System.in);
    private static final AlquilerBusiness alquilerBusiness = new AlquilerBusiness();

    public static void main(String[] args) {
        int opcionPrincipal = 0;
        do {
            System.out.println("  SISTEMA DE GESTION EQUIPAYA (LAB 12)");
            System.out.println("1. GESTION DE CLIENTES");
            System.out.println("2. GESTION DE EQUIPOS");
            System.out.println("3. GESTION DE ALQUILERES");
            System.out.println("4. SALIR DEL SISTEMA");
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
                    menuAlquileres();
                    break;
                case 4:
                    System.out.println("SALIENDO DEL SISTEMA.");
                    break;
                default:
                    System.out.println("OPCION NO VALIDA.");
            }
        } while (opcionPrincipal != 4);
        scanner.close();
    }

    private static void menuClientes() {
        int op = 0;
        ClienteDAOImpl clienteDAO = new ClienteDAOImpl();
        do {
            System.out.println("  MODULO DE CLIENTES");
            System.out.println("1. BUSCAR CLIENTE POR DOCUMENTO (MOSTRADOR)");
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
                            System.out.println("CLIENTE ENCONTRADO: " + c.getNombreRazonSocial() + " | TELEFONO: " + c.getTelefono());
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
            System.out.println("  MODULO DE EQUIPOS");
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

    private static void menuAlquileres() {
        int op = 0;
        AlquilerDAOImpl alquilerDAO = new AlquilerDAOImpl();
        do {
            System.out.println("    MODULO DE ALQUILERES");
            System.out.println("1. REGISTRAR NUEVO ALQUILER");
            System.out.println("2. PROCESAR DEVOLUCION DE EQUIPO");
            System.out.println("3. LISTAR ALQUILERES PENDIENTES");
            System.out.println("4. VOLVER AL MENU PRINCIPAL");
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
                        System.out.print("DOCUMENTO DEL CLIENTE: ");
                        String doc = scanner.nextLine();
                        System.out.print("CODIGO DEL EQUIPO: ");
                        String cod = scanner.nextLine();
                        System.out.print("CANTIDAD DE DIAS: ");
                        int dias = scanner.nextInt();
                        scanner.nextLine();

                        Cliente cli = new ClienteDAOImpl().buscarPorDocumento(con, doc);
                        if (cli == null) {
                            System.out.println("EL CLIENTE NO EXISTE EN LA BASE DE DATOS.");
                            break;
                        }
                        Equipo eq = new EquipoDaoImpl().buscarPorCodigo(con, cod);
                        if (eq == null) {
                            System.out.println("EL EQUIPO NO EXISTE.");
                            break;
                        }
                        double monto = eq.calcularMonto(dias);

                        Alquiler alquiler = new Alquiler(0, cli, eq, LocalDate.now(), dias, monto, false);
                        alquilerBusiness.registrarAlquiler(alquiler);
                        break;
                    case 2:
                        System.out.print("INGRESE ID DEL ALQUILER A DEVOLVER: ");
                        int idAlquiler = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("INGRESE CODIGO DEL EQUIPO LIBERADO: ");
                        String codEquipo = scanner.nextLine();
                        alquilerBusiness.procesarDevolucion(idAlquiler, codEquipo);
                        break;
                    case 3:
                        alquilerDAO.mostrarPendientesDevolucion(con);
                        break;
                    case 4:
                        System.out.println("REGRESANDO.");
                        break;
                    default:
                        System.out.println("OPCION INVALIDA.");
                }
            } catch (Exception e) {
                System.err.println("ERROR EN MODULO ALQUILERES: " + e.getMessage());
            }
        } while (op != 4);
    }
}