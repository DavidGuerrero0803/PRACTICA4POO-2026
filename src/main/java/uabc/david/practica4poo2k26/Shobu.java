package uabc.david.practica4poo2k26;

import java.util.ArrayList;
import java.util.HashMap;

public class Shobu {
    private HashMap<String, Tablero> tableros;
    private String turnoActual;

    public static final String ARRIBA_IZQUIERDA = "ARRIBA_IZQUIERDA";
    public static final String ARRIBA_DERECHA   = "ARRIBA_DERECHA";
    public static final String ABAJO_IZQUIERDA  = "ABAJO_IZQUIERDA";
    public static final String ABAJO_DERECHA    = "ABAJO_DERECHA";

    private final String[] POSICIONES_TABLERO = {
      ARRIBA_IZQUIERDA, ARRIBA_DERECHA, ABAJO_IZQUIERDA, ABAJO_DERECHA
    };

    public Shobu() {
        this.tableros = new HashMap<>();
        this.turnoActual = Tablero.NEGRA;
        prepararJuego();
    }

    private void prepararJuego() {
        tableros.put(ARRIBA_IZQUIERDA, new Tablero(Tablero.COLOR_NEGRO));
        tableros.put(ARRIBA_DERECHA, new Tablero(Tablero.COLOR_BLANCO));
        tableros.put(ABAJO_IZQUIERDA, new Tablero(Tablero.COLOR_NEGRO));
        tableros.put(ABAJO_DERECHA, new Tablero(Tablero.COLOR_BLANCO));

        for (int columna = 0; columna < Tablero.TAMAÑO; columna++) {
            for (Tablero tablero : tableros.values()) {
                tablero.setPosicion(new Posicion(0, columna), Tablero.BLANCA);
                tablero.setPosicion(new Posicion(3, columna), Tablero.NEGRA);
            }
        }
    }

    public String getTurnoActual() {
        return this.turnoActual;
    }

    public void cambiarTurno() {
        turnoActual = turnoActual.equals(Tablero.NEGRA) ? Tablero.BLANCA : Tablero.NEGRA;
    }

    public int[] calcularVector(Posicion inicio, Posicion fin) {
        return new int[] {
                fin.getFila() - inicio.getFila(),
                fin.getColumna() - inicio.getColumna()
        };
    }

    public ArrayList<Posicion> obtenerTrayectoria(Posicion inicio, int[] vector) {
        ArrayList<Posicion> pasos = new ArrayList<>();

        int pasoFila    = (int) Math.signum(vector[0]);
        int pasoColumna = (int) Math.signum(vector[1]);
        int distancia   = Math.max(Math.abs(vector[0]), Math.abs(vector[1]));

        for (int i = 1; i <= distancia; i++) {
            pasos.add(new Posicion(
                    inicio.getFila() + pasoFila * i,
                    inicio.getColumna() + pasoColumna * i
            ));
        }

        return pasos;
    }

    public boolean esPasivoValido(Tablero tablero, Posicion inicio, Posicion fin) {
        int[] vector = calcularVector(inicio, fin);
        int distanciaFila = Math.abs(vector[0]);
        int distanciaColumna = Math.abs(vector[1]);

        if ((distanciaFila > 2 || distanciaColumna > 2) || (distanciaFila == 0 && distanciaColumna == 0)) {
            return false;
        }

        if (distanciaFila != 0 && distanciaColumna != 0 && distanciaFila != distanciaColumna) {
            return false;
        }

        for (Posicion paso : obtenerTrayectoria(inicio, vector)) {
            if (!tablero.estaVacia(paso)) {
                return false;
            }
        }

        return true;
    }

    public boolean esAgresivoValido(Tablero tablero, Posicion inicio, int[] vector) {
        ArrayList<Posicion> trayectoria = obtenerTrayectoria(inicio, vector);
        Posicion destino = trayectoria.get(trayectoria.size() - 1);

        if (!destino.estaEnRango()) {
            return false;
        }

        int piedrasEnemigas = 0;

        for (Posicion paso : trayectoria) {
            String contenido = tablero.getPosicion(paso);

            if (contenido.equals(turnoActual)) {
                return false;
            }

            if (!tablero.estaVacia(paso)) {
                piedrasEnemigas++;

                if (!paso.equals(destino)) {
                    return false;
                }
            }
        }

        if (piedrasEnemigas > 1) {
            return false;
        }

        if (piedrasEnemigas == 1) {
            validarEmpuje(tablero, destino, vector);
        }

        return true;
    }

    private boolean validarEmpuje(Tablero tablero, Posicion posEnemiga, int[] vector) {
        Posicion destinoEnemigo = new Posicion(
                posEnemiga.getFila() + (int) Math.signum(vector[0]),
                posEnemiga.getColumna() + (int) Math.signum(vector[1])
        );

        if (!destinoEnemigo.estaEnRango()){
            return true;
        }

        return tablero.estaVacia(destinoEnemigo);
    }

    public ArrayList<Posicion> obtenerMovimientosLegales(Tablero tableroPasivo, Posicion inicio) {
        ArrayList<Posicion> destinosValidos = new ArrayList<>();

        for (int fila = 0; fila < 4; fila++) {
            for (int columma = 0; columma < 4; columma++) {
                Posicion destinoCandidato = new Posicion(fila, columma);

                if (esPasivoValido(tableroPasivo, inicio, destinoCandidato)) {
                    int[] vector = calcularVector(inicio, destinoCandidato);

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

    public String verificarGanador() {
        String[] posicionesTableros = {"ARRIBA_IZQUIERDA", "ARRIBA_DERECHA", "ABAJO_IZQUIERDA", "ABAJO_DERECHA"};

        for (String nombre : posicionesTableros) {
            Tablero tablero = tableros.get(nombre);
            int negras = 0;
            int blancas = 0;

            for (int fila = 0; fila < 4; fila++) {
                for (int columna = 0; columna < 4; columna++) {
                    String posiciones = tablero.getPosicion(new Posicion(fila, columna));
                    if (posiciones.equals("N")) {
                        negras++;
                    }
                    if (posiciones.equals("B")) {
                        blancas++;
                    }
                }
            }

            if (negras == 0) {
                return "B";
            }
            if (blancas == 0) {
                return "N";
            }
        }

        return null;
    }

    public Tablero getTablero(String nombre) {
        return tableros.get(nombre);
    }
}
