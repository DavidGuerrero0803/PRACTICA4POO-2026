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

public class ShobuGUI extends Application {
    private Stage escenarioPrincipal;
    private Shobu shobu;

    @Override
    public void start(Stage escenario) {
        this.escenarioPrincipal = escenario;
        escenarioPrincipal.setTitle("Shobu");
        mostrarMenuPrincipal();
        escenarioPrincipal.show();
    }

    private void mostrarMenuPrincipal() {
        Label titulo = new Label("Shobu");
        titulo.setFont(Font.font("Arial", 100));

        String buttonStyle = "-fx-font-size: 20px; -fx-min-width: 250px; -fx-background-radius: 10;";

        Button botonPvP = new Button("Jugador vs. Jugador");
        botonPvP.setStyle(buttonStyle);
        Button botonPvE = new Button("Jugador vs. Máquina");
        botonPvE.setStyle(buttonStyle);

        botonPvP.setPrefWidth(320);
        botonPvP.setPrefHeight(50);
        botonPvE.setPrefWidth(320);
        botonPvE.setPrefHeight(50);

        botonPvP.setOnAction(e -> iniciarPartida(false));
        botonPvE.setOnAction(e -> iniciarPartida(true));

        Label nombre = new Label("Práctica #4 - Mario David Guerrero Márquez");
        nombre.setFont(Font.font(15));
        nombre.setTextFill(Color.WHITE);

        VBox contenedor = new VBox(20, titulo, botonPvP, botonPvE, nombre);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(40));

        Scene escena = new Scene(contenedor, 800, 800);
        escenarioPrincipal.setScene(escena);
    }

    private void iniciarPartida(boolean contraMaquina) {
        Jugador jugadorNegro = new Jugador("JUGADOR NEGRO", 1, false);
        Jugador jugadorBlanco = new Jugador("JUGADOR BLANCO", 2, contraMaquina);
        shobu = new Shobu(jugadorNegro, jugadorBlanco);
        mostrarJuego();
    }

    private void mostrarJuego() {
        VistaTablero vistaTablero = new VistaTablero(shobu);

        VBox contenedor = new VBox(10, vistaTablero.getContenedor());
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(20));

        Scene escena = new Scene(contenedor, 800, 800);
        escenarioPrincipal.setScene(escena);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
