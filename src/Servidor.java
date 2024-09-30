//El proyecto de Arquitectura Cliente-Servidor se desarrolló utilizando NetBeans como entorno de desarrollo. En esta arquitectura, el servidor es responsable de gestionar las solicitudes de los clientes conectados, quienes envían y reciben mensajes a través de la red. }
//El uso de NetBeans facilitó la organización del código y la depuración del mismo, permitiendo manejar eficientemente múltiples conexiones mediante la implementación de sockets. El objetivo de este ejercicio es demostrar la interacción entre aplicaciones distribuidas, donde los clientes se comunican con un servidor centralizado que coordina las respuestas, poniendo en práctica conceptos de programación en red.
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Clase Servidor: Inicializa un servidor TCP que escucha en un puerto específico (8080).
 * Administra múltiples conexiones de clientes mediante un pool de hilos (10 por defecto).
 */
public class Servidor {
    private static final int PUERTO = 8080;
    private static final int MAX_HILOS = 10;
    private ExecutorService manejadorClientes;

    /**
     * Constructor de la clase Servidor. Inicializa el pool de hilos.
     */
    public Servidor() {
        manejadorClientes = Executors.newFixedThreadPool(MAX_HILOS);
    }

    /**
     * Inicia el servidor, aceptando conexiones de clientes y delegando su manejo
     * a instancias de la clase AtenderCliente.
     */
    public void iniciar() {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("[SERVIDOR ACTIVO] Esperando conexiones en el puerto " + PUERTO + "...");

            // Ciclo infinito para aceptar clientes
            while (true) {
                Socket socketCliente = servidor.accept();
                System.out.println("[NUEVA CONEXIÓN] Cliente conectado desde: " + socketCliente.getInetAddress());
                manejadorClientes.submit(new AtenderCliente(socketCliente));
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Ocurrió un problema en el servidor: " + e.getMessage());
        } finally {
            cerrarManejadorClientes();
        }
    }

    /**
     * Cierra el pool de hilos si está activo.
     */
    private void cerrarManejadorClientes() {
        if (manejadorClientes != null && !manejadorClientes.isShutdown()) {
            manejadorClientes.shutdown();
        }
    }

    /**
     * Método principal para ejecutar el servidor.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}

/**
 * Clase AtenderCliente: Implementa Runnable para manejar la interacción con un cliente
 * de forma concurrente, leyendo y respondiendo a los mensajes del cliente.
 */
class AtenderCliente implements Runnable {
    private Socket socketCliente;

    /**
     * Constructor que inicializa el socket del cliente.
     *
     * @param socketCliente Socket conectado al cliente.
     */
    public AtenderCliente(Socket socketCliente) {
        this.socketCliente = socketCliente;
    }

    /**
     * Método que se ejecuta en un hilo separado para manejar la comunicación con el cliente.
     */
    @Override
    public void run() {
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
             PrintWriter salida = new PrintWriter(socketCliente.getOutputStream(), true)) {

            String mensajeCliente;
            while ((mensajeCliente = entrada.readLine()) != null) {
                System.out.println("[MENSAJE CLIENTE] " + mensajeCliente);
                salida.println("Servidor: Mensaje recibido");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Problema de E/S con el cliente: " +
