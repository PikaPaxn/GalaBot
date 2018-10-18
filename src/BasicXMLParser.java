import java.io.File;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Scanner;

//////////////////////////////
//      ♡ Basic XML Parser ♡
//  Autor:      Pencho
//  Creado:     01-Agosto-2018
//  Modificado: 01-Agosto-2018
//
//  Clase dedicada especificamente a la lectura del archivo XML que contiene
//  todos los textos que enviará el bot en el chat ♡
//////////////////////////////
public class BasicXMLParser {

    //////////////////////////////
    //  ♡ Singleton ♡
    //////////////////////////////
    private static BasicXMLParser instance;
    private static BasicXMLParser getInstance(){
        if (instance == null)
            instance = new BasicXMLParser();

        return instance;
    }

    //////////////////////////////
    //  ♡ "Objeto" ♡
    //////////////////////////////
    private HashMap<String, String> responses;

    BasicXMLParser(){               responses = new HashMap<>();     }
    public String get(String key){    return responses.get(key);     }
    public void   put(String k, String v){   responses.put(k, v);    }

    public Iterator<String> ids()    { return responses.keySet().iterator(); }

    //////////////////////////////
    //  ♡ "Clase" ♡
    //  Metodos que se utilizarán para leer el "xml"
    //////////////////////////////
    public static boolean load(String pathname){
        try {
            Scanner sc = new Scanner(new File(pathname));
            //Ir directo a lo que me interesa.
            while(sc.hasNextLine()){
                String line = sc.nextLine();

                //Revisar si encontré un comando
                if (line.contains("<Command>")){
                    String id = sc.nextLine();
                    String response = sc.nextLine();

                    String key = cut(id, "Id");
                    String value = cut(response, "Response");
                    getInstance().put(key, value);
                }
            }
            sc.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getResponse(String k){
        return getInstance().get(k);
    }

    public static Iterator<String> getIds(){
        return getInstance().ids();
    }

    private static String cut(String string, String delimeters){
        int i1 = string.indexOf(delimeters) + delimeters.length() + 1; // index + lenght + ">"
        int i2 = string.lastIndexOf(delimeters) - 2; // lastIndex - "</"
        return string.substring(i1, i2);
    }
}

//////////////////////////////
//      ♡ Response ♡
//  Clase dedicada para almacenar las "respuestas"
//  Contiene un id, la respuesta, y "comentarios"
//////////////////////////////
class Response {

}