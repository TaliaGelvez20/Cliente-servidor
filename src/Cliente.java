import java.io.*;
import java.net.*;

/**
 * Clase Cliente: Se conecta a un servidor TCP utilizando la dirección IP y puerto especificados (127.0.0.1:8080).
 * Permite al usuario enviar mensajes al servidor y recibir respuestas.
 */
public class Cliente {
    private static final String HOST_SERVIDOR = "127.0.0.1";
    private static final int PUERTO_SERVIDOR = 8080;

    /**
     * Inicia la conexión con el servidor, permite enviar mensajes desde la consola
     * y recibir respuestas del servidor hasta que el usuario escriba 'salir'.
     */
    public void iniciar() {
        try (Socket socket = new Socket(HOST_SERVIDOR, PUERTO_SERVIDOR);
             BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader entradaServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salidaServidor = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Conectado al servidor. Escribe un mensaje (o 'salir' para finalizar):");

            String mensaje;
            // Lee mensajes de la consola y los envía al servidor
            while (!(mensaje = teclado.readLine()).equalsIgnoreCase("salir")) {
                salidaServidor.println(mensaje);
                String respuestaServidor = entradaServidor.readLine();
                System.out.println("[RESPUESTA SERVIDOR] " + respuestaServidor);
            }
        } catch (IOException e) {
            System.err.println("[ERROR] No se pudo establecer la conexión con el servidor: " + e.getMessage());
        }
    }

    /**
     * Método principal para ejecutar el cliente.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        new Cliente().iniciar();
    }
}
