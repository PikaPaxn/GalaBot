package cl.pencho.galabot;

import cl.pencho.galabot.bot.Main;
import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;

/**
 * @author Pencho
 */

public class Launcher extends Application {
    
    static InputStream ICON = Launcher.class.getResourceAsStream("/images/galaaGasm.png");
    
    @Override public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Launcher.fxml"));
        
        //createTrayIcon(stage);
        //Platform.setImplicitExit(false);
        
        Scene scene = new Scene(root);
        
        stage.setTitle("GalaBot");
        Image e = new Image(ICON);
        stage.getIcons().add(e);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        
        stage.setOnCloseRequest((event) -> { System.exit(0); });
    }
    
    private void createTrayIcon(Stage stage){
        if (SystemTray.isSupported()){
            //Get the instance
            SystemTray tray = SystemTray.getSystemTray();
            java.awt.Image image = null;
            try {
                image = ImageIO.read(ICON);
            } catch (IOException ex) {
                System.out.println(ex);
            }
            
            stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
                @Override public void handle(WindowEvent t){
                    hide(stage);
                }
            });
            
            final ActionListener closeListener = new ActionListener() {
                @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                    System.exit(0);
                }
            };
            
            ActionListener showListener = new ActionListener() {
                @Override public void actionPerformed(java.awt.event.ActionEvent e){
                    Platform.runLater(() -> {
                        stage.show();
                    });
                }
            };
            
            PopupMenu popup = new PopupMenu();
            
            MenuItem showItem = new MenuItem("Show");
            showItem.addActionListener(showListener);
            popup.add(showItem);
            
            MenuItem closeItem = new MenuItem("Close");
            showItem.addActionListener(closeListener);
            popup.add(closeItem);
            
            TrayIcon trayIcon = new TrayIcon(image, "GalaBot", popup);
            trayIcon.addActionListener(showListener);
            
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.out.println(e);
            }
        }
    }
    
    private void showProgramIsMinimizedMsg(){
        
    }
    
    private void hide(final Stage stage){
        Platform.runLater(() -> {
            if (SystemTray.isSupported()){
                stage.hide();
                showProgramIsMinimizedMsg();
            } else {
                System.exit(0);
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length > 1)
            Main.main(args);
        else
            launch(args);
    }
    
}
