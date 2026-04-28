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
    private Label estadoJuego;

    private ArrayList<Posicion> destinosLegalesActuales = new ArrayList<>();
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

        turnoNegro = new Label("JUGADOR NEGRO");
        turnoBlanco = new Label("JUGADOR BLANCO");

        estadoJuego = new Label("Inicia tu movimiento PASIVO");
        estadoJuego.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #d35400;");

        turnoNegro.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        turnoBlanco.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        contenedorPrincipal = crearTableros();

        interfaz.getChildren().addAll(titulo, turnoBlanco, contenedorPrincipal, estadoJuego, turnoNegro);

        actualizarInterfaz();

        Scene sceneJuego = new Scene(interfaz, 900, 850);
        stagePrincipal.setScene(sceneJuego);
    }

    private GridPane crearTableros() {
        GridPane cuadricula = new GridPane();
        cuadricula.setAlignment(Pos.CENTER);
        cuadricula.setPadding(new Insets(20));
        cuadricula.setHgap(20);
        cuadricula.setVgap(20);

        cuadricula.add(crearVistaTablero(Shobu.ARRIBA_IZQUIERDA), 0, 0);
        cuadricula.add(crearVistaTablero(Shobu.ARRIBA_DERECHA), 1, 0);
        cuadricula.add(crearVistaTablero(Shobu.ABAJO_IZQUIERDA), 0, 1);
        cuadricula.add(crearVistaTablero(Shobu.ABAJO_DERECHA), 1, 1);

        return cuadricula;
    }

    private GridPane crearVistaTablero(String nombreTablero) {
        GridPane cuadricula = new GridPane();
        Tablero tablero = juego.getTablero(nombreTablero);

        String colorTablero = tablero.getColor().equals(Tablero.COLOR_BLANCO) ? "#f0d9b5" : "#b58863";
        cuadricula.setStyle("-fx-background-color: " + colorTablero + "; -fx-padding: 10;");

        Button[][] matrizBotones = new Button[Tablero.TAMAÑO][Tablero.TAMAÑO];
        botonesGUI.put(nombreTablero, matrizBotones);

        for (int fila = 0; fila < Tablero.TAMAÑO; fila++) {
            for (int columna = 0; columna < Tablero.TAMAÑO; columna++) {
                Button botonCasilla = new Button();

                botonCasilla.setPrefSize(60, 60);

                final int filas = fila;
                final int columnas = columna;
                botonCasilla.setOnAction(e -> manejarClic(nombreTablero, filas, columnas));

                matrizBotones[fila][columna] = botonCasilla;
                cuadricula.add(botonCasilla, columna, fila);
            }
        }

        return cuadricula;
    }

    private Circle crearPiedra(String tipo) {
        if (tipo.equals(Tablero.VACÍA)) {
            return null;
        }

        Circle piedra = new Circle(20);

        if (tipo.equals(Tablero.NEGRA)) {
            piedra.setFill(Color.BLACK);
        } else {
            piedra.setFill(Color.WHITE);
            piedra.setStroke(Color.GRAY);
        }

        return piedra;
    }

    private void actualizarInterfaz() {
        actualizarTableros();
        actualizarTurnos();
    }

    private void actualizarTableros() {
        for (String nombreTablero : botonesGUI.keySet()) {
            Tablero tablero = juego.getTablero(nombreTablero);
            Button[][] matriz = botonesGUI.get(nombreTablero);

            for (int fila = 0; fila < Tablero.TAMAÑO; fila++) {
                for (int columna = 0; columna < Tablero.TAMAÑO; columna++) {
                    String contenido = tablero.getPosicion(new Posicion(fila, columna));
                    matriz[fila][columna].setGraphic(crearPiedra(contenido));
                }
            }
        }
    }

    private void actualizarTurnos() {
        String turno = juego.getTurnoActual();

        if (turno.equals(Tablero.BLANCA)) {
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
        Tablero tableroActual = juego.getTablero(nombreTablero);

        if (inicioPasivo == null) {
            intentarSeleccionarPasivo(nombreTablero, clicPos, tableroActual);
        } else if (vectorActual == null) {
            intentarConfirmarPasivo(nombreTablero, clicPos);
        } else {
            intentarEjecutarAgresivo(nombreTablero, clicPos, tableroActual);
        }
    }

    private void intentarSeleccionarPasivo(String nombreTablero, Posicion clicPos, Tablero tableroActual) {
        if (!esPiedraDeJugadorActual(tableroActual, clicPos)) {
            mostrarMensaje("Pieza incorrecta o casilla vacía.", true);
            return;
        }

        if (!esTableroHomeDelJugador(nombreTablero)) {
            mostrarMensaje("El movimiento PASIVO debe ser en tus tableros.", true);
            return;
        }

        inicioPasivo = clicPos;
        tableroPasivoNombre = nombreTablero;

        ArrayList<Posicion> legales = juego.obtenerMovimientosLegales(tableroActual, inicioPasivo);
        destinosLegalesActuales = legales;
        iluminarCasillas(nombreTablero, legales);
        mostrarMensaje("Movimiento PASIVO seleccionado. Elige la casilla.", false);
    }

    private void intentarConfirmarPasivo(String nombreTablero, Posicion clicPos) {
        if (!nombreTablero.equals(tableroPasivoNombre) ||
                !destinosLegalesActuales.contains(clicPos)) {
            mostrarMensaje("Movimiento PASIVO no válido. Elige una casilla válida.", true);
            resetearSeleccion();
            return;
        }

        vectorActual = juego.calcularVector(inicioPasivo, clicPos);
        mostrarMensaje("PASIVO fijado. Ahora haz el movimiento AGRESIVO.", false);
    }

    private void intentarEjecutarAgresivo(String nombreTablero, Posicion clicPos, Tablero tableroActual) {
        Tablero tableroPasivo = juego.getTablero(tableroPasivoNombre);

        if (tableroPasivo.getColor().equals(tableroActual.getColor())) {
            String colorNecesario = tableroPasivo.getColorOpuesto().toLowerCase();
            mostrarMensaje("Debes atacar en un tablero de color " + colorNecesario + ".", true);
            return;
        }

        if (!esPiedraDeJugadorActual(tableroActual, clicPos)) {
            mostrarMensaje("Debes seleccionar una de tus piezas para el movimiento AGRESIVO.", true);
            return;
        }

        if (!juego.esAgresivoValido(tableroActual, clicPos, vectorActual)) {
            mostrarMensaje("Movimiento no permitido (obstruido o fuera de rango).", true);
            return;
        }

        ejecutarTurnoCompleto(clicPos, nombreTablero);
    }

    private void ejecutarTurnoCompleto(Posicion inicioAgresivo, String nombreTabAgresivo) {
        juego.moverPieza(tableroPasivoNombre, inicioPasivo, vectorActual);
        juego.moverPieza(nombreTabAgresivo, inicioAgresivo, vectorActual);

        resetearSeleccion();
        actualizarInterfaz();

        String ganador = juego.verificarGanador();

        if (ganador != null) {
            mostrarVictoria(ganador);
        } else {
            juego.cambiarTurno();
            actualizarInterfaz();
            mostrarMensaje("Turno del Jugador " + (juego.getTurnoActual().
                    equals(Tablero.NEGRA) ? Tablero.COLOR_NEGRO : Tablero.COLOR_BLANCO), false);

            if (contraMaquina && juego.getTurnoActual().equals(Tablero.BLANCA)) {
                PauseTransition delay = new PauseTransition(seconds(1.2));
                delay.setOnFinished(e -> ejecutarTurnoMaquina());
                delay.play();
            }
        }
    }

    private void mostrarVictoria(String ganador) {
        String colorGanador = ganador.equals(Tablero.NEGRA) ? Tablero.COLOR_NEGRO : Tablero.COLOR_BLANCO;
        estadoJuego.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        mostrarMensaje("HA GANADO EL JUGADOR " + colorGanador, false);
        contenedorPrincipal.setDisable(true);
    }

    private void ejecutarTurnoMaquina() {
        if (juego.verificarGanador() != null) {
            return;
        }

        String[] tablerosHomeMaquina = { Shobu.ARRIBA_IZQUIERDA, Shobu.ARRIBA_DERECHA };

        for (String nombreTabPasivo : tablerosHomeMaquina) {
            Tablero tabPasivo = juego.getTablero(nombreTabPasivo);

            for (int filaPasiva = 0; filaPasiva < Tablero.TAMAÑO; filaPasiva++) {
                for (int columnaPasiva = 0; columnaPasiva < Tablero.TAMAÑO; columnaPasiva++) {
                    Posicion posPasiva = new Posicion(filaPasiva, columnaPasiva);
                    if (!tabPasivo.getPosicion(posPasiva).equals(Tablero.BLANCA)) {
                        continue;
                    }

                    if (intentarJugadaMaquina(nombreTabPasivo, tabPasivo, posPasiva)) {
                        return;
                    }
                }
            }
        }
    }

    private boolean intentarJugadaMaquina(String nombreTabPasivo, Tablero tabPasivo, Posicion posPasiva) {
        String colorOpuesto = tabPasivo.getColorOpuesto();

        for (Posicion destPasivo : juego.obtenerMovimientosLegales(tabPasivo, posPasiva)) {
            int[] vector = juego.calcularVector(posPasiva, destPasivo);

            for (String nombreTabAgresivo : Shobu.POSICIONES_TABLERO) {
                Tablero tabAgresivo = juego.getTablero(nombreTabAgresivo);
                if (!tabAgresivo.getColor().equals(colorOpuesto)) continue;

                for (int filaAgresiva = 0; filaAgresiva < Tablero.TAMAÑO; filaAgresiva++) {
                    for (int columnaAgresiva = 0; columnaAgresiva < Tablero.TAMAÑO; columnaAgresiva++) {
                        Posicion posAgresiva = new Posicion(filaAgresiva, columnaAgresiva);

                        if (tabAgresivo.getPosicion(posAgresiva).equals(Tablero.BLANCA) &&
                                juego.esAgresivoValido(tabAgresivo, posAgresiva, vector)) {

                            this.inicioPasivo = posPasiva;
                            this.tableroPasivoNombre = nombreTabPasivo;
                            this.vectorActual = vector;
                            ejecutarTurnoCompleto(posAgresiva, nombreTabAgresivo);
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean esPiedraDeJugadorActual(Tablero tablero, Posicion pos) {
        String contenido = tablero.getPosicion(pos);
        return contenido.equals(juego.getTurnoActual());
    }

    private boolean esTableroHomeDelJugador(String nombreTablero) {
        String turno = juego.getTurnoActual();
        return (turno.equals(Tablero.BLANCA) && nombreTablero.startsWith("ARRIBA")) ||
                (turno.equals(Tablero.NEGRA)  && nombreTablero.startsWith("ABAJO"));
    }

    private void iluminarCasillas(String nombreTablero, ArrayList<Posicion> destinos) {
        Button[][] matriz = botonesGUI.get(nombreTablero);
        for (Posicion pos : destinos) {
            matriz[pos.getFila()][pos.getColumna()].setStyle(
                    "-fx-border-color: #00ff00; -fx-border-width: 3; -fx-border-radius: 5;"
            );
        }
    }

    private void resetearSeleccion() {
        inicioPasivo = null;
        tableroPasivoNombre = null;
        vectorActual = null;
        destinosLegalesActuales.clear();

        for (Button[][] matriz : botonesGUI.values()) {
            for (int fila = 0; fila < Tablero.TAMAÑO; fila++) {
                for (int columna = 0; columna < Tablero.TAMAÑO; columna++) {
                    matriz[fila][columna].setStyle("-fx-border-color: transparent;");
                }
            }
        }
    }

    private void mostrarMensaje(String mensaje, boolean hayError) {
        estadoJuego.setText(mensaje);
        if (hayError) {
            estadoJuego.setTextFill(Color.RED);
        } else {
            estadoJuego.setTextFill(Color.DARKGREEN);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}