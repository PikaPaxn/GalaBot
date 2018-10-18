import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Iterator;

public class Testo {
    private static final int SIZE = 15;

    public static void main (String[] args){
        int iter[] = new int[SIZE];
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();
        System.out.print("Ingrese la cantidad de iteraciones: ");
        int _it = sc.nextInt();
        sc.close();
        for (int i = 0; i < _it; i++){
            int r = rand.nextInt(15);
            iter[r]++;
        }

        System.out.println("Results: ");
        for (int i = 0; i < SIZE; i++){
            float p = (100.0f * iter[i]/_it);
            System.out.print(((i < 10) ? "0" + i : i) + ": " + ((iter[i] < 10) ? "0" + iter[i] : iter[i]) + "| " + ((p < 10.0f) ? "0" + p : p) + "%");
            if ((i+1) % 3 == 0){
                System.out.println();
            } else {
                System.out.print("      ");
            }
        }

        // for (SubTier t : SubTier.values()){
        //     try {
        //         Scanner tc = new Scanner(new File(t.getFilename()));
        //         ArrayList<String> prize = new ArrayList<String>();
        //         while(tc.hasNextLine()){
        //             prize.add(tc.nextLine());
        //         }
        //         System.out.print("OPEN " + t.getFilename());
        //     } catch (Exception e) {
        //         System.out.println("!! Cant open " + t.getFilename());
        //     }
        // }
    }

    // public static void main(String[] args){
    //     // String tags = "@badges=subscriber/3;color=#8A2BE2;display-name=alvaro_lopezc;emotes=;id=55201379-fa01-414c-ba;mod=0;room-id=1073";
    //     // String _tempMod = "alvaro_lopezc";
    //     // HashSet<String> _tempMods = new HashSet<>();
    //     // _tempMods.add(_tempMod);

    //     // String badges = tags.substring(0, tags.indexOf(";"));
    //     // boolean mod = badges.contains("moderator") || badges.contains("broadcaster");
    //     // if (!mod) {
    //     //     String _user = tags.substring(tags.indexOf("display-name"));
    //     //     _user = _user.substring(13, _user.indexOf(";")).toLowerCase().trim();
    //     //     System.out.println(_user);
    //     //     mod = _tempMods.contains(_user);
    //     // }
    //     // System.out.println(mod);

    //     boolean[] bools = {false, true, true, false, false, false};
    //     turnTrue(bools);
    //     for (int i = 0; i < bools.length; i++) {
    //         System.out.print(bools[i] + " ");
    //     }
    //     System.out.println();

    //     Runtime.getRuntime().addShutdownHook(new Thread() {
    //         public void run() {
    //             try {
    //                 Thread.sleep(200);
    //                 System.out.println("Ctrl + C has been pressed!");
    //             } catch (Exception e) {
    //                 e.printStackTrace();
    //             }
    //         }
    //     });
        
    //     System.out.println("args: " + args.length);
    //     for (int i = 0; i < args.length; i++){
    //         System.out.println("|" + args[i] + "|");
    //     }

    //     String phrase2 = "This is a very long phrase with <athing> a thing between things </athing>";
    //     String phrase = "<athing>a thing between things</athing>";
    //     System.out.println(phrase + "| i1: " + (phrase.indexOf("athing") + "athing".length()) + ", i2: " + phrase.lastIndexOf("athing"));
    //     System.out.println("Cutted!: |" + phrase.substring((phrase.indexOf("athing") + "athing".length() + 1), (phrase.lastIndexOf("athing") - 2)) + "|");

    //     if (BasicXMLParser.load("responses.xml")){
    //         System.out.println("XML READED");
    //         Iterator<String> ids = BasicXMLParser.getIds();
    //         while(ids.hasNext()){
    //             String id = ids.next();
    //             System.out.println(BasicXMLParser.getResponse(id));
    //         }
    //     }

    //     while(true);
    // }

    // private static void turnTrue(boolean[] bools){
    //     for (int i = 0; i < bools.length; i++)
    //         bools[i] = true;
    // }
}