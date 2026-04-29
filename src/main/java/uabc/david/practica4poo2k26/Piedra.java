package uabc.david.practica4poo2k26;

public class Piedra {
    private int propietario;
    private Posicion posicion;

    public Piedra(int propietario, Posicion posicion) {
        this.propietario = propietario;
        this.posicion = posicion;
    }

    public int getPropietario() {
        return propietario;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public void setPosicion(Posicion posicion) {
        this.posicion = posicion;
    }

}
