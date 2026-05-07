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
     * Identifica y regresa al jugador ganador.
     * @return El jugador ganador, o null si es que aún no hay un ganador.
     */
    public Jugador getGanador() {
        for (Tablero tablero : tableros) {
            int piedrasJ1 = (int) tablero.getPiedras().values().stream()
                    .filter(p -> p.getPropietario() == 1).count();

            int piedrasJ2 = (int) tablero.getPiedras().values().stream()
                    .filter(p -> p.getPropietario() == 2).count();

            if (piedrasJ1 == 0) {
                // Gana Jugador 2 (índice 1).
                return jugadores.get(1);
            }
            if (piedrasJ2 == 0) {
                // Gana Jugador 1 (índice 0)
                return jugadores.get(0);
            }
        }
        // Si no hay ganador, se regresará un null.
        return null;
    }

    /**
     * Se valida si un movimiento es legal, validando lo siguiente:
     * Si la distancia es de 1 o de 2.
     * No hay obstáculos intermedios en movimientos de distancia 2.
     * @param posibleMovimiento Movimiento a validar.
     * @param tablero Tablero donde se realiza el movimiento.
     * @return true si el movimiento es válido, false en caso de que no lo sea.
     */
    private boolean validarMovimiento(Movimiento posibleMovimiento, Tablero tablero) {
        Posicion origen = posibleMovimiento.getOrigen();
        Posicion destino = posibleMovimiento.getDestino();

        // Verifica si el destino está dentro de los límites del tablero 4x4.
        if (destino.getFila() < 0 || destino.getFila() > 3 || destino.getColumna() < 0 || destino.getColumna() > 3) {
            return false;
        }

        // Verifica si el movimiento es de 1 o 2 casillas.
        int distancia = posibleMovimiento.getDistancia();
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
                    origen.getFila() + posibleMovimiento.getDeltaFila(),
                    origen.getColumna() + posibleMovimiento.getDeltaColumna()
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
     * @param agresivoAValidar Movimiento agresivo a validar.
     * @param tablero Tablero donde ocurre el empuje.
     * @return true si el empuje es válido o no hay nada que empujar, false en caso contrario.
     */
    private boolean validarEmpuje(Movimiento agresivoAValidar, Tablero tablero) {
        Posicion destino = agresivoAValidar.getDestino();
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
                destino.getFila() + agresivoAValidar.getDeltaFila(),
                destino.getColumna() + agresivoAValidar.getDeltaColumna()
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
     * Verifica que el tablero del movimiento agresivo sea de color opuesto al tablero del movimiento pasivo.
     * @param indiceTablero Índice del tablero agresivo.
     * @return true si el tablero es de color opuesto al pasivo, false en caso de que no.
     */
    private boolean esTableroOpuesto(int indiceTablero) {
        return !tableros.get(movimientoPasivo.getIndiceTablero()).getColor()
                .equals(tableros.get(indiceTablero).getColor());
    }

    /**
     * Devuelve las posiciones válidas para un movimiento pasivo desde una posición dada.
     * @param origen Posición de la piedra que se quiere mover.
     * @param indiceTablero Índice del tablero de un jugador.
     * @return ArrayList de posiciones destino válidas para el movimiento pasivo.
     */
    public ArrayList<Posicion> getPosValidasPasivas(Posicion origen, int indiceTablero) {
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

            // Ahora se revisa cada una de las piedras presentes en el tablero enemigo.
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
    public ArrayList<Posicion> getPosValidasAgresivas(Posicion origen, int indiceTablero) {
        ArrayList<Posicion> posicionesValidas = new ArrayList<>();

        // Primero se verifica si el tablero agresivo es de color opuesto al tablero pasivo.
        if (!esTableroOpuesto(indiceTablero)) {
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
     */
    public void ejecutarMovimientoPasivo(Movimiento pasivo) {
        // Se obtiene el tablero en donde el jugador hizo clic.
        Tablero tablero = tableros.get(pasivo.getIndiceTablero());

        // El movimiento no se hará en caso de:
        // El tablero no pertenezca al jugador actual.
        // El movimiento haga que esté fuera de los límites o pase sobre otras piedras.
        if (tablero.getPropietario() != turnoActual || !validarMovimiento(pasivo, tablero)) {
            return;
        }
        // Se verifica que un movimiento pasivo termine en una casilla vacía.
        if (tablero.getPosPiedra(pasivo.getDestino()) != null) {
            return;
        }

        // Sí pasó todas las condiciones, el tablero se actualiza
        // con la piedra situada en su nueva posición.
        actualizarTablero(tablero, pasivo);
        // La variable "movimientoPasivo" pasa a guardar la información de "pasivo".
        movimientoPasivo = pasivo;
        // El booleano se marca como true al haber concluído el turno.
        pasivoRealizado = true;
    }

    /**
     * Actualiza la posición de una piedra en el tablero tras un movimiento.
     * Modifica tanto el tablero como el atributo posición de la piedra.
     * @param tablero Tablero donde ocurre el movimiento.
     * @param movimientoHecho Tipo de movimiento realizado.
     */
    private void actualizarTablero(Tablero tablero, Movimiento movimientoHecho) {
        Piedra piedra = tablero.getPosPiedra(movimientoHecho.getOrigen());
        tablero.actualizarPosPiedra(movimientoHecho.getOrigen(), movimientoHecho.getDestino(), piedra);
        piedra.setPosicion(movimientoHecho.getDestino());
    }

    /**
     * Ejecuta el movimiento agresivo del turno actual.
     * Replica exactamente la dirección y distancia del movimiento pasivo.
     * @param agresivo Movimiento agresivo a realizar.
     */
    public void ejecutarMovimientoAgresivo(Movimiento agresivo) {
        // La condición regresa un false en caso de que:
        // No se haya completado el movimiento pasivo.
        // Si el tablero elegido no es del color opuesto al anterior.
        if (!pasivoRealizado || !esTableroOpuesto(agresivo.getIndiceTablero())) {
            return;
        }

        Tablero tablero = tableros.get(agresivo.getIndiceTablero());

        // Se realiza una comparación del movimiento actual con el movimientoPasivo.
        // Deben coincidir exactamente en dirección y distancia.
        if (agresivo.getDeltaFila() != movimientoPasivo.getDeltaFila() ||
                agresivo.getDeltaColumna() != movimientoPasivo.getDeltaColumna() ||
                agresivo.getDistancia() != movimientoPasivo.getDistancia()) {
            // En caso de que no coincidan, regresará un false.
            return;
        }

        // Se verifica que el camino esté libre de piedras,
        // y en caso de que haya una enemiga, esta pueda ser empujada (si se puede).
        if (!validarMovimiento(agresivo, tablero) || !validarEmpuje(agresivo, tablero)) {
            return;
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
            // Cuenta cuántas piedras tiene cada jugador en el tablero.
            int piedrasJ1 = (int) tablero.getPiedras().values().stream()
                    .filter(piedra -> piedra.getPropietario() == 1).count();

            int piedrasJ2 = (int) tablero.getPiedras().values().stream()
                    .filter(piedra -> piedra.getPropietario() == 2).count();

            // Si alguno llegó a cero, hay un ganador
            return piedrasJ1 == 0 || piedrasJ2 == 0;
        });
    }

    /**
     * Realiza un turno completo hecho por la máquina.
     * Primero realiza su movimiento pasivo aleatorio
     * y después ejecuta el primer movimiento agresivo que esté disponible.
     */
    public void ejecutarMovimientoMaquina() {
        // Toma un movimiento pasivo aleatorio disponible.
        Movimiento pasivo = elegirMovimientoPasivo();
        // Si no se encontró movimiento pasivo posible, la máquina no podrá jugar.
        if (pasivo == null) {
            return;
        }
        // Ejecutará su movimiento pasivo con la opción aleatoria regresada.
        ejecutarMovimientoPasivo(pasivo);

        // Toma un movimiento agresivo que sea capaz de poder replicar.
        Movimiento agresivo = elegirMovimientoAgresivo();
        // La condición hará que pueda atacar si encontró un movimiento agresivo válido.
        if (agresivo != null) {
            ejecutarMovimientoAgresivo(agresivo);
        }
    }

    /**
     * Recolecta todos los movimientos pasivos válidos disponibles
     * para la máquina y devuelve uno elegido al azar.
     * @return Un Movimiento pasivo al azar, null si no hay ninguno disponible.
     */
    private Movimiento elegirMovimientoPasivo() {
        // ArrayList que guardará todos los movimientos pasivos posibles.
        ArrayList<Movimiento> opciones = new ArrayList<>();

        // El ciclo for recorre todos los tableros.
        for (Tablero tablero : tableros) {
            // Esta condición considera los tableros propios de la máquina.
            if (tablero.getPropietario() != turnoActual) {
                continue;
            }
            for (Piedra piedra : tablero.getPiedras().values()) {
                // Esta condición considera las piedras propias de la máquina.
                if (piedra.getPropietario() != turnoActual) {
                    continue;
                }
                // getPosValidasPasivas() devuelve todos los destinos válidos para esta piedra.
                for (Posicion destino : getPosValidasPasivas(piedra.getPosicion(), tablero.getIndice())) {
                    opciones.add(new Movimiento(tablero.getIndice(), piedra.getPosicion(), destino, true));
                }
            }
        }
        // Si no se llega a haber una opción, la máquina no tendrá movimientos disponibles.
        if (opciones.isEmpty()) {
            return null;
        }
        // Se usa Math.random() para generar un valor de 0.0 a 1.0, ese valor
        // se multiplica por el tamaño del ArrayList y se convierte en int, ese será el índice aleatorio.
        return opciones.get((int)(Math.random() * opciones.size()));
    }

    /**
     * Busca el primer movimiento agresivo válido disponible para la máquina.
     * @return El primer Movimiento agresivo válido encontrado, null si no hay ninguno.
     */
    private Movimiento elegirMovimientoAgresivo() {
        // El ciclo for recorre todos los tableros.
        for (Tablero tablero : tableros) {
            // Solo se consideran tableros de color opuesto al tablero del movimiento pasivo.
            if (!esTableroOpuesto(tablero.getIndice())) {
                continue;
            }
            for (Piedra piedra : tablero.getPiedras().values()) {
                // Solo se consideran las piedras propias de la máquina.
                if (piedra.getPropietario() != turnoActual) {
                    continue;
                }
                // getPosValidasAgresivas() devuelve los destinos válidos que se pueden replicar.
                ArrayList<Posicion> validos = getPosValidasAgresivas(piedra.getPosicion(), tablero.getIndice());
                if (!validos.isEmpty()) {
                    // Si puede ejecutar el movimiento, encontrará la primera piedra capaz de atacar.
                    return new Movimiento(tablero.getIndice(), piedra.getPosicion(), validos.get(0), false);
                }
            }
        }
        // Si ninguna piedra pudo realizar un movimiento agresivo, regresará null.
        return null;
    }
}
