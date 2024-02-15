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
import java.util.List;

/**
 *
 * @author DAM_M
 */
public class GestorDePaginas {

    private static GestorDePaginas miGestor = null;

    public static GestorDePaginas getMiGestor() {
        return miGestor;

    }

    public static void setMiGestor(GestorDePaginas aMiGestor) {
        miGestor = aMiGestor;
    }

    public static GestorDePaginas getCliente() {
        if (miGestor == null) {
            miGestor = new GestorDePaginas();
        }
        return miGestor;
    }

    public GestorDePaginas() {
    }

    public static String getHTML_Index(List<String> nuevasFilasHTML) {
        StringBuilder contenidoHTML = new StringBuilder();
        // Obtener el directorio actual
        String directorioActual = System.getProperty("user.dir");
        // Ruta relativa del archivo GestorDePaginas.html
        String rutaArchivo;
        rutaArchivo = directorioActual + "\\src\\main\\java\\HTML\\index.html";
        escribirIndex(rutaArchivo, nuevasFilasHTML, contenidoHTML);
        return contenidoHTML.toString();
    }

    public static String getHTML_Login() {
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

    public static String getHTML_Error() {
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

    private static void escribirIndex(String rutaArchivo, List<String> nuevasFilasHTML, StringBuilder contenidoHTML) {
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            boolean insertarFilas = false;
            while ((linea = reader.readLine()) != null) {
                AutoCompletar(linea, insertarFilas, nuevasFilasHTML, contenidoHTML);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void AutoCompletar(String linea, boolean insertarFilas, List<String> nuevasFilasHTML, StringBuilder contenidoHTML) {
        if (linea.contains("<!-- INSERTAR_NUEVAS_FILAS -->")) {
            insertarFilas = true;
        }
        if (insertarFilas) {
            for (String nuevaFilaHTML : nuevasFilasHTML) {
                contenidoHTML.append(nuevaFilaHTML).append("\n");
            }
            insertarFilas = false;
        }

        contenidoHTML.append(linea).append("\n");
    }
}
