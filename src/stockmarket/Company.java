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
     * @param symbol
     * @return un string JSON con los datos estadísticos de la empresa
     */
    public static String getCompanyStats(String symbol){
        return ClienteREST.request(urlPrefix+symbol+"/stats");
    }
    
    /**
     * Muestra en la consola los datos obtenidos sobre las estadísticas de la empresa
     * @param symbol es el símbolo de la empresa
     * @throws Exception 
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
     * @throws Exception 
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
    
    public static String getCompanyPrice(String symbol){
        return "{ \n\"price\": \""+ClienteREST.request(urlPrefix+symbol+"/price")+"\"\n}";
    }
        
    public static double getCompanyDoublePrice(String symbol) throws JSONException{
        String txt=getCompanyPrice(symbol);
        JSONObject obj = new JSONObject(txt);
        return obj.getDouble("price");
    }
    public static void ShowCompanyPrice(String symbol) throws Exception{
        String txt=getCompanyPrice(symbol);
        JSONObject obj = new JSONObject(txt);
        
        System.out.println("Precio:    " + obj.getString("price"));
    }
    
    public static String getCompanyQuote(String symbol){
        return ClienteREST.request(urlPrefix+symbol+"/quote");
    }
    
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
    
    public static String getCompanyLogo(String symbol){
        return ClienteREST.request(urlPrefix+symbol+"/logo");
    }
    
    public static String getRawCompanyLogo(String symbol)throws Exception{
        String txt = getCompanyLogo(symbol);
        JSONObject obj = new JSONObject(txt);
        String url = obj.getString("url");
        
        return url;
    }
    
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
    
    public static DefaultMutableTreeNode getTreeFromDB(String username){
        /*
        Crea un objeto DefaultMutableTreeNode leyendo la base de datos
        El objeto se devuelve a la clase Principal para que lo plasme en el arbol
        */
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
        //System.out.println("Ultima hoja: " + tree.getLastLeaf().toString());
        
        //System.out.println("Arbol: " + tree.toString());
        return tree;
    }
    
    /**
     * Suspende la cotización de una empresa poniendo a cero el campo Cotiza
     * @param symbol es el símbolo de la empresa
     * @throws Exception 
     */
    public static void FillDetailsToNull(String symbol) throws Exception{
        String query = "UPDATE company "
                + "SET Cotiza=0 "
                + "WHERE Symbol = '" + symbol + "'";
        dbAccess.ExecuteNQ(query);
        Consola.Info("Se ha suspendido la cotización de la empresa " + symbol, "Suspender cotización");
    }
    
    public static void FillDetailsFromAPI(String symbol) throws Exception{
        /*
        This method gets the information of a symbol from de API and updates them in the database
        Parameters:
        - symbol: the symbol of the firm
        */
        String txt = getCompanyData(symbol);
        JSONObject obj = new JSONObject(txt);
        
        String txt2 = getCompanyStats(symbol);
        JSONObject obj2 = new JSONObject(txt2);
        
        String txt3 = getCompanyPrice(symbol);
        JSONObject obj3 = new JSONObject(txt3);
        
        String descripcion = obj.getString("description");
        System.out.println(descripcion);
        if (descripcion.contains("\'")){
            descripcion = descripcion.replaceAll("\'", "");
        }
        System.out.println(descripcion);
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
        System.out.println(query);
        dbAccess.ExecuteNQ(query);
        Consola.Info("Se ha activado la cotización de " + symbol, "Activar cotización");
    }
    
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
    
    private static boolean SymbolExists(String symbol) throws Exception{
        String query = "SELECT COUNT(*) AS Contador FROM company WHERE Symbol = '" + symbol + "';";
        ResultSet rs = dbAccess.exQuery(query);
        return (rs.getInt("Contador")==1);
    }
                
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
    
    private static void printMercados(ResultSet rs) throws Exception{
        while(rs.next()){
            System.out.print("\nFila:");
            for(int i=0;i<rs.getMetaData().getColumnCount();i++){
                System.out.print("\t"+rs.getString(i));
            }
            
        }
    }
    private static void printMercados(String[] m){
        System.out.println("Imprimiendo lista de mercados");
        for(int i=0;i<m.length;i++){
            System.out.println(""+i+"\t"+m[i]);
        }
    }
}
