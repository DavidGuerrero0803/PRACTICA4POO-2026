package uabc.david.practica4poo2k26;

public class Movimiento {
    private int indiceTablero;
    private Posicion origen;
    private Posicion destino;

    public Movimiento(int indiceTablero, Posicion origen, Posicion destino, boolean esPasivo) {
        this.indiceTablero = indiceTablero;
        this.origen = origen;
        this.destino = destino;
    }

    public int getIndiceTablero() {
        return indiceTablero;
    }

    public Posicion getOrigen() {
        return origen;
    }

    public Posicion getDestino() {
        return destino;
    }
}
