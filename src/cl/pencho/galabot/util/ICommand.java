package cl.pencho.galabot.util;

//////////////////////////////
//      ♡ ICommand ♡
//  Autor:      Pencho
//  Creado:     08-Julio-2018
//  Modificado: 31-Diciembre-2018
//////////////////////////////
/**
 * ICommand
 
 Interface utilizada para generalizar todos los comandos que puede
 utilizar el bot
 */
public interface ICommand {
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
    
    /**
     * Metodo que se usa para mostrar una lista con la descripción de
     * cada comando.
     * @return Descripción del comando.
     */
    public String getDescripcion();
    
    /**
     * Metodo que se usa para mostrar una lista con la categoría
     * de cada comando.
     * @return Categoría del comando.
     */
    public CmdCategory getCategory();
    
    /**
     * Metodo que devuelve quien puede usar el comando.
     * @return String de quien puede usarlo.
     */
    public UserCategory getUser();
}
