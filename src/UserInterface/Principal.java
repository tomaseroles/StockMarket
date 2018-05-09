package UserInterface;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.sql.ResultSet;
import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import stockmarket.dbAccess;
import stockmarket.Company;
import stockmarket.Consola;
import stockmarket.Player;
import stockmarket.Transaction;

public class Principal extends javax.swing.JFrame {
    private DefaultTreeModel treeModel;

    public Principal() {
        //PanelDetalle.setVisible(false);
        initComponents();
    }
    
    public Principal(String username){
        System.out.println("Usuario: " + username);
        txtPlayer.setText(username);
    }
    
    //---------------------------------------------------------------------------
    // Funciones definidas para el proyecto
    public static void UpdateTimerAPI(){
        /*
        Actualiza el valor de la barra de progreso API o la pone a cero. 
        Al llegar al final actualiza los valores de las acciones del jugador
        */
        if(timerAPI.getValue()==timerAPI.getMaximum()){
            timerAPI.setValue(0);
            timerAPI.setString(""+timerAPI.getValue()+"/"+timerAPI.getMaximum());
            Player.UpdateEquities(txtPlayer.getText());
            //TO-DO: actualizar contenido de la tabla Valores
        } else {
            timerAPI.setValue(timerAPI.getValue()+1);
        }
    }
    
    public static void UpdateTimerData(){
        /*
        UpdateData
        Actualiza el contenido de los controles que dependen de la base de datos y/o de la API
        */
        
        if(timerRanking.getValue()==timerRanking.getMaximum()){
            timerRanking.setValue(timerRanking.getMinimum());
            timerRanking.setString(""+timerRanking.getValue()+"/"+timerRanking.getMaximum());
            CalcularRanking();
            UpdateTimerAPI();
        } else {
            timerRanking.setValue(timerRanking.getValue()+1);
        }
    }
    public static String getCurrentPlayer(){
        /*
        getCurrentPlayer
        Devuelve el nombre de usuario de jugador activo
        */
        return txtPlayer.getText();
    }
    
    private boolean canTransact(){
        return (Valores.getModel().getValueAt(Valores.getSelectedRow(),0)!=null && !txtPlayer.getText().equals("guest"));
    }
    
    public void setUser(String username){
        txtPlayer.setText(username);
    }
    
    public void PreparaValores() throws Exception{
        /*
        PreparaValores()
        Prepara la información de la tabla Valores con la lista de Valores
        Primera version: todos los valores
        */
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
                            +"WHERE ((coMarket = '"+camino[1]+"') AND (coSector='"+camino[2]+"') AND coIndustry=('"+camino[3]+"'));";
                    flag=true;
                }
            }
            
            if(flag){
                DefaultTableModel modelo = new DefaultTableModel();
                modelo = dbAccess.ObtenerModelo(query);
                this.Valores.setModel(modelo);
            } else {
                System.out.println("No hay datos que mostrar.");
            }
        } catch (Exception ex) {
            System.err.println("Error en consulta. " + ex.getMessage());
        }
    }
    
    private void MostrarJugadas(String jugador){
        String query = "SELECT Simbolo, Empresa, Fecha, Compra, Venta "+
                        "FROM operacionesjugador "+
                        "WHERE PlayerName = '" + jugador + "'"+
                        "ORDER BY Fecha ASC";
        DefaultTableModel modelo = dbAccess.ObtenerModelo(query);
        playerTransactions.setModel(modelo);
    }
    private static void AccionesJugador(){
        String query = "SELECT Simbolo, Empresa, Acciones "+
                        "FROM AccionesJugador "+
                        "WHERE PlayerName = '" + txtPlayer.getText() + "';";
        DefaultTableModel modelo = dbAccess.ObtenerModelo(query);
        Acciones.setModel(modelo);
    }
    
    private static  void OperacionesJugador(){
        String query = "SELECT Simbolo, Empresa, Fecha, Compra, Venta "+
                        "FROM OperacionesJugador "+
                        "WHERE PlayerName = '" + txtPlayer.getText() + "';";
        DefaultTableModel modelo = dbAccess.ObtenerModelo(query);
        Operaciones.setModel(modelo);
    }
    
    private static void CalcularRanking(){
        String ranking = "SELECT * FROM playersranking;";
        DefaultTableModel modelo = dbAccess.ObtenerModelo(ranking);
        Ranking.setModel(modelo);
    }
        
    public void MostrarDetalleSimbolo(String simbolo){
        if(treeStocks.getLastSelectedPathComponent().toString().equals("Not classified")){
            PanelDetalle.setVisible(true);
            String query = "SELECT coName, coCEO, coWebsite, coDescription "
                    +"FROM company "
                    +"WHERE Symbol = '" + simbolo + "';";
            try{
                System.out.println(query);
                
                ResultSet rs = dbAccess.exQuery(query);
                System.out.println("Columnas: " + rs.getMetaData().getColumnCount());
                for(int i=1;i<=rs.getMetaData().getColumnCount();i++)
                    System.out.println(rs.getString(i) + " / ");
                
                //detalleCEO.setText(rs.getString("coCEO"));
                //detalleCEO.setText(Valores.getModel().getValueAt(Valores.getSelectedRow(),3).toString());
                
                //detalleDetalle.setText(rs.getString("Description"));
                //detalleEmpresa.setText(rs.getString("coName"));
                //detalleSimbolo.setText(simbolo);
                //detalleWeb.setText(rs.getString("coWeb"));
                System.out.println("Colocamos la imagen en el control.\n"+getClass().getResource(Company.getRawCompanyLogo(simbolo)).toString());
                BufferedImage img = ImageIO.read(new URL(getClass().getResource(Company.getRawCompanyLogo(simbolo)).toString()));
                Logotipo.setIcon(new javax.swing.ImageIcon(img.getScaledInstance(438,438,2)));
            } catch(Exception ex){
                System.out.println("Error en consulta: " + ex.getMessage());
            }
        }
    }

    public  void PreparaArbol() throws Exception{
        /*
        PreparaArbol()
        Prepara la información a mostrar en el árbol
        */
        Consola.Mensaje("Preparando Arbol");
        treeStocks.removeAll();
        try{
            DefaultMutableTreeNode arbol = Company.getTreeFromDB();
            treeModel = new DefaultTreeModel(arbol);
            treeModel.setRoot(arbol);
            treeStocks.setModel(treeModel);             //treeModel es el control JTree
            System.out.println(arbol.toString());
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
        BarraSuperior = new javax.swing.JToolBar();
        jLabel8 = new javax.swing.JLabel();
        timerRanking = new javax.swing.JProgressBar();
        jLabel7 = new javax.swing.JLabel();
        timerAPI = new javax.swing.JProgressBar();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        tabStatus = new javax.swing.JInternalFrame();
        jScrollPane6 = new javax.swing.JScrollPane();
        Operaciones = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        lblFechaAlta = new javax.swing.JLabel();
        txtFechaAlta = new javax.swing.JFormattedTextField();
        lblTiempoJuego = new javax.swing.JLabel();
        txtPlayer = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtAccionesCompradas = new javax.swing.JTextField();
        txtComprasDinero = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtTiempoJuego = new javax.swing.JTextField();
        jScrollPane7 = new javax.swing.JScrollPane();
        Acciones = new javax.swing.JTable();
        tabEquities = new javax.swing.JInternalFrame();
        jPanel2 = new javax.swing.JPanel();
        cmdRefresh = new javax.swing.JButton();
        cmdComprar = new javax.swing.JButton();
        txtNumAcciones = new javax.swing.JSpinner();
        cmdVender = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        treeStocks = new javax.swing.JTree();
        jScrollPane4 = new javax.swing.JScrollPane();
        Valores = new javax.swing.JTable();
        cmdClasificar = new javax.swing.JButton();
        cmdDeclasificar = new javax.swing.JButton();
        PanelDetalle = new javax.swing.JPanel();
        Logotipo = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        detalleSimbolo = new javax.swing.JTextField();
        detalleEmpresa = new javax.swing.JTextField();
        detalleCEO = new javax.swing.JTextField();
        detalleWeb = new javax.swing.JTextField();
        jScrollPane8 = new javax.swing.JScrollPane();
        detalleDetalle = new javax.swing.JTextPane();
        tabRanking = new javax.swing.JInternalFrame();
        jScrollPane5 = new javax.swing.JScrollPane();
        Ranking = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        playerTransactions = new javax.swing.JTable();

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
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        BarraSuperior.setRollover(true);

        jLabel8.setText("Recálculo del ranking: ");
        BarraSuperior.add(jLabel8);

        timerRanking.setMaximum(60);
        BarraSuperior.add(timerRanking);

        jLabel7.setText("  Recálculo de la API: ");
        BarraSuperior.add(jLabel7);

        timerAPI.setMaximum(15);
        BarraSuperior.add(timerAPI);

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(100, 30));

        tabStatus.setVisible(true);

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

        lblFechaAlta.setText("Fecha de alta:");

        txtFechaAlta.setEditable(false);
        txtFechaAlta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));

        lblTiempoJuego.setText("Tiempo de juego:");

        txtPlayer.setFont(new java.awt.Font("Dialog", 3, 24)); // NOI18N
        txtPlayer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtPlayer.setText("jLabel13");

        jLabel1.setText("Operaciones:");

        jLabel2.setText("Comprado:");

        txtAccionesCompradas.setEditable(false);

        txtComprasDinero.setEditable(false);

        jTextField5.setEditable(false);

        jTextField6.setEditable(false);

        jTextField8.setEditable(false);

        jLabel5.setText("En acciones:");

        jLabel6.setText("En dinero:");

        jLabel9.setText("Vendido:");

        jLabel10.setText("Balance:");

        txtTiempoJuego.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPlayer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblFechaAlta)
                            .addComponent(lblTiempoJuego, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                .addGap(107, 107, 107)
                                                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(txtFechaAlta, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                .addComponent(txtAccionesCompradas, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(txtComprasDinero, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGap(0, 42, Short.MAX_VALUE)))
                                .addGap(96, 96, 96))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtTiempoJuego, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField6, txtAccionesCompradas, txtFechaAlta});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtPlayer)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaAlta)
                    .addComponent(txtFechaAlta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTiempoJuego)
                    .addComponent(txtTiempoJuego, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtAccionesCompradas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtComprasDinero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
        jScrollPane7.setViewportView(Acciones);

        javax.swing.GroupLayout tabStatusLayout = new javax.swing.GroupLayout(tabStatus.getContentPane());
        tabStatus.getContentPane().setLayout(tabStatusLayout);
        tabStatusLayout.setHorizontalGroup(
            tabStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabStatusLayout.createSequentialGroup()
                .addGroup(tabStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE))
        );
        tabStatusLayout.setVerticalGroup(
            tabStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
            .addGroup(tabStatusLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Estado", tabStatus);

        tabEquities.setVisible(true);

        cmdRefresh.setText("Refrescar");

        cmdComprar.setText("Comprar");
        cmdComprar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdComprarMouseClicked(evt);
            }
        });

        cmdVender.setText("Vender");
        cmdVender.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdVenderMouseClicked(evt);
            }
        });

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

        cmdClasificar.setText("Clasificar");
        cmdClasificar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdClasificarMouseClicked(evt);
            }
        });

        cmdDeclasificar.setText("A null");
        cmdDeclasificar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmdDeclasificarMouseClicked(evt);
            }
        });

        Logotipo.setText("jLabel13");

        jLabel14.setText("Simbolo:");

        jLabel16.setText("Nombre empresa:");

        jLabel18.setText("CEO:");

        jLabel20.setText("Sitio web:");

        jLabel22.setText("Descripción:");

        detalleSimbolo.setText("jTextField3");

        detalleEmpresa.setText("jTextField3");

        detalleCEO.setText("jTextField3");

        detalleWeb.setText("jTextField3");

        jScrollPane8.setViewportView(detalleDetalle);

        javax.swing.GroupLayout PanelDetalleLayout = new javax.swing.GroupLayout(PanelDetalle);
        PanelDetalle.setLayout(PanelDetalleLayout);
        PanelDetalleLayout.setHorizontalGroup(
            PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Logotipo, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelDetalleLayout.createSequentialGroup()
                        .addGroup(PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(detalleCEO)
                            .addComponent(detalleWeb)
                            .addComponent(jScrollPane8)))
                    .addGroup(PanelDetalleLayout.createSequentialGroup()
                        .addGroup(PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(detalleSimbolo, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                            .addComponent(detalleEmpresa))))
                .addContainerGap())
        );
        PanelDetalleLayout.setVerticalGroup(
            PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDetalleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelDetalleLayout.createSequentialGroup()
                        .addComponent(Logotipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(PanelDetalleLayout.createSequentialGroup()
                        .addGroup(PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(detalleSimbolo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(detalleEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(detalleCEO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(detalleWeb, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PanelDetalleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelDetalleLayout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)))))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                            .addComponent(PanelDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cmdRefresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdComprar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNumAcciones, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdVender)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmdDeclasificar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdClasificar)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmdRefresh)
                    .addComponent(cmdComprar)
                    .addComponent(txtNumAcciones)
                    .addComponent(cmdVender)
                    .addComponent(cmdClasificar)
                    .addComponent(cmdDeclasificar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );

        javax.swing.GroupLayout tabEquitiesLayout = new javax.swing.GroupLayout(tabEquities.getContentPane());
        tabEquities.getContentPane().setLayout(tabEquitiesLayout);
        tabEquitiesLayout.setHorizontalGroup(
            tabEquitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tabEquitiesLayout.setVerticalGroup(
            tabEquitiesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Valores", tabEquities);

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
        Ranking.setShowHorizontalLines(false);
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
        jScrollPane1.setViewportView(playerTransactions);

        javax.swing.GroupLayout tabRankingLayout = new javax.swing.GroupLayout(tabRanking.getContentPane());
        tabRanking.getContentPane().setLayout(tabRankingLayout);
        tabRankingLayout.setHorizontalGroup(
            tabRankingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabRankingLayout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE))
        );
        tabRankingLayout.setVerticalGroup(
            tabRankingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Ranking", tabRanking);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(BarraSuperior, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 917, Short.MAX_VALUE))
                .addGap(1, 1, 1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(BarraSuperior, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE))
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
            //Configuracion general de controles de la ventana
            setLocationRelativeTo(null);
            timerRanking.setMinimum(0);                 //sets the minimum value for timerRanking
            timerRanking.setMaximum(6);                //sets the maximum value for timerRanking
            timerAPI.setMinimum(0);                     //sets the minimum value for timerAPI
            timerAPI.setMaximum(15);                    //sets the maximum value for timerAPI
            //pestaña estado -------------------------------------------------------------------
            //calcular fecha de alta
            String queryFA = "SELECT FechaAlta, TiempoJuego "+
                            "FROM player "+
                            "WHERE playerName = '" + txtPlayer.getText() + "'";
            ResultSet rs = dbAccess.exQuery(queryFA);
            
            txtFechaAlta.setText(rs.getDate("FechaAlta").toString());
            txtTiempoJuego.setText((Consola.int2strTime(rs.getInt("TiempoJuego"))));
            
            //pestaña valores ------------------------------------------------------------------
            PreparaArbol();
            PanelDetalle.setVisible(false);             //oculta PanelDetalle (jPanel de detalle de empresa)
            //pestaña Ranking ------------------------------------------------------------------
            playerTransactions.setVisible(false);       //oculta playerTransactions (jTable de transacciones)
        } catch(Exception ex){
            System.err.println("Error en inicializacion de pantalla.\n"+ex.getMessage());
        }
    }//GEN-LAST:event_formWindowOpened

    private void cmdClasificarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdClasificarMouseClicked
        // TODO add your handling code here:
        if(Valores.getSelectedRow()==-1){
            //mostrar mensaje: no hay nada seleccionado
        } else {
            String simbolo = String.valueOf(Valores.getModel().getValueAt(Valores.getSelectedRow(),0));
            System.out.println(simbolo);
            try{
                Company.FillDetailsFromAPI(simbolo);
                PreparaArbol();
                PreparaValores();
            } catch(Exception ex){
                System.err.println("Error en actualizacion de detalles de símbolo. " + ex.getMessage());
            }
        }
    }//GEN-LAST:event_cmdClasificarMouseClicked

    private void cmdDeclasificarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdDeclasificarMouseClicked
        // TODO add your handling code here:
        if(Valores.getSelectedRow()==-1){
            //mostrar mensaje: no hay nbada seleccionado
        } else{
            String simbolo = String.valueOf(Valores.getModel().getValueAt(Valores.getSelectedRow(), 0));
            try{
                Company.FillDetailsToNull(simbolo);
                PreparaArbol();
                PreparaValores();
            } catch (Exception ex){
                System.err.println("Error en la actualización de detalles de simbolo. " + ex.getMessage());
            }
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
        } catch(Exception ex){
            System.out.println("Error en Valores. " + ex.getMessage());
        }
    }//GEN-LAST:event_treeStocksMouseClicked

    private void ValoresMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ValoresMouseClicked
        // TODO add your handling code here:
        System.out.println("Se ha seleccionado Valores: " + Valores.getValueAt(Valores.getSelectedRow() , 0).toString());
        MostrarDetalleSimbolo(Valores.getValueAt(Valores.getSelectedRow() , 0).toString());
    }//GEN-LAST:event_ValoresMouseClicked

    private void cmdComprarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdComprarMouseClicked
        // TODO add your handling code here:
        if(canTransact()){
            Transaction.newTransaction(String.valueOf(Valores.getModel().getValueAt(Valores.getSelectedRow(),0)), txtPlayer.getText() , (Integer)txtNumAcciones.getValue(), -1);
        } else {
            System.out.println("Error al intentar transaccion.");
        }
    }//GEN-LAST:event_cmdComprarMouseClicked

    private void cmdVenderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdVenderMouseClicked
        // TODO add your handling code here:
        if(canTransact()){
            Transaction.newTransaction(String.valueOf(Valores.getModel().getValueAt(Valores.getSelectedRow(),0)), txtPlayer.getText() , (Integer)txtNumAcciones.getValue(), 1);
        } else {
            System.out.println("Error al intentar transaccion.");
        }        
    }//GEN-LAST:event_cmdVenderMouseClicked

    private void RankingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RankingMouseClicked
        // TODO add your handling code here:
        playerTransactions.setVisible(true);
        MostrarJugadas(String.valueOf(Ranking.getModel().getValueAt(Ranking.getSelectedRow(),0)));
    }//GEN-LAST:event_RankingMouseClicked

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
    private javax.swing.JToolBar BarraSuperior;
    private javax.swing.JLabel Logotipo;
    private static javax.swing.JTable Operaciones;
    private javax.swing.JPanel PanelDetalle;
    public static javax.swing.JTable Ranking;
    private javax.swing.JTable Valores;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton cmdClasificar;
    private javax.swing.JButton cmdComprar;
    private javax.swing.JButton cmdDeclasificar;
    private javax.swing.JButton cmdRefresh;
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
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JLabel lblFechaAlta;
    private javax.swing.JLabel lblTiempoJuego;
    private javax.swing.JTable playerTransactions;
    private javax.swing.JInternalFrame tabEquities;
    private javax.swing.JInternalFrame tabRanking;
    private javax.swing.JInternalFrame tabStatus;
    private static javax.swing.JProgressBar timerAPI;
    private static javax.swing.JProgressBar timerRanking;
    private javax.swing.JTree treeStocks;
    private javax.swing.JTextField txtAccionesCompradas;
    private javax.swing.JTextField txtComprasDinero;
    private javax.swing.JFormattedTextField txtFechaAlta;
    private javax.swing.JSpinner txtNumAcciones;
    private static javax.swing.JLabel txtPlayer;
    private javax.swing.JTextField txtTiempoJuego;
    // End of variables declaration//GEN-END:variables
}
