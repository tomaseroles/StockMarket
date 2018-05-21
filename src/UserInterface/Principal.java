package UserInterface;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import stockmarket.dbAccess;
import stockmarket.Company;
import stockmarket.Consola;
import stockmarket.Player;
import stockmarket.Transaction;

/**
 * Ventana principal de la aplicación
 * @author Tomas Eroles
 */
public class Principal extends javax.swing.JFrame {
    private DefaultTreeModel treeModel;
    boolean PuedeJugar=false;               //determina si el usuario activo puede jugar o no
    
    /**
     * Método constructor de la clase
     */
    public Principal() {
        //PanelDetalle.setVisible(false);
        initComponents();
    }
    
    /**
     * 
     * @author Tomas Eroles
     * @param username  es el nombre del usuario
     */
    public Principal(String username){
        System.out.println("Usuario: " + username);
        txtPlayer.setText(username);
    }
    
    /**
     * Configura la venta de acciones
     * - Actualiza el spinner de ventas
     */
    private void cfgVentaAcciones(){
        System.out.println("cfgVentaAcciones");
        try{
            //PrecioVenta.setValue(Acciones.getModel().getValueAt(Acciones. 3));
            ActualizarSpinnerVentas();
        } catch(Exception ex){
            Consola.Error(ex.getMessage(), "Configurar Venta de acciones");
        }
    }
    
    
    /**
     * Actualiza el estado del jugador, calculando las acciones que lleva compradas, las que ha vendido
     * el dinero gastado en las transacciones, el balance económico y el valor total del jugador
     * @param jugador es el identificador único del jugador
     * @author Tomas Eroles
     */
    private void ActualizarEstadoJugador(String jugador){
        try {
            AccionesCompradas.setValue(dbAccess.DSum("AccCompradas","operacionesjugador","PlayerName='" + jugador + "'"));
            AccionesVendidas.setValue(dbAccess.DSum("AccVendidas","operacionesjugador","PlayerName='" + jugador+"'"));
            ComprasDinero.setValue(dbAccess.DSum("Compra","operacionesjugador","PlayerName='" + jugador+"'"));
            VentasDinero.setValue(dbAccess.DSum("Venta","operacionesjugador","PlayerName = '" + jugador+"'"));
            double balance = dbAccess.DSum("Venta","operacionesjugador","PlayerName = '" + jugador+"'")-
                    dbAccess.DSum("Compra","operacionesjugador","PlayerName='" + jugador+"'");
            Balance.setValue(balance);
            ValorTotal.setValue(Player.CalcularValorTotal(jugador));
        } catch (Exception ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Actualiza los parámetros de funcionamiento del spinner de cómputo de acciones para realizar compras.
     * Obtiene un modelo SpinnerModel con los parámetros (valor actual, minimo, maximo, paso)
     * @throws Exception 
     */
    private  void ActualizarSpinnerCompras() throws Exception{
        String simbolo = Valores.getValueAt(Valores.getSelectedRow(),0).toString();
        int Paso = Integer.parseInt(MultiplosCompra.getItemAt(MultiplosCompra.getSelectedIndex()));
        AccionesCompra.setModel(Transaction.getSpinnerModel((double)AccionesCompra.getValue(), 1, dbAccess.DSum("sharesOutstanding", "Company", "Symbol = '" + simbolo +"'"), Paso));
    }    
    
    /**
     * Actualiza los parámetros de funcionamiento del spinner de cómputo de acciones para realizar ventas
     * Obtiene un modelo SpinnerModel con los parámetros (valor actual, minimo, maximo, paso)
     * @throws Exception 
     */
    private void ActualizarSpinnerVentas() throws Exception{
        try{
            String simbolo = Acciones.getValueAt(Acciones.getSelectedRow(),0).toString();
            String jugador = txtPlayer.getText();
            int actual = (int)AccionesVenta.getValue();
            int minimo = 1;
            int maximo = dbAccess.DSum("Acciones", "accionesjugador", "PlayerName ='" + jugador + "' AND Simbolo = '" + simbolo +"'");
            System.out.println("Maximo: "+maximo);
            int Paso = Integer.parseInt(MultiplosVenta.getItemAt(MultiplosVenta.getSelectedIndex()));
            AccionesVenta.setModel(Transaction.getSpinnerModel(actual, minimo, maximo, Paso));
            AccionesVenta.setToolTipText("Actual:"+actual+", Min:"+minimo+"Max:"+maximo+", Paso:"+Paso);
        } catch(Exception ex){
            
        }
    }
    
    /**
     * Calcula los datos de una transacción de compra
     * Por un lado calcula el coste de la operación, es decir, el dinero necesario
     * Por otro lado calcula el dinero líquido que quedará si se hace la operación
     * 
     * @author Tomas Eroles
     */
    private void CalculaValorOperacion(){
        try{
            double actual = (double)txtPrecioActual.getValue();
            double titulos = (double)AccionesCompra.getValue();
            double coste = actual*titulos;
            CosteOperacion.setValue(coste);
            System.out.println("Actual: " + actual + "\nTitulos: " + titulos + "\nCoste: " + coste);

            double disponible = dbAccess.DSum("cashMoney", "player", "playerName = '" + txtPlayer.getText() + "'");
            double djuego = disponible-coste;
            DisponibleJuego.setValue(djuego);
            System.out.println("Disponible: " + disponible + "\nDispJuego: " + djuego);
        } catch(Exception ex){
            Consola.Error(ex.getMessage(), "Error en CalculaValorOperacion");
        }
    }

    /**
     * Cierra la sesión, notificando a la base de datos que el usuario no está activo
     * 
     * @author Tomas Eroles
     */
    private void CerrarSesion(){
        String sql = "UPDATE player SET isAlive=0 WHERE playerName = '" + txtPlayer.getText() + "';";
        
        try {
            dbAccess.ExecuteNQ(sql);
        } catch (Exception ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
        dispose();
    }
    
    /**
    * Prepara la configuracion de la pantalla al inicio de la ejecución de la sesión.
    * Centra la ventana en la pantalla, ajusta los indicadores de progreso, obtiene la fecha de alta
    * Calcula el tiempo de juego, activa o desactiva paneles según sea el jugador y calcula la tabla Ranking
    * 
    * @author Tomas Eroles
    */
    private void ConfiguraInicio(){
        try{
            //Configuracion general de controles de la ventana
            setLocationRelativeTo(null);
            timerRanking.setMinimum(0);                 //sets the minimum value for timerRanking
            timerRanking.setMaximum(6);                //sets the maximum value for timerRanking
            timerAPI.setMinimum(0);                     //sets the minimum value for timerAPI
            timerAPI.setMaximum(15);                    //sets the maximum value for timerAPI
            
            //pestaña Ranking ------------------------------------------------------------------
            playerTransactions.setVisible(false);       //oculta playerTransactions (jTable de transacciones)
            CalcularRanking();
        } catch (Exception ex){
            System.err.println("Error en ConfiguracionInicio.\n"+ex.getMessage());
        }
    }

    /**
    * <h2>ConfiguraSesion</h2>
    * Configura la sesion para el usuario activo
    * Determina si el usuario puede o no jugar (si es admin o guest no puede)
    * Para el jugador recupera la fecha de alta, el tiempo jugado, el saldo disponible, lista de jugadas y valores comprados
    * @author Tomas Eroles
    */
    private void ConfiguraSesion(){
        String jugador=txtPlayer.getText();
        try{
            //parametros generales: sólo los jugadores pueden jugar
            PuedeJugar = !((jugador.equals("guest")) || (jugador.equals("admin")));
            
            PanelJugador.setVisible(PuedeJugar);
            if(PuedeJugar){
                Estado.setVisible(PuedeJugar);
                String q1 = "SELECT FechaAlta FROM Player WHERE playerName = '" + jugador + "';";
                ResultSet rs = dbAccess.exQuery(q1);
                while(rs.next()){
                    txtFechaAlta.setValue(rs.getDate("FechaAlta"));
                }
                Disponible.setValue(dbAccess.DSum("cashMoney", "player", "playerName = '" + jugador + "'"));
                DisponibleJuego.setValue(Disponible.getValue());
                CosteOperacion.setValue(0);
                TiempoJuego.setValue(Consola.int2strTime(dbAccess.DSum("TiempoJuego","Player","playerName = '" + jugador + "'")));
                MostrarJugadas(jugador);
                AccionesJugador();
                OperacionesJugador();
                PanelVentas.setEnabled(PuedeJugar);
                PanelAdmin.setVisible(false);
                PanelCompras.setVisible(false);
                PanelDetalle.setVisible(false);
                ActualizarEstadoJugador(jugador);
            }
            //Pestaña Valores --------------------------------------------------
            PreparaArbol(jugador);
            
            //Pestaña Ranking --------------------------------------------------
        } catch (Exception ex){
            System.err.println("Error en configuracion de sesion.\n"+ex.getMessage());
        }
    }

    /**
    * Actualiza el valor de la barra de progreso API o la pone a cero. 
    * Al llegar al final actualiza los valores de las acciones del jugador
    */
    public static void UpdateTimerAPI(){
        if(timerAPI.getValue()==timerAPI.getMaximum()){
            timerAPI.setValue(0);
            timerAPI.setString(""+timerAPI.getValue()+"/"+timerAPI.getMaximum());
            Player.UpdateEquities(txtPlayer.getText());
            //TO-DO: actualizar contenido de la tabla Valores
        } else {
            timerAPI.setValue(timerAPI.getValue()+1);
        }
    }
    
    /**
     * Actualiza el contenido de los controles que dependen de la base de datos y/o de la API
     * @throws Exception 
     */
    public static void UpdateTimerData() throws Exception{
        try{
        if(timerRanking.getValue()==timerRanking.getMaximum()){
            timerRanking.setValue(timerRanking.getMinimum());
            timerRanking.setString(""+timerRanking.getValue()+"/"+timerRanking.getMaximum());
            CalcularRanking();
            UpdateTimerAPI();
            Player.AumentaMinutos(txtPlayer.getText(),1);
            TiempoJuego.setValue(Consola.int2strTime(dbAccess.DSum("TiempoJuego","Player","playerName = '" + txtPlayer.getText() + "'")));
        } else {
            timerRanking.setValue(timerRanking.getValue()+1);
        }
        } catch(Exception ex){
            Consola.Error("TimerRanking" + timerRanking.getValue(),"UpdateTimerData");
        }
    }

    /**
    * Devuelve el nombre de usuario de jugador activo
    * @return nombre del usuario activo, identificador único en la base de datos
    */
    public static String getCurrentPlayer(){
        return txtPlayer.getText();
    }

    /**
    * Devuelve verdadero en caso de que se pueda realizar una transaccion
    * Para realizarse una transaccion se tiene que cumplir que:
    * - se haya seleccionado una empresa de la tabla Valores
    * Sólo funciona cuando el usuario es un jugador, es decir, ni es admin ni es guest
    * porque los botones de transacciones están deshabilitados
    * @param TypeOfTransaction es el tipo de transacción (1: venta, -1: compra)
    * @author Tomas Eroles
    */
    private boolean canTransact(int TypeOfTransaction){
        boolean salida;
        if(TypeOfTransaction==1){            //venta
            salida = (
                    Acciones.getModel().getValueAt(Acciones.getSelectedRow(),0)!=null && 
                    !((double)AccionesVenta.getValue()==0) 
                    );
        } else {                            //compra
            salida = (
                    Valores.getModel().getValueAt(Valores.getSelectedRow(),0)!=null &&
                    !((double)AccionesCompra.getValue()==0)
                    );
        }
        return salida;
    }
    
    public void setUser(String username){
        txtPlayer.setText(username);
    }
    
    /**
    * Prepara la información de la tabla Valores con la lista de Valores
    * @throws java.lang.Exception
    */
    public void PreparaValores() throws Exception{
        try {
            String query="";
            String nodo = treeStocks.getLastSelectedPathComponent().toString();
            System.out.println("Nodo: " + nodo);
            boolean flag=false;
            if(nodo.equals("Not classified")){
                query = "SELECT Symbol, coName, coCEO, coWebsite, Format(Capitalization,'Currency') AS Capital, Format(sharesOutstanding,'Currency') AS Acciones, Format(coValue,'Currency') AS PrecioAccion "
                        + "FROM Company "
                        + "WHERE ((coMarket Is Null) AND (coSector Is Null) AND (coIndustry Is Null));";
                flag=true;
            } else {            
                String path = treeStocks.getSelectionPath().toString();
                String[] camino = Company.getCompletePath(path);
                
                if(!(camino[1].equals(null)) && !(camino[2].equals(null)) && !(camino[3].equals(null))){
                    query = "SELECT Symbol, coName, coCEO, coWebSite, Format(Capitalization,'Currency') AS Capital, Format(sharesOutstanding,'Currency') AS Acciones, Format(coValue,'Currency') AS PrecioAccion "
                            +"FROM Company "
                            +"WHERE ((Cotiza<>0) AND (coMarket = '"+camino[1]+"') AND (coSector='"+camino[2]+"') AND coIndustry=('"+camino[3]+"'));";
                    flag=true;
                }
            }
            
            if(flag){
                DefaultTableModel modelo = dbAccess.ObtenerModelo(query);
                this.Valores.setModel(modelo);
            } else {
                Consola.Info("No hay datos que mostrar.","Tabla de Valores");
            }
        } catch (Exception ex) {
            System.err.println("Error en consulta. " + ex.getMessage());
        }
    }
    
    /**
    * MostrarJugadas
    * Lee de la base de datos, y muestra en la tabla playerTransactions las transacciones del jugador
    * @param jugador es el nombre del jugador del cual hay que mostrar las jugadas
    */
    private void MostrarJugadas(String jugador){
        String query = "SELECT Simbolo, Empresa, Fecha, Compra, Venta "+
                        "FROM operacionesjugador "+
                        "WHERE PlayerName = '" + jugador + "'"+
                        "ORDER BY Fecha ASC";
        DefaultTableModel modelo = dbAccess.ObtenerModelo(query);
        playerTransactions.setModel(modelo);
    }
    
    /**
     * Obtiene la lista de acciones que tiene el jugador y las muestra en la tabla Acciones
     */
    private static void AccionesJugador(){
        String query = "SELECT Simbolo, Empresa, Acciones, PrecioCompra "+
                        "FROM AccionesJugador "+
                        "WHERE PlayerName = '" + txtPlayer.getText() + "';";
        DefaultTableModel modelo = dbAccess.ObtenerModelo(query);
        Acciones.setModel(modelo);
    }
    
    /**
     * Recupera la lista de operaciones llevadas a cabo por el jugador y las muestra en la tabla Operaciones
     */
    private static  void OperacionesJugador(){
        Preparado.setText("Calculando...");
        Mensaje.setText("Recuperando operaciones del jugador " + txtPlayer.getText());
        String query = "SELECT Simbolo, Empresa, Fecha, Compra, Venta, Precio "+
                        "FROM OperacionesJugador "+
                        "WHERE PlayerName = '" + txtPlayer.getText() + "';";
        DefaultTableModel modelo = dbAccess.ObtenerModelo(query);
        Operaciones.setModel(modelo);
    }
    
    /**
     * Obtiene el ranking de los jugadores y lo muestra en la tabla Ranking
     */
    private static void CalcularRanking(){
        Preparado.setText("Calculando...");
        Mensaje.setText("Calculando ranking...");
        String ranking = "SELECT Name, JuegaDesde, TiempoJuego, Disponible, Invertido, Riqueza "+
                "FROM playersranking " +
                "ORDER BY Riqueza DESC;";
        DefaultTableModel modelo = dbAccess.ObtenerModelo(ranking);
        Ranking.setModel(modelo);
        Preparado.setText("Preparado");
        Mensaje.setText("");
    }
        
    public void MostrarDetalleSimbolo(String simbolo) throws Exception{
        Progreso.setIndeterminate(true);
        detalleSimbolo.setText(simbolo);
        String query = "SELECT * FROM Company WHERE Symbol = '" + simbolo + "';";
        ResultSet rs = dbAccess.exQuery(query);
        while(rs.next()){
            detalleCEO.setText(rs.getObject("coCEO").toString());
            detalleEmpresa.setText(rs.getObject("coName").toString());
            detalleWeb.setText(rs.getObject("coWebsite").toString());
            detalleDetalle.setText(rs.getObject("coDescription").toString());
        }
        //imagen de la API
        try{
            String urlImg = Company.getRawCompanyLogo(simbolo);
            BufferedImage img = ImageIO.read(new URL(urlImg));
            System.out.println(urlImg);
            Mensaje.setText("Localizando imagen de " + urlImg);
            Logotipo.setIcon(new javax.swing.ImageIcon(img.getScaledInstance(100,100,2)));
        } catch(Exception ex){
            Consola.Error("Error al obtener imagen de la API", simbolo);
            Logotipo.setIcon(null);
        }
        Mensaje.setText("");
        Progreso.setIndeterminate(false);
    }
    
    public  void PreparaArbol(String jugador) throws Exception{
        /*
        PreparaArbol()
        Prepara la información a mostrar en el árbol
        */
        Mensaje.setText("Preparando Arbol");
        treeStocks.removeAll();
        try{
            Mensaje.setText("Obteniendo información del contenido del árbol.");
            DefaultMutableTreeNode arbol = Company.getTreeFromDB(jugador);
            Mensaje.setText("Modelo de árbol obtenido.");
            treeModel = new DefaultTreeModel(arbol);
            treeModel.setRoot(arbol);
            treeStocks.setModel(treeModel);             //treeModel es el control JTree
            //System.out.println(arbol.toString());
        } catch (Exception ex){
            System.err.println(ex.getMessage());
        }
    }
    //---------------------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        entityManager = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory("stockmarket?zeroDateTimeBehavior=convertToNullPU").createEntityManager();
        companyQuery = java.beans.Beans.isDesignTime() ? null : entityManager.createQuery("SELECT c FROM Company c");
        companyList = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : companyQuery.getResultList();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        BarraSuperior = new javax.swing.JToolBar();
        jLabel8 = new javax.swing.JLabel();
        timerRanking = new javax.swing.JProgressBar();
        jLabel7 = new javax.swing.JLabel();
        timerAPI = new javax.swing.JProgressBar();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        cmdCloseSession = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        Estado = new javax.swing.JInternalFrame();
        jPanel9 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        PanelJugador = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        ValorTotal = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        TiempoJuego = new javax.swing.JFormattedTextField();
        txtFechaAlta = new javax.swing.JFormattedTextField();
        lblFechaAlta = new javax.swing.JLabel();
        Disponible = new javax.swing.JFormattedTextField();
        lblTiempoJuego = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        AccionesCompradas = new javax.swing.JFormattedTextField();
        ComprasDinero = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        AccionesVendidas = new javax.swing.JFormattedTextField();
        VentasDinero = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        Balance = new javax.swing.JFormattedTextField();
        EstadoIzquierdo = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        txtPlayer = new javax.swing.JLabel();
        Compras = new javax.swing.JInternalFrame();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        treeStocks = new javax.swing.JTree();
        jScrollPane4 = new javax.swing.JScrollPane();
        Valores = new javax.swing.JTable();
        PanelDetalle = new javax.swing.JPanel();
        Logotipo = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        detalleEmpresa = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        detalleSimbolo = new javax.swing.JTextField();
        detalleCEO = new javax.swing.JTextField();
        detalleWeb = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        detalleDetalle = new javax.swing.JTextPane();
        PanelCompras = new javax.swing.JPanel();
        cmdComprar = new javax.swing.JButton();
        lblPrecioActual = new javax.swing.JLabel();
        AccionesCompra = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        CosteOperacion = new javax.swing.JFormattedTextField();
        txtPrecioActual = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        DisponibleJuego = new javax.swing.JFormattedTextField();
        MultiplosCompra = new javax.swing.JComboBox<>();
        PanelAdmin = new javax.swing.JDesktopPane();
        cmdClasificar = new javax.swing.JButton();
        cmdDeclasificar = new javax.swing.JButton();
        tabRanking = new javax.swing.JInternalFrame();
        jScrollPane5 = new javax.swing.JScrollPane();
        Ranking = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        playerTransactions = new javax.swing.JTable();
        Actividad = new javax.swing.JInternalFrame();
        jPanel7 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        Operaciones = new javax.swing.JTable();
        Portafolio = new javax.swing.JInternalFrame();
        jPanel8 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        Acciones = new javax.swing.JTable();
        PanelVentas = new javax.swing.JPanel();
        lblPrecioActual1 = new javax.swing.JLabel();
        AccionesVenta = new javax.swing.JSpinner();
        jLabel15 = new javax.swing.JLabel();
        CosteOperacion1 = new javax.swing.JFormattedTextField();
        PrecioVenta = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        ResultadoVenta = new javax.swing.JFormattedTextField();
        MultiplosVenta = new javax.swing.JComboBox<>();
        cmdVender = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        Mensaje = new javax.swing.JTextField();
        Progreso = new javax.swing.JProgressBar();
        Preparado = new javax.swing.JTextField();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        BarraSuperior.setRollover(true);

        jLabel8.setText("Recálculo del ranking: ");
        BarraSuperior.add(jLabel8);

        timerRanking.setMaximum(60);
        timerRanking.setToolTipText("Consta de 6 ciclos de 10 segundos cada uno.\nCada minuto se recalcula el ranking de jugadores.");
        BarraSuperior.add(timerRanking);

        jLabel7.setText("  Recálculo de la API: ");
        BarraSuperior.add(jLabel7);

        timerAPI.setMaximum(15);
        timerAPI.setToolTipText("Tiene una duración de 15 minutos.\nCada vez que se completa el ciclo se recalculan los precios de las acciones.");
        BarraSuperior.add(timerAPI);
        BarraSuperior.add(jSeparator1);

        cmdCloseSession.setText("Cerrar sesión");
        cmdCloseSession.setFocusable(false);
        cmdCloseSession.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdCloseSession.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cmdCloseSession.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdCloseSessionMouseClicked(evt);
            }
        });
        BarraSuperior.add(cmdCloseSession);

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(100, 30));

        Estado.setVisible(true);

        ValorTotal.setEditable(false);
        ValorTotal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("¤#,##0.00"))));
        ValorTotal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ValorTotal.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N

        jLabel17.setFont(new java.awt.Font("sansserif", 1, 24)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("VALOR TOTAL DEL IMPERIO");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 863, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(282, 282, 282)
                .addComponent(ValorTotal)
                .addGap(284, 284, 284))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        TiempoJuego.setEditable(false);
        TiempoJuego.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        TiempoJuego.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N

        txtFechaAlta.setEditable(false);
        txtFechaAlta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));
        txtFechaAlta.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtFechaAlta.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N

        lblFechaAlta.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        lblFechaAlta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFechaAlta.setText("Activo desde");

        Disponible.setEditable(false);
        Disponible.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat(" #,##0.00 €"))));
        Disponible.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        Disponible.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N

        lblTiempoJuego.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        lblTiempoJuego.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTiempoJuego.setText("Tiempo de juego:");

        jLabel11.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Saldo disponible");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(9, 9, 9))
                    .addComponent(txtFechaAlta)
                    .addComponent(lblFechaAlta, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TiempoJuego)
                    .addComponent(lblTiempoJuego, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Disponible))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblFechaAlta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtFechaAlta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblTiempoJuego)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TiempoJuego, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Disponible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel11.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel11.setLayout(new java.awt.GridLayout(4, 3, 1, 1));

        jLabel1.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Operaciones");
        jPanel11.add(jLabel1);

        jLabel2.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Comprado");
        jLabel2.setToolTipText("");
        jPanel11.add(jLabel2);

        jLabel9.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Vendido");
        jPanel11.add(jLabel9);

        jLabel5.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel5.setText("En acciones:");
        jPanel11.add(jLabel5);

        AccionesCompradas.setEditable(false);
        AccionesCompradas.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        AccionesCompradas.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        AccionesCompradas.setFont(new java.awt.Font("sansserif", 1, 36)); // NOI18N
        jPanel11.add(AccionesCompradas);

        ComprasDinero.setEditable(false);
        ComprasDinero.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat(" #,##0.00 €"))));
        ComprasDinero.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ComprasDinero.setFont(new java.awt.Font("sansserif", 1, 36)); // NOI18N
        jPanel11.add(ComprasDinero);

        jLabel6.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel6.setText("En dinero:");
        jPanel11.add(jLabel6);

        AccionesVendidas.setEditable(false);
        AccionesVendidas.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        AccionesVendidas.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        AccionesVendidas.setFont(new java.awt.Font("sansserif", 1, 36)); // NOI18N
        jPanel11.add(AccionesVendidas);

        VentasDinero.setEditable(false);
        VentasDinero.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat(" #,##0.00 €"))));
        VentasDinero.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        VentasDinero.setFont(new java.awt.Font("sansserif", 1, 36)); // NOI18N
        jPanel11.add(VentasDinero);

        jLabel10.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jLabel10.setText("Balance:");
        jPanel11.add(jLabel10);

        jLabel19.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        jPanel11.add(jLabel19);

        Balance.setEditable(false);
        Balance.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat(" #,##0.00 €"))));
        Balance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        Balance.setFont(new java.awt.Font("sansserif", 1, 36)); // NOI18N
        jPanel11.add(Balance);

        EstadoIzquierdo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        EstadoIzquierdo.setName(""); // NOI18N

        javax.swing.GroupLayout EstadoIzquierdoLayout = new javax.swing.GroupLayout(EstadoIzquierdo);
        EstadoIzquierdo.setLayout(EstadoIzquierdoLayout);
        EstadoIzquierdoLayout.setHorizontalGroup(
            EstadoIzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        EstadoIzquierdoLayout.setVerticalGroup(
            EstadoIzquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 31, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout PanelJugadorLayout = new javax.swing.GroupLayout(PanelJugador);
        PanelJugador.setLayout(PanelJugadorLayout);
        PanelJugadorLayout.setHorizontalGroup(
            PanelJugadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelJugadorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelJugadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelJugadorLayout.createSequentialGroup()
                        .addGroup(PanelJugadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(EstadoIzquierdo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(PanelJugadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        PanelJugadorLayout.setVerticalGroup(
            PanelJugadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelJugadorLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelJugadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelJugadorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(EstadoIzquierdo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        txtPlayer.setFont(new java.awt.Font("Dialog", 3, 24)); // NOI18N
        txtPlayer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtPlayer.setText("jLabel13");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPlayer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelJugador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(txtPlayer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelJugador, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(40, 40, 40))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout EstadoLayout = new javax.swing.GroupLayout(Estado.getContentPane());
        Estado.getContentPane().setLayout(EstadoLayout);
        EstadoLayout.setHorizontalGroup(
            EstadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        EstadoLayout.setVerticalGroup(
            EstadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, EstadoLayout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Tu estado", Estado);

        Compras.setVisible(true);

        treeStocks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeStocksMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(treeStocks);

        Valores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ValoresMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(Valores);

        Logotipo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));

        detalleEmpresa.setEditable(false);
        detalleEmpresa.setText("jTextField3");

        jLabel14.setText("Empresa:");

        detalleSimbolo.setEditable(false);
        detalleSimbolo.setText("jTextField3");

        detalleCEO.setEditable(false);
        detalleCEO.setText("jTextField3");

        detalleWeb.setEditable(false);
        detalleWeb.setText("jTextField3");

        jLabel18.setText("CEO:");

        jLabel20.setText("Sitio web:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(detalleSimbolo, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(detalleEmpresa, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                    .addComponent(detalleCEO)
                    .addComponent(detalleWeb))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(detalleSimbolo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(detalleEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(detalleCEO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(detalleWeb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        detalleDetalle.setEditable(false);
        jScrollPane8.setViewportView(detalleDetalle);

        javax.swing.GroupLayout PanelDetalleLayout = new javax.swing.GroupLayout(PanelDetalle);
        PanelDetalle.setLayout(PanelDetalleLayout);
        PanelDetalleLayout.setHorizontalGroup(
            PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8)
                    .addGroup(PanelDetalleLayout.createSequentialGroup()
                        .addComponent(Logotipo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        PanelDetalleLayout.setVerticalGroup(
            PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Logotipo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8)
                .addContainerGap())
        );

        cmdComprar.setText("Comprar");
        cmdComprar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdComprarMouseClicked(evt);
            }
        });

        lblPrecioActual.setText("Precio actual: ");

        AccionesCompra.setModel(new javax.swing.SpinnerNumberModel(1.0d, null, null, 1.0d));
        AccionesCompra.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                AccionesCompraStateChanged(evt);
            }
        });

        jLabel4.setText("Acciones: ");

        CosteOperacion.setEditable(false);
        CosteOperacion.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00 €"))));
        CosteOperacion.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtPrecioActual.setEditable(false);
        txtPrecioActual.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00 €"))));
        txtPrecioActual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPrecioActual.setToolTipText("");

        jLabel3.setText("Disponible:");

        DisponibleJuego.setEditable(false);
        DisponibleJuego.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat(" #,##0.00 €"))));
        DisponibleJuego.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        MultiplosCompra.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "10", "100", "1000" }));
        MultiplosCompra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MultiplosCompraActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelComprasLayout = new javax.swing.GroupLayout(PanelCompras);
        PanelCompras.setLayout(PanelComprasLayout);
        PanelComprasLayout.setHorizontalGroup(
            PanelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelComprasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmdComprar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(PanelComprasLayout.createSequentialGroup()
                        .addGroup(PanelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelComprasLayout.createSequentialGroup()
                                .addGroup(PanelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                                .addGroup(PanelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(AccionesCompra)
                                    .addComponent(MultiplosCompra, 0, 76, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                            .addGroup(PanelComprasLayout.createSequentialGroup()
                                .addComponent(lblPrecioActual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(20, 20, 20)))
                        .addGroup(PanelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPrecioActual, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(DisponibleJuego, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CosteOperacion, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        PanelComprasLayout.setVerticalGroup(
            PanelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelComprasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPrecioActual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPrecioActual))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(AccionesCompra)
                    .addComponent(CosteOperacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(DisponibleJuego, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MultiplosCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdComprar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(7, 7, 7))
        );

        cmdClasificar.setText("Habilitar cotización");
        cmdClasificar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdClasificarMouseClicked(evt);
            }
        });

        cmdDeclasificar.setText("Suspender cotización");
        cmdDeclasificar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdDeclasificarMouseClicked(evt);
            }
        });

        PanelAdmin.setLayer(cmdClasificar, javax.swing.JLayeredPane.DEFAULT_LAYER);
        PanelAdmin.setLayer(cmdDeclasificar, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout PanelAdminLayout = new javax.swing.GroupLayout(PanelAdmin);
        PanelAdmin.setLayout(PanelAdminLayout);
        PanelAdminLayout.setHorizontalGroup(
            PanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelAdminLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cmdDeclasificar, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmdClasificar, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                .addContainerGap())
        );
        PanelAdminLayout.setVerticalGroup(
            PanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelAdminLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdClasificar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdDeclasificar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelCompras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelAdmin)
                    .addComponent(jScrollPane3))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(PanelAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelCompras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(PanelDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout ComprasLayout = new javax.swing.GroupLayout(Compras.getContentPane());
        Compras.getContentPane().setLayout(ComprasLayout);
        ComprasLayout.setHorizontalGroup(
            ComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ComprasLayout.setVerticalGroup(
            ComprasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Comprar acciones", Compras);

        tabRanking.setVisible(true);

        Ranking.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        Ranking.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RankingMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(Ranking);

        playerTransactions.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        playerTransactions.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                playerTransactionsFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(playerTransactions);

        javax.swing.GroupLayout tabRankingLayout = new javax.swing.GroupLayout(tabRanking.getContentPane());
        tabRanking.getContentPane().setLayout(tabRankingLayout);
        tabRankingLayout.setHorizontalGroup(
            tabRankingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabRankingLayout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE))
        );
        tabRankingLayout.setVerticalGroup(
            tabRankingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Ranking", tabRanking);

        Actividad.setVisible(true);

        jLabel12.setFont(new java.awt.Font("sansserif", 3, 18)); // NOI18N
        jLabel12.setText("Lista de movimientos");

        Operaciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane6.setViewportView(Operaciones);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 905, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout ActividadLayout = new javax.swing.GroupLayout(Actividad.getContentPane());
        Actividad.getContentPane().setLayout(ActividadLayout);
        ActividadLayout.setHorizontalGroup(
            ActividadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 905, Short.MAX_VALUE)
            .addGroup(ActividadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ActividadLayout.setVerticalGroup(
            ActividadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 496, Short.MAX_VALUE)
            .addGroup(ActividadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Actividad", Actividad);

        Portafolio.setVisible(true);

        jLabel13.setFont(new java.awt.Font("sansserif", 3, 18)); // NOI18N
        jLabel13.setText("Acciones que posee el jugador:");

        Acciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        Acciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AccionesMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(Acciones);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 905, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7)
                .addContainerGap())
        );

        lblPrecioActual1.setText("Precio actual: ");

        AccionesVenta.setModel(new javax.swing.SpinnerNumberModel(0.0d, null, null, 1.0d));
        AccionesVenta.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                AccionesVentaStateChanged(evt);
            }
        });

        jLabel15.setText("Acciones: ");

        CosteOperacion1.setEditable(false);
        CosteOperacion1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00 €"))));
        CosteOperacion1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        PrecioVenta.setEditable(false);
        PrecioVenta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.00 €"))));
        PrecioVenta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        PrecioVenta.setToolTipText("");

        jLabel16.setText("Ganancias:");

        ResultadoVenta.setEditable(false);
        ResultadoVenta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat(" #,##0.00 €"))));
        ResultadoVenta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        MultiplosVenta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "10", "100", "1000" }));
        MultiplosVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MultiplosVentaActionPerformed(evt);
            }
        });

        cmdVender.setText("Vender");
        cmdVender.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdVenderMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout PanelVentasLayout = new javax.swing.GroupLayout(PanelVentas);
        PanelVentas.setLayout(PanelVentasLayout);
        PanelVentasLayout.setHorizontalGroup(
            PanelVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelVentasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPrecioActual1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PrecioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AccionesVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CosteOperacion1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ResultadoVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                .addComponent(MultiplosVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cmdVender, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        PanelVentasLayout.setVerticalGroup(
            PanelVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelVentasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelVentasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PrecioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPrecioActual1)
                    .addComponent(jLabel15)
                    .addComponent(AccionesVenta)
                    .addComponent(CosteOperacion1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(MultiplosVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ResultadoVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdVender))
                .addContainerGap())
        );

        javax.swing.GroupLayout PortafolioLayout = new javax.swing.GroupLayout(Portafolio.getContentPane());
        Portafolio.getContentPane().setLayout(PortafolioLayout);
        PortafolioLayout.setHorizontalGroup(
            PortafolioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelVentas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(PortafolioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PortafolioLayout.setVerticalGroup(
            PortafolioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PortafolioLayout.createSequentialGroup()
                .addGap(0, 456, Short.MAX_VALUE)
                .addComponent(PanelVentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(PortafolioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(PortafolioLayout.createSequentialGroup()
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 32, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Portafolio", Portafolio);

        Mensaje.setEditable(false);

        Progreso.setFocusable(false);

        Preparado.setEditable(false);
        Preparado.setText("Preparado");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(Preparado, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Mensaje)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Progreso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(Mensaje)
                .addComponent(Preparado))
            .addComponent(Progreso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BarraSuperior, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 917, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(1, 1, 1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(BarraSuperior, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        
        /*
        pestaña estado
        lista de todas las acciones de cada usuario, con precio de compra y precio actual consultado en API
        el precio actual servirá para calcular el valor actual de las acciones compradas
        esta lista viene de la lista de transacciones, y se 
        */
        try{            
            ConfiguraInicio();
            ConfiguraSesion();
                        
        } catch(Exception ex){
            System.err.println("Error en inicializacion de pantalla.\n"+ex.getMessage());
        }
    }//GEN-LAST:event_formWindowOpened

    /**
     * Habilita la cotización de una empresa
     * Para habilitar una empresa se tiene que poner a 1 el campo Cotiza de la tabla Company
     * La lista de empresas contiene dos campos (Symbol y coName) por defecto.
     * Al activar la cotización hay que comprobar si el resto de campos de información están vacios
     * Si los campos están vacíos se obtiene la información de la API y se incorporan a la tabla
     * @see Company.TieneDatos
     * @param evt 
     */
    private void cmdClasificarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdClasificarMouseClicked
        // TODO add your handling code here:
        String simbolo = String.valueOf(Valores.getModel().getValueAt(Valores.getSelectedRow(),0));
        try{
            Company.FillDetailsFromAPI(simbolo);
            PreparaArbol(txtPlayer.getText());
            PreparaValores();
            PanelAdmin.setVisible(false);
            PanelDetalle.setVisible(false);
        } catch(Exception ex){
            Consola.Error(ex.getMessage(),"Error en actualizacion de detalles de símbolo.");
        }
    }//GEN-LAST:event_cmdClasificarMouseClicked

    private void cmdDeclasificarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdDeclasificarMouseClicked
        // TODO add your handling code here:
        String simbolo = String.valueOf(Valores.getModel().getValueAt(Valores.getSelectedRow(), 0));
        try{
            Company.FillDetailsToNull(simbolo);
            PreparaArbol(txtPlayer.getText());
            PreparaValores();
            PanelAdmin.setVisible(false);
            PanelDetalle.setVisible(false);
        } catch (Exception ex){
            Consola.Error(ex.getMessage(),"Error en la actualización de detalles de simbolo. " );
        }
    }//GEN-LAST:event_cmdDeclasificarMouseClicked

    private void treeStocksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeStocksMouseClicked
        // TODO add your handling code here:
        /*
        Este código está copiado de la clase MainWindow
        Alli funciona para recoger los datos de un JTree cuando se produce el evento click sobre el tree
        Aqui se tiene que acoplar para el arbol existente
        */
        try{
            PreparaValores();
            PanelDetalle.setVisible(false);
            PanelAdmin.setVisible(false);
            PanelCompras.setVisible(false);
        } catch(Exception ex){
            Consola.Error(ex.getMessage(),"Error en Valores. " );
        }
    }//GEN-LAST:event_treeStocksMouseClicked

    /**
     * Ocurre cada vez que se hace clic en el JTable Valores (cada vez que se selecciona una empresa)
     * Se tiene que activar el panel de compras si es un jugador
     * Se activará el panel de detalle
     * Se activará el panel de Administracion si es admin
     * Mostrará los detalles del símbolo elegido
     * @param evt 
     */
    private void ValoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ValoresMouseClicked
        try{                                     
            String simbolo = Valores.getValueAt(Valores.getSelectedRow(),0).toString();
            String jugador = txtPlayer.getText();
            PanelCompras.setVisible(PuedeJugar);
            PanelDetalle.setVisible(true);
            PanelAdmin.setVisible((txtPlayer.getText().equals("admin")));
            MostrarDetalleSimbolo(simbolo);
            DisponibleJuego.setValue(0);
            txtPrecioActual.setValue((double)dbAccess.DSum("coValue", "Company", "Symbol = '" + simbolo + "'"));
            ActualizarSpinnerCompras();
            try{
                DisponibleJuego.setValue(Disponible.getValue());
            } catch(Exception ex){
                System.err.println("Excepcion : " + ex.getMessage());
            }
        } catch(Exception ex){
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }//GEN-LAST:event_ValoresMouseClicked

    private void cmdComprarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdComprarMouseClicked
        // TODO add your handling code here:
        try{
            if(canTransact(-1)){
                String simbolo = String.valueOf(Valores.getModel().getValueAt(Valores.getSelectedRow(),0));
                String jugador = txtPlayer.getText();
                double acciones = (double)AccionesCompra.getValue();
                Transaction.newTransaction(simbolo, jugador, acciones, -1);
                PreparaValores();
                MostrarJugadas(jugador);
                AccionesJugador();
                OperacionesJugador();
                CalcularRanking();
                Disponible.setValue(dbAccess.DSum("cashMoney", "player", "playerName = '" + jugador + "'"));
                ActualizarEstadoJugador(jugador);
            } else {
                Consola.Error("Error al intentar transaccion.","Comprar acciones.");
            }
        } catch(Exception ex){
            
        }
    }//GEN-LAST:event_cmdComprarMouseClicked

    private void cmdVenderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdVenderMouseClicked
        // TODO add your handling code here:
        try{
            if(canTransact(1)){
                
                String simbolo = String.valueOf(Acciones.getModel().getValueAt(Acciones.getSelectedRow(),0));
                String jugador = txtPlayer.getText();
                double acciones = (double)AccionesVenta.getValue();
                Transaction.newTransaction(simbolo, jugador , acciones, 1);
                PreparaValores();
                MostrarJugadas(jugador);
                AccionesJugador();
                OperacionesJugador();
                CalcularRanking();
                Disponible.setValue(dbAccess.DSum("cashMoney", "player", "playerName = '" + jugador + "'"));
                ActualizarEstadoJugador(jugador);
            } else {
                Consola.Error("Error al intentar transaccion.","Vender aciones.");
            }   
        } catch(Exception ex){
            
        }
    }//GEN-LAST:event_cmdVenderMouseClicked

    private void RankingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RankingMouseClicked
        // TODO add your handling code here:
        playerTransactions.setVisible(true);
        MostrarJugadas(String.valueOf(Ranking.getModel().getValueAt(Ranking.getSelectedRow(),0)));
    }//GEN-LAST:event_RankingMouseClicked

    private void cmdCloseSessionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdCloseSessionMouseClicked
        // TODO add your handling code here:
        CerrarSesion();
    }//GEN-LAST:event_cmdCloseSessionMouseClicked

    private void AccionesCompraStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_AccionesCompraStateChanged
        // TODO add your handling code here:
        CalculaValorOperacion();
    }//GEN-LAST:event_AccionesCompraStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        CerrarSesion();
    }//GEN-LAST:event_formWindowClosing

    private void AccionesVentaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_AccionesVentaStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_AccionesVentaStateChanged

    private void MultiplosCompraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MultiplosCompraActionPerformed
        // TODO add your handling code here:
        //Consola.Info(MultiplosCompra.getItemAt(MultiplosCompra.getSelectedIndex()) ,"");
        try{
            ActualizarSpinnerCompras();
        } catch(Exception ex){
            
        }
    }//GEN-LAST:event_MultiplosCompraActionPerformed

    /**
     * Código que se ejecuta cuando se hace clic en la tabla Acciones (las que tiene el jugador)
     * @param evt 
     */
    private void AccionesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AccionesMouseClicked
        cfgVentaAcciones();
    }//GEN-LAST:event_AccionesMouseClicked

    private void MultiplosVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MultiplosVentaActionPerformed
        // TODO add your handling code here:
        try{
            ActualizarSpinnerVentas();
        } catch(Exception ex){
            
        }
    }//GEN-LAST:event_MultiplosVentaActionPerformed

    /**
     * Oculta la tabla de transacciones cuando ésta pierde el foco
     * @param evt 
     */
    private void playerTransactionsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_playerTransactionsFocusLost
        // TODO add your handling code here:
        playerTransactions.setVisible(false);
    }//GEN-LAST:event_playerTransactionsFocusLost

    /**
     * @param args the command line arguments
    */
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JTable Acciones;
    private javax.swing.JSpinner AccionesCompra;
    private static javax.swing.JFormattedTextField AccionesCompradas;
    private static javax.swing.JFormattedTextField AccionesVendidas;
    private javax.swing.JSpinner AccionesVenta;
    private javax.swing.JInternalFrame Actividad;
    private static javax.swing.JFormattedTextField Balance;
    private javax.swing.JToolBar BarraSuperior;
    private javax.swing.JInternalFrame Compras;
    private static javax.swing.JFormattedTextField ComprasDinero;
    private javax.swing.JFormattedTextField CosteOperacion;
    private javax.swing.JFormattedTextField CosteOperacion1;
    private javax.swing.JFormattedTextField Disponible;
    private static javax.swing.JFormattedTextField DisponibleJuego;
    private javax.swing.JInternalFrame Estado;
    private static javax.swing.JPanel EstadoIzquierdo;
    private static javax.swing.JLabel Logotipo;
    private static javax.swing.JTextField Mensaje;
    private javax.swing.JComboBox<String> MultiplosCompra;
    private javax.swing.JComboBox<String> MultiplosVenta;
    private static javax.swing.JTable Operaciones;
    private javax.swing.JDesktopPane PanelAdmin;
    private javax.swing.JPanel PanelCompras;
    private javax.swing.JPanel PanelDetalle;
    private javax.swing.JPanel PanelJugador;
    private javax.swing.JPanel PanelVentas;
    private javax.swing.JInternalFrame Portafolio;
    private javax.swing.JFormattedTextField PrecioVenta;
    private static javax.swing.JTextField Preparado;
    private static javax.swing.JProgressBar Progreso;
    public static javax.swing.JTable Ranking;
    private static javax.swing.JFormattedTextField ResultadoVenta;
    private static javax.swing.JFormattedTextField TiempoJuego;
    private javax.swing.JFormattedTextField ValorTotal;
    private javax.swing.JTable Valores;
    private static javax.swing.JFormattedTextField VentasDinero;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton cmdClasificar;
    private javax.swing.JButton cmdCloseSession;
    private javax.swing.JButton cmdComprar;
    private javax.swing.JButton cmdDeclasificar;
    private javax.swing.JButton cmdVender;
    private java.util.List<UserInterface.Company> companyList;
    private javax.persistence.Query companyQuery;
    private javax.swing.JTextField detalleCEO;
    private javax.swing.JTextPane detalleDetalle;
    private javax.swing.JTextField detalleEmpresa;
    private javax.swing.JTextField detalleSimbolo;
    private javax.swing.JTextField detalleWeb;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblFechaAlta;
    private javax.swing.JLabel lblPrecioActual;
    private javax.swing.JLabel lblPrecioActual1;
    private javax.swing.JLabel lblTiempoJuego;
    private javax.swing.JTable playerTransactions;
    private javax.swing.JInternalFrame tabRanking;
    private static javax.swing.JProgressBar timerAPI;
    private static javax.swing.JProgressBar timerRanking;
    private javax.swing.JTree treeStocks;
    private javax.swing.JFormattedTextField txtFechaAlta;
    private static javax.swing.JLabel txtPlayer;
    private javax.swing.JFormattedTextField txtPrecioActual;
    // End of variables declaration//GEN-END:variables
}
