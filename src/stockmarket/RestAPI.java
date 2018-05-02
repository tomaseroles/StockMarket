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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.*;

public class RestAPI {
    String urlStock="";
        
    //funciones que recuperan informacion de la api
    public Company getCompanyData(String alias){
        Company mycom = new Company();
        String connStr = mycom.urlPrefix + alias + "/company";
        System.out.println(getJSON(connStr));
        return mycom;
    }
    
    public String getJSON(String givenString){
        /*
        This function gets the complete JSON string given by the urlStock string
        */
        String name="";
        try {
            JSONObject obj = new JSONObject();
            name=obj.get(givenString).toString();
            //name=obj.getString("Symbol");
        } catch (JSONException ex) {
            Logger.getLogger(RestAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return name;
    }
    
    public void setURL(String url){urlStock=url;}
    
    public String getURL(){return urlStock;}
    
    public String clienteREST(String cadena) throws Exception {
        URL url = new URL(cadena);
        URLConnection urlConn = url.openConnection();
        InputStreamReader isr = new InputStreamReader(urlConn.getInputStream());
        BufferedReader br=new BufferedReader(isr);
        String txt="", aux="";
        int c;
        while((c=br.read())!=-1){
            aux = ""+(char)c;
            txt=txt+aux;
        }
        br.close();
        isr.close();
                
        return txt;
    }
}
