package ru.avalon.javapp.devj120.tableviewerdemo;

import javax.swing.table.AbstractTableModel;

public class MyTableModel extends AbstractTableModel {
    private Object[][] data;
    private String[] columnNames;

    private Class[] types;

    public void setDataModel(Object[][] data, String[] columnNames) {
        this.data = data;
        this.columnNames = columnNames;
        types = new Class[columnNames.length];
        fireTableStructureChanged();
    }

    public Class[] getTypes() {
        return types;
    }

    public void setTypes(Class[] types) {
        this.types = types;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public Object[][] getData() {
        return data;
    }

    public int getColumnCount() {
        if (columnNames != null)
            return columnNames.length;
        else
            return 0;
        }

        public int getRowCount() {
        if(data != null)
            return data.length;
        else
            return 0;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {

            if(data == null) {
            return null;
            }
            if(data[row][col] != null) {
                if (data[row][col].equals("true")) {
                    return Boolean.TRUE;
                }
                if (data[row][col].equals("false")) {
                    return Boolean.FALSE;
                }
            }
            return data[row][col];
        }

        @Override
        public Class getColumnClass(int c) {
            return types[c];
        }
}
