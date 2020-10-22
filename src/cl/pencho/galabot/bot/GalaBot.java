package cl.pencho.galabot.bot;

import cl.pencho.galabot.util.CmdCategory;
import cl.pencho.galabot.util.SubTier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import cl.pencho.galabot.util.ICommand;
import cl.pencho.galabot.util.UserCategory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

//////////////////////////////
//      ♡ GalaBot ♡
//  Autor:      Pencho
//  Creado:     08-Julio-2018
//  Modificado: 22-Octubre-2020
//  Bot dedicado especificamente al stream
//  de Galaxias, configurado para ello y todas sus necesidades ♡
//
//  Por el momento contiene funciones para:
//    ♡ General
//    ♡ Gala Awards
//    ♡ SubDay
//    ♡ Navidad (Evento)
//    ♡ Concursos
//    ♡ Signs
//    ♡ Fortnite
//    ♡ League of Legends
//    ♡ Deceit
//////////////////////////////
public class GalaBot extends TwitchBot {
    private static String BOT_VERSION = "v1.2.4";
    
    //////////////////////////////
    //  ♡ CONFIGURACIONES BASICAS
    //////////////////////////////
    public String getPrefix() { return _prefix; }
    private HashMap<String, ICommand> commands = new HashMap<>();
    public GalaBot(String nombre, String password, String channel, String prefix, boolean[] logs) {
        super(nombre, password, channel, logs);
        super._prefix = prefix;
        System.out.println("Galabot Version: " + BOT_VERSION);
        addStandardCommands();
        addTextCommands();
        addGalaAwardsCommands();
        //addQueueCommands();
        //addGameCommands();
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
            boolean mod = checkModOrStreamer(tags);
            log("*** Handleling Message: " + msg + " Mod?: " + mod);
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
        commands.put(_prefix + "help", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Envia una pequeña descripción del bot al chat."; }
            
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
        commands.put(_prefix + "commands", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Envía un link con la lista de todos los comandos al chat."; }
            
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("Para ver una lista de todos mis comandos, ingresa a este link ♡: http://bit.ly/laxybotcommands");
            }
        });

        /**
         * >kappa
         * Kappa
         */
        commands.put(_prefix + "kappa", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Kappa."; }
            
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("Kappa");
            }
        });

        /**
         * >mesa
         * Envía un emoticon de "Rage" al chat
         */
        commands.put(_prefix + "mesa", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Envía un emoticon de \"Rage\" al chat."; }
            
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("(ノಠ益ಠ)ノ彡┻━┻");
            }
        });

        /**
         * >pencho
         * Comando que entrega una breve descripción del creador del bot.
         */
        commands.put(_prefix + "pencho", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Entrega una breve descripción del creador del bot."; }
            
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("Mi Dios creador, el mod más querido de todos los niños, el favorito de todos y aquel que siempre estará en sus corazones. (づ￣ ³￣)づ");
            }
        });

        /**
         * >pingu
         * Comando que envía un gif de pingu bailando
         */
        commands.put(_prefix + "pingu", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Envía un gif de pingu bailando."; }
            
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("bit.ly/pingubot");
            }
        });

        /**
         * >what
         * What?
         */
        commands.put(_prefix + "what", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "what"; }
            
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("what galaaWhat");
            }
        });

        /**
         * >mileto
         * Comando que muestra lo mucho que amamos a Mileto ♡
         */
        commands.put(_prefix + "mileto", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra todo el amor a Mileto ♡"; }
            
            @Override public void execute(String user, String msg, String tags) {
                sendToChat("#MiletoLovers (づ￣ ³￣)づ");
            }
        });

        /**
         * >hype
         * Genera una cadena de Hype en el chat
         */
        String hypeCmd = "hype";
        commands.put(_prefix + hypeCmd, new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Spammea galaaHypes en el chat (Max 30)"; }
            
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

                if (repeticiones > 30){
                    repeticiones = 30;
                }

                for (int i = 0; i < repeticiones; i++)
                    _msg += " galaaHype";

                sendToChat(_msg);
            }
        });
        
        /**
         * >sad
         * Genera una cadena de sad en el chat
         */
        String sadCmd = "sad";
        commands.put(_prefix + sadCmd, new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Spammea galaaSad en el chat (Max 30)"; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";
                int repeticiones = -1;
                try {
                    Scanner sc = new Scanner(msg.substring(_prefix.length() + sadCmd.length()));
                    repeticiones = sc.nextInt();
                    sc.close();
                } catch (Exception e) {
                    repeticiones = 10;
                }

                if (repeticiones > 30){
                    repeticiones = 30;
                }

                for (int i = 0; i < repeticiones; i++)
                    _msg += " galaaSad";

                sendToChat(_msg);
            }
        });

        /**
         * >actualmode
         * Comando que notifica el modo actual en que se encuentra el bot.
         */
        commands.put(_prefix + "actualmode", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra el modo actual que tiene el bot."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "El juego actual es: " + betMode.toString() + " | La cola actual es: " + queueMode.toString() + " galaaGG";

                sendToChat(_msg);
            }
        });

        /**
         * >followage
         * Comando que muestra cuanto tiempo ha estado siguiendo el usuario a Galaxias.
         */
        commands.put(_prefix + "followage", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra el tiempo que lleva siguiendo a Galaxias."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                //Try to get from a certain api
                try {
                    String curl = "https://2g.be/twitch/following.php?user=" + user + "&channel=" + getChannel() + "&format=mwdhms";

                    URLConnection url = new URL(curl).openConnection();
                    url.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.getInputStream(), "UTF-8"))) {
                        _msg += user + " sigue a Galaxias hace ";
                        
                        for (String line; (line = reader.readLine()) != null;) {
                            
                            //Transformar la linea de ingles a un texto entendible en español.
                            int yearsIdx = line.indexOf("year");
                            int monthIdx = line.indexOf("month");
                            int weeksIdx = line.indexOf("week");
                            int daysIdx = line.indexOf("day");
                            String temp = "";
                            boolean hasWord = false;
                            int number;
                            
                            if (yearsIdx != -1) {
                                temp = line.substring(0, yearsIdx - 1);
                                number = Integer.parseInt(temp.substring(temp.lastIndexOf(" ") + 1));
                                if (number > 1)
                                    _msg += number + " años";
                                else
                                    _msg += number + " año";
                                hasWord = true;
                            }
                            
                            if (monthIdx != -1) {
                                temp = line.substring(0, monthIdx - 1);
                                if (hasWord)
                                    _msg += ", ";
                                number = Integer.parseInt(temp.substring(temp.lastIndexOf(" ") + 1));
                                if (number > 1)
                                    _msg += number + " meses";
                                else
                                    _msg += number + " mes";
                                hasWord = true;
                            }
                            
                            if (weeksIdx != -1) {
                                temp = line.substring(0, weeksIdx - 1);
                                if (hasWord)
                                    _msg += ", ";
                                number = Integer.parseInt(temp.substring(temp.lastIndexOf(" ") + 1));
                                if (number > 1)
                                    _msg += number + " semanas";
                                else
                                    _msg += number + " semana";
                                hasWord = true;
                            }
                            
                            if (daysIdx != -1) {
                                temp = line.substring(0, daysIdx - 1);
                                if (hasWord)
                                    _msg += ", ";
                                number = Integer.parseInt(temp.substring(temp.lastIndexOf(" ") + 1));
                                if (number > 1)
                                    _msg += number + " días";
                                else
                                    _msg += number + " día";
                                hasWord = true;
                            }
                        }
                        
                        _msg += " galaaPog";
                    }
                } catch (Exception ex) {
                    log("No se pudo conseguir la conexión. " + ex.getMessage());
                    _msg = "Servicio no disponible temporalmente galaaSad";
                }

                sendToChat(_msg);
            }
        });

        /**
         * >uptime
         * Comando que muestra cuanto tiempo ha estado stremeando Galaxias.
         */
        commands.put(_prefix + "uptime", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra el tiempo que lleva stremeando Galaxias."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                //Try to get from a certain api
                try {
                    String curl = "http://www.decapi.me/twitch/uptime.php?channel=" + getChannel();

                    URLConnection url = new URL(curl).openConnection();
                    url.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.getInputStream(), "UTF-8"))) {
                        _msg += "Galaxias lleva stremeando ";
                        
                        for (String line; (line = reader.readLine()) != null;) {
                            
                            //Transformar la linea de ingles a un texto entendible en español.
                            int daysIdx = line.indexOf("day");
                            int hourIdx = line.indexOf("hour");
                            int minuteIdx = line.indexOf("minute");
                            int secondIdx = line.indexOf("second");
                            String temp = "";
                            boolean hasWord = false;
                            int number;
                            
                            line = " " + line;
                            
                            if (daysIdx != -1) {
                                temp = line.substring(0, daysIdx - 1);
                                if (hasWord)
                                    _msg += ", ";
                                number = Integer.parseInt(temp.substring(temp.lastIndexOf(" ") + 1));
                                if (number > 1)
                                    _msg += number + " días";
                                else
                                    _msg += number + " día";
                                hasWord = true;
                            }
                            
                            if (hourIdx != -1) {
                                temp = line.substring(0, hourIdx - 1);
                                if (hasWord)
                                    _msg += ", ";
                                number = Integer.parseInt(temp.substring(temp.lastIndexOf(" ") + 1));
                                if (number > 1)
                                    _msg += number + " horas";
                                else
                                    _msg += number + " hora";
                                hasWord = true;
                            }
                            
                            if (minuteIdx != -1) {
                                temp = line.substring(0, minuteIdx - 1);
                                if (hasWord)
                                    _msg += ", ";
                                number = Integer.parseInt(temp.substring(temp.lastIndexOf(" ") + 1));
                                if (number > 1)
                                    _msg += number + " minutos";
                                else
                                    _msg += number + " minuto";
                                hasWord = true;
                            }
                            
                            if (secondIdx != -1) {
                                temp = line.substring(0, secondIdx - 1);
                                if (hasWord)
                                    _msg += ", ";
                                number = Integer.parseInt(temp.substring(temp.lastIndexOf(" ") + 1));
                                if (number > 1)
                                    _msg += number + " segundos";
                                else
                                    _msg += number + " segundo";
                                hasWord = true;
                            }
                        }
                        
                        _msg += " galaaGG";
                    }
                } catch (Exception ex) {
                    log("No se pudo conseguir la conexión. " + ex.getMessage());
                    _msg = "Servicio no disponible temporalmente galaaSad";
                }

                sendToChat(_msg);
            }
        });
    }
    
    //////////////////////////////
    //      ♡ TEXT COMMANDS ♡
    //  Estos comandos deberian moverse a un archivo aparte,
    //  y dar la posibilidad de ser editados sin editar el
    //  codigo del bot uwu.
    //////////////////////////////
    private int piscolas = 0;
    private int awa = 0;
    
     /**
     * Metodo que añade comandos de texto básicos.
     */
    private void addTextCommands(){
        //////////////////////////////
        //  ♡ PLATAFORMAS
        //////////////////////////////
        /**
         * >blizzard
         * Comando que muestra el nick de Galaxias en Blizzard
         */
        commands.put(_prefix + "blizzard", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Nick en Blizzard."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Mi nick en Blizzard es Galaxias#1387 galaaGG";

                sendToChat(_msg);
            }
        });
        
        /**
         * >riotgames
         * Comando que muestra el nick de Galaxias en Riot Games
         */
        commands.put(_prefix + "riotgames", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Nick en Riot Games."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Mi nick en Riot Games es Galaxias #LAS galaaGG";

                sendToChat(_msg);
            }
        });

        /**
         * >origin
         * Comando que muestra el nick de Galaxias en Origin
         */
        commands.put(_prefix + "origin", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Nick en Origin."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Mi nick en Origin es TwitchGalaxias galaaGG";

                sendToChat(_msg);
            }
        });

        /**
         * >steam
         * Comando que muestra el nick de Galaxias en Steam
         */
        commands.put(_prefix + "steam", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Nick en Steam."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Mi nick en Steam es Galaxias galaaGG";

                sendToChat(_msg);
            }
        });

        /**
         * >epicgames
         * Comando que muestra el nick de Galaxias en Epic Games
         */
        commands.put(_prefix + "epicgames", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Nick en Epic Games."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Mi nick en Epic Games es LCGalaxias galaaGG";

                sendToChat(_msg);
            }
        });

        //////////////////////////////
        //  ♡ REDES SOCIALES
        //////////////////////////////
        /**
         * >ig
         * Comando que muestra el instagram de Galaxias
         */
        commands.put(_prefix + "ig", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Instagram."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Sígueme en instagram ---> www.instagram.com/lcgalaxias galaaQueen";

                sendToChat(_msg);
            }
        });

        /**
         * >twitter
         * Comando que muestra el twitter de Galaxias
         */
        commands.put(_prefix + "twitter", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Twitter."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Sígueme en twitter ---> www.twitter.com/lcgalaxias galaaQueen";

                sendToChat(_msg);
            }
        });

        /**
         * >fb
         * Comando que muestra la fanpage de Galaxias
         */
        commands.put(_prefix + "fb", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Facebook."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Sigue la fanpage ---> www.facebook.com/lcgalaxias galaaQueen";

                sendToChat(_msg);
            }
        });

        /**
         * >army
         * Comando que muestra la army de Galaxias
         */
        commands.put(_prefix + "army", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Galaxias Army."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Únete a la army más galáctica de todo el universo ---> www.facebook.com/groups/galaxiasarmy galaaQueen";

                sendToChat(_msg);
            }
        });

        /**
         * >yt
         * Comando que muestra el youtube de Galaxias
         */
        commands.put(_prefix + "yt", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Youtube."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Sigue mi canal de youtube para ver mis videos más recientes ---> www.youtube.com/galaxias galaaQueen";

                sendToChat(_msg);
            }
        });
        
        /**
         * >discord
         * Comando que muestra el discord de Galaxias
         */
        commands.put(_prefix + "dc", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Discord."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Únete a nuestro canal de discord ---> https://discord.gg/RPPmX4t galaaQueen";

                sendToChat(_msg);
            }
        });

        /**
         * >memelaxias
         * Comando que muestra el instagram de memelaxias
         */
        commands.put(_prefix + "memelaxias", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Memelaxias."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Sigue nuestra página de memes oficial ---> www.instagram.com/memelaxias galaaQueen";

                sendToChat(_msg);
            }
        });

        /**
         * >gs
         * Comando que muestra el mensaje de Gorilla Setups
         */
        commands.put(_prefix + "gs", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Gorilla Setups."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Sigue a Gorilla Setups, la familia más linda ---> www.instagram.com/gorilla.setups galaaQueen";

                sendToChat(_msg);
            }
        });

        //////////////////////////////
        //  ♡ GENERAL
        //////////////////////////////
        /**
         * >xd
         * Comando que xd
         */
        commands.put(_prefix + "xd", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "xd."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Equis dé no más po galaaKappa";

                sendToChat(_msg);
            }
        });
        
        /**
         * >beso
         * Comando que da un beso a otro viewer
         */
        commands.put(_prefix + "beso", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Da un beso a otro viewer."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                if (getActiveUsers().size() < 2) {
                    _msg = "Comando no disponible temporalmente. galaaSad";
                } else {
                    String _otroUser = getActiveUsers().get(rand.nextInt(getActiveUsers().size()));
                    _msg += user + " le ha dado un beso pride a " + _otroUser + " galaaPride";
                }

                sendToChat(_msg);
            }
        });

        /**
         * >guamazo
         * Comando que le da un guamazo a otro viewer
         */
        commands.put(_prefix + "guamazo", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Da un guamazo a otro viewer."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";
                
                if (getActiveUsers().size() < 2) {
                    _msg = "Comando no disponible temporalmente. galaaSad";
                } else {
                    String _otroUser = getActiveUsers().get(rand.nextInt(getActiveUsers().size()));
                    _msg += user + " le ha dado un guamazo a " + _otroUser + " galaaTilt";
                }

                sendToChat(_msg);
            }
        });

        /**
         * >besotriple
         * Comando que da un beso triple a otros dos viewers
         */
        commands.put(_prefix + "besotriple", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Da un beso triple a otros dos viewers."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";
                
                if (getActiveUsers().size() < 3) {
                    _msg = "Comando no disponible temporalmente. galaaSad";
                } else {
                    int user1 = rand.nextInt(getActiveUsers().size());
                    int user2 = user1;
                    while (user1 == user2){
                        user2 = rand.nextInt(getActiveUsers().size());
                    }

                    _msg += user + " se ha dado un beso triple con " + getActiveUsers().get(user1) + " y " + getActiveUsers().get(user2) + " galaaGasm";
                }

                sendToChat(_msg);
            }
        });

        /**
         * >clip
         * Comando que pide un clip
         */
        commands.put(_prefix + "clip", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Pide un clip."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Clipeen eso galaaPog";

                sendToChat(_msg);
            }
        });

        /**
         * >piscola
         * Comando que cuenta las piscolas
         */
        commands.put(_prefix + "piscola", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Shots de piscola."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";
                piscolas++;
                _msg += "Galaxias se ha tomado " + piscolas + " shots de piscola galaaPisco";

                sendToChat(_msg);
            }
        });

        /**
         * >agua
         * Comando que cuenta los vasos de agua
         */
        commands.put(_prefix + "agua", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Shots de agua."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";
                awa++;
                _msg += "Galaxias se ha tomado " + awa + " sorbitos de agua galaaUWU";

                sendToChat(_msg);
            }
        });

        /**
         * >gh
         * Comando que corrige la House de Galita
         */
        commands.put(_prefix + "gh", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Gorila, no Gaming."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Nonono, no es Gaming House, es Gorilla House galaaTilt";

                sendToChat(_msg);
            }
        });

        /**
         * >uwu
         * Comando que es simplemente uwu
         */
        commands.put(_prefix + "uwu", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Simplemente uwu."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Simplemente momento uwu galaaUWU";

                sendToChat(_msg);
            }
        });

        /**
         * >wsp
         * Comando que da el whatsapp de la Galita
         */
        commands.put(_prefix + "wsp", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Whatsapp."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "galaaKappa";

                sendToChat(_msg);
            }
        });

        /**
         * >elo
         * Comando que muestra el elo actual
         */
        commands.put(_prefix + "elo", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Elo."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Valorant ---> Platino 1 || League of Legends ---> Platino 4 galaaGG";

                sendToChat(_msg);
            }
        });
        
        /**
         * >pc
         * Comando que muestra las especificaciones del pc de la gala
         */
        commands.put(_prefix + "pc", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Pc Specs."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Procesador: R7 3700X || RAM: 32GB 3600Mhz GEiL RGB || Tarjeta de video: ROG-STRIX RTX2070S || Motherboard: X570 AORUS ELITE Wi-Fi galaaQueen";

                sendToChat(_msg);
            }
        });
        
        /**
         * >alargar
         * Comando que muestra las indicaciones para alargar el stream.
         */
        commands.put(_prefix + "alargar", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GENERAL; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Alargar Stream."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _msg = "";

                _msg += "Si quieres alargar el stream, puedes contribuir donando: 1 USD = +3 min || 100 bits = +3 min || Sub Tier 1: +8 min || Sub Tier 2: +20 min || Sub Tier 3: +30 min || Muchas gracias por apoyar el stream galaaMoney";

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
    //      ♡ GALA AWARDS ♡
    //  Modo utilizado para mostrar a los ganadores de los
    //  Gala Awards en los distintos años en que se ha premiado.
    //
    //  ♡ MÁS AMISTOSO
    //  ♡ MÁS OTAKU
    //  ♡ MÁS PRIDE
    //  ♡ MEJOR EDITOR
    //  ♡ MEJOR SHITPOSTER
    //  ♡ MEJOR MOD
    //  ♡ MEJOR SIGN
    //  ♡ MEJOR CLIP
    //  ♡ TÉCNICOS (Mención Honrosa)
    //////////////////////////////

    private String galaAwardsHelp(){
        return "";
    }

    /**
     * Metodo que añade los comandos del Gala Awards al bot. 
     * Estos comandos contienen menciones honrosas a quienes han
     * ganado las distintas menciones en los diferentes años que 
     * los premios se han realizado.
     */
    private void addGalaAwardsCommands(){
        //////////////////////////////
        //  ♡ COMANDOS DE CUALQUIERA
        //////////////////////////////
        /**
         * >masamistoso
         * Muestra en el chat el ganador en la categoría "Más Amistoso"
         */
        commands.put(_prefix + "masamistoso", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra al ganador de \"Más Amistoso\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                _mensaje += "¡Ganador Categoría Más Amistoso!: Mileto (2018), 4_Marichi (2019) galaaHi";

                sendToChat(_mensaje);
            }
        });

        /**
         * >maschistoso
         * Muestra en el chat el ganador de la categoría "Más Chistoso"
         */
        commands.put(_prefix + "maschistoso", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra al ganador de \"Más Chistoso\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                _mensaje += "¡Ganador Categoría Más Chistoso!: ynfinitoqlo (2019) galaaDance";

                sendToChat(_mensaje);
            }
        });

        /**
         * >masotaku
         * Muestra en el chat el ganador en la categoría "Más Otaku"
         */
        commands.put(_prefix + "masotaku", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra al ganador de \"Más Otaku\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                _mensaje += "¡Ganador Categoría Más Otaku!: Bascu (2018), Madware1 (2019) galaaGG";

                sendToChat(_mensaje);
            }
        });

        /**
         * >maspride
         * Muestra en el chat el ganador en la categoría "Más Pride"
         */
        commands.put(_prefix + "maspride", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra al ganador de \"Más Pride\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                _mensaje += "¡Ganador Categoría Más Pride!: Mileto (2018), 4_marichi (2019) galaaPride";

                sendToChat(_mensaje);
            }
        });

        /**
         * >besteditor
         * Muestra en el chat el ganador en la categoría "Mejor Editor"
         */
        commands.put(_prefix + "besteditor", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra al ganador de \"Mejor Editor\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                _mensaje += "¡Ganador Categoría Mejor Editor!: Xan (2018), 4_marichi (2019) galaaHype";

                sendToChat(_mensaje);
            }
        });

        /**
         * >bestshitposter
         * Muestra en el chat el ganador en la categoría "Mejor Shitposter"
         */
        commands.put(_prefix + "bestshitposter", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra al ganador de \"Mejor Shitposter\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                _mensaje += "¡Ganador Categoría Mejor Shitposter!: Marichi (2018) galaaGasm";

                sendToChat(_mensaje);
            }
        });

        /**
         * >bestmod
         * Muestra en el chat el ganador en la categoría "Mejor Mod"
         */
        commands.put(_prefix + "bestmod", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra al ganador de \"Mejor Mod\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                _mensaje += "¡Ganador Categoría Mejor Moderador!: Dskater (2018), 4_marichi (2019) galaaUWU";

                sendToChat(_mensaje);
            }
        });

        /**
         * >bestvip
         * Muestra en el chat el ganador en la categoría "Mejor VIP"
         */
        commands.put(_prefix + "bestvip", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra al ganador de \"Mejor VIP\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                _mensaje += "¡Ganador Categoría Mejor VIP!: Luketiix (2019) galaaDance";

                sendToChat(_mensaje);
            }
        });

        /**
         * >bestsign
         * Muestra en el chat el ganador en la categoría "Mejor Sign"
         */
        commands.put(_prefix + "bestsign", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra al ganador de \"Mejor Sign\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                _mensaje += "¡Ganador Categoría Mejor Sign!: Eevee (2018) http://bit.ly/galaSign2018 Sasuke (2019) http://bit.ly/galaSign2019 galaaKappa";

                sendToChat(_mensaje);
            }
        });

        /**
         * >bestclip
         * Muestra en el chat el ganador en la categoría "Mejor Clip"
         */
        commands.put(_prefix + "bestclip", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra al ganador de \"Mejor Clip\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                //TODO: FALTA EL LINK
                _mensaje += "¡Ganador Categoría Mejor Clip!: Me mataron a puro pico (2018) http://bit.ly/galaClip2018 Sacada de Chucha (2019) galaaGasm";

                sendToChat(_mensaje);
            }
        });

        /**
         * >bestship
         * Muestra en el chat los ganadores en la categoría "Mejor Ship"
         */
        commands.put(_prefix + "bestship", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra al ganador de la \"Mejor Ship\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                _mensaje += "¡Ganadores Categoría Mejor Ship!: Guonejo X Cits (2019) galaaPride";

                sendToChat(_mensaje);
            }
        });

        /**
         * >bestimitador
         * Muestra en el chat el ganador en la categoría "Mejor Imitador"
         */
        commands.put(_prefix + "bestimitador", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra al ganador de la \"Mejor Imitador\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                _mensaje += "¡Ganadores Categoría Mejor Imitador!: RoygSnake (2019) galaaGG";

                sendToChat(_mensaje);
            }
        });

        /**
         * >bestcantante
         * Muestra en el chat el ganador en la categoría "Mejor Cantante"
         */
        commands.put(_prefix + "bestcantante", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra al ganador de la \"Mejor Cantante\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                _mensaje += "¡Ganadores Categoría Mejor Cantante!: Marthin1010 (2019) galaaDance";

                sendToChat(_mensaje);
            }
        });

        /**
         * >topct
         * Muestra en el chat el ganador en la categoría "Top Donador Cuenta Rut"
         */
        commands.put(_prefix + "topct", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra al ganador de la \"Top Donador Cuenta Rut\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                _mensaje += "¡Ganadores Categoría Top Donador Cuenta Rut!: dskater88 (2019) galaaMoney";

                sendToChat(_mensaje);
            }
        });

        /**
         * >toppaypal
         * Muestra en el chat el ganador en la categoría "Top Donador PayPal"
         */
        commands.put(_prefix + "toppaypal", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra al ganador de la \"Top Donador PayPal\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                _mensaje += "¡Ganadores Categoría Top Donador PayPal!: dskater88 (2019) galaaMoney";

                sendToChat(_mensaje);
            }
        });

        /**
         * >mayorsub
         * Muestra en el chat el ganador en la categoría "Mayor Suscriptor"
         */
        commands.put(_prefix + "mayorsub", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra al ganador de la \"Mayor Suscriptor\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                _mensaje += "¡Ganadores Categoría Mayor Suscriptor!: iL_ReaveN_ (2019) galaaMoney";

                sendToChat(_mensaje);
            }
        });

        /**
         * >tecnicos
         * Muestra en el chat la mención honrosa en la categoría "Tecnicos"
         */
        commands.put(_prefix + "tecnicos", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.GALA_AWARDS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra la mención honrosa de \"Técnicos\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                _mensaje += "¡Mención Honrosa a los Técnicos del Stream!: Hugo y Pencho (2018) galaaHi";

                sendToChat(_mensaje);
            }
        });
    }

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
//                Scanner sc = new Scanner(new File(t.getFilename()));
                Scanner sc = new Scanner(getClass().getResourceAsStream(t.getFilename()));
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
        commands.put(_prefix + "subday", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.COLAS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Settea el modo de las colas a \"SubDay\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";
                _mensaje = "Se ha seteado el modo a SubDay ¡Envíen " + _prefix + "join para unirse a la cola! galaaHype";
                queueMode = BotMode.SUBDAY;
                queueOpen = true;

                sendToChat(_mensaje);
            }
        });

        /**
         * >signs
         * Comando que settea el modo de las colas del bot a "Signs"
         */
        commands.put(_prefix + "signs", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.COLAS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Settea el modo de las colas a \"Signs\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";
                _mensaje = "Se ha seteado el modo a Signs galaaKiss";
                queueMode = BotMode.SIGNS;

                sendToChat(_mensaje);
            }
        });

        /**
         * >concursos
         * Comando que settea el modo de las colas del bot a "Concursos"
         */
        commands.put(_prefix + "concursos", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.COLAS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Settea el modo de las colas a \"Concursos\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";
                _mensaje = "Se ha seteado el modo a Concursos ¡Envíen " + _prefix + "join para unirse a la cola! galaaHype";
                queueMode = BotMode.CONCURSOS;
                queueOpen = true;

                sendToChat(_mensaje);
            }
        });

        /**
         * >navidad
         * Comando que settea el modo de las colas del bot a "Navidad"
         */
        commands.put(_prefix + "navidad", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.COLAS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Settea el modo de las colas a \"Navidad\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";
                _mensaje = "Se ha seteado el modo a Navidad ¡Envíen " + _prefix + "join para unirse a la cola! galaaHype";
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
        commands.put(_prefix + "noqueue", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.COLAS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Cierra las colas y limpia todo (Las elimina)."; }
            
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";
                
                //Definir mensaje según caso
                switch(queueMode){
                    case SUBDAY:    _mensaje = "¡El SubDay se ha terminado! Gracias por participar galaaKiss"; break;
                    case NAVIDAD:   _mensaje = "¡El evento de Navidad se ha terminado! Gracias por participar galaaKiss"; break;
                    case SIGNS:     _mensaje = "¡Los Signs se han terminado! Gracias a todos quienes canjearon galaaKiss"; break;
                    case CONCURSOS: _mensaje = "¡El Concurso se ha acabado! Gracias a todos los concursantes galaaKiss"; break;
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
        commands.put(_prefix + "closequeue", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.COLAS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Cierra las colas para que nadie más entre."; }
            
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";
            
                //Definir mensaje según caso
                if (isOnQueue()){
                    _mensaje = "¡La cola se ha cerrado! Sigan sintonizando para ver como le va a los restantes galaaGG";
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
        commands.put(_prefix + addQueueCmd, new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.COLAS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Añade usuarios a la cola, usa \"|\" como separador de nombres."; }
            
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
                    _mensaje = user + " no especificaste ningún usuario. Intenta usar el comando de nuevo galaaSad";
                }
                sendToChat(_mensaje);
            }
        });

        /**
         * >next
         * Hace pasar al siguiente usuario de la cola
         * eliminando al usuario que se encontraba ahora en "juego"
         */
        commands.put(_prefix + "next", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.COLAS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Hace pasar al siguiente en la cola y elimina el actual."; }
            
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
                log("*** DEBUG next: " + next);
                if (next != null) {

                    //SUBDAY
                    if (queueMode == BotMode.SUBDAY) {
                        int _count = Integer.parseInt(next.substring(next.lastIndexOf("@") + 1));
                        next = next.substring(0, next.lastIndexOf("@"));
                        actualTier = SubTier.getTier(_count);
                        _mensaje = "¡ @" + next + " eres un alien " + actualTier.getColor() + " ! Por favor usa " + _prefix + "spin para girar tu ruleta galáctica correspondiente galaaKiss";

                    //NAVIDAD
                    } else if (queueMode == BotMode.NAVIDAD) {
                        int _count = Integer.parseInt(next.substring(next.lastIndexOf("@") + 1));
                        next = next.substring(0, next.lastIndexOf("@"));                        
                        actualTier = SubTier.getTier(_count);
                        _mensaje = "¡ @" + next + " es tu turno! Por favor usa " + _prefix + "spin para girar tu ruleta navideña correspondiente galaaKiss";

                    //OTRO MODO
                    } else {
                        _mensaje = "Ahora es turno de: " + next + " galaaKiss";
                    }
                    onQueue.remove(next);
                    actualUser = next;
                } else {
                    _mensaje = "¡La cola está vacia! galaaSad";
                    actualUser = null;
                }

                log("*** DEBUG: onQueue: " + onQueue.size());
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
        commands.put(_prefix + "otro", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.COLAS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Manda el actual al agua (al final) y hace pasar uno nuevo."; }
            
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


                _mensaje = actualUser + " se ha ido al agua y deberá nadar hasta el final de la cola. galaaSad";
                if (queueMode == BotMode.SUBDAY || queueMode == BotMode.NAVIDAD){
                    queue.add(actualUser + "@" + actualTier.getMeses());
                } else {
                    queue.add(actualUser);
                }

                String next = queue.poll();
                if (next == null){
                    _mensaje += " Sin embargo no quedaba nadie en la cola, por lo que sigue siendo el turno de: " + actualUser + " galaaKiss";
                } else {
                    if (queueMode == BotMode.SUBDAY || queueMode == BotMode.NAVIDAD){
                        int _count = Integer.parseInt(next.substring(next.lastIndexOf("@") + 1));
                        next = next.substring(0, next.lastIndexOf("@"));
                        actualTier = SubTier.getTier(_count);
                    }
                    
                    if (!next.equals(actualUser)){
                        _mensaje += " Ahora es turno de: ";
                        onQueue.remove(next);
                        onQueue.add(actualUser);
                        actualUser = next;

                        if (queueMode == BotMode.SUBDAY){
                            _mensaje +=  "@" + next + ". Por favor usa " + _prefix + "spin para girar tu ruleta galáctica correspondiente galaaKiss";
                        } else if (queueMode == BotMode.NAVIDAD) {
                            _mensaje +=  "@" + next + ". Por favor usa " + _prefix + "spin para girar tu ruleta navideña correspondiente galaaKiss";
                        } else {
                            _mensaje += "@" + next + " galaaKiss";
                        }
                    } else {
                        _mensaje += " Sin embargo no quedaba nadie en la cola, por lo que sigue siendo el turno de: " + actualUser + ". galaaKiss";
                    }
                }
                
                log("*** DEBUG: onQueue: " + onQueue.size());
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
        commands.put(_prefix + "join", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.COLAS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Ingresa a la cola actual."; }
            
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
                            _mensaje = "Al SubDay solo pueden entrar subs galaaSad";
                            sendToChat(_mensaje);
                            return;
                        }
                        break;
                    case CONCURSOS:
                    case NAVIDAD:
                        break;
                    case SIGNS:
                        _mensaje = "Por favor pidele a un Mod que te añada a la cola galaaKiss";
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

                log("*** DEBUG onQueue: " + onQueue.size());
                sendToChat(_mensaje);
            }
        });

        /**
         * >spin
         * Gira la ruleta correspondiente según el modo y los meses
         * de sub que tenga el usuario.
         */
        commands.put(_prefix + "spin", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.COLAS; }
            @Override public UserCategory getUser() { return UserCategory.ACTIVE; }
            @Override public String getDescripcion() { return "Hace girar la ruleta."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                if (actualUser == null)
                    return;
                
                if (queueMode != BotMode.SUBDAY && queueMode != BotMode.NAVIDAD){
                    sendToChat("No estamos en epoca de Ruletas. ¡No puedes girar nada! galaaSad");
                    return;
                }

                if (user.compareTo(actualUser) != 0){
                    log("|" + user + " != " + actualUser + "|");
                    return;
                }

                if (hasSpin){
                    sendToChat("¡Es sólo un lanzamiento! galaaSad");
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
        commands.put(_prefix + "checknext", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.COLAS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Revisa quien es el siguiente usuario en la cola."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";

                if (isOnQueue()){
                    String next = queue.peek();
                    //Hacer la corrección del sub si es SUBDAY o NAVIDAD
                    if ((queueMode == BotMode.SUBDAY || queueMode == BotMode.NAVIDAD) && next != null) {
                        next = next.substring(0, next.lastIndexOf("@"));
                    }
                    _mensaje = (next != null) ? "El siguiente en la lista es: " + next : "¡La cola está vacia! galaaSad";
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
        commands.put(_prefix + "actual", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.COLAS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Revisa quien es el usuario que debe girar la ruleta."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";
                
                switch(queueMode){
                    case CONCURSOS:
                        _mensaje = ((actualUser != null) ? "El concursante actual es: " + actualUser : " ¡No hay nadie concursando! galaaSad");
                        break;
                    case SUBDAY:
                    case NAVIDAD:
                        _mensaje = ((actualUser != null) ? "El participante actual es: " + actualUser : " ¡No hay nadie participando! galaaSad");
                        break;
                    case SIGNS:
                        _mensaje = ((actualUser != null) ? "El sign actual es: " + actualUser : " ¡No hay nadie para el sign! galaaSad");
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
        commands.put(_prefix + "queueleft", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.COLAS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Revisa cuantos participantes quedan en la cola."; }
            
            @Override public void execute(String user, String msg, String tags) {
                String _mensaje = "";
                
                int left = queue.size();
                String _moreThanOne;
                String _one;
                String _none = "¡No queda nadie más en la cola! galaaSad";

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
    //      ♡ DRUNK STREAM ♡
    //  Are you drunk?
    private HashMap <String, Integer> contador = new HashMap<>();
    //////////////////////////////

    private void addDrunkCommands(){
        commands.put(_prefix + "carrete", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.JUEGOS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Settea el modo de juego a \"Carrete\"."; }
            
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                String _mensaje = "";

                if (running) {
                    _mensaje = "Hay una apuesta en curso, por favor terminala antes de cambiar de modo galaaGG";
                } else {
                    _mensaje = user + " se ha seteado el modo a Carrete galaaGasm";
                    betMode = BotMode.CARRETE;
                }

                sendToChat(_mensaje);
            }
        });

        String drunkCmd = "contador";
        commands.put(_prefix + drunkCmd, new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.JUEGOS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Streamer: Añade al contador | User: Ve el contador."; }
            
            @Override public void execute(String user, String msg, String tags) {
                if (betMode != BotMode.CARRETE) {
                    sendToChat("No estamos en carrete galaaRage");
                    return;
                }

                String weon = "";
                String _mensaje = "";
                try {
                    Scanner sc = new Scanner(msg.substring(_prefix.length() + drunkCmd.length()));
                    weon = sc.next().toLowerCase();
                    sc.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    sendToChat(user + " no especificaste un valor. Por favor intenta usar el comando de nuevo galaaSad");
                    return;
                }
                
                if (checkStreamer(tags)){
                    if (contador.containsKey(weon)){
                        contador.replace(weon, contador.get(weon) + 1);
                    } else {
                        contador.put(weon, 1);
                    }

                    _mensaje = "Le has dado un vaso a " + weon + " galaaGasm. Lleva " + contador.get(weon) + " vasos galaaGG";
                } else {
                    if (contador.containsKey(weon)){
                        _mensaje = weon + " lleva " + contador.get(weon) + " vasitos galaaGG";
                    } else {
                        _mensaje = weon + " no lleva ningún vaso galaaRage";
                    }
                }

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
        addDrunkCommands();

        //////////////////////////////
        //  ♡ COMANDOS DE STREAMER O MODS
        //////////////////////////////
        /**
         * >lol
         * Comando que settea el modo del bot a "LoL"
         * No cambiará de modo si es que una apuesta quedó abierta
         */
        commands.put(_prefix + "lol", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.JUEGOS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Settea el modo de juego a \"LoL\"."; }
            
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
        commands.put(_prefix + "fortnite", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.JUEGOS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Settea el modo de juego a \"Fortnite\"."; }
            
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
        commands.put(_prefix + "deceit", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.JUEGOS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Settea el modo de juego a \"Deceit\"."; }
            
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
        commands.put(_prefix + "nogame", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.JUEGOS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Vuelve el bot a neutro."; }
            
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
        commands.put(_prefix + betCmd, new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.JUEGOS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Inicia las votaciones."; }
            
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                if (!isOnGame()) {
                    sendToChat("¡No hay ningún juego setteado!. Por favor usa " + _prefix + "lol, " + _prefix + "fortnite, o " + _prefix + "deceit antes de iniciar la votación galaaKiss");
                    return;
                }

                int laxys = -1;
                try {
                    Scanner sc = new Scanner(msg.substring(_prefix.length() + betCmd.length()));
                    laxys = sc.nextInt();
                    sc.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    sendToChat("¡Debes especificar el monto de laxycoins en el comando! galaaUWU");
                    return;
                }

                String _mensaje = "";

                if (laxys != -1)
                    _mensaje = "¡Por " + laxys + " laxycoins! ";

                if (!betting)
                    _mensaje += "¡Las apuestas se han activado!";
                else
                    _mensaje += "¡Las apuestas ya se encontraban activadas!";
                
                _mensaje += " ¡Envia " + _prefix + "vote ";

                if (betMode == BotMode.FORTNITE)
                    _mensaje += "<lugar>";
                else if (betMode == BotMode.LOL)
                    _mensaje += "<kills>";
                else if (betMode == BotMode.DECEIT)
                    _mensaje += "<win|lose>";

                _mensaje += " para apostar! ¡Que comience el juego! galaaHype";
                
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
        commands.put(_prefix + "closebet", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.JUEGOS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Cierra las votaciones."; }
            
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                if (!running) {
                    sendToChat("No hay ninguna apuesta en curso galaaSad");
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
        commands.put(_prefix + "resetbet", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.JUEGOS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Reinicia las votaciones manteniendo el premio."; }
            
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                if (!running){
                    sendToChat("No hay ninguna apuesta en curso galaaSad");
                    return;
                }

                votesReady.clear();
                votes.clear();

                String _mensaje = user + " ¡Apuestas reiniciadas! ¡Por " + laxycoins + " laxycoins! ¡Envia " + _prefix + "vote ";
                
                if (betMode == BotMode.FORTNITE)
                    _mensaje += "<lugar>";
                else if (betMode == BotMode.LOL)
                    _mensaje += "<kills>";
                else if (betMode == BotMode.DECEIT)
                    _mensaje += "<win|lose>";

                _mensaje += " para apostar! galaaHype";
                sendToChat(_mensaje);
                betting = true;
            }
        });

        /**
         * >winner <lugar>
         * Define al ganador de las apuestas, y anuncia el premio que obtuvo el/los ganador/es.
         */
        String winCmd = "winner";
        commands.put(_prefix + winCmd, new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.JUEGOS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Define al ganador de las apuestas."; }
            
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;

                if (!running){
                    sendToChat("No hay ninguna apuesta en curso galaaSad");
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
                        _mensaje += "¡Se " + ((winners.size() > 1) ? "reparten " : "ganó ") + laxycoins + " laxycoins! ";
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
                        sendToChat("¡Oops! Nadie ha salido ganador esta vez. " + _winners + ((laxycoins != -1) ? (" Se perdieron " + laxycoins + " laxycoins galaaSad")  : "") + " ¡Mayor suerte para la próxima!");
                    } else
                        sendToChat("¡Oops! Nadie ha salido ganador esta vez." + ((laxycoins != -1) ? (" Se perdieron " + laxycoins + " laxycoins galaaSad")  : "") + " ¡Mayor suerte para la próxima!");
                }
                
                running = false;
            }
        });

        /**
         * >cancelbet
         * Cancela la actual apuesta en curso. Eliminandola completamente
         */
        commands.put(_prefix + "cancelbet", new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.JUEGOS; }
            @Override public UserCategory getUser() { return UserCategory.MOD; }
            @Override public String getDescripcion() { return "Cancela la apuesta actual."; }
            
            @Override public void execute(String user, String msg, String tags) {
                if (!checkModOrStreamer(tags))
                    return;
                
                String _mensaje = "";

                if (!running) {
                    _mensaje = "Ninguna apuesta estaba en curso galaaSad";
                } else {
                    _mensaje = "Se ha cancelado la apuesta, nadie a ganado galaaSad";
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
        commands.put(_prefix + voteCmd, new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.JUEGOS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Vota para la apuesta actual."; }
            
            @Override public void execute(String user, String msg, String tags) {
                if (!betting){
                    sendToChat("¡" + user + " no hay votaciones abiertas! galaaSad");
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
                        switch (lugar) {
                            case "win":  place = 1; break;
                            case "lose": place = 0; break;
                            default:
                                sc.close();
                                throw new IllegalArgumentException("¡Keyword invalida o no especificada!");
                        }
                    } else
                        place = sc.nextInt();
                    sc.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    sendToChat(user + " no especificaste un valor. Por favor intenta usar el comando de nuevo galaaSad");
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
        commands.put(_prefix + leftCmd, new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.JUEGOS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra cuantos viewers siguen participando."; }
            
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
                    sendToChat(user + " no especificaste un valor. Por favor intenta usar el comando de nuevo galaaSad");
                    return;
                }

                int _contador = 0;
                switch (betMode) {
                    case FORTNITE:
                        for (int i = 1; i <= place; i++) {
                            if (votes.containsKey(i)) {
                                _contador += votes.get(i).size();
                            }
                        }   break;
                    case LOL:
                        for (int key : votes.keySet()) {
                            if (key >= place) {
                                _contador += votes.get(key).size();
                            }
                        }   break;
                    case DECEIT:
                        if (votes.containsKey(0)){
                            _contador += votes.get(0).size();
                        }   if (votes.containsKey(1)){
                            _contador += votes.get(1).size();
                        }   break;
                }

                if (_contador != 0)
                    sendToChat("¡" + user + " queda" + ((_contador > 1) ? ("n " + _contador + " viewers") : (" " + _contador + " viewer")) + " participando galaaGG!");
                else
                    sendToChat("Ya nadie queda en juego galaaSad");
            }
        });
        
        /**
         * >maxbet
         * Revisa el viewer/los viewers que hicieron la apuesta más alta.
         */
        String maxCmd = "maxbet";
        commands.put(_prefix + maxCmd, new ICommand(){
            @Override public CmdCategory getCategory() { return CmdCategory.JUEGOS; }
            @Override public UserCategory getUser() { return UserCategory.USER; }
            @Override public String getDescripcion() { return "Muestra los viewers que hicieron la votación más alta."; }
            
            @Override public void execute(String user, String msg, String tags) {
                if (!isOnGame()){
                    sendToChat("No hay juego al que verle los votantes restantes. (Are you drunk?) galaaWhat");
                    return;
                }

                if (betMode == BotMode.DECEIT){
                    sendToChat("No hay un voto maximo en Deceit. (Are you drunk?) galaaWhat");
                    return;
                }
                
                int maxbet = -1;
                if (betMode == BotMode.FORTNITE)
                    maxbet = 101;
                
                for (int key : votes.keySet()){
                    switch(betMode){
                        case FORTNITE:
                            if (key < maxbet)
                                maxbet = key;
                            break;
                        case LOL:
                            if (key > maxbet)
                                maxbet = key;
                            break;
                    }
                }
                
                if (!votes.containsKey(maxbet))
                    sendToChat("No existe ninguna votación más alta galaaSad");
                
                LinkedList<String> list = votes.get(maxbet);
                int _contador = list.size();
                
                String _msg = "¡" + maxbet + " es la votación más alta!, y ";
                
                if (list.size() == 1){
                    _msg += list.get(0) + " votó por ella galaaGG";
                } else {
                    for (String u : list){
                        _msg += u + ", ";
                    }
                    
                    _msg = _msg.substring(0, _msg.length() - 2) + " votaron por ella galaaGG";
                }
                    
                sendToChat(_msg);
            }
        });
    }
}
