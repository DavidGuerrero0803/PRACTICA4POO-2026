package uabc.david.practica4poo2k26;

import java.util.Objects;

/**
 * Esta clase representa una coordenada en el tablero de juego mediante una fila y columna.
 * Se utiliza principalmente como la llave en un HashMap para localizar las piedras.
 */
public class Posicion {
    private int fila;
    private int columna;

    /**
     * Crea una posición con fila y columna específicas.
     * @param fila Fila del tablero (0 a 3).
     * @param columna Columna del tablero (0 a 3).
     */
    public Posicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    /**
     * Regresa la fila de la posición.
     * @return La fila de esta posición.
     * */
    public int getFila() {
        return fila;
    }

    /**
     * Regresa la columna de la posición.
     * @return La columna de esta posición.
     * */
    public int getColumna() {
        return  columna;
    }

    /**
     * Compara esta posición con otro objeto.
     * Dos posiciones son iguales si tienen la misma fila y columna.
     * Es necesario para usar la Posicion como llave en HashMap.
     */
    @Override
    public boolean equals(Object obj) {
        // Compara si es el mismo objeto en memoria.
        if (this == obj) {
            return true;
        }
        // Se compara si son o no es del mismo tipo
        if (!(obj instanceof Posicion)) {
            return false;
        }
        // Pasa por un casting de objeto.
        Posicion otra = (Posicion) obj;
        // Se realiza la comparación de atributos.
        return fila == otra.fila && columna == otra.columna;
    }

    /**
     * Genera un código hash de fila y columna.
     * Se complementa con equals() para que el HashMap funcione correctamente.
     */
    @Override
    public int hashCode() {
        return Objects.hash(fila, columna);
    }
}
