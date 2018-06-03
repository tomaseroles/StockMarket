
package stockmarket;

import javax.swing.JOptionPane;

/**
 * Métodos para interacción con el usuario
 * @author Tomas
 */
public class Consola {
            
    /**
     * Muestra una caja de diálogo de Información para dar un mensaje al usuario
     * @param mensaje es el mensaje a mostrar
     * @param titulo es el título de la caja de diálogo
     */
    public static void Info(String mensaje,String titulo){
        JOptionPane.showMessageDialog(null, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * Muestra una caja de diálogo con un mensaje de pregunta al usuario
     * @param mensaje es el mensaje a mostrar
     * @param titulo es el título de la caja de diálogo
     */
    public static void Question(String mensaje, String titulo){
        JOptionPane.showMessageDialog(null,mensaje,titulo,JOptionPane.QUESTION_MESSAGE);
    }
    /**
     * Muestra un mensaje al usuario con una caja de diálogo para mostrar un error
     * @param mensaje es el mensaje a mostrar
     * @param titulo es el título de la caja de diálogo
     */
    public static void Error(String mensaje, String titulo){
        JOptionPane.showMessageDialog(null,mensaje,titulo,JOptionPane.ERROR_MESSAGE);
    }
    /**
     * Muestra una caja de diálogo con un mensaje de advertencia
     * @param mensaje el el mensaje a mostrar
     * @param titulo es el título de la caja de diálogo
     */
    public static void Warning(String mensaje, String titulo){
        JOptionPane.showMessageDialog(null,mensaje,titulo,JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Convierte un entero en minutos en una cadena en formato dd hh:mm (días horas y minutos)
     * Este método se usa principalmente para imprimir el tiempo de juego del usuario en la ventana Principal
     * @param minutos es un entero que almacena los minutos acumulados de juego
     * @return una cadena formateada como xx d hh:mm
     */
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
