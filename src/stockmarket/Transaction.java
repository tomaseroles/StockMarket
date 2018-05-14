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

/**
 *
 * @author Tomas
 */
public class Transaction {
    public static void newTransaction(String symbol, String Player, int equities, int operation){
        String query1, query2;
        double valorOperacion = equities*Double.parseDouble(Company.getCompanyPrice(symbol));
        query1 = "INSERT INTO transaction(Symbol, PlayerName, Equities, syPrice, Multiplier) "
                +"VALUES('" + symbol + "', "
                +       "'" + Player + "', "
                +       ""  + equities + ", "
                +       "'" + Double.parseDouble(Company.getCompanyPrice(symbol)) + "', "
                +       ""  + operation + ");"; 
        query2 = "UPDATE Player "+
                "SET cashMoney = cashMoney - " + valorOperacion + " "+
                    "moneyInvested = moneyInvested + " + valorOperacion + " " +
                "WHERE playerName = '" + Player + "';";
        try{
            dbAccess.ExecuteNQ(query1);
            dbAccess.ExecuteNQ(query2);
        } catch(Exception ex){
            System.out.println("Error en registro de transaccion: " + ex.getMessage() + "\n" + query1 + "\n" + query2);
        }
    }
}
