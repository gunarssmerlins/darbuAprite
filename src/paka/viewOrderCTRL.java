package paka;

import connectivity.mysqlConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.w3c.dom.ls.LSOutput;

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

public class viewOrderCTRL implements Initializable {
    @FXML private ComboBox customerFX;
    @FXML private DatePicker dueDateFX;
    @FXML public TextField pasNrFX;
    @FXML private TextField jobNameFX;
    @FXML private CheckBox jobSheetYNFX;
    @FXML private ComboBox<String> reproFX;
    @FXML private ComboBox<String> projectManagerFX;
    @FXML private ComboBox<String> paperStateFX;
    @FXML private CheckBox offsetYNFX;
    @FXML private CheckBox digitalYNFX;
    @FXML private CheckBox designYNFX;
    @FXML private CheckBox largeFormatYNFX;
    @FXML private CheckBox postPressYNFX;
    @FXML private CheckBox otherJobFX;
    @FXML private CheckBox printedYNFX;
    @FXML private ComboBox<String> projectStateFX;
    @FXML private TextArea notesFX;
    @FXML public Label pasNrLabelFX;

    orderClass pasutijums = new orderClass();
    String pasNr;
    String typedString = "";
    long t1, t2;
    public String searchField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        try {
            fillAllFields();    //ielasa visus laukus
            listCustomers();    //ielasa klientu kombo boksī visus klientus
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        /*/////////////////////////////////////////////////////////////////////////////////
        //Klausītājs klienta kombo boksim uz pirmiem burtiem piemeklē atbilstošus klientus
        .................nestrādā
        /////////////////////////////////////////////////////////////////////////////////*/
        customerFX.setOnKeyTyped((actionEvent) ->  {
            Calendar c1 = Calendar.getInstance();
            Date timeStamp = c1.getTime();

            if(actionEvent.getCode() != KeyCode.TAB){
                typedString = typedString + actionEvent.getCode();
                t2 = timeStamp.getTime();
                if(t2 - t1 > 2000){
                    typedString = actionEvent.getCode().toString();
                }
                t1 = t2;
            }

            ObservableList<String> optionsC = FXCollections.observableArrayList();
            String sql2 = "SELECT * FROM orders.customers WHERE companyName LIKE '" + typedString + "%'";

            try {
                Statement pst2 = connection.prepareStatement(sql2);
                ResultSet rs2 = pst2.executeQuery(sql2);
                while (rs2.next()) {
                    optionsC.add(rs2.getString("companyName"));
                    customerFX.setValue(rs2.getString("companyName"));
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


    /* ielasa info visos laukos ielasīt laukus objektā pārbaudīt vai nav aizņemts
    ja aizņemts, tad notīrīt logu izdrukāt paziņojumu un pogu atpakaļ uz galveno logu
    */
    public void fillAllFields() throws SQLException {
        Date datums;
        boolean orderLock = false;

        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        Statement stmt;
        stmt = (Statement) connection.createStatement();
        String sql = "SELECT * FROM ORDERS.orders Where pasNr = '" + pasNr + "'";
        Statement statement = null;
        ResultSet rs = null;
        statement = connection.createStatement();
        rs = statement.executeQuery(sql);

        // ielasa no objektā info no db
        while (true) {
            try {
                assert rs != null;
                if (!rs.next()) break;
                else{
                    customerFX.setValue(pasutijums.getCustomerByID(rs.getInt("customerID")));
                    jobNameFX.setText(rs.getString("jobName"));
                    jobSheetYNFX.setSelected(rs.getBoolean("worksheet"));
                    reproFX.setValue(rs.getString("prepressOp"));
                    projectManagerFX.setValue(pasutijums.getManagerByID(rs.getInt("managerID")));
                    paperStateFX.setValue(rs.getString("paper"));
                    offsetYNFX.setSelected(rs.getBoolean("ofsets"));
                    digitalYNFX.setSelected(rs.getBoolean("digital"));
                    designYNFX.setSelected(rs.getBoolean("design"));
                    largeFormatYNFX.setSelected(rs.getBoolean("largeFormat"));
                    postPressYNFX.setSelected(rs.getBoolean("postpress"));
                    datums = rs.getDate("dueDate");
                    if(datums != null){
                        java.sql.Date sqlDate = java.sql.Date.valueOf(String.valueOf(datums));
                        LocalDate localDate2 = sqlDate.toLocalDate();
                        dueDateFX.setValue(localDate2);
                    }
                    printedYNFX.setSelected(rs.getBoolean("printedYN"));
                    projectStateFX.setValue(rs.getString("projectPrepState"));
                    notesFX.setText(rs.getString("notes"));
                    orderLock = rs.getBoolean("orderlock");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
//        System.out.println("Orderlok - " + orderLock);

        if(!orderLock){
            String query1 = "UPDATE ORDERS.orders SET orderlock = 1 WHERE pasNr = '" + pasNr + "'";
            stmt.executeUpdate(query1);

            pasutijums.setOrderLock(true);
            try {
                rs.close();
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else{
            //parādīt info logu par aizņemtu pas

        }

        try {
            rs.close();
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    //piepildam klientu sarakstu no klientu tabulas
    public void listCustomers() throws SQLException {
        ObservableList<String> options = FXCollections.observableArrayList();

        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        String sql = "SELECT companyName FROM CUSTOMERS";
        Statement pst = connection.prepareStatement(sql);
        ResultSet rs = pst.executeQuery(sql);

        while (rs.next()) {
            options.add(rs.getString("companyName"));
        }

        Collections.sort(options);

        customerFX.setItems(options);
        pst.close();
        rs.close();
    }


    //piepildam projvadu sarakstu no projvadu tabulas
    public void listManagers() throws SQLException {
        ObservableList<String> options = FXCollections.observableArrayList();

        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        String sql = "SELECT manager FROM projectmanagers";
        Statement pst = connection.prepareStatement(sql);
        ResultSet rs = pst.executeQuery(sql);
        while (rs.next()) {
            options.add(rs.getString("manager"));
        }
//        System.out.println(projectManagerFX.getValue());
        projectManagerFX.setItems(options);
        pst.close();
        rs.close();
    }


    //Saglabāt ievadīto pasūtījumu
    //pāriet uz pasūtījumu kopskatu
    public void updateOrder(ActionEvent event) throws IOException, SQLException {
        //ielasām objektā visus laukus
        pasutijums.setCustomerID(pasutijums.getCustomerID((String) customerFX.getValue()));
        pasutijums.setManagerID(pasutijums.getManagerID(projectManagerFX.getValue()));
        pasutijums.setPasNr(pasNr);
        pasutijums.setKlients(String.valueOf(customerFX.getValue()));
        pasutijums.setDarbaNosaukums(jobNameFX.getText());
        pasutijums.setDarbaLapaYN(jobSheetYNFX.isSelected());
        pasutijums.setRepro(reproFX.getValue());
        pasutijums.setManager(projectManagerFX.getValue());
        pasutijums.setPapiraStatuss(paperStateFX.getValue());
        pasutijums.setOffset(offsetYNFX.isSelected());
        pasutijums.setDigital(digitalYNFX.isSelected());
        pasutijums.setDesign(designYNFX.isSelected());
        pasutijums.setLargeFormat(largeFormatYNFX.isSelected());
        pasutijums.setPostPress(postPressYNFX.isSelected());
        pasutijums.setOtherJob(otherJobFX.isSelected());
        pasutijums.setIzpildesDatums(dueDateFX.getValue().toString());
        pasutijums.setNodrukatsYN(printedYNFX.isSelected());
        pasutijums.setDarbaStatuss(projectStateFX.getValue());
        pasutijums.setPiezimes(notesFX.getText());
        pasutijums.setOrderLock(false);

//        pasutijums.printAllVariables();

        pasutijums.updateOrder();

        Parent viewOrders = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        Scene viewOrdersScene = new Scene(viewOrders);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(viewOrdersScene);
        window.show();
    }

    //Atteikt poga – atpakaļ uz pamatlogu
    public void backToMain(ActionEvent event) throws IOException, SQLException {
        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        Statement stmt;
        stmt = (Statement) connection.createStatement();
        String query = "UPDATE ORDERS.orders SET orderlock = 0 WHERE pasNr = '" + pasNr + "'";
        stmt.executeUpdate(query);
        try {
            stmt.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        Parent viewOrders = FXMLLoader.load(getClass().getResource("mainWindow.fxml"));
        Scene viewOrdersScene = new Scene(viewOrders);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(viewOrdersScene);
        window.show();
    }
}