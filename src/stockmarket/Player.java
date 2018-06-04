package stockmarket;

import java.sql.ResultSet;

/**
 * Esta clase incluye todos los métodos referentes al jugador
 * @author Tomas
 */
public class Player {
    /**
     * Calculo del valor total del capital del jugador (cash + invertido)
     * Si el usuario es admin o guest, se calcula el capital del total de jugadores
     * @param jugador es el nombre del jugador
     * @return el capital total del jugador
     * @throws Exception 
     */
    public static double CalcularValorTotal(String jugador) throws Exception{
        double valor;
        String filtro;
        if(jugador.equals("admin") || jugador.equals("guest"))
            filtro="";
        else
            filtro = "playerName = '"+jugador+"'";
        valor = dbAccess.DSum("cashMoney+investMoney", "Player", filtro);
        return valor;
    }
    
    /**
     * Obtiene una lista de acciones del jugador dado, para cada una de ellas obtiene el nuevo valor del precio por acción en la API y actualiza la BBDD.
     * Esta acción se ejecuta mediante hilos, y tiene lugar una vez cada 15 minutos mientras el usuario está activo
     * @param jugador  es el jugador del cual se va a hacer la comprobación
     */
    public static void UpdateEquities(String jugador){
        System.out.println("Recuperar nuevos valores de las acciones del jugador desde la API...");
        String query = "SELECT Symbol, Sum(equities*multiplier) AS Neto "+
                       "FROM transaction " +
                       "GROUP BY Symbol " +
                       "HAVING Neto>0 AND PlayerName ='" + jugador +"';" ;
        
        try{
            ResultSet rs = dbAccess.exQuery(query);
            while(rs.next()){
                double valorAct=Double.parseDouble(Company.getCompanyPrice(rs.getString("Symbol")));
                String qUP = "UPDATE company "+
                             "SET coValue = '" + valorAct + "' "+
                             "WHERE Symbol = " + rs.getString("Symbol");
                dbAccess.ExecuteNQ(qUP);
            }
            
        } catch(Exception ex){
            System.out.println("Fallo la consulta. "+ex.getMessage());
        }
    }

    /**
     * Este método comprueba si existe un usuario dado por un nombre.
     * Si no existe, da de alta un nuevo jugador en la base de datos y comprueba si se ha dado de alta correctamente
     * Puede ser llamado desde la interfaz gráfica o desde consola
     * 
     * @param email: es una dirección de correo electrónico
     * @param name:  un nombre de usuario
     * @param password: la contraseña
     * @return un valor booleano indicando si la operación ha tenido éxito
    */
    public static boolean Register(String email, String name, String password) throws Exception{
        boolean salida=false;
        if(dbAccess.DCount("playerName","player","playerName = '" + name + "'")==0){
            String sqlAction;

            sqlAction = "INSERT INTO player (playerName, plEmail, plPassword, FechaAlta) ";
            sqlAction+= "VALUES ('" + name + "', '" + email + "', md5('" + password + "'), curdate());";

            System.out.println(sqlAction);
            dbAccess.ExecuteNQ(sqlAction);
            if(dbAccess.DCount("playerName", "player", "playerName = '" + name + "'")==1){
                System.out.println("Usuario " + name + " dado de alta.");
                salida=true;
            } else {
                System.out.println("No se ha podido dar de alta al usuario " + name);
            }
        } else {
            Consola.Warning("El jugador especificado ya existe.", "Nuevo jugador");
            salida=false;
        }
        return salida;
    }    
    
    /**
     * Valida la entrada de un usuario comprobando si existe su nombre y contraseña en la BBDD
     * @param username es el nombre de usuario
     * @param password es la contraseña
     * @return verdadero si existe la combinación, y falso en caso contrario
     * @throws Exception 
     */
    public static boolean LogIn(String username, String password) throws Exception{
        boolean salida=false;
        if(username.equals("guest") || (username.equals("admin") && (password.equals("admin")))){
            salida= true;
        } else{
            try{
                if(dbAccess.DCount("playerName", "Player", " playerName = '" + username + "' AND plPassword = md5('" + password + "')")==1){
                    salida=true;
                } else{
                    salida=false;
                }
            } catch(Exception ex){
                System.err.println("Error en autenticación de usuario: " + ex.toString());
            }
        }
        return salida;
    }
        
    /**
     * Devuelve verdadero si es un correo electrónico(si contiene el carácter '@')
     * @param email es el string a comprobar
     * @return 
     */
    public static boolean isEmail(String email){
        return (email.contains("@"));
    }
    
    /**
     * Devuelve verdadero si el nombre de usuario está duplicado en la base de datos
     * @param uname nombre de usuario a comprobar
     * @return devuelve verdadero si está repetido
     * @throws Exception 
     */
    public static boolean usrNameDuplicate(String uname) throws Exception{
        String query = "SELECT COUNT(*) AS Contador FROM player WHERE playerName = '" + uname + "';";
        boolean salida=true;
        System.out.println(query);
        ResultSet rs = dbAccess.exQuery(query);
        while(rs.next()){
            salida = (rs.getInt("Contador")!=0);
        }
        return salida;
    }
    
    /**
     * Devuelve verdadero si el correo electrónico dado está repetido en la tabla de usuarios
     * @param email correo electrónico a comprobar
     * @return devuelve verdadero si está repetido
     * @throws Exception 
     */
    public static boolean emailDuplicated(String email) throws Exception{
        String query = "SELECT COUNT(*) AS Contador FROM player WHERE plEmail = '" + email + "';";
        boolean salida=true;
        System.out.println(query);
        ResultSet rs = dbAccess.exQuery(query);
        while(rs.next()){
            salida = (rs.getInt("Contador")!=0);
        }
        return salida;
    }
        
    /**
     * Suma minutos a la cuenta del jugador en la base de datos
     * @param jugador es el jugador que va a acumular los minutos
     * @param cantidad es la cantidad de minutos que hay que añadir (por defecto 1, pero se pone con variable por si se quiere cambiar más adelante)
     * @throws Exception 
     */
    public static void AumentaMinutos(String jugador, int cantidad) throws Exception{
        String query = "UPDATE Player SET TiempoJuego = TiempoJuego + " + cantidad + " WHERE PlayerName = '" + jugador + "';";
        dbAccess.ExecuteNQ(query);
    }
}
