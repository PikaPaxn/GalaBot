package cl.pencho.galabot.bot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * TODO
 * ♡ Cola para jugar con Gala (Prioridad para Subs) (1-1?)
 * ♡ Implementar interface de opciones (Con que cuenta entrar, que prefijo usar)
 * ♡ Implementar interface de detalles (Modos actuales, cuantos han votado)
 * 
 * POSIBLES
 * ♡ Implementación con Discord?
 * ♡ Creación de Bot en Discord?
 * ♡ Externalizar los textos a archivo de texto plano
 * ♡ Leer comandos de un archivo -> Creación de comandos personalizados en runtime
 */

//////////////////////////////
//      ♡ Main GalaBot ♡
//  Autor:      Pencho
//  Creado:     08-Julio-2018
//  Modificado: 29-Enero-2019
//
//  Bot para la Gala ♡
//////////////////////////////
public class Main {
    public static SimpleBooleanProperty RUNNING = new SimpleBooleanProperty(false);
    
    private static String _nombre;
    private static String _password;
    private static String _channel;
    private static String _prefix;
    private static final boolean[] _logs = new boolean[4];

    private static final HashMap<String, String> oathkeys = new HashMap<>();

    private static GalaBot bot;
    
    public static void main(String[] args) {
        loadOathKeys();
        handleArgs(args);
        System.out.println("Nombre: " + _nombre);
        System.out.println("Pass: " + _password);
        System.out.println("Channel: " + _channel);
        System.out.println("Prefix: " + _prefix);
        System.out.println("verbose, msg, chat, cmd");
        System.out.println(Arrays.toString(_logs));


        bot = new GalaBot(_nombre, _password, _channel, _prefix, _logs);
        bot.sendToChat("¡Estoy listo para trabajar ♡! Usa " + bot.getPrefix() + "help para saber más de mi.");
        RUNNING.setValue(true);

        /**
        * Añadir Handler para "Ctrl+C" en la terminal, de manera
        * que el bot se cierre de buena manera al cerrar el programa
        * de manera forzada.
        */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override public void run() {
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
    
    public static void disconnect(){
        if (bot != null){
            bot.sendToChat("Gracias por llamarme, ¡nos vemos!");
            bot.close();
        }
    }

    /**
     * Carga las keys necesarias para que el bot pueda identificarse
     * de buena manera en los canales de Twitch
     */
    private static void loadOathKeys(){
        try {
//            Scanner sc = new Scanner(new File("resources/oath.keys"));
            Scanner sc = new Scanner(Main.class.getResourceAsStream("/resources/oath.keys"));
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
        _prefix = "!";
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

