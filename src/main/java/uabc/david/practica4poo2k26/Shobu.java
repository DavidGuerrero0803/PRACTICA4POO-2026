package uabc.david.practica4poo2k26;

import java.util.ArrayList;

/**
 * Esta clase maneja la lógica del Shobu.
 * Maneja los 4 tableros, los 2 jugadores, los turnos y las reglas del juego.
 */
public class Shobu {
    private ArrayList<Tablero> tableros;
    private ArrayList<Jugador> jugadores;
    private Movimiento movimientoPasivo;
    private boolean pasivoRealizado;
    private int turnoActual;

    /**
     * Inicializa una partida de Shobu con dos jugadores.
     * Crea los tableros y coloca las piedras iniciales.
     * @param jugador1 Primer jugador (color negro, con el ID: 1).
     * @param jugador2 Segundo jugador (color blanco, con el ID: 2).
     */
    public Shobu(Jugador jugador1, Jugador jugador2) {
        this.tableros = new ArrayList<>();
        this.jugadores = new ArrayList<>();
        this.jugadores.add(jugador1);
        this.jugadores.add(jugador2);
        // Por simplicidad, el jugador que siempre empezará será el de color negro.
        this.turnoActual = 1;
        this.movimientoPasivo = null;
        this.pasivoRealizado = false;
        inicializarTableros();
        colocarPiedras();
    }

    /**
     * Crea los 4 tableros del juego.
     * Los tableros 0 y 2 son los de color negro.
     * Los tableros 1 y 3 son los de color blanco.
     * Los tableros 2 y 3 al jugador 1 (negro), mientras que
     * los tableros 0 y 1 pertenecen al jugador 2 (blanco).
     */
    private void inicializarTableros() {
        tableros.add(new Tablero(0, "negro", 2));
        tableros.add(new Tablero(1, "blanco", 2));
        tableros.add(new Tablero(2, "negro", 1));
        tableros.add(new Tablero(3, "blanco", 1));
    }

    /**
     * Coloca las piedras iniciales en todos los tableros.
     * Jugador 1 ocupa la fila 3 (inferior) y el jugador 2 la fila 0 (superior).
     */
    private void colocarPiedras() {
        for (int columna = 0; columna < 4; columna++) {
            // La variable columna se hace final para poder usarse en la lambda.
            final int COLUMNA = columna;
            // El forEach hace que cada tablero que pase, realice la instrucción interna.
            tableros.forEach(tablero -> {
                // Agrega la piedra del Jugador 1 en la fila 3 de la columna actual.
                tablero.agregarPiedra(new Piedra(1, new Posicion(3, COLUMNA)));
                // Agrega la piedra del Jugador 2 en la fila 0 de la columna actual.
                tablero.agregarPiedra(new Piedra(2, new Posicion(0, COLUMNA)));
            });
        }
    }

    /**
     * Se valida si un movimiento es legal, validando lo siguiente:
     * Si la distancia es de 1 o de 2.
     * No hay obstáculos intermedios en movimientos de distancia 2.
     * @param movimiento Movimiento a validar.
     * @param tablero Tablero donde se realiza el movimiento.
     * @return true si el movimiento es válido, false en caso de que no lo sea.
     */
    private boolean validarMovimiento(Movimiento movimiento, Tablero tablero) {
        Posicion origen = movimiento.getOrigen();
        Posicion destino = movimiento.getDestino();

        // Verifica si el destino está dentro de los límites del tablero 4x4.
        if (destino.getFila() < 0 || destino.getFila() > 3 || destino.getColumna() < 0 || destino.getColumna() > 3) {
            return false;
        }

        // Verifica si el movimiento es de 1 o 2 casillas.
        int distancia = movimiento.getDistancia();
        if (distancia < 1 || distancia > 2) {
            return false;
        }

        Piedra piedraOrigen = tablero.getPosPiedra(origen);
        // Verifica si existe una piedra propia en la posición de origen.
        if (piedraOrigen == null || piedraOrigen.getPropietario() != turnoActual) {
            return false;
        }

        // Verifica el movimiento como válido si
        // la distancia es 2 y la casilla intermedia está libre.
        if (distancia == 2) {
            // Se obtiene la posición intermedia.
            Posicion intermedia = new Posicion(
                    origen.getFila() + movimiento.getDeltaFila(),
                    origen.getColumna() + movimiento.getDeltaColumna()
            );
            // Se realiza la condición para saber si el movimiento de 2 es válido.
            if (tablero.getPosPiedra(intermedia) != null) {
                // En caso de cumplirla, el movimiento no es válido.
                return false;
            }
        }
        // Si con las condiciones no se detiene, se tomará como movimiento válido.
        return true;
    }

    /**
     * Se valida si es posible empujar la piedra en un movimiento agresivo.
     * @param movimiento Movimiento agresivo a validar
     * @param tablero Tablero donde ocurre el empuje
     * @return true si el empuje es válido o no hay nada que empujar, false en caso contrario.
     */
    private boolean validarEmpuje(Movimiento movimiento, Tablero tablero) {
        Posicion destino = movimiento.getDestino();
        Piedra piedraDestino = tablero.getPosPiedra(destino);

        // Esta condición permite saber si la casilla está o no vacía.
        if (piedraDestino == null) {
            // Si lo está, entonces no hay nada que empujar.
            return true;
        }
        // Esta condición permite saber si es una casilla propia.
        if (piedraDestino.getPropietario() == turnoActual) {
            // Regresará false ya que no se puede empujar una casilla propia.
            return false;
        }

        // Calcula la posición de la piedra empujada.
        Posicion posEmpuje = new Posicion(
                destino.getFila() + movimiento.getDeltaFila(),
                destino.getColumna() + movimiento.getDeltaColumna()
        );

        // La condición existe para saber si la posición queda fuera del tablero.
        if (posEmpuje.getFila() < 0 || posEmpuje.getFila() > 3 || posEmpuje.getColumna() < 0 || posEmpuje.getColumna() > 3) {
            // Si la piedra cae, entonces el empuje será válido.
            return true;
        }
        // Regresará true o false dependiendo si queda espacio libre detrás.
        return tablero.getPosPiedra(posEmpuje) == null;
    }

    /**
     * Verifica que el tablero agresivo sea de color opuesto al tablero pasivo.
     * @param indiceTablero Índice del tablero agresivo.
     * @return true si el tablero es de color opuesto al pasivo, false en caso de que no.
     */
    private boolean validarTableroAgresivo(int indiceTablero) {
        return !tableros.get(movimientoPasivo.getIndiceTablero()).getColor()
                .equals(tableros.get(indiceTablero).getColor());
    }

    /**
     * Devuelve las posiciones válidas para un movimiento pasivo desde una posición dada.
     * @param origen Posición de la piedra que se quiere mover.
     * @param indiceTablero Índice del tablero de un jugador.
     * @return ArrayList de posiciones destino válidas para el movimiento pasivo.
     */
    public ArrayList<Posicion> getMovimientosPasivos(Posicion origen, int indiceTablero) {
        ArrayList<Posicion> posicionesValidas = new ArrayList<>();
        Tablero tablero = tableros.get(indiceTablero);

        // Se crea un arreglo que contiene las diferencias de dirección.
        int[] deltas = {-1, 0, 1};
        // Se prueban todas las direcciones posibles (8 direcciones + distancias 1 y 2).
        for (int deltaFila : deltas) {
            for (int deltaColumna : deltas) {
                // Si ambos deltas son 0, significa que la piedra no se mueve.
                if (deltaFila == 0 && deltaColumna == 0) {
                    // En caso de ser verdadero, se ignora.
                    continue;
                }
                // Este ciclo indica que se permite mover 1 o 2 espacios.
                for (int distancia = 1; distancia <= 2; distancia++) {
                    // El destino se calcula multiplicando la dirección por la distancia.
                    Posicion destino = new Posicion(
                            origen.getFila() + deltaFila * distancia,
                            origen.getColumna() + deltaColumna * distancia
                    );
                    Movimiento movimiento = new Movimiento(indiceTablero, origen, destino, true);

                    // Verifica si el destino está dentro del tablero y el camino está despejado.
                    if (!validarMovimiento(movimiento, tablero)) {
                        continue;
                    }
                    // Verifica que un movimiento pasivo no pueda terminar en una casilla ocupada.
                    if (tablero.getPosPiedra(destino) != null) {
                        continue;
                    }

                    // Se valida si el movimiento pasivo es legal, teniendo en cuenta
                    // que ese mismo movimiento debe ser replicable en un movimiento agresivo.
                    if (validarAtaqueAgresivo(movimiento)) {
                        posicionesValidas.add(destino);
                    }
                }
            }
        }
        return posicionesValidas;
    }

    /**
     * Se verifica si existe al menos un movimiento agresivo válido.
     * Esto asegura que el movimiento anterior (pasivo) elegido sea "jugable".
     * @param posiblePasivo Movimiento pasivo que se está evaluando.
     * @return true si hay al menos un ataque agresivo posible con esa misma dirección.
     */
    private boolean validarAtaqueAgresivo(Movimiento posiblePasivo) {
        // El String recoge el color del tablero en donde se hizo el movimiento pasivo.
        String colorPasivo = tableros.get(posiblePasivo.getIndiceTablero()).getColor();

        // El for recorre los 4 tableros.
        for (Tablero tablero : tableros) {
            // Se aplica la condición de que el movimiento agresivo sea en un color opuesto.
            if (tablero.getColor().equals(colorPasivo)) {
                // En caso de ser del mismo color, es ignorado y pasa a lo siguiente.
                continue;
            }

            // Ahora se revisa cada una de las piedras presentes en eñ tablero enemigo.
            for (Piedra piedra : tablero.getPiedras().values()) {
                // Esta condición permite conocer las piedras que pertenecen al jugador actual.
                if (piedra.getPropietario() != turnoActual) {
                    continue;
                }

                // Se intenta aplicar la misma dirección y distancia del movimiento pasivo.
                Posicion origen = piedra.getPosicion();
                int deltaFila = posiblePasivo.getDeltaFila();
                int deltaColumna = posiblePasivo.getDeltaColumna();
                int distancia = posiblePasivo.getDistancia();

                Posicion destino = new Posicion(
                        origen.getFila() + deltaFila * distancia,
                        origen.getColumna() + deltaColumna * distancia
                );

                // Se crea el movimiento agresivo, en donde se le indica al último parámetro
                // de que no es un movimiento pasivo, poniendo "false".
                Movimiento movimientoAgresivo = new Movimiento(
                        tablero.getIndice(), origen, destino, false
                );

                // Finalmente, se verifica si el destino está dentro del tablero y el camino está libre,
                // además se verifica que si hay una piedra enemiga en el camino, esta se puede empujar.
                if (validarMovimiento(movimientoAgresivo, tablero) &&
                        validarEmpuje(movimientoAgresivo, tablero)) {
                    // Si el movimiento pasivo es posible de replicar, entonces el agresivo será válido.
                    return true;
                }
            }
        }
        // Si al final de todas las verificaciones no se encontró una réplica válida,
        // entonces es que no hay un movimiento agresivo.
        return false;
    }

    /**
     * Devuelve las posiciones válidas para el movimiento agresivo desde una posición dada.
     * El movimiento agresivo debe usar la misma dirección y distancia que el pasivo ya realizado.
     * @param origen Posición de la piedra que se quiere mover agresivamente.
     * @param indiceTablero Índice del tablero rival (de color opuesto al pasivo).
     * @return ArrayList de posiciones destino válidas, o vacía si no hay movimiento posible.
     */
    public ArrayList<Posicion> getMovimientosAgresivos(Posicion origen, int indiceTablero) {
        ArrayList<Posicion> posicionesValidas = new ArrayList<>();

        // Primero se verifica si el tablero agresivo es de color opuesto al tablero pasivo.
        if (!validarTableroAgresivo(indiceTablero)) {
            // Si el color no coincide, entonces no hay movimientos posibles.
            return posicionesValidas;
        }

        // Se manejan la dirección y distancia del movimiento pasivo que se ha estado usando.
        Tablero tablero = tableros.get(indiceTablero);
        int deltaFila = movimientoPasivo.getDeltaFila();
        int deltaColumna = movimientoPasivo.getDeltaColumna();
        int distancia = movimientoPasivo.getDistancia();

        // Basado en el pasivo, solo habrá un posible destino para la piedra seleccionada.
        Posicion destino = new Posicion(
                origen.getFila() + deltaFila * distancia,
                origen.getColumna() + deltaColumna * distancia
        );

        // Al ser el segundo movimiento del turno (el agresivo), se debe indicar que entonces no es pasivo.
        Movimiento movimientoAgresivo = new Movimiento(indiceTablero, origen, destino, false);

        // Nuevamente, se valida si un movimiento es agresivo si cumple 2 condiciones:
        // Que no salga del tablero y no hay nada que lo obstruya.
        // Si hay una piedra enemiga en el camino, que esta pueda empujarse.
        if (validarMovimiento(movimientoAgresivo, tablero) && validarEmpuje(movimientoAgresivo, tablero)) {
            posicionesValidas.add(destino);
        }

        // Devuelve el ArrayList con el movimiento hecho.
        return posicionesValidas;
    }

    /**
     * Ejecuta el movimiento pasivo del turno actual.
     * Solo es válido en tableros propios del jugador, sin empuje y con destino válido.
     * Registra el movimiento para que el movimiento agresivo lo replique.
     * @param pasivo Movimiento pasivo a realizar.
     * @return true si el movimiento fue ejecutado con éxito, false en caso de que no.
     */
    public boolean hacerMovimientoPasivo(Movimiento pasivo) {
        // Se obtiene el tablero en donde el jugador hizo clic.
        Tablero tablero = tableros.get(pasivo.getIndiceTablero());

        // El movimiento no se hará en caso de:
        // El tablero no pertenezca al jugador actual.
        // El movimiento haga que esté fuera de los límites o pase sobre otras piedras.
        if (tablero.getPropietario() != turnoActual || !validarMovimiento(pasivo, tablero)) {
            return false;
        }
        // Se verifica que un movimiento pasivo termine en una casilla vacía.
        if (tablero.getPosPiedra(pasivo.getDestino()) != null) {
            return false;
        }

        // Sí pasó todas las condiciones, el tablero se actualiza
        // con la piedra situada en su nueva posición.
        actualizarTablero(tablero, pasivo);
        // La variable "movimientoPasivo" pasa a guardar la información de "pasivo".
        movimientoPasivo = pasivo;
        // El booleano se marca como true al haber concluído el turno.
        pasivoRealizado = true;
        return true;
    }

    /**
     * Actualiza la posición de una piedra en el tablero tras un movimiento.
     * Modifica tanto el tablero como el atributo posición de la piedra.
     * @param tablero Tablero donde ocurre el movimiento.
     * @param movimiento Movimiento realizado.
     */
    private void actualizarTablero(Tablero tablero, Movimiento movimiento) {
        Piedra piedra = tablero.getPosPiedra(movimiento.getOrigen());
        tablero.actualizarPosPiedra(movimiento.getOrigen(), movimiento.getDestino(), piedra);
        piedra.setPosicion(movimiento.getDestino());
    }

    /**
     * Ejecuta el movimiento agresivo del turno actual.
     * Replica exactamente la dirección y distancia del movimiento pasivo.
     * @param agresivo Movimiento agresivo a realizar
     * @return true si el movimiento fue ejecutado con éxito
     */
    public boolean hacerMovimientoAgresivo(Movimiento agresivo) {
        // La condición regresa un false en caso de que:
        // No se haya completado el movimiento pasivo.
        // Si el tablero elegido no es del color opuesto al anterior.
        if (!pasivoRealizado || !validarTableroAgresivo(agresivo.getIndiceTablero())) {
            return false;
        }

        Tablero tablero = tableros.get(agresivo.getIndiceTablero());

        // Se realiza una comparación del movimiento actual con el movimientoPasivo.
        // Deben coincidir exactamente en dirección y distancia.
        if (agresivo.getDeltaFila() != movimientoPasivo.getDeltaFila() ||
                agresivo.getDeltaColumna() != movimientoPasivo.getDeltaColumna() ||
                agresivo.getDistancia() != movimientoPasivo.getDistancia()) {
            // En caso de que no coincidan, regresará un false.
            return false;
        }

        // Se verifica que el camino esté libre de piedras,
        // y en caso de que haya una enemiga, esta pueda ser empujada (si se puede).
        if (!validarMovimiento(agresivo, tablero) || !validarEmpuje(agresivo, tablero)) {
            return false;
        }

        Posicion destino = agresivo.getDestino();
        Piedra piedraEnemiga = tablero.getPosPiedra(destino);

        if (piedraEnemiga != null) {
            // Se calcula en dónde terminará la piedra enemiga tras ser empujada.
            Posicion nuevaPosEnemiga = new Posicion(
                    destino.getFila() + agresivo.getDeltaFila(),
                    destino.getColumna() + agresivo.getDeltaColumna()
            );
            // En caso de que la nueva posición está fuera de los límites,
            // la piedra se eliminará del juego.
            if (nuevaPosEnemiga.getFila() < 0 || nuevaPosEnemiga.getFila() > 3 ||
                    nuevaPosEnemiga.getColumna() < 0 || nuevaPosEnemiga.getColumna() > 3) {
                tablero.eliminarPiedra(piedraEnemiga);
            } else {
                // Si aún está dentro de los márgenes, actualiza la posición de la piedra enemiga.
                tablero.actualizarPosPiedra(destino, nuevaPosEnemiga, piedraEnemiga);
                piedraEnemiga.setPosicion(nuevaPosEnemiga);
            }
        }

        // Finalmente, se mueve la piedra del jugador a la casilla destino.
        actualizarTablero(tablero, agresivo);
        // Se resetea el estado de la fase pasiva.
        pasivoRealizado = false;
        // La variable movimientoPasivo vuelve a su "valor" original.
        movimientoPasivo = null;
        return true;
    }

    /**
     * Cambia el turno al jugador contrario al finalizar un turno completo.
     * Solamente se utiliza al momento de hacer un movimiento agresivo.
     */
    public void finalizarTurno() {
        // 1 = negro, 2 = blanco.
        // Si el turnoActual es 1, será cambiado al 2. De lo contrario, cambiará a 1.
        turnoActual = (turnoActual == 1) ? 2 : 1;
    }

    /**
     * Comprueba si algún jugador ha perdido todas sus piedras en algún tablero propio.
     * @return true si hay un ganador, false en caso de que aún no lo haya.
     */
    public boolean hayGanador() {
        // El ArrayList pasa por un flujo para llegar a un anyMatch, este sirve para
        // encontrar al menos un tablero que cumpla con la condición interior.
        return tableros.stream().anyMatch(tablero -> {
            // Se obtiene al dueño del tablero actual.
            int propietario = tablero.getPropietario();
            // Ahora el HashMap del tablero pasa por un nuevo flujo, dentro de él
            // se usa noneMatch para saber si ya no hay ninguna piedra en uno de sus tableros.
            return tablero.getPiedras().values().stream()
                    .noneMatch(piedra -> piedra.getPropietario() == propietario);
        });
    }

    /**
     * Identifica y regresa al jugador ganador.
     * @return El jugador ganador, o null si es que aún no hay un ganador.
     */
    public Jugador getGanador() {
        // Se recorren cada uno de los 4 tableros del juego.
        for (Tablero tablero : tableros) {
            // Se asume que el dueño del tablero ya no tiene piedras.
            boolean propietarioSinPiedras = true;
            // Se revisan todas las piedras que hay en el HashMap del tablero actual.
            for (Piedra piedra : tablero.getPiedras().values()) {
                // Si hay al menos una piedra que pertenezca al dueño del tablero,
                // significa que todavía no ha perdido en este tablero.
                if (piedra.getPropietario() == tablero.getPropietario()) {
                    // El booleano pasa ahora a ser false.
                    propietarioSinPiedras = false;
                    // Se deja de revisar el tablero y busca en el siguiente.
                    break;
                }
            }
            // Si aun el booleano sigue siendo true, pasa al interior de la condición.
            if (propietarioSinPiedras) {
                // Si el dueño del tablero perdió sus piedras, entonces el ganador es el otro jugador.
                // Se consigue el ID para darle la victoria al jugador opuesto.
                int idGanador = (tablero.getPropietario() == 1) ? 2 : 1;
                // Regresa el jugador correspondiente del ArrayList.
                // Al ser diferentes el ID y el índice del ArrayList, es necesario realizar la resta.
                return jugadores.get(idGanador - 1);
            }
        }
        // En caso de que al revisar y nadie haya perdido, entonces regresará un null.
        return null;
    }

    /**
     * Regresa los 4 tableros.
     * @return El ArrayList de los 4 tableros del juego.
     */
    public ArrayList<Tablero> getTableros() {
        return tableros;
    }

    /**
     * Regresa el valor del jugador actual.
     * @return El jugador cuyo turno es actualmente.
     */
    public Jugador getJugadorActual() {
        // Como el valor del turnoActual y el índice del ArrayList son diferentes,
        // se debe realizar la resta para evitar un desbordamiento.
        return jugadores.get(turnoActual - 1);
    }

    /**
     * Regresa el booleano que se encarga de avisar si ya se realizó el mov. pasivo.
     * @return true si ya se realizó el movimiento pasivo en el turno.
     */
    public boolean getPasivoRealizado() {
        return pasivoRealizado;
    }

    /**
     * Realiza un turno completo controlado por la máquina.
     * Selecciona aleatoriamente un movimiento pasivo válido y luego ejecuta
     * el primer movimiento agresivo disponible que encuentre.
     */
    public void realizarMovimientoMaquina() {
        // Se crea un ArrayList para almacenar todas las combinaciones de movimientos posibles.
        ArrayList<int[]> opcionesPasivo = new ArrayList<>();

        // Recorre los tableros y piedras buscando movimientos pasivos válidos.
        for (Tablero tablero : tableros) {
            if (tablero.getPropietario() == turnoActual) {
                for (Piedra piedra : tablero.getPiedras().values()) {
                    if (piedra.getPropietario() == turnoActual) {
                        // Guarda los datos del movimiento (índice, origen y destino) en el ArrayList.
                        ArrayList<Posicion> validos = getMovimientosPasivos(piedra.getPosicion(), tablero.getIndice());

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

        // Se verifica si hay movimientos posibles.
        if (opcionesPasivo.isEmpty()) {
            // En caso de no haberlos, la ejecución de realizarMovimientoMaquina() se detiene.
            return;
        }

        // Elige una opción al azar del ArrayList, sirve para no ser predecible.
        // Se usa Math.random() para generar un double entre 0.0 a 1.0, pasándolo a int con casting.
        // Posteriormente, multiplica ese decimal por el total de movimientos encontrados.
        int[] opcion = opcionesPasivo.get((int)(Math.random() * opcionesPasivo.size()));
        Movimiento pasivo = new Movimiento(
                // 0 = ID tablero, 1 = fila origen, 2 = columna origen,
                // 3 = fila destino, 4 = fila columna.
                opcion[0],
                new Posicion(opcion[1], opcion[2]),
                new Posicion(opcion[3], opcion[4]),
                true
        );
        hacerMovimientoPasivo(pasivo);

        // Busca en los tableros del color opuesto una piedra que pueda
        // replicar el movimiento pasivo recién realizado.
        for (Tablero tablero : tableros) {
            if (validarTableroAgresivo(tablero.getIndice())) {
                for (Piedra piedra : tablero.getPiedras().values()) {
                    if (piedra.getPropietario() == turnoActual) {
                        ArrayList<Posicion> validos = getMovimientosAgresivos(piedra.getPosicion(), tablero.getIndice());

                        // En cuanto encuentra la primera piedra capaz de
                        // hacer el ataque, realiza el movimiento agresivo.
                        if (!validos.isEmpty()) {
                            Movimiento agresivo = new Movimiento(
                                    tablero.getIndice(),
                                    piedra.getPosicion(),
                                    // Escoge el primer destino válido encontrado.
                                    validos.get(0),
                                    false
                            );
                            hacerMovimientoAgresivo(agresivo);
                            // Termina el turno de la máquina.
                            return;
                        }
                    }
                }
            }
        }
    }
}
