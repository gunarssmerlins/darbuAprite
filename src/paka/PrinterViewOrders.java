package paka;

import connectivity.mysqlConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class PrinterViewOrders implements Initializable {
    @FXML public Label ppasNrFX;
    @FXML private Label pcustomerFX;
    @FXML private Label pjobNameFX;
    @FXML private Label pprepressFX;
    @FXML private Label pmanagerFX;
    @FXML private Label ppaperFX;
    @FXML private Label pprintFX;
    @FXML private Label duedateFX;
    @FXML private ComboBox pprinterFX;
    @FXML private TextArea pnotesFX;

    orderClass pasutijums = new orderClass();
    public String pasNr;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();


    }

    /* ielasa info visos laukos ielasīt laukus objektā
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
                    pcustomerFX.setText(pasutijums.getCustomerByID(rs.getInt("customerID")));
                    pjobNameFX.setText(rs.getString("jobName"));
//                    jobSheetYNFX.setSelected(rs.getBoolean("worksheet"));
                    pprepressFX.setText(rs.getString("prepressOp"));
                    pmanagerFX.setText(pasutijums.getManagerByID(rs.getInt("managerID")));
                    ppaperFX.setText(rs.getString("paper"));
//                    offsetYNFX.setSelected(rs.getBoolean("ofsets"));
//                    digitalYNFX.setSelected(rs.getBoolean("digital"));
//                    designYNFX.setSelected(rs.getBoolean("design"));
//                    largeFormatYNFX.setSelected(rs.getBoolean("largeFormat"));
//                    postPressYNFX.setSelected(rs.getBoolean("postpress"));
                    duedateFX.setText(String.valueOf(rs.getDate("dueDate")));
//                    if(datums != null){
//                        java.sql.Date sqlDate = java.sql.Date.valueOf(String.valueOf(datums));
//                        LocalDate localDate2 = sqlDate.toLocalDate();
//                        dueDateFX.setValue(localDate2);
//                    }
//                    printedYNFX.setSelected(rs.getBoolean("printedYN"));
//                    projectStateFX.setValue(rs.getString("projectPrepState"));
                    pnotesFX.setText(rs.getString("notes"));
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

}
