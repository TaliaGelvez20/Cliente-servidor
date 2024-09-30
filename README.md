# Aplicación Cliente-Servidor en Java

Este proyecto es una aplicación básica cliente-servidor escrita en Java, que demuestra cómo implementar un servidor capaz de atender múltiples clientes de forma concurrente utilizando hilos (ExecutorService). La aplicación se compone de dos partes: un servidor que escucha conexiones en un puerto y procesa mensajes de varios clientes, y un cliente que envía mensajes al servidor y recibe respuestas.

# Integrantes

- Talia Yaritza Gelvez Gelvez
- Paula Andrea Ramirez Casilimas

# Estructura de la Aplicación

- Servidor: Administra múltiples conexiones de clientes a través de un pool de hilos, permitiendo la concurrencia y evitando bloqueos.
- Cliente: Se conecta al servidor y envía mensajes para recibir respuestas en tiempo real.

# Atributos de Calidad

1. Escalabilidad: El servidor puede manejar múltiples clientes simultáneamente usando un pool de hilos.
2. Mantenibilidad: El código está modularizado y organizado en clases, facilitando su mantenimiento.
3. Rendimiento: Al utilizar hilos, el servidor puede gestionar múltiples conexiones sin bloquearse mientras espera las respuestas de los clientes.

# Requisitos

- Java JDK 8 o superior.
- Un editor de texto o IDE como IntelliJ IDEA o Eclipse.

# Ejecución de la Aplicación

Paso 1: Ejecutar el Servidor

Compila y ejecuta la clase Servidor para iniciar el servidor en el puerto 8080. El servidor quedará a la espera de conexiones entrantes de los clientes.

Paso 2: Ejecutar el Cliente

En otra terminal o consola, compila y ejecuta la clase Cliente. El cliente intentará conectarse al servidor en la dirección 127.0.0.1 y puerto 8080. Una vez conectado, podrás enviar mensajes al servidor.

# Interacción

Cliente: Escribe cualquier mensaje en la consola, y el servidor lo recibirá y responderá con "Mensaje recibido".
Servidor: Procesará los mensajes de los clientes y los imprimirá en su consola, respondiendo con la confirmación.

# Finalizar la Conexión

Para finalizar la conexión desde el cliente, simplemente escribe salir. El cliente cerrará la conexión y se desconectará del servidor.

# Código del servidor

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Clase Servidor: Inicializa un servidor TCP que escucha en el puerto 8080.
 * Administra múltiples conexiones de clientes utilizando un pool de hilos.
 */
public class Servidor {
    private static final int PUERTO = 8080;
    private static final int MAX_HILOS = 10;
    private ExecutorService manejadorClientes;

    public Servidor() {
        manejadorClientes = Executors.newFixedThreadPool(MAX_HILOS);
    }

    public void iniciar() {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("[SERVIDOR ACTIVO] Esperando conexiones en el puerto " + PUERTO + "...");

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

    private void cerrarManejadorClientes() {
        if (manejadorClientes != null && !manejadorClientes.isShutdown()) {
            manejadorClientes.shutdown();
        }
    }

    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}

class AtenderCliente implements Runnable {
    private Socket socketCliente;

    public AtenderCliente(Socket socketCliente) {
        this.socketCliente = socketCliente;
    }

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
            System.err.println("[ERROR] Problema de E/S con el cliente: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }

    private void cerrarConexion() {
        try {
            if (socketCliente != null && !socketCliente.isClosed()) {
                socketCliente.close();
                System.out.println("[DESCONEXIÓN] Cliente desconectado.");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] No se pudo cerrar la conexión del cliente: " + e.getMessage());
        }
    }
}

# Código del cliente

import java.io.*;
import java.net.*;

/**
 * Clase Cliente: Se conecta a un servidor TCP utilizando la dirección IP y puerto especificados (127.0.0.1:8080).
 * Permite al usuario enviar mensajes al servidor y recibir respuestas.
 */
public class Cliente {
    private static final String HOST_SERVIDOR = "127.0.0.1";
    private static final int PUERTO_SERVIDOR = 8080;

    public void iniciar() {
        try (Socket socket = new Socket(HOST_SERVIDOR, PUERTO_SERVIDOR);
             BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader entradaServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salidaServidor = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Conectado al servidor. Escribe un mensaje (o 'salir' para finalizar):");

            String mensaje;
            while (!(mensaje = teclado.readLine()).equalsIgnoreCase("salir")) {
                salidaServidor.println(mensaje);
                String respuestaServidor = entradaServidor.readLine();
                System.out.println("[RESPUESTA SERVIDOR] " + respuestaServidor);
            }
        } catch (IOException e) {
            System.err.println("[ERROR] No se pudo establecer la conexión con el servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Cliente().iniciar();
    }
}
