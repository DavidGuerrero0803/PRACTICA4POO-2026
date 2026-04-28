package uabc.david.practica4poo2k26;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.animation.PauseTransition;
import static javafx.util.Duration.seconds;

public class ShobuMain extends Application {
    private Shobu juego;
    private Stage stagePrincipal;
    private boolean contraMaquina = false;
    private GridPane contenedorPrincipal;
    private Posicion inicioPasivo;
    private String tableroPasivoNombre;
    private int[] vectorActual;
    private Label turnoNegro;
    private Label turnoBlanco;
    private Label labelEstado;
    private HashMap<String, Button[][]> botonesGUI = new HashMap<>();

    @Override
    public void start(Stage stage) {
        this.stagePrincipal = stage;
        mostrarMenu();
    }

    private void mostrarMenu() {
        VBox menu = new VBox(30);
        menu.setAlignment(Pos.CENTER);
        menu.setStyle("-fx-background-color: #2c3e50; -fx-padding: 50;");

        Label titulo = new Label("Shobu");
        titulo.setFont(Font.font(100));
        titulo.setTextFill(Color.WHITE);

        Label nombre = new Label("Práctica #4 - Mario David Guerrero Márquez");
        nombre.setFont(Font.font("Arial", 15));
        nombre.setTextFill(Color.WHITE);

        String buttonStyle = "-fx-font-size: 18px; -fx-min-width: 250px; -fx-background-radius: 10;";

        Button buttonPvP = new Button("Jugador vs Jugador");
        buttonPvP.setStyle(buttonStyle);
        buttonPvP.setOnAction(e -> iniciarEscenaJuego(false));

        Button buttonPvE = new Button("Jugador vs Máquina");
        buttonPvE.setStyle(buttonStyle);
        buttonPvE.setOnAction(e -> iniciarEscenaJuego(true));

        menu.getChildren().addAll(titulo, buttonPvP, buttonPvE, nombre);
        Scene sceneMenu = new Scene(menu, 900, 850);
        stagePrincipal.setScene(sceneMenu);
        stagePrincipal.setTitle("Shobu");
        stagePrincipal.show();
    }

    private void iniciarEscenaJuego(boolean modoMaquina) {
        this.contraMaquina = modoMaquina;
        this.juego = new Shobu();
        this.botonesGUI.clear();

        VBox interfaz = new VBox(5);
        interfaz.setAlignment(Pos.CENTER);

        Label titulo = new Label("Shobu");
        titulo.setFont(Font.font("Arial", 80));
        titulo.setTextFill(Color.BLACK);

        turnoNegro = new Label("JUGADOR NEGRO");
        turnoBlanco = new Label("JUGADOR BLANCO");

        labelEstado = new Label("Inicia tu movimiento PASIVO");
        labelEstado.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #d35400;");

        turnoNegro.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        turnoBlanco.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        contenedorPrincipal = new GridPane();
        contenedorPrincipal.setAlignment(Pos.CENTER);
        contenedorPrincipal.setPadding(new Insets(20));
        contenedorPrincipal.setHgap(20);
        contenedorPrincipal.setVgap(20);

        contenedorPrincipal.add(crearVistaTablero("ARRIBA_IZQUIERDA"), 0, 0);
        contenedorPrincipal.add(crearVistaTablero("ARRIBA_DERECHA"), 1, 0);
        contenedorPrincipal.add(crearVistaTablero("ABAJO_IZQUIERDA"), 0, 1);
        contenedorPrincipal.add(crearVistaTablero("ABAJO_DERECHA"), 1, 1);

        interfaz.getChildren().addAll(titulo, turnoBlanco, contenedorPrincipal, labelEstado, turnoNegro);

        actualizarInterfaz();

        Scene sceneJuego = new Scene(interfaz, 900, 850);
        stagePrincipal.setScene(sceneJuego);
    }

    private void mostrarMensaje(String mensaje, boolean hayError) {
        labelEstado.setText(mensaje);
        if (hayError) {
            labelEstado.setTextFill(Color.RED);
        } else {
            labelEstado.setTextFill(Color.DARKGREEN);
        }
    }

    private GridPane crearVistaTablero(String nombreTablero) {
        GridPane grid = new GridPane();
        Tablero tablero = juego.getTablero(nombreTablero);

        Button[][] matrizBotones = new Button[4][4];
        botonesGUI.put(nombreTablero, matrizBotones);

        String colorTablero = tablero.getColor().equals("BLANCO") ? "#f0d9b5" : "#b58863";
        grid.setStyle("-fx-background-color: " + colorTablero + "; -fx-padding: 10;");

        for (int filas = 0; filas < 4; filas++) {
            for (int columnas = 0; columnas < 4; columnas++) {
                Button botonCasilla = new Button();

                botonCasilla.setMinSize(60, 60);
                botonCasilla.setPrefSize(60, 60);

                final int fila = filas;
                final int columna = columnas;
                botonCasilla.setOnAction(e -> manejarClic(nombreTablero, fila, columna));

                matrizBotones[filas][columnas] = botonCasilla;



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

        return pieza;
    }

    private void actualizarInterfaz() {
        for (String nombreTablero : botonesGUI.keySet()) {
            Tablero logicaTablero = juego.getTablero(nombreTablero);
            Button[][] matrizBotones = botonesGUI.get(nombreTablero);

            for (int fila = 0; fila < 4; fila++) {
                for (int columna = 0; columna < 4; columna++) {
                    String contenido = logicaTablero.getPosicion(new Posicion(fila, columna));
                    matrizBotones[fila][columna].setGraphic(crearPiezaVisual(contenido));
                }
            }
        }

        String turno = juego.getTurnoActual();

        if (turno.equals("B")) {
            turnoBlanco.setText("-> TU TURNO, JUGADOR BLANCO");
            turnoBlanco.setTextFill(Color.DARKBLUE);

            turnoNegro.setText("JUGADOR NEGRO");
            turnoNegro.setTextFill(Color.GRAY);
        } else {
            turnoNegro.setText("-> TU TURNO, JUGADOR NEGRO");
            turnoNegro.setTextFill(Color.DARKBLUE);

            turnoBlanco.setText("JUGADOR BLANCO");
            turnoBlanco.setTextFill(Color.GRAY);
        }

    }

    private void manejarClic(String nombreTablero, int fila, int columna) {
        Posicion clicPos = new Posicion(fila, columna);
        labelEstado.setTextFill(Color.DARKGRAY);
        Tablero tableroActual = juego.getTablero(nombreTablero);

        if (inicioPasivo == null) {
            if (esPiezaDelJugadorActual(tableroActual, clicPos)) {

                boolean esMiHome = false;
                String turno = juego.getTurnoActual();

                if (turno.equals("B") && nombreTablero.startsWith("ARRIBA")) {
                    esMiHome = true;
                } else if (turno.equals("N") && nombreTablero.startsWith("ABAJO")) {
                    esMiHome = true;
                }

                if (esMiHome) {
                    inicioPasivo = clicPos;
                    tableroPasivoNombre = nombreTablero;

                    ArrayList<Posicion> legales = juego.obtenerMovimientosLegales(tableroActual, inicioPasivo);
                    iluminarCasillas(nombreTablero, legales);
                    mostrarMensaje("Movimiento pasivo seleccionado. Elige la casilla.", false);
                } else {
                    mostrarMensaje("El movimiento PASIVO debe ser en tus tableros.", true);
                }
            } else {
                mostrarMensaje("Pieza incorrecta/Casilla vacía.", true);
            }
        }

        else if (vectorActual == null) {
            if (juego.esPasivoValido(tableroActual, inicioPasivo, clicPos) && nombreTablero.equals(tableroPasivoNombre)) {
                vectorActual = juego.obtenerVectorMovimiento(inicioPasivo, clicPos);
                mostrarMensaje("PASIVO fijado. Ahora haz el movimiento AGRESIVO.", false);
                prepararSeleccionAgresiva();
            } else {
                mostrarMensaje("Movimiento PASIVO no válido. Intenta en otra casilla.", true);
                resetearSeleccion();
            }
        }

        else {
            Tablero tableroPasivo = juego.getTablero(tableroPasivoNombre);
            String colorPasivo = tableroPasivo.getColor();
            String colorAgresivo = tableroActual.getColor();

            if (!colorPasivo.equals(colorAgresivo)) {
                if (juego.esAgresivoValido(tableroActual, clicPos, vectorActual)) {
                    ejecutarTurnoCompleto(clicPos, nombreTablero);

                } else {
                    mostrarMensaje("Movimiento NO permitido (Obstruido o fuera de límites).", true);
                }
            } else {
                String colorNecesario = (colorPasivo.equals("blanco") ? "negro" : "blanco");
                mostrarMensaje("Debes atacar en un tablero de color " + colorNecesario, true);
            }
        }
    }

    private boolean esPiezaDelJugadorActual(Tablero tablero, Posicion pos) {
        String contenido = tablero.getPosicion(pos);
        return contenido.equals(juego.getTurnoActual());
    }

    private void iluminarCasillas(String nombreTablero, ArrayList<Posicion> destinos) {
        Button[][] matriz = botonesGUI.get(nombreTablero);
        for (Posicion pos : destinos) {

            matriz[pos.getFila()][pos.getColumna()].setStyle(
                    "-fx-border-color: #00ff00; -fx-border-width: 3; -fx-border-radius: 5;"
            );
        }
    }

    private void prepararSeleccionAgresiva() {
        mostrarMensaje("Movimiento PASIVO fijado. Ahora elige el movimiento AGRESIVO.", false);
    }

    private void resetearSeleccion() {
        inicioPasivo = null;
        tableroPasivoNombre = null;
        vectorActual = null;

        for (Button[][] matriz : botonesGUI.values()) {
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

        resetearSeleccion();
        actualizarInterfaz();

        String ganador = juego.verificarGanador();

        if (ganador != null) {
            String colorGanador = ganador.equals("N") ? "NEGRO" : "BLANCO";
            labelEstado.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
            mostrarMensaje("HA GANADO EL JUGADOR " + colorGanador, false);

            contenedorPrincipal.setDisable(true);
        } else {
            juego.cambiarTurno();
            resetearSeleccion();
            actualizarInterfaz();
            mostrarMensaje("Turno del Jugador " + (juego.getTurnoActual().equals("N") ? "NEGRO" : "BLANCO"), false);

            if (contraMaquina && juego.getTurnoActual().equals("B")) {
                PauseTransition delay = new PauseTransition(seconds(1.2));
                delay.setOnFinished(e -> ejecutarTurnoMaquina());
                delay.play();
            }
        }
    }

    private void ejecutarTurnoMaquina() {
        if (juego.verificarGanador() != null) {
            return;
        }

        String[] tablerosHomeMaquina = {"ARRIBA_IZQUIERDA", "ARRIBA_DERECHA"};

        for (String nombreTableroPasivo : tablerosHomeMaquina) {
            Tablero tableroPasivo = juego.getTablero(nombreTableroPasivo);

            for (int filaPasiva = 0; filaPasiva < 4; filaPasiva++) {
                for (int columnaPasiva = 0; columnaPasiva < 4; columnaPasiva++) {
                    Posicion posicionPasiva = new Posicion(filaPasiva, columnaPasiva);

                    if (tableroPasivo.getPosicion(posicionPasiva).equals("B")) {
                        ArrayList<Posicion> caminosPasivos = juego.obtenerMovimientosLegales(tableroPasivo, posicionPasiva);

                        for (Posicion posCaminoPasivo : caminosPasivos) {
                            int[] vector = juego.obtenerVectorMovimiento(posicionPasiva, posCaminoPasivo);

                            String colorOpuesto = tableroPasivo.getColor().equals("BLANCO") ? "NEGRO" : "BLANCO";

                            String[] todosLosTableros = {"ARRIBA_IZQUIERDA", "ARRIBA_DERECHA", "ABAJO_IZQUIERDA", "ABAJO_DERECHA"};

                            for (String nombreTableroAgresivo : todosLosTableros) {
                                Tablero tableroAgresivo = juego.getTablero(nombreTableroAgresivo);

                                if (tableroAgresivo.getColor().equals(colorOpuesto)) {

                                    for (int filaAgresiva = 0; filaAgresiva < 4; filaAgresiva++) {
                                        for (int columnaAgresiva = 0; columnaAgresiva < 4; columnaAgresiva++) {
                                            Posicion posicionAgresiva = new Posicion(filaAgresiva, columnaAgresiva);
                                            if (tableroAgresivo.getPosicion(posicionAgresiva).equals("B") &&
                                                    juego.esAgresivoValido(tableroAgresivo, posicionAgresiva, vector)) {

                                                this.inicioPasivo = posicionPasiva;
                                                this.tableroPasivoNombre = nombreTableroPasivo;
                                                this.vectorActual = vector;
                                                ejecutarTurnoCompleto(posicionAgresiva, nombreTableroAgresivo);
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}