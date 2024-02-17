/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ConexionServidorHTTP;

import Datos.ConexionConBDD;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author DAM_M
 */
public class AtenderCliente extends Thread {

    private final SSLSocket skCliente;
    private InputStreamReader flujo_entrada;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    public AtenderCliente(SSLSocket skCliente) {
        this.skCliente = skCliente;
    }

    @Override
    public void run() {
        try {
            flujo_entrada = new InputStreamReader(skCliente.getInputStream());
            bufferedReader = new BufferedReader(flujo_entrada);
            printWriter = new PrintWriter(skCliente.getOutputStream(), true);

            String contenidoHTML;
            String url = bufferedReader.readLine();
            String linea;
            int contentLength = 0;

            while ((linea = bufferedReader.readLine()) != null && !linea.isEmpty()) {
                if (linea.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(linea.substring("Content-Length:".length()).trim());
                }
            }

            System.out.println(url);
            String[] partes = url.split(" ");
            String metodo = partes[0];

            if (metodo != null) {
                switch (metodo) {
                    case "GET" -> {
                        contenidoHTML = Gestor.GestorDePaginas.getHTML_Login();
                        enviarRespuestaHTML(contenidoHTML);
                    }
                    case "POST" -> {
                        atenderPorPost(contentLength);
                    }
                    default -> {
                        contenidoHTML = Gestor.GestorDePaginas.getHTML_Error();
                        enviarRespuestaHTML(contenidoHTML);
                        System.out.println("-> Ups, ha ocurrido algo inesperado: ");
                    }
                }
            } else {
                contenidoHTML = Gestor.GestorDePaginas.getHTML_Error();
                enviarRespuestaHTML(contenidoHTML);
                System.out.println("--> Ups.");
            }
        } catch (IOException ex) {
            Logger.getLogger(AtenderCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void atenderPorPost(int contentLength) {
        try {
            // Crear un StringBuilder para almacenar el cuerpo del mensaje POST
            StringBuilder bodyBuilder = new StringBuilder();

            // Leer el cuerpo del mensaje POST exactamente contentLength bytes
            char[] buffer = new char[contentLength];
            int bytesRead = bufferedReader.read(buffer, 0, contentLength);
            bodyBuilder.append(buffer, 0, bytesRead);

            // Convertir el cuerpo del mensaje a una cadena
            String body = bodyBuilder.toString();

            // Procesar el cuerpo del mensaje POST como desees, por ejemplo, puedes imprimirlo
            System.out.println("Cuerpo del mensaje POST:");
            System.out.println(body);
            // Dividir el string en nombre y número usando el carácter "&" como delimitador
            String[] partes = body.split("&");

            // Crear variables para almacenar el nombre y el número
            String nombre = "";
            String numero = "";

            // Iterar sobre las partes y extraer el nombre y el número
            for (String parte : partes) {
                String[] keyValue = parte.split("=");
                String key = keyValue[0];
                String value = keyValue[1];

                // Verificar si la parte es el nombre o el número y asignar el valor correspondiente
                if (key.equals("nombre")) {
                    nombre = value;
                } else if (key.equals("contrasena")) {
                    numero = value;
                }
            }

            // Imprimir los resultados
            System.out.println("Nombre: " + nombre);
            System.out.println("Contraseña: " + numero);
            // Aquí puedes realizar cualquier procesamiento adicional necesario con los datos del formulario
            // Enviar una respuesta al cliente
            Datos.ConexionConBDD miCone = new ConexionConBDD();
            int numero1 = Integer.parseInt(numero);
            int id = miCone.consultarIDJugador(nombre, numero1);
            miCone.obtenerPartidasTerminadasPorJugador(id);
            crearHTML_TablaPartidas(id);

        } catch (IOException ex) {
            Logger.getLogger(AtenderCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void crearHTML_TablaPartidas(int id) {

        String contenidoHTML;
        Datos.ConexionConBDD misDatos = new ConexionConBDD();
        HashMap<Integer, String> mapaPartidasTerminadas;

        mapaPartidasTerminadas = misDatos.obtenerPartidasTerminadasPorJugador(id);

        // Lista para las nuevas filas HTML
        List<String> nuevasFilasHTML = new ArrayList<>();
        // Lista para las nuevas opciones HTML del formulario

        // Iterar sobre las partidas terminadas y generar las filas HTML correspondientes
        for (Map.Entry<Integer, String> entrada : mapaPartidasTerminadas.entrySet()) {

            int idPartida = entrada.getKey();
            String[] detallesPartida = entrada.getValue().split(";");
            String nombreJugador1 = detallesPartida[1];
            String nombreJugador2 = detallesPartida[2];
            String nombreGanador = detallesPartida[3];
            String nombreUltimoTurno = detallesPartida[4];

            // Crear la fila HTML con los detalles de la partida
            String filaHTML = String.format("<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                    idPartida, nombreJugador1, nombreJugador2, nombreGanador, nombreUltimoTurno);

            // Agregar la fila HTML a la lista de nuevas filas
            nuevasFilasHTML.add(filaHTML);

        }

        contenidoHTML = Gestor.GestorDePaginas.getHTML_Index(nuevasFilasHTML);
        enviarRespuestaHTML(contenidoHTML);
    }

    private void enviarRespuestaHTML(String contenidoHTML) {
        // Enviar una respuesta HTTP al cliente
        printWriter.println("HTTP/1.1 200 OK");
        printWriter.println("Content-Type: text/html");
        printWriter.println("Content-Length: " + contenidoHTML.length());
        printWriter.println();
        printWriter.println(contenidoHTML);

        // Cerrar la conexión
        try {
            skCliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
