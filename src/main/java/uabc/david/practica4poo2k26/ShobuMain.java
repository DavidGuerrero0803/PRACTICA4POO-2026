package uabc.david.practica4poo2k26;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ShobuMain extends Application {
    private Shobu juego;
    private GridPane contenedorPrincipal;

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

    private void manejarClic(String tablero, int fila, int columna) {
        System.out.println("Clic en tablero " + tablero + ", posición: " + fila + "," + columna);

    }

    public static void main(String[] args) {
        launch(args);
    }
}