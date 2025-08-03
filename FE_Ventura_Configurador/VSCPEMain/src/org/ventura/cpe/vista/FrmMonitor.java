/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.vista;

import com.sap.smb.sbo.api.ICompany;
import com.toedter.calendar.JDateChooser;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.springframework.boot.ResourceBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.ventura.cpe.bl.PublicardocWsBL;
import org.ventura.cpe.bl.TransaccionBL;
import org.ventura.cpe.dao.DocumentoDAO;
import org.ventura.cpe.dao.controller.PublicardocWsJC;
import org.ventura.cpe.dto.hb.PublicardocWs;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.cpe.main.Programa;
import org.ventura.cpe.main.config.Configuracion;
import org.ventura.cpe.main.config.Loader;
import org.ventura.cpe.reporte.Reporte;
import org.ventura.cpe.sb1.DocumentoBL;
import org.ventura.utilidades.config.FrmConfig;
import org.ventura.utilidades.entidades.GestorTiempo;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.awt.Color;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;

/**
 * @author Percy
 */
public class FrmMonitor extends javax.swing.JFrame {

    public static ConfigurableApplicationContext applicationContext;

    /**
     * Creates new form FrmMonitor
     */
    private static String valorFeId = null;

    private static final SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy"); //jbecerra

    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS"); //jbecerra

    private static ServerSocket socket = null;

    private static ScheduledFuture sf = null;

    private static ScheduledFuture sf1 = null;

    private List<PublicardocWs> listDocPub = null;

    public static boolean bandMensaje = false;

    static MensajeEspera ventanaMensajeEspera = new MensajeEspera(new javax.swing.JFrame(), true);

    /*
     Objeto para la tabla
     */
    private JTable tabla;

    /*
     Objeto para las fechas
     */
    public static JDateChooser fechaFin;

    public static JDateChooser fechaInicio;

    /*
     Objeto para el llenado de la tabla
     */
    private DefaultTableModel modelo;

    public static ResourceBundle rs;

    public FrmMonitor(Configuracion configuracion) {
        initComponents();
        //btnDetener.setEnabled(false);
        mnDetener.setEnabled(false);
        AreaNotificacion(configuracion.getErp().getBaseDeDatos().normalValue());
        this.setExtendedState(JFrame.ICONIFIED);
        mnIniciarActionPerformed(null);

        // Coloca las fechas según la ubicación dada.
        fechaFin = new JDateChooser();
        fechaFin.setDateFormatString("dd/MM/yyyy");
        fechaFin.setBounds(12, 60, 210, 30); // Modify depending on your preference

        fechaInicio = new JDateChooser();
        fechaInicio.setDateFormatString("dd/MM/yyyy");
        fechaInicio.setBounds(12, 125, 210, 30); // Modify depending on your preference

        jPanel5.add(fechaInicio);
        jPanel5.add(fechaFin);

        rs = ResourceBundle.getBundle("org.ventura.cpe.recursos.DatosReglasIdioma", Locale.getDefault());
        FrmConfig.setRs(rs);
    }

    private void vaciarTabla() {
        if (modelo != null) {
            while (modelo.getRowCount() > 0) {
                modelo.removeRow(0);
            }
        }

    }

    private void llenarTabla(List<Reporte> lst) {

        modelo = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        tabla = this.jTable1;
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setModel(modelo);

        modelo.setColumnIdentifiers(new Object[]{"Tipo de Documento", "Serie/Correlativo", "Fecha Emision", "RUC", "Importe", "Respuesta SUNAT"});

        if (lst.size() > 0) {
            for (int i = 0; i < lst.size(); i++) {
                Format formatter = new SimpleDateFormat("yyyy-MM-dd");
                String fecha = formatter.format(lst.get(i).getFecha());
                modelo.addRow(new Object[]{lst.get(i).getTipoDocumento(), lst.get(i).getNumeroSerie(), fecha, lst.get(i).getRucCliente(), lst.get(i).getTotalDocumento(), lst.get(i).getRespuestaSunat()});
            }
        }

        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(0).setCellRenderer(tcr);
        tabla.getColumnModel().getColumn(1).setCellRenderer(tcr);
        tabla.getColumnModel().getColumn(2).setCellRenderer(tcr);
        tabla.getColumnModel().getColumn(3).setCellRenderer(tcr);
        tabla.getColumnModel().getColumn(4).setCellRenderer(tcr);
        tabla.getColumnModel().getColumn(5).setCellRenderer(tcr);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(450);

        //jTable1.updateUI();
    }

    public void AreaNotificacion(String mensaje) {
        if (SystemTray.isSupported()) {
            PopupMenu popup = new PopupMenu();
            Image image = null;
            try {
                /* Evitar utilizar este metodo para imagenes superiores a 100KB*/
                ImageIO.setUseCache(false);
                image = ImageIO.read(ClassLoader.getSystemResourceAsStream("org/ventura/cpe/iconos/icono16.png"));
            } catch (Exception ex) {
                LoggerTrans.getCDMainLogger().log(Level.WARNING, "{0}. {1}",
                        new Object[]{"No es posible cargar el ícono", ex.getMessage()}
                );
            }
            final SystemTray tray = SystemTray.getSystemTray();
            final TrayIcon trayIcon = new TrayIcon(image, "VSCPE - Facturación Electrónica - " + mensaje, popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent me) {
                    if (me.getClickCount() == 2) {
                        tray.remove(trayIcon);
                        setVisible(true);
                        setExtendedState(JFrame.NORMAL);
                    }
                }

                @Override
                public void mousePressed(MouseEvent me) {

                }

                @Override
                public void mouseReleased(MouseEvent me) {

                }

                @Override
                public void mouseEntered(MouseEvent me) {

                }

                @Override
                public void mouseExited(MouseEvent me) {

                }
            });
            ActionListener exitListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (Programa.ESTADO != Programa.ESTADO_DETENIDO) {
                        JOptionPane.showMessageDialog(null, "Debe detener el servicio antes de terminar la ejecución");
                        tray.remove(trayIcon);
                        setVisible(true);
                        setExtendedState(JFrame.NORMAL);
                    } else {
                        int ret = JOptionPane.showConfirmDialog(null, "¿Está seguro que desea SALIR?", "Advertencia", JOptionPane.YES_NO_OPTION);
                        if (ret == JOptionPane.YES_OPTION) {
                            System.exit(0);
                        }
                    }
                }
            };

            MenuItem defaultItem = new MenuItem("Abrir");
            defaultItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tray.remove(trayIcon);
                    setVisible(true);
                    setExtendedState(JFrame.NORMAL);
                }
            });
            popup.add(defaultItem);
            defaultItem = new MenuItem("Salir");
            defaultItem.addActionListener(exitListener);
            popup.add(defaultItem);
            addWindowStateListener(new WindowStateListener() {
                @Override
                public void windowStateChanged(WindowEvent e) {
                    if (e.getNewState() == ICONIFIED || e.getNewState() == 7) {
                        try {
                            tray.add(trayIcon);
                            setVisible(false);
                        } catch (AWTException ex) {
                            LoggerTrans.getCDMainLogger().log(Level.WARNING, "{0}. {1}",
                                    new Object[]{"No es posible agregar al área de notificación", ex.getMessage()}
                            );
                        }
                    }
                    if (e.getNewState() == MAXIMIZED_BOTH || e.getNewState() == NORMAL) {
                        tray.remove(trayIcon);
                        setVisible(true);
                    }
                }
            });

            addWindowListener(new WindowListener() {

                @Override
                public void windowOpened(WindowEvent we) {
                    tray.remove(trayIcon);
                }

                @Override
                public void windowClosing(WindowEvent we) {
                    try {
                        tray.add(trayIcon);
                        setVisible(false);
                    } catch (AWTException ex) {
                        LoggerTrans.getCDMainLogger().log(Level.WARNING, "{0}. {1}",
                                new Object[]{"No es posible agregar al área de notificación", ex.getMessage()}
                        );
                    }
                }

                @Override
                public void windowClosed(WindowEvent we) {

                }

                @Override
                public void windowIconified(WindowEvent we) {

                }

                @Override
                public void windowDeiconified(WindowEvent we) {

                }

                @Override
                public void windowActivated(WindowEvent we) {

                }

                @Override
                public void windowDeactivated(WindowEvent we) {

                }
            });
        } else {
            LoggerTrans.getCDMainLogger().log(Level.WARNING, "Área de notificación NO SOPORTADA");
        }

        setIconImage(Toolkit.getDefaultToolkit().getImage("icono32.png"));

        setVisible(true);
        Rectangle screen = getGraphicsConfiguration().getBounds();
        setLocation((screen.width - getBounds().width) / 2, (screen.height - getBounds().height) / 2);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton3 = new javax.swing.JButton();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        mnuEliminar = new javax.swing.JMenuItem();
        mnuForzarActualización = new javax.swing.JMenuItem();
        mnuSeleccionarTodo = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        txtExportar = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTransacciones = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        btnVerDetalle = new javax.swing.JButton();
        chkSeleccionarTodo = new javax.swing.JCheckBox();
        btnEliminar = new javax.swing.JButton();
        btnForzarActualizacion = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTblPublicaciones = new javax.swing.JTable();
        jBtnForzarActualizacionPublic = new javax.swing.JButton();
        jChkSeleccionarTodoPublic = new javax.swing.JCheckBox();
        jBtnPublicarDoc = new javax.swing.JButton();
        jBtnBuscarDocPublic = new javax.swing.JButton();
        jTxfBuscarDocPublic = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        lblVersion = new javax.swing.JLabel();
        lblTexto = new javax.swing.JLabel();
        mnSociedad = new javax.swing.JLabel();
        cmbSociedades = new javax.swing.JComboBox<String>();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        mnIniciar = new javax.swing.JMenuItem();
        mnDetener = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        mnManualUsuario = new javax.swing.JMenuItem();
        mnConfiguracion = new javax.swing.JMenuItem();
        mnListaErrores = new javax.swing.JMenuItem();
        mnCatalogo8 = new javax.swing.JMenuItem();
        mnProcedimientosAux = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        mnsb1ManualUsuario = new javax.swing.JMenuItem();
        mnsbConfiguracion = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        mnLogViewer = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMItmAcercaDe = new javax.swing.JMenuItem();

        jButton3.setText("Manual de Configuración");

        mnuEliminar.setText("Eliminar");
        mnuEliminar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mnuEliminarMouseReleased(evt);
            }
        });
        jPopupMenu1.add(mnuEliminar);

        mnuForzarActualización.setText("Forzar Actualización");
        mnuForzarActualización.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mnuForzarActualizaciónMouseReleased(evt);
            }
        });
        jPopupMenu1.add(mnuForzarActualización);

        mnuSeleccionarTodo.setText("Seleccionar Todo");
        mnuSeleccionarTodo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mnuSeleccionarTodoMouseReleased(evt);
            }
        });
        jPopupMenu1.add(mnuSeleccionarTodo);

        jMenu1.setText("jMenu1");

        setTitle("VSCPE");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null}
                },
                new String[]{
                        "", "", "", ""
                }
        ));
        jScrollPane2.setViewportView(jTable1);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Búsqueda"));

        jLabel1.setText("FECHA INICIO");

        jLabel2.setText("FECHA FIN");

        jButton5.setText("Buscar");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Exportar");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("Examinar");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel3.setText("UBICACION:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(txtExportar, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel1)
                                                .addComponent(jLabel2))
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(jButton5))
                                        .addGroup(jPanel5Layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(jButton7)
                                                .addGap(38, 38, 38)
                                                .addComponent(jButton6)
                                                .addGap(21, 21, 21)))
                                .addGap(22, 22, 22))
        );
        jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addGap(58, 58, 58)
                                .addComponent(jLabel2)
                                .addGap(67, 67, 67)
                                .addComponent(jButton5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtExportar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton6)
                                        .addComponent(jButton7))
                                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 683, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGap(23, 23, 23)
                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap())
        );

        jTabbedPane1.addTab("Reporte de Estado", jPanel4);

        tblTransacciones.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "Id", "DocEntry", "DocNum", "Tipo Doc.", "Estado", ""
                }
        ));
        tblTransacciones.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblTransaccionesMouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(tblTransacciones);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Transacción");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Cabecera");
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("Detalles");
        treeNode1.add(treeNode2);
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane4.setViewportView(jTree1);

        btnVerDetalle.setText("Ver Detalle");
        btnVerDetalle.setVisible(false);
        btnVerDetalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerDetalleActionPerformed(evt);
            }
        });

        chkSeleccionarTodo.setText("Seleccionar Todo");
        chkSeleccionarTodo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chkSeleccionarTodoMouseClicked(evt);
            }
        });

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        btnForzarActualizacion.setText("Forzar Actualización");
        btnForzarActualizacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForzarActualizacionActionPerformed(evt);
            }
        });

        jTblPublicaciones.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "Num. Doc.", "Doc. Cliente", "Nombre Cliente", "Fecha Emisión", "Monto Total", ""
                }
        ) {
            boolean[] canEdit = new boolean[]{
                    false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTblPublicaciones);
        if (jTblPublicaciones.getColumnModel().getColumnCount() > 0) {
            jTblPublicaciones.getColumnModel().getColumn(0).setResizable(false);
            jTblPublicaciones.getColumnModel().getColumn(1).setResizable(false);
            jTblPublicaciones.getColumnModel().getColumn(2).setResizable(false);
            jTblPublicaciones.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTblPublicaciones.getColumnModel().getColumn(3).setResizable(false);
            jTblPublicaciones.getColumnModel().getColumn(4).setResizable(false);
            jTblPublicaciones.getColumnModel().getColumn(5).setResizable(false);
            jTblPublicaciones.getColumnModel().getColumn(5).setPreferredWidth(6);
        }

        jBtnForzarActualizacionPublic.setText("Forzar Actualización");
        jBtnForzarActualizacionPublic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnForzarActualizacionPublicActionPerformed(evt);
            }
        });

        jChkSeleccionarTodoPublic.setText("Seleccionar Todo");
        jChkSeleccionarTodoPublic.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jChkSeleccionarTodoPublicMouseClicked(evt);
            }
        });

        jBtnPublicarDoc.setText("Publicar");
        jBtnPublicarDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnPublicarDocActionPerformed(evt);
            }
        });

        jBtnBuscarDocPublic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ventura/cpe/iconos/search.png"))); // NOI18N
        jBtnBuscarDocPublic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnBuscarDocPublicActionPerformed(evt);
            }
        });

        jTxfBuscarDocPublic.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTxfBuscarDocPublicKeyPressed(evt);
            }
        });

        jSeparator1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                                                .addComponent(btnForzarActualizacion)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(chkSeleccionarTodo))
                                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                                                .addComponent(jTxfBuscarDocPublic, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jBtnBuscarDocPublic, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                .addGap(0, 0, Short.MAX_VALUE)))
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap())
                                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                                .addGap(24, 24, 24)
                                                                .addComponent(jBtnForzarActualizacionPublic)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jBtnPublicarDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(btnVerDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jChkSeleccionarTodoPublic))
                                                                .addGap(10, 10, 10))))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jSeparator1)
                                                        .addComponent(jScrollPane1))
                                                .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jBtnBuscarDocPublic, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jBtnForzarActualizacionPublic)
                                                .addComponent(jChkSeleccionarTodoPublic)
                                                .addComponent(jBtnPublicarDoc)
                                                .addComponent(jTxfBuscarDocPublic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btnVerDetalle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(btnEliminar)
                                                .addComponent(btnForzarActualizacion)
                                                .addComponent(chkSeleccionarTodo)))
                                .addContainerGap())
        );

        jTabbedPane1.addTab("Gestor Publicaciones / Transacciones", jPanel3);

        lblVersion.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        lblVersion.setForeground(new java.awt.Color(102, 102, 102));

        lblTexto.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblTexto.setForeground(new java.awt.Color(204, 0, 0));
        lblTexto.setText("Servicio detenido");

        mnSociedad.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        mnSociedad.setForeground(new java.awt.Color(0, 51, 153));

        cmbSociedades.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

        jMenu2.setText("Componente");

        mnIniciar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        mnIniciar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ventura/cpe/iconos/Start-icon.png"))); // NOI18N
        mnIniciar.setText("Iniciar");
        mnIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnIniciarActionPerformed(evt);
            }
        });
        jMenu2.add(mnIniciar);

        mnDetener.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        mnDetener.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ventura/cpe/iconos/Stop-red-icon.png"))); // NOI18N
        mnDetener.setText("Detener");
        mnDetener.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnDetenerActionPerformed(evt);
            }
        });
        jMenu2.add(mnDetener);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ventura/cpe/iconos/Configuration_icon.png"))); // NOI18N
        jMenuItem3.setText("Configuración");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Documentos");

        mnManualUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ventura/cpe/iconos/document_icon.jpg"))); // NOI18N
        mnManualUsuario.setText("VSCPE - Manual de Usuario");
        mnManualUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnManualUsuarioActionPerformed(evt);
            }
        });
        jMenu3.add(mnManualUsuario);

        mnConfiguracion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ventura/cpe/iconos/document_icon.jpg"))); // NOI18N
        mnConfiguracion.setText("VSCPE - Manual de Configuración");
        mnConfiguracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnConfiguracionActionPerformed(evt);
            }
        });
        jMenu3.add(mnConfiguracion);

        mnListaErrores.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ventura/cpe/iconos/document_icon.jpg"))); // NOI18N
        mnListaErrores.setText("Lista de Errores");
        mnListaErrores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnListaErroresActionPerformed(evt);
            }
        });
        jMenu3.add(mnListaErrores);

        mnCatalogo8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ventura/cpe/iconos/document_icon.jpg"))); // NOI18N
        mnCatalogo8.setText("Catálogo Sunat");
        mnCatalogo8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnCatalogo8ActionPerformed(evt);
            }
        });
        jMenu3.add(mnCatalogo8);

        mnProcedimientosAux.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ventura/cpe/iconos/document_icon.jpg"))); // NOI18N
        mnProcedimientosAux.setText("Procedimiento Auxiliares");
        mnProcedimientosAux.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnProcedimientosAuxActionPerformed(evt);
            }
        });
        jMenu3.add(mnProcedimientosAux);

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ventura/cpe/iconos/document_icon.jpg"))); // NOI18N
        jMenuItem1.setText("Manual de Excepciones");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1);

        mnsb1ManualUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ventura/cpe/iconos/document_icon.jpg"))); // NOI18N
        mnsb1ManualUsuario.setText("SB1 - Manual de Usuario");
        mnsb1ManualUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnsb1ManualUsuarioActionPerformed(evt);
            }
        });
        jMenu3.add(mnsb1ManualUsuario);

        mnsbConfiguracion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ventura/cpe/iconos/document_icon.jpg"))); // NOI18N
        mnsbConfiguracion.setText("SB1 - Manual de Configuración");
        mnsbConfiguracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnsbConfiguracionActionPerformed(evt);
            }
        });
        jMenu3.add(mnsbConfiguracion);

        jMenuBar1.add(jMenu3);

        jMenu5.setText("Logs");

        mnLogViewer.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        mnLogViewer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ventura/cpe/iconos/log-icon.png"))); // NOI18N
        mnLogViewer.setText("Abrir LogViewer");
        mnLogViewer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnLogViewerActionPerformed(evt);
            }
        });
        jMenu5.add(mnLogViewer);

        jMenuBar1.add(jMenu5);

        jMenu4.setText("Acerca De");

        jMItmAcercaDe.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        jMItmAcercaDe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ventura/cpe/iconos/icon-about.png"))); // NOI18N
        jMItmAcercaDe.setText("Acerca de");
        jMItmAcercaDe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMItmAcercaDeActionPerformed(evt);
            }
        });
        jMenu4.add(jMItmAcercaDe);

        jMenuBar1.add(jMenu4);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jTabbedPane1)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(lblTexto, javax.swing.GroupLayout.PREFERRED_SIZE, 479, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(40, 40, 40)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(lblVersion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(mnSociedad, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cmbSociedades, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(lblTexto, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(19, 19, 19))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(mnSociedad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(cmbSociedades))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(lblVersion, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(1, 1, 1)))
                                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 566, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (Programa.ESTADO == Programa.ESTADO_DETENIDO) {
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

    private void verDetalleEnTree(String id) {
        Transaccion tr = null;

        try {
            // se obtiene el arbol
            DefaultTreeModel treeModel = (DefaultTreeModel) jTree1.getModel();
            // se obtiene la raiz del arbol (Transaccion)
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
            // se obtiene el primer nodo hijo (Cabecera)
            DefaultMutableTreeNode nodo1 = (DefaultMutableTreeNode) root.getChildAt(0);
            // se obtiene el segundo nodo hijo (Lineas)
            DefaultMutableTreeNode nodo2 = (DefaultMutableTreeNode) root.getChildAt(1);

            // se elimina todos los hijos del nodo cabecera y lineas
            nodo1.removeAllChildren();
            nodo2.removeAllChildren();
            // se actualiza el arbol con los nuevos nodos cabecera y lineas
            treeModel.reload(nodo1);
            treeModel.reload(nodo2);

            if (!id.isEmpty()) {

                // se obtiene la data de transaccion segun el parametro fe_id
                tr = TransaccionBL.ListarTransaccionCola(id);

                switch (tr.getDOCCodigo()) {
                    case "01": //Factura
                    case "03": //Boleta
                    case "07": //Nota Credito
                    case "08": //Nota Debito

                        // se agrega los siguientes subnodos al primer nodo hijo
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Fecha Emisión : </b>" + ((tr.getDOCFechaEmision() != null) ? sdf1.format(tr.getDOCFechaEmision()) : "") + "</html>"));
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Num. Documento : </b>" + ((tr.getDOCSerie() != null) ? tr.getDOCSerie() : "") + '-' + ((tr.getDOCNumero() != null) ? tr.getDOCNumero() : "") + "</html>"));
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Tipo Documento : </b>" + ((tr.getDOCCodigo() != null) ? tr.getDOCCodigo() : "") + "</html>"));
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Tipo Moneda : </b>" + ((tr.getDOCMONCodigo() != null) ? tr.getDOCMONCodigo() : "") + "</html>"));
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Importe Total Venta : </b>" + ((tr.getDOCMontoTotal() != null) ? tr.getDOCMontoTotal().toString() : "") + "</html>"));

                        if (tr.getDOCCodigo().equalsIgnoreCase("07") || tr.getDOCCodigo().equalsIgnoreCase("08")) {
                            nodo1.add(new DefaultMutableTreeNode("<html><b>Doc. Afectación : </b>" + ((tr.getREFDOCId() != null) ? tr.getREFDOCId() : "") + "</html>"));
                            nodo1.add(new DefaultMutableTreeNode("<html><b>Tip. Doc. Afectación : </b>" + ((tr.getREFDOCTipo() != null) ? tr.getREFDOCTipo() : "") + "</html>"));
                        }

                        // se actualiza el arbol con el sub nodo hijo
                        treeModel.reload(nodo1);

                        if (tr.getTransaccionLineasList() != null) {

                            if (!tr.getTransaccionLineasList().isEmpty()) {
                                for (int i = 0; i < tr.getTransaccionLineasList().size(); i++) {
                                    nodo2.add(new DefaultMutableTreeNode("Detalle-" + String.valueOf(tr.getTransaccionLineasList().get(i).getTransaccionLineasPK().getNroOrden())));
                                    treeModel.reload(nodo2);

                                    DefaultMutableTreeNode nodoLinea = (DefaultMutableTreeNode) nodo2.getChildAt(i);

                                    // se agrega los siguientes subnodos al segundo nodo hijo
                                    nodoLinea.add(new DefaultMutableTreeNode("<html><b>Id Línea : </b>" + String.valueOf(tr.getTransaccionLineasList().get(i).getTransaccionLineasPK().getNroOrden() + "</html>")));
                                    nodoLinea.add(new DefaultMutableTreeNode("<html><b>Cod. Producto : </b>" + ((tr.getTransaccionLineasList().get(i).getCodArticulo() != null) ? tr.getTransaccionLineasList().get(i).getCodArticulo() : "") + "</html>"));
                                    nodoLinea.add(new DefaultMutableTreeNode("<html><b>Descripción : </b>" + ((tr.getTransaccionLineasList().get(i).getDescripcion() != null) ? tr.getTransaccionLineasList().get(i).getDescripcion() : "") + "</html>"));
                                    nodoLinea.add(new DefaultMutableTreeNode("<html><b>Cantidad : </b>" + ((tr.getTransaccionLineasList().get(i).getCantidad() != null) ? tr.getTransaccionLineasList().get(i).getCantidad().toString() : "") + "</html>"));
                                    nodoLinea.add(new DefaultMutableTreeNode("<html><b>Precio Venta : </b>" + ((tr.getTransaccionLineasList().get(i).getPrecioIGV() != null) ? tr.getTransaccionLineasList().get(i).getPrecioIGV().toString() : "") + "</html>"));
                                    nodoLinea.add(new DefaultMutableTreeNode("<html><b>Total Línea : </b>" + ((tr.getTransaccionLineasList().get(i).getTotalLineaConIGV() != null) ? tr.getTransaccionLineasList().get(i).getTotalLineaConIGV().toString() : "") + "</html>"));
                                    nodoLinea.add(new DefaultMutableTreeNode("<html><b>Tipo Afectación : </b>" + ((tr.getTransaccionImpuestosList().get(i).getTipoAfectacion() != null) ? tr.getTransaccionImpuestosList().get(i).getTipoAfectacion() : "") + "</html>"));

                                    treeModel.reload(nodoLinea);
                                }

                            } else {
                                // se pone por defecto vacio en el nodo 2
                                nodo2.add(new DefaultMutableTreeNode("-"));
                                // se actualiza el arbol con el sub nodo hijo
                                treeModel.reload(nodo2);
                            }

                        } else {
                            // se pone por defecto vacio en el nodo 2
                            nodo2.add(new DefaultMutableTreeNode("-"));
                            // se actualiza el arbol con el sub nodo hijo
                            treeModel.reload(nodo2);
                        }

                        break;

                    case "20": // Retención
                    case "40": // Percepción

                        // se agrega los siguientes subnodos al primer nodo hijo
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Fecha Emisión : </b>" + ((tr.getDOCFechaEmision() != null) ? sdf1.format(tr.getDOCFechaEmision()) : "") + "</html>"));
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Num. Documento : </b>" + ((tr.getDOCSerie() != null) ? tr.getDOCSerie() : "") + '-' + ((tr.getDOCNumero() != null) ? tr.getDOCNumero() : "") + "</html>"));
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Nombre Emisor : </b>" + ((tr.getRazonSocial() != null) ? tr.getRazonSocial() : "") + "</html>"));
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Ruc Emisor : </b>" + ((tr.getDocIdentidadNro() != null) ? tr.getDocIdentidadNro() : "") + "</html>"));
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Nombre Cliente : </b>" + ((tr.getSNRazonSocial() != null) ? tr.getSNRazonSocial() : "") + "</html>"));
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Num. Doc. Cliente : </b>" + ((tr.getSNDocIdentidadNro() != null) ? tr.getSNDocIdentidadNro() : "") + "</html>"));
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Regimen Percepción : </b>" + ((tr.getRETRegimen() != null) ? tr.getRETRegimen() : "") + "</html>"));
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Tasa Percepción : </b>" + ((tr.getRETTasa() != null) ? tr.getRETTasa() : "") + "</html>"));

                        BigDecimal importeTotalPercibido = new BigDecimal("0.0");
                        for (int i = 0; i < tr.getTransaccionComprobantePagoList().size(); i++) {
                            if (tr.getTransaccionComprobantePagoList().get(i).getCPImporte() != null) {
                                importeTotalPercibido = importeTotalPercibido.add(tr.getTransaccionComprobantePagoList().get(i).getCPImporte());
                            }
                        }
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Importe Total Percibido : </b>" + importeTotalPercibido.toString() + "</html>"));
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Moneda Importe Total Percibido : </b> PEN </html>"));

                        BigDecimal importeTotalCobrado = new BigDecimal("0.0");
                        for (int i = 0; i < tr.getTransaccionComprobantePagoList().size(); i++) {
                            if (tr.getTransaccionComprobantePagoList().get(i).getCPImporteTotal() != null) {
                                importeTotalCobrado = importeTotalCobrado.add(tr.getTransaccionComprobantePagoList().get(i).getCPImporteTotal());
                            }
                        }
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Importe Total Cobrado : </b>" + importeTotalCobrado.toString() + "</html>"));
                        nodo1.add(new DefaultMutableTreeNode("<html><b>Moneda Importe Total Cobrado : </b>" + ((tr.getMonedaPagado() != null) ? tr.getMonedaPagado() : "") + "</html>"));

                        // se actualiza el arbol con el sub nodo hijo
                        treeModel.reload(nodo1);

                        if (tr.getTransaccionComprobantePagoList() != null) {
                            if (!tr.getTransaccionComprobantePagoList().isEmpty()) {
                                for (int i = 0; i < tr.getTransaccionComprobantePagoList().size(); i++) {
                                    nodo2.add(new DefaultMutableTreeNode("Detalle-" + String.valueOf(tr.getTransaccionComprobantePagoList().get(i).getTransaccionComprobantePagoPK().getNroOrden())));
                                    treeModel.reload(nodo2);

                                    DefaultMutableTreeNode nodoLinea = (DefaultMutableTreeNode) nodo2.getChildAt(i);
                                    if (tr.getDOCCodigo().equalsIgnoreCase("40")) {
                                        // se agrega los siguientes subnodos al segundo nodo hijo                                  
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Id Linea : </b>" + String.valueOf(tr.getTransaccionComprobantePagoList().get(i).getTransaccionComprobantePagoPK().getNroOrden()) + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Tip. Doc. Relac. : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getDOCTipo() != null) ? tr.getTransaccionComprobantePagoList().get(i).getDOCTipo() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Num. Doc. Relac. : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getDOCNumero() != null) ? tr.getTransaccionComprobantePagoList().get(i).getDOCNumero() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Fecha Emisión Doc. Relac. : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getDOCFechaEmision() != null) ? sdf1.format(tr.getTransaccionComprobantePagoList().get(i).getDOCFechaEmision()) : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Importe Total Doc. Relac. : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getDOCImporte() != null) ? tr.getTransaccionComprobantePagoList().get(i).getDOCImporte().toString() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Tipo Moneda Doc. Relac. : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getDOCMoneda() != null) ? tr.getTransaccionComprobantePagoList().get(i).getDOCMoneda() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Fecha Cobro : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getPagoFecha() != null) ? sdf1.format(tr.getTransaccionComprobantePagoList().get(i).getPagoFecha()) : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Número Cobro : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getPagoNumero() != null) ? tr.getTransaccionComprobantePagoList().get(i).getPagoNumero() : "") + "</html>"));
                                        if (tr.getTransaccionComprobantePagoList().get(i).getCPImporteTotal() != null && tr.getTransaccionComprobantePagoList().get(i).getCPImporte() != null) {
                                            nodoLinea.add(new DefaultMutableTreeNode("<html><b>Importe Cobro : </b>" + tr.getTransaccionComprobantePagoList().get(i).getCPImporteTotal().subtract(tr.getTransaccionComprobantePagoList().get(i).getCPImporte()).toString() + "</html>"));
                                        }
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Moneda Cobro : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getPagoMoneda() != null) ? tr.getTransaccionComprobantePagoList().get(i).getPagoMoneda() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Importe Percibido : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getCPImporte() != null) ? tr.getTransaccionComprobantePagoList().get(i).getCPImporte().toString() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Moneda Importe Percibido : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getCPMoneda() != null) ? tr.getTransaccionComprobantePagoList().get(i).getCPMoneda() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Fecha Pecepción : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getPagoFecha() != null) ? sdf1.format(tr.getTransaccionComprobantePagoList().get(i).getPagoFecha()) : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Monto Total A Cobrar : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getCPImporteTotal() != null) ? tr.getTransaccionComprobantePagoList().get(i).getCPImporteTotal().toString() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Moneda Monto Total A Cobrar : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getCPMonedaMontoNeto() != null) ? tr.getTransaccionComprobantePagoList().get(i).getCPMonedaMontoNeto() : "") + "</html>"));
                                    } else if (tr.getDOCCodigo().equalsIgnoreCase("20")) {
                                        // se agrega los siguientes subnodos al segundo nodo hijo                                  
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Id Linea : </b>" + String.valueOf(tr.getTransaccionComprobantePagoList().get(i).getTransaccionComprobantePagoPK().getNroOrden()) + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Tip. Doc. Relac. : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getDOCTipo() != null) ? tr.getTransaccionComprobantePagoList().get(i).getDOCTipo() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Num. Doc. Relac. : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getDOCNumero() != null) ? tr.getTransaccionComprobantePagoList().get(i).getDOCNumero() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Fecha Emisión Doc. Relac. : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getDOCFechaEmision() != null) ? sdf1.format(tr.getTransaccionComprobantePagoList().get(i).getDOCFechaEmision()) : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Importe Total Doc. Relac. : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getDOCImporte() != null) ? tr.getTransaccionComprobantePagoList().get(i).getDOCImporte().toString() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Tipo Moneda Doc. Relac. : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getDOCMoneda() != null) ? tr.getTransaccionComprobantePagoList().get(i).getDOCMoneda() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Fecha Pago : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getPagoFecha() != null) ? sdf1.format(tr.getTransaccionComprobantePagoList().get(i).getPagoFecha()) : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Número Pago : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getPagoNumero() != null) ? tr.getTransaccionComprobantePagoList().get(i).getPagoNumero() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Importe Pago Sin Retención : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getPagoImporteSR().toString() != null) ? tr.getTransaccionComprobantePagoList().get(i).getPagoImporteSR().toString() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Moneda Pago : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getPagoMoneda() != null) ? tr.getTransaccionComprobantePagoList().get(i).getPagoMoneda() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Importe Retenido : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getCPImporte() != null) ? tr.getTransaccionComprobantePagoList().get(i).getCPImporte().toString() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Moneda Importe Retenido : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getCPMoneda() != null) ? tr.getTransaccionComprobantePagoList().get(i).getCPMoneda() : "") + "</html>"));
                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Fecha Retención : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getPagoFecha() != null) ? sdf1.format(tr.getTransaccionComprobantePagoList().get(i).getPagoFecha()) : "") + "</html>"));
                                        if (tr.getTransaccionComprobantePagoList().get(i).getPagoImporteSR() != null && tr.getTransaccionComprobantePagoList().get(i).getCPImporte() != null) {
                                            nodoLinea.add(new DefaultMutableTreeNode("<html><b>Importe Total A Pagar : </b>" + tr.getTransaccionComprobantePagoList().get(i).getPagoImporteSR().add(tr.getTransaccionComprobantePagoList().get(i).getCPImporte()).toString() + "</html>"));
                                        }

                                        nodoLinea.add(new DefaultMutableTreeNode("<html><b>Moneda Monto Neto Pagado : </b>" + ((tr.getTransaccionComprobantePagoList().get(i).getCPMonedaMontoNeto() != null) ? tr.getTransaccionComprobantePagoList().get(i).getCPMonedaMontoNeto() : "") + "</html>"));
                                    }
                                    treeModel.reload(nodoLinea);
                                }

                            } else {
                                // se pone por defecto vacio en el nodo 2
                                nodo2.add(new DefaultMutableTreeNode("-"));
                                // se actualiza el arbol con el sub nodo hijo
                                treeModel.reload(nodo2);
                            }

                        } else {
                            // se pone por defecto vacio en el nodo 2
                            nodo2.add(new DefaultMutableTreeNode("-"));
                            // se actualiza el arbol con el sub nodo hijo
                            treeModel.reload(nodo2);
                        }

                        break;
                    default:
                        break;
                }

            } else {
                // se pone por defecto vacio en el nodo 1
                nodo1.add(new DefaultMutableTreeNode("-"));
                // se actualiza el arbol con el sub nodo hijo
                treeModel.reload(nodo1);
                // se pone por defecto vacio en el nodo 2
                nodo2.add(new DefaultMutableTreeNode("-"));
                // se actualiza el arbol con el sub nodo hijo
                treeModel.reload(nodo2);
            }

            // expandir todos los nodos del treeview
            for (int i = 0; i < jTree1.getRowCount(); i++) {
                jTree1.expandRow(i);
            }

        } catch (Exception ex) {
            throw ex;
        }
    }

    //Clase para manejar el TableCellRenderer, que permitirá mostrar el JCheckBox
    class Render_CheckBox extends JCheckBox implements TableCellRenderer {

        private final JComponent component = new JCheckBox();

        public Render_CheckBox() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            //Color de fondo de la celda
            ((JCheckBox) component).setBackground(new Color(98, 138, 183));
            //obtiene valor boolean y coloca valor en el JCheckBox
            boolean b = (Boolean) value;
            ((JCheckBox) component).setSelected(b);
            return ((JCheckBox) component);
        }
    }

    private int contarRegistrosMarcados(DefaultTableModel dtm, int nroColumna) {
        int contadorRegistrosMarcados = 0;
        try {
            int registrosEnTabla = dtm.getRowCount();

            if (registrosEnTabla > 0) {
                for (int i = 0; i < registrosEnTabla; i++) {
                    if (dtm.getValueAt(i, nroColumna).equals(true)) {
                        contadorRegistrosMarcados = contadorRegistrosMarcados + 1;
                    }
                }
            } else {
                // si no hay registros en tabla
                contadorRegistrosMarcados = -1;
            }
            return contadorRegistrosMarcados;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void mnuEliminarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuEliminarMouseReleased
        String id = "";
        try {
            int row = tblTransacciones.getSelectedRow();
            int col = 0;
            // obteniedo id
            id = tblTransacciones.getValueAt(row, col).toString();
            // preguntar esta seguro que desea eliminar la transación
            int ret = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar la transacción con Id: " + id + "?", "Advertencia", JOptionPane.YES_NO_OPTION);
            if (ret == JOptionPane.YES_OPTION) {
                if (row >= 0) {
                    // se eliminar transaccion según id
                    TransaccionBL.Eliminar(new Transaccion(id));
                    // se refresca tabla
                    listarTransaccionesEnTabla();
                    // se refresca tree
                    verDetalleEnTree("");
                }
                JOptionPane.showMessageDialog(this, "Se eliminó la transacción.");
                LoggerTrans.getCDMainLogger().log(Level.INFO, "{0} - Se realizó la acción: Eliminar, en el Id: {1}.", new Object[]{sdf2.format(new Date()), id});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al eliminar: {0}", ex.getMessage());
        }
    }//GEN-LAST:event_mnuEliminarMouseReleased

    private void mnuForzarActualizaciónMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuForzarActualizaciónMouseReleased
        try {
            listarTransaccionesEnTabla();
            if (valorFeId != null && !valorFeId.equals("")) {
                if (!verificarIdEnTabla(valorFeId)) {
                    verDetalleEnTree("");
                }
            }
            LoggerTrans.getCDMainLogger().log(Level.INFO, "{0} - Se realizó la acción: Forzar Actualización", sdf2.format(new Date()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al forzar actualización: " + ex.getMessage());
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al forzar actualización: {0}", ex.getMessage());
        }
    }//GEN-LAST:event_mnuForzarActualizaciónMouseReleased

    private void mnuSeleccionarTodoMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mnuSeleccionarTodoMouseReleased
        DefaultTableModel dtm = (DefaultTableModel) tblTransacciones.getModel();
        int registrosMarcados = 0;
        for (int i = 0; i < dtm.getRowCount(); i++) {
            if (dtm.getValueAt(i, 5).equals(true)) {
                registrosMarcados = registrosMarcados + 1;
            }
        }

        if (registrosMarcados == dtm.getRowCount()) {
            for (int i = 0; i < dtm.getRowCount(); i++) {
                dtm.setValueAt(false, i, 5);
            }
        } else if (registrosMarcados == 0) {
            for (int i = 0; i < dtm.getRowCount(); i++) {
                dtm.setValueAt(true, i, 5);
            }
        } else if (registrosMarcados > 0 && registrosMarcados < dtm.getRowCount()) {
            for (int i = 0; i < dtm.getRowCount(); i++) {
                dtm.setValueAt(true, i, 5);
            }
        }
    }//GEN-LAST:event_mnuSeleccionarTodoMouseReleased

    private void mnManualUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnManualUsuarioActionPerformed
        FrmMU fmu = FrmMU.getInstance();
        fmu.ModalDynamicPDFViewer();
    }//GEN-LAST:event_mnManualUsuarioActionPerformed

    private void mnConfiguracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnConfiguracionActionPerformed
        // TODO add your handling code here:
        FrmMC fmc = FrmMC.getInstance();
        fmc.ModalDynamicPDFViewer();
    }//GEN-LAST:event_mnConfiguracionActionPerformed

    private void mnListaErroresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnListaErroresActionPerformed
        // TODO add your handling code here:
        FrmLE fle = FrmLE.getInstance();
        fle.ModalDynamicPDFViewer();
    }//GEN-LAST:event_mnListaErroresActionPerformed

    private void mnProcedimientosAuxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnProcedimientosAuxActionPerformed
        // TODO add your handling code here:
        FrmPA fpa = FrmPA.getInstance();
        fpa.ModalDynamicPDFViewer();
    }//GEN-LAST:event_mnProcedimientosAuxActionPerformed

    private void mnCatalogo8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnCatalogo8ActionPerformed
        // TODO add your handling code here:
        FrmC8 fc8 = FrmC8.getInstance();
        fc8.ModalDynamicPDFViewer();
    }//GEN-LAST:event_mnCatalogo8ActionPerformed

    private void mnLogViewerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnLogViewerActionPerformed
        // TODO add your handling code here:
        Runtime aplicacion = Runtime.getRuntime();
        try {

            String sRutaLog = System.getProperty("user.dir");
            String sRutaGeneral[] = sRutaLog.split("[\\\\/]", -1);
            sRutaLog = "";
            for (int i = 0; i < sRutaGeneral.length - 1; i++) {
                sRutaLog = sRutaLog + sRutaGeneral[i] + File.separator;
            }
            sRutaLog = sRutaLog + "\\olv-1.3.2" + File.separator + "olv.bat";

            aplicacion.exec("cmd.exe /K " + sRutaLog);

        } catch (Exception e) {
            System.out.println(e);
        }

    }//GEN-LAST:event_mnLogViewerActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // TODO add your handling code here:
        FrmConfig config = FrmConfig.getInstance();
        config.setVisible(true);
        if (!Programa.MENSAJE.startsWith("ERR:")) {
            config.limpiarTablaReglas();
            config.cargarCombosTablaReglasIdioma();
            config.obtenerRegistrosReglaBD();
            config.limpiarTablaBdAnadidas();
            config.cargarInicialesBdAnadidas();
            config.obtenerRegistrosBDsAnadidas();
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void mnIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnIniciarActionPerformed
        // TODO add your handling code here:

        mnIniciar.setEnabled(false);
        Programa.start();
        lblTexto.setText(Programa.MENSAJE);
        lblVersion.setText(Loader.version);

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r);
                    }
                }
        );

        sf = scheduler.scheduleWithFixedDelay(() -> {
            lblTexto.setText(Programa.MENSAJE);
            switch (Programa.ESTADO) {
                case Programa.ESTADO_INICIADO:
                    lblTexto.setText("Servicio en ejecución");
                    mnIniciar.setEnabled(false);
                    mnDetener.setEnabled(true);
                    ejecutarGestorXTiempo();
                    sf.cancel(false);
                    mnSociedad.setText(Programa.SAPSociedad);
                    cmbSociedades.removeAllItems();
                    if (DocumentoBL.Sociedad != null) {
                        cmbSociedades.addItem(DocumentoBL.Sociedad.getCompanyName());
                    }
                    if (DocumentoBL.listSociedades != null) {
                        for (ICompany sociedad : DocumentoBL.listSociedades) {
                            cmbSociedades.addItem(sociedad.getCompanyName());
                        }
                    }
                    break;
                case Programa.ESTADO_DETENIDO:
                    mnIniciar.setEnabled(true);
                    mnDetener.setEnabled(false);
                    sf.cancel(false);
                    break;
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        sf1 = scheduler.scheduleWithFixedDelay(() -> listarLogs(), 0, 15, TimeUnit.SECONDS);
    }//GEN-LAST:event_mnIniciarActionPerformed

    private void mnDetenerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnDetenerActionPerformed
        // TODO add your handling code here:
        mnDetener.setEnabled(false);

        Programa.stop();
        lblTexto.setText(Programa.MENSAJE);
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, Thread::new);
        sf = scheduler.scheduleWithFixedDelay(() -> {
            lblTexto.setText(Programa.MENSAJE);
            if (Programa.ESTADO == Programa.ESTADO_DETENIDO) {
                mnIniciar.setEnabled(true);
                mnDetener.setEnabled(false);
                scheduler.shutdown();
                sf.cancel(false);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }//GEN-LAST:event_mnDetenerActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        FrmME fme = FrmME.getInstance();
        fme.ModalDynamicPDFViewer();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMItmAcercaDeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMItmAcercaDeActionPerformed
        FrmAcercaDe frmAcercaDe = new FrmAcercaDe();
        frmAcercaDe.setFrmMonitor(this);
        frmAcercaDe.detalleInformacion();//Carga TextArea con informacion de la aplicacion
        frmAcercaDe.setVisible(true);
        this.setEnabled(false);
    }//GEN-LAST:event_jMItmAcercaDeActionPerformed

    private void mnsb1ManualUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnsb1ManualUsuarioActionPerformed
        // TODO add your handling code here:
        FrmSB1MU fsbmu = FrmSB1MU.getInstance();
        fsbmu.ModalDynamicPDFViewer();
    }//GEN-LAST:event_mnsb1ManualUsuarioActionPerformed

    private void mnsbConfiguracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnsbConfiguracionActionPerformed
        // TODO add your handling code here:
        FrmSB1MC fsbmc = FrmSB1MC.getInstance();
        fsbmc.ModalDynamicPDFViewer();
    }//GEN-LAST:event_mnsbConfiguracionActionPerformed

    private void jTxfBuscarDocPublicKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTxfBuscarDocPublicKeyPressed

    }//GEN-LAST:event_jTxfBuscarDocPublicKeyPressed

    private void jBtnBuscarDocPublicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnBuscarDocPublicActionPerformed
        String docBuscado = jTxfBuscarDocPublic.getText();
        if (docBuscado == null || docBuscado.equals("")) {
            JOptionPane.showMessageDialog(this, "Ingrese Nro. de Documento a Buscar.");
        } else {
            PublicardocWs docBuscadoPublic = obtenerDocMarcado(docBuscado);
            if (docBuscadoPublic == null) {
                JOptionPane.showMessageDialog(this, "No se encontró documento a Buscar.");
            } else {
                mostrarDocBuscadoTabla(docBuscadoPublic);
            }
        }
    }//GEN-LAST:event_jBtnBuscarDocPublicActionPerformed

    private void jBtnPublicarDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnPublicarDocActionPerformed
        DefaultTableModel dtm = null;
        String nroDocRegistros = "";
        bandMensaje = false;
        try {
            dtm = (DefaultTableModel) jTblPublicaciones.getModel();

            int registrosMarcados = contarRegistrosMarcados(dtm, 5);//Columna que contiene los check

            if (registrosMarcados > 0) {
                nroDocRegistros = idRegistrosMarcados(dtm);
                // preguntar está seguro que desea eliminar las transaciones
                int ret = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea publicar los documentos seleccionados?: \n" + nroDocRegistros.replaceAll(",", "\n"), "Advertencia", JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.YES_OPTION) {
                    String[] arrayIdRegistros = nroDocRegistros.split(",");
                    hiloClasePublicacion = new Thread(new ClasePublicacion(arrayIdRegistros), "PublicWS");
                    hiloClasePublicacion.start();
                    hiloClaseVentanaMensajeEspera = new Thread(new ClaseVentanaMensajeEspera(), "PublicWS");
                    hiloClaseVentanaMensajeEspera.start();
                    jTxfBuscarDocPublic.setText("");
                    jChkSeleccionarTodoPublic.setSelected(false);

                }
            } else if (registrosMarcados == -1) {
                JOptionPane.showMessageDialog(this, "No hay documentos en tabla de Publicaciones.");
                LoggerTrans.getCDMainLogger().log(Level.INFO, "No hay documentos en tabla.");
            } else {
                JOptionPane.showMessageDialog(this, "Debe marcar el documento que desea publicar.");
                LoggerTrans.getCDMainLogger().log(Level.INFO, "Debe marcar el documento que desea publicar.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al publicar: " + ex.getMessage());
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al publicar: {0}", ex.getMessage());
        }
    }//GEN-LAST:event_jBtnPublicarDocActionPerformed

    private void jChkSeleccionarTodoPublicMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jChkSeleccionarTodoPublicMouseClicked
        if (jChkSeleccionarTodoPublic.isSelected()) {
            for (int i = 0; i < jTblPublicaciones.getRowCount(); i++) {
                jTblPublicaciones.getModel().setValueAt(true, i, 5);
            }
        } else {
            for (int i = 0; i < jTblPublicaciones.getRowCount(); i++) {
                jTblPublicaciones.getModel().setValueAt(false, i, 5);
            }
        }
    }//GEN-LAST:event_jChkSeleccionarTodoPublicMouseClicked

    private void jBtnForzarActualizacionPublicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnForzarActualizacionPublicActionPerformed
        try {
            jTxfBuscarDocPublic.setText("");
            listarDocPublicacionesEnTabla();
            LoggerTrans.getCDMainLogger().log(Level.INFO, "{0} - Se realizó la acción: Forzar Actualización de Tabla de Publicaciones", sdf2.format(new Date()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al forzar actualización de tabla de publicaciones: " + ex.getMessage());
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al forzar actualización de tabla de  publicaciones: {0}", ex.getMessage());
        }
    }//GEN-LAST:event_jBtnForzarActualizacionPublicActionPerformed

    private void btnForzarActualizacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForzarActualizacionActionPerformed
        try {
            listarTransaccionesEnTabla();
            if (valorFeId != null && !valorFeId.equals("")) {
                if (!verificarIdEnTabla(valorFeId)) {
                    verDetalleEnTree("");
                }
            }
            LoggerTrans.getCDMainLogger().log(Level.INFO, "{0} - Se realizó la acción: Forzar Actualización", sdf2.format(new Date()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al forzar actualización: " + ex.getMessage());
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al forzar actualización: {0}", ex.getMessage());
        }
    }//GEN-LAST:event_btnForzarActualizacionActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        DefaultTableModel dtm = null;
        String idRegistros = "";
        try {
            // se toma el model de la tabla actual, ya que este puede
            // variar el orden al insertarse otro registro, por motivo de la actualización automática
            dtm = (DefaultTableModel) tblTransacciones.getModel();

            int registrosMarcados = contarRegistrosMarcados(dtm, 5);//Columna que contiene los check

            if (registrosMarcados > 0) {
                idRegistros = idRegistrosMarcados(dtm);
                // preguntar está seguro que desea eliminar las transaciones
                int ret = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar las transacciones seleccionadas?: \n" + idRegistros.replaceAll(",", "\n"), "Advertencia", JOptionPane.YES_NO_OPTION);
                if (ret == JOptionPane.YES_OPTION) {
                    String[] arrayIdRegistros = idRegistros.split(",");

                    for (String arrayIdRegistro : arrayIdRegistros) {
                        TransaccionBL.Eliminar(new Transaccion(arrayIdRegistro));
                    }
                    // se refresca la tabla
                    listarTransaccionesEnTabla();
                    // se refresca tree
                    verDetalleEnTree("");

                    JOptionPane.showMessageDialog(this, "Se eliminó las transacciones marcadas.");
                    LoggerTrans.getCDMainLogger().log(Level.INFO, "{0} - Se realizó la acción: Eliminar, en los Id: {1}.", new Object[]{sdf2.format(new Date()), idRegistros});
                }
            } else if (registrosMarcados == -1) {
                JOptionPane.showMessageDialog(this, "No hay registros en tabla.");
                LoggerTrans.getCDMainLogger().log(Level.INFO, "No hay registros en tabla.");
            } else {
                JOptionPane.showMessageDialog(this, "Debe marcar el registro que desea eliminar.");
                LoggerTrans.getCDMainLogger().log(Level.INFO, "Debe marcar el registro que desea eliminar.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al eliminar: {0}", ex.getMessage());
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void chkSeleccionarTodoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chkSeleccionarTodoMouseClicked
        if (chkSeleccionarTodo.isSelected()) {
            for (int i = 0; i < tblTransacciones.getRowCount(); i++) {
                tblTransacciones.getModel().setValueAt(true, i, 5);
            }
        } else {
            for (int i = 0; i < tblTransacciones.getRowCount(); i++) {
                tblTransacciones.getModel().setValueAt(false, i, 5);
            }
        }
    }//GEN-LAST:event_chkSeleccionarTodoMouseClicked

    private void btnVerDetalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerDetalleActionPerformed
        DefaultTableModel dtm = null;
        String idRegistro = "";
        try {
            dtm = (DefaultTableModel) tblTransacciones.getModel();
            //Columna que contiene los check = 5
            switch (contarRegistrosMarcados(dtm, 5)) {
                case 1:
                    for (int i = 0; i < dtm.getRowCount(); i++) {
                        if (dtm.getValueAt(i, 5).equals(true)) {
                            idRegistro = dtm.getValueAt(i, 0).toString();
                            break;
                        }
                    }
                    break;
                case 0:
                    JOptionPane.showMessageDialog(this, "Debe marcar el registro que desea ver el detalle.");
                    break;
                case -1:
                    JOptionPane.showMessageDialog(this, "No hay registros en tabla.");
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Solo se puede visualizar el detalle de un registro.");
                    break;
            }

            verDetalleEnTree(idRegistro);
            LoggerTrans.getCDMainLogger().log(Level.INFO, "{0} - Se realizó la acción: Ver Detalle", sdf2.format(new Date()));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al ver detalle: " + ex.getMessage());
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al ver detalle: {0}", ex.getMessage());
        }
    }//GEN-LAST:event_btnVerDetalleActionPerformed

    private void tblTransaccionesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTransaccionesMouseReleased
        switch (evt.getButton()) {
            case MouseEvent.BUTTON1: // click principal
                // se obtiene el indice de la fila seleccionada
                int row = tblTransacciones.rowAtPoint(evt.getPoint());
                // se asigna a la columna valor cero para obtener el valor de la columna fe_id
                int col = 0;

                if (row >= 0) {
                    String valor = tblTransacciones.getValueAt(row, col).toString();
                    valorFeId = valor;
                    verDetalleEnTree(valor);
                }
                break;
            case MouseEvent.BUTTON2: //click central
                break;
            case MouseEvent.BUTTON3: //click secundario
                //seleccionar fila con click secundario
                int idx = tblTransacciones.rowAtPoint(evt.getPoint());
                tblTransacciones.getSelectionModel().setSelectionInterval(idx, idx);
                //mostrar el componente popupmenu
                if (evt.isPopupTrigger()) {
                    jPopupMenu1.show(evt.getComponent(), evt.getX(), evt.getY());
                }
                break;
        }
    }//GEN-LAST:event_tblTransaccionesMouseReleased

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        String dirAdjuntos = txtExportar.getText();
        JFileChooser fc = new JFileChooser(dirAdjuntos);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(false);
        int ret = fc.showDialog(this, "Seleccionar");
        if (ret == JFileChooser.APPROVE_OPTION) {
            txtExportar.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if (txtExportar.getText().equalsIgnoreCase("") || txtExportar.getText().equalsIgnoreCase(null)) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar un destino de exportar", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            FileOutputStream out = null;
            try {
                // TODO add your handling code here:

                Workbook wb = new HSSFWorkbook();
                CreationHelper createhelper = wb.getCreationHelper();
                Sheet sheet = wb.createSheet("Reporte");
                Row row = null;
                Cell cell = null;
                int rowCount = modelo.getRowCount();
                int rowNum;
                int colNum;
                int tempRows;
                int columnCount = modelo.getColumnCount();

                // create the headers
                for (colNum = 0; colNum < columnCount; colNum++) {
                    CellStyle style = wb.createCellStyle();//Create style
                    Font font = wb.createFont();//Create font
                    font.setBold(true);//Make font bold
                    style.setFont(font);//set it to bold

                    if (colNum == 0) {
                        row = sheet.createRow(0);
                    }
                    cell = row.createCell(colNum);
                    cell.setCellValue(modelo.getColumnName(colNum));
                    row.getCell(colNum).setCellStyle(style);
                }

                sheet.autoSizeColumn(0);
                sheet.autoSizeColumn(1);
                sheet.autoSizeColumn(2);
                sheet.autoSizeColumn(3);
                sheet.autoSizeColumn(4);
                sheet.autoSizeColumn(5);

                // create the body
                for (int i = 0; i < modelo.getRowCount(); i++) {
                    row = sheet.createRow(i + 1);
                    for (int j = 0; j < modelo.getColumnCount(); j++) {

                        cell = row.createCell(j);
                        if (modelo.getValueAt(i, j) != null) {

                            //System.out.println(modelo.getValueAt(i, j).getClass().getName());
                            if (modelo.getValueAt(i, j).getClass().getName().equalsIgnoreCase("java.math.BigDecimal")) {
                                BigDecimal bd = (BigDecimal) modelo.getValueAt(i, j);
                                cell.setCellValue(bd.doubleValue());
                            } else {
                                cell.setCellValue(modelo.getValueAt(i, j).toString());

                            }

                        }

                    }
                }
                Calendar calendario = Calendar.getInstance();
                String pathfull = txtExportar.getText() + File.separator + "Reporte_" + calendario.get(Calendar.YEAR) + "_" + calendario.get(Calendar.MONTH + 1) + "_" + calendario.get(Calendar.DATE) + "_" + calendario.get(Calendar.HOUR_OF_DAY) + "_" + calendario.get(Calendar.MINUTE) + "_" + calendario.get(Calendar.SECOND) + ".xls";
                out = new FileOutputStream(pathfull);
                wb.write(out);
                out.close();
                JOptionPane.showMessageDialog(null, "Se exporto correctamente en " + pathfull, "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Error " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if (fechaFin.getDate() == null || fechaInicio.getDate() == null) {
            if (fechaFin.getDate() == null) {

                JOptionPane.showMessageDialog(null, "Verificar la fecha de inicio", "Error", JOptionPane.ERROR_MESSAGE);

            } else {
                if (fechaInicio.getDate() == null) {

                    JOptionPane.showMessageDialog(null, "Verificar la fecha fin", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {

            Calendar calInicio = Calendar.getInstance();
            Calendar calFin = Calendar.getInstance();
            calInicio.setTime(fechaInicio.getDate());
            calFin.setTime(fechaFin.getDate());
            if (calFin.after(calInicio)) {
                JOptionPane.showMessageDialog(null, "Verificar el orden de las fechas", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dateInicio = fechaFin.getDate();
                Date dateFin = fechaInicio.getDate();
                SwingWorker<List<Reporte>, Void> worker = new SwingWorker<List<Reporte>, Void>() {
                    @Override
                    protected List<Reporte> doInBackground() throws Exception {
                        return DocumentoDAO.extraerReporte(dateFormat.format(dateInicio), dateFormat.format(dateFin));
                    }

                    @Override
                    protected void done() {
                        try {
                            llenarTabla(get());
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                };
                worker.execute();
            }

        }

    }//GEN-LAST:event_jButton5ActionPerformed

    public class ClasePublicacion implements Runnable {

        private String[] arrayIdRegistros;

        public ClasePublicacion(String[] arrayIdRegistros) {
            this.arrayIdRegistros = arrayIdRegistros;
        }

        @Override
        public void run() {
            for (String arrayIdRegistro : arrayIdRegistros) {
                PublicardocWs docSeleccionado = obtenerDocMarcado(arrayIdRegistro);
                try {
                    Optional<PublicardocWs> optional = Optional.ofNullable(docSeleccionado);
                    if (optional.isPresent()) {
                        docSeleccionado.setEstadoPublicacion('A');
                        PublicardocWsJC.edit(docSeleccionado);
                    }
                } catch (Exception ex) {
                    LoggerTrans.getCDMainLogger().log(Level.INFO, "Error al publicar documento en el portal: " + ex.getMessage());
                }
                listarDocPublicacionesEnTabla();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            }
            bandMensaje = true;
            ventanaMensajeEspera.setVisible(false);
            LoggerTrans.getCDMainLogger().log(Level.INFO, "Finaliza publicación de documento");
        }
    }

    public static class ClaseVentanaMensajeEspera implements Runnable {

        boolean flag = false;

        public ClaseVentanaMensajeEspera() {
        }

        @Override
        public void run() {
            bandMensaje = false;
            while (!bandMensaje) {
                if (!flag) {
                    LoggerTrans.getCDMainLogger().log(Level.INFO, "Inicia ventana de espera.");
                    flag = true;
                    ventanaMensajeEspera.setVisible(true);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    Thread hiloClasePublicacion;

    Thread hiloClaseVentanaMensajeEspera;

    private void mostrarDocBuscadoTabla(PublicardocWs docBuscadoPublic) {
        DefaultTableModel dtm = null;
        JCheckBox check = new JCheckBox();
        listDocPub = null;
        try {
            dtm = (DefaultTableModel) jTblPublicaciones.getModel();
            while (dtm.getRowCount() > 0) {
                dtm.removeRow(0);
            }
            DateFormat formatoFechaEmision = new SimpleDateFormat("dd/MM/yyyy");
            dtm.addRow(new Object[]{
                    docBuscadoPublic.getDOCId(),
                    docBuscadoPublic.getSNDocIdentidadNro(),
                    docBuscadoPublic.getSNRazonSocial(),
                    formatoFechaEmision.format(docBuscadoPublic.getDOCFechaEmision()),
                    docBuscadoPublic.getDOCMontoTotal() + " " + docBuscadoPublic.getDOCMONNombre(),
                    false});
            jTblPublicaciones.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(check));
            jTblPublicaciones.getColumnModel().getColumn(5).setCellRenderer(new Render_CheckBox());

        } catch (Exception ex) {
            throw ex;
        }
    }

    private void listarTransaccionesEnTabla() {
        DefaultTableModel dtm = null;
        JCheckBox check = new JCheckBox();
        try {
            // se obtiene el model del componente tabla para agregar filas
            dtm = (DefaultTableModel) tblTransacciones.getModel();
            // se limpia tabla
            while (dtm.getRowCount() > 0) {
                dtm.removeRow(0);
            }
            // se lista transacciones
            List<Transaccion> tcs = TransaccionBL.ListarAll();
            // se agrega nuevas filas
            for (Transaccion tc : tcs) {
                dtm.addRow(new Object[]{
                        tc.getFEId(),
                        tc.getFEDocEntry(),
                        tc.getFEDocNum(),
                        tc.getDOCCodigo(),
                        tc.getFEEstado(),
                        false});
            }

            tblTransacciones.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(check));
            tblTransacciones.getColumnModel().getColumn(5).setCellRenderer(new Render_CheckBox());

        } catch (Exception ex) {
            throw ex;
        }

    }

    private void listarDocPublicacionesEnTabla() {
        DefaultTableModel dtm = null;
        JCheckBox check = new JCheckBox();
        listDocPub = null;
        try {
            // se obtiene el model del componente tabla para agregar filas
            dtm = (DefaultTableModel) jTblPublicaciones.getModel();
            // se limpia tabla
            while (dtm.getRowCount() > 0) {
                dtm.removeRow(0);
            }
            // se lista doc. pendientes de publicacion
            listDocPub = PublicardocWsBL.listarHabilitadas();
            // se agrega nuevas filas
            for (PublicardocWs docPub : listDocPub) {
                DateFormat formatoFechaEmision = new SimpleDateFormat("dd/MM/yyyy");
                dtm.addRow(new Object[]{
                        docPub.getDOCId(),
                        docPub.getSNDocIdentidadNro(),
                        docPub.getSNRazonSocial(),
                        formatoFechaEmision.format(docPub.getDOCFechaEmision()),
                        docPub.getDOCMontoTotal() + " " + docPub.getDOCMONNombre(),
                        false});
            }

            jTblPublicaciones.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(check));
            jTblPublicaciones.getColumnModel().getColumn(5).setCellRenderer(new Render_CheckBox());

        } catch (Exception ex) {
            throw ex;
        }
    }

    private PublicardocWs obtenerDocMarcado(String nroDocMarcado) {
        for (PublicardocWs docPub : listDocPub) {
            if (docPub.getDOCId().equals(nroDocMarcado)) {
                return docPub;
            }
        }
        return null;
    }

    private boolean verificarIdEnTabla(String id) {
        DefaultTableModel dtm = null;
        boolean respuesta = false;
        try {
            dtm = (DefaultTableModel) tblTransacciones.getModel();
            int registrosEnTabla = dtm.getRowCount();

            if (registrosEnTabla > 0) {
                for (int i = 0; i < registrosEnTabla; i++) {
                    if (dtm.getValueAt(i, 0).equals(id)) {
                        respuesta = true;
                        break;
                    }
                }
            }

            return respuesta;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void ejecutarGestorXTiempo() {

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r);
                    }
                });

        sf1 = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    listarTransaccionesEnTabla();
                    if (jTxfBuscarDocPublic.getText().equals("")) {
                        listarDocPublicacionesEnTabla();
                    }
                    if (valorFeId != null && !valorFeId.equals("")) {
                        if (!verificarIdEnTabla(valorFeId)) {
                            verDetalleEnTree("");
                        }
                    }
                } catch (Exception ex) {
                    LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "EjecutarGestorXTiempo: {0}", new Object[]{ex.getMessage()});
                }
            }
        }, 0, Integer.parseInt(GestorTiempo.periodo), TimeUnit.SECONDS);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws JAXBException {



        /* if (!InstanciaUnica()) {
         JOptionPane.showMessageDialog(null, "Existe otra instancia de la aplicación ejectandose.");
         return;
         }*/

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                } else {
                    javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmMonitor.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmMonitor.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmMonitor.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmMonitor.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        final Configuracion configuracion = FrmMonitor.loadConfiguracion();
        SpringApplication application = new SpringApplication(Programa.class);
        application.setBanner(new ResourceBanner(new ClassPathResource("org/ventura/cpe/iconos/banner.txt")));
        applicationContext = application.run(args);
        java.awt.EventQueue.invokeLater(() -> new FrmMonitor(configuracion));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEliminar;

    private javax.swing.JButton btnForzarActualizacion;

    private javax.swing.JButton btnVerDetalle;

    private javax.swing.JCheckBox chkSeleccionarTodo;

    private javax.swing.JComboBox<String> cmbSociedades;

    private javax.swing.JButton jBtnBuscarDocPublic;

    private javax.swing.JButton jBtnForzarActualizacionPublic;

    private javax.swing.JButton jBtnPublicarDoc;

    private javax.swing.JButton jButton3;

    private javax.swing.JButton jButton5;

    private javax.swing.JButton jButton6;

    private javax.swing.JButton jButton7;

    private javax.swing.JCheckBox jChkSeleccionarTodoPublic;

    private javax.swing.JLabel jLabel1;

    private javax.swing.JLabel jLabel2;

    private javax.swing.JLabel jLabel3;

    private javax.swing.JMenuItem jMItmAcercaDe;

    private javax.swing.JMenu jMenu1;

    private javax.swing.JMenu jMenu2;

    private javax.swing.JMenu jMenu3;

    private javax.swing.JMenu jMenu4;

    private javax.swing.JMenu jMenu5;

    private javax.swing.JMenuBar jMenuBar1;

    private javax.swing.JMenuItem jMenuItem1;

    private javax.swing.JMenuItem jMenuItem3;

    private javax.swing.JPanel jPanel3;

    private javax.swing.JPanel jPanel4;

    private javax.swing.JPanel jPanel5;

    private javax.swing.JPopupMenu jPopupMenu1;

    private javax.swing.JScrollPane jScrollPane1;

    private javax.swing.JScrollPane jScrollPane2;

    private javax.swing.JScrollPane jScrollPane3;

    private javax.swing.JScrollPane jScrollPane4;

    private javax.swing.JSeparator jSeparator1;

    private javax.swing.JTabbedPane jTabbedPane1;

    private javax.swing.JTable jTable1;

    private javax.swing.JTable jTblPublicaciones;

    private javax.swing.JTree jTree1;

    private javax.swing.JTextField jTxfBuscarDocPublic;

    private javax.swing.JLabel lblTexto;

    private javax.swing.JLabel lblVersion;

    private javax.swing.JMenuItem mnCatalogo8;

    private javax.swing.JMenuItem mnConfiguracion;

    private javax.swing.JMenuItem mnDetener;

    private javax.swing.JMenuItem mnIniciar;

    private javax.swing.JMenuItem mnListaErrores;

    private javax.swing.JMenuItem mnLogViewer;

    private javax.swing.JMenuItem mnManualUsuario;

    private javax.swing.JMenuItem mnProcedimientosAux;

    private javax.swing.JLabel mnSociedad;

    private javax.swing.JMenuItem mnsb1ManualUsuario;

    private javax.swing.JMenuItem mnsbConfiguracion;

    private javax.swing.JMenuItem mnuEliminar;

    private javax.swing.JMenuItem mnuForzarActualización;

    private javax.swing.JMenuItem mnuSeleccionarTodo;

    private javax.swing.JTable tblTransacciones;

    private javax.swing.JTextField txtExportar;
    // End of variables declaration//GEN-END:variables

    private static boolean InstanciaUnica() {
        try {
            socket = new ServerSocket(34567);
            Thread N = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket.accept();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } finally {
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            });
            N.start();
            return true;
        } catch (IOException ex) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                }
            }
            return false;
        }
    }

    private String idRegistrosMarcados(DefaultTableModel dtm) {
        String idRegistros = "";
        for (int i = 0; i < dtm.getRowCount(); i++) {
            if (dtm.getValueAt(i, 5).equals(true)) {
                if (idRegistros.length() == 0) {
                    idRegistros = dtm.getValueAt(i, 0).toString();
                } else {
                    idRegistros = dtm.getValueAt(i, 0).toString() + "," + idRegistros;
                }
            }
        }
        return idRegistros;
    }

    private void listarLogs() {
        //recolectar archivos de log por fecha
        SortedMap<String, String> logFiles = new TreeMap<String, String>(Collections.reverseOrder());
        String sRutaConfigMain = System.getProperty("user.dir");
        String sRutaConfigGeneral[] = sRutaConfigMain.split("[\\\\/]", -1);
        sRutaConfigMain = "";
        for (int i = 0; i < sRutaConfigGeneral.length - 1; i++) {
            sRutaConfigMain = sRutaConfigMain + sRutaConfigGeneral[i] + File.separator;
        }
        try {

            File f = new File(sRutaConfigMain + "\\logs\\");
            File[] files = f.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    String f1 = file.getName();
                    String[] fec1 = f1.split("-");
                    try {
                        Date fecha = LoggerTrans.d.parse(fec1[2] + fec1[1] + fec1[0]);
                        String fecha1 = LoggerTrans.d1.format(fecha);
                        logFiles.put(fecha1, fecha1);
                    } catch (ParseException pe1) {

                    }
                }
            }
        } catch (Exception ex) {
            LoggerTrans.getCDMainLogger().log(Level.WARNING, "listarLogs. {0}", ex.getMessage());
        }
        //llenar tabla con nuevos
        Iterator<String> iValores = logFiles.keySet().iterator();
        while (jMenu5.getItemCount() > 1) {
            jMenu5.remove(1);
        }
        int contRegLog = 1;
        while (iValores.hasNext() && contRegLog <= 15) {
            String val = iValores.next();
            JMenuItem newItem = new JMenuItem();
            newItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/ventura/cpe/iconos/log-icon.png")));
            newItem.setText(val);
            newItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String fecha = e.getActionCommand();
                    fecha = fecha.replaceAll("-", "");
                    try {
                        int logsEncontrados = 0;
                        String date = fecha.substring(6, 8) + "-" + fecha.substring(4, 6) + "-" + fecha.substring(0, 4);
                        String sRutaLog = System.getProperty("user.dir");
                        String sRutaGeneral[] = sRutaLog.split("[\\\\/]", -1);
                        sRutaLog = "";
                        for (int i = 0; i < sRutaGeneral.length - 1; i++) {
                            sRutaLog = sRutaLog + sRutaGeneral[i] + File.separator;
                        }
                        String command = "olv-1.3.2/olv.bat -tail";
                        String[] logMain = {sRutaLog + "\\logs" + File.separator + date + "\\" + fecha + "Main_Main.log",
                                sRutaLog + "\\logs" + File.separator + date + "\\" + fecha + "Request_Request.log",
                                sRutaLog + "\\logs" + File.separator + date + "\\" + fecha + "Extractor_Extractor.log",
                                sRutaLog + "\\logs" + File.separator + date + "\\" + fecha + "Response_Response.log",
                                sRutaLog + "\\logs" + File.separator + date + "\\" + fecha + "PublicWS_PublicWS.log",
                                sRutaLog + "\\logs" + File.separator + date + "\\" + fecha + "Resumen_Resumen.log"};
                        LoggerTrans.getCDMainLogger().log(Level.INFO, "Abrir Logger: " + logMain);
                        for (String itemMain : logMain) {
                            File f = new File(itemMain);
                            if (f.exists()) {
                                logsEncontrados++;
                                command += " " + itemMain;
                            }
                        }
                        command = sRutaLog + "\\" + command;
                        if (logsEncontrados > 0) {
                            Runtime.getRuntime().exec(command);
                        } else {
                            JOptionPane.showMessageDialog(null, "No se encontro archivos logs.");
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                        LoggerTrans.getCDMainLogger().log(Level.INFO, "Abrir OtroLogger. {0}", ex.getMessage());
                    }

                }

            });
            if (jMenu5.getItemCount() <= 15) {
                jMenu5.add(newItem);
                contRegLog++;
            }
        }
    }

    private static Configuracion loadConfiguracion() throws JAXBException {
        String sRutaConfigReal = System.getProperty("user.dir");
        String[] sRutaConfigGeneral = sRutaConfigReal.split("[\\\\/]", -1);
        sRutaConfigReal = "";
        for (int i = 0; i < sRutaConfigGeneral.length - 1; i++) {
            sRutaConfigReal = sRutaConfigReal + sRutaConfigGeneral[i] + File.separator;
        }
        String inXmlFile = sRutaConfigReal + "Config.xml";
        File file = new File(inXmlFile);
        JAXBContext jaxbContext = JAXBContext.newInstance(Configuracion.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Configuracion configuracion = (Configuracion) jaxbUnmarshaller.unmarshal(file);
        return configuracion;
    }
}
