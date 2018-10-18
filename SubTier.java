public enum SubTier {
    NO_ALIEN     (-1,  ""),
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
    
    private final String ruta = "subday_";

    public int    getMeses() { return tiempo; }
    public String getColor() { return color;  }
    public String getFilename() { return ruta + color + ".txt"; }
}