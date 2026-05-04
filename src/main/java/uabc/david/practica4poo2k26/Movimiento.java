package uabc.david.practica4poo2k26;

/**
 * Esta clase representa el movimiento de una piedra en un tablero específico.
 * Contiene elementos como el origen, destino e índice del tablero involucrado.
 */
public class Movimiento {
    private Posicion origen;
    private Posicion destino;
    private int indiceTablero;

    /**
     * Crea un movimiento entre dos posiciones en un tablero.
     * @param indiceTablero Índice del tablero (0-3) en donde se realiza el movimiento.
     * @param origen Posición de inicio de la piedra.
     * @param destino Posición a la que se moverá la piedra.
     * @param esPasivo Indica si el movimiento es pasivo o no.
     */
    public Movimiento(int indiceTablero, Posicion origen, Posicion destino, boolean esPasivo) {
        this.indiceTablero = indiceTablero;
        this.origen = origen;
        this.destino = destino;
    }

    /**
     * Regresa el índice del tablero en donde ocurre
     * el movimiento de la piedra.
     * @return El índice del tablero donde ocurre este movimiento.
     */
    public int getIndiceTablero() {
        return indiceTablero;
    }

    /**
     * Regresa la posición de origen en donde ocurre el movimiento.
     * @return La Posición de origen del movimiento.
     */
    public Posicion getOrigen() {
        return origen;
    }

    /**
     * Regresa la posición del destino en donde ocurre el movimiento.
     * @return La Posición de destino del movimiento.
     */
    public Posicion getDestino() {
        return destino;
    }

    /**
     * Calcula la dirección vertical del movimiento.
     * @return -1 (si es arriba), 0 (si no hay cambio) o 1 (si es abajo).
     */
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

    /**
     * Calcula la dirección horizontal del movimiento.
     * @return -1 (si es izquierda), 0 (si no hay cambio) o 1 (si es derecha).
     */
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

    /**
     * Calcula la distancia del movimiento en casillas (1-2)
     * (el máximo entre la diferencia de fila y columna).
     * Los movimientos válidos son de distancia 1 o 2.
     * @return La distancia en casillas del movimiento.
     */
    public int getDistancia() {
        int diferenciaFila = Math.abs(destino.getFila() - origen.getFila());
        int diferenciaColumna = Math.abs(destino.getColumna() - origen.getColumna());
        return Math.max(diferenciaFila, diferenciaColumna);
    }
}
