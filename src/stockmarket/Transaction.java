/*
 * Copyright (C) 2018 Tomas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package stockmarket;

import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Tomas
 */
public class Transaction {
    public static SpinnerNumberModel getSpinnerModel(double value, double min, double max, double step){
        /*
        getSpinnerModel (value, min, max, step)
        */
        return new SpinnerNumberModel(value, min, max, step);
    }
    
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
                "SET cashMoney = cashMoney - " + valorOperacion + " "+
                "WHERE playerName = '" + Player + "';";
        System.out.println(query2);
        query3 = "UPDATE Company "+
                "SET sharesOutstanding = sharesOutstanding - " + equities + " "+
                "WHERE Symbol = '" + symbol + "';";
        System.out.println(query3);
        // añadir la resta de acciones de la compañia o la suma en caso de venta, para variar el numero de acciones disponibles en mercado
        try{
            dbAccess.ExecuteNQ(query1);
            dbAccess.ExecuteNQ(query2);
            dbAccess.ExecuteNQ(query3);
        } catch(Exception ex){
            System.out.println("Error en registro de transaccion: " + ex.getMessage() + "\n" + query1 + "\n" + query2);
        }
    }
}
