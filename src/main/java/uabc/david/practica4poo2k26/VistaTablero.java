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

/**
 * Clase que funciona como la Vista gráfica del Shobu.
 * Construye y actualiza la interfaz visual de los 4 tableros,
 * gestiona los clics del usuario, las fases pasiva y agresiva,
 * y actualiza las etiquetas de turno y estado del juego.
 */
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

    /**
     * Construye la vista del tablero.
     * Inicializa el estado visual y construye el panel gráfico.
     * @param shobu Instancia de la clase Shobu.
     */
    public VistaTablero(Shobu shobu) {
        this.shobu = shobu;
        this.cuadriculas = new ArrayList<>();
        this.posicionesResaltadas = new ArrayList<>();
        this.piedraSeleccionada = null;
        // El -1 es para indicar que no hay tablero seleccionado.
        this.indiceTableroSeleccionado = -1;
        this.juegoTerminado = false;
        construirPanel();
    }

    /**
     * Crea el panel principal con los 4 tableros junto a las etiquetas de turno y estado.
     * Los tableros 0 y 1 se colocan en la fila superior (del jugador blanco),
     * y los tableros 2 y 3 en la fila inferior (del jugador negro).
     */
    private void construirPanel() {
        // Etiquetas de turno, estado general y estado pasivo/agresivo.
        turnoBlanco = new Label();
        turnoNegro = new Label();
        estado = new Label();
        estadoPasivo = new Label();
        estadoAgresivo = new Label();
        // Estilos independientes para cada etiqueta.
        turnoBlanco.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        turnoNegro.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        estado.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        estadoPasivo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        estadoAgresivo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Creación de HBoxes que contendrán a los tableros.
        // Tableros del jugador blanco.
        HBox filaSuperior = new HBox(10);
        filaSuperior.setAlignment(Pos.CENTER);
        // Tableros del jugador negro.
        HBox filaInferior = new HBox(10);
        filaInferior.setAlignment(Pos.CENTER);

        // Este ciclo usará el crearGridPane() para generar cada tablero.
        for (int i = 0; i < 4; i++) {
            GridPane cuadricula = crearGridPane(i);
            // Guarda la referencia del ArrayList.
            cuadriculas.add(cuadricula);
            if (i < 2) {
                // Tableros 0 y 1: arriba.
                filaSuperior.getChildren().add(cuadricula);
            } else {
                // Tableros 2 y 3: abajo.
                filaInferior.getChildren().add(cuadricula);
            }
            
        }

        // Todos los elementos visuales se añaden al VBox por orden.
        contenedor = new VBox(10, turnoBlanco, estado, filaSuperior, filaInferior, turnoNegro);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(10));

        actualizarPanel();
    }

    /**
     * Refresca toda la interfaz visual: etiquetas de turno, instrucción de fase y los 4 tableros.
     * No hace nada si el juego ya terminó.
     */
    public void actualizarPanel() {
        // Verifica primero si es que el juego ha terminado.
        if (juegoTerminado) {
            return;
        }
        // Si el juego sigue, los turnos se actualizan.
        actualizarTurnos();
        // Mostrar instrucción según la fase actual
        estado.setText(shobu.getPasivoRealizado() ? "Haz un movimiento AGRESIVO en un tablero de color opuesto"
                : "Haz un movimiento PASIVO en tus tableros");
        // El ciclo hace que los 4 tableros se actualicen.
        for (int i = 0; i < 4; i++) {
            actualizarGridPane(i);
        }
    }

    /**
     * Actualiza las etiquetas de turno, indicando con color y flecha quién juega ahora.
     */
    public void actualizarTurnos() {
        // Si el ID es 2 (blanco) indicará la etiqueta que es su turno.
        if (shobu.getJugadorActual().getIdentificador() == 2) {
            turnoBlanco.setText("-> TU TURNO, JUGADOR BLANCO");
            turnoBlanco.setTextFill(Color.DARKBLUE);
            // En este turno, la etiqueta del jugador negro se torna gris.
            turnoNegro.setText("JUGADOR NEGRO");
            turnoNegro.setTextFill(Color.GRAY);
        } else {
            // En caso de que no sea el ID 2, sino el 1,
            // entonces indicará el turno al jugador negro.
            turnoNegro.setText("-> TU TURNO, JUGADOR NEGRO");
            turnoNegro.setTextFill(Color.DARKBLUE);
            // En este turno, la etiqueta del jugador blanco se torna gris.
            turnoBlanco.setText("JUGADOR BLANCO");
            turnoBlanco.setTextFill(Color.GRAY);
        }
    }

    /**
     * Crea el GridPane de un tablero con sus 16 casillas.
     * Aplica el color de fondo según el color del tablero (negro o blanco).
     * Cada casilla es un botón que reacciona al manejo de clics.
     * @param indiceTablero Índice del tablero a crear (0 a 3).
     * @return GridPane configurado y listo para mostrarse.
     */
    private GridPane crearGridPane(int indiceTablero) {
        // Se crea el GridPane con separación en ambas direcciones.
        GridPane cuadricula = new GridPane();
        cuadricula.setHgap(4);
        cuadricula.setVgap(4);
        cuadricula.setPadding(new Insets(8));

        // El operador ternario permite diferenciar entre tableros blancos y negros.
        String colorTablero = shobu.getTableros().get(indiceTablero).getColor();
        String estiloFondo = colorTablero.equals("negro")
                ? "-fx-background-color: #3a2a1a; -fx-border-color: #3a2a1a; -fx-border-width: 3;"
                : "-fx-background-color: #f0d9b5; -fx-border-color: #f0d9b5; -fx-border-width: 3;";
        cuadricula.setStyle(estiloFondo);

        // A través el ciclo se crean los 16 botones que representan un tablero.
        for (int fila = 0; fila < 4; fila++) {
            for (int columna = 0; columna < 4; columna++) {
                Button casilla = new Button();
                // El tamaño se ajusta para que sea cuadrado y no rectangular.
                casilla.setPrefSize(65, 65);

                // Para poder usar la lambda dentro de setOnAction, fila y columna
                // deben pasar a ser constantes (tipo final).
                final int FILA = fila;
                final int COLUMNA = columna;
                // La casilla se vincula con la lógica de los clics para todas las casillas del tablero.
                casilla.setOnAction(e -> manejarClicCasilla(indiceTablero, FILA, COLUMNA));
                // Se añade el botón en la cuadrícula en su coordenada correspondiente.
                cuadricula.add(casilla, columna, fila);
            }
        }
        return cuadricula;
    }

    /**
     * Maneja el clic del usuario sobre una casilla.
     * Se comporta según la fase del turno (sea pasiva o agresiva).
     * @param indiceTablero Índice del tablero seleccionado.
     * @param fila Fila de la casilla seleccionada.
     * @param columna Columna de la casilla seleccionada.
     */
    private void manejarClicCasilla(int indiceTablero, int fila, int columna) {
        // Se verifica si alguien ya ganó, para que el tablero deje de responder.
        if (juegoTerminado) {
            return;
        }

        // Las coordenadas del clic son convertidas en instancias.
        Posicion posicionClicada = new Posicion(fila, columna);
        Tablero tablero = shobu.getTableros().get(indiceTablero);

        // Revisa si en el lugar donde se hizo clic hay una piedra.
        Piedra piedraEnCasilla = tablero.getPosPiedra(posicionClicada);

        // Se verifica si ya se hizo o aún no el movimiento pasivo.
        if (!shobu.getPasivoRealizado()) {
            // Si aún no hay movimiento pasivo, el clic nada más puede seleccionar/mover piedras propias.
            manejarFasePasiva(indiceTablero, posicionClicada, piedraEnCasilla, tablero);
        } else {
            // Si ya hubo el movimiento pasivo, entonces el clic hará el movimiento agresivo.
            manejarFaseAgresiva(indiceTablero, posicionClicada, piedraEnCasilla);
        }
    }

    /**
     * Maneja la fase pasiva del turno.
     * @param indiceTablero Índice del tablero seleccionado.
     * @param pos Posición seleccionada.
     * @param piedra Piedra en la posición seleccionada.
     * @param tablero Tablero seleccionado.
     */
    private void manejarFasePasiva(int indiceTablero, Posicion pos, Piedra piedra, Tablero tablero) {
        // Verifica si no hay ninguna piedra seleccionada todavía.
        if (piedraSeleccionada == null) {
            // La condición se valida si:
            // Hay una piedra en la casilla.
            // La piedra es del jugador actual.
            // Está en los tableros que le pertenecen.
            if (piedra != null &&
                    piedra.getPropietario() == shobu.getJugadorActual().getIdentificador() &&
                    tablero.getPropietario() == shobu.getJugadorActual().getIdentificador()) {

                // Guarda la piedra y el tablero para recordar que se va a mover.
                piedraSeleccionada = piedra;
                indiceTableroSeleccionado = indiceTablero;
                // Calcula y guarda los destinos válidos para resaltarlos de color verde.
                posicionesResaltadas = shobu.getMovimientosPasivos(pos, indiceTablero);
            }
        } else {
            // Se verifica si ya hay una piedra seleccionada, para que el jugador haga su segundo clic.
            if (posicionesResaltadas.contains(pos)) {
                // Ejecuta entonces un movimiento pasivo del juego.
                shobu.hacerMovimientoPasivo(new Movimiento(indiceTableroSeleccionado, piedraSeleccionada.getPosicion(), pos, true));
                // Posteriormente la selección se limpia para la fase agresiva.
                resetearSeleccion();
            } else {
                // En caso de hacer clic en cualquier otro lado, se cancela la selección actual.
                resetearSeleccion();
            }
        }
        // Se actualiza el panel con los cambios realizados.
        actualizarPanel();
    }

    /**
     * Maneja la interacción de la fase agresiva del jugador en turno.
     * @param indiceTablero Índice del tablero seleccionado.
     * @param posDestino Posición seleccionada.
     * @param piedraEscogida Piedra en la posición seleccionada.
     */
    private void manejarFaseAgresiva(int indiceTablero, Posicion posDestino, Piedra piedraEscogida) {
        // Verifica si no se ha seleccionado la piedra que realizará el ataque.
        if (piedraSeleccionada == null) {
            // Se valida que el clic sea en una piedra propia.
            if (piedraEscogida != null && piedraEscogida.getPropietario() == shobu.getJugadorActual().getIdentificador()) {

                // Calcula si la piedra puede repetir el movimiento del pasivo en el tablero.
                ArrayList<Posicion> validos = shobu.getMovimientosAgresivos(posDestino, indiceTablero);

                // Solo selecciona si el movimiento es posible.
                if (!validos.isEmpty()) {
                    piedraSeleccionada = piedraEscogida;
                    indiceTableroSeleccionado = indiceTablero;
                    posicionesResaltadas = validos;
                }
            }
            // En caso de haber elegido ya la piedra, debe seleccionar el destino.
        } else {
            if (posicionesResaltadas.contains(posDestino)) {
                // Ejecuta un movimiento agresivo del juego.
                shobu.hacerMovimientoAgresivo(new Movimiento(indiceTableroSeleccionado, piedraSeleccionada.getPosicion(), posDestino, false));

                // Verifica de antemano si ya hay una victoria antes de terminar el turno.
                if (verificarVictoria()) {
                    return;
                }

                // Se reinicia la selección y cambia de turno.
                resetearSeleccion();
                shobu.finalizarTurno();
                // Si el siguiente jugador es la máquina, ejecuta su turno con delay incluído.
                if (shobu.getJugadorActual().esMaquina()) {
                    // Se crea un PauseTransition para que genere una breve "pausa" antes de que actúe.
                    PauseTransition pausa = new PauseTransition(seconds(1.2));

                    pausa.setOnFinished(evento -> {
                        // La máquina hace su turno completo.
                        shobu.realizarMovimientoMaquina();

                        // Verifica si la máquina llegó a eliminar la última piedra de un tablero.
                        if (verificarVictoria()) {
                            return;
                        }

                        // Cambia el turno y actualiza toda la vista.
                        shobu.finalizarTurno();
                        actualizarPanel();
                    });
                    // La pausa se reproduce y al detenerse ejecuta la acción.
                    pausa.play();
                }
            } else {
                // Si el destino no era válido, se cancela la selección.
                resetearSeleccion();
            }
        }
        // Se actualiza el panel con los cambios realizados.
        actualizarPanel();
    }

    /**
     * Reinicia la selección actual:
     * deja de seleccionar la piedra y limpia los destinos resaltados.
     */
    private void resetearSeleccion() {
        piedraSeleccionada = null;
        indiceTableroSeleccionado = -1;
        posicionesResaltadas.clear();
    }

    /**
     * Actualiza un tablero específico:
     * - Dibuja las piedras como círculos.
     * - Resalta la piedra seleccionada con borde verde.
     * - Pinta de verde las casillas de destino válidas.
     * @param indiceTablero Índice del tablero a actualizar (0 a 3).
     */
    private void actualizarGridPane(int indiceTablero) {
        // Se accede al GridPane y al tablero lógico correspondiente.
        GridPane cuadricula = cuadriculas.get(indiceTablero);
        Tablero tablero = shobu.getTableros().get(indiceTablero);

        // El ciclo for pasa por todas las coordenadas de un tablero.
        for (int fila = 0; fila < 4; fila++) {
            for (int columna = 0; columna < 4; columna++) {

                // Se busca el botón (casilla) dentro del GridPane.
                Button casilla = (Button) cuadricula.getChildren().get(fila * 4 + columna);
                Posicion posActual = new Posicion(fila, columna);

                // Se restaura el color original de la casilla y quita cualquier piedra anterior.
                casilla.setStyle("-fx-background-color: #ffffff; -fx-border-color: #8a7a60; -fx-border-radius: 5;");
                casilla.setGraphic(null);

                // Verifica si hay una piedra en la posición lógica.
                Piedra piedra = tablero.getPosPiedra(posActual);

                if (piedra != null) {
                    // Se crea la representación visual de la piedra (con un círculo).
                    Circle circulo = new Circle(20);
                    circulo.setFill(piedra.getPropietario() == 1 ? Color.BLACK : Color.WHITE);
                    circulo.setStroke(Color.BLACK);
                    circulo.setStrokeWidth(2);

                    // Si es la piedra que el jugador seleccionó, se le añadirá un borde verde.
                    if (piedra.equals(piedraSeleccionada) && indiceTablero == indiceTableroSeleccionado) {
                        circulo.setStroke(Color.GREEN);
                        circulo.setStrokeWidth(4);
                    }
                    // Se dibuja el círculo dentro del botón.
                    casilla.setGraphic(circulo);
                }

                // Si la casilla es un movimiento válido, pintará de verde las opciones que tenga.
                if (posicionesResaltadas.contains(posActual) && indiceTablero == indiceTableroSeleccionado) {
                    casilla.setStyle("-fx-border-color: #00ff00; -fx-background-color: #057d05; -fx-border-width: 3; -fx-border-radius: 5;");
                }
            }
        }
    }

    /**
     * Verifica si hay un ganador y, de ser así, muestra el mensaje de victoria
     * y deshabilita la interfaz para evitar más interacciones.
     * @return true si el juego terminó con un ganador
     */
    private boolean verificarVictoria() {
        // Busca si en el juego ya hay un ganador.
        if (shobu.hayGanador()) {
            // En caso de que lo haya, juegoTerminado se marca como true.
            this.juegoTerminado = true;
            // Cambia la etiqueta de los estados para anunciar al ganador con un color verde.
            estado.setText("EL GANADOR ES EL " + shobu.getGanador().getNombre().toUpperCase());
            estado.setTextFill(Color.GREEN);
            // Actualiza por última vez los 4 tableros.
            for (int i = 0; i < 4; i++) {
                // Muestra el estado final del tablero completo.
                actualizarGridPane(i);
            }
            // Finalmente, se bloquea toda interacción dentro de la interfaz.
            contenedor.setDisable(true);
            return true;
        }
        // Si no hay ganador, entonces regresará un false.
        return false;
    }

    /**
     * Devuelve el contenedor principal de la vista para que pueda integrarse a la GUI.
     * @return VBox con la interfaz del juego.
     */
    public VBox getContenedor() {
        return contenedor;
    }
}