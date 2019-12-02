package cl.pencho.galabot.bot;

import cl.pencho.galabot.util.Logger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

//////////////////////////////
//      ♡ Twitch Bot ♡
//  Autor:      Pencho
//  Creado:     08-Julio-2018
//  Modificado: 29-Enero-2019
//
//  Contiene la configuración y los metodos necesarios
//  para mantener al bot andando bien en los canales de Twitch.
//  Contiene methodos que pueden ser sobreescritos para poder interpretar
//  los comandos del chat.
//////////////////////////////
public class TwitchBot {
    public final static String TWITCH_HOST = "irc.chat.twitch.tv";
    public final static int TWITCH_PORT = 6667;

    protected String _prefix = "!";

    private String _nickname;
    private String _password;
    private String _channel;
    public String getChannel() { return this._channel; }
    
    //////////////////////////////
    //  LOGS
    public final boolean _verbose;
    public final boolean _msgLog;
    public final boolean _chatLog;
    public final boolean _cmdLog;
    //////////////////////////////
  
    public final int MESSAGE_DELAY = 1000;
    private BufferedReader reader;
    private BufferedWriter writer;

    private Socket _socket;
    private InputThread _inputThread;
    private OutputThread _outputThread;
    private LinkedList<String> outQueue = new LinkedList<>();

    private HashSet<String> _tempMods = new HashSet<>();

    //////////////////////////////
    //  METHODS AND STUFF
    //////////////////////////////

    public TwitchBot(String nickname, String password, String channel, boolean[] logs) {
        _nickname = nickname;
        _password = password;
        _channel = channel;
        
        _verbose = logs[0];
        _msgLog = logs[1];
        _chatLog = logs[2];
        _cmdLog = logs[3];

        try {
            connect();
        } catch (Exception e) {
            log("Ooops. Conexión incorrecta");
            e.printStackTrace();
        }
    }

    private void connect() throws Exception {
        _socket = new Socket(TWITCH_HOST, TWITCH_PORT);
        log ("*** Connected to server.");
        
        reader = new BufferedReader(new InputStreamReader (_socket.getInputStream ()));
        writer = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream()));

        if (_socket.isConnected()){
            System.out.println("Connected");
        } else {
            return;
        }

        OutputThread.sendToSystem(this, writer, "PASS " + _password);
        OutputThread.sendToSystem(this, writer, "NICK " + _nickname);
        OutputThread.sendToSystem(this, writer, "USER " + _nickname + " 0 * " + _nickname);
        OutputThread.sendToSystem(this, writer, "CAP REQ :twitch.tv/tags");
        OutputThread.sendToSystem(this, writer, "CAP REQ :twitch.tv/commands");
        // sendToSystem("CAP REQ :twitch.tv/membership");

        _inputThread = new InputThread(this, reader, writer);
        //Check if we are connected
        String line;
        while ((line = reader.readLine()) != null) {
            int firstSpace = line.indexOf(" ");
            int secondSpace = line.indexOf(" ", firstSpace + 1);
            if (secondSpace >= 0) {
                String code = line.substring(firstSpace + 1, secondSpace);

                if (code.equals("004")) {
                    // We're connected to the server.
                    break;
                }
            }
        }
        log("*** LOGGED IN.");
        _inputThread.start();

        if (_outputThread == null) {
            _outputThread = new OutputThread(this, outQueue);
            _outputThread.start();
        }

        sendToSystem("JOIN #" + _channel);
    }

    public void log(String message) {
        if (_verbose)
            Logger.log("* LOG " + System.currentTimeMillis() + " * : " + message);
//            System.out.println("* LOG " + System.currentTimeMillis() + " * : " + message);
    }

    public void sendToSystem(String message) {
        _inputThread.sendToSystem(message);
    }

    public void sendToChat(String message) {
        sendToSystem("PRIVMSG #" + _channel + " :" + message);
        if (_chatLog)
            log("CHAT>>>" + _nickname + ": " + message);
    }

    public void close(){
        // sendToSystem("PART #" + _channel);
        // sendToSystem("QUIT :Command Requires it");
        sendToChat(".disconnect");
        try {
            _socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        _inputThread.detener();
        _outputThread.detener();
        Main.RUNNING.setValue(false);
        log("*** DISCONNECTED");
    }

    public void handleMessage(String line) {
        //Identificar si es un mensaje del chat
        if (line.contains("PRIVMSG #" + _channel + " :")){
            String linea = line.substring(0, line.indexOf("!") + 1);
            String tags = linea.substring(0, linea.lastIndexOf(":"));
            String user = linea.substring(linea.lastIndexOf(":") + 1, linea.indexOf("!"));
            String msg = line.substring(line.indexOf("PRIVMSG #" + _channel + " :") + 11 + _channel.length());
            
            if (_chatLog)
                log("CHAT<<<" + user + ": " + msg);
            handleChatMessage(user, msg.toLowerCase(), tags);
        }
    }

    public void handleChatMessage(String user, String msg, String tags) {
        if (msg.startsWith(_prefix)){
            if (checkModOrStreamer(tags)){
                if (msg.startsWith(_prefix + "quit") || msg.startsWith(_prefix + "exit")){
                    sendToChat("Gracias por llamarme, ¡nos vemos!");
                    close();
                }

                if (msg.startsWith(_prefix + "tempmod")){
                    try {
                        Scanner sc = new Scanner(msg.substring(_prefix.length() + 7));
                        String _newMod = sc.nextLine().toLowerCase().trim();
                        sc.close();
                        _tempMods.add(_newMod);
                        sendToChat("Mod temporal " + _newMod + " ha sido agregado.");
                        if (_cmdLog)
                            log("*** MOD AÑADIDO: |" + _newMod + "|");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (msg.startsWith(_prefix + "tempunmod")){
                    try {
                        Scanner sc = new Scanner(msg.substring(_prefix.length() + 9));
                        String _newMod = sc.nextLine().toLowerCase().trim();
                        sc.close();
                        if(_tempMods.contains(_newMod)){
                            _tempMods.remove(_newMod);
                            sendToChat("Mod temporal " + _newMod + " eliminado.");
                            if (_cmdLog)
                                log("*** MOD ELIMINADO: |" + _newMod + "|");
                        } else {
                            sendToChat("Mod temporal " + _newMod + " nunca fue mod.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //@badges=subscriber/3;color=#8A2BE2;display-name=MidianGatess;emotes=;id=55201379-fa01-414c-ba;mod=0;room-id=1073

    protected boolean checkStreamer(String tags) {
        String badges = tags.substring(0, tags.indexOf(";", tags.indexOf(";") + 1));
        boolean streamer = badges.contains("broadcaster");
        return streamer;
    }

    protected boolean checkModOrStreamer(String tags) {
        String badges = tags.substring(0, tags.indexOf(";", tags.indexOf(";") + 1));
        log("*** DEBUG badges: " + badges);
        boolean mod = badges.contains("moderator") || badges.contains("broadcaster");
        if (!mod) {
            String _user = tags.substring(tags.indexOf("display-name"));
            _user = _user.substring(13, _user.indexOf(";")).toLowerCase().trim();
            mod = _tempMods.contains(_user);
            if (_cmdLog)
                log("*** CHECKING TEMPMODS - _user:" + _user + "|is:" + mod);
            return mod;
        }
        return mod;
    }

    protected boolean checkSub(String tags) {
        String badges = tags.substring(0, tags.indexOf(";"));
        return badges.contains("subscriber") || badges.contains("founder");
    }

    protected int subsRow(String tags) {
        String tag = "subscriber";
        if (tags.contains("founder")) {
            tag = "founder";
        }
        
        String _badges = tags.substring(0, tags.indexOf(";"));
        String _fromSub = _badges.substring(_badges.indexOf(tag));
        int index = _fromSub.indexOf(",");
        String _subBadge = "";
        if (index != -1)
            _subBadge = _fromSub.substring(0, index);
        else 
            _subBadge = _fromSub;
        return Integer.parseInt(_subBadge.substring(_subBadge.indexOf("/") + 1));
    }
}

//////////////////////////////
//  *** Threads ***
//  La lectura y el envio de los mensajes se hacen en threads separados
//  que van corriendo simultaneamente, de tal manera que si se necesitan
//  enviar muchos mensajes o si varios mensajes han sido enviados al chat,
//  el bot los pueda ir interpretando sin perjudicar el resto de las acciones.
//////////////////////////////
class InputThread extends Thread {
    private TwitchBot bot;
    private BufferedReader reader;
    private BufferedWriter writer;

    private boolean running = true;
    public void detener() { running = false; }

    InputThread(TwitchBot bot, BufferedReader br, BufferedWriter bw) {
        this.bot = bot;
        this.reader = br;
        this.writer = bw;
    }

    public void sendToSystem(String line){
        OutputThread.sendToSystem(bot, writer, line);
    }

    //Thread para ir manteniendo los mensajes entrantes
    public void run() {
        try {
            while(running) {
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        try {                           
                            //Checkear si hubo ping
                            if (line.contains("PING :tmi.twitch.tv")) {
                                bot.log("<<<" + line);
                                bot.log("Is running? " + running);
                                sendToSystem("PONG :tmi.twitch.tv");
                            }
                            if (bot._msgLog)
                                bot.log("<<<" + line);
                            
                            bot.handleMessage(line);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (line == null) {
                        running = false;
                        bot.log("*** STOP READING");
                    } else {
                        System.out.println("OUT WHILE: " + line);
                    }
                } catch (Exception e) {
                    sendToSystem("PING " + (System.currentTimeMillis() / 1000));
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        //Cerrando
        try {
            bot.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

class OutputThread extends Thread {
    TwitchBot bot;
    Queue<String> outQueue;

    private boolean running = true;
    public void detener() { running = false; }

    OutputThread(TwitchBot bot, Queue<String> queue) {
        this.bot = bot;
        outQueue = queue;
    }

    static void sendToSystem(TwitchBot tbot, BufferedWriter bwriter, String line){
        synchronized(bwriter){
            try {
                bwriter.write(line + "\r\n");
                bwriter.flush();
                if (tbot._msgLog)
                    tbot.log(">>>" + line);
            } catch (Exception e) {

            }
        }
    }

    public void run() {
        try {
            while (running) {
                Thread.sleep(bot.MESSAGE_DELAY);

                String line = outQueue.poll();
                if (line != null)
                    bot.sendToSystem(line);
                else
                    running = false;
            }
        } catch (Exception e) {

        }
    }
}