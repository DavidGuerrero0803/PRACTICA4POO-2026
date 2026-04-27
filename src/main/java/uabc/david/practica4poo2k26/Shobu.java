package uabc.david.practica4poo2k26;

import java.util.ArrayList;
import java.util.HashMap;

public class Shobu {
    private HashMap<String, Tablero> tableros;
    private String turnoActual;

    public Shobu() {
        this.tableros = new HashMap<>();
        this.turnoActual = "N";
        prepararJuego();
    }

    private void prepararJuego() {
        // Clave: Posición del tablero, valor: Color del tablero.
        tableros.put("ARRIBA_IZQUIERDA", new Tablero("NEGRO"));
        tableros.put("ARRIBA_DERECHA", new Tablero("BLANCO"));
        tableros.put("ABAJO_IZQUIERDA", new Tablero("NEGRO"));
        tableros.put("ABAJO_DERECHA", new Tablero("BLANCO"));

        for (int i = 0; i < 4; i++) {
            for (Tablero tablero : tableros.values()) {
                tablero.setPosicion(new Posicion(0, i), "N");
                tablero.setPosicion(new Posicion(3, i), "B");
            }
        }
    }

    public String getTurnoActual() {
        return this.turnoActual;
    }

    public void cambiarTurno() {
        if (this.turnoActual.equals("N")) {
            this.turnoActual = "B";
        } else {
            this.turnoActual = "N";
        }
    }

    public int[] obtenerVectorMovimiento(Posicion inicio, Posicion fin) {
        return new int[] {fin.getFila() - inicio.getFila(), fin.getColumna() - inicio.getColumna()};
    }

    public ArrayList<Posicion> obtenerTrayectoria(Posicion inicio, int[] vector) {
        ArrayList<Posicion> pasos = new ArrayList<>();

        int cambioFila = vector[0];
        int cambioColumna = vector[1];

        int pasoFila = 0;
        int pasoColumna = 0;

        if (cambioFila > 0) {
            pasoFila = 1;
        } else if (cambioFila < 0) {
            pasoFila = -1;
        }

        if (cambioColumna > 0) {
            pasoColumna = 1;
        } else if (cambioColumna < 0) {
            pasoColumna = -1;
        }

        int distanciaTotal = Math.max(Math.abs(cambioFila), Math.abs(cambioColumna));

        for (int i = 1; i <= distanciaTotal; i++) {
            int nuevaFila = inicio.getFila() + (pasoFila * i);
            int nuevaColumna = inicio.getColumna() + (pasoColumna * i);

            pasos.add(new Posicion(nuevaFila, nuevaColumna));
        }

        return pasos;
    }

    public boolean esPasivoValido(Tablero tablero, Posicion inicio, Posicion fin) {
        int[] vector = obtenerVectorMovimiento(inicio, fin);
        int distanciaFila = Math.abs(vector[0]);
        int distanciaColumna = Math.abs(vector[1]);

        if ((distanciaFila > 2 || distanciaColumna > 2) || (distanciaFila == 0 && distanciaColumna == 0)) {
            return false;
        }

        if (distanciaFila != 0 && distanciaColumna != 0 && distanciaFila != distanciaColumna) {
            return false;
        }

        ArrayList<Posicion> trayectoria = obtenerTrayectoria(inicio, vector);

        for (Posicion posicion : trayectoria) {
            if (!tablero.getPosicion(posicion).equals("V")) {
                return false;
            }
        }

        return true;
    }

    public boolean esAgresivoValido(Tablero tablero, Posicion inicio, int[] vector) {
        ArrayList<Posicion> trayectoria = obtenerTrayectoria(inicio, vector);
        Posicion destinoFinal = trayectoria.get(trayectoria.size() - 1);

        if (estaFueraDeRango(destinoFinal)) {
            return false;
        }

        int piezasEnemigasEnCamino = 0;

        for (Posicion posicion : trayectoria) {
            String contenido = tablero.getPosicion(posicion);

            if (contenido.equals(this.turnoActual)) {
                return false;
            }

            if (!contenido.equals("V") && !contenido.equals(this.turnoActual)) {
                piezasEnemigasEnCamino++;

                if (!posicion.equals(destinoFinal)) {
                    return false;
                }
            }
        }

        if (piezasEnemigasEnCamino > 1) {
            return false;
        }

        if (piezasEnemigasEnCamino == 1) {
            return validarEmpuje(tablero, destinoFinal, vector);
        }

        return true;
    }

    private boolean estaFueraDeRango(Posicion posicion) {
        return posicion.getFila() < 0 || posicion.getFila() > 3 || posicion.getColumna() < 0 || posicion.getColumna() > 3;
    }

    private boolean validarEmpuje(Tablero tablero, Posicion posEnemiga, int[] vector) {
        int pasoFila = (int) Math.signum(vector[0]);
        int pasoColumna = (int) Math.signum(vector[1]);

        int filaDestinoEnemigo = posEnemiga.getFila() + pasoFila;
        int colDestinoEnemigo = posEnemiga.getColumna() + pasoColumna;

        if (filaDestinoEnemigo < 0 || filaDestinoEnemigo > 3 ||
                colDestinoEnemigo < 0 || colDestinoEnemigo > 3) {
            return true;
        }

        Posicion destinoEnemigo = new Posicion(filaDestinoEnemigo, colDestinoEnemigo);
        if (tablero.getPosicion(destinoEnemigo).equals("V")) {
            return true;
        }

        return false;
    }

    public ArrayList<Posicion> obtenerMovimientosLegales(Tablero tableroPasivo, Posicion inicio) {
        ArrayList<Posicion> destinosValidos = new ArrayList<>();

        for (int fila = 0; fila < 4; fila++) {
            for (int columma = 0; columma < 4; columma++) {
                Posicion destinoCandidato = new Posicion(fila, columma);

                if (esPasivoValido(tableroPasivo, inicio, destinoCandidato)) {
                    int[] vector = obtenerVectorMovimiento(inicio, destinoCandidato);

                    if (hayEspejoValido(tableroPasivo, vector)) {
                        destinosValidos.add(destinoCandidato);
                    }
                }
            }
        }
        return destinosValidos;
    }

    private boolean hayEspejoValido(Tablero tableroPasivo, int[] vector) {
        String colorOpuesto = tableroPasivo.getColor().equals("BLANCO") ? "NEGRO" : "BLANCO";

        for (Tablero tablero : tableros.values()) {
            if (tablero.getColor().equals(colorOpuesto)) {

                for (int fila = 0; fila < 4; fila++) {
                    for (int columna = 0; columna < 4; columna++) {
                        Posicion posicionPieza = new Posicion(fila, columna);

                        if (tablero.getPosicion(posicionPieza).equals(this.turnoActual)) {
                            if (esAgresivoValido(tablero, posicionPieza, vector)) {

                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public void moverPieza(String nombreTablero, Posicion inicio, int[] vector) {
        Tablero tablero = tableros.get(nombreTablero);
        String pieza = tablero.getPosicion(inicio);

        tablero.setPosicion(inicio, "V");

        Posicion destino = new Posicion(inicio.getFila() + vector[0],
                inicio.getColumna() + vector[1]);

        if (!tablero.getPosicion(destino).equals("V") && !tablero.getPosicion(destino).equals(pieza)) {
            aplicarEmpujeLogico(tablero, destino, vector);
        }

        tablero.setPosicion(destino, pieza);
    }

    private void aplicarEmpujeLogico(Tablero tablero, Posicion posEnemigo, int[] vector) {
        int pasoFila = (int) Math.signum(vector[0]);
        int pasoColumna = (int) Math.signum(vector[1]);

        int nuevaFila = posEnemigo.getFila() + pasoFila;
        int nuevaCol = posEnemigo.getColumna() + pasoColumna;

        if (nuevaFila < 0 || nuevaFila > 3 || nuevaCol < 0 || nuevaCol > 3) {

        } else {

            tablero.setPosicion(new Posicion(nuevaFila, nuevaCol), tablero.getPosicion(posEnemigo));
        }
    }

    public Tablero getTablero(String nombre) {
        return tableros.get(nombre);
    }
}
