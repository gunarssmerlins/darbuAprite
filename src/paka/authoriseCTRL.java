/*//////////////////////////////////////////////////////////
                   Autorizācijas logs
//////////////////////////////////////////////////////////*/

package paka;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class authoriseCTRL {


////////////////////////////////////////////////////////////
/*                Uz pasūtījumu kopskatu                  */

    public void switchToOrders(ActionEvent event) throws IOException {
        Parent viewOrders = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        Scene viewOrdersScene = new Scene(viewOrders);
//        viewOrdersScene.getStylesheets().add("CSS/t.css");
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setTitle("Pasūtījumi");
        window.setScene(viewOrdersScene);
        window.show();
    }
}
