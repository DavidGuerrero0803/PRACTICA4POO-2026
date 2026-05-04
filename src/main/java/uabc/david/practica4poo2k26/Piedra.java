package uabc.david.practica4poo2k26;

/**
 * Esta clase representa una "piedra" del Shobu.
 * Se tiene a su propietario (jugador 1 o 2) y su posición actual en el tablero.
 */
public class Piedra {
    private Posicion posicion;
    private int propietario;

    /**
     * Crea una piedra asignada a un propietario en una posición inicial.
     * @param propietario Identificador del jugador/dueño de la piedra.
     * @param posicion Posición inicial de la piedra en el tablero.
     */
    public Piedra(int propietario, Posicion posicion) {
        this.propietario = propietario;
        this.posicion = posicion;
    }

    /**
     * Regresa al dueño/jugador de la piedra.
     * @return El identificador del jugador/propietario de la piedra.
     */
    public int getPropietario() {
        return propietario;
    }

    /**
     * Obtiene la posición actual que tiene la piedra.
     * @return La posición actual de la piedra.
     */
    public Posicion getPosicion() {
        return posicion;
    }

    /**
     * Cambiar la posición que tenga la piedra.
     * Esto permite actualizar la posición de la piedra tras un movimiento.
     * @param posicion Nueva posición de la piedra.
     */
    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

}
