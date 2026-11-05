package controller;

import business.ClienteBusiness;
import model.Cliente;
import java.util.List;

public class ClienteController {
    private ClienteBusiness business;

    public ClienteController(ClienteBusiness business) {
        this.business = business;
    }

    public void consultarPorDocumento(String documento) {
        Cliente c = business.buscarPorDocumento(documento);
        if (c != null) {
            System.out.println(c);
        } else {
            System.out.println("Cliente no encontrado.");
        }
    }

    public void consultarPorNombre(String texto) {
        List<Cliente> lista = business.buscarPorNombre(texto);
        if (lista.isEmpty()) {
            System.out.println("Sin resultados.");
        } else {
            lista.forEach(System.out::println);
        }
    }

    public void registrarCliente(Cliente c) {
        if (business.registrar(c)) {
            System.out.println("Cliente registrado.");
        }
    }

    public void listarClientes() {
        business.listar().forEach(System.out::println);
    }
}