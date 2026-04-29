package uabc.david.practica4poo2k26;

import java.util.ArrayList;
import java.util.HashMap;

public class Tablero {
    private HashMap<Posicion, Piedra> piedras;
    private int indice;
    private String color;
    private int propietario;

    public Tablero(int indice, String color, int propietario) {
        this.indice = indice;
        this.color = color;
        this.propietario = propietario;
        this.piedras = new HashMap<>();
    }

    private void inicializarCuadricula() {
        for (int i = 0; i < TAMAÑO; i++) {
            ArrayList<String> fila = new ArrayList<>();
            for (int j = 0; j < TAMAÑO; j++) {
                fila.add(VACÍA);
            }
            this.cuadricula.add(fila);
        }
    }

    public String getColor() {
        return color;
    }

    public int getPropietario() {
        return propietario;
    }

    public String getColorOpuesto() {
        return colorTablero.equals(COLOR_BLANCO) ? COLOR_NEGRO : COLOR_BLANCO;
    }

    public String getPosicion(Posicion posicion) {
        return cuadricula.get(posicion.getFila()).get(posicion.getColumna());
    }

    public void setPosicion(Posicion posicion, String piedra) {
        cuadricula.get(posicion.getFila()).set(posicion.getColumna(), piedra);
    }

    public boolean estaVacia(Posicion posicion) {
        return getPosicion(posicion).equals(VACÍA);
    }

}
