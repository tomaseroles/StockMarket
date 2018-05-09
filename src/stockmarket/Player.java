package stockmarket;

import UserInterface.Principal;
import java.sql.ResultSet;
import java.util.Scanner;

public class Player {
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

    public static void Register(String email, String name, String password) throws Exception{
        /*
        This method creates a new user using verified data such a non repeated username and a valid email.
        This method may be called from the GUI or from the console method.
        After receiveing the verified parameters, the method creates a sql sentence and executes it by calling
        the ExecuteNQ method from the dbAccess class
        
        Parameters:
        email: a valid email (String)
        name:  a non duplicated username (String)
        password: a password
        */
        
        String sqlAction;

        sqlAction = "INSERT INTO player (playerName, plEmail, plPassword) ";
        sqlAction+= "VALUES ('" + name + "', '" + email + "', md5('" + password + "'));";
        
        Consola.Mensaje(sqlAction);
        dbAccess.ExecuteNQ(sqlAction);
    }
    
    public static void Register() throws Exception{
        /*
        This method uses the console to create a new user.
        The method reads the username, email and password, checks if the username and the password are repeateds
        If both 3 inputs are correct, executes the Overrided method Register to create the new user.
        This method has no input parameters.
        */
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Email: ");
        String email = sc.nextLine();
        while(!isEmail(email)){
            System.out.print("The provided email is not correct. Enter a valid email to continue:");
            email = sc.nextLine();
        }
        while(emailDuplicated(email)){
            System.out.print("This email already exists. Enter a diffent email:");
            email=sc.nextLine();
        }
        System.out.print("Nombre de usuario: ");
        String nombre = sc.nextLine();
        while(usrNameDuplicate(nombre)){
            System.out.print("Username duplicated. Choose another name: ");
            nombre = sc.nextLine();
        }
        System.out.print("Contraseña: ");
        String passw = sc.nextLine();
        
        Register(email,nombre,passw);
    }
    
    public static void LogIn() throws Exception{
        /*
        This method asks for user data (username and password) to enter a valid session
        */
        String username;
        String password;
        Scanner sc = new Scanner(System.in);

        
        System.out.print("Nombre de usuario: ");
        username = sc.nextLine();
        System.out.print("Enter your password: ");
        password = sc.nextLine();
        
        if (LogIn(username,password)){
            Consola.Mensaje("Usuario validado. Sesion iniciada.");
            GameMain.playerName=username;
        } else {
            Consola.Mensaje("Usuario no existe.");
        }
    }
    
    public static boolean LogIn(String username, String password) throws Exception{
        /*
        Este metodo valida si el usuario/contraseña existen en la BBDD.
        Parametros:
        - username (String). Nombre de usuario dado
        - password (String). Contraseña introducida
        Devuelve
        - boolean: verdadero si es correcto, falso si no es correcto
        */
        if(username.equals("guest")){
            return true;
        } else{
            System.out.println("La funcion Player.Login recibe params: " + username + "/" + password);
            String query = "SELECT playerName " +
                           "FROM player " +
                           "WHERE ((playerName = '" + username + "') AND (plPassword = md5('" + password + "')));";
            System.out.println(query);
            System.out.println("Resultado: " + dbAccess.exQuery(query).getString(1));
            return (dbAccess.exQuery(query).getRow()==1);
        }
    }
        
    public static boolean isEmail(String email){
        return (email.contains("@"));
    }
    
    public static boolean usrNameDuplicate(String uname) throws Exception{
        String query = "SELECT COUNT(*) AS Contador FROM player WHERE playerName = '" + uname + "';";
        boolean salida=true;
        Consola.Mensaje(query);
        ResultSet rs = dbAccess.exQuery(query);
        while(rs.next()){
            salida = (rs.getInt("Contador")!=0);
        }
        return salida;
    }
    
    public static boolean emailDuplicated(String email) throws Exception{
        String query = "SELECT COUNT(*) AS Contador FROM player WHERE plEmail = '" + email + "';";
        boolean salida=true;
        Consola.Mensaje(query);
        ResultSet rs = dbAccess.exQuery(query);
        while(rs.next()){
            salida = (rs.getInt("Contador")!=0);
        }
        return salida;
    }
    
    public static void ListUsers() throws Exception{
        /*
        This methods list the users in the console
        */
        String query = "SELECT playerName, plEmail, plPassword, cashMoney, moneyInvested FROM player ";
        ResultSet rs = dbAccess.exQuery(query);
        while(rs.next()){
            Consola.Mensaje("Plajer name: " + rs.getString("playerName"));
            Consola.Mensaje("Player email:" + rs.getString("plEmail"));
            Consola.Mensaje("Password:    " + rs.getString("plPassword"));
            Consola.Mensaje("");
        }
    }
}
