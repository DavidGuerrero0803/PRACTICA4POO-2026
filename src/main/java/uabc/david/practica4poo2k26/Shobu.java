package uabc.david.practica4poo2k26;

import java.util.ArrayList;

public class Shobu {
    private ArrayList<Tablero> tableros;
    private ArrayList<Jugador> jugadores;
    private int turnoActual;
    private Movimiento movimientoPasivo;

    public Shobu(Jugador jugador1, Jugador jugador2) {
        this.tableros = new ArrayList<>();
        this.jugadores = new ArrayList<>();
        this.jugadores.add(jugador1);
        this.jugadores.add(jugador2);
        this.turnoActual = 1;
        this.movimientoPasivo = null;

        inicializarTableros();
        colocarPiedrasIniciales();
    }

    private void inicializarTableros() {
        tableros.add(new Tablero(0, "negro", 2));
        tableros.add(new Tablero(1, "blanco", 2));
        tableros.add(new Tablero(2, "negro", 1));
        tableros.add(new Tablero(3, "blanco", 1));
    }

    private void colocarPiedrasIniciales() {
        for (int columna = 0; columna < 4; columna++) {
            final int COLUMNA = columna;
            tableros.forEach(tablero -> {
                tablero.agregarPiedra(new Piedra(1, new Posicion(3, COLUMNA)));
                tablero.agregarPiedra(new Piedra(2, new Posicion(0, COLUMNA)));
            });
        }
    }

    public Jugador getJugadorActual() {
        return jugadores.get(turnoActual - 1);
    }

    public void finalizarTurno() {
        turnoActual = (turnoActual == 1) ? 2 : 1;
    }

    private boolean esMovimientoValido(Movimiento movimiento, Tablero tablero) {
        Posicion origen = movimiento.getOrigen();
        Posicion destino = movimiento.getDestino();

        if (destino.getFila() < 0 || destino.getFila() > 3 || destino.getColumna() < 0 || destino.getColumna() > 3) {
            return false;
        }

        int distancia = movimiento.getDistancia();
        if (distancia < 1 || distancia > 2) {
            return false;
        }

        Piedra piedraOrigen = tablero.getPiedraEnPosicion(origen);
        if (piedraOrigen == null || piedraOrigen.getPropietario() != turnoActual) {
            return false;
        }

        if (distancia == 2) {
            Posicion intermedia = new Posicion(
                    origen.getFila() + movimiento.getDeltaFila(),
                    origen.getColumna() + movimiento.getDeltaColumna()
            );
            if (tablero.getPiedraEnPosicion(intermedia) != null) {
                return false;
            }
        }
        return true;
    }

    private boolean esEmpujeValido(Movimiento movimiento, Tablero tablero) {
        Posicion destino = movimiento.getDestino();
        Piedra piedraDestino = tablero.getPiedraEnPosicion(destino);

        if (piedraDestino == null) {
            return true;
        }
        if (piedraDestino.getPropietario() == turnoActual) {
            return false;
        }

        Posicion detras = new Posicion(
                destino.getFila() + movimiento.getDeltaFila(),
                destino.getColumna() + movimiento.getDeltaColumna()
        );

        if (detras.getFila() < 0 || detras.getFila() > 3 || detras.getColumna() < 0 || detras.getColumna() > 3) {
            return true;
        }
        return tablero.getPiedraEnPosicion(detras) == null;
    }

    private boolean esTableroAgresivoValido(int indiceTablero) {
        return !tableros.get(movimientoPasivo.getIndiceTablero()).getColor()
                .equals(tableros.get(indiceTablero).getColor());
    }

    public ArrayList<Posicion> obtenerMovimientosPasivosValidos(Posicion origen, int indiceTablero) {
        ArrayList<Posicion> posicionesValidas = new ArrayList<>();
        Tablero tablero = tableros.get(indiceTablero);

        int[] deltas = {-1, 0, 1};
        for (int deltaFila : deltas) {
            for (int deltaColumna : deltas) {
                if (deltaFila == 0 && deltaColumna == 0) {
                    continue;
                }
                for (int distancia = 1; distancia <= 2; distancia++) {
                    Posicion destino = new Posicion(
                            origen.getFila() + deltaFila * distancia,
                            origen.getColumna() + deltaColumna * distancia
                    );
                    Movimiento movimiento = new Movimiento(indiceTablero, origen, destino, true);

                    if (!esMovimientoValido(movimiento, tablero)) {
                        continue;
                    }
                    if (tablero.getPiedraEnPosicion(destino) != null) {
                        continue;
                    }

                    if (existeReplicaAgresivaValida(movimiento)) {
                        posicionesValidas.add(destino);
                    }
                }
            }
        }

        return posicionesValidas;
    }

    private boolean existeReplicaAgresivaValida(Movimiento movimientoPasivoTentativo) {
        String colorPasivo = tableros.get(movimientoPasivoTentativo.getIndiceTablero()).getColor();

        for (Tablero tablero : tableros) {
            if (tablero.getColor().equals(colorPasivo)) {
                continue;
            }

            for (Piedra piedra : tablero.getPiedras().values()) {
                if (piedra.getPropietario() != turnoActual) {
                    continue;
                }

                Posicion origen = piedra.getPosicion();
                int deltaFila = movimientoPasivoTentativo.getDeltaFila();
                int deltaColumna = movimientoPasivoTentativo.getDeltaColumna();
                int distancia = movimientoPasivoTentativo.getDistancia();

                Posicion destino = new Posicion(
                        origen.getFila() + deltaFila * distancia,
                        origen.getColumna() + deltaColumna * distancia
                );

                Movimiento movimientoAgresivo = new Movimiento(
                        tablero.getIndice(), origen, destino, false
                );

                if (esMovimientoValido(movimientoAgresivo, tablero) &&
                        esEmpujeValido(movimientoAgresivo, tablero)) {
                    return true;
                }
            }
        }
        return false;
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
