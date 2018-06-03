package stockmarket;

import javax.swing.SpinnerNumberModel;

/**
 * Clase dedicada a gestionar todo lo relativo a transacciones
 * @author Tomas
 */
public class Transaction {
    
    /**
     * Crea y devuelve un modelo de tipo SpinnerNumberModel de double a partir de los datos de entrada
     * Se ha incluido en esta clase porque los dos spinners que hay en el programa se dedican a transacciones
     * @param value es al valor que mostrará el spinner por defecto
     * @param min es el valor mínimo del spinner
     * @param max es el valor máximo del spinner
     * @param step es el valor de salto del spinner
     * @return un objeto SpinnerNumberModel listo para aplicarse en cualquier Spinner
     */
    public static SpinnerNumberModel getSpinnerModel(double value, double min, double max, double step){
        return new SpinnerNumberModel(value, min, max, step);
    }
    
    /**
     * Da de alta una nueva transacción en la base de datos, con los parámetros dados.
     * Una transacción consiste en tres pasos:
     * - Añadir un registro en la tabla de transacciones (simbolo, jugador, acciones, precio, mutiplicador)
     * - Actualizar la tabla de jugadores para restar el importe de la transacción
     * - Actualizar la tabla de empresas para restar las acciones compradas por el jugador
     * @param symbol Símbolo que se compra o se vende
     * @param Player Nombre del jugador que realiza la operación
     * @param equities Número de acciones compradas o vendidas
     * @param operation Tipo de operación: -1 compra, 1 venta (multiplicador)
     * @throws Exception 
     */
    public static void newTransaction(String symbol, String Player, double equities, int operation) throws Exception{
        String query1, query2, query3;
        System.out.println("Transaccion");
        double valorOperacion = equities*Company.getCompanyDoublePrice(symbol);
        System.out.println("El valor de la operacion es de: " + valorOperacion);
        query1 = "INSERT INTO transaction(Symbol, PlayerName, Equities, syPrice, Multiplier) "
                +"VALUES('" + symbol + "', "
                +       "'" + Player + "', "
                +       ""  + equities + ", "
                +       "" + Company.getCompanyDoublePrice(symbol) + ", "
                +       ""  + operation + ");"; 
        System.out.println(query1);
        query2 = "UPDATE Player "+
                "SET cashMoney = cashMoney - " + valorOperacion + ", " +
                    "investMoney = investMoney + " + valorOperacion + " " +
                "WHERE playerName = '" + Player + "';";
        System.out.println(query2);
        query3 = "UPDATE Company "+
                "SET sharesOutstanding = sharesOutstanding - " + equities + " "+
                "WHERE Symbol = '" + symbol + "';";
        System.out.println(query3);
        try{
            dbAccess.ExecuteNQ(query1);
            dbAccess.ExecuteNQ(query2);
            dbAccess.ExecuteNQ(query3);
        } catch(Exception ex){
            System.out.println("Error en registro de transaccion: " + ex.getMessage() + "\n" + query1 + "\n" + query2 + "\n" + query3);
        }
    }
}
