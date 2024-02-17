/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gestor;

import Datos.ConexionConBDD;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author DAM_M
 */
public class GestorDePaginas {

    private static GestorDePaginas miGestor = null;
    private final String[][] tablero = new String[20][20];
    private List<String> nuevasFilasHTML = null;
    private List<String> nuevasOpcionHTML = null;

    public static GestorDePaginas getGestor() {
        if (miGestor == null) {
            miGestor = new GestorDePaginas();
        }
        return miGestor;
    }

    private GestorDePaginas() {
    }

    public String getHTML_Index(int id) {
        StringBuilder contenidoHTML = new StringBuilder();
        // Obtener el directorio actual
        String directorioActual = System.getProperty("user.dir");
        // Ruta relativa del archivo GestorDePaginas.html
        String rutaArchivo;
        rutaArchivo = directorioActual + "\\src\\main\\java\\HTML\\index.html";
        crearHTML_TablaPartidas(id);
        escribirIndex(rutaArchivo, contenidoHTML);
        return contenidoHTML.toString();
    }

    public String getHTML_Login() {
        StringBuilder contenidoHTML = new StringBuilder();
        String directorioActual = System.getProperty("user.dir");
        String rutaArchivo;
        rutaArchivo = directorioActual + "\\src\\main\\java\\HTML\\Login.html";
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenidoHTML.append(linea).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contenidoHTML.toString();
    }

    public String getHTML_Error() {
        StringBuilder contenidoHTML = new StringBuilder();
        String directorioActual = System.getProperty("user.dir");
        String rutaArchivo;
        rutaArchivo = directorioActual + "\\src\\main\\java\\HTML\\PaginaError.html";
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenidoHTML.append(linea).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contenidoHTML.toString();
    }

    public String getHTML_Tablero(int id) {
        StringBuilder contenidoHTML = new StringBuilder();
        String directorioActual = System.getProperty("user.dir");
        String rutaArchivo;
        rutaArchivo = directorioActual + "\\src\\main\\java\\HTML\\Tableros.html";
        escribirTablero(rutaArchivo, contenidoHTML, id);
        return contenidoHTML.toString();
    }

    private void crearHTML_TablaPartidas(int id) {
        Datos.ConexionConBDD misDatos = new ConexionConBDD();
        HashMap<Integer, String> mapaPartidasTerminadas;

        mapaPartidasTerminadas = misDatos.obtenerPartidasTerminadasPorJugador(id);
        nuevasFilasHTML = new ArrayList<>();
        nuevasOpcionHTML = new ArrayList<>();
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
            // Crear la opción HTML para el formulario de selección
            String opcionHTML = String.format("<option value=%d>Partida %d</option>",
                    idPartida, idPartida);

            // Agregar la fila HTML a la lista de nuevas filas
            nuevasFilasHTML.add(filaHTML);
            // Agregar la opción HTML a la lista de nuevas opciones
            nuevasOpcionHTML.add(opcionHTML);
        }
    }

    private void escribirTablero(String rutaArchivo, StringBuilder contenidoHTML, int id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            Datos.ConexionConBDD miCone = new ConexionConBDD();
            String jugador = miCone.obtenerPartidas(id);
            String[] jugadores = jugador.split(";");
            String jugador1 = jugadores[0];
            String jugador2 = jugadores[1];
            while ((linea = reader.readLine()) != null) {
                setTitulos(linea, contenidoHTML, id, miCone, jugadores);

                ArrayList<String> disparos = miCone.obtenerDisparosDePartida(id);
                ArrayList<String> barcos = miCone.consultarBarcosEnPartida(id);

                if (linea.contains("<!-- INSERTAR_Tablero1 -->")) {
                    tableroVacio();
                    pintarBarcos(barcos, jugador1);
                    disparos(disparos, jugador2);

                    for (int i = 0; i < 10; i++) {
                        for (int j = 0; j < 10; j++) {
                            contenidoHTML.append(tablero[i][j]);
                        }
                    }
                } else if (linea.contains("<!-- INSERTAR_Tablero2 -->")) {
                    tableroVacio();
                    pintarBarcos(barcos, jugador2);
                    disparos(disparos, jugador1);
                    for (int i = 0; i < 10; i++) {
                        for (int j = 0; j < 10; j++) {
                            contenidoHTML.append(tablero[i][j]);
                        }
                    }
                }

                // Añadir la línea actual al contenido HTML
                contenidoHTML.append(linea).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tableroVacio() {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                tablero[i][j] = "<div class=\"casilla\"></div>";
            }
        }
    }

    private void disparos(ArrayList<String> disparos, String jugador1) throws NumberFormatException {
        for (int row = 1; row <= 20; row++) {
            for (int col = 1; col <= 20; col++) {
                for (String disparo : disparos) {
                    String[] partes = disparo.split("-");
                    String jugadorId = partes[0];
                    int posicionX = Integer.parseInt(partes[1]);
                    int posicionY = Integer.parseInt(partes[2]);

                    if (jugador1.equals(jugadorId) && posicionX == row && posicionY == col) {
                        String mensajeResultado = partes[3];
                        if (mensajeResultado.equals("A")) {
                            tablero[row][col] = "<div class=\"casilla\" style=\"background-color: blue;\">A " + posicionX + "-" + posicionY + "</div>";
                        } else if (mensajeResultado.equals("T")) {
                            tablero[row][col] = "<div class=\"casilla\" style=\"background-color: red;\">T " + posicionX + "-" + posicionY + "</div>";
                        } else if (mensajeResultado.equals("H")) {
                            tablero[row][col] = "<div class=\"casilla\" style=\"background-color: black;\">H " + posicionX + "-" + posicionY + "</div>";
                        }
                        break;
                    }
                }
            }
        }
    }

    private void pintarBarcos(ArrayList<String> barcos, String jugador2) throws NumberFormatException {
        for (int row = 1; row <= 20; row++) {
            for (int col = 1; col <= 20; col++) {

                for (String barco : barcos) {
                    String[] partes = barco.split("-");
                    String jugadorId = partes[0];
                    int tamaño = Integer.parseInt(partes[1]);
                    int posicionX = Integer.parseInt(partes[2]);
                    int posicionY = Integer.parseInt(partes[3]);
                    String orientacion = partes[4];

                    if (jugador2.equals(jugadorId)) {
                        if (orientacion.equals("H")) {
                            if (posicionX == row && col >= posicionY && col < posicionY + tamaño) {
                                tablero[row][col] = "<div class=\"casilla\" style=\"background-color: green;\">B " + posicionX + "-" + posicionY + "</div>";
                                break;
                            }
                        } else if (orientacion.equals("V")) {
                            if (posicionY == col && row >= posicionX && row < posicionX + tamaño) {
                                tablero[row][col] = "<div class=\"casilla\" style=\"background-color: green;\">B " + posicionX + "-" + posicionY + "</div>";
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void setTitulos(String linea, StringBuilder contenidoHTML, int id, ConexionConBDD miCone, String[] jugadores) {

        if (linea.contains("<!-- H1 -->")) {
            contenidoHTML.append("<h1>Tablas " + id + " Ganador: " + miCone.obtenerGanadorDePartida(id) + "</h1>").append("\n");
        }
        int j1 = Integer.parseInt(jugadores[0]);
        int j2 = Integer.parseInt(jugadores[1]);
        if (linea.contains("<!-- H2 -->")) {
            contenidoHTML.append("<h2>Jugador " + miCone.obtenerNombreJugadorPorID(j1) + "</h2>").append("\n");
        }
        if (linea.contains("<!-- H2.2 -->")) {
            contenidoHTML.append("<h2>Jugador " + miCone.obtenerNombreJugadorPorID(j2) + "</h2>").append("\n");
        }
    }

    private void escribirIndex(String rutaArchivo, StringBuilder contenidoHTML) {
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                AutoCompletar(linea, contenidoHTML);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void AutoCompletar(String linea, StringBuilder contenidoHTML) {
        boolean insertarFilas = false;
        // Activar el marcador de inserción cuando encuentres el marcador correspondiente
        if (linea.contains("<!-- INSERTAR_NUEVAS_FILAS -->")) {
            insertarFilas = true;
        }
        // Insertar las nuevas filas HTML si el marcador de inserción está activo
        if (insertarFilas) {
            for (String nuevaFilaHTML : nuevasFilasHTML) {
                contenidoHTML.append(nuevaFilaHTML).append("\n");
            }
            insertarFilas = false; // Desactivar el marcador después de insertar las nuevas filas
        }
        // Activar el marcador de inserción cuando encuentres el marcador correspondiente para las opciones de las partidas
        if (linea.contains("<!-- OPTIONES_DE_PARTIDAS -->")) {
            insertarFilas = true;
        }
        // Insertar las nuevas opciones HTML si el marcador de inserción está activo
        if (insertarFilas) {
            for (String nuevaOpcionHTML : nuevasOpcionHTML) {
                contenidoHTML.append(nuevaOpcionHTML).append("\n");
            }
            insertarFilas = false; // Desactivar el marcador después de insertar las nuevas opciones
        }
        // Añadir la línea actual al contenido HTML
        contenidoHTML.append(linea).append("\n");
    }
}
