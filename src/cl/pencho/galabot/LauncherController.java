package cl.pencho.galabot;

import cl.pencho.galabot.bot.Main;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * @author Pencho
 */

public class LauncherController implements Initializable {
    
    @FXML private Label laEstado;
    
    @FXML private void conectar(ActionEvent event) {
        Main.main(new String[]{"-v", "-msg"});
    }
    
    @FXML private void reiniciar(ActionEvent event) {
        desconectar(event);
        conectar(event);
    }
    
    @FXML private void desconectar(ActionEvent event) {
        Main.disconnect();
    }
    
    @Override public void initialize(URL url, ResourceBundle rb) {
        Main.RUNNING.addListener((observable, oldValue, newValue) -> {
            Platform.runLater( () -> {
                if (newValue){
                    laEstado.setText("CONECTADO");
                    laEstado.setTextFill(Color.GREEN);
                } else {
                    laEstado.setText("No conectado...");
                    laEstado.setTextFill(Color.RED);
                }
            });
        });
    }    
    
}
