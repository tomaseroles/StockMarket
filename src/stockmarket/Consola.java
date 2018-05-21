
package stockmarket;

import javax.swing.JOptionPane;

/**
 *
 * @author Tomas
 */
public class Consola {
    int Info     = JOptionPane.INFORMATION_MESSAGE;
    int Question = JOptionPane.QUESTION_MESSAGE;
    int Error    = JOptionPane.ERROR_MESSAGE;
    int Warning  = JOptionPane.WARNING_MESSAGE;
    
    public static void Mensaje(String mensaje){
        if(GameMain.verbose)
            System.out.println(mensaje);
    }
        
    public static void Info(String mensaje,String titulo){
        JOptionPane.showMessageDialog(null, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
    public static void Question(String mensaje, String titulo){
        JOptionPane.showMessageDialog(null,mensaje,titulo,JOptionPane.QUESTION_MESSAGE);
    }
    public static void Error(String mensaje, String titulo){
        JOptionPane.showMessageDialog(null,mensaje,titulo,JOptionPane.ERROR_MESSAGE);
    }
    public static void Warning(String mensaje, String titulo){
        JOptionPane.showMessageDialog(null,mensaje,titulo,JOptionPane.WARNING_MESSAGE);
    }

    
    public static String int2strTime(int minutos){
        String salida;
        int horas = minutos/60;
        int dias=0;
        if(horas>24){
            dias=horas/24;
            horas = horas - 24*dias;
        }
        return  "" + dias + "d " + horas +":"+(minutos%60<10?"0":"")+ minutos%60;
    }
}
