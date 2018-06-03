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
 * Esta clase se ocupa de la gestión de las empresas que vienen de la API
 * @author Tomas
 */
public class Companies {
    public Companies(){
        
    }
    
    public String List(){
        dbAccess dba = new dbAccess();
        //dba.setQuery("SELECT symbol, name FROM Company");
        return "";
    }
    
    public String List(String Inicial){
        dbAccess dba = new dbAccess();
        //dba.setQuery("SELECT symbol, name FROM Company WHERE Left(symbol,1)='" + Inicial + "';");
        return "";
    }
}
