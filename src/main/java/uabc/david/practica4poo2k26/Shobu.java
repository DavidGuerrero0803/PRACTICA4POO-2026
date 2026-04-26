package uabc.david.practica4poo2k26;

import java.util.HashMap;

public class Shobu {
    private HashMap<String, Tablero> tableros;
    private String turnoActual;

    public Shobu() {
        this.tableros = new HashMap<>();
        this.turnoActual = "NEGRO";
        prepararJuego();
    }

    private void prepararJuego() {
        // Clave: Posición del tablero, valor: Color del tablero.
        tableros.put("ARRIBA_IZQUIERDA", new Tablero("NEGRO"));
        tableros.put("ARRIBA_DERECHA", new Tablero("BLANCO"));
        tableros.put("ABAJO_IZQUIERDA", new Tablero("BLANCO"));
        tableros.put("ABAJO_DERECHA", new Tablero("NEGRO"));

    }

    public Tablero getTablero(String nombre) {
        return tableros.get(nombre);
    }
}
