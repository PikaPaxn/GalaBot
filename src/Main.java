import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

//////////////////////////////
//      ♡ Main GalaBot ♡
//  Autor:      Pencho
//  Creado:     08-Julio-2018
//  Modificado: 24-Diciembre-2018
//
//  Bot para la Gala ♡
//  Por el momento contiene funciones para:
//    ♡ General
//    ♡ SubDay
//    ♡ Navidad (Evento)
//    ♡ Fortnite
//    ♡ Signs
//    ♡ League of Legends
//    ♡ Deceit
//////////////////////////////
enum BotMode { NONE, FORTNITE, LOL, DECEIT,
               SUBDAY, NAVIDAD, SIGNS, CONCURSOS; }

public class Main {
    private static String _nombre;
    private static String _password;
    private static String _channel;
    private static String _prefix;
    private static boolean[] _logs = new boolean[4];

    private static HashMap<String, String> oathkeys = new HashMap<>();

    public static void main(String[] args) {
        loadOathKeys();
        handleArgs(args);
        System.out.println("Nombre: " + _nombre);
        System.out.println("Pass: " + _password);
        System.out.println("Channel: " + _channel);
        System.out.println("Prefix: " + _prefix);
        System.out.println("verbose, msg, chat, cmd");
        System.out.println(Arrays.toString(_logs));


        GalaBot bot = new GalaBot(_nombre, _password, _channel, _prefix, _logs);
        bot.sendToChat("¡Estoy listo para trabajar ♡! Usa " + bot.getPrefix() + "help para saber más de mi.");

        /**
        * Añadir Handler para "Ctrl+C" en la terminal, de manera
        * que el bot se cierre de buena manera al cerrar el programa
        * de manera forzada.
        */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    Thread.sleep(200);
                    bot.sendToChat("Gracias por llamarme, ¡nos vemos!");
                    bot.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Carga las keys necesarias para que el bot pueda identificarse
     * de buena manera en los canales de Twitch
     */
    private static void loadOathKeys(){
        try {
            Scanner sc = new Scanner(new File("../resources/oath.keys"));
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String k = line.substring(0, line.indexOf("|"));
                String v = line.substring(line.indexOf("|")+1) + "\r\n";
                oathkeys.put(k, v);
            }
        } catch (Exception e) {
            System.out.println("No key has been found, closing the program...");
            System.exit(1);
        }
    }

   
    //////////////////////////////
    //  ♡ ARGS ♡
    //  0 => Default ()
    //  -v   / +v       | verbose on/off
    //  -msg / +msg     | msgLog  on/off
    //  -cht / +cht     | chatLog on/off
    //  -cmd / +cmd     | cmdLog  on/off
    //  -n <nick>       | nombre
    //  -u paxn/bot     | password (Paxncho | PenchoBot)
    //  -c <channel>    | channel
    //  -p <prefix>     | prefix
    private static void handleArgs(String[] args){
        //////////////////////////////
        // ♡ DEFAULTS
        _nombre = "LaxyBot";
        _password = oathkeys.get("laxybot");
        _channel = "galaxias";
        _prefix = "!!";
        _logs[0] = true;
        _logs[1] = false;
        _logs[2] = false;
        _logs[3] = false;
        //////////////////////////////

        for (int i = 0; i < args.length; i++) {
            switch(args[i].substring(1)){
                case "v":   _logs[0] = args[i].startsWith("-");  break;
                case "msg": _logs[1] = args[i].startsWith("-");  break;
                case "cht": _logs[2] = args[i].startsWith("-");  break;
                case "cmd": _logs[3] = args[i].startsWith("-");  break;
                case "n":   _nombre = args[i+1];     i++;        break;
                case "c":   _channel = args[i+1];    i++;        break;
                case "p":   _prefix = args[i+1];     i++;        break;
                case "u":
                    if (args[i+1].toLowerCase().equals("paxncho"))
                        _password = oathkeys.get("paxncho");
                    else if (args[i+1].toLowerCase().equals("penchobot"))
                        _password = oathkeys.get("penchobot");
                    else if (args[i+1].toLowerCase().equals("laxybot"))
                        _password = oathkeys.get("laxybot");
                    break;
            }
        }
    }
    //////////////////////////////
}

/**
 * Command
 * 
 * Interface utilizada para generalizar todos los comandos que puede
 * utilizar el bot
 */
interface Command {
    /**
     * Metodo que se ejecuta cada vez que el bot detecta que el comando
     * asociado fue llamado.
     * 
     * @param user String que porta el nombre del usuario que ha lanzado el comando.
     * @param msg String que porta el mensaje que el usuario ha ingresado, incluyendo
     * el comando al inicio.
     * @param tags String que involucra información adicional del usuario, tal como
     * los meses de sub, si es sub, si es el streamer, si es mod, etc.
     */
    public void execute(String user, String msg, String tags);
}

//////////////////////////////
//      ♡ GalaBot ♡
//  Bot dedicado especificamente al stream
//  de Galaxias, configurado para ello y todas sus necesidades ♡
//////////////////////////////
class GalaBot extends TwitchBot {

    //////////////////////////////
    //  ♡ CONFIGURACIONES BASICAS
    //////////////////////////////
    public String getPrefix() { return _prefix; }
    private HashMap<String, Command> commands = new HashMap<>();
    public GalaBot(String nombre, String password, String channel, String prefix, boolean[] logs) {
        super(nombre, password, channel, logs);
        super._prefix = prefix;
        addStandardCommands();
        addQueueCommands();
        addGameCommands();
    }

    /**
     * Metodo que funciona como un despachador de comandos, verifica primero
     * que es un comando que el bot debe leer (Mediante el prefijo configurado)
     * y de ser así, envía el mensaje al parseador correspondiente
     */
    @Override
    public void handleChatMessage(String user, String msg, String tags){
        super.handleChatMessage(user, msg, tags);
        if (msg.startsWith(_prefix)){
            handleCommand(user, msg.trim(), tags);
        }
    }

    /**
     * Metodo que dispacha los comandos del bot, parsea el comando adecuado y de
     * existir, llama a la ejecución que corresponde.
     */
    private void handleCommand(String user, String msg, String tags){
        int spaceIndex = msg.indexOf(" ");
        String _command = (spaceIndex != -1) ? msg.substring(0, spaceIndex) : msg;
        if (commands.containsKey(_command)){
            commands.get(_command).execute(user, msg, tags);
        }
    }

    /**
     * Metodo que añade los comandos estándar o básicos del bot. Son los metodos
     * génericos que no están asociados a ningún modo del bot
     */
    private void addStandardCommands(){
        /**
         * >help
         * Comando que envia una pequeña descripción del bot al chat.
         */
        commands.put(_prefix + "help", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("¡Hola " + user + "! Soy LaxyBot, un fiel sirviente del canal que fue programado por un ocioso con tiempo libre " + 
                    "para poder ayudar con necesidades especificas del Stream. Mi prefijo actual es " +
                    _prefix + ". Para ver mis comandos disponibles usa " + _prefix + "commands");
                    //_prefix);
            }
        });

        /**
         * >commands
         * Comando que envía un link con la lista de todos los comandos al chat.
         */
        commands.put(_prefix + "commands", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("Para ver una lista de todos mis comandos, ingresa a este link ♡: http://bit.ly/laxybotcommands");
            }
        });

        /**
         * >kappa
         * Kappa
         */
        commands.put(_prefix + "kappa", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("Kappa");
            }
        });

        /**
         * >mesa
         * Envía un emoticon de "Rage" al chat
         */
        commands.put(_prefix + "mesa", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("(ノಠ益ಠ)ノ彡┻━┻");
            }
        });

        /**
         * >pencho
         * Comando que entrega una breve descripción del creador del bot.
         */
        commands.put(_prefix + "pencho", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("Mi Dios creador, el mod más querido de todos los niños, el favorito de todos y aquel que siempre estará en sus corazones. (づ￣ ³￣)づ");
            }
        });

        /**
         * >pingu
         * Comando que envía un gif de pingu bailando
         */
        commands.put(_prefix + "pingu", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("bit.ly/pingubot");
            }
        });

        /**
         * >what
         * What?
         */
        commands.put(_prefix + "what", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("what galaaWhat");
            }
        });

        /**
         * >mileto
         * Comando que muestra lo mucho que amamos a Mileto ♡
         */
        commands.put(_prefix + "mileto", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("#MiletoLovers (づ￣ ³￣)づ");
            }
        });

        /**
         * >hype
         * Genera una cadena de Hype en el chat
         */
        String hypeCmd = "hype";
        commands.put(_prefix + hypeCmd, new Command(){
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";
                int repeticiones = -1;
                try {
                    Scanner sc = new Scanner(msg.substring(_prefix.length() + hypeCmd.length()));
                    repeticiones = sc.nextInt();
                    sc.close();
                } catch (Exception e) {
                    repeticiones = 10;
                }

                for (int i = 0; i < repeticiones; i++)
                    _msg += " galaaHype";

                sendToChat(_msg);
            }
        });
    }

    //////////////////////////////
    //      ♡ COMUN ♡
    //  Variables comunes para todo el bot.
    private BotMode betMode = BotMode.NONE;
    private BotMode queueMode = BotMode.NONE;
    private Random rand = new Random();
    //////////////////////////////

    //////////////////////////////
    //      ♡ COLAS ♡
    //  ♡ SUBDAY
    //  ♡ NAVIDAD
    //  ♡ SIGNS
    //  ♡ CONCURSOS
    //////////////////////////////
    private boolean queueOpen = false;
    private String actualUser = null;
    private LinkedList<String> queue = new LinkedList<>();
    private HashSet<String> onQueue = new HashSet<>();
    private HashSet<String> queueReady = new HashSet<>();

    private String queueHelp() {
        return "";
    }

    private boolean isOnQueue() {
        return (queueMode == BotMode.CONCURSOS ||
                queueMode == BotMode.NAVIDAD ||
                queueMode == BotMode.SIGNS ||
                queueMode == BotMode.SUBDAY);
    }

    //////////////////////////////
    //  ♡ SUBDAY
    private SubTier actualTier = null;
    private HashMap<SubTier, ArrayList<String>> subdayPrizes = new HashMap<>();
    private boolean hasSpin = false;
    
    private void loadPrizes(){
        for (SubTier t : SubTier.values()){
            try {
                Scanner sc = new Scanner(new File(t.getFilename()));
                ArrayList<String> prize = new ArrayList<String>();
                while(sc.hasNextLine()){
                    prize.add(sc.nextLine());
                }
                subdayPrizes.put(t, prize);
            } catch (Exception e) {
                log("!! Cant open " + t.getFilename());
            }
        }
    }
    //////////////////////////////

    /**
     * Metodo que añade los comandos de cola del bot. Esos que están asociados
     * a concursos y eventos irl que se realizan durante el stream.
     */
    private void addQueueCommands(){
        loadPrizes();

        //////////////////////////////
        //  ♡ COMANDOS DE STREAMER O MODS
        //////////////////////////////
        /**
         * >subday
         * Comando que settea el modo de las colas del bot a "SubDay"
         */
        commands.put(_prefix + "subday", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";
                _mensaje = "♡ Se ha seteado el modo a SubDay ♡ ¡Envíen " + _prefix + "join para unirse a la cola! galaaHype";
                queueMode = BotMode.SUBDAY;
                queueOpen = true;

                sendToChat(_mensaje);
            }
        });

        /**
         * >signs
         * Comando que settea el modo de las colas del bot a "Signs"
         */
        commands.put(_prefix + "signs", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";
                _mensaje = "♡ Se ha seteado el modo a Signs ♡ galaaKiss";
                queueMode = BotMode.SIGNS;

                sendToChat(_mensaje);
            }
        });

        /**
         * >concursos
         * Comando que settea el modo de las colas del bot a "Concursos"
         */
        commands.put(_prefix + "concursos", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";
                _mensaje = "♡ Se ha seteado el modo a Concursos ♡ ¡Envíen " + _prefix + "join para unirse a la cola! galaaHype";
                queueMode = BotMode.CONCURSOS;
                queueOpen = true;

                sendToChat(_mensaje);
            }
        });

        /**
         * >navidad
         * Comando que settea el modo de las colas del bot a "Navidad"
         */
        commands.put(_prefix + "navidad", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";
                _mensaje = "♡ Se ha seteado el modo a Navidad ♡ ¡Envíen " + _prefix + "join para unirse a la cola! galaaHype";
                queueMode = BotMode.NAVIDAD;
                queueOpen = true;

                sendToChat(_mensaje);
            }
        });

        /**
         * >noqueue
         * Comando que cierra todas las colas.
         * Realiza también una limpieza de las estructuras usadas de
         * manera que quedan listas para un nuevo uso.
         */
        commands.put(_prefix + "noqueue", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";
                
                //Definir mensaje según caso
                switch(queueMode){
                    case SUBDAY:    _mensaje = "¡El SubDay se ha terminado! Gracias por participar ♡ galaaKiss"; break;
                    case NAVIDAD:   _mensaje = "¡El evento de Navidad se ha terminado! Gracias por participar ♡ galaaKiss"; break;
                    case SIGNS:     _mensaje = "¡Los Signs se han terminado! Gracias a todos quienes canjearon ♡ galaaKiss"; break;
                    case CONCURSOS: _mensaje = "¡El Concurso se ha acabado! Gracias a todos los concursantes ♡ galaaKiss"; break;
                    default:        _mensaje = "No hay ninguna cola que terminar. (Are you drunk?) galaaWhat" ;
                }

                //Limpiar
                queueOpen = false;
                queue.clear();
                onQueue.clear();
                queueReady.clear();
                actualUser = null;
                queueMode = BotMode.NONE;

                sendToChat(_mensaje);
            }
        });

        /**
         * >closequeue
         * Cierra la cola de tal manera que ningún usuario
         * se puede seguir añadiendo por su cuenta
         */
        commands.put(_prefix + "closequeue", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";
            
                //Definir mensaje según caso
                if (isOnQueue()){
                    _mensaje = "¡La cola se ha cerrado! Sigan sintonizando para ver como le va a los restantes ♡ galaaGG";
                    queueOpen = false;
                } else {
                    _mensaje = "No hay ninguna cola que cerrar. (Are you drunk?) galaaWhat";
                }

                sendToChat(_mensaje);
            }
        });

        /**
         * >addqueue <usuario|s>
         * Fuerza la añadidura de usuarios a la cola,
         * da lo mismo si está cerrada o no, o si ese usuario
         * ya había participado.
         * 
         * Para añadir más de un usuario a la vez, se deben
         * introducir sus nombres en cadena utilizando el caracter
         * '|' como separador.
         * 
         * Ejemplo: addqueue usuario1|usuario2|usuario3
         */
        String addQueueCmd = "addqueue";
        commands.put(_prefix + addQueueCmd, new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";

                if (!isOnQueue()){
                    _mensaje = "No hay ninguna cola a la que añadirle usuarios. (Are you drunk?) galaaWhat";
                    sendToChat(_mensaje);
                    return;
                }

                String _users = msg.substring(_prefix.length() + addQueueCmd.length());
                boolean listo = false;
                while(!listo) {
                    int separador = _users.indexOf("|");
                    String _user = "";

                    //separador = -1 -> Solo queda un user por agregar.
                    if (separador == -1) {
                        if (_users.trim().length() < 1)
                            break;
                        _user = _users.trim();
                        listo = true;
                    } else {
                        _user = _users.substring(0, separador).trim();
                        _users = _users.substring(separador + 1);
                    }

                    queue.add(_user);
                }

                if (listo) {
                    switch(queueMode){
                        case SUBDAY:    _mensaje = "Se han agregado correctamente los subs a la cola galaaGG"; break;
                        case SIGNS:     _mensaje = "Se han agregado correctamente los signs a la cola galaaGG"; break;
                        case NAVIDAD:   _mensaje = "Se han agregado correctamente los concursantes a la cola galaaGG"; break;
                        case CONCURSOS: _mensaje = "Se han agregado correctamente los concursantes a la cola galaaGG"; break;
                        default: break;
                    }
                } else {
                    _mensaje = user + " no especificaste ningún usuario. Intenta usar el comando de nuevo galaaCry";
                }
                sendToChat(_mensaje);
            }
        });

        /**
         * >next
         * Hace pasar al siguiente usuario de la cola
         * eliminando al usuario que se encontraba ahora en "juego"
         */
        commands.put(_prefix + "next", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";

                if (!isOnQueue()){
                    _mensaje = "No hay cola a la que hacer avanzar. (Are you drunk?) galaaWhat";
                    sendToChat(_mensaje);
                    return;
                }

                if (!queueReady.contains(actualUser) && actualUser != null)
                    queueReady.add(actualUser);

                String next = queue.poll();
                if (next != null) {

                    //SUBDAY
                    if (queueMode == BotMode.SUBDAY) {
                        int _count = Integer.parseInt(next.substring(next.lastIndexOf("@") + 1));
                        next = next.substring(0, next.lastIndexOf("@"));

                        switch (_count) {
                            case 0:  actualTier = SubTier.ALIEN_GRIS;   break;
                            case 3:  actualTier = SubTier.ALIEN_VERDE;  break;
                            case 6:  actualTier = SubTier.ALIEN_DORADO; break;
                            case 12: actualTier = SubTier.ALIEN_MORADO; break;
                        }

                        _mensaje = "¡ @" + next + " eres un alien " + actualTier.getColor() + " ! Por favor usa " + _prefix + "spin para girar tu ruleta galáctica correspondiente ♡ galaaKiss";

                    //NAVIDAD
                    } else if (queueMode == BotMode.NAVIDAD) {
                        int _count = Integer.parseInt(next.substring(next.lastIndexOf("@") + 1));
                        next = next.substring(0, next.lastIndexOf("@"));

                        switch (_count) {
                            case 0:  actualTier = SubTier.ALIEN_GRIS;   break;
                            case 3:  actualTier = SubTier.ALIEN_VERDE;  break;
                            case 6:  actualTier = SubTier.ALIEN_DORADO; break;
                            case 12: actualTier = SubTier.ALIEN_MORADO; break;
                            default: actualTier = SubTier.FOLLOWER;     break;
                        }

                        _mensaje = "¡ @" + next + " es tu turno! Por favor usa " + _prefix + "spin para girar tu ruleta navideña correspondiente ♡ galaaKiss";

                    //OTRO MODO
                    } else {
                        _mensaje = "Ahora es turno de: " + next + " galaaKiss";
                    }
                    onQueue.remove(next);
                    actualUser = next;
                } else {
                    _mensaje = "¡La cola está vacia! galaaCry";
                }

                sendToChat(_mensaje);
                hasSpin = false;
            }
        });

        /**
         * >otro
         * Hace pasar al siguiente usuario de la cola, mientras
         * que el actual entra de nuevo pero al final.
         * 
         * Es un comando diseñado especialmente para cuando los usuarios
         * no se encuentran disponibles en el momento, enviandolos "al agua".
         * Lo que les da una nueva oportunidad sin tener que freezear el stream
         */
        commands.put(_prefix + "otro", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje;

                if (!isOnQueue()) {
                    _mensaje = "No hay cola abierta para mandar a alguien al agua. (Are you drunk?) galaaWhat";
                    sendToChat(_mensaje);
                    return;
                }


                if (actualUser == null){
                    switch(queueMode){
                        case SUBDAY:    _mensaje = "No habia nadie participando. No puedes enviar al agua a la nada galaaGG"; break;
                        case SIGNS:     _mensaje = "No habia nadie con Sign. No puedes enviar al agua a la nada galaaGG"; break;
                        case NAVIDAD:   _mensaje = "No habia nadie concursando. No puedes enviar al agua a la nada galaaGG"; break;
                        case CONCURSOS: _mensaje = "No habia nadie concursando. No puedes enviar al agua a la nada galaaGG"; break;
                        default: _mensaje = "";
                    }

                    sendToChat(_mensaje);
                    return;
                }


                _mensaje = actualUser + " se ha ido al agua y deberá nadar hasta el final de la cola. galaaCry";
                if (queueMode == BotMode.SUBDAY || queueMode == BotMode.NAVIDAD){
                    queue.add(actualUser + "@" + actualTier.getMeses());
                } else {
                    queue.add(actualUser);
                }

                String next = queue.poll();
                if (next != actualUser) {
                    if (queueMode == BotMode.SUBDAY || queueMode == BotMode.NAVIDAD){
                        int _count = Integer.parseInt(next.substring(next.lastIndexOf("@") + 1));
                        next = next.substring(0, next.lastIndexOf("@"));

                        switch (_count) {
                            case 0:  actualTier = SubTier.ALIEN_GRIS;   break;
                            case 3:  actualTier = SubTier.ALIEN_VERDE;  break;
                            case 6:  actualTier = SubTier.ALIEN_DORADO; break;
                            case 12: actualTier = SubTier.ALIEN_MORADO; break;
                            default: actualTier = SubTier.FOLLOWER;     break;
                        }
                    }
                    _mensaje += " Ahora es turno de: ";
                    onQueue.remove(next);
                    onQueue.add(actualUser);
                    actualUser = next;

                    if (queueMode == BotMode.SUBDAY){
                        _mensaje +=  "@" + next + " Por favor usa " + _prefix + "spin para girar tu ruleta galáctica correspondiente ♡ galaaKiss";
                    } else if (queueMode == BotMode.NAVIDAD) {
                        _mensaje +=  "@" + next + " Por favor usa " + _prefix + "spin para girar tu ruleta navideña correspondiente ♡ galaaKiss";
                    } else {
                        _mensaje += "@" + next + " galaaKiss";
                    }

                } else {
                    _mensaje += " Sin embargo no quedaba nadie en la cola, por lo que sigue siendo el turno de: " + actualUser + " galaaKiss";
                }

                sendToChat(_mensaje);
                hasSpin = false;
            }
        });

        //////////////////////////////
        //  ♡ COMANDOS DE CUALQUIERA
        //////////////////////////////
        /**
         * >join
         * Ingresa a la cola actual, este comando discriminará si posees
         * los privilegios necesarios en caso de ser una cola restringida,
         * como la de los SubDay
         */
        commands.put(_prefix + "join", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                if (!isOnQueue()){
                    _mensaje = user + " no hay cola a la que te puedas añadir. (Are you drunk?) galaaWhat";
                    sendToChat(_mensaje);
                    return;
                }

                if (!queueOpen){
                    _mensaje = "Lo siento, la cola está cerrada galaaGG";
                    sendToChat(_mensaje);
                    return;
                }

                switch (queueMode) {
                    case SUBDAY:
                        if (!checkSub(tags)) {
                            _mensaje = "Al SubDay solo pueden entrar subs galaaCry";
                            sendToChat(_mensaje);
                            return;
                        }
                        break;
                    case CONCURSOS:
                    case NAVIDAD:
                        break;
                    case SIGNS:
                        _mensaje = "Por favor pidele a un Mod que te añada a la cola ♡ galaaKiss";
                        sendToChat(_mensaje);
                        return;
                    default: return;
                }

                //Añadir que tipo de sub es a la Key en caso de necesitarlo
                String _key = user;
                if (queueMode == BotMode.SUBDAY || queueMode == BotMode.NAVIDAD) {
                    //Si está aquí, es porque es sub
                    try {
                        _key += "@" + subsRow(tags);
                    } catch (Exception e){
                        //Catchea cuando no es sub
                        _key += "@" + SubTier.FOLLOWER.getMeses();
                    }
                    log("|||Cola: " + _key);
                }

                if (onQueue.contains(_key))
                    _mensaje = "¡" + user + " ya está en la cola! galaaGG";
                else if (queueReady.contains(_key))
                    _mensaje = "¡" + user + " ya ha participado! galaaWhat";
                else {
                    queue.add(_key);
                    onQueue.add(_key);
                    _mensaje = "¡" + user + " ha sido agregado a la cola! galaaKiss";
                }

                sendToChat(_mensaje);
            }
        });

        /**
         * >spin
         * Gira la ruleta correspondiente según el modo y los meses
         * de sub que tenga el usuario.
         */
        commands.put(_prefix + "spin", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                if (queueMode != BotMode.SUBDAY && queueMode != BotMode.NAVIDAD){
                    sendToChat("No estamos en epoca de Ruletas. ¡No puedes girar nada! galaaCry");
                    return;
                }

                if (user.compareTo(actualUser) != 0){
                    log("|" + user + " != " + actualUser + "|");
                    return;
                }

                if (hasSpin){
                    sendToChat("¡Es sólo un lanzamiento! galaaCry");
                    return;
                }

                ArrayList<String> _prizes = subdayPrizes.get(actualTier);
                int p = rand.nextInt(_prizes.size());

                _mensaje = "Gracias por participar " + actualUser + " ♡. Tu premio es: " + _prizes.get(p) + " galaaHype";
                sendToChat(_mensaje);
                hasSpin = true;
            }
        });        

        /**
         * >checknext
         * Revisa quien es el siguiente usuario en la cola, para que se vaya preparando.
         */
        commands.put(_prefix + "checknext", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                if (isOnQueue()){
                    String next = queue.peek();
                    //Hacer la corrección del sub si es SUBDAY o NAVIDAD
                    if (queueMode == BotMode.SUBDAY || queueMode == BotMode.NAVIDAD) {
                        next = next.substring(0, next.lastIndexOf("@"));
                    }
                    _mensaje = (next != null) ? "El siguiente en la lista es: " + next : "¡La cola está vacia! galaaCry";
                } else {
                    _mensaje = "No hay ninguna cola a quien revisarle el siguiente. (Are you drunk?) galaaWhat";
                }

                sendToChat(_mensaje);
            }
        });

        /**
         * >actual
         * Revisa quien es el usuario que le toca sign/girar ruleta en este momento
         */
        commands.put(_prefix + "actual", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";
                
                switch(queueMode){
                    case CONCURSOS:
                        _mensaje = ((actualUser != null) ? "El concursante actual es: " + actualUser : " ¡No hay nadie concursando! galaaCry");
                        break;
                    case SUBDAY:
                    case NAVIDAD:
                        _mensaje = ((actualUser != null) ? "El participante actual es: " + actualUser : " ¡No hay nadie participando! galaaCry");
                        break;
                    case SIGNS:
                        _mensaje = ((actualUser != null) ? "El sign actual es: " + actualUser : " ¡No hay nadie para el sign! galaaCry");
                        break;
                    default:
                        _mensaje = "No hay ninguna cola a quien revisarle el actual. (Are you drunk?) galaaWhat";
                        break;
                }

                sendToChat(_mensaje);
            }
        });

        /**
         * >queueleft
         * Revisa cuantos participantes quedan en la cola y lo comunica en el chat
         */
        commands.put(_prefix + "queueleft", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";
                
                int left = queue.size();
                String _moreThanOne;
                String _one;
                String _none = "¡No queda nadie más en la cola! galaaCry";

                switch(queueMode) {
                    case SUBDAY:
                        _moreThanOne = "¡Quedan " + left + " subs en la cola! galaaHype";
                        _one = "¡Queda solo 1 sub en la cola! galaaHype";
                        break;
                    case SIGNS:
                        _moreThanOne = "¡Quedan " + left + " signs en la cola! galaaHype";
                        _one = "¡Queda solo 1 sign en la cola! galaaHype";
                        break;
                    case CONCURSOS:
                        _moreThanOne = "¡Quedan " + left + " concursantes en la cola! galaaHype";
                        _one = "¡Queda solo 1 concursante en la cola! galaaHype";
                        break;
                    case NAVIDAD:
                        _moreThanOne = "¡Quedan " + left + " participantes en la cola! galaaHype";
                        _one = "¡Queda solo 1 participante en la cola! galaaHype";
                        break;
                    default:
                        _mensaje = "No hay ninguna cola a quien revisarle los restantes. (Are you drunk?) galaaWhat";
                        sendToChat(_mensaje);
                        return;
                }

                if (left > 0)
                    if (left == 1)
                        _mensaje = _one;
                    else
                        _mensaje = _moreThanOne;
                else
                    _mensaje = _none;
                
                sendToChat(_mensaje);                   
            }
        });
    }

    //////////////////////////////
    //      ♡ VOTACIONES JUEGOS ♡
    //  ♡ FORTNITE
    //  ♡ LEAGUE OF LEGENDS
    //  ♡ DECEIT
    //////////////////////////////
    private int laxycoins = -1;
    private boolean betting = false;
    private boolean running = false;
    private HashSet<String> votesReady = new HashSet<>();
    private HashMap<Integer, LinkedList<String>> votes = new HashMap<>();

    private String votacionesHelp() {
        return "";
    }

    private boolean isOnGame(){
        return (betMode == BotMode.FORTNITE ||
                betMode == BotMode.LOL ||
                betMode == BotMode.DECEIT);
    }

    private void addGameCommands(){
        //////////////////////////////
        //  ♡ COMANDOS DE STREAMER O MODS
        //////////////////////////////
        /**
         * >lol
         * Comando que settea el modo del bot a "LoL"
         * No cambiará de modo si es que una apuesta quedó abierta
         */
        commands.put(_prefix + "lol", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";

                if (running) {
                    _mensaje = "Hay una apuesta en curso, por favor terminala antes de cambiar de modo ♡ galaaGG";
                } else {
                    _mensaje = user + " se ha seteado el modo a League of Legends ♡ galaaHype";
                    betMode = BotMode.LOL;
                }

                sendToChat(_mensaje);
            }
        });

        /**
         * >fortnite
         * Comando que settea el modo del bot a "Fortnite"
         * No cambiará de modo si es que una apuesta quedó abierta
         */
        commands.put(_prefix + "fortnite", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";

                if (running) {
                    _mensaje = "Hay una apuesta en curso, por favor terminala antes de cambiar de modo ♡ galaaGG";
                } else {
                    _mensaje = user + " se ha seteado el modo a Fortnite ♡ galaaHype";
                    betMode = BotMode.FORTNITE;
                }

                sendToChat(_mensaje);
            }
        });

        /**
         * >deceit
         * Comando que settea el modo del bot a "Deceit"
         * No cambiará de modo si es que una apuesta quedó abierta
         */
        commands.put(_prefix + "deceit", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";

                if (running) {
                    _mensaje = "Hay una apuesta en curso, por favor terminala antes de cambiar de modo ♡ galaaGG";
                } else {
                    _mensaje = user + " se ha seteado el modo a Deceit ♡ galaaHype";
                    betMode = BotMode.DECEIT;
                }

                sendToChat(_mensaje);
            }
        });

        /**
         * >nogame
         * Comando que settea el bot de vuelta a un modo neutro.
         * No cambiará de modo si es que una apuesta quedó abierta
         */
        commands.put(_prefix + "nogame", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";

                if (running) {
                    _mensaje = "Hay una apuesta en curso, por favor terminala antes de cambiar de modo ♡ galaaGG";
                } else {
                    _mensaje = user + " se ha vuelto el modo a nada ♡ galaaKiss";
                    betMode = BotMode.NONE;
                }

                sendToChat(_mensaje);
            }
        });

        /**
         * >startbet <laxys>
         * Inicia las votaciones de los juegos, estás votaciones son dependiendo
         * del modo activo.
         * 
         * ♡ Fornite: Lugar donde queda la Gala
         * ♡ LoL:     Cuantas Kills consigue la Gala
         * ♡ Deceit:  Si ganó o perdió la Gala
         */
        String betCmd = "startbet";
        commands.put(_prefix + betCmd, new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                if (!isOnGame()) {
                    sendToChat("¡No hay ningún juego setteado!. Por favor usa " + _prefix + "lol, " + _prefix + "fortnite, o " + _prefix + "deceit antes de iniciar la votación ♡ galaaKiss");
                    return;
                }

                int laxys = -1;
                try {
                    Scanner sc = new Scanner(msg.substring(_prefix.length() + betCmd.length()));
                    laxys = sc.nextInt();
                    sc.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                String _mensaje = "";

                if (laxys != -1)
                    _mensaje = "♡ ¡Por " + laxys + " laxycoins! ";

                if (!betting)
                    _mensaje += "¡Las apuestas se han activado!";
                else
                    _mensaje += "¡Las apuestas ya se encontraban activadas!";
                
                _mensaje += " ♡ ¡Envia " + _prefix + "vote ";

                if (betMode == BotMode.FORTNITE)
                    _mensaje += "<lugar>";
                else if (betMode == BotMode.LOL)
                    _mensaje += "<kills>";
                else if (betMode == BotMode.DECEIT)
                    _mensaje += "<win|lose>";

                _mensaje += " para apostar! ♡ ¡Que comience el juego ♡! galaaHype";
                
                sendToChat(_mensaje);

                if (!betting) {
                    votesReady.clear();
                    votes.clear();
                }
                
                betting = true;
                running = true;
                laxycoins = laxys;
            }
        });

        /**
         * >closebet
         * Cierra las votaciones anteriormente abiertas
         */
        commands.put(_prefix + "closebet", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                if (!running) {
                    sendToChat("No hay ninguna apuesta en curso galaaCry");
                    return;
                }

                sendToChat(user + ((betting) ? " ¡Las apuestas se han cerrado!" : " ¡Las apuestas ya se encontraban cerradas!") +
                    " ¡Estén atentos al resultado! galaaGG");
                betting = false;
            }
        });

        /**
         * >resetbet
         * Reinicia las votaciones anteriormente cerradas
         * Las colas se vacian, pero se mantiene el premio setteado.
         */
        commands.put(_prefix + "resetbet", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                if (!running){
                    sendToChat("No hay ninguna apuesta en curso galaaCry");
                    return;
                }

                votesReady.clear();
                votes.clear();

                String _mensaje = user + " ¡Apuestas reiniciadas! ¡Por " + laxycoins + " laxycoins! ♡ ¡Envia " + _prefix + "vote ";
                
                if (betMode == BotMode.FORTNITE)
                    _mensaje += "<lugar>";
                else if (betMode == BotMode.LOL)
                    _mensaje += "<kills>";
                else if (betMode == BotMode.DECEIT)
                    _mensaje += "<win|lose>";

                _mensaje += " para apostar! ♡ galaaHype";
                sendToChat(_mensaje);
                betting = true;
            }
        });

        /**
         * >winner <lugar>
         * Define al ganador de las apuestas, y anuncia el premio que obtuvo el/los ganador/es.
         */
        String winCmd = "winner";
        commands.put(_prefix + winCmd, new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                if (!running){
                    sendToChat("No hay ninguna apuesta en curso galaaCry");
                    return;
                }

                int winner = -1;
                try {
                    Scanner sc = new Scanner(msg.substring(_prefix.length() + winCmd.length()));
                    
                    //Hacer una diferenciación con el caso de DECEIT
                    if (betMode == BotMode.DECEIT){
                        String lugar = sc.next().toLowerCase();
                        if (lugar.equals("win"))
                            winner = 1;
                        else if (lugar.equals("lose"))
                            winner = 0;
                        else{
                            sc.close();
                            throw new IllegalArgumentException("¡Keyword invalida o no especificada!");
                        }
                    } else
                        winner = sc.nextInt();
                    sc.close();
                } catch (Exception e) {
                    e.printStackTrace(); 
                    sendToChat(user + " no especificaste ningún valor. Por favor intenta usar el comando de nuevo galaaRage");
                    return;
                }

                if (betting)
                    betting = false;

                if (votes.containsKey(winner)){
                    LinkedList<String> winners = votes.get(winner);
                    String _mensaje = "¡Tenemos ganador" + ((winners.size() > 1) ? "es" : "") + "!. Felicidades a ";
                    for (int i = 0; i < winners.size(); i++){
                        _mensaje += winners.get(i) + ((i+1 != winners.size()) ? ", " : " ");
                    }
                    _mensaje += "por haber acertado el concurso. ";
                    if (laxycoins != -1);
                        _mensaje += "¡Se " + ((winners.size() > 1) ? "reparten " : "ganó ") + laxycoins + " laxycoins! ♡ ";
                    _mensaje += "Sigan disfrutando del Stream galaaGG";
                    sendToChat(_mensaje);
                } else {
                    //Buscar más cercanos
                    if (votes.size() > 0) {
                        if (_cmdLog)
                            log("*** EMPEZANDO A BUSCAR MÁS CERCANO");
                        LinkedList<String> ganadores = new LinkedList<>();
                        int diff = 1;
                        boolean found = false;
                        while (!found) {
                            if (votes.containsKey(winner + diff) && (winner + diff) <= 100){
                                LinkedList<String> winners = votes.get(winner + diff);
                                for (int i = 0; i < winners.size(); i++){
                                    ganadores.add(winners.get(i) + " (" + (winner + diff) + ")");
                                }
                                found = true;
                            }
                            if (votes.containsKey(winner - diff) && (winner - diff) > 0){
                                LinkedList<String> winners = votes.get(winner - diff);
                                for (int i = 0; i < winners.size(); i++){
                                    ganadores.add(winners.get(i) + " (" + (winner - diff) + ")");
                                }
                                found = true;
                            }

                            if (winner + diff > 100 && winner - diff < 1)
                                break;

                            diff++;
                        }
                        String _winners = (ganadores.size() > 1) ? "Los más cercanos fueron " : "El más cercano fue ";
                        for (int i = 0; i < ganadores.size(); i++){
                            _winners += ganadores.get(i) + ((i+1 != ganadores.size()) ? ", " : ". ");
                        }
                        sendToChat("¡Oops! Nadie ha salido ganador esta vez. " + _winners + ((laxycoins != -1) ? (" Se perdieron " + laxycoins + " laxycoins galaaCry")  : "") + " ¡Mayor suerte para la próxima ♡!");
                    } else
                        sendToChat("¡Oops! Nadie ha salido ganador esta vez." + ((laxycoins != -1) ? (" Se perdieron " + laxycoins + " laxycoins galaaCry")  : "") + " ¡Mayor suerte para la próxima ♡!");
                }
                
                running = false;
            }
        });

        /**
         * >cancelbet
         * Cancela la actual apuesta en curso. Eliminandola completamente
         */
        commands.put(_prefix + "cancelbet", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;
                
                String _mensaje = "";

                if (!running) {
                    _mensaje = "Ninguna apuesta estaba en curso galaaCry";
                } else {
                    _mensaje = "Se ha cancelado la apuesta, nadie a ganado galaaCry";
                }
                
                sendToChat(_mensaje);
                running = false;
                betting = false;
            }
        });

        //////////////////////////////
        //  ♡ COMANDOS DE CUALQUIERA
        //////////////////////////////
        /**
         * >vote <lugar>
         * Permite a los viewers votar para la apuesta actualmente en curso
         * Esta apuesta se ve diferenciada según el modo en que esté actualmente el bot.
         * 
         * ♡ Fornite: Lugar donde creen que quedara la Gala
         * ♡ LoL:     Cuantas Kills creen que conseguirá la Gala
         * ♡ Deceit:  Si ganará o perderá la Gala
         */
        String voteCmd = "vote";
        commands.put(_prefix + voteCmd, new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!betting){
                    sendToChat("¡" + user + " no hay votaciones abiertas! galaaCry");
                    return;
                }

                if (votesReady.contains(user)){
                    sendToChat(user + " ya votaste, por favor espera a los resultados. galaaGG");
                    return;
                }
                
                int place = -1;
                try {
                    Scanner sc = new Scanner(msg.substring(_prefix.length() + voteCmd.length()));
                    //Hacer una diferenciación con el caso de DECEIT
                    if (betMode == BotMode.DECEIT) {
                        String lugar = sc.next().toLowerCase();
                        if (lugar.equals("win"))
                            place = 1;
                        else if (lugar.equals("lose"))
                            place = 0;
                        else {
                            sc.close();
                            throw new IllegalArgumentException("¡Keyword invalida o no especificada!");
                        }
                    } else
                        place = sc.nextInt();
                    sc.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    sendToChat(user + " no especificaste un valor. Por favor intenta usar el comando de nuevo galaaCry");
                    return;
                }

                LinkedList<String> list = new LinkedList<>();
                
                if (!votes.containsKey(place))
                    votes.put(place, list);
                else
                    list = votes.get(place);
                
                list.add(user);
                votesReady.add(user);
                if (_cmdLog)
                    log("*GALA* " + user + " añadido. Lugar: " + place);
            }
        });

        /**
         * >left <lugar>
         * Revisa cuantos viewers siguen participando por el premio.
         * Realiza una distinción según el modo en el que esté el bot actualmente.
         * 
         * ♡ Fornite: Cuenta los que están sobre el lugar especificado (menor a)
         * ♡ LoL:     Cuenta aquellas kills que estén sobre las especificadas (mayor a)
         * ♡ Deceit:  Cuenta a toda la cola
         */
        String leftCmd = "left";
        commands.put(_prefix + leftCmd, new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!isOnGame()){
                    sendToChat("No hay juego al que verle los votantes restantes. (Are you drunk?) galaaWhat");
                    return;
                }

                int place = -1;
                try {
                    Scanner sc = new Scanner(msg.substring(_prefix.length() + leftCmd.length()));
                    
                    //Diferenciación por ser DECEIT
                    if (betMode == BotMode.DECEIT)
                        place = -1;
                    else
                        place = sc.nextInt();
                    sc.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    sendToChat(user + " no especificaste un valor. Por favor intenta usar el comando de nuevo galaaCry");
                    return;
                }

                int _contador = 0;
                if (betMode == BotMode.FORTNITE) {
                    for (int i = 1; i <= place; i++) {
                        if (votes.containsKey(i)) {
                            _contador += votes.get(i).size();
                        }
                    }
                } else if (betMode == BotMode.LOL) {
                    for (int key : votes.keySet()) {
                        if (key >= place) {
                            _contador += votes.get(key).size();
                        }
                    }
                } else if (betMode == BotMode.DECEIT) {
                    if (votes.containsKey(0)){
                        _contador += votes.get(0).size();
                    }
                    if (votes.containsKey(1)){
                        _contador += votes.get(1).size();
                    }
                }

                if (_contador != 0)
                    sendToChat("¡" + user + " queda" + ((_contador > 1) ? ("n " + _contador + " viewers") : (" " + _contador + " viewer")) + " participando ♡ galaaGG!");
                else
                    sendToChat("Ya nadie queda en juego galaaCry");
            }
        });
    }
}
