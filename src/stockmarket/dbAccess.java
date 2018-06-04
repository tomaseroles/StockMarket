package stockmarket;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 * Esta clase gestiona todo lo relacionado con acceso a base de datos
 * @author Tomas
 */
public class dbAccess {
    
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String localDB_URL = "jdbc:mysql://localhost:3307/stockmarket";
    //static final String localDB_URL = "jdbc:mysql://localhost:3307/stockmarket";
    static final String remoteDB_URL= "jdbc:mysql://estructuradedatos.com:3306/tomas";
    
    static final String USER = "root";
    static final String PASS = "eroles";
    static final String PORT = "3307";
    
    static final String usrRemoto = "tomas";
    static final String pasRemoto = "Coromines/";
    private static Connection conn;
    private static Statement state;
    
    
    /**
    * Obtiene un modelo de datos a partir de una consulta SQL dada, y devuelve un DefaultTableModel
    * El DefaultTableModel es un modelo listo para ser aplicado a cualqiuer JTable
    * @param query es la consulta que se realiza a la base de datos para obtener el modelo
    * @return un objeto DefaultTableModel, el modelo básico de un JTable
    */
    public static DefaultTableModel ObtenerModelo(String query){
        DefaultTableModel modelo=new DefaultTableModel();
        try{
            ResultSet rs = dbAccess.exQuery(query);
            for (int i=1;i<=rs.getMetaData().getColumnCount();i++){
                modelo.addColumn(rs.getMetaData().getColumnName(i));
            }
            while(rs.next()){
                Object[] fila = new Object[rs.getMetaData().getColumnCount()];
                for(int i=0;i<rs.getMetaData().getColumnCount();i++){
                    fila[i]=rs.getObject(i+1);
                }
                modelo.addRow(fila);
            }
        } catch(Exception ex){
            System.out.println("Error al calcular modelo de \n"+query+"\n"+ex.getMessage());
        }
        return modelo;
    }

    /**
     * Realiza una conexión a la Base de Datos a patir de los datos constantes de conexión
     */
    public static void Conectar(){
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
    
    /**
     * Ejecuta una sentencia SQL que no devuelve valores
     * @param query es una sentencia SQL tipo Update, Insert o Delete
     * @throws Exception 
     */
    public static void ExecuteNQ(String query) throws Exception{
        Conectar();
        state.execute(query);
    }
            
    /**
     * Devuelve un ResultSet como consecuencia de una consulta SELECT a una Base de Datos
     * @param sql es una consulta SQL Select válida
     * @return un ResultSet
     * @throws Exception 
     */
    public static ResultSet exQuery(String sql) throws Exception{
        ResultSet rs;
        Conectar();
        state = (Statement) conn.createStatement();
        rs = state.executeQuery(sql);
        return rs;
    }
    
    /**
     * Devuelve el número de registros de un ResultSet
     * @param rs es un ResultSet
     * @return es el número de registros del RS de entrada
     * @throws Exception 
     */
    public static int length(ResultSet rs) throws Exception{
        int i = 0;
        while(rs.next())
            i++;
        return i;
    }
    
    /**
     * Devuelve un valor resultado de una consulta de conteo con solo un campo
     * @param sql es una consulta SQL
     * @return el número resultado de la consulta
     * @throws Exception 
     */
    public static int exQueryCount(String sql) throws Exception{
        ResultSet rs=null;
        
        Conectar();
        state = (Statement) conn.createStatement();
        try{
            rs=state.executeQuery(sql);
            System.out.println(sql);
        } catch(SQLException e){
            System.err.println("Error en ejecución de consulta: " + e.getSQLState() + "\n"+e.getMessage());
        } catch(NullPointerException e){
            System.err.println("Error Null Pointer Exception. " + e.getMessage());
        }
        System.out.println(rs.getRow());
        return rs.getRow();
    }
    
    public static double exQueryDouble(String sql, int column) throws Exception{
        ResultSet rs = exQuery(sql);
        return rs.getDouble(column);
    }

    /**
     * Cierra la conexión con la BBDD
     * @throws SQLException 
     */    
    public static void stClose() throws SQLException{
        state.close();
    }
    
    /**
     * Realiza una consulta SQL, obtiene un ResultSet y lo escribe en la consola
     * Método escrito sólo para propósitos de prueba y comprobación de resultados.
     * @param sql 
     */
    public static void rsConsole(String sql){
        try{
            System.out.println("Resultado para \n" + sql);
            ResultSet rs = exQuery(sql);
            System.out.println();
            for(int i=1;i<rs.getMetaData().getColumnCount();i++){
                System.out.print(rs.getMetaData().getColumnName(i) + "|\t");
            }
            System.out.println();
            int i=1;
            System.out.println("--- Inicio ---");
            while(rs.next()){
                for(i=1;i<rs.getMetaData().getColumnCount();i++){
                    System.out.println(rs.getMetaData().getColumnLabel(i) +"\t"+ rs.getObject(i) + "|\t");
                }
                System.out.println();
            }
            System.out.println("--- Fin ---");
        } catch (Exception ex){
            System.out.println("Error en rsConsole: " + ex.getMessage());
        }
        
    }

    /**
     * Obtiene la suma de un valor de un campo. Con los parámetros dados construye una consulta y revuelve el valor pedido
     * @param fieldName es el campo con valores numéricos a sumar
     * @param tableName es el nombre de la tabla que contiene el campo
     * @param whereCondition es la condición que permite discrimiar registros
     * @return la suma de los elementos dados por la condición de la consulta
     * @throws Exception 
     */
    public static int DSum(String fieldName, String tableName, String whereCondition) throws Exception{
        System.out.println(fieldName);
        System.out.println(tableName);
        System.out.println(whereCondition);
        String query = "SELECT Sum(" + fieldName + ") "+
                "FROM " + tableName + " " +
                "WHERE (" + whereCondition + ");";
        System.out.println(query);
        ResultSet rs = exQuery(query);
        int valor=0;
        while(rs.next()){
            valor=rs.getInt(1);
        }
        
        return valor;
    }
    
    /**
     * Obtiene la cuenta de valores dados por una consulta
     * @param fieldName es el nombre del campo que se usará para contar
     * @param tableName es el nombre de la tabla
     * @param whereCondition es la condición que permitirá obtener el filtro
     * @return
     * @throws Exception 
     */
    public static int DCount(String fieldName, String tableName, String whereCondition) throws Exception{
        String query = "SELECT Count(" + fieldName + ") "+
                "FROM " + tableName + " " + 
                "WHERE (" + whereCondition + ");";
        ResultSet rs = exQuery(query);
        int valor=0;
        while (rs.next())
            valor = rs.getInt(1);
        return valor;
    }
}
