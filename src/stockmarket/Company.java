package stockmarket;

import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Esta clase gestiona los datos de la tabla Company, así como los métodos que trabajan con esos datos
 * @author Tomas Eroles
 */
public class Company {
    /**
     * Es el prefijo por defecto para todos los accesos a la API
     */
    static String urlPrefix="https://api.iextrading.com/1.0/stock/";
        
    /**
     * Obtiene los datos de la empresa en la API
     * @param symbol es el simbolo de la empresa en la API
     * @return un string JSON con los datos de la empresa
     */
    public static String getCompanyData(String symbol){
        return ClienteREST.request(urlPrefix+symbol+"/company");
    }
    
    /**
     * Obtiene estadísticas de la empresa: Capitalización y Número de acciones
     * Estos dos datos se usan para conocer el número de acciones que tiene la empresa
     * Sólo se graban en la tabla de empresa cuando se da de alta para cotización
     * El número de acciones aumenta o disminuye cuando se producen transacciones
     * @param symbol es el símbolo que se desea consultar
     * @return un string JSON con los datos estadísticos de la empresa
     */
    public static String getCompanyStats(String symbol){
        return ClienteREST.request(urlPrefix+symbol+"/stats");
    }
    
    /**
     * Muestra en la consola los datos obtenidos sobre las estadísticas de la empresa
     * @param symbol es el símbolo de la empresa
     * @throws Exception cuando no se puede obtener conexión y/o la información es errónea
     */
    public static void ShowCompanyStats(String symbol) throws Exception{
        String txt=getCompanyStats(symbol);
        JSONObject obj = new JSONObject(txt);
        
        System.out.println("Simbolo       : " + symbol);
        System.out.println("Capitalizacion: " + obj.getString("marketcap"));
        System.out.println("Acciones      : " + obj.getString("sharesOutstandig"));
    }
    
    /**
     * Muestra en la consola los datos de la empresa que vienen de la API:
     * 
     * @param symbol es el símbolo de la empresa
     * @throws Exception Cuando no se puede obtener conexión
     */
    public static void ShowCompanyData(String symbol) throws Exception{
        String txt=getCompanyData(symbol);
        JSONObject obj = new JSONObject(txt);
        
        System.out.println("Simbolo:   " + obj.getString("symbol"));
        System.out.println("Nombre:    " + obj.getString("companyName"));
        System.out.println("Mercado:   " + obj.getString("exchange"));
        System.out.println("Actividad: " + obj.getString("industry"));
        System.out.println("Website:   " + obj.getString("website"));
        System.out.println("CEO:       " + obj.getString("CEO"));
        System.out.println("Sector:    " + obj.getString("sector"));
    }
    
    /**
     * Compone una consulta a la API para obtener el precio de la acción de una compañía dada
     * Tal como viene de la api, se obtiene un solo string con un valor, por lo que no se puede consultar de forma estándar con una llamada JSON
     * Lo que hace este método es, a partir del valor obtenido, componer un String JSON
     * @param symbol es el símbolo que se va a consultar en la API
     * @return un String con la cadena JSON que contiene el precio
     */
    public static String getCompanyPrice(String symbol){
        return "{ \n\"price\": \""+ClienteREST.request(urlPrefix+symbol+"/price")+"\"\n}";
    }
    
    /**
     * Obtiene un valor en formato double a partir de un string JSON que viene de la API y que contiene el precio de la acción
     * @param symbol es el simbolo que se consulta a la API
     * @return el valor en formato double 
     * @throws JSONException cuando la información obtenida es errónea
     */
    public static double getCompanyDoublePrice(String symbol) throws JSONException{
        String txt=getCompanyPrice(symbol);
        JSONObject obj = new JSONObject(txt);
        return obj.getDouble("price");
    }
    
    /**
     * Obtiene de la API y muestra en pantalla el precio de la acción para un simbolo dado
     * @param symbol es el simbolo del cual se obtiene la información
     * @throws Exception Cuando la conexión es errónea
     */
    public static void ShowCompanyPrice(String symbol) throws Exception{
        String txt=getCompanyPrice(symbol);
        JSONObject obj = new JSONObject(txt);
        
        System.out.println("Precio:    " + obj.getString("price"));
    }
    
    /**
     * Obtiene de la API la cotiación de una empresa dada. La API renueva este dato cada 15 minutos en horario laboral USA de la costa Este
     * @param symbol es el símbolo a consultar
     * @return  una cadena JSON con la información de cotización
     */
    public static String getCompanyQuote(String symbol){
        return ClienteREST.request(urlPrefix+symbol+"/quote");
    }
    
    /**
     * Obtiene de la API, e imprime en la consola datos acerca de los precios de apertura, mayor, menor y cierre de una empresa dada
     * @param symbol es el símbolo que se consulta a la API
     * @throws Exception Cuando la conexión es errónea
     */
    public static void ShowCompanyQuote(String symbol) throws Exception{
        String txt = getCompanyQuote(symbol);
        JSONObject obj = new JSONObject(txt);
        
        double open = obj.getDouble("open");
        System.out.println("Open:      " + open);
        double close = obj.getDouble("close");
        System.out.println("Close:     " + close);
        double high = obj.getDouble("high");
        System.out.println("High:      " + high);
        double low = obj.getDouble("low");
        System.out.println("Low:       " + low);
        double change = obj.getDouble("change");
        System.out.println("Change:    " + change);
    }
    
    /**
     * Obtiene el JSON correspondiente a la llamada a API para obtener la URL de un logotipo de una empresa
     * @param symbol es el símbolo del cual se quiere obtener la información
     * @return una cadena que es el JSON del logo
     */
    public static String getCompanyLogo(String symbol){
        return ClienteREST.request(urlPrefix+symbol+"/logo");
    }
    
    /**
     * Obtiene, mediante llamada a la API, la URL de un logotipo dado por un simbolo, que será en que se muestre en la pantalla Principal
     * @param symbol es el símbolo a consultar
     * @return la URL completa del logotipo
     * @throws Exception Cuando la conexión es errónea
     */
    public static String getRawCompanyLogo(String symbol)throws Exception{
        String txt = getCompanyLogo(symbol);
        JSONObject obj = new JSONObject(txt);
        String url = obj.getString("url");
        
        return url;
    }
    
    /**
     * Obtiene un vector de String con el camino elegido en el árbol de empresas
     * Cuando se hace clic en un elemento del árbol de empresas, éste devuelve un nodo como resultado, que es una cadena con el formato:
     * [nivel1, nivel2, nivel3]. Esta función descompone la cadena de entrada en tres cadenas en un vector
     * 
     * @param node es el nodo que se ha clicado en el árbol y que se tiene que convertir
     * @return un vector de String con los nodos separados
     */
    public static String[] getCompletePath(String node){
        String[] camino = new String[4];
        int posicion=0;
        for (int i=1;i<node.length()-1;i++){
            if(node.charAt(i)==','){
                posicion++;
                i++;
            }else{
                camino[posicion]=camino[posicion]+node.charAt(i);
            }
        }
        //System.out.println("Vector:");
        for(int i=0;i<camino.length;i++){
            camino[i]=camino[i].trim();
            if(camino[i].startsWith("null")){
                camino[i]=camino[i].substring(4);
            }
            //System.out.println("i:"+i+": " + camino[posicion]);
        }
        return camino;
    }
    
    /**
     * Prepara la estructura del árbol de empresas en un modelo DefaultMutableTreeNode, que devuelve para mostrarlo en pantalla.
     * El modelo se obtiene leyendo en la tabla de empresas los mercados, para cada mercado, los sectores, y para cada sector, las industrias.
     * El árbol contiene una entrada de empresas no clasificadas si el usuario es el administrador, que es quien las puede habilitar/suspender
     * @param username el nombre del usuario para el que se va a preparar el árbol
     * @return un DefaultMutableTreeNode (modelo de árbol aplicable a un JTree)
     */
    public static DefaultMutableTreeNode getTreeFromDB(String username){
        DefaultMutableTreeNode tree=new DefaultMutableTreeNode("Markets");
        try{
            String sqlMarkets = "SELECT coMarket "
                    +"FROM company "
                    +"GROUP BY coMarket "
                    +"HAVING coMarket Is Not NULL;";
            ResultSet markets = dbAccess.exQuery(sqlMarkets);
            while(markets.next()){
                DefaultMutableTreeNode market;
                market = new DefaultMutableTreeNode(markets.getString("coMarket"));
                tree.add(market);
                String sqlSector = "SELECT coSector "
                        +"FROM company "
                        +"GROUP BY coMarket, coSector "
                        +"HAVING coMarket = '" + markets.getString("coMarket") + "';";
                ResultSet sectors = dbAccess.exQuery(sqlSector);
                while(sectors.next()){
                    DefaultMutableTreeNode sector;
                    sector = new DefaultMutableTreeNode(sectors.getString("coSector"));
                    market.add(sector);
                    String sqlIndustry = "SELECT coIndustry "
                            +"FROM company "
                            +"GROUP BY coMarket, coSector, coIndustry "
                            +"HAVING coMarket = '" + markets.getString("coMarket") + "' AND "
                            +"coSector = '" + sectors.getString("coSector") + "';";
                    ResultSet industries = dbAccess.exQuery(sqlIndustry);
                    while(industries.next()){
                        DefaultMutableTreeNode industry;
                        industry = new DefaultMutableTreeNode(industries.getString("coIndustry"));
                        sector.add(industry);
                    }
                }
            }
            if(username.equals("admin"))
                tree.add(new DefaultMutableTreeNode("Not classified"));
        } catch(Exception ex){
            System.out.println("Error en getTreeFromDB. " + ex.getMessage());
        }
        return tree;
    }
    
    /**
     * Suspende la cotización de una empresa poniendo a cero el campo Cotiza.
     * Si una empresa no cotiza, no la pueden ver los jugadores en el árbol
     * @param symbol es el símbolo de la empresa que se va a suspender de cotización
     * @throws Exception Cuando la consulta de actualización tiene un error
     */
    public static void FillDetailsToNull(String symbol) throws Exception{
        String query = "UPDATE company "
                + "SET Cotiza=0 "
                + "WHERE Symbol = '" + symbol + "'";
        dbAccess.ExecuteNQ(query);
        Consola.Info("Se ha suspendido la cotización de la empresa " + symbol, "Suspender cotización");
    }
    
    /**
     * Habilita para cotización a una empresa en la tabla de empresas.
     * La habilitacion consiste en recuperar los detalles de una empresa mediante llamadas a la API y almacenarlos en la tabla de empresas
     * La información que se almacena en la tabla Company de la BBDD se obtiene mediante tres consultas distintas a la API:
     * - una que obtiene el CEO, la URL, el Mercado, Sector e Industria y la descripción
     * - una que recupera la capitalización (capital de la empresa), y el número de acciones (SharesOutstandings)
     * - una que obtiene el precio de la acción
     * @param symbol es el símbolo que se tiene que recuperar y almacenar.
     * @throws Exception cuando ocurre un error en las operaciones de la BBDD
     */
    public static void FillDetailsFromAPI(String symbol) throws Exception{
        String txt = getCompanyData(symbol);        JSONObject obj = new JSONObject(txt);
        String txt2 = getCompanyStats(symbol);      JSONObject obj2 = new JSONObject(txt2);
        String txt3 = getCompanyPrice(symbol);      JSONObject obj3 = new JSONObject(txt3);
        
        String descripcion = obj.getString("description");
        descripcion = descripcion.replaceAll("\'", "");
        String query;
        if(dbAccess.DCount("*", "company", "Cotiza=0 AND coCEO Is Null AND coWebsite Is Null AND coMarket Is Null and coSector Is Null AND coIndustry Is Null")==1){
            query = "UPDATE company "
                         + "SET coCEO = '"      + obj.getString("CEO") + "', "
                         +     "coWebsite = '"  + obj.getString("website") + "', "
                         +     "coMarket = '"   + obj.getString("exchange") + "', " 
                         +     "coSector = '"   + obj.getString("sector") + "', "
                         +     "coIndustry = '" + obj.getString("industry") + "', "
                         +     "coDescription = '" + descripcion + "', "
                         +     "Capitalization = " + obj2.getLong("marketcap") + ", "
                         +     "sharesOutstanding = " + obj2.getLong("sharesOutstanding") + ", "
                         +     "coValue = '" + obj3.getDouble("price") + "' " 
                         +     "Cotiza = 1, "
                         + "WHERE Symbol = '"   + symbol + "';";
            
        } else{
            query = "UPDATE company "+
                    "SET Cotiza = 1 WHERE Symbol = '" + symbol + "';";
        }
        System.out.println("Consulta de actualización de tabla Company:\n"+query);
        dbAccess.ExecuteNQ(query);
        Consola.Info("Se ha activado la cotización de " + symbol, "Activar cotización");
    }
    
    /**
     * Recupera e imprime en consola los datos de una empresa de la tabla de empresas
     * @param symbol es el símbolo del que se quieere imprimr la información
     * @throws Exception cuando ocurre un error en las consultas a la BBDD
     */
    public static void getCompanyDB(String symbol) throws Exception{
        String query = "SELECT coName, coCEO, coWebsite, coMarket, coSector, coIndustry "
                      +"FROM Company "
                      +"WHERE Symbol = '" + symbol + "';";
        
        ResultSet rs = dbAccess.exQuery(query);
        while (rs.next()){
            System.out.println("Symbol   : " + symbol);
            System.out.println("Company  : " + rs.getString("coName"));
            System.out.println("CEO      : " + rs.getString("coCEO"));
            System.out.println("Website  : " + rs.getString("coWebsite"));
            System.out.println("Market   : " + rs.getString("coMarket"));
            System.out.println("Sector   : " + rs.getString("coSector"));
            System.out.println("Industry : " + rs.getString("coIndustry"));
            System.out.println();
        }
    }
    
    /**
     * Verifica si un símbolo existe en la tabla de empresas, es decir, si hay alguna empresa almacenada con un símbolo dado
     * @param symbol es el símbolo a comprobar
     * @return verdadero si el simbolo ya existe en la tabla o falso si no existe
     * @throws Exception 
     */
    private static boolean SymbolExists(String symbol) throws Exception{
        String query = "SELECT COUNT(*) AS Contador FROM company WHERE Symbol = '" + symbol + "';";
        ResultSet rs = dbAccess.exQuery(query);
        return (rs.getInt("Contador")==1);
    }
    
    //---------------------------------------------------------------
    //- parece que los siguientes metodos sobran y pueden eliminarse
    //---------------------------------------------------------------
    
    /**
     * Obtiene un ArrayList con una lista de sectores de la BD a partir de un mercado
     * @param market es el mercado dado
     * @return ArrayList de sectores
     */
    public static ArrayList getTreeSectors(String market){
        ArrayList list = new ArrayList();
        try{
            String sqlSectors = "SELECT coSector FROM company GROUP BY coSector, coMarket HAVING coMarket = '" + market + "';";
            ResultSet sectors = dbAccess.exQuery(sqlSectors);
            while(sectors.next()){
                Object value[] = {sectors.getString(1)};
                list.add(value);
            }
        } catch (Exception ex){
            System.err.println("Error en select de Sectors para " + market + ".\n" + ex.getMessage());
        }
        return list;
    }
    
    /**
     * Obtiene una lista de mercados de las empresas almacenadas en la Base de Datos y la almacena en un vector de Strings
     * @return un vector de String con la lista de los Mercados que hay en la BBDD
     * @throws Exception cuando hay un error en la obtención de la infromación
     */
    public static String[] getMarketsFromDB() throws Exception{
        String[] markets={};
        try{
            String sqlMercados = "SELECT coMarket AS Mercado FROM Company GROUP BY coMarket;";       // HAVING coMarket Is Not Null;";
            System.out.println("SQL: " + sqlMercados + "\nObteniendo lista de mercados.");
            ResultSet rsMarket = dbAccess.exQuery(sqlMercados);
            System.out.println("Imprimiendo lista de mercados:");
            printMercados(rsMarket);
            
            int size = 5;    //dbAccess.length(rsMarket);
            
            System.out.println("Es ultimo registro? " + rsMarket.last());
            System.out.println("Se han encontrado " + size + " mercados distintos. Creando vector de mercados.");
            markets = new String[size];
            int i=0;
            rsMarket.first();
            System.out.println("Es ultimo registro? " + rsMarket.last());
            
            while(rsMarket.next()){
                System.out.println("\tAñadiendo elemento " + i + ": " + rsMarket.getString(rsMarket.findColumn("Mercado")));
                markets[i]= rsMarket.getString(rsMarket.findColumn("Mercado"));
                System.out.println(markets[i]);
                i++;
            }
            
            printMercados(markets);
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
        return markets;
    }
    
    /**
     * Este método imprime una lista de mercados procedentes de una consulta a la base de datos
     * @param rs es el ResultSet de los mercados obtenido de la base de datos
     * @throws Exception 
     */
    private static void printMercados(ResultSet rs) throws Exception{
        while(rs.next()){
            System.out.print("\nFila:");
            for(int i=0;i<rs.getMetaData().getColumnCount();i++){
                System.out.print("\t"+rs.getString(i));
            }
            
        }
    }
    
    /**
     * Imprime por consola la lista de mercados de un vector de String de los mercados
     * Este método se usa para propósitos de depuración
     * @param m es el array de mercados dado
     */
    private static void printMercados(String[] m){
        System.out.println("Imprimiendo lista de mercados");
        for(int i=0;i<m.length;i++){
            System.out.println(""+i+"\t"+m[i]);
        }
    }
}
