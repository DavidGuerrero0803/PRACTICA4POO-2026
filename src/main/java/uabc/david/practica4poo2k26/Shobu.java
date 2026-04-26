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

        for (int i = 0; i < 4; i++) {
            // N = negro.
            // B = blanco.
            tableros.get("ARRIBA_IZQUIERDA").setPosicion(new Posicion(0, i), "N");
            tableros.get("ARRIBA_DERECHA").setPosicion(new Posicion(0, i), "N");
            tableros.get("ABAJO_IZQUIERDA").setPosicion(new Posicion(3, i), "B");
            tableros.get("ABAJO_DERECHA").setPosicion(new Posicion(3, i), "B");
        }
    }

    public Tablero getTablero(String nombre) {
        return tableros.get(nombre);
    }
}
