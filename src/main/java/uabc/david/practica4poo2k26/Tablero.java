package uabc.david.practica4poo2k26;

import java.util.ArrayList;

public class Tablero {
    private ArrayList<ArrayList<String>> cuadricula;
    private String colorTablero;

    public static final String VACÍA = "V";
    public static final String NEGRA = "N";
    public static final String BLANCA = "B";

    public static final String COLOR_NEGRO = "NEGRO";
    public static final String COLOR_BLANCO = "BLANCO";

    public static final int TAMAÑO = 4;

    public Tablero(String color) {
        this.colorTablero = color;
        this.cuadricula = new ArrayList<>();
        inicializarCuadricula();
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
        return this.colorTablero;
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
