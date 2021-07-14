/*//////////////////////////////////////////////////////////
               Jauna pasūtījuma ievades logs
//////////////////////////////////////////////////////////*/

package paka;

import connectivity.mysqlConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.ResourceBundle;

public class newOrderCTRL implements Initializable {
    @FXML public ComboBox customerFX;
    @FXML private DatePicker dueDateFX;
    @FXML private TextField pasNrFX;
    @FXML private TextField jobNameFX;
    @FXML private CheckBox jobSheetYNFX;
    @FXML private ComboBox reproFX;
    @FXML private ComboBox projectManagerFX;
    @FXML private ComboBox paperStateFX;
    @FXML private CheckBox offsetYNFX;
    @FXML private CheckBox digitalYNFX;
    @FXML private CheckBox designYNFX;
    @FXML private CheckBox largeFormatYNFX;
    @FXML private CheckBox postPressYNFX;
    @FXML private CheckBox otherJobFX;
    @FXML private CheckBox printedYNFX;
    @FXML private ComboBox projectStateFX;
    @FXML private TextArea notesFX;
    @FXML private Label badOrderNumFX;
    @FXML private TextField customerTextFieldFX;
    @FXML private Button saveOrderButtonFX;

    orderClass pasutijums = new orderClass();
//    String typedChar = "";
    String typedString = "";
    long t1, t2;
    int i = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerFX.setVisibleRowCount(15);
        pasNrFX.requestFocus();

        try { listCustomers(); } catch (SQLException throwables) { throwables.printStackTrace(); }

        //Ielasam menedžerus no db
        ObservableList<String> options = FXCollections.observableArrayList();
        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        String sql = "SELECT manager FROM projectmanagers";
        Statement pst = null;
        ResultSet rs = null;
        try {
            pst = connection.prepareStatement(sql);
            rs = pst.executeQuery(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        while (true) {
            try {
                if (!rs.next()) break;
                options.add(rs.getString("manager"));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        projectManagerFX.setItems(options);

        try {
            pst.close();
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        dueDateFX.setOnAction((event) -> {
            pasutijums.setIzpildesDatums(dueDateFX.getValue().toString());
        });


        reproFX.setOnAction((event) -> {
            int selectedIndex = reproFX.getSelectionModel().getSelectedIndex();
            Object selectedItem = reproFX.getSelectionModel().getSelectedItem();

            System.out.println("Izvēlēts: [" + selectedIndex + "] " + selectedItem +
                    "\n\tComboBox.getValue(): " + reproFX.getValue());

        });


        /*/////////////////////////////////////////////////////////////////////////////////
        //Klausītājs klienta kombo boksim uz pirmiem burtiem piemeklē atbilstošus klientus
        /////////////////////////////////////////////////////////////////////////////////*/
        customerFX.setOnKeyPressed((actionEvent) ->  {
            Calendar c1 = Calendar.getInstance();
            Date timeStamp = c1.getTime();

            if(actionEvent.getCode() != KeyCode.TAB && actionEvent.getCode() != KeyCode.ENTER && actionEvent.getCode() != KeyCode.UP&& actionEvent.getCode() != KeyCode.DOWN){
                typedString = typedString + actionEvent.getCode();
                t2 = timeStamp.getTime();
//                System.out.println(actionEvent.getCode());
                if(t2 - t1 > 2000){
                    typedString = actionEvent.getCode().toString();
                }
                t1 = t2;

                customerFX.show();
            }

            ObservableList<String> optionsC = FXCollections.observableArrayList();
            String sql2 = "SELECT * FROM orders.customers WHERE companyName LIKE '" + typedString + "%'";
            if (actionEvent.getCode() == KeyCode.MULTIPLY) sql2 = "SELECT * FROM orders.customers";
            System.out.println(typedString);

            try {
                Statement pst2 = connection.prepareStatement(sql2);
                ResultSet rs2 = pst2.executeQuery(sql2);
                while (rs2.next()) {
                    i++;
                    optionsC.add(rs2.getString("companyName"));
                    customerFX.setValue(rs2.getString("companyName"));
                }

                if (i > 15) {
                    customerFX.setVisibleRowCount(15);
                } else {
                    customerFX.setVisibleRowCount(i);
                }
                pst2.close();
                rs2.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            Collections.sort(optionsC);
            customerFX.setItems(optionsC);

        });
    }


    // klienta unikalitātes čeks, jāuzlabo
    public Boolean checkCustomerFX() throws SQLException {
        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        String sql = "SELECT * FROM orders.customers WHERE companyName = '" + customerTextFieldFX.getText() + "'";
        System.out.println(sql);
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        if (rs.next()) {
//            System.out.println(rs.getString("companyName"));
            System.out.println("Atrasts: " + rs.getString("companyName"));
            statement.close();
            rs.close();
            return true;
        } else {
            System.out.println("Neatradās");
            statement.close();
            rs.close();
            return false;
        }
    }


    //pievieno jaunu klientu datu bāzei
    public void addCustomer2(ActionEvent event) throws SQLException {
        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        String sql = "INSERT INTO CUSTOMERS (companyName) VALUES ('" + customerTextFieldFX.getText() + "')";
//        System.out.println(sql);
        Statement statement = connection.createStatement();
        if (!checkCustomerFX() && customerTextFieldFX.getText() != "") {
            statement.executeUpdate(sql);
            System.out.println("pievienots klients " + customerTextFieldFX.getText());
            customerFX.requestFocus();
            listCustomers();                //ielasa par jaunu comboboksī klientus
            customerFX.setValue(customerTextFieldFX.getText());
        }
        statement.close();
    }


    //Saglabāt ievadīto pasūtījumu
    //pāriet uz pasūtījumu kopskatu
    public void saveOrder(ActionEvent event) throws IOException, SQLException {
        if (pasNrFX.getText().length() == 8) {
            badOrderNumFX.setText("Pas numurs ≠ 8 zīmes!");
            System.out.println("Pas numurs ≠ 8 zīmes!");
        } else badOrderNumFX.setText("");

        if (!checkOrderNumber() && pasNrFX.getText().length() == 8){
            pasutijums.setPasNr(pasNrFX.getText());
            pasutijums.setKlients((String) (customerFX.getValue()));
            pasutijums.setDarbaNosaukums(jobNameFX.getText());
            pasutijums.setDarbaLapaYN(jobSheetYNFX.isSelected());
            pasutijums.setRepro((String) reproFX.getValue());
            pasutijums.setManager((String) projectManagerFX.getValue());
            pasutijums.setPapiraStatuss((String) paperStateFX.getValue());
            pasutijums.setOffset(offsetYNFX.isSelected());
            pasutijums.setDigital(digitalYNFX.isSelected());
            pasutijums.setDesign(designYNFX.isSelected());
            pasutijums.setLargeFormat(largeFormatYNFX.isSelected());
            pasutijums.setPostPress(postPressYNFX.isSelected());
            pasutijums.setOtherJob(otherJobFX.isSelected());
            if(dueDateFX.getValue() == null){ // ja nav ievadīts datums, tad par izpildes datumu kļūst rītdiena
                LocalDate today = LocalDate.now();
                LocalDate tomorrow = today.plusDays(1);
                pasutijums.setIzpildesDatums(tomorrow.toString());
            } else {
                pasutijums.setIzpildesDatums(dueDateFX.getValue().toString()); }
            pasutijums.setNodrukatsYN(printedYNFX.isSelected());
            pasutijums.setDarbaStatuss((String) projectStateFX.getValue());
            pasutijums.setPiezimes(notesFX.getText());
            pasutijums.setOrderLock(false);
//            pasutijums.printAllVariables();

            pasutijums.writeOrderInDatabase();

            // atpakaļ uz pasūtījumu sarakstu
            Parent viewOrders = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
            Scene viewOrdersScene = new Scene(viewOrders);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

            window.setScene(viewOrdersScene);
            window.show();
        }
    }


    // pārbauda pasūtījuma numura unikalitāti, nav lieks!!!
    public Boolean checkOrderNumber() throws SQLException {
        saveOrderButtonFX.setDisable(false);
        pasNrFX.setStyle("-fx-text-inner-color: black");
        badOrderNumFX.setText("");
        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
//        String pNr = pasNrFX.getText();

        if (pasNrFX.getText().length() != 8) {
            badOrderNumFX.setText("Pas numurs ≠ 8 zīmes!");
        } else badOrderNumFX.setText("");

//        System.out.println(pasNrFX.getLength());
        if(pasNrFX.getText().length() == 8){
            String sql = "SELECT pasNr FROM orders WHERE pasNr = '" + pasNrFX.getText() + "'";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                if(rs.getString("pasNr").equals(pasNrFX.getText())){
                    pasNrFX.setStyle("-fx-text-inner-color: red");
                    badOrderNumFX.setText("Dublējas numurs!");
                    badOrderNumFX.setStyle("-fx-text-inner-color: red");
//                    pasNrFX.selectAll();
                    saveOrderButtonFX.setDisable(true);
                    return true;
                } else saveOrderButtonFX.setDisable(false);
            }
            rs.close();
            statement.close();
        }
        if(pasNrFX.getText().length() > 8) {
            pasNrFX.deletePreviousChar();
            badOrderNumFX.setText("");
        }
        return false;
    }


    //Jaunā klienta logam, lieks
    public void customerOnType() throws SQLException {
        String typedString;
        String foundName = null;
        typedString = (String) customerTextFieldFX.getText();
        System.out.println(typedString);

        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        String sql = "SELECT * FROM orders.customers WHERE companyName LIKE '" + typedString + "%'";
        System.out.println(sql);
        Statement pst = connection.prepareStatement(sql);
        ResultSet rs = pst.executeQuery(sql);
        System.out.print("FoundName: ");
        while (rs.next()) {
            foundName = rs.getString("companyName");
            System.out.print(foundName + "\t");
            customerTextFieldFX.setText(foundName);
        }
        if(foundName != null){
            customerTextFieldFX.positionCaret(typedString.length());
            customerTextFieldFX.extendSelection(foundName.length());
        }
    }


    //piepildam klientu sarakstu no klientu tabulas
    public void listCustomers() throws SQLException {
        ObservableList<String> optionsC = FXCollections.observableArrayList();

        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        String sql = "SELECT companyName FROM orders.customers";
        Statement pst = connection.prepareStatement(sql);
        ResultSet rs = pst.executeQuery(sql);

        while (rs.next()) {
            optionsC.add(rs.getString("companyName"));
        }
        Collections.sort(optionsC);
        customerFX.setItems(optionsC);
        pst.close();
        rs.close();
    }


    //Atteikt poga – atpakaļ uz pamatlogu
    public void backToMain(ActionEvent event) throws IOException {
        Parent viewOrders = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        Scene viewOrdersScene = new Scene(viewOrders);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(viewOrdersScene);
        window.show();
    }
}