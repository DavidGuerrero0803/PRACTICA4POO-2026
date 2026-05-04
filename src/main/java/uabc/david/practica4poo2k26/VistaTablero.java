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
    private Label turnoBlanco;
    private Label turnoNegro;
    private Label estado;
    private Label estadoPasivo;
    private Label estadoAgresivo;
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
        turnoBlanco = new Label();
        turnoNegro = new Label();
        estado = new Label();
        estadoPasivo = new Label();
        estadoAgresivo = new Label();
        turnoBlanco.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        turnoNegro.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        estado.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        estadoPasivo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        estadoAgresivo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox filaSuperior = new HBox(10);
        filaSuperior.setAlignment(Pos.CENTER);
        HBox filaInferior = new HBox(10);
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

        contenedor = new VBox(10, turnoBlanco, estado, filaSuperior, filaInferior, turnoNegro);
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
                ? "-fx-background-color: #3a2a1a; -fx-border-color: #3a2a1a; -fx-border-width: 3;"
                : "-fx-background-color: #f0d9b5; -fx-border-color: #f0d9b5; -fx-border-width: 3;";
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

        Piedra piedraEnCasilla = tablero.getPosPiedra(posicionClicada);

        if (!shobu.getPasivoRealizado()) {
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
                posicionesResaltadas = shobu.getMovimientosPasivos(pos, indiceTablero);
            }
        } else {
            if (posicionesResaltadas.contains(pos)) {
                shobu.hacerMovimientoPasivo(new Movimiento(indiceTableroSeleccionado, piedraSeleccionada.getPosicion(), pos, true));
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
                ArrayList<Posicion> validos = shobu.getMovimientosAgresivos(pos, indiceTablero);
                if (!validos.isEmpty()) {
                    piedraSeleccionada = piedra;
                    indiceTableroSeleccionado = indiceTablero;
                    posicionesResaltadas = validos;
                }
            }
        } else {
            if (posicionesResaltadas.contains(pos)) {
                shobu.hacerMovimientoAgresivo(new Movimiento(indiceTableroSeleccionado, piedraSeleccionada.getPosicion(), pos, false));

                if (verificarVictoria()) {
                    return;
                }

                resetearSeleccion();

                shobu.finalizarTurno();
                if (shobu.getJugadorActual().esMaquina()) {
                    PauseTransition pausa = new PauseTransition(seconds(1.2));

                    pausa.setOnFinished(evento -> {
                        shobu.realizarMovimientoMaquina();

                        if (verificarVictoria()) {
                            return;
                        }

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
        if (juegoTerminado) {
            return;
        }
        actualizarTurnos();

        estado.setText(shobu.getPasivoRealizado() ? "Haz un movimiento AGRESIVO en un tablero de color opuesto"
                : "Haz un movimiento PASIVO en tus tableros");
        for (int i = 0; i < 4; i++) {
            actualizarGridPane(i);
        }
    }

    public void actualizarTurnos() {
        if (shobu.getJugadorActual().getIdentificador() == 2) {
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

    private void actualizarGridPane(int indiceTablero) {
        GridPane cuadricula = cuadriculas.get(indiceTablero);
        Tablero tablero = shobu.getTableros().get(indiceTablero);

        for (int fila = 0; fila < 4; fila++) {
            for (int columna = 0; columna < 4; columna++) {
                Button casilla = (Button) cuadricula.getChildren().get(fila * 4 + columna);
                Posicion posActual = new Posicion(fila, columna);

                casilla.setStyle("-fx-background-color: #ffffff; -fx-border-color: #8a7a60; -fx-border-radius: 5;");
                casilla.setGraphic(null);

                Piedra piedra = tablero.getPosPiedra(posActual);

                if (piedra != null) {
                    Circle circulo = new Circle(20);
                    circulo.setFill(piedra.getPropietario() == 1 ? Color.BLACK : Color.WHITE);
                    circulo.setStroke(Color.BLACK);
                    circulo.setStrokeWidth(2);

                    if (piedra.equals(piedraSeleccionada) && indiceTablero == indiceTableroSeleccionado) {
                        circulo.setStroke(Color.GREEN);
                        circulo.setStrokeWidth(4);
                    }
                    casilla.setGraphic(circulo);
                }

                if (posicionesResaltadas.contains(posActual) && indiceTablero == indiceTableroSeleccionado) {
                    casilla.setStyle("-fx-border-color: #00ff00; -fx-background-color: #057d05; -fx-border-width: 3; -fx-border-radius: 5;");
                }
            }
        }
    }

    private boolean verificarVictoria() {
        if (shobu.hayGanador()) {
            this.juegoTerminado = true;
            estado.setText("EL GANADOR ES EL " + shobu.getGanador().getNombre().toUpperCase());
            estado.setTextFill(Color.GREEN);
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