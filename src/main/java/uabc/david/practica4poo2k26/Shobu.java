package uabc.david.practica4poo2k26;

import java.util.ArrayList;
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

    public int[] obtenerVectorMovimiento(Posicion inicio, Posicion fin) {
        return new int[] {fin.getFila() - inicio.getFila(), fin.getColumna() - inicio.getColumna()};
    }

    public ArrayList<Posicion> obtenerTrayectoria(Posicion inicio, int[] vector) {
        ArrayList<Posicion> pasos = new ArrayList<>();

        int cambioFila = vector[0];
        int cambioColumna = vector[1];

        int pasoFila = 0;
        int pasoColumna = 0;

        if (cambioFila > 0) {
            pasoFila = 1;
        } else if (cambioFila < 0) {
            pasoFila = -1;
        }

        if (cambioColumna > 0) {
            pasoColumna = 1;
        } else if (cambioColumna < 0) {
            pasoColumna = -1;
        }

        int distanciaTotal = Math.max(Math.abs(cambioFila), Math.abs(cambioColumna));

        for (int i = 1; i <= distanciaTotal; i++) {
            int nuevaFila = inicio.getFila() + (pasoFila * i);
            int nuevaColumna = inicio.getColumna() + (pasoColumna * i);

            pasos.add(new Posicion(nuevaFila, nuevaColumna));
        }

        return pasos;
    }

    public boolean esPasivoValido(Tablero tablero, Posicion inicio, Posicion fin) {
        int[] vector = obtenerVectorMovimiento(inicio, fin);
        int distanciaFila = Math.abs(vector[0]);
        int distanciaColumna = Math.abs(vector[1]);

        if ((distanciaFila > 2 || distanciaColumna > 2) || (distanciaFila == 0 && distanciaColumna == 0)) {
            return false;
        }

        if (distanciaFila != 0 && distanciaColumna != 0 && distanciaFila != distanciaColumna) {
            return false;
        }

        ArrayList<Posicion> trayectoria = obtenerTrayectoria(inicio, vector);

        for (Posicion posicion : trayectoria) {
            if (!tablero.getPosicion(posicion).equals("V")) {
                return false;
            }
        }

        return true;
    }

    public Tablero getTablero(String nombre) {
        return tableros.get(nombre);
    }
}
