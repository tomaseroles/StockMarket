package stockmarket;

import UserInterface.Principal;
import UserInterface.Splash;
import com.alee.managers.WebLafManagers;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameMain {
    public static boolean verbose=true;
    public static boolean online=true;
    public static boolean logfile=false;
    public static String playerName="";
    private static int msMin = 10*1000;

    public static class hPrincipal extends Thread{
        Splash splash = new Splash();
        
        @Override
        public void run(){
            splash.setVisible(true);
        }
        
        public static void setOnline(){
            online=true;
        }
    }
    
    public static class hAccesoAPI extends Thread{
        Principal vPrincipal = new Principal();
        @Override
        public void run(){
            System.out.println("Thread API en marcha");
            int ciclos=0;
            System.out.println("Principal visible: " + vPrincipal.isVisible());
            System.out.println("Principal enabled: " + vPrincipal.isEnabled());
            while (true){
                try {
                    System.out.println("Hilo hAccesoAPI dormido");
                    System.out.println("Ciclo " + ciclos);
                    sleep(msMin);
                    System.out.println("Hilo hAccesoAPI despierto");
                    ciclos++;
                    if (ciclos==15){
                        //actualizar precios de acciones para usuario des de la api (cada 15 minutos)
                        Player.UpdateEquities();
                        ciclos=0;
                    }
                    //actualizar ranking (cada minuto)
                    System.out.println("Ciclo " + ciclos);
                    Player.UpdateRanking();
                    Principal.UpdateData();
                } catch (InterruptedException ex) {
                    Logger.getLogger(GameMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }        
    }

    public static void main(String[] args) throws Exception{
        WebLafManagers.initialize();

        Thread principal = new hPrincipal();
        principal.start();
    }
    
    
    public static boolean getVerbose(){
        return verbose;
    }    
}
