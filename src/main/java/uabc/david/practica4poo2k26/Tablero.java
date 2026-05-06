package uabc.david.practica4poo2k26;

import java.util.HashMap;

/**
 * Esta clase representa uno de los 4 tableros del Shobu.
 * Maneja las piedras presentes usando un HashMap, en donde
 * la "llave" es la Posicion y el "valor" es la Piedra en la casilla.
 */
public class Tablero {
    private HashMap<Posicion, Piedra> piedras;
    private String color;
    private int indice;
    private int idJugador;

    /**
     * Crea un tablero vacío con índice, color y propietario.
     * @param indice índice del tablero (0, 1, 2 o 3).
     * @param color Color del tablero ("negro" o "blanco").
     * @param idJugador Jugador al que pertenece este tablero (1 o 2).
     */
    public Tablero(int indice, String color, int idJugador) {
        this.indice = indice;
        this.color = color;
        this.idJugador = idJugador;
        this.piedras = new HashMap<>();
    }

    /**
     * Regresa el índice del tablero.
     * @return El índice del tablero.
     */
    public int getIndice() {
        return indice;
    }

    /**
     * Obtiene el color que tiene el tablero.
     * @return El color del tablero ("negro" o "blanco").
     */
    public String getColor() {
        return color;
    }

    /**
     * Obtiene el propietario del tablero en ese momento.
     * @return El identificador del jugador del tablero.
     */
    public int getPropietario() {
        return idJugador;
    }

    /**
     * Regresa las piedras del tablero de un jugador.
     * @return HashMap con las posiciones y piedras del tablero.
     */
    public HashMap<Posicion, Piedra> getPiedras() {
        return piedras;
    }

    /**
     * Regresa la piedra en una posición específica (null si está vacía).
     * @param posicion Posición a consultar.
     * @return Piedra en esa posición (null si no hay ninguna).
     */
    public Piedra getPosPiedra(Posicion posicion) {
        return piedras.get(posicion);
    }

    /**
     * Añade una piedra al tablero usando su posición.
     * @param nuevaPiedra Piedra a agregar.
     */
    public void agregarPiedra(Piedra nuevaPiedra) {
        piedras.put(nuevaPiedra.getPosicion(), nuevaPiedra);
    }

    /**
     * Elimina una piedra del tablero.
     * @param piedraEliminada Piedra a eliminar.
     */
    public void eliminarPiedra(Piedra piedraEliminada) {
        piedras.remove(piedraEliminada.getPosicion());
    }

    /**
     * Actualiza la posición de una piedra.
     * @param anterior Posición previa de la piedra.
     * @param nueva Nueva posición de la piedra.
     * @param piedraMovida La piedra que se está moviendo.
     */
    public void actualizarPosPiedra(Posicion anterior, Posicion nueva, Piedra piedraMovida) {
        // Primero se borra la entrada anterior.
        piedras.remove(anterior);
        // Después se añade la nueva piedra con la posición actualizada.
        piedras.put(nueva, piedraMovida);
    }

}
