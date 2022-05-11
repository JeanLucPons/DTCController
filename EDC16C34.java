package DTCController;


import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 * Class to edit the DTC table on EDC16C34 platform
 */
public class EDC16C34 extends JFrame implements ActionListener {

    public static final Font bigFont = new Font("Dialog",Font.BOLD,18);

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem openMenuItem;
    private JMenuItem saveMenuItem;
    private JMenuItem exitMenuItem;
    private JMenu findMenu;
    private JMenuItem findMenuItem;
    private JMenuItem showAllMenuItem;
    private JMenu sortMenu;
    private JMenuItem sortClassMenuItem;
    private JMenuItem noSortMenuItem;
    private JMenu classMenu;

    private JTextField softwareText;
    private JTextField projectText;
    private JTextField fileText;

    private JTable            dtcTable;
    private DefaultTableModel dtcModel;
    private String[]          classList;
    private JComboBox         classComboBox;

    private String lastOpenedDir = ".";
    private String filter = "";

    private Dump dump = null;
    private DTCInfo dtcInfo = null;
    private Vector<DTC> allDTC = new Vector<>();
    private Vector<DTC> filteredDTC = new Vector<>();
    private int sortBy = 0;

    private String APP_RELEASE = "v0.1";

    // Class Cell Renderer/Editor
    class DTCClassCellRenderer extends DefaultTableCellRenderer {
        JComboBox cb = new JComboBox(classList);
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            cb.setSelectedIndex((Integer)value);
            return cb;
        }
    }

    class DTCClassCellEditor extends DefaultCellEditor
    {
        int row;

        public DTCClassCellEditor()
        {
            super(classComboBox);
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column)
        {
            this.row = row;
            classComboBox.setSelectedIndex((Integer)value);
            return classComboBox;
        }

        @Override
        public Object getCellEditorValue() {
            return new Integer(classComboBox.getSelectedIndex());
        }

    }

    // Env Cell Renderer / Editor
    class EnvClassCellRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return new JButton("Edit");
        }

    }

    class EnvClassCellEditor extends DefaultCellEditor
    {
        JButton editButton;
        int row;

        public EnvClassCellEditor()
        {
            super(new JCheckBox());
            editButton = new JButton("Edit");
            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new DTCEnvPanel(new DTCEnv(filteredDTC.get(row),dtcInfo,dump.memory));
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column)
        {
            this.row = row;
            return editButton;
        }

    }

    public EDC16C34() {

        // Create GUI
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        openMenuItem = new JMenuItem("Open DUMP");
        openMenuItem.addActionListener(this);
        fileMenu.add(openMenuItem);
        saveMenuItem = new JMenuItem("Save DUMP");
        saveMenuItem.addActionListener(this);
        fileMenu.add(saveMenuItem);
        fileMenu.add(new JSeparator());
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitMenuItem);

        findMenu = new JMenu("Find DTC");
        findMenuItem = new JMenuItem("Find code");
        findMenuItem.addActionListener(this);
        findMenu.add(findMenuItem);
        showAllMenuItem = new JMenuItem("View all");
        showAllMenuItem.addActionListener(this);
        findMenu.add(showAllMenuItem);
        menuBar.add(findMenu);

        sortMenu = new JMenu("Sort");
        noSortMenuItem = new JMenuItem("None");
        noSortMenuItem.addActionListener(this);
        sortMenu.add(noSortMenuItem);
        sortClassMenuItem = new JMenuItem("By class");
        sortClassMenuItem.addActionListener(this);
        sortMenu.add(sortClassMenuItem);
        menuBar.add(sortMenu);

        classMenu = new JMenu("DTC Class");
        menuBar.add(classMenu);
        for(int i=1;i<=Dump.CLASS_NUMBER;i++) {
            JMenuItem clItem = new JMenuItem("Class #"+i);
            final int classID = i;
            clItem.addActionListener(e -> {
                if(dump != null ) {
                    new DTCClassPanel(new DTCClass(classID,dtcInfo,dump.memory));
                }
            });
            classMenu.add(clItem);
        }

        setJMenuBar(menuBar);

        setLayout(new BorderLayout());

        JPanel refPanel = new JPanel();
        refPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets.top = 2;
        gbc.insets.bottom = 2;
        gbc.insets.left = 2;
        gbc.insets.right = 2;

        gbc.weightx = 1.0;
        fileText = new JTextField("");
        fileText.setEditable(false);
        refPanel.add(fileText,gbc);

        gbc.weightx = 0.0;
        JLabel softwareLabel = new JLabel("Software");
        refPanel.add(softwareLabel,gbc);
        softwareText = new JTextField("0000000000");
        softwareText.setFont(bigFont);
        softwareText.setEditable(false);
        refPanel.add(softwareText,gbc);
        JLabel projectLabel = new JLabel("Project");
        refPanel.add(projectLabel,gbc);
        projectText = new JTextField("XXXXXXXX");
        projectText.setFont(bigFont);
        projectText.setEditable(false);
        refPanel.add(projectText,gbc);

        add(refPanel,BorderLayout.NORTH);

        // Table

        dtcModel = new DefaultTableModel() {
            public Class getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: // Idx
                    case 1: // Main DTC code
                    case 2: // Code path #1
                    case 3: // Code path #2
                    case 4: // Code path #3
                    case 5: // Code path #4
                        return String.class;
                    case 6: // DTC Class
                        // Class
                        return Integer.class;
                    case 7: // DTC Env
                        // Class
                        return JButton.class;
                }
                return String.class;
            }
            public boolean isCellEditable(int row, int column) {
                return column>=6;
            }
            public void setValueAt(Object aValue, int row, int column) {
                if(column==6) {
                    filteredDTC.get(row).setDTCClass((Integer)aValue);
                    updateTable();
                }
            }
        };

        classList = new String[Dump.CLASS_NUMBER+1];
        classList[0] = "Disabled";
        for(int i=1;i<=Dump.CLASS_NUMBER;i++)
            classList[i] = "Class " + i;
        classComboBox = new JComboBox(classList);

        dtcTable = new JTable(dtcModel);
        dtcTable.setDefaultRenderer(Integer.class, new DTCClassCellRenderer());
        dtcTable.setDefaultEditor(Integer.class, new DTCClassCellEditor());
        dtcTable.setDefaultRenderer(JButton.class, new EnvClassCellRenderer());
        dtcTable.setDefaultEditor(JButton.class, new EnvClassCellEditor());
        dtcTable.setRowHeight(25);
        JScrollPane dtcView = new JScrollPane(dtcTable);
        add(dtcView,BorderLayout.CENTER);

        setTitle("EDC16C34 DTC Controller [" + APP_RELEASE + "]");
        setPreferredSize(new Dimension(1000,800));
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }


    public static void main(String[] args) {

        new EDC16C34();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if( e.getSource()==openMenuItem ) {

            JFileChooser chooser = new JFileChooser(lastOpenedDir);
            int returnVal = chooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                if (f != null) {
                    try {
                        loadDUMP(f.getAbsolutePath());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                    }
                    lastOpenedDir = f.getAbsolutePath();
                }
            }

        } else if (e.getSource()==findMenuItem) {

            String newFilter = JOptionPane.showInputDialog(this,"Find",filter);
            if(newFilter!=null) {
                filter = newFilter;
                updateTable();
            }

        } else if (e.getSource()==sortClassMenuItem) {

            sortBy = 1;
            updateTable();

        } else if (e.getSource()==noSortMenuItem) {

            sortBy = 0;
            updateTable();

        } if (e.getSource()==showAllMenuItem) {

            filter = "";
            updateTable();

        } else if (e.getSource()==saveMenuItem) {

            JFileChooser chooser = new JFileChooser(lastOpenedDir);
            int returnVal = chooser.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                if (f != null) {
                    try {
                        if( f.exists() ) {
                            if (JOptionPane.showConfirmDialog(this,
                                    "The file already exists, Do you want to overwrite ?","Warning",
                                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                                dump.write(f.getAbsolutePath());
                        } else {
                          dump.write(f.getAbsolutePath());
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage());
                    }
                    lastOpenedDir = f.getAbsolutePath();
                }
            }

        }

    }

    private void loadDUMP(String fileName) throws IOException {

        try {
            dump = new Dump(fileName);
        } catch (IOException e) {
            throw new IOException("Cannot read: " + e.getMessage());
        }
        dtcInfo = dump.findDTCInfo();

        fileText.setText(fileName);
        softwareText.setText(dump.software);
        projectText.setText(dump.project);

        allDTC.clear();
        for(int i = 0; i< this.dtcInfo.nbDTC; i++)
            allDTC.add(new DTC(i,dtcInfo,dump.memory));

        updateTable();

    }

    private void updateTable() {

        filteredDTC = new Vector<>();
        for(int i = 0; i< allDTC.size(); i++) {
            if(allDTC.get(i).match(filter))
                filteredDTC.add(allDTC.get(i));
        }

        if(sortBy>0) {
            Collections.sort(filteredDTC, new Comparator<DTC>() {
                @Override
                public int compare(DTC o1, DTC o2) {
                    switch (sortBy) {
                        case 1: // By Class
                            return Integer.compare(o1.getDTCClass(), o2.getDTCClass());
                    }
                    return 0;
                }
            });
        }

        String colName[] = {"" , "Internal code" , "Code #1" , "Code #2" , "Code #3" , "Code #4" , "Class", "Env"};
        Object[][] dtcInfo = new Object[filteredDTC.size()][8];
        for(int i = 0; i< filteredDTC.size(); i++) {
            dtcInfo[i][0] = "#" + filteredDTC.get(i).idx;
            dtcInfo[i][1] = filteredDTC.get(i).getbaseCode();
            dtcInfo[i][2] = filteredDTC.get(i).getCode(0);
            dtcInfo[i][3] = filteredDTC.get(i).getCode(1);
            dtcInfo[i][4] = filteredDTC.get(i).getCode(2);
            dtcInfo[i][5] = filteredDTC.get(i).getCode(3);
            dtcInfo[i][6] = new Integer(filteredDTC.get(i).getDTCClass());
            dtcInfo[i][7] = "";
        }
        dtcModel.setDataVector(dtcInfo, colName);
        dtcTable.getColumnModel().getColumn(0).setMaxWidth(40);
        dtcTable.getColumnModel().getColumn(7).setMaxWidth(50);

    }

}
