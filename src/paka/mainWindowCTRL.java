/*//////////////////////////////////////////////////////////
                  Pasūtījumu kopskata logs
//////////////////////////////////////////////////////////*/

package paka;

import connectivity.mysqlConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class mainWindowCTRL implements Initializable {

    @FXML public TextField findByOrderNumFX;
    @FXML private Button viewOrderButtonFX;
    @FXML private TableView<orderClass> tableFX;
    @FXML private Button newOrderFX;

    final ObservableList<orderClass> saraksts = FXCollections.observableArrayList();
    //    public String NumberToFind;
//    orderClass pas = new orderClass();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        newOrderFX.requestFocus();
        viewOrderButtonFX.setDisable(true);

        // kolonnas
        TableColumn pasNrColFX = new TableColumn("Pas. numurs");
        TableColumn darbaNosColFX = new TableColumn("Nosaukums");
        TableColumn klientsColFX = new TableColumn("Klients");
        TableColumn darbaLapaYNColFX = new TableColumn("D/L");
        TableColumn reproColFX = new TableColumn("Repro");
        TableColumn papirsColFX = new TableColumn("Papīrs");
        TableColumn projVadColFX = new TableColumn("Menedžeris");
        TableColumn dueDateColFX = new TableColumn("Izp.datums");
        TableColumn printedYNFX = new TableColumn("Nodrukāts?");
        // un to platumi
        pasNrColFX.setPrefWidth(70);
        darbaNosColFX.setPrefWidth(220);
        klientsColFX.setPrefWidth(140);
        darbaLapaYNColFX.setPrefWidth(280);
        darbaLapaYNColFX.setPrefWidth(50);
        reproColFX.setPrefWidth(70);
        papirsColFX.setPrefWidth(90);
        projVadColFX.setPrefWidth(70);
        dueDateColFX.setPrefWidth(80);
        printedYNFX.setPrefWidth(60);
//        pasNrColFX.setCellValueFactory();

        tableFX.getColumns().addAll(pasNrColFX, darbaNosColFX, klientsColFX, darbaLapaYNColFX,
                reproColFX, papirsColFX, projVadColFX, dueDateColFX, printedYNFX);

        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        String sql;

        sql = "select orders.orderID, orders.pasNr, orders.customerID, orders.managerID, customers.companyName, orders.jobName, " +
                "projectManagers.manager, orders.worksheet, orders.prepressOp, " +
                "orders.paper, orders.ofsets, orders.digital, orders.design, orders.largeFormat, orders.postpress, orders.otherJob, orders.dueDate, " +
                "orders.printedYN, orders.projectPrepState, orders.notes, orders.orderLock " +
                "FROM ORDERS " +
                "INNER JOIN customers ON orders.CustomerID=customers.CustomerID " +
                "INNER JOIN projectManagers ON orders.managerID=projectManagers.managerID";

        Statement pst = null;
        ResultSet rs = null;
        try {
            pst = connection.prepareStatement(sql);
            rs = pst.executeQuery(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //saistu tabulas kolonnu ar objekta attiecīgo mainīgo
        pasNrColFX.setCellValueFactory(new PropertyValueFactory<>("pasNr"));
        darbaNosColFX.setCellValueFactory(new PropertyValueFactory<>("darbaNosaukums"));
        klientsColFX.setCellValueFactory(new PropertyValueFactory<>("Klients"));
        darbaLapaYNColFX.setCellValueFactory(new PropertyValueFactory<>("darbaLapaYN"));
        reproColFX.setCellValueFactory(new PropertyValueFactory<>("repro"));
        papirsColFX.setCellValueFactory(new PropertyValueFactory<>("papiraStatuss"));
        projVadColFX.setCellValueFactory(new PropertyValueFactory<>("manager"));
        dueDateColFX.setCellValueFactory(new PropertyValueFactory<>("izpildesDatums"));
        printedYNFX.setCellValueFactory(new PropertyValueFactory<>("nodrukatsYN"));
//        editOrderFX.setCellValueFactory(new PropertyValueFactory<>("button"));

        //ielasu no DB nepieciešamos datus >> "pas" objektā
        while (true) {
            try {
                if (!rs.next()) break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                saraksts.add(new orderClass(rs.getInt("orderID"),
                        rs.getInt("customerID"), rs.getInt("managerID"), rs.getString("manager"),
                        rs.getString("pasNr"), rs.getString("companyName"), rs.getString("jobName"),
                        rs.getBoolean("worksheet"), rs.getString("prepressOp"), rs.getString("paper"),
                        rs.getBoolean("ofsets"), rs.getBoolean("digital"), rs.getBoolean("design"),
                        rs.getBoolean("largeFormat"), rs.getBoolean("postpress"), rs.getBoolean("otherJob"),
                        rs.getString("dueDate"), rs.getBoolean("printedYN"), rs.getString("projectPrepState"),
                        rs.getString("notes"), rs.getBoolean("orderlock")));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        // 1. Padod ObservableList uz FilteredList.
        FilteredList<orderClass> filteredData = new FilteredList<>(saraksts, p -> true);
        // 2. Iestatam filtra klausītāju.
        findByOrderNumFX.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(pas -> {
                // Ja lauks ir tukšs, tad rāda visus ierakstus
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // lasam pēc filtra
                String lowerCaseFilter = newValue.toLowerCase();

                if (pas.getPasNr().toLowerCase().contains(lowerCaseFilter)) {
                    return true;        // filtrē pēc pas numura
                } else if (pas.getDarbaNosaukums().toLowerCase().contains(lowerCaseFilter)) {
                    return true;        // filtrē pēc darba nosaukuma
                } else if (pas.getKlients().toLowerCase().contains(lowerCaseFilter)) {
                    return true;        // filtrē pēc klienta
                } else return false; // ja nav, tad nav
            });
        });

        // 3. Padod FilteredList uz SortedList.
        SortedList<orderClass> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(tableFX.comparatorProperty());

        // 5. Padod sakārtoto sarakstu.
        tableFX.setItems(sortedData);

        // Sakārtoju pēc svaigākā pasūtījuma numura
        pasNrColFX.setSortType(TableColumn.SortType.DESCENDING);
        tableFX.getSortOrder().add(pasNrColFX);
        tableFX.sort();

        // kad tukša tabula vai nekas neatbilst meklēšans kritērijiem
        tableFX.setPlaceholder(new Label("Bēdīgi, nav pasūtījumu. Ļoti bēdīgi."));

        // iegūt iezīmētās rindas pasūtījuma numuru
        tableFX.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 1) {
//                    System.out.println(tableFX.getSelectionModel().getSelectedItem().getPasNr());
                    orderNumToEdit = tableFX.getSelectionModel().getSelectedItem().getPasNr();
                    viewOrderButtonFX.setText("Skatīt " + orderNumToEdit);
                    viewOrderButtonFX.setDisable(false);
                }
            }
        });

        try {
            pst.close();
            rs.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        tableFX.setRowFactory(row -> new TableRow<orderClass>(){
            @Override
            public void updateItem(orderClass item, boolean empty){
                super.updateItem(item, empty);
                LocalDate today = LocalDate.now();
                int todayStamp;

                todayStamp = parseDate(today.toString());

                if (item == null || empty) {
                    setStyle("");
                } else {
                    //pirmajā if visi nosacījumi, iekšējos if katrs nosacījums atsevišķi
                    if (parseDate(item.getIzpildesDatums()) <= todayStamp + 1 && (item.getRepro().equals("")
                            || item.getRepro().equals("null"))
                            || item.isNodrukatsYN()
                            || item.getDarbaStatuss().equals("Apstādināts – ir problēmas")
                            || item.getDarbaStatuss().equals("Stiprinās")) {

                        //iekrāso visas atbilstošās rindas šūnas
                        if (item.isNodrukatsYN()) {
                            for (int i=0; i<getChildren().size(); i++){
                                ((Labeled) getChildren().get(i)).setTextFill(Color.WHITE);
                                getChildren().get(i).setStyle("-fx-background-color: green");
                            }
                        }
                        if (item.isNodrukatsYN() && getTableView().getSelectionModel().getSelectedItems().contains(item)) {
                            for (int i=0; i<getChildren().size(); i++){
                                ((Labeled) getChildren().get(i)).setTextFill(Color.GREEN);
                                getChildren().get(i).setStyle("-fx-background-color: blue");
                            }
                        }
//
                        if (parseDate(item.getIzpildesDatums()) <= todayStamp + 1 && (item.getRepro().equals("") || item.getRepro().equals("null"))) {
                            for (int i=0; i<getChildren().size(); i++){
                                ((Labeled) getChildren().get(i)).setTextFill(Color.BLACK);
                                getChildren().get(i).setStyle("-fx-background-color: red");
                            }
                        }
                        if ((parseDate(item.getIzpildesDatums()) <= todayStamp + 1 && (item.getRepro().equals("") || item.getRepro().equals("null"))) && getTableView().getSelectionModel().getSelectedItems().contains(item)) {
                            for (int i=0; i<getChildren().size(); i++){
                                ((Labeled) getChildren().get(i)).setTextFill(Color.RED);
                                getChildren().get(i).setStyle("-fx-background-color: blue");
                            }
                        }

                        if (item.getDarbaStatuss().equals("Apstādināts – ir problēmas")) {
                            for (int i=0; i<getChildren().size(); i++){
                                ((Labeled) getChildren().get(i)).setTextFill(Color.RED);
                                getChildren().get(i).setStyle("-fx-background-color: white");
                            }
                        }
                        if (item.getDarbaStatuss().equals("Apstādināts – ir problēmas") && getTableView().getSelectionModel().getSelectedItems().contains(item)) {
                            for (int i=0; i<getChildren().size(); i++){
                                ((Labeled) getChildren().get(i)).setTextFill(Color.RED);
                                getChildren().get(i).setStyle("-fx-background-color: blue");
                            }
                        }

                        if (item.getDarbaStatuss().equals("Stiprinās")) {
                            for (int i=0; i<getChildren().size(); i++){
                                ((Labeled) getChildren().get(i)).setTextFill(Color.GREEN);
                                getChildren().get(i).setStyle("-fx-background-color: white");
                            }
                        }
                        if (item.getDarbaStatuss().equals("Stiprinās") && getTableView().getSelectionModel().getSelectedItems().contains(item)) {
                            for (int i=0; i<getChildren().size(); i++){
                                ((Labeled) getChildren().get(i)).setTextFill(Color.GREEN);
                                getChildren().get(i).setStyle("-fx-background-color: blue");
                            }
                        }

                    } else { // vienkārši iezīmēta rinda
                        if (getTableView().getSelectionModel().getSelectedItems().contains(item)){ //selected colors
                            for(int i=0; i<getChildren().size();i++){
                                ((Labeled) getChildren().get(i)).setTextFill(Color.WHITE);
                                getChildren().get(i).setStyle("-fx-background-color: blue");
                            }
                        }
                        else { //neatbilst atlasei >> normāls stils
                            for(int i=0; i<getChildren().size();i++){
                                ((Labeled) getChildren().get(i)).setTextFill(Color.BLACK);
                                getChildren().get(i).setStyle(""); //-fx-background-color: white
                            }
                        }
                    }

                }
            }
        });


    }

    public int parseDate (String s){
        return Integer.parseInt(s.substring(0,4)) * 365 +
                Integer.parseInt(s.substring(5,7)) * 12 +
                Integer.parseInt(s.substring(8,10));
    }

    /*//////////////////////////////////////////////////////////
             Izsauc pasūtījuma apskati un labošanu
    //////////////////////////////////////////////////////////*/
    String orderNumToEdit;
    public void viewOrder(ActionEvent event) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("viewOrder.fxml"));
        Parent root = loader.load();
        viewOrderCTRL viewOrderCTRL = loader.getController();

        viewOrderCTRL.pasNr = orderNumToEdit;
        viewOrderCTRL.pasNrLabelFX.setText(orderNumToEdit);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(new Scene(root));
        window.show();
        viewOrderCTRL.fillAllFields();
    }

    /*//////////////////////////////////////////////////////////
                  Izsauc jauna pasūtījuma logu
    //////////////////////////////////////////////////////////*/
    public void newOrder(ActionEvent event) throws IOException {
        Parent viewOrders = FXMLLoader.load(getClass().getResource("newOrder.fxml"));
        Scene viewOrdersScene = new Scene(viewOrders);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(viewOrdersScene);
        window.show();
    }
}