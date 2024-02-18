/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ConexionServidorHTTP;

import Datos.ConexionConBDD;
import Datos.Usuario;
import Gestor.GestorDePaginas;
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
    private final Gestor.GestorDePaginas miGestor = GestorDePaginas.getGestor();
    private ArrayList<Integer> listaNegra = new ArrayList<>();

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

            listaNegra.add(2);//Ponemos una perosna en la lista negra
            listaNegra.add(10);//Ponemos una perosna en la lista negra
            while ((linea = bufferedReader.readLine()) != null && !linea.isEmpty()) {
                if (linea.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(linea.substring("Content-Length:".length()).trim());
                }
            }

            String[] partes = url.split(" ");
            String metodo = partes[0];

            if (metodo != null) {
                switch (metodo) {
                    case "GET" -> {
                        contenidoHTML = miGestor.getHTML_Login();
                        enviarRespuestaHTML(contenidoHTML);
                    }
                    case "POST" -> {
                        System.out.println(url);
                        atenderPorPost(contentLength);
                    }
                    default -> {
                        contenidoHTML = miGestor.getHTML_Error();
                        enviarRespuestaHTML(contenidoHTML);
                        System.out.println("-> Ups, ha ocurrido algo inesperado: ");
                    }
                }
            } else {
                contenidoHTML = miGestor.getHTML_Error();
                enviarRespuestaHTML(contenidoHTML);
                System.out.println("--> Ups.");
            }
        } catch (IOException ex) {
        }
    }

    private void atenderPorPost(int contentLength) {
        try {
            StringBuilder bodyBuilder = new StringBuilder();
            char[] buffer = new char[contentLength];
            int bytesRead = bufferedReader.read(buffer, 0, contentLength);
            bodyBuilder.append(buffer, 0, bytesRead);
            String body = bodyBuilder.toString();
            System.out.println("Cuerpo del mensaje POST:");
            System.out.println(body);
            if (body.startsWith("partidaSelect")) {
                procesarPeticion(body);
            } else {
                login(body);
            }

        } catch (IOException ex) {
            Logger.getLogger(AtenderCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void login(String body) throws NumberFormatException {
        String contenidoHTML;
        String[] partes = body.split("&");
        String nombre = "";
        String contraseña = "";
        for (String parte : partes) {
            String[] keyValue = parte.split("=");
            String key = keyValue[0];
            String value = keyValue[1];
            if (key.equals("nombre")) {
                nombre = value;
            } else if (key.equals("contrasena")) {
                contraseña = value;
            }
        }
        System.out.println("Nombre: " + nombre);
        System.out.println("Contraseña: " + contraseña);
        Datos.ConexionConBDD miCone = new ConexionConBDD();
        Datos.Usuario miUsuario = new Usuario();
        miUsuario.setNombre(nombre);
        miUsuario.setContraseña(contraseña);
        System.out.println("*"+miUsuario.hashCode());
        int id = miCone.consultarIDJugador(nombre, miUsuario.hashCode());
        for (int i = 0; i < listaNegra.size(); i++) {
            if ((id != -1) && (id != listaNegra.get(i))) {
                miCone.obtenerPartidasTerminadasPorJugador(id);
                contenidoHTML = miGestor.getHTML_Index(id);
                enviarRespuestaHTML(contenidoHTML);
            } else {
                contenidoHTML = miGestor.getHTML_Error();
                enviarRespuestaHTML(contenidoHTML);
            }

        }

    }

    private void procesarPeticion(String body) {
        String[] partes = body.split("=");
        int id = Integer.parseInt(partes[1]);
        System.out.println(body);
        String contenidoHTML = miGestor.getHTML_Tablero(id);
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
