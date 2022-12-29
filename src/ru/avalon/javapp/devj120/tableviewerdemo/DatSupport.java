package ru.avalon.javapp.devj120.tableviewerdemo;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatSupport {
    public static Class[] type;
    public static void writeDat(File target, String[] colHdrs, Object[][] data, Class[] types) throws IOException {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(target))) {
            // (1) идентификатор формата
            out.writeByte('T');
            out.writeByte('B');
            out.writeByte('L');
            out.writeByte('1');

            // (2) количество колонок
            out.writeInt(colHdrs.length);

            // (3) описание колонок
            for(int i = 0; i < colHdrs.length; i++) {
                if(types[i] == Integer.class) {
                    out.writeChar('I');
                }
                if(types[i] == String.class) {
                    out.writeChar('S');
                }
                if(types[i] == BigDecimal.class) {
                    out.writeChar('N');
                }
                if(types[i] == Boolean.class) {
                    out.writeChar('B');
                }
                if(types[i] == LocalDate.class) {
                    out.writeChar('D');
                }
                out.writeUTF(colHdrs[i]);
            }

            // (4) данные таблицы
            // (4.1) количество строк
            out.writeInt(data.length);
            // (4.2)
            for (int j = 0; j < data.length; j++) {
                for(int i = 0; i < data[j].length; i++) {
                    out.writeByte(data[j][i] != null ? '*' : '-');
                    if(data[j][i] != null) {
                        if(types[i] == Integer.class) {
                            out.writeInt(Integer.parseInt((String) data[j][i]));
                            continue;
                        }
                        if(types[i] == String.class) {
                            out.writeUTF((String) data[j][i]);
                            continue;
                        }
                        if(types[i] == BigDecimal.class) {
                            out.writeObject(data[j][i]);
                            continue;
                        }
                        if(types[i] == Boolean.class) {
                            out.writeBoolean(Boolean.parseBoolean((String) data[j][i]));
                            continue;
                        }
                        if(types[i] == LocalDate.class) {
                            out.writeObject(data[j][i]);
                        }
                    }
                }
            }
        }
    }

    public static Object[][] readDat(File file) throws IOException, ClassNotFoundException {
        List<Object[]> res = new ArrayList<>();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            if(ois.readByte() != 'T')
                throw new IllegalArgumentException("Invalid dat file");
            if(ois.readByte() != 'B')
                throw new IllegalArgumentException("Invalid dat file");
            if(ois.readByte() != 'L')
                throw new IllegalArgumentException("Invalid dat file");
            if(ois.readByte() != '1')
                throw new IllegalArgumentException("Invalid dat file");

            int columns = ois.readInt();

            String [] columnsHeaders = new String[columns];
            char[] types = new char[columns];
            for (int i = 0; i < columnsHeaders.length; i++) {
                types[i] = ois.readChar();
                columnsHeaders[i] = ois.readUTF();
            }
            res.add(columnsHeaders);

            int rows = ois.readInt();

            Object[][] data = new Object[rows][columns];
            Class[] resTypes = new Class[columns];
            int p;
            for(int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    p = ois.readByte();
                    if( (char) p == '*') {
                        switch (types[j]) {
                            case 'I' -> {
                                data[i][j] = ois.readInt();
                                resTypes[j] = Integer.class;
                            }
                            case 'S' -> {
                                data[i][j] = ois.readUTF();
                                resTypes[j] = String.class;
                            }
                            case 'N' -> {
                                data[i][j] = ois.readObject();
                                resTypes[j] = BigDecimal.class;
                            }
                            case 'D' -> {
                                data[i][j] = ois.readObject();
                                resTypes[j] = LocalDate.class;
                            }
                            case 'B' -> {
                                data[i][j] = ois.readBoolean();
                                resTypes[j] = Boolean.class;
                            }
                            default -> throw new IllegalArgumentException("Invalid type");
                        }
                    }
                    else {
                        data[i][j] = "";
                    }
                }
                res.add(data[i]);
            }
            type = resTypes;
            return res.toArray(new Object[0][]);
        }
    }
}

