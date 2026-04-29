package uabc.david.practica4poo2k26;

import java.util.Objects;

public class Posicion {
    private int fila;
    private int columna;

    public Posicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return  columna;
    }

    public boolean estaEnRango() {
        return fila >= 0 && fila <= 3 && columna >= 0 && columna <= 3;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Posicion)) {
            return false;
        }
        Posicion otra = (Posicion) obj;
        return fila == otra.fila && columna == otra.columna;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fila, columna);
    }
}
