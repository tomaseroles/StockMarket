
package stockmarket;

import UserInterface.DialogBox;

/**
 *
 * @author Tomas
 */
public class Consola {
    public static void Mensaje(String mensaje){
        if(GameMain.verbose)
            System.out.println(mensaje);
    }
    
    public static void DialogBox(String mensaje, String titulo){
        DialogBox db = new DialogBox(mensaje,titulo);
        //db.setVisible(true);
    }
    
    public static String int2strTime(int minutos){
        String salida="";
        int horas = minutos/60;
        int dias=0;
        if(horas>24){
            dias=horas/24;
            horas = horas - 24*dias;
        }
        
        salida = "" + dias + "d " + horas +":"+(minutos%60<10?"0":"")+ minutos%60;
        return salida;
    }
}
