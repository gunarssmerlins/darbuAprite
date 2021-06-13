/*//////////////////////////////////////////////////////////
              Jauna klienta pievienošanas logs
//////////////////////////////////////////////////////////*/

package paka;

import connectivity.mysqlConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class newCustomerCTRL {

    @FXML
    private TextField customerName;
    @FXML
    private Button cancelAdd;
    @FXML
    private Button addCustomer;
    @FXML
    private ComboBox customerFX;


/*////////////////////////////////////////////////////////*/
//          Atpakaļ uz jaunā pasūtījuma logu              //

    public void enterCustomer(ActionEvent event) throws IOException, SQLException {

        /*////////////////////////////////////////////////////*/
        //             Pievieno jaunu klientu bāzei           //
        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();

        /*      !!! jāpārbauda vai jau nav šāds klients, neaizmirsti vecais !!!     */
        String sql = "INSERT INTO CUSTOMERS (companyName) VALUES ('"+customerName.getText()+"')";

        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);

        //////////////////////////////////////////////////////////////////////////
        /* iegūstam klienta vārdu                                               */
        FXMLLoader loader = new FXMLLoader(getClass().getResource("newOrder.fxml"));
        Parent root = (Parent) loader.load();

        newOrderCTRL newCustomContr = loader.getController();
//        newCustomContr.customerPass(customerName.getText());

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();


        /*Parent viewOrders = FXMLLoader.load(getClass().getResource("newOrder.fxml"));
        Scene viewOrdersScene = new Scene(viewOrders);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(viewOrdersScene);
        window.show();*/
    }
}
