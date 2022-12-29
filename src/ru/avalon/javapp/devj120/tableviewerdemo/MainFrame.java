package ru.avalon.javapp.devj120.tableviewerdemo;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainFrame extends JFrame  {
    private final JTable table;
    private final JFileChooser chooser;

    private MyTableModel myTableModel;

    public MainFrame() {
        super("Table viewer");

        setBounds(900, 600, 600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        chooser = new JFileChooser();

        JToolBar toolBar = new JToolBar();
        Container contentPane = getContentPane();

        JButton button = new JButton(new ImageIcon("fopen.png"));
        button.setToolTipText("Open file...");
        button.addActionListener(e -> {
            int res = chooser.showOpenDialog(this);
            if(res == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                if(f.getName().endsWith(".csv")) {
                    openCsv(f);
                }
                if(f.getName().endsWith(".dat")) {
                    openDat(f);
                }
            }
        });
        toolBar.add(button);

        button = new JButton(new ImageIcon("fsave.png"));
        button.setToolTipText("Save file...");
        button.addActionListener(e -> {
            int res = chooser.showSaveDialog(this);
            if(res == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                if(f.exists()) {
                    if(JOptionPane.showConfirmDialog(this, "Are you sure you want to overwrite the file?",
                            "File overwriting confirmation",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION)
                        return;
                }
                if(f.getName().endsWith(".dat"))
                    saveDat(f);

                if(f.getName().endsWith(".csv")){
                    saveCsv(f);
                }

            }
        });
        toolBar.add(button);

        contentPane.add(toolBar, BorderLayout.NORTH);

        myTableModel = new MyTableModel();
        table = new JTable(myTableModel);
        table.setAutoCreateColumnsFromModel(true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(table.getTableHeader(), BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        contentPane.add(panel, BorderLayout.CENTER);

        chooser.addChoosableFileFilter(new FileNameExtensionFilter("CSV-file", "csv"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Dat-file", "dat"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
    }
    private void openCsv(File f) {
        try {
            String[][] csv = CsvSupport.readCsv(f);// Получаем двумерный массив слов из файла
            String[] colHdrs = csv[0];// получаем массив содержащий заголовки таблицы
            String[][] data = new String[csv.length - 1][]; // создаем новый массив
            System.arraycopy(csv, 1, data, 0, data.length);// копируем в новый массив данные из файла исключая заголовки
            ((MyTableModel) table.getModel()).setDataModel(data, colHdrs);// передаем данные и заголовки в TableModel
            myTableModel.setTypes(CsvSupport.types(data));
        } catch(IOException ex){
                JOptionPane.showMessageDialog(this, ex, "Error reading CSV file", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveCsv(File file) {
        MyTableModel tm = (MyTableModel) table.getModel();
        String[] colHdrs = tm.getColumnNames();
        Object[][] data = tm.getData();
        try {
            CsvSupport.writeCsv(file, colHdrs, data);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex, "Error saving csv-file", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDat(File file) {
        try {
            Object[][] dat = DatSupport.readDat(file);
            String[] columnHeaders = (String[]) dat[0];
            Object[][] data = new Object[dat.length - 1][];
            System.arraycopy(dat, 1, data, 0, data.length);
            ((MyTableModel) table.getModel()).setDataModel(data, columnHeaders);
            myTableModel.setTypes(DatSupport.type);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, e, "Error reading dat-file", JOptionPane.ERROR_MESSAGE);

        }
    }

    private void saveDat(File f) {
        MyTableModel tm = (MyTableModel) table.getModel();
        String[] colHdrs = tm.getColumnNames();
        Class[] types = tm.getTypes();
        Object[][] data = tm.getData();
        try {
            DatSupport.writeDat(f, colHdrs, data, types);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, ex, "Error saving dat-file", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new MainFrame().setVisible(true);
    }
}
