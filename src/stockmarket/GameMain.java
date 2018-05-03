package stockmarket;

import UserInterface.Splash;
import UserInterface.SymbolData;
import com.alee.managers.WebLafManagers;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameMain extends Thread {
    public static boolean verbose=true;
    public static boolean logfile=false;
    public static String playerName="";
    private int msAPI = 15*60*1000;
    private int msMin = 1*60*1000;

    public static void main(String[] args) throws Exception{
        WebLafManagers.initialize();
        //cnfParameters(args);
        //PruebasAPI();
        
        //MainWindow principal = new MainWindow();
        //principal.setVisible(true);
        
        //Console.Mensaje("Registro de usuario");
        //Player.Register();            ok
        //Player.ListUsers();           ok
        //Player.LogIn();               ok
        
        //Consola.Mensaje("Recuperacion de información de simbolo en api y actualizacion en BBDD.");
        //Company.getCompanyDB("AAPL");
        //Company.FillDetailsFromAPI("AAPL");   ok
        //Company.getCompanyDB("AAPL");         ok

        Splash splash = new Splash();
        splash.setVisible(true);
        
        //UserInterface.Principal vp = new UserInterface.Principal();
        //vp.setVisible(true);
        
        
        /* Esta parte aún no funciona
        Consola.Mensaje("Pantalla de detalle de empresa con logo");
                String query = "SELECT Symbol, coName, coCEO, coWebsite, coMarket, coSector, coIndustry "
                      +"FROM Company "
                      +"WHERE Symbol = 'AAPL';";
        System.out.println(query);
        ResultSet rs = dbAccess.exQuery(query);
        while (rs.next()){
            System.out.println(rs.getString("Symbol"));
            SymbolData.CompanySymbol.setText(rs.getString("Symbol"));
            SymbolData.CompanyName.setText(String.valueOf(rs.getString("coName")));
            SymbolData.CompanyCEO.setText(rs.getString("coCEO"));
            SymbolData.CompanyWebsite.setText(rs.getString("coWebsite"));
            SymbolData.MarketSectorIndustry.setText(rs.getString("coMarket") + "/" + rs.getString("coSector") + "/" + rs.getString("coIndustry"));
        }
        dbAccess.stClose();
        SymbolData sd = new SymbolData();
        sd.setVisible(true);
        sd.PrepareWindowData("AAPL");
        */
        
    }
        
    
    public static void PruebasAPI(){
        try {
            // pruebas api
            //System.out.println(Company.getCompanyData("AAPL"));
            Company.ShowCompanyData("AAPL");
            Company.ShowCompanyLogo("AAPL");
            Company.ShowCompanyPrice("AAPL");
            Company.ShowCompanyQuote("AAPL");
            // pruebas base de datos
        } catch (Exception ex) {
            Logger.getLogger(GameMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean getVerbose(){
        return verbose;
    }


    private static void cnfParameters(String[] args){
        

        /*
        This method configures the application depending on the input parameters
        */
        verbose = (args[1].equals("-v"));
        logfile = (args[2].equals("-l") && args[1].equals("-v"));
    }
    
}
