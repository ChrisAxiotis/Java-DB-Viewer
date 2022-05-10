import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class Connector {


    public Connection conn;


    public Connector(String url, String user, String pass, String port){
            Connect(url,user,pass,port);
    }

    public void Connect(String url, String user, String pass, String port) {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://" + url, user, pass);
            System.out.println("Connected!");
        } catch (SQLException e) {
            System.out.println("Cant Connect!");
            Program.infoBox("Can't Connect!", "Alert!");
        }
    }


    public List GetSchemas() throws SQLException {
        List<String> schemas = new ArrayList<>();

        ResultSet rs = conn.getMetaData().getCatalogs();
        while (rs.next()) {
            schemas.add(rs.getString("TABLE_CAT"));
        }

        return schemas;
    }


    public List GetTable(String schema) throws SQLException {
        List<String> tables = new ArrayList<>();

        DatabaseMetaData md = conn.getMetaData();
        ResultSet rs = md.getTables(schema, null, "%", null);
        while (rs.next()) {
            tables.add(rs.getString(3));
        }
        return tables;
    }

    public JTable GetFromTable(String schema, String table) throws SQLException {

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + schema + "." + table);

        return new JTable(buildTableModel(rs));
    }



    public static DefaultTableModel buildTableModel(ResultSet rs)
            throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);

    }


    public boolean ValidCheck(String key) throws SQLException {
        boolean isValid = false;

        for (Object str:  GetSchemas()) {
            if(key.equals(str))
                isValid = true;
        }

        return isValid;
    }





}
