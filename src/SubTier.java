//////////////////////////////
//      ♡ SubTier GalaBot ♡
//  Autor:      Pencho
//  Creado:     29-Agosto-2018
//  Modificado: 24-Diciembre-2018
//
//  Este Enum funciona como una centralización de los datos que
//  son necesarios para que la interacción de los subs funcione
//  de la mejor manera posible.
//////////////////////////////
public enum SubTier {
    NO_ALIEN     (-2,  ""),
    FOLLOWER     (-1,  "follower"),
    ALIEN_GRIS   (0,  "gris"),
    ALIEN_VERDE  (3,  "verde"),
    ALIEN_DORADO (6,  "dorado"),
    ALIEN_MORADO (12, "morado");

    private final int tiempo;
    private final String color;
    SubTier(int t, String c){
        tiempo = t;
        color = c;
    }
    
    private final String ruta = "../resources/subday_";

    public int    getMeses() { return tiempo; }
    public String getColor() { return color;  }
    public String getFilename() { return ruta + color + ".txt"; }
}