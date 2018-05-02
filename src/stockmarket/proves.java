/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockmarket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import org.json.*;

/**
 *
 * @author Tomas
 */
public class proves {    
    public void prova2() throws Exception{
        int i=0;
        int opcion;
        Scanner sc=new Scanner(System.in);
        //String connexio="https://api.iextrading.com/1.0/ref-data/symbols";
        String connexio="https://api.iextrading.com/1.0/stock/crusc/company";
//        String txt = RestAPI.clienteREST(connexio);
                
        JSONObject obj=new JSONObject("");
        String symbol = obj.getString("symbol");
        String compan = obj.getString("companyName");
        String market = obj.getString("exchange");
        String indust = obj.getString("industry");
        String websit = obj.getString("website");
        String descrp = obj.getString("description");
        String ceonam = obj.getString("CEO");
        String issuet = obj.getString("issueType");
        String sector = obj.getString("sector");
        
        System.out.println("Symbol: " + symbol);
        System.out.println("Company: " + compan);
        System.out.println("Market: " + market);
        System.out.println("Industry: " + indust);
        System.out.println("Website: " + websit);
        System.out.println("Description: " + descrp);
        System.out.println("CEO: " + ceonam);
        System.out.println("Issue: " + issuet);
        System.out.println("Sector: " + sector);
    }
}
