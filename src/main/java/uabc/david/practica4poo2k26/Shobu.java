package uabc.david.practica4poo2k26;

import java.util.ArrayList;

public class Shobu {
    private ArrayList<Tablero> tableros;
    private ArrayList<Jugador> jugadores;
    private int turnoActual;
    private Movimiento movimientoPasivo;
    private boolean pasivoRealizado;

    public Shobu(Jugador jugador1, Jugador jugador2) {
        this.tableros = new ArrayList<>();
        this.jugadores = new ArrayList<>();
        this.jugadores.add(jugador1);
        this.jugadores.add(jugador2);
        this.turnoActual = 1;
        this.movimientoPasivo = null;
        this.pasivoRealizado = false;

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

    public boolean pasivoRealizado() {
        return pasivoRealizado;
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

    public ArrayList<Posicion> obtenerMovimientosAgresivosValidos(Posicion origen, int indiceTablero) {
        ArrayList<Posicion> posicionesValidas = new ArrayList<>();

        if (!esTableroAgresivoValido(indiceTablero)) {
            return posicionesValidas;
        }

        Tablero tablero = tableros.get(indiceTablero);
        int deltaFila = movimientoPasivo.getDeltaFila();
        int deltaColumna = movimientoPasivo.getDeltaColumna();
        int distancia = movimientoPasivo.getDistancia();

        Posicion destino = new Posicion(
                origen.getFila() + deltaFila * distancia,
                origen.getColumna() + deltaColumna * distancia
        );

        Movimiento movimiento = new Movimiento(indiceTablero, origen, destino, false);
        if (esMovimientoValido(movimiento, tablero) && esEmpujeValido(movimiento, tablero)) {
            posicionesValidas.add(destino);
        }

        return posicionesValidas;
    }

    public boolean realizarMovimientoPasivo(Movimiento movimiento) {
        Tablero tablero = tableros.get(movimiento.getIndiceTablero());
        if (tablero.getPropietario() != turnoActual || !esMovimientoValido(movimiento, tablero)) {
            return false;
        }
        if (tablero.getPiedraEnPosicion(movimiento.getDestino()) != null) {
            return false;
        }

        actualizarMapaTablero(tablero, movimiento);
        movimientoPasivo = movimiento;
        pasivoRealizado = true;
        return true;
    }

    public boolean realizarMovimientoAgresivo(Movimiento movimiento) {
        if (!pasivoRealizado || !esTableroAgresivoValido(movimiento.getIndiceTablero())) return false;

        Tablero tablero = tableros.get(movimiento.getIndiceTablero());

        if (movimiento.getDeltaFila() != movimientoPasivo.getDeltaFila() ||
                movimiento.getDeltaColumna() != movimientoPasivo.getDeltaColumna() ||
                movimiento.getDistancia() != movimientoPasivo.getDistancia()) {
            return false;
        }

        if (!esMovimientoValido(movimiento, tablero) || !esEmpujeValido(movimiento, tablero)) {
            return false;
        }

        Posicion destino = movimiento.getDestino();
        Piedra piedraEnemiga = tablero.getPiedraEnPosicion(destino);

        if (piedraEnemiga != null) {
            Posicion nuevaPosEnemiga = new Posicion(
                    destino.getFila() + movimiento.getDeltaFila(),
                    destino.getColumna() + movimiento.getDeltaColumna()
            );
            if (nuevaPosEnemiga.getFila() < 0 || nuevaPosEnemiga.getFila() > 3 ||
                    nuevaPosEnemiga.getColumna() < 0 || nuevaPosEnemiga.getColumna() > 3) {
                tablero.eliminarPiedra(piedraEnemiga);
            } else {
                tablero.actualizarPosicionPiedra(destino, nuevaPosEnemiga, piedraEnemiga);
                piedraEnemiga.setPosicion(nuevaPosEnemiga);
            }
        }

        actualizarMapaTablero(tablero, movimiento);
        pasivoRealizado = false;
        movimientoPasivo = null;
        return true;
    }

    private void actualizarMapaTablero(Tablero tablero, Movimiento movimiento) {
        Piedra piedra = tablero.getPiedraEnPosicion(movimiento.getOrigen());
        tablero.actualizarPosicionPiedra(movimiento.getOrigen(), movimiento.getDestino(), piedra);
        piedra.setPosicion(movimiento.getDestino());
    }

    public boolean hayGanador() {
        return tableros.stream().anyMatch(tablero -> {
            int propietario = tablero.getPropietario();
            return tablero.getPiedras().values().stream()
                    .noneMatch(piedra -> piedra.getPropietario() == propietario);
        });
    }

    public Jugador getGanador() {
        for (Tablero tablero : tableros) {
            boolean propietarioSinPiedras = true;
            for (Piedra piedra : tablero.getPiedras().values()) {
                if (piedra.getPropietario() == tablero.getPropietario()) {
                    propietarioSinPiedras = false;
                    break;
                }
            }
            if (propietarioSinPiedras) {
                int idGanador = (tablero.getPropietario() == 1) ? 2 : 1;
                return jugadores.get(idGanador - 1);
            }
        }
        return null;
    }

    public void realizarMovimientoMaquina() {
        ArrayList<int[]> opcionesPasivo = new ArrayList<>();

        for (Tablero tablero : tableros) {
            if (tablero.getPropietario() == turnoActual) {
                for (Piedra piedra : tablero.getPiedras().values()) {
                    if (piedra.getPropietario() == turnoActual) {
                        ArrayList<Posicion> validos = obtenerMovimientosPasivosValidos(
                                piedra.getPosicion(), tablero.getIndice()
                        );
                        for (Posicion destino : validos) {
                            opcionesPasivo.add(new int[]{
                                    tablero.getIndice(),
                                    piedra.getPosicion().getFila(),
                                    piedra.getPosicion().getColumna(),
                                    destino.getFila(),
                                    destino.getColumna()
                            });
                        }
                    }
                }
            }
        }

        if (opcionesPasivo.isEmpty()) {
            return;
        }

        int[] opcion = opcionesPasivo.get((int)(Math.random() * opcionesPasivo.size()));
        Movimiento pasivo = new Movimiento(
                opcion[0],
                new Posicion(opcion[1], opcion[2]),
                new Posicion(opcion[3], opcion[4]),
                true
        );
        realizarMovimientoPasivo(pasivo);

        for (Tablero tablero : tableros) {
            if (esTableroAgresivoValido(tablero.getIndice())) {
                for (Piedra piedra : tablero.getPiedras().values()) {
                    if (piedra.getPropietario() == turnoActual) {
                        ArrayList<Posicion> validos = obtenerMovimientosAgresivosValidos(
                                piedra.getPosicion(), tablero.getIndice()
                        );
                        if (!validos.isEmpty()) {
                            Movimiento agresivo = new Movimiento(
                                    tablero.getIndice(),
                                    piedra.getPosicion(),
                                    validos.get(0),
                                    false
                            );
                            realizarMovimientoAgresivo(agresivo);
                            return;
                        }
                    }
                }
            }
        }
    }

    public ArrayList<Tablero> getTableros() {
        return tableros;
    }
}
