package stockmarket;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class dbAccess {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String localDB_URL = "jdbc:mysql://localhost:3307/stockmarket";
    //static final String localDB_URL = "jdbc:mysql://localhost:3307/stockmarket";
    static final String remoteDB_URL= "jdbc:mysql://tomforhost:3307/stockmarket";
    
    static final String USER = "root";
    static final String PASS = "eroles";
    static final String PORT = "3307";
    private static Connection conn;
    private static Statement state;
    
    
    public static void Conectar(){
        /*
        This method connects to the db server
        */
        Consola.Mensaje("Inicializando el objeto");
        try {
            Class.forName("com.mysql.jdbc.Connection");
            conn = DriverManager.getConnection(localDB_URL,USER,PASS);
            state = (Statement) conn.createStatement();
        } catch (ClassNotFoundException ex) {
            System.err.println("Error 1: ClassNotFound");
            Logger.getLogger(dbAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            System.err.println("Error: SQLException");
            Logger.getLogger(dbAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void ExecuteNQ(String query) throws Exception{
        /*
        This method executes a SQL statement against the db server, that does not have to return values
        It is used to INSERT, UPDATE and DELETE statements
        */
        Consola.Mensaje("Entrada en función ejecutar");
        Conectar();
        state.execute(query);
    }
            
    public static ResultSet exQuery(String sql) throws Exception{
        /*
        This method returns the result of a query lauched against the db server
        */
        ResultSet rs=null;
        //conn.close();
        Consola.Mensaje("Dentro de exQuery. Conectando...");
        Conectar();
        Consola.Mensaje("Conectado. Creando Statement");
        state = (Statement) conn.createStatement();
        Consola.Mensaje("Statement creado. Ejecutando consulta...");
        rs = state.executeQuery(sql);
        Consola.Mensaje("Consulta ejecutada. Devolviendo resultado. Fila: " + rs.getRow());
        return rs;
    }
    
    public static int length(ResultSet rs) throws Exception{
        int i = 0;
        while(rs.next())
            i++;
        return i;
    }
    
    public static int exQueryCount(String sql) throws Exception{
        ResultSet rs=null;
        
        Consola.Mensaje("Dentro de exQueryCount. Conectando.");
        Conectar();
        Consola.Mensaje("Creando Statement.");
        state = (Statement) conn.createStatement();
        Consola.Mensaje("Ejecutando query: " + sql);
        try{
            rs=state.executeQuery(sql);
        } catch(SQLException e){
            System.err.println("Error en ejecución de consulta: " + e.getSQLState() + "\n"+e.getMessage());
            Consola.Mensaje(e.getMessage());
        }
        try{
            Consola.Mensaje("Devolviendo resultado: " + rs.getInt(1));        
        } catch(NullPointerException e){
            System.err.println("Error Null Pointer Exception. " + e.getMessage());
        }
        return rs.getRow();
    }
    
    public static void stClose() throws SQLException{
        /*
        This method closes the current connection to the database server
        */
        state.close();
    }
    
}
