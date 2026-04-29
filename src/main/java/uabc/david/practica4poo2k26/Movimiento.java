package uabc.david.practica4poo2k26;

public class Movimiento {
    private Posicion origen;
    private Posicion destino;
    private int indiceTablero;

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

    public int getDeltaFila() {
        int diferencia = destino.getFila() - origen.getFila();
        if (diferencia > 0) {
            return 1;
        }
        if (diferencia < 0) {
            return -1;
        }
        return 0;
    }

    public int getDeltaColumna() {
        int diferencia = destino.getColumna() - origen.getColumna();
        if (diferencia > 0) {
            return 1;
        }
        if (diferencia < 0) {
            return -1;
        }
        return 0;
    }

    public int getDistancia() {
        int diferenciaFila = Math.abs(destino.getFila() - origen.getFila());
        int diferenciaColumna = Math.abs(destino.getColumna() - origen.getColumna());
        return Math.max(diferenciaFila, diferenciaColumna);
    }
}
