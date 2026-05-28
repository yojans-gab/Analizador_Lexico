package minilang;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

public class GUI extends JFrame {

    // ── Tema actual ───────────────────────────────────────────────────────────
    private boolean modoOscuro = true;

    // ── Paletas de colores ────────────────────────────────────────────────────
    // OSCURO
    private static final Color D_BG_DARK    = new Color(30, 31, 34);
    private static final Color D_BG_PANEL   = new Color(40, 42, 46);
    private static final Color D_BG_TABLE   = new Color(45, 47, 52);
    private static final Color D_ACCENT     = new Color(97, 175, 239);
    private static final Color D_SUCCESS    = new Color(152, 195, 121);
    private static final Color D_DANGER     = new Color(224, 108, 117);
    private static final Color D_WARNING    = new Color(229, 192, 123);
    private static final Color D_TEXT_MAIN  = new Color(200, 200, 200);
    private static final Color D_TEXT_MUTED = new Color(120, 125, 135);
    private static final Color D_BORDER     = new Color(60, 63, 68);
    private static final Color D_EDITOR_BG  = new Color(25, 27, 30);
    private static final Color D_EDITOR_FG  = new Color(171, 178, 191);
    private static final Color D_LINENUM_BG = new Color(33, 35, 39);
    private static final Color D_LINENUM_FG = new Color(80, 85, 95);

    // CLARO
    private static final Color L_BG_DARK    = new Color(245, 246, 248);
    private static final Color L_BG_PANEL   = new Color(255, 255, 255);
    private static final Color L_BG_TABLE   = new Color(252, 252, 253);
    private static final Color L_ACCENT     = new Color(30, 100, 200);
    private static final Color L_SUCCESS    = new Color(39, 120, 60);
    private static final Color L_DANGER     = new Color(180, 40, 40);
    private static final Color L_WARNING    = new Color(160, 100, 0);
    private static final Color L_TEXT_MAIN  = new Color(30, 30, 35);
    private static final Color L_TEXT_MUTED = new Color(120, 125, 140);
    private static final Color L_BORDER     = new Color(210, 213, 220);
    private static final Color L_EDITOR_BG  = new Color(250, 251, 252);
    private static final Color L_EDITOR_FG  = new Color(40, 44, 52);
    private static final Color L_LINENUM_BG = new Color(238, 240, 244);
    private static final Color L_LINENUM_FG = new Color(150, 155, 165);

    // ── Colores activos (cambian con el tema) ─────────────────────────────────
    private Color BG_DARK, BG_PANEL, BG_TABLE, ACCENT, SUCCESS, DANGER,
            WARNING, TEXT_MAIN, TEXT_MUTED, BORDER_COL,
            EDITOR_BG, EDITOR_FG, LINENUM_BG, LINENUM_FG;

    // ── Componentes que se repintan al cambiar tema ───────────────────────────
    private JTextArea  editorArea, lineNums;
    private JLabel     lblArchivo, lblTokens, lblIdentificadores,
            lblErrores, lblEstado;
    private JPanel     headerPanel, statusPanel, editorPanel,
            resultPanel, cardPanel, tabsPanel;
    private JButton    btnAnalizar, btnAbrir, btnTema,
            btnTok, btnSim, btnLex, btnSint, btnSem;
    private JScrollPane editorScroll;

    private DefaultTableModel modelTokens, modelSimbolos,
            modelErrLex, modelErrSint, modelErrSem;
    private JTable tablaTokens, tablaSimbolos,
            tablaErrLex, tablaErrSint, tablaErrSem;
    private JScrollPane spTokens, spSimbolos, spErrLex, spErrSint, spErrSem;

    private CardLayout cardLayout;
    private JButton tabActivo;

    // ── Constructor ───────────────────────────────────────────────────────────
    public GUI() {
        setTitle("Compilador TurboX");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        aplicarPaleta();
        setLayout(new BorderLayout());
        buildUI();
    }

    // ── Aplica la paleta correcta según el modo ───────────────────────────────
    private void aplicarPaleta() {
        if (modoOscuro) {
            BG_DARK    = D_BG_DARK;    BG_PANEL   = D_BG_PANEL;
            BG_TABLE   = D_BG_TABLE;   ACCENT     = D_ACCENT;
            SUCCESS    = D_SUCCESS;    DANGER     = D_DANGER;
            WARNING    = D_WARNING;    TEXT_MAIN  = D_TEXT_MAIN;
            TEXT_MUTED = D_TEXT_MUTED; BORDER_COL = D_BORDER;
            EDITOR_BG  = D_EDITOR_BG;  EDITOR_FG  = D_EDITOR_FG;
            LINENUM_BG = D_LINENUM_BG; LINENUM_FG = D_LINENUM_FG;
        } else {
            BG_DARK    = L_BG_DARK;    BG_PANEL   = L_BG_PANEL;
            BG_TABLE   = L_BG_TABLE;   ACCENT     = L_ACCENT;
            SUCCESS    = L_SUCCESS;    DANGER     = L_DANGER;
            WARNING    = L_WARNING;    TEXT_MAIN  = L_TEXT_MAIN;
            TEXT_MUTED = L_TEXT_MUTED; BORDER_COL = L_BORDER;
            EDITOR_BG  = L_EDITOR_BG;  EDITOR_FG  = L_EDITOR_FG;
            LINENUM_BG = L_LINENUM_BG; LINENUM_FG = L_LINENUM_FG;
        }
    }

    // ── Construye toda la UI ──────────────────────────────────────────────────
    private void buildUI() {
        getContentPane().removeAll();
        getContentPane().setBackground(BG_DARK);

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    // ── Cambia el tema y repinta todo ─────────────────────────────────────────
    private void toggleTema() {
        modoOscuro = !modoOscuro;
        aplicarPaleta();
        buildUI();
        if (tabActivo != null) resaltarTab(tabActivo);
    }

    // ── HEADER ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        headerPanel = darkPanel(new BorderLayout(16, 0));
        headerPanel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_COL),
                new EmptyBorder(12, 20, 12, 20)
        ));

        // Título + botón tema
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        izq.setOpaque(false);

        JLabel title = new JLabel("Compilador TurboX");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(TEXT_MAIN);

        btnTema = new JButton(modoOscuro ? "☀ Modo Claro" : "☾ Modo Oscuro");
        btnTema.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnTema.setBackground(BG_PANEL);
        btnTema.setForeground(ACCENT);
        btnTema.setFocusPainted(false);
        btnTema.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL),
                new EmptyBorder(4, 12, 4, 12)
        ));
        btnTema.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTema.addActionListener(e -> toggleTema());

        izq.add(title);
        izq.add(btnTema);

        // Stats
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 28, 0));
        stats.setOpaque(false);
        lblTokens          = statLabel("0", "TOKENS");
        lblIdentificadores = statLabel("0", "SÍMBOLOS");
        lblErrores         = statLabel("0", "ERRORES");
        stats.add(lblTokens);
        stats.add(lblIdentificadores);
        stats.add(lblErrores);

        headerPanel.add(izq,   BorderLayout.WEST);
        headerPanel.add(stats, BorderLayout.EAST);
        return headerPanel;
    }

    private JLabel statLabel(String val, String label) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);
        JLabel lVal = new JLabel(val, SwingConstants.CENTER);
        lVal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lVal.setForeground(label.equals("ERRORES") ? DANGER : ACCENT);
        lVal.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lLbl = new JLabel(label, SwingConstants.CENTER);
        lLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lLbl.setForeground(TEXT_MUTED);
        lLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(lVal); box.add(lLbl);
        return lVal;
    }

    // ── CENTRO ────────────────────────────────────────────────────────────────
    private JSplitPane buildCenter() {
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                buildEditorPanel(),
                buildResultsPanel()
        );
        split.setDividerLocation(440);
        split.setDividerSize(4);
        split.setBorder(null);
        split.setBackground(BG_DARK);
        return split;
    }

    // ── EDITOR ────────────────────────────────────────────────────────────────
    private JPanel buildEditorPanel() {
        editorPanel = darkPanel(new BorderLayout(0, 8));
        editorPanel.setBorder(new EmptyBorder(12, 16, 12, 8));

        // Barra superior
        JPanel barra = new JPanel(new BorderLayout(8, 0));
        barra.setOpaque(false);
        lblArchivo = new JLabel("Sin archivo cargado");
        lblArchivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblArchivo.setForeground(TEXT_MUTED);
        btnAbrir = accentButton("Abrir .txt");
        btnAbrir.addActionListener(e -> abrirArchivo());
        barra.add(lblArchivo, BorderLayout.CENTER);
        barra.add(btnAbrir,   BorderLayout.EAST);

        // Editor de código
        String textoActual = (editorArea != null)
                ? editorArea.getText() : EJEMPLO_PROGRAMA;
        editorArea = new JTextArea(textoActual);
        editorArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
        editorArea.setBackground(EDITOR_BG);
        editorArea.setForeground(EDITOR_FG);
        editorArea.setCaretColor(modoOscuro ? Color.WHITE : Color.BLACK);
        editorArea.setLineWrap(false);
        editorArea.setBorder(new EmptyBorder(10, 12, 10, 12));
        editorArea.setTabSize(4);
        editorArea.setSelectionColor(modoOscuro
                ? new Color(70, 90, 130) : new Color(180, 210, 255));

        lineNums = new JTextArea("1");
        lineNums.setBackground(LINENUM_BG);
        lineNums.setForeground(LINENUM_FG);
        lineNums.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
        lineNums.setEditable(false);
        lineNums.setBorder(new EmptyBorder(10, 8, 10, 8));

        editorScroll = new JScrollPane(editorArea);
        editorScroll.setBorder(BorderFactory.createLineBorder(BORDER_COL));
        editorScroll.getViewport().setBackground(EDITOR_BG);
        editorScroll.setRowHeaderView(lineNums);

        editorArea.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e)  { updateLineNumbers(); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e)  { updateLineNumbers(); }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { updateLineNumbers(); }
                });
        updateLineNumbers();

        // Botón analizar
        btnAnalizar = new JButton("▶  Ejecutar análisis completo");
        btnAnalizar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAnalizar.setBackground(ACCENT);
        btnAnalizar.setForeground(modoOscuro
                ? new Color(25, 27, 30) : Color.WHITE);
        btnAnalizar.setFocusPainted(false);
        btnAnalizar.setBorderPainted(false);
        btnAnalizar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAnalizar.setBorder(new EmptyBorder(10, 0, 10, 0));
        btnAnalizar.addActionListener(e -> ejecutarAnalisis());

        editorPanel.add(barra,       BorderLayout.NORTH);
        editorPanel.add(editorScroll, BorderLayout.CENTER);
        editorPanel.add(btnAnalizar,  BorderLayout.SOUTH);
        return editorPanel;
    }

    // ── PANEL DE RESULTADOS ───────────────────────────────────────────────────
    private JPanel buildResultsPanel() {
        resultPanel = darkPanel(new BorderLayout());
        resultPanel.setBorder(new EmptyBorder(12, 8, 12, 16));

        // Tabs
        tabsPanel = new JPanel(new GridLayout(1, 5, 4, 0));
        tabsPanel.setOpaque(false);
        tabsPanel.setBorder(new EmptyBorder(0, 0, 8, 0));

        btnTok  = tabButton("Tokens");
        btnSim  = tabButton("Símbolos");
        btnLex  = tabButton("Err. Léxico");
        btnSint = tabButton("Err. Sintáctico");
        btnSem  = tabButton("Err. Semántico");

        tabsPanel.add(btnTok);
        tabsPanel.add(btnSim);
        tabsPanel.add(btnLex);
        tabsPanel.add(btnSint);
        tabsPanel.add(btnSem);

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        // Crear tablas
        spTokens  = buildTablaPanel(initTablaTokens());
        spSimbolos= buildTablaPanel(initTablaSimbolos());
        spErrLex  = buildTablaPanel(initTablaErrLex());
        spErrSint = buildTablaPanel(initTablaErrSint());
        spErrSem  = buildTablaPanel(initTablaErrSem());

        cardPanel.add(spTokens,   "tokens");
        cardPanel.add(spSimbolos, "simbolos");
        cardPanel.add(spErrLex,   "errlex");
        cardPanel.add(spErrSint,  "errsint");
        cardPanel.add(spErrSem,   "errsem");

        btnTok.addActionListener(e  -> { cardLayout.show(cardPanel,"tokens");   resaltarTab(btnTok); });
        btnSim.addActionListener(e  -> { cardLayout.show(cardPanel,"simbolos"); resaltarTab(btnSim); });
        btnLex.addActionListener(e  -> { cardLayout.show(cardPanel,"errlex");   resaltarTab(btnLex); });
        btnSint.addActionListener(e -> { cardLayout.show(cardPanel,"errsint");  resaltarTab(btnSint); });
        btnSem.addActionListener(e  -> { cardLayout.show(cardPanel,"errsem");   resaltarTab(btnSem); });

        tabActivo = btnTok;
        resaltarTab(btnTok);

        resultPanel.add(tabsPanel, BorderLayout.NORTH);
        resultPanel.add(cardPanel, BorderLayout.CENTER);
        return resultPanel;
    }

    private JScrollPane buildTablaPanel(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COL));
        sp.getViewport().setBackground(BG_TABLE);
        return sp;
    }

    // ── TABLAS ────────────────────────────────────────────────────────────────
    private JTable initTablaTokens() {
        modelTokens = new DefaultTableModel(
                new String[]{"Token","Lexema","Línea","Columna"}, 0
        ) { public boolean isCellEditable(int r,int c){return false;} };
        tablaTokens = styledTable(modelTokens);
        tablaTokens.getColumnModel().getColumn(0).setPreferredWidth(170);
        tablaTokens.getColumnModel().getColumn(1).setPreferredWidth(130);
        tablaTokens.getColumnModel().getColumn(2).setPreferredWidth(55);
        tablaTokens.getColumnModel().getColumn(3).setPreferredWidth(65);
        return tablaTokens;
    }

    private JTable initTablaSimbolos() {
        modelSimbolos = new DefaultTableModel(
                new String[]{"Identificador","Tipo","Línea","Columna"}, 0
        ) { public boolean isCellEditable(int r,int c){return false;} };
        tablaSimbolos = styledTable(modelSimbolos);
        return tablaSimbolos;
    }

    private JTable initTablaErrLex() {
        modelErrLex = new DefaultTableModel(
                new String[]{"Línea","Columna","Descripción"}, 0
        ) { public boolean isCellEditable(int r,int c){return false;} };
        tablaErrLex = styledTable(modelErrLex);
        tablaErrLex.getColumnModel().getColumn(0).setPreferredWidth(55);
        tablaErrLex.getColumnModel().getColumn(1).setPreferredWidth(65);
        tablaErrLex.getColumnModel().getColumn(2).setPreferredWidth(300);
        return tablaErrLex;
    }

    private JTable initTablaErrSint() {
        modelErrSint = new DefaultTableModel(
                new String[]{"#","Descripción"}, 0
        ) { public boolean isCellEditable(int r,int c){return false;} };
        tablaErrSint = styledTable(modelErrSint);
        tablaErrSint.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaErrSint.getColumnModel().getColumn(1).setPreferredWidth(380);
        return tablaErrSint;
    }

    private JTable initTablaErrSem() {
        modelErrSem = new DefaultTableModel(
                new String[]{"#","Descripción"}, 0
        ) { public boolean isCellEditable(int r,int c){return false;} };
        tablaErrSem = styledTable(modelErrSem);
        tablaErrSem.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaErrSem.getColumnModel().getColumn(1).setPreferredWidth(380);
        return tablaErrSem;
    }

    // ── STATUS BAR ────────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        statusPanel = darkPanel(new BorderLayout());
        statusPanel.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, BORDER_COL),
                new EmptyBorder(8, 20, 8, 20)
        ));
        lblEstado = new JLabel("Listo — escribe código o carga un archivo .txt");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(TEXT_MUTED);
        statusPanel.add(lblEstado, BorderLayout.WEST);
        return statusPanel;
    }

    // ── LÓGICA PRINCIPAL ──────────────────────────────────────────────────────
    private void abrirArchivo() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter(
                "Archivos de texto (*.txt)", "txt"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                String contenido = new String(
                        java.nio.file.Files.readAllBytes(f.toPath()));
                editorArea.setText(contenido);
                lblArchivo.setText(f.getName());
                lblArchivo.setForeground(SUCCESS);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo leer el archivo: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void ejecutarAnalisis() {
        modelTokens.setRowCount(0);
        modelSimbolos.setRowCount(0);
        modelErrLex.setRowCount(0);
        modelErrSint.setRowCount(0);
        modelErrSem.setRowCount(0);

        TablaSimbolos ts = TablaSimbolos.getInstance();
        ts.limpiar();
        CupHelper.limpiar();

        String fuente = editorArea.getText();
        Lexer   lexer  = null;
        Parser  parser = null;
        boolean exitoSintactico = false;

        try (Reader reader = new StringReader(fuente)) {
            lexer  = new Lexer(reader);
            parser = new Parser(lexer);
            lexer.setParser(parser);
            parser.parse();
            exitoSintactico = true;
        } catch (Exception ignored) {}

        if (lexer != null) {
            for (Token t : lexer.tokensReconocidos) {
                if (!t.esEOF()) {
                    modelTokens.addRow(new Object[]{
                            t.getTipo(), t.getLexema(),
                            t.getLinea(), t.getColumna()
                    });
                }
            }
            for (Token err : lexer.erroresLexicos) {
                modelErrLex.addRow(new Object[]{
                        err.getLinea(), err.getColumna(),
                        "Carácter no reconocido: '" + err.getLexema() + "'"
                });
            }
        }

        if (parser != null) {
            List<String> errSint = parser.getErroresSintacticos();
            for (int i = 0; i < errSint.size(); i++)
                modelErrSint.addRow(new Object[]{ i+1, errSint.get(i) });
        }

        List<String> errSem = CupHelper.getErroresSemanticos();
        for (int i = 0; i < errSem.size(); i++)
            modelErrSem.addRow(new Object[]{ i+1, errSem.get(i) });

        ts.getTabla().forEach((nombre, tok) -> {
            String tipo = ts.getTipo(nombre);
            if (tipo != null && !tipo.equals("desconocido")
                    && !tipo.equals("programa")) {
                modelSimbolos.addRow(new Object[]{
                        nombre, tipo, tok.getLinea(), tok.getColumna()
                });
            }
        });

        int totalTokens  = modelTokens.getRowCount();
        int totalSimbol  = modelSimbolos.getRowCount();
        int totalErrores = modelErrLex.getRowCount()
                + modelErrSint.getRowCount()
                + modelErrSem.getRowCount();

        lblTokens.setText(String.valueOf(totalTokens));
        lblIdentificadores.setText(String.valueOf(totalSimbol));
        lblErrores.setText(String.valueOf(totalErrores));

        if (totalErrores == 0 && exitoSintactico) {
            lblEstado.setText("✓  Análisis completado sin errores");
            lblEstado.setForeground(SUCCESS);
        } else {
            lblEstado.setText("✗  " + totalErrores + " error(es): " +
                    modelErrLex.getRowCount()  + " léxicos, " +
                    modelErrSint.getRowCount() + " sintácticos, " +
                    modelErrSem.getRowCount()  + " semánticos");
            lblEstado.setForeground(DANGER);
        }

        // Navegar automáticamente a la pestaña con errores
        if (modelErrSem.getRowCount() > 0)       { cardLayout.show(cardPanel,"errsem");  resaltarTab(btnSem); }
        else if (modelErrSint.getRowCount() > 0)  { cardLayout.show(cardPanel,"errsint"); resaltarTab(btnSint); }
        else if (modelErrLex.getRowCount() > 0)   { cardLayout.show(cardPanel,"errlex");  resaltarTab(btnLex); }
        else                                       { cardLayout.show(cardPanel,"tokens");  resaltarTab(btnTok); }
    }

    // ── HELPERS DE UI ─────────────────────────────────────────────────────────
    private JPanel darkPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(BG_DARK);
        return p;
    }

    private JButton accentButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setBackground(BG_PANEL);
        b.setForeground(ACCENT);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COL),
                new EmptyBorder(4, 12, 4, 12)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton tabButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setBackground(BG_PANEL);
        b.setForeground(TEXT_MUTED);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(7, 4, 7, 4));
        return b;
    }

    private void resaltarTab(JButton activo) {
        tabActivo = activo;
        for (JButton b : new JButton[]{btnTok,btnSim,btnLex,btnSint,btnSem}) {
            if (b == null) continue;
            b.setForeground(b == activo ? ACCENT     : TEXT_MUTED);
            b.setBackground(b == activo ? BG_TABLE   : BG_PANEL);
        }
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(BG_TABLE);
        t.setForeground(TEXT_MAIN);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(32);
        t.setGridColor(BORDER_COL);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setSelectionBackground(modoOscuro
                ? new Color(55,65,85) : new Color(180,210,255));
        t.setSelectionForeground(modoOscuro ? Color.WHITE : Color.BLACK);
        t.setFillsViewportHeight(true);
        t.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = t.getTableHeader();
        header.setBackground(BG_PANEL);
        header.setForeground(TEXT_MUTED);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(new MatteBorder(0,0,1,0,BORDER_COL));
        header.setReorderingAllowed(false);

        Color ROW_ALT = modoOscuro
                ? new Color(42,44,49) : new Color(245,246,250);

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl,
                                                           Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl,val,sel,foc,row,col);
                setBackground(sel ? t.getSelectionBackground()
                        : (row%2==0 ? BG_TABLE : ROW_ALT));
                setBorder(new EmptyBorder(0,12,0,12));
                if (!sel) {
                    if (tbl == tablaTokens && col == 0) {
                        String tipo = val!=null ? val.toString() : "";
                        if      (tipo.startsWith("PR_"))   setForeground(modoOscuro ? new Color(198,120,221) : new Color(130,40,180));
                        else if (tipo.equals("ID"))        setForeground(ACCENT);
                        else if (tipo.contains("LITERAL")) setForeground(SUCCESS);
                        else if (tipo.equals("ERROR"))     setForeground(DANGER);
                        else if (tipo.equals("EOF"))       setForeground(TEXT_MUTED);
                        else                               setForeground(WARNING);
                    } else if (tbl==tablaErrLex || tbl==tablaErrSint || tbl==tablaErrSem) {
                        setForeground(col==0 ? TEXT_MUTED : DANGER);
                    } else if (tbl==tablaSimbolos && col==1) {
                        setForeground(ACCENT);
                    } else {
                        setForeground(TEXT_MAIN);
                    }
                }
                return this;
            }
        });
        return t;
    }

    private void updateLineNumbers() {
        if (editorArea == null || lineNums == null) return;
        int lines = editorArea.getLineCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lines; i++) sb.append(i).append("\n");
        lineNums.setText(sb.toString());
    }

    // ── EJEMPLO POR DEFECTO ───────────────────────────────────────────────────
    private static final String EJEMPLO_PROGRAMA =
            "programa Demo <<\n\n" +
                    "    num x;\n" +
                    "    decimal y;\n" +
                    "    texto nombre;\n" +
                    "    logico activo;\n\n" +
                    "    x = 10;\n" +
                    "    y = 20.5;\n" +
                    "    nombre = \"Luis\";\n" +
                    "    activo = cierto;\n\n" +
                    "    mostrar(nombre);\n\n" +
                    "    si (x > 5) <<\n" +
                    "        mostrar(\"Mayor\");\n" +
                    "    >>\n\n" +
                    ">>";

    // ── MAIN ──────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new GUI().setVisible(true);
        });
    }
}