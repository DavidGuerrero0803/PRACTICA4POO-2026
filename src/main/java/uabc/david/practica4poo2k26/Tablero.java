package uabc.david.practica4poo2k26;

import java.util.ArrayList;

public class Tablero {
    private ArrayList<ArrayList<String>> cuadricula;
    private String colorTablero;

    public Tablero(String color) {
        this.colorTablero = color;
        this.cuadricula = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            ArrayList<String> fila = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                // V = vacío.
                fila.add("V");
            }
            this.cuadricula.add(fila);
        }
    }

    public String getColor() {
        return this.colorTablero;
    }

    public String getPosicion(Posicion posicion) {
        return cuadricula.get(posicion.getFila()).get(posicion.getColumna());
    }

    public void setPosicion(Posicion posicion, String pieza) {
        cuadricula.get(posicion.getFila()).set(posicion.getColumna(), pieza);
    }

}
