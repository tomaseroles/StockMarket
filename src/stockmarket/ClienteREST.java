package stockmarket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Esta clase se ocupa de la comunicación con la API de iextrading.com
 * @author Tomas
 */
public class ClienteREST {
    /**
     * Verifica si es posible conectarse a una URL dada antes de efectuar la conexión y obtener una excepción
     * @param direccion es la URL que se quiere probar
     * @return cierto si es posible, falso si la URL no responde o no hay conexión de red
     * @throws Exception 
     */
    public static boolean checkURL(String direccion) throws Exception{
        try{
            URL url = new URL(direccion);
            HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
            urlconn.connect();
            return true;
        } catch(IOException e){
            Consola.Error("No se ha podido establecer la conexión a " + direccion + "\nReintentelo más tarde.", "Error de conexión");
            return false;
        }
    }
    
    /**
     * Realiza una petición a la API y obtiene un string JSON de datos que devuelve al método que lo llama
     * @param dirURL Es la URL a la que se tiene que hacer la petición
     * @return un String JSON con el resultado de la llamada
     */
    public static String request(String dirURL) {

		try {
			// Creamos una URL y una conexión a URL
			URL url = new URL(dirURL);
			URLConnection urlConn = url.openConnection();
			// Creamos un InputStreamReader & BufferedReader para leer la respuesta
			InputStreamReader isr = new InputStreamReader(urlConn.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			// Mientras el buffer no esté vacio, leemos chars y almacenamos en String
			String txt = "";
			int c;
			while ((c = br.read()) != -1) {
				txt = txt + (char) c;
			}

			// Cerramos el BufferedReader y el InputStreamReader
			br.close();
			isr.close();
			// Devolvemos la respuesta
			return txt;
			
		} catch (Exception e) {
			// Mostramos información de la excepción
			System.out.println("Se ha producido una excepción inesperada:");
			e.printStackTrace();
			// Devolvemos null
			return null;
		}
	}
}
