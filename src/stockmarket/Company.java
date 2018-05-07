package stockmarket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.tree.DefaultMutableTreeNode;
import org.json.JSONObject;

public class Company {
    static String urlPrefix="https://api.iextrading.com/1.0/stock/";
        
    //operative methods    
    public static String getCompanyData(String symbol){
        return ClienteREST.request(urlPrefix+symbol+"/company");
    }
    
    public static String getCompanyStats(String symbol){
        return ClienteREST.request(urlPrefix+symbol+"/stats");
    }
    
    public static void ShowCompanyStats(String symbol) throws Exception{
        String txt=getCompanyStats(symbol);
        JSONObject obj = new JSONObject(txt);
        
        System.out.println("Simbolo       : " + symbol);
        System.out.println("Capitalizacion: " + obj.getString("marketcap"));
        System.out.println("Acciones      : " + obj.getString("sharesOutstandig"));
    }
    
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
    
    public static void ShowCompanyLogo(String symbol) throws Exception{
        /*
        This method has to be changed.
        When writing this comment, the method gets the url for a logo of the company, and downloads the file
        It has to be changed to only return the url.
        This url will be returned in two ways:
        - in the console context, to write the url
        - in the swing context to watch the logo in a form
        */
        String txt = getCompanyLogo(symbol);
        JSONObject obj = new JSONObject(txt);
        String url = obj.getString("url");
        
        System.out.println("Logo:      " + url);
        System.out.println("Descarga de la imagen:");
        String folder = "descargas/";
        String name = symbol+".jpg";
        
        
        try{
            File dir = new File(folder);
            if(!dir.exists())
                if(!dir.mkdir())
                    return;         //no se pudo crear la carpeta de destino

            File file = new File(folder+name);
            URLConnection conn = new URL(url).openConnection();
            conn.connect();
            System.out.println("Empezando descarga...");
            System.out.println(">>URL:    "+url);
            System.out.println(">>Nombre: "+name);

            InputStream in = conn.getInputStream();
            OutputStream out = new FileOutputStream(file);

            int b=0;
            while(b!=-1){
                b=in.read();
                if(b!=-1)
                    out.write(b);
            }
            out.close();
            in.close();
            System.out.println("Descarga finalizada.");
        } catch (MalformedURLException e){
            System.out.println("la url: " + url + " no es valida!");
        } catch (IOException e){
            e.printStackTrace();
        }
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
    
    public static DefaultMutableTreeNode getTreeFromDB(){
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
            tree.add(new DefaultMutableTreeNode("Not classified"));
        } catch(Exception ex){
            System.out.println("Error en getTreeFromDB. " + ex.getMessage());
        }
        //System.out.println("Ultima hoja: " + tree.getLastLeaf().toString());
        
        //System.out.println("Arbol: " + tree.toString());
        return tree;
    }
    public static void FillDetailsToNull(String symbol) throws Exception{
        /*
        This method sets the details of Market, Sector and Industry to null in the database for a given symbol
        */
        String query = "UPDATE company "
                + "SET coMarket = NULL, "
                + "coSector = NULL, "
                + "coIndustry = NULL, "
                + "coCEO = NULL, "
                + "coWebsite = NULL "
                + "WHERE Symbol = '" + symbol + "'";
        dbAccess.ExecuteNQ(query);
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
        
        String query = "UPDATE company "
                     + "SET coCEO = '"      + obj.getString("CEO") + "', "
                     +     "coWebsite = '"  + obj.getString("website") + "', "
                     +     "coMarket = '"   + obj.getString("exchange") + "', " 
                     +     "coSector = '"   + obj.getString("sector") + "', "
                     +     "coIndustry = '" + obj.getString("industry") + "', "
                     +     "coDescription = '" + obj.getString("description") + "', "
                     +     "Capitalization = " + obj2.getLong("marketcap") + ", "
                     +     "sharesOutstanding = " + obj2.getLong("sharesOutstanding") + ", "
                     +     "coValue = '" + obj2.getLong("marketcap")/obj2.getLong("sharesOutstanding") + "' " 
                     + "WHERE Symbol = '"   + symbol + "';";
        System.out.println(query);
        dbAccess.ExecuteNQ(query);
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
    
//    public static ArrayList getTreeFromDB2(){
//        ArrayList list = new ArrayList();
//        try{
//            String sqlArbolMarket = "SELECT coMarket, coSector, coIndustry "
//                                    +"FROM company "
//                                    +"GROUP BY coMarket, coSector, coIndustry "
//                                    +"HAVING ((coMarket Is Not Null) AND (coSector Is Not Null)) AND (coIndustry Is Not Null);";
//            Consola.Mensaje(sqlArbolMarket);
//            list.add("Markets");
//            ResultSet rsMarket = dbAccess.exQuery(sqlArbolMarket);
//            while (rsMarket.next()){
//                Object value[] = {rsMarket.getString(1),rsMarket.getString(2),rsMarket.getString(3)};
//                list.add(value);
//            }
//            list.add("Not yet classified");
//        } catch(Exception ex){
//            System.err.println(ex.getMessage());
//        }
//        return list;
//    }            
            
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
