package uabc.david.practica4poo2k26;

import java.util.HashMap;

public class Tablero {
    private HashMap<Posicion, Piedra> piedras;
    private String color;
    private int indice;
    private int propietario;

    public Tablero(int indice, String color, int propietario) {
        this.indice = indice;
        this.color = color;
        this.propietario = propietario;
        this.piedras = new HashMap<>();
    }

    public int getIndice() {
        return indice;
    }

    public String getColor() {
        return color;
    }

    public int getPropietario() {
        return propietario;
    }

    public HashMap<Posicion, Piedra> getPiedras() {
        return piedras;
    }

    public Piedra getPosPiedra(Posicion posicion) {
        return piedras.get(posicion);
    }

    public void agregarPiedra(Piedra piedra) {
        piedras.put(piedra.getPosicion(), piedra);
    }

    public void eliminarPiedra(Piedra piedra) {
        piedras.remove(piedra.getPosicion());
    }

    public void actualizarPosPiedra(Posicion anterior, Posicion nueva, Piedra piedra) {
        piedras.remove(anterior);
        piedras.put(nueva, piedra);
    }

}
