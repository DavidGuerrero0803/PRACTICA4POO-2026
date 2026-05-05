package uabc.david.practica4poo2k26;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * ShobuGUI maneja la interfaz de usuario gráfica (GUI) del Shobu.
 * Extiende Application de JavaFX y maneja el escenario principal.
 * Se encarga de mostrar el menú inicial y de inicializar la partida.
 */
public class ShobuGUI extends Application {
    private Stage escenarioPrincipal;
    private Shobu shobu;

    /**
     * Configura el título de la ventana y muestra el menú principal.
     * @param escenario Escenario principal.
     */
    @Override
    public void start(Stage escenario) {
        this.escenarioPrincipal = escenario;
        escenarioPrincipal.setTitle("Shobu");
        mostrarMenuPrincipal();
        escenarioPrincipal.setResizable(false);
        escenarioPrincipal.show();
    }

    /**
     * Muestra el menú principal con el título y los botones de modo de juego.
     */
    private void mostrarMenuPrincipal() {
        // Label y propiedades del título.
        Label titulo = new Label("Shobu");
        titulo.setFont(Font.font("Arial", 100));

        // Se le aplica un mismo estilo a ambos botones del menú.
        String buttonStyle = "-fx-font-size: 20px; -fx-min-width: 250px; -fx-background-radius: 10;";

        // Se crean los dos botones que funcionarán como selector de modo de juego.
        Button botonPvP = new Button("Jugador vs. Jugador");
        botonPvP.setStyle(buttonStyle);
        Button botonPvE = new Button("Jugador vs. Máquina");
        botonPvE.setStyle(buttonStyle);

        botonPvP.setPrefWidth(320);
        botonPvP.setPrefHeight(50);
        botonPvE.setPrefWidth(320);
        botonPvE.setPrefHeight(50);

        // Si se maneja este botón, entonces contraMaquina es false, haciéndolo un PvP.
        botonPvP.setOnAction(e -> iniciarPartida(false));
        // Se se usa este botón, entonces contraMaquina será true, volviéndolo un PvE.
        botonPvE.setOnAction(e -> iniciarPartida(true));

        Label nombre = new Label("Práctica #4 - Mario David Guerrero Márquez");
        nombre.setFont(Font.font(15));
        nombre.setTextFill(Color.WHITE);

        // Todos los elementos se añaden dentro del VBox en orden.
        VBox contenedor = new VBox(20, titulo, botonPvP, botonPvE, nombre);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(40));
        // El VBox es agregado al escenario principal junto a la resolución de la ventana.
        Scene escena = new Scene(contenedor, 800, 800);
        escenarioPrincipal.setScene(escena);
    }

    /**
     * Inicializa una nueva partida creando los jugadores y el objeto Shobu.
     * @param contraMaquina true si el jugador blanco será controlado por la máquina.
     */
    private void iniciarPartida(boolean contraMaquina) {
        // El jugador 1 siempre será tomado como humano.
        Jugador jugadorNegro = new Jugador("JUGADOR NEGRO", 1, false);
        // Jugador 2 tendrá la chance de ser humano o máquina.
        Jugador jugadorBlanco = new Jugador("JUGADOR BLANCO", 2, contraMaquina);
        shobu = new Shobu(jugadorNegro, jugadorBlanco);
        mostrarJuego();
    }

    /**
     * Crea la vista del tablero y la coloca en el escenario principal para iniciar la partida.
     */
    private void mostrarJuego() {
        // Se crea una instancia de VistaTablero.
        VistaTablero vistaTablero = new VistaTablero(shobu);

        // Se crea un VBox que va a guardar lo que se ha generado en VistaTablero.
        VBox contenedor = new VBox(10, vistaTablero.getContenedor());
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(20));
        // Este VBox agrega todos los elementos de VistaTablero a una nueva escena.
        Scene escena = new Scene(contenedor, 800, 800);
        escenarioPrincipal.setScene(escena);
    }

    /**
     * main que llama a launch() para iniciar.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
