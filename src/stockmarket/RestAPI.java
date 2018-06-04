package stockmarket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.*;

/**
 * Esta es la clase que se encarga de comunicarse con la librería Java-JSON
 * @author Tomas
 */
public class RestAPI {
    private static String urlStock="";
        
    /**
     * Obtiene el JSON con los datos de la entrada company de la API
     * @deprecated 
     * @param alias es el símbolo de la compañía
     * @return un string JSON con el resultado de la consulta a la API
     */
    public static Company aaagetCompanyData(String alias){
        Company mycom = new Company();
        String connStr = mycom.urlPrefix + alias + "/company";
        System.out.println(getJSON(connStr));
        return mycom;
    }
    
    /**
     * Obtiene un string JSON dado por un parámetro de entrada
     * @param givenString es la consulta a realiar a la API
     * @return el string json obtenido
     */
    public static String getJSON(String givenString){
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
    
    public static void setURL(String url){
        urlStock=url;
    }
    
    public static String getURL(){
        return urlStock;
    }
    
    /**
     * Compone una cadena a partir de una consulta a la API
     * @param cadena es la cadena a consultar a la API
     * @return una cadena resultado de consultar a la API
     * @throws Exception cuando hay un error en la consulta a la API
     */
    public static String clienteREST(String cadena) throws Exception {
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
