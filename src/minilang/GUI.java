package minilang;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame {

    // ── Colores ──────────────────────────────────────────────────────────────
    private static final Color BG_DARK    = new Color(30, 31, 34);
    private static final Color BG_PANEL   = new Color(40, 42, 46);
    private static final Color BG_TABLE   = new Color(45, 47, 52);
    private static final Color ACCENT     = new Color(97, 175, 239);
    private static final Color SUCCESS    = new Color(152, 195, 121);
    private static final Color DANGER     = new Color(224, 108, 117);
    private static final Color WARNING    = new Color(229, 192, 123);
    private static final Color TEXT_MAIN  = new Color(200, 200, 200);
    private static final Color TEXT_MUTED = new Color(120, 125, 135);
    private static final Color BORDER_COL = new Color(60, 63, 68);

    // ── Componentes principales ───────────────────────────────────────────────
    private JTextArea editorArea;
    private JLabel    lblArchivo;

    // Stats
    private JLabel lblTokens, lblIdentificadores, lblErrores, lblEstado;

    // Tablas
    private DefaultTableModel modelTokens, modelSimbolos, modelErrores;
    private JTable            tablaTokens, tablaSimbolos, tablaErrores;

    // Panel de vistas con CardLayout
    private JPanel    cardPanel;
    private CardLayout cardLayout;

    // Datos del análisis
    private List<Token>  tokensResultado  = new ArrayList<>();
    private List<Token>  erroresResultado = new ArrayList<>();
    private TablaSimbolos tablaSimbolos2  = TablaSimbolos.getInstance();

    // ─────────────────────────────────────────────────────────────────────────
    public GUI() {
        setTitle("Analizador Léxico TurboX");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ── HEADER ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = darkPanel(new BorderLayout(16, 0));
        p.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_COL),
                new EmptyBorder(14, 20, 14, 20)
        ));

        // Título
        JLabel title = new JLabel("Analizador Léxico TurboX");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_MAIN);

        // Stats
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 0));
        stats.setOpaque(false);
        lblTokens         = statLabel("0", "TOKENS");
        lblIdentificadores= statLabel("0", "IDENTIFICADORES");
        lblErrores        = statLabel("0", "ERRORES");
        stats.add(lblTokens);
        stats.add(lblIdentificadores);
        stats.add(lblErrores);

        p.add(title, BorderLayout.WEST);
        p.add(stats,  BorderLayout.EAST);
        return p;
    }

    private JLabel statLabel(String val, String label) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);

        JLabel lVal = new JLabel(val, SwingConstants.CENTER);
        lVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lVal.setForeground(label.equals("ERRORES") ? DANGER : ACCENT);
        lVal.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lLbl = new JLabel(label, SwingConstants.CENTER);
        lLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lLbl.setForeground(TEXT_MUTED);
        lLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(lVal);
        box.add(lLbl);

        // Guardamos referencia al JLabel del valor para actualizarlo
        lVal.setName(label);
        return lVal;
    }

    // ── CENTRO ────────────────────────────────────────────────────────────────
    private JSplitPane buildCenter() {
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                buildEditorPanel(),
                buildResultsPanel()
        );
        split.setDividerLocation(420);
        split.setDividerSize(4);
        split.setBorder(null);
        split.setBackground(BG_DARK);
        return split;
    }

    // Panel izquierdo: editor + botones
    private JPanel buildEditorPanel() {
        JPanel p = darkPanel(new BorderLayout(0, 8));
        p.setBorder(new EmptyBorder(12, 16, 12, 8));

        // Barra de archivo
        JPanel barraArchivo = new JPanel(new BorderLayout(8, 0));
        barraArchivo.setOpaque(false);

        lblArchivo = new JLabel("Sin archivo cargado");
        lblArchivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblArchivo.setForeground(TEXT_MUTED);

        JButton btnAbrir = accentButton("Abrir archivo");
        btnAbrir.addActionListener(e -> abrirArchivo());

        barraArchivo.add(lblArchivo,  BorderLayout.CENTER);
        barraArchivo.add(btnAbrir,    BorderLayout.EAST);

        // Editor
        editorArea = new JTextArea(EJEMPLO_PROGRAMA);
        editorArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
        editorArea.setBackground(new Color(25, 27, 30));
        editorArea.setForeground(new Color(171, 178, 191));
        editorArea.setCaretColor(Color.WHITE);
        editorArea.setLineWrap(false);
        editorArea.setBorder(new EmptyBorder(10, 12, 10, 12));
        editorArea.setTabSize(4);

        JScrollPane scroll = new JScrollPane(editorArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COL));
        scroll.getViewport().setBackground(new Color(25, 27, 30));

        // Número de líneas
        JTextArea lineNumbers = new JTextArea("1");
        lineNumbers.setBackground(new Color(33, 35, 39));
        lineNumbers.setForeground(new Color(80, 85, 95));
        lineNumbers.setFont(new Font("JetBrains Mono", Font.PLAIN, 13));
        lineNumbers.setEditable(false);
        lineNumbers.setBorder(new EmptyBorder(10, 8, 10, 8));
        scroll.setRowHeaderView(lineNumbers);

        editorArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { updateLineNumbers(lineNumbers); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { updateLineNumbers(lineNumbers); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateLineNumbers(lineNumbers); }
        });
        updateLineNumbers(lineNumbers);

        // Botón analizar
        JButton btnAnalizar = new JButton("▶  Ejecutar análisis");
        btnAnalizar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAnalizar.setBackground(ACCENT);
        btnAnalizar.setForeground(new Color(25, 27, 30));
        btnAnalizar.setFocusPainted(false);
        btnAnalizar.setBorderPainted(false);
        btnAnalizar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAnalizar.setBorder(new EmptyBorder(10, 0, 10, 0));
        btnAnalizar.addActionListener(e -> ejecutarAnalisis());

        p.add(barraArchivo, BorderLayout.NORTH);
        p.add(scroll,       BorderLayout.CENTER);
        p.add(btnAnalizar,  BorderLayout.SOUTH);
        return p;
    }

    // Panel derecho: tabs + tablas
    private JPanel buildResultsPanel() {
        JPanel p = darkPanel(new BorderLayout(0, 0));
        p.setBorder(new EmptyBorder(12, 8, 12, 16));

        // Tabs
        JPanel tabs = new JPanel(new GridLayout(1, 3, 4, 0));
        tabs.setOpaque(false);
        tabs.setBorder(new EmptyBorder(0, 0, 8, 0));

        JButton btnTok = tabButton("Tokens",        "tokens");
        JButton btnSim = tabButton("Símbolos",      "simbolos");
        JButton btnErr = tabButton("Errores",        "errores");

        tabs.add(btnTok);
        tabs.add(btnSim);
        tabs.add(btnErr);

        // CardLayout para las tres vistas
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setOpaque(false);

        cardPanel.add(buildTablaPanel(initTablaTokens()),    "tokens");
        cardPanel.add(buildTablaPanel(initTablaSimbolos()),  "simbolos");
        cardPanel.add(buildTablaPanel(initTablaErrores()),   "errores");

        btnTok.addActionListener(e -> cardLayout.show(cardPanel, "tokens"));
        btnSim.addActionListener(e -> cardLayout.show(cardPanel, "simbolos"));
        btnErr.addActionListener(e -> { cardLayout.show(cardPanel, "errores"); });

        p.add(tabs,      BorderLayout.NORTH);
        p.add(cardPanel, BorderLayout.CENTER);
        return p;
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
                new String[]{"Token", "Lexema", "Línea", "Columna"}, 0
        ) { public boolean isCellEditable(int r, int c){return false;} };
        tablaTokens = styledTable(modelTokens);
        tablaTokens.getColumnModel().getColumn(0).setPreferredWidth(160);
        tablaTokens.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaTokens.getColumnModel().getColumn(2).setPreferredWidth(55);
        tablaTokens.getColumnModel().getColumn(3).setPreferredWidth(65);
        return tablaTokens;
    }

    private JTable initTablaSimbolos() {
        modelSimbolos = new DefaultTableModel(
                new String[]{"Identificador", "Línea", "Columna"}, 0
        ) { public boolean isCellEditable(int r, int c){return false;} };
        tablaSimbolos = styledTable(modelSimbolos);
        return tablaSimbolos;
    }

    private JTable initTablaErrores() {
        modelErrores = new DefaultTableModel(
                new String[]{"Línea", "Columna", "Descripción"}, 0
        ) { public boolean isCellEditable(int r, int c){return false;} };
        tablaErrores = styledTable(modelErrores);
        tablaErrores.getColumnModel().getColumn(0).setPreferredWidth(55);
        tablaErrores.getColumnModel().getColumn(1).setPreferredWidth(65);
        tablaErrores.getColumnModel().getColumn(2).setPreferredWidth(250);
        return tablaErrores;
    }

    // ── STATUS BAR ────────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel p = darkPanel(new BorderLayout());
        p.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, BORDER_COL),
                new EmptyBorder(8, 20, 8, 20)
        ));
        lblEstado = new JLabel("Listo — escribe o carga un archivo y presiona Ejecutar análisis");
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstado.setForeground(TEXT_MUTED);
        p.add(lblEstado, BorderLayout.WEST);
        return p;
    }

    // ── LÓGICA PRINCIPAL ──────────────────────────────────────────────────────
    private void abrirArchivo() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                String contenido = new String(java.nio.file.Files.readAllBytes(f.toPath()));
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
        tokensResultado.clear();
        erroresResultado.clear();
        tablaSimbolos2.limpiar();

        modelTokens.setRowCount(0);
        modelSimbolos.setRowCount(0);
        modelErrores.setRowCount(0);

        String fuente = editorArea.getText();

        // Usa el mismo Lexer que tu consola
        try (Reader reader = new java.io.StringReader(fuente)) {
            Lexer lexer = new Lexer(reader);
            Token t;
            while (true) {
                t = lexer.yylex();
                if (t == null) break;
                if (t.esEOF()) { tokensResultado.add(t); break; }
                if (t.esError()) {
                    erroresResultado.add(t);
                    modelErrores.addRow(new Object[]{
                            t.getLinea(), t.getColumna(),
                            "Carácter no reconocido: '" + t.getValor() + "'"
                    });
                } else {
                    tokensResultado.add(t);
                    modelTokens.addRow(new Object[]{
                            t.getTipo(), t.getValor(), t.getLinea(), t.getColumna()
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error en el análisis: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Llenar tabla de símbolos
        tablaSimbolos2.getTabla().forEach((nombre, tok) ->
                modelSimbolos.addRow(new Object[]{
                        nombre, tok.getLinea(), tok.getColumna()
                })
        );

        // Actualizar stats
        long validos = tokensResultado.stream()
                .filter(tk -> !tk.esEOF()).count();
        actualizarStat(lblTokens,          String.valueOf(validos));
        actualizarStat(lblIdentificadores, String.valueOf(tablaSimbolos2.tamanio()));
        actualizarStat(lblErrores,         String.valueOf(erroresResultado.size()));

        // Estado
        if (erroresResultado.isEmpty()) {
            lblEstado.setText("✓  Análisis completado sin errores");
            lblEstado.setForeground(SUCCESS);
        } else {
            lblEstado.setText("✗  Se encontraron " + erroresResultado.size() + " error(es) léxico(s)");
            lblEstado.setForeground(DANGER);
        }
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

    private JButton tabButton(String text, String card) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        b.setBackground(BG_PANEL);
        b.setForeground(TEXT_MAIN);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(7, 0, 7, 0));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(BG_TABLE); }
            public void mouseExited(MouseEvent e)  { b.setBackground(BG_PANEL); }
        });
        return b;
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
        t.setSelectionBackground(new Color(55, 65, 85));
        t.setSelectionForeground(Color.WHITE);
        t.setFillsViewportHeight(true);
        t.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = t.getTableHeader();
        header.setBackground(BG_PANEL);
        header.setForeground(TEXT_MUTED);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COL));
        header.setReorderingAllowed(false);

        // Renderer con colores por tipo de token
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                setBackground(sel ? new Color(55,65,85) : (row%2==0 ? BG_TABLE : new Color(42,44,49)));
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!sel && col == 0 && tbl == tablaTokens) {
                    String tipo = val != null ? val.toString() : "";
                    if (tipo.startsWith("PR_"))                     setForeground(new Color(198,120,221));
                    else if (tipo.equals("ID"))                     setForeground(ACCENT);
                    else if (tipo.contains("LITERAL"))              setForeground(SUCCESS);
                    else if (tipo.equals("ERROR"))                  setForeground(DANGER);
                    else if (tipo.equals("EOF"))                    setForeground(TEXT_MUTED);
                    else                                            setForeground(WARNING);
                } else if (!sel && tbl == tablaErrores) {
                    setForeground(DANGER);
                } else if (!sel) {
                    setForeground(TEXT_MAIN);
                }
                return this;
            }
        });
        return t;
    }

    private void actualizarStat(JLabel lbl, String val) {
        lbl.setText(val);
    }

    private void updateLineNumbers(JTextArea nums) {
        int lines = editorArea.getLineCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lines; i++) sb.append(i).append("\n");
        nums.setText(sb.toString());
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
                    "    si (x > 5) ??\n" +
                    "        mostrar(\"Mayor\");\n" +
                    "    !!\n\n" +
                    "    @\n\n" +
                    ">>";

    // ── MAIN de la GUI ────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new GUI().setVisible(true);
        });
    }
}