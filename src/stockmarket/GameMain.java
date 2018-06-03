package stockmarket;

import UserInterface.FormularioPrincipal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase principal del programa.
 * El primer método que se ejecuta del programa es el main de esta clase
 * 
 * @author Tomas Eroles
 */
public class GameMain {
    public static boolean online=true;      //identifica si hay usuario logueado y está en línea
    public static String playerName;        //nombre del usuario activo
    private static int msMin = 10000;       //tiempo de sleep del hilo, se establece en 10 segundos. Si t<5 da error
    private static int minEnMarcha=0;       //tiempo de ejecución del programa

    /**
     * Hilo principal, arranca el empezar el programa.
     * Este hilo abre el formulario principal con la configuración a cero, sin datos
     */
    public static class hPrincipal extends Thread{
        
        /**
         * Ejecuta el hilo, comprobando si hay acceso a internet, y si lo hay, abriendo la ventana FormularioPrincipal
         */
        @Override
        public void run(){
            try{
                String url = "http://www.google.es";
                if(ClienteREST.checkURL(url)){
                    FormularioPrincipal fMain=new FormularioPrincipal();
                    fMain.setVisible(true);
                } else{
                    Consola.Error("No es posible conectarse a internet.", "Error de conexión");
                }
            } catch(Exception ex){
                System.out.println("Ha ocurrido un error: \n"+ex.getMessage());
            }
        }
        
        /**
         * Establece el estado del juego a en línea, está en marcha
         */
        public static void setOnline(){
            online=true;
        }
    }
    
    /**
     * Este hilo se emplea para automatizar en segundo plano el recálculo de las jugadas
     * 
     */
    public static class hAccesoAPI extends Thread{
        @Override
        public void run(){
            System.out.println("Thread API en marcha");
            int ciclos=0;
            while (true){
                try {
                    sleep(msMin);       //duerme durante 10 segundos
                    System.out.println("Hilo hAccesoAPI dormido");
                    FormularioPrincipal.UpdateTimerData();                    //actualizacion de la ventana principal
                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(GameMain.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(GameMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }        
    }

    public static void main(String[] args) throws Exception{
        Thread principal = new hPrincipal();
        principal.start();
    }    
}
