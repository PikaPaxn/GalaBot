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
//  Modificado: 18-Octubre-2018
//
//  Bot para la Gala ♡
//  Por el momento contiene funciones para:
//    ♡ General
//    ♡ SubDay
//    ♡ Fortnite
//    ♡ Signs
//    ♡ League of Legends
//    ♡ Deceit
//////////////////////////////
enum BotMode { NONE, FORTNITE, LOL, DECEIT,
               SUBDAY, SIGNS, CONCURSOS; }

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

interface Command {
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

    @Override
    public void handleChatMessage(String user, String msg, String tags){
        super.handleChatMessage(user, msg, tags);
        if (msg.startsWith(_prefix)){
            handleCommand(user, msg.trim(), tags);
        }
    }

    private void handleCommand(String user, String msg, String tags){
        int spaceIndex = msg.indexOf(" ");
        String _command = (spaceIndex != -1) ? msg.substring(0, spaceIndex) : msg;
        if (commands.containsKey(_command)){
            commands.get(_command).execute(user, msg, tags);
        }
    }

    private void addStandardCommands(){
        commands.put(_prefix + "help", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("¡Hola " + user + "! Soy LaxyBot, un fiel sirviente del canal que fue programado por un ocioso con tiempo libre " + 
                    "para poder ayudar con necesidades especificas del Stream. Mi prefijo actual es " +
                    _prefix + ". Para ver mis comandos disponibles usa " + _prefix + "commands");
                    //_prefix);
            }
        });

        commands.put(_prefix + "commands", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("Para ver una lista de todos mis comandos, ingresa a este link ♡: http://bit.ly/laxybotcommands");
            }
        });

        commands.put(_prefix + "kappa", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("Kappa");
            }
        });

        commands.put(_prefix + "mesa", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("(ノಠ益ಠ)ノ彡┻━┻");
            }
        });

        commands.put(_prefix + "pencho", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("Mi Dios creador, el mod más querido de todos los niños, el favorito de todos y aquel que siempre estará en sus corazones. (づ￣ ³￣)づ");
            }
        });

        commands.put(_prefix + "pingu", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("bit.ly/pingubot");
            }
        });

        commands.put(_prefix + "what", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("what");
            }
        });

        commands.put(_prefix + "mileto", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("#MiletoLovers (づ￣ ³￣)づ");
            }
        });
    }

    //////////////////////////////
    //      ♡ COMUN ♡
    //  Variables comunes para todo el bot.
    private BotMode actualMode = BotMode.NONE;
    private Random rand = new Random();
    //////////////////////////////

    //////////////////////////////
    //      ♡ COLAS ♡
    //  ♡ SUBDAY
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
        return (actualMode == BotMode.CONCURSOS ||
                actualMode == BotMode.SIGNS ||
                actualMode == BotMode.SUBDAY);
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

    private void addQueueCommands(){
        loadPrizes();

        //////////////////////////////
        //  STREAMER O MODS
        //////////////////////////////
        //  - subday        | Settea el modo a SubDay
        commands.put(_prefix + "subday", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";

                //Se estaba usando en un juego y quedó una apuesta abierta
                if (running) {
                    _mensaje = "Hay una apuesta en curso, por favor terminala antes de cambiar de modo ♡";
                } else {
                    _mensaje = "♡ Se ha seteado el modo a SubDay ♡ ¡Envíen " + _prefix + "join para unirse a la cola!";
                    actualMode = BotMode.SUBDAY;
                    queueOpen = true;
                }

                sendToChat(_mensaje);
            }
        });

        //  - signs         | Settea el modo a Signs
        commands.put(_prefix + "signs", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";

                //Se estaba usando en un juego y quedó una apuesta abierta
                if (running) {
                    _mensaje = "Hay una apuesta en curso, por favor terminala antes de cambiar de modo ♡";
                } else {
                    _mensaje = "♡ Se ha seteado el modo a Signs ♡";
                    actualMode = BotMode.SIGNS;
                }

                sendToChat(_mensaje);
            }
        });

        // - concursos      | Settea el modo a Concursos
        commands.put(_prefix + "concursos", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";

                //Se estaba usando en un juego y quedó una apuesta abierta
                if (running) {
                    _mensaje = "Hay una apuesta en curso, por favor terminala antes de cambiar de modo ♡";
                } else {
                    _mensaje = "♡ Se ha seteado el modo a Concursos ♡ ¡Envíen " + _prefix + "join para unirse a la cola!";
                    actualMode = BotMode.CONCURSOS;
                    queueOpen = true;
                }

                sendToChat(_mensaje);
            }
        });

        //  - exitqueue     | Cierra las colas y limpia todo.
        commands.put(_prefix + "exitqueue", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";
                
                //Definir mensaje según caso
                switch(actualMode){
                    case SUBDAY:    _mensaje = "¡El SubDay se ha Cerrado! Gracias por participar ♡"; break;
                    case SIGNS:     _mensaje = "¡Los Signs se han terminado! Gracias a todos quienes canjearon ♡"; break;
                    case CONCURSOS: _mensaje = "¡El Concurso se ha acabado! Gracias a todos los concursantes ♡"; break;
                    default:        _mensaje = "No hay ninguna cola que terminar. (Are you drunk?)" ;
                }

                //Limpiar
                queueOpen = false;
                queue.clear();
                onQueue.clear();
                queueReady.clear();
                actualUser = null;
                actualMode = BotMode.NONE;

                sendToChat(_mensaje);
            }
        });

        //  - closequeue    | Cierra la cola
        commands.put(_prefix + "closequeue", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";
            
                //Definir mensaje según caso
                if (isOnQueue()){
                    _mensaje = "¡La cola se ha cerrado! Sigan sintonizando para ver como le va a los restantes ♡";
                    queueOpen = false;
                } else {
                    _mensaje = "No hay ninguna cola que cerrar. (Are you drunk?)";
                }

                sendToChat(_mensaje);
            }
        });

        //  - addqueue <user|s> | Añade usuarios a la cola.
        String addQueueCmd = "addqueue";
        commands.put(_prefix + addQueueCmd, new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";

                if (!isOnQueue()){
                    _mensaje = "No hay ninguna cola a la que añadirle usuarios. (Are you drunk?)";
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
                    switch(actualMode){
                        case SUBDAY:    _mensaje = "Se han agregado correctamente los subs a la cola"; break;
                        case SIGNS:     _mensaje = "Se han agregado correctamente los signs a la cola"; break;
                        case CONCURSOS: _mensaje = "Se han agregado correctamente los concursante a la cola"; break;
                        default: break;
                    }
                } else {
                    _mensaje = user + " no especificaste ningún usuario. Intenta usar el comando de nuevo :(";
                }
                sendToChat(_mensaje);
            }
        });

        //  - next          | Hace pasar al siguiente en la cola.
        commands.put(_prefix + "next", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";

                if (!isOnQueue()){
                    _mensaje = "No hay cola a la que hacer avanzar. (Are you drunk?)";
                    sendToChat(_mensaje);
                    return;
                }

                if (!queueReady.contains(actualUser) && actualUser != null)
                    queueReady.add(actualUser);

                String next = queue.poll();
                if (next != null) {
                    if (actualMode == BotMode.SUBDAY) {
                        int _count = Integer.parseInt(next.substring(next.lastIndexOf("@") + 1));
                        next = next.substring(0, next.lastIndexOf("@"));

                        switch (_count) {
                            case 0:  actualTier = SubTier.ALIEN_GRIS;   break;
                            case 3:  actualTier = SubTier.ALIEN_VERDE;  break;
                            case 6:  actualTier = SubTier.ALIEN_DORADO; break;
                            case 12: actualTier = SubTier.ALIEN_MORADO; break;
                        }

                        _mensaje = "¡ @" + next + " eres un alien " + actualTier.getColor() + " ! Por favor usa " + _prefix + "spin para girar tu ruleta galáctica correspondiente ♡";
                    } else {
                        _mensaje = "Ahora es turno de: " + next;
                    }
                    onQueue.remove(next);
                    actualUser = next;
                } else {
                    _mensaje = "¡La cola está vacia!";
                }

                sendToChat(_mensaje);
                hasSpin = false;
            }
        });

        //  - otro          | Envia al user actual al final de la cola, para cuando no responden a tiempo.
        commands.put(_prefix + "otro", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje;

                if (!isOnQueue()) {
                    _mensaje = "No hay cola abierta para mandar a alguien al agua. (Are you drunk?)";
                    sendToChat(_mensaje);
                    return;
                }


                if (actualUser == null){
                    switch(actualMode){
                        case SUBDAY:    _mensaje = "No habia nadie participando. No puedes enviar al agua a la nada ¯\\_(ツ)_/¯"; break;
                        case SIGNS:     _mensaje = "No habia nadie con Sign. No puedes enviar al agua a la nada ¯\\_(ツ)_/¯"; break;
                        case CONCURSOS: _mensaje = "No habia nadie concursando. No puedes enviar al agua a la nada ¯\\_(ツ)_/¯"; break;
                        default: _mensaje = "";
                    }

                    sendToChat(_mensaje);
                    return;
                }


                _mensaje = actualUser + " se ha ido al agua y deberá nadar hasta el final de la cola :(.";
                if (actualMode == BotMode.SUBDAY){
                    queue.add(actualUser + "@" + actualTier.getMeses());
                } else {
                    queue.add(actualUser);
                }

                String next = queue.poll();
                if (next != actualUser) {
                    if (actualMode == BotMode.SUBDAY){
                        int _count = Integer.parseInt(next.substring(next.lastIndexOf("@") + 1));
                        next = next.substring(0, next.lastIndexOf("@"));

                        switch (_count) {
                            case 0:  actualTier = SubTier.ALIEN_GRIS;   break;
                            case 3:  actualTier = SubTier.ALIEN_VERDE;  break;
                            case 6:  actualTier = SubTier.ALIEN_DORADO; break;
                            case 12: actualTier = SubTier.ALIEN_MORADO; break;
                        }
                    }
                    _mensaje += " Ahora es turno de: ";
                    onQueue.remove(next);
                    onQueue.add(actualUser);
                    actualUser = next;

                    if (actualMode == BotMode.SUBDAY){
                        _mensaje +=  "@" + next + " Por favor usa " + _prefix + "spin para girar tu ruleta galáctica correspondiente ♡";
                    } else {
                        _mensaje += next;
                    }

                } else {
                    _mensaje += " Sin embargo no quedaba nadie en la cola, por lo que sigue siendo el turno de: " + actualUser;
                }

                sendToChat(_mensaje);
                hasSpin = false;
            }
        });

        //////////////////////////////
        //  CUALQUIERA
        //////////////////////////////
        //  - join          | Entra a la cola para el concurso de este día.
        commands.put(_prefix + "join", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                if (!isOnQueue()){
                    _mensaje = user + " no hay cola a la que te puedas añadir. (Are you drunk?)";
                    sendToChat(_mensaje);
                    return;
                }

                if (!queueOpen){
                    _mensaje = "Lo siento, la cola está cerrada ¯\\_(ツ)_/¯";
                    sendToChat(_mensaje);
                    return;
                }

                switch (actualMode) {
                    case SUBDAY:
                        if (!checkSub(tags)) {
                            _mensaje = "Al SubDay solo pueden entrar subs :(";
                            sendToChat(_mensaje);
                            return;
                        }
                        break;
                    case CONCURSOS:
                        break;
                    case SIGNS:
                        _mensaje = "Por favor pidele a un Mod que te añada a la cola ♡";
                        sendToChat(_mensaje);
                        return;
                    default: return;
                }

                //Añadir que tipo de sub es a la Key
                String _key = user;
                if (actualMode == BotMode.SUBDAY) {
                    _key += "@" + subsRow(tags);
                }

                if (onQueue.contains(_key))
                    _mensaje = "¡" + user + " ya está en la cola!";
                else if (queueReady.contains(_key))
                    _mensaje = "¡" + user + " ya participó de este SubDay!";
                else {
                    queue.add(_key);
                    onQueue.add(_key);
                    _mensaje = "¡" + user + " ha sido agregado a la cola!";
                }

                sendToChat(_mensaje);
            }
        });

        //  - spin          | Gira tu rueda galactica correspondiente
        commands.put(_prefix + "spin", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                if (actualMode != BotMode.SUBDAY){
                    sendToChat("No estamos en SubDay. ¡No puedes girar nada!");
                    return;
                }

                if (user.compareTo(actualUser) != 0){
                    log("|" + user + " != " + actualUser + "|");
                    return;
                }

                if (hasSpin){
                    sendToChat("¡Es sólo un lanzamiento! :(");
                    return;
                }

                ArrayList<String> _prizes = subdayPrizes.get(actualTier);
                int p = rand.nextInt(_prizes.size());

                _mensaje = "Gracias por participar " + actualUser + " ♡. Tu premio es: " + _prizes.get(p);
                sendToChat(_mensaje);
                hasSpin = true;
            }
        });        

        //  - checknext     | Revisa quien es el siguiente en la cola.
        commands.put(_prefix + "checknext", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                if (isOnQueue()){
                    String next = queue.peek();
                    //Hacer la corrección del sub si es SUBDAY
                    if (actualMode == BotMode.SUBDAY) {
                        next = next.substring(0, next.lastIndexOf("@"));
                    }
                    _mensaje = (next != null) ? "El siguiente en la lista es: " + next : "¡La cola está vacia!";
                } else {
                    _mensaje = "No hay ninguna cola a quien revisarle el siguiente. (Are you drunk?)";
                }

                sendToChat(_mensaje);
            }
        });

        //  - actual        | Revisa a quien le toca ahora 
        commands.put(_prefix + "actual", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";
                
                switch(actualMode){
                    case CONCURSOS:
                        _mensaje = ((actualUser != null) ? "El concursante actual es: " + actualUser : " ¡No hay nadie concursando!");
                        break;
                    case SUBDAY:
                        _mensaje = ((actualUser != null) ? "El participante actual es: " + actualUser : " ¡No hay nadie participando!");
                        break;
                    case SIGNS:
                        _mensaje = ((actualUser != null) ? "El sign actual es: " + actualUser : " ¡No hay nadie para el sign!");
                        break;
                    default:
                        _mensaje = "No hay ninguna cola a quien revisarle el actual. (Are you drunk?)";
                        break;
                }

                sendToChat(_mensaje);
            }
        });

        //  - queueleft     | Revisa cuantos subs quedan en la cola
        commands.put(_prefix + "queueleft", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";
                
                int left = queue.size();
                String _moreThanOne;
                String _one;
                String _none = "¡No queda nadie más en la cola";

                switch(actualMode) {
                    case SUBDAY:
                        _moreThanOne = "¡Quedan " + left + " subs en la cola!";
                        _one = "¡Queda solo 1 sub en la cola!";
                        break;
                    case SIGNS:
                        _moreThanOne = "¡Quedan " + left + " signs en la cola!";
                        _one = "¡Queda solo 1 sign en la cola!";
                        break;
                    case CONCURSOS:
                        _moreThanOne = "¡Quedan " + left + " concursantes en la cola!";
                        _one = "¡Queda solo 1 concursante en la cola!";
                        break;
                    default:
                        _mensaje = "No hay ninguna cola a quien revisarle los restantes. (Are you drunk?)";
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
        return (actualMode == BotMode.FORTNITE ||
                actualMode == BotMode.LOL ||
                actualMode == BotMode.DECEIT);
    }

    private void addGameCommands(){
        //////////////////////////////
        //  STREAMER O MODS
        //////////////////////////////
        //  - lol                   | Settea el modo en LoL
        commands.put(_prefix + "lol", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";

                if (running) {
                    _mensaje = "Hay una apuesta en curso, por favor terminala antes de cambiar de modo ♡";
                } else {
                    _mensaje = user + " se ha seteado el modo a League of Legends ♡";
                    actualMode = BotMode.LOL;
                }

                sendToChat(_mensaje);
            }
        });

        //  - fortnite              | Settea el modo en Fortnite
        commands.put(_prefix + "fortnite", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";

                if (running) {
                    _mensaje = "Hay una apuesta en curso, por favor terminala antes de cambiar de modo ♡";
                } else {
                    _mensaje = user + " se ha seteado el modo a Fortnite ♡";
                    actualMode = BotMode.FORTNITE;
                }

                sendToChat(_mensaje);
            }
        });

        //  - deceit                | Settea el modo en Deceit
        commands.put(_prefix + "deceit", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";

                if (running) {
                    _mensaje = "Hay una apuesta en curso, por favor terminala antes de cambiar de modo ♡";
                } else {
                    _mensaje = user + " se ha seteado el modo a Deceit ♡";
                    actualMode = BotMode.DECEIT;
                }

                sendToChat(_mensaje);
            }
        });

        //  - nogame                | Settea el modo en Ningún juego
        commands.put(_prefix + "nogame", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";

                if (running) {
                    _mensaje = "Hay una apuesta en curso, por favor terminala antes de cambiar de modo ♡";
                } else {
                    _mensaje = user + " se ha vuelto el modo a nada ♡";
                    actualMode = BotMode.NONE;
                }

                sendToChat(_mensaje);
            }
        });

        //  - startbet <laxys>      | Inicia las votaciones por el lugar donde quedó la Gala (Fortnite), por cuantas kills obtendrá (LoL), o si ganó (Deceit)
        String betCmd = "startbet";
        commands.put(_prefix + betCmd, new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                if (!isOnGame()) {
                    sendToChat("¡No hay ningún juego setteado!. Por favor usa " + _prefix + "lol, " + _prefix + "fortnite, o " + _prefix + "deceit antes de iniciar la votación ♡");
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
                    _mensaje += "¡Las apuestas se han activado! ";
                else
                    _mensaje += "¡Las apuestas ya se encontraban activadas! ";
                
                _mensaje += " ♡ ¡Envia " + _prefix + "vote ";

                if (actualMode == BotMode.FORTNITE)
                    _mensaje += "<lugar>";
                else if (actualMode == BotMode.LOL)
                    _mensaje += "<kills>";
                else if (actualMode == BotMode.DECEIT)
                    _mensaje += "<win|lose>";

                _mensaje += " para apostar! ♡ ¡Que comience el juego ♡!";
                
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

        //  - closebet          | Cierra las votaciones por el <lugar donde quedó>/<kills que obtuvo>/<ganó|perdió> la Gala
        commands.put(_prefix + "closebet", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                if (!running) {
                    sendToChat("No hay ninguna apuesta en curso :(");
                    return;
                }

                sendToChat(user + ((betting) ? " ¡Las apuestas se han cerrado!" : " ¡Las apuestas ya se encontraban cerradas!") +
                    " ¡Estén atentos al resultado!");
                betting = false;
            }
        });

        //  - resetbet          | Reinicia las votaciones, para no tener que cerrar y volver a abrir
        commands.put(_prefix + "resetbet", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                if (!running){
                    sendToChat("No hay ninguna apuesta en curso :(");
                    return;
                }

                votesReady.clear();
                votes.clear();

                sendToChat(user + " ¡Apuestas reiniciadas! ♡ ¡Envia " + _prefix + "vote " + ((actualMode == BotMode.FORTNITE) ? "<lugar>" : "<kills>") + " para apostar! ♡");
                betting = true;
            }
        });

        //  - winner <lugar>    | Define el lugar ganador y revisa si es que hay ganadores.
        String winCmd = "winner";
        commands.put(_prefix + winCmd, new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                if (!running){
                    sendToChat("No hay ninguna apuesta en curso :(");
                    return;
                }

                int winner = -1;
                try {
                    Scanner sc = new Scanner(msg.substring(_prefix.length() + winCmd.length()));
                    
                    //Hacer una diferenciación con el caso de DECEIT
                    if (actualMode == BotMode.DECEIT){
                        String lugar = sc.next().toLowerCase();
                        if (lugar.equals("win"))
                            winner = 1;
                        else if (lugar.equals("lose"))
                            winner = 0;
                        else
                            throw new IllegalArgumentException("¡Keyword invalida o no especificada!");
                    } else
                        winner = sc.nextInt();
                    sc.close();
                } catch (Exception e) {
                    e.printStackTrace(); 
                    sendToChat(user + " no especificaste ningún valor. Por favor intenta usar el comando de nuevo");
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
                    _mensaje += "Sigan disfrutando del Stream :)";
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
                        sendToChat("¡Oops! Nadie ha salido ganador esta vez. " + _winners + ((laxycoins != -1) ? (" Se perdieron " + laxycoins + " laxycoins :(")  : "") + " ¡Mayor suerte para la próxima ♡!");
                    } else
                        sendToChat("¡Oops! Nadie ha salido ganador esta vez." + ((laxycoins != -1) ? (" Se perdieron " + laxycoins + " laxycoins :(")  : "") + " ¡Mayor suerte para la próxima ♡!");
                }
                
                running = false;
            }
        });

        //  - cancelbet         | Cancela la apuesta en curso
        commands.put(_prefix + "cancelbet", new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;
                
                String _mensaje = "";

                if (!running) {
                    _mensaje = "Ninguna apuesta estaba en curso ):";
                } else {
                    _mensaje = "Se ha cancelado la apuesta, nadie a ganado ):";
                }
                
                sendToChat(_mensaje);
                running = false;
                betting = false;
            }
        });

        //////////////////////////////
        //  CUALQUIERA
        //////////////////////////////
        //  - vote <lugar>      | Permite a los viewers votar por el lugar/kills en que creen que la Gala quedará
        String voteCmd = "vote";
        commands.put(_prefix + voteCmd, new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!betting){
                    sendToChat("¡" + user + " no hay votaciones abiertas!");
                    return;
                }

                if (votesReady.contains(user)){
                    sendToChat(user + " ya votaste, por favor espera a los resultados.");
                    return;
                }
                
                int place = -1;
                try {
                    Scanner sc = new Scanner(msg.substring(_prefix.length() + voteCmd.length()));
                    //Hacer una diferenciación con el caso de DECEIT
                    if (actualMode == BotMode.DECEIT) {
                        String lugar = sc.next().toLowerCase();
                        if (lugar.equals("win"))
                            place = 1;
                        else if (lugar.equals("lose"))
                            place = 0;
                        else
                            throw new IllegalArgumentException("¡Keyword invalida o no especificada!");
                    } else
                        place = sc.nextInt();
                    sc.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    sendToChat(user + " no especificaste un valor. Por favor intenta usar el comando de nuevo");
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

        //  - left <lugar>      | Permite ver cuantos viewers están aún participando para ganar
        String leftCmd = "left";
        commands.put(_prefix + leftCmd, new Command(){
            @Override public void execute(String user, String msg, String tags) {
                if (!isOnGame()){
                    sendToChat("No hay juego al que verle los votantes restantes. (Are you drunk?)");
                    return;
                }

                int place = -1;
                try {
                    Scanner sc = new Scanner(msg.substring(_prefix.length() + leftCmd.length()));
                    
                    //Diferenciación por ser DECEIT
                    if (actualMode == BotMode.DECEIT)
                        place = -1;
                    else
                        place = sc.nextInt();
                    sc.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    sendToChat(user + " no especificaste un valor. Por favor intenta usar el comando de nuevo");
                    return;
                }

                int _contador = 0;
                if (actualMode == BotMode.FORTNITE) {
                    for (int i = 1; i <= place; i++) {
                        if (votes.containsKey(i)) {
                            _contador += votes.get(i).size();
                        }
                    }
                } else if (actualMode == BotMode.LOL) {
                    for (int key : votes.keySet()) {
                        if (key >= place) {
                            _contador += votes.get(key).size();
                        }
                    }
                } else if (actualMode == BotMode.DECEIT) {
                    if (votes.containsKey(0)){
                        _contador += votes.get(0).size();
                    }
                    if (votes.containsKey(1)){
                        _contador += votes.get(1).size();
                    }
                }

                if (_contador != 0)
                    sendToChat("¡" + user + " queda" + ((_contador > 1) ? ("n " + _contador + " viewers") : (" " + _contador + " viewer")) + " participando ♡!");
                else
                    sendToChat("Ya nadie queda en juego :(");
            }
        });
    }
}
