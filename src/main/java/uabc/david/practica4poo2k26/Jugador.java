package uabc.david.practica4poo2k26;

public class Jugador {
    private String nombre;
    private int identificador;
    private boolean esMaquina;

    public Jugador(String nombre, int identificador, boolean esMaquina) {
        this.nombre = nombre;
        this.identificador = identificador;
        this.esMaquina = esMaquina;
    }

    public String getNombre() {
        return nombre;
    }

    public int getIdentificador() {
        return identificador;
    }

    public boolean esMaquina() {
        return esMaquina;
    }

}
