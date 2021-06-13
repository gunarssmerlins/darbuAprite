package paka;

import connectivity.mysqlConnection;
import javafx.scene.control.Button;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class orderClass {

    private int ID;
    private int customerID;
    private int managerID;
    private String manager;
    private String pasNr;
    private String Klients;
    private String darbaNosaukums;
    private boolean darbaLapaYN;
    private String repro;
    private String papiraStatuss;
    private boolean offset;
    private boolean digital;
    private boolean design;
    private boolean largeFormat;
    private boolean postPress;
    private boolean otherJob;
    private String izpildesDatums;
    private boolean nodrukatsYN;
    private String darbaStatuss;
    private String piezimes;
    private Boolean orderLock;


    public orderClass() {

    }

    //iegūstam saistošo ID pēc klienta vārda
    public int getCustomerID(String company) throws SQLException {
        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        String sql = "SELECT customerID FROM customers Where companyName = '" + company + "'";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            ID = rs.getInt("customerID");
        }
        rs.close();
        statement.close();
        return ID;
    }


    //iegūstam klienta vārdu pēc klientaID vārda
    public String getCustomerByID(int companyID) throws SQLException {
        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        String sql = "SELECT companyName FROM customers Where customerID = " + companyID;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        String customer = "";
        while (rs.next()) {
            customer = rs.getString("companyName");
        }
        rs.close();
        statement.close();
        return customer;
    }


    //iegūstam projekta vadītāja vārdu pēc managerID vārda
    public String getManagerByID(int managerID) throws SQLException {
        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        String sql = "SELECT manager FROM projectManagers Where managerID = " + managerID;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            manager = rs.getString("manager");
        }
        rs.close();
        statement.close();
        return manager;
    }


    //iegūstam saistošo ID pēc menedžera vārda
    public int getManagerID(String manager) throws SQLException {
        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        String sql = "SELECT managerID FROM projectManagers Where manager = '" + manager + "'";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            ID = rs.getInt("managerID");
        }
//        System.out.println("getManagerID: " + manager + " ID=" + ID);
        rs.close();
        statement.close();
        return ID;
    }


    public void updateOrder() throws SQLException {
        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        String sql = "UPDATE orders.orders SET " +
                "customerID = " + customerID + ", " +
                "managerID = " + managerID + ", " +
                "jobName = '" + darbaNosaukums +
                "', worksheet = " + darbaLapaYN +
                ", prepressOp = '" + repro +
                "', paper = '" + papiraStatuss +
                "', ofsets = " + offset +
                ", digital = " + digital +
                ", design = " + design +
                ", largeFormat = " + largeFormat +
                ", postpress = " + postPress +
                ", otherJob = " + otherJob +
                ", dueDate = '" + izpildesDatums +
                "', printedYN = " + nodrukatsYN +
                ", projectPrepState = '" + darbaStatuss +
                "', notes = '" + piezimes +
                "', orderlock = " + orderLock +
                " WHERE (pasNr = '" + pasNr + "')";
        System.out.println(sql);

        Statement stmt;
        stmt = connection.createStatement();
        stmt.executeUpdate(sql);
        System.out.println("Updeits, tipa, sanāca ;)");
        stmt.close();
        connection.close();
    }


    public void writeOrderInDatabase() throws SQLException {
        mysqlConnection mysqlConnection = new mysqlConnection();
        Connection connection = mysqlConnection.getConnection();
        String sql = "INSERT INTO orders (" +
                "customerID, " +
                "managerID, " +
                "pasNr, " +
                "jobName, " +
                "worksheet, " +
                "prepressOp, " +
                "paper, " +
                "ofsets," +
                "digital, " +
                "design, " +
                "largeFormat, " +
                "postpress, " +
                "otherJob, " +
                "dueDate, " +
                "printedYN, " +
                "projectPrepState, " +
                "notes, " +
                "orderlock) " +
                "VALUES (" +
                getCustomerID(Klients) + ", " +
                getManagerID(manager) + ", '" +
                pasNr + "', '" +
                darbaNosaukums+ "', " +
                darbaLapaYN + ", '" +
                repro + "', '" +
                papiraStatuss + "', " +
                offset + ", " +
                digital + ", " +
                design + ", " +
                largeFormat + ", " +
                postPress + ", " +
                otherJob + ", '" +
                izpildesDatums + "', " +
                nodrukatsYN + ", '" +
                darbaStatuss + "', '" +
                piezimes + "', " +
                orderLock + ")";
//        System.out.println("Pasūtījuma rinda: " + sql);
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);

        statement.close();
        connection.close();
//        System.out.println("Jauns pas tipa sanāca ;)");
    }


    public void printAllVariables(){
        System.out.println(pasNr + "\t" +
                Klients + "\t" +
                darbaNosaukums + "\t" +
                manager + "\t" +
                darbaLapaYN + "\t" +
                repro + "\t" +
                /*projektaMenedzers + "\t" +*/
                papiraStatuss + "\t" +
                offset + "\t" +
                digital + "\t" +
                design + "\t" +
                largeFormat + "\t" +
                postPress + "\t" +
                otherJob + "\t" +
                izpildesDatums + "\t" +
                nodrukatsYN + "\t" +
                darbaStatuss + "\t" +
                piezimes);
    }

    public orderClass(int ID, int customerID, int managerID, String manager, String pasNr, String klients,
                      String darbaNosaukums, boolean darbaLapaYN, String repro, /*String projektaMenedzers,*/
                      String papiraStatuss, boolean offset, boolean digital, boolean design, boolean largeFormat,
                      boolean postPress, boolean otherJob, String izpildesDatums, boolean nodrukatsYN,
                      String darbaStatuss, String piezimes, Boolean orderLock) {
        this.ID = ID;
        this.customerID = customerID;
        this.managerID = managerID;
        this.manager = manager;
        this.pasNr = pasNr;
        this.Klients = klients;
        this.darbaNosaukums = darbaNosaukums;
        this.darbaLapaYN = darbaLapaYN;
        this.repro = repro;
//        this.projektaMenedzers = projektaMenedzers;
        this.papiraStatuss = papiraStatuss;
        this.offset = offset;
        this.digital = digital;
        this.design = design;
        this.largeFormat = largeFormat;
        this.postPress = postPress;
        this.otherJob = otherJob;
        this.izpildesDatums = izpildesDatums;
        this.nodrukatsYN = nodrukatsYN;
        this.darbaStatuss = darbaStatuss;
        this.piezimes = piezimes;
        this.orderLock = orderLock;
        /*this.button = new Button("Labot");*/
//        this.button = button;
//        printAllVariables();
    }


    //////////////////////////////////////////// geteri un seteri //////////////////////////////////////////////////////

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getManagerID() { return managerID; }

    public void setManagerID(int managerID) {
        this.managerID = managerID;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getPasNr() {
        return pasNr;
    }

    public void setPasNr(String pasNr) {
        this.pasNr = pasNr;
    }

    public String getKlients() {
        return Klients;
    }

    public void setKlients(String klients) {
        Klients = klients;
    }

    public String getDarbaNosaukums() {
        return darbaNosaukums;
    }

    public void setDarbaNosaukums(String darbaNosaukums) {
        this.darbaNosaukums = darbaNosaukums;
    }

    public boolean isDarbaLapaYN() {
        return darbaLapaYN;
    }

    public void setDarbaLapaYN(boolean darbaLapaYN) {
        this.darbaLapaYN = darbaLapaYN;
    }

    public String getRepro() {
        return repro;
    }

    public void setRepro(String repro) {
        this.repro = repro;
    }

//    public String getProjektaMenedzers() {
//        return projektaMenedzers;
//    }
//
//    public void setProjektaMenedzers(String projektaMenedzers) {
//        this.projektaMenedzers = projektaMenedzers;
//    }

    public String getPapiraStatuss() {
        return papiraStatuss;
    }

    public void setPapiraStatuss(String papiraStatuss) {
        this.papiraStatuss = papiraStatuss;
    }

    public boolean isOffset() {
        return offset;
    }

    public void setOffset(boolean offset) {
        this.offset = offset;
    }

    public boolean isDigital() {
        return digital;
    }

    public void setDigital(boolean digital) {
        this.digital = digital;
    }

    public boolean isDesign() {
        return design;
    }

    public void setDesign(boolean design) {
        this.design = design;
    }

    public boolean isLargeFormat() {
        return largeFormat;
    }

    public void setLargeFormat(boolean largeFormat) {
        this.largeFormat = largeFormat;
    }

    public boolean isPostPress() {
        return postPress;
    }

    public void setPostPress(boolean postPress) {
        this.postPress = postPress;
    }

    public boolean isOtherJob() {
        return otherJob;
    }

    public void setOtherJob(boolean otherJob) {
        this.otherJob = otherJob;
    }

    public String getIzpildesDatums() {
        return izpildesDatums;
    }

    public void setIzpildesDatums(String izpildesDatums) {
        this.izpildesDatums = izpildesDatums;
    }

    public boolean isNodrukatsYN() {
        return nodrukatsYN;
    }

    public void setNodrukatsYN(boolean nodrukatsYN) {
        this.nodrukatsYN = nodrukatsYN;
    }

    public String getDarbaStatuss() {
        return darbaStatuss;
    }

    public void setDarbaStatuss(String darbaStatuss) {
        this.darbaStatuss = darbaStatuss;
    }

    public String getPiezimes() {
        return piezimes;
    }

    public void setPiezimes(String piezimes) {
        this.piezimes = piezimes;
    }

    public Boolean getOrderLock() {
        return orderLock;
    }

    public void setOrderLock(Boolean orderLock) {
        this.orderLock = orderLock;
    }

//    public Button getButton() {
//        return button;
//    }
//
//    public void setButton(Button button) {
//        this.button = button;
//    }
}