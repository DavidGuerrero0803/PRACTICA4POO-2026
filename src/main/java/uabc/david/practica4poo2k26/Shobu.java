package uabc.david.practica4poo2k26;

import java.util.ArrayList;

public class Shobu {
    private ArrayList<Tablero> tableros;
    private ArrayList<Jugador> jugadores;
    private int turnoActual;


    public Shobu(Jugador jugador1, Jugador jugador2) {
        this.tableros = new ArrayList<>();
        this.jugadores = new ArrayList<>();
        this.jugadores.add(jugador1);
        this.jugadores.add(jugador2);
        this.turnoActual = 1;


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
            return validarEmpuje(tablero, destino, vector);
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

        for (int fila = 0; fila < Tablero.TAMAÑO; fila++) {
            for (int columna = 0; columna < Tablero.TAMAÑO; columna++) {
                Posicion candidato = new Posicion(fila, columna);

                if (esPasivoValido(tableroPasivo, inicio, candidato)) {
                    int[] vector = calcularVector(inicio, candidato);

                    if (hayEspejoValido(tableroPasivo, vector)) {
                        destinosValidos.add(candidato);
                    }
                }
            }
        }

        return destinosValidos;
    }

    private boolean hayEspejoValido(Tablero tableroPasivo, int[] vector) {
        String colorOpuesto = tableroPasivo.getColorOpuesto();

        for (Tablero tablero : tableros.values()) {
            if (tablero.getColor().equals(colorOpuesto)) {

                for (int fila = 0; fila < Tablero.TAMAÑO; fila++) {
                    for (int columna = 0; columna < Tablero.TAMAÑO; columna++) {
                        Posicion posicion = new Posicion(fila, columna);

                        if (tablero.getPosicion(posicion).equals(turnoActual) &&
                                esAgresivoValido(tablero, posicion, vector)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public void moverPieza(String nombreTablero, Posicion inicio, int[] vector) {
        Tablero tablero = tableros.get(nombreTablero);
        String piedra = tablero.getPosicion(inicio);

        tablero.setPosicion(inicio, Tablero.VACÍA);

        Posicion destino = new Posicion(
                inicio.getFila() + vector[0],
                inicio.getColumna() + vector[1]
        );

        if (!tablero.estaVacia(destino) && !tablero.getPosicion(destino).equals(piedra)) {
            aplicarEmpuje(tablero, destino, vector);
        }

        tablero.setPosicion(inicio, Tablero.VACÍA);
        tablero.setPosicion(destino, piedra);
    }

    private void aplicarEmpuje(Tablero tablero, Posicion posEnemigo, int[] vector) {
        String piedraEmpujada = tablero.getPosicion(posEnemigo);

        Posicion destinoEnemigo = new Posicion(
                posEnemigo.getFila() + (int) Math.signum(vector[0]),
                posEnemigo.getColumna() + (int) Math.signum(vector[1])
        );

        tablero.setPosicion(posEnemigo, Tablero.VACÍA);

        if (destinoEnemigo.estaEnRango()) {
            tablero.setPosicion(destinoEnemigo, piedraEmpujada);
        }

    }

    public String verificarGanador() {
        for (String nombre : POSICIONES_TABLERO) {
            Tablero tablero = tableros.get(nombre);
            int negras = 0;
            int blancas = 0;

            for (int fila = 0; fila < Tablero.TAMAÑO; fila++) {
                for (int columna = 0; columna < Tablero.TAMAÑO; columna++) {
                    String valorCelda = tablero.getPosicion(new Posicion(fila, columna));
                    if (valorCelda.equals(Tablero.NEGRA)) {
                        negras++;
                    }
                    if (valorCelda.equals(Tablero.BLANCA)) {
                        blancas++;
                    }
                }
            }

            if (negras == 0) {
                return Tablero.BLANCA;
            }
            if (blancas == 0) {
                return Tablero.NEGRA;
            }
        }

        return null;
    }

    public Tablero getTablero(String nombre) {
        return tableros.get(nombre);
    }
}
