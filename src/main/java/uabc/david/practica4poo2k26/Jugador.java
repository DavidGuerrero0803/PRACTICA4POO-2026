package uabc.david.practica4poo2k26;

/**
 * Esta clase representa a un participante del Shobu.
 * Puede ser un jugador humano o controlado por la máquina.
 */
public class Jugador {
    private String nombre;
    private int identificador;
    private boolean esMaquina;

    /**
     * Crea un jugador con nombre, identificador y tipo (si es humano o máquina).
     * @param nombre Nombre del jugador.
     * @param identificador "ID" único del jugador (1 o 2).
     * @param esMaquina es true si es controlado por la computadora.
     */
    public Jugador(String nombre, int identificador, boolean esMaquina) {
        this.nombre = nombre;
        this.identificador = identificador;
        this.esMaquina = esMaquina;
    }

    /**
     * Regresa el nombre del jugador "BLANCO" O "NEGRO".
     * @return El nombre del jugador.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Regresa el ID del jugador.
     * @return El identificador del jugador (1 o 2).
     */
    public int getIdentificador() {
        return identificador;
    }

    /**
     * Booleano que da true o false si se trata de un humano o la computadora.
     * @return true si este jugador es controlado por la máquina, false si no lo es.
     */
    public boolean esMaquina() {
        return esMaquina;
    }

}
