package uabc.david.practica4poo2k26;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.ArrayList;

import static javafx.util.Duration.seconds;

public class VistaTablero {
    private Shobu shobu;
    private VBox contenedor;
    private Label turno;
    private Label estado;
    private boolean juegoTerminado;
    private ArrayList<GridPane> cuadriculas;

    private Piedra piedraSeleccionada;
    private int indiceTableroSeleccionado;
    private ArrayList<Posicion> posicionesResaltadas;

    public VistaTablero(Shobu shobu) {
        this.shobu = shobu;
        this.cuadriculas = new ArrayList<>();
        this.posicionesResaltadas = new ArrayList<>();
        this.piedraSeleccionada = null;
        this.indiceTableroSeleccionado = -1;
        this.juegoTerminado = false;
        construirPanel();
    }

    private void construirPanel() {
        turno = new Label();
        estado = new Label();
        turno.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        estado.setStyle("-fx-font-size: 13px;");

        HBox filaSuperior = new HBox(20);
        filaSuperior.setAlignment(Pos.CENTER);
        HBox filaInferior = new HBox(20);
        filaInferior.setAlignment(Pos.CENTER);

        for (int i = 0; i < 4; i++) {
            GridPane cuadricula = crearGridPane(i);
            cuadriculas.add(cuadricula);
            if (i < 2) {
                filaSuperior.getChildren().add(cuadricula);
            } else {
                filaInferior.getChildren().add(cuadricula);
            }
            
        }

        contenedor = new VBox(20, turno, estado, filaSuperior, filaInferior);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(10));

        actualizarPanel();
    }

    private GridPane crearGridPane(int indiceTablero) {
        GridPane cuadricula = new GridPane();
        cuadricula.setHgap(4);
        cuadricula.setVgap(4);
        cuadricula.setPadding(new Insets(8));

        String colorTablero = shobu.getTableros().get(indiceTablero).getColor();
        String estiloFondo = colorTablero.equals("negro")
                ? "-fx-background-color: #b58863; -fx-border-color: #3a2a1a; -fx-border-width: 3;"
                : "-fx-background-color: #f0d9b5; -fx-border-color: #8b6914; -fx-border-width: 3;";
        cuadricula.setStyle(estiloFondo);

        for (int fila = 0; fila < 4; fila++) {
            for (int columna = 0; columna < 4; columna++) {
                Button casilla = new Button();
                casilla.setPrefSize(65, 65);

                final int FILA = fila;
                final int COLUMNA = columna;
                casilla.setOnAction(e -> manejarClicCasilla(indiceTablero, FILA, COLUMNA));
                cuadricula.add(casilla, columna, fila);
            }
        }
        return cuadricula;
    }

    private void manejarClicCasilla(int indiceTablero, int fila, int columna) {
        if (juegoTerminado) {
            return;
        }

        Posicion posicionClicada = new Posicion(fila, columna);
        Tablero tablero = shobu.getTableros().get(indiceTablero);

        Piedra piedraEnCasilla = tablero.getPiedraEnPosicion(posicionClicada);

        if (!shobu.pasivoRealizado()) {
            manejarFasePasiva(indiceTablero, posicionClicada, piedraEnCasilla, tablero);
        } else {
            manejarFaseAgresiva(indiceTablero, posicionClicada, piedraEnCasilla);
        }
    }

    private void manejarFasePasiva(int indiceTablero, Posicion pos, Piedra piedra, Tablero tablero) {
        if (piedraSeleccionada == null) {
            if (piedra != null &&
                    piedra.getPropietario() == shobu.getJugadorActual().getIdentificador() &&
                    tablero.getPropietario() == shobu.getJugadorActual().getIdentificador()) {

                piedraSeleccionada = piedra;
                indiceTableroSeleccionado = indiceTablero;
                posicionesResaltadas = shobu.obtenerMovimientosPasivosValidos(pos, indiceTablero);
            }
        } else {
            if (posicionesResaltadas.contains(pos)) {
                shobu.realizarMovimientoPasivo(new Movimiento(indiceTableroSeleccionado, piedraSeleccionada.getPosicion(), pos, true));
                resetearSeleccion();
            } else {
                resetearSeleccion();
            }
        }
        actualizarPanel();
    }

    private void manejarFaseAgresiva(int indiceTablero, Posicion pos, Piedra piedra) {
        if (piedraSeleccionada == null) {
            if (piedra != null && piedra.getPropietario() == shobu.getJugadorActual().getIdentificador()) {
                ArrayList<Posicion> validos = shobu.obtenerMovimientosAgresivosValidos(pos, indiceTablero);
                if (!validos.isEmpty()) {
                    piedraSeleccionada = piedra;
                    indiceTableroSeleccionado = indiceTablero;
                    posicionesResaltadas = validos;
                }
            }
        } else {
            if (posicionesResaltadas.contains(pos)) {
                shobu.realizarMovimientoAgresivo(new Movimiento(indiceTableroSeleccionado, piedraSeleccionada.getPosicion(), pos, false));
                resetearSeleccion();

                if (verificarVictoria()) {
                    return;
                }

                shobu.finalizarTurno();
                if (shobu.getJugadorActual().esMaquina()) {
                    PauseTransition pausa = new PauseTransition(seconds(1.2));

                    pausa.setOnFinished(evento -> {
                        shobu.realizarMovimientoMaquina();

                        if (verificarVictoria()) return;

                        shobu.finalizarTurno();
                        actualizarPanel();
                    });

                    pausa.play();
                }
            } else {
                resetearSeleccion();
            }
        }
        actualizarPanel();
    }

    private void resetearSeleccion() {
        piedraSeleccionada = null;
        indiceTableroSeleccionado = -1;
        posicionesResaltadas.clear();
    }

    public void actualizarPanel() {
        if (juegoTerminado) return;
        turno.setText("Turno: " + shobu.getJugadorActual().getNombre());
        estado.setText(shobu.pasivoRealizado() ? "Movimiento AGRESIVO" : "Movimiento PASIVO");
        for (int i = 0; i < 4; i++) {
            actualizarGridPane(i);
        }
    }

    private void actualizarGridPane(int indiceTablero) {
        GridPane cuadricula = cuadriculas.get(indiceTablero);
        Tablero tablero = shobu.getTableros().get(indiceTablero);

        for (int fila = 0; fila < 4; fila++) {
            for (int columna = 0; columna < 4; columna++) {
                Button casilla = (Button) cuadricula.getChildren().get(fila * 4 + columna);
                Posicion posActual = new Posicion(fila, columna);

                casilla.setStyle("-fx-background-color: #c0b090; -fx-border-color: #8a7a60; -fx-border-width: 1;");
                casilla.setGraphic(null);

                Piedra piedra = tablero.getPiedraEnPosicion(posActual);

                if (piedra != null) {
                    Circle circulo = new Circle(24);
                    circulo.setFill(piedra.getPropietario() == 1 ? Color.BLACK : Color.WHITE);
                    circulo.setStroke(Color.GRAY);

                    if (piedraSeleccionada != null && piedra.equals(piedraSeleccionada) && indiceTablero == indiceTableroSeleccionado) {
                        circulo.setStroke(Color.GOLD);
                        circulo.setStrokeWidth(3);
                    }
                    casilla.setGraphic(circulo);
                }

                if (posicionesResaltadas.contains(posActual) && indiceTablero == indiceTableroSeleccionado) {
                    casilla.setStyle("-fx-background-color: rgba(0, 200, 0, 0.4); -fx-border-color: #00aa00; -fx-border-width: 2;");
                }
            }
        }
    }

    private boolean verificarVictoria() {
        if (shobu.hayGanador()) {
            this.juegoTerminado = true;
            turno.setText("SE ACABÓ LA PARTIDA");
            estado.setText("EL GANADOR ES EL " + shobu.getGanador().getNombre().toUpperCase());
            for (int i = 0; i < 4; i++) {
                actualizarGridPane(i);
            }
            contenedor.setDisable(true);
            return true;
        }
        return false;
    }

    public VBox getContenedor() {
        return contenedor;
    }
}