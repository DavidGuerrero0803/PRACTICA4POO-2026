package uabc.david.practica4poo2k26;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class ShobuMain extends Application {
    private Shobu juego;
    private GridPane contenedorPrincipal;
    private Posicion inicioPasivo;
    private String tableroPasivoNombre;
    private int[] vectorActual;

    @Override
    public void start(Stage stage) {
        juego = new Shobu();
        contenedorPrincipal = new GridPane();
        contenedorPrincipal.setHgap(20);
        contenedorPrincipal.setVgap(20);

        contenedorPrincipal.add(crearVistaTablero("ARRIBA_IZQUIERDA"), 0, 0);
        contenedorPrincipal.add(crearVistaTablero("ARRIBA_DERECHA"), 1, 0);
        contenedorPrincipal.add(crearVistaTablero("ABAJO_IZQUIERDA"), 0, 1);
        contenedorPrincipal.add(crearVistaTablero("ABAJO_DERECHA"), 1, 1);

        Scene scene = new Scene(contenedorPrincipal, 800, 800);
        stage.setTitle("Shobu");
        stage.setScene(scene);
        stage.show();
    }

    private GridPane crearVistaTablero(String nombreTablero) {
        GridPane grid = new GridPane();

        Tablero tablero = juego.getTablero(nombreTablero);

        String colorTablero = tablero.getColor().equals("BLANCO") ? "#f0d9b5" : "#b58863";
        grid.setStyle("-fx-background-color: " + colorTablero + "; -fx-padding: 10;");

        for (int filas = 0; filas < 4; filas++) {
            for (int columnas = 0; columnas < 4; columnas++) {
                Button botonCasilla = new Button();
                botonCasilla.setPrefSize(60, 60);

                final int fila = filas;
                final int columna = columnas;
                botonCasilla.setOnAction(e -> manejarClic(nombreTablero, fila, columna));

                grid.add(botonCasilla, columnas, filas);
            }
        }
        return grid;
    }

    private Circle crearPiezaVisual(String tipo) {
        if (tipo.equals("V")) return null;

        Circle pieza = new Circle(20);

        if (tipo.equals("N")) {
            pieza.setFill(Color.BLACK);
        } else {
            pieza.setFill(Color.WHITE);
            pieza.setStroke(Color.GRAY);
        }

        pieza.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.5)));

        return pieza;
    }

    private HashMap<String, Button[][]> botonesGui = new HashMap<>();

    private void actualizarInterfaz() {
        for (String nombreTablero : botonesGui.keySet()) {
            Tablero logicaTablero = juego.getTablero(nombreTablero);
            Button[][] matrizBotones = botonesGui.get(nombreTablero);

            for (int fila = 0; fila < 4; fila++) {
                for (int columna = 0; columna < 4; columna++) {
                    String contenido = logicaTablero.getPosicion(new Posicion(fila, columna));
                    matrizBotones[fila][columna].setGraphic(crearPiezaVisual(contenido));
                }
            }
        }
    }

    private void manejarClic(String nombreTablero, int f, int c) {
        Posicion clicPos = new Posicion(f, c);
        Tablero tableroActual = juego.getTablero(nombreTablero);

        if (inicioPasivo == null) {
            if (esPiezaDelJugadorActual(tableroActual, clicPos)) {
                inicioPasivo = clicPos;
                tableroPasivoNombre = nombreTablero;

                ArrayList<Posicion> legales = juego.obtenerMovimientosLegales(tableroActual, inicioPasivo);
                iluminarCasillas(nombreTablero, legales);
            }
        }

        else if (vectorActual == null) {
            if (juego.esPasivoValido(tableroActual, inicioPasivo, clicPos) && nombreTablero.equals(tableroPasivoNombre)) {
                vectorActual = juego.obtenerVectorMovimiento(inicioPasivo, clicPos);

                prepararSeleccionAgresiva();
            } else {
                resetearSeleccion();
            }
        }

        else {
            if (juego.esAgresivoValido(tableroActual, clicPos, vectorActual)) {

                ejecutarTurnoCompleto(clicPos, nombreTablero);
                actualizarInterfaz();
                resetearSeleccion();
            }
        }
    }

    private boolean esPiezaDelJugadorActual(Tablero tablero, Posicion pos) {
        String contenido = tablero.getPosicion(pos);
        return contenido.equals(juego.getTurnoActual());
    }

    private void iluminarCasillas(String nombreTablero, ArrayList<Posicion> destinos) {
        Button[][] matriz = botonesGui.get(nombreTablero);
        for (Posicion pos : destinos) {

            matriz[pos.getFila()][pos.getColumna()].setStyle(
                    "-fx-border-color: #00ff00; -fx-border-width: 3; -fx-border-radius: 5;"
            );
        }
    }

    private void prepararSeleccionAgresiva() {
        System.out.println("Movimiento pasivo fijado ");
    }

    private void resetearSeleccion() {
        inicioPasivo = null;
        tableroPasivoNombre = null;
        vectorActual = null;

        for (Button[][] matriz : botonesGui.values()) {
            for (int fila = 0; fila < 4; fila++) {
                for (int columna = 0; columna < 4; columna++) {
                    matriz[fila][columna].setStyle("-fx-border-color: transparent;");
                }
            }
        }
    }

    private void ejecutarTurnoCompleto(Posicion inicioAgresivo, String nombreTabAgresivo) {
        juego.moverPieza(tableroPasivoNombre, inicioPasivo, vectorActual);

        juego.moverPieza(nombreTabAgresivo, inicioAgresivo, vectorActual);

        juego.cambiarTurno();

        actualizarInterfaz();
        resetearSeleccion();
    }

    public static void main(String[] args) {
        launch(args);
    }
}