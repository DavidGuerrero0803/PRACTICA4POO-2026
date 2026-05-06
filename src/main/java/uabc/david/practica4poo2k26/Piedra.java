package uabc.david.practica4poo2k26;

/**
 * Esta clase representa una "piedra" del Shobu.
 * Se tiene un identificador del propietario (jugador 1 o 2) y su posición actual en el tablero.
 */
public class Piedra {
    private Posicion posicion;
    private int idPropietario;

    /**
     * Crea una piedra asignada a un propietario en una posición inicial.
     * @param idPropietario Identificador del jugador/dueño de la piedra.
     * @param posicion Posición inicial de la piedra en el tablero.
     */
    public Piedra(int idPropietario, Posicion posicion) {
        this.idPropietario = idPropietario;
        this.posicion = posicion;
    }

    /**
     * Regresa al dueño de la piedra.
     * @return El identificador del jugador/propietario de la piedra.
     */
    public int getPropietario() {
        return idPropietario;
    }

    /**
     * Obtiene la posición actual que tiene la piedra.
     * @return La posición actual de la piedra.
     */
    public Posicion getPosicion() {
        return posicion;
    }

    /**
     * Cambia la posición que tenga la piedra.
     * Permite actualizar la posición de la piedra tras un movimiento.
     * @param nuevaPosicion Posición nueva de la piedra.
     */
    public void setPosicion(Posicion nuevaPosicion) {
        this.posicion = nuevaPosicion;
    }

}
