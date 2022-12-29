package ru.avalon.javapp.devj120.tableviewerdemo;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvSupport {
    private static final char SEP = ',';

    public static String[][] readCsv(File file) throws IOException { // Получаем двумерный массив слов
        List<String[]> res = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String s;
            int colCnt = -1;
            while( ( s = br.readLine() ) != null ) {
                String[] row = parseLine(s); // получили разбитую на массив строку
                if(colCnt == -1)
                    colCnt = row.length; // присвоили количество слов первой строки
                else if(colCnt != row.length) //сравниваем количество слов в строках
                    throw new FileFormatException("Rows contain different number of values.");
                res.add(row); // помещаем значение массива в List
            }
        }
        return res.toArray(new String[0][]); // создаем из Lista двумерный массив
    }

    private static String[] parseLine(String s) {
        List<String> res = new ArrayList<>();
        int p = 0;
        while(p < s.length()) { // разбиваем строку на слова
            int st = p;
            String v;
            if(s.charAt(p) == '"') {
                p++;
                p = s.indexOf('"', p);
                while(p < (s.length() - 1) && s.charAt(p + 1) == '"') {
                    p += 2;
                    p = s.indexOf('"', p);
                }
                v = s.substring(st + 1, p).replace("\"\"", "\"");
                p += 2;
            } else {
                p = s.indexOf(SEP, p);
                if(p == -1)
                    p = s.length();
                v = s.substring(st, p);
                if(v.length() == 0)
                    v = null;
                p++;
            }
            res.add(v); //помещаем слова в List
        }
        return res.toArray(new String[0]); // создаем из Lista массив
    }

    public static void writeCsv(File file, String[] colHdrs, Object[][] data) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (int i = 0; i < colHdrs.length; i++) {
                if(i == colHdrs.length - 1) {
                    bw.write(colHdrs[i]);
                }
                else {
                    bw.write(colHdrs[i] + SEP);
                }
            }

            for (int i = 0; i < data.length; i++){
                bw.newLine();
                for (int j = 0; j < data[0].length; j++) {
                    if(data[i][j] == null) {
                        if(j == data[0].length - 1) {
                            bw.write("");
                            continue;
                        } else {
                            bw.write("" + SEP);
                            continue;
                        }
                    }
                    if(j == data[0].length - 1) {
                        bw.write(bildCsv((String) data[i][j]));
                    } else {
                        bw.write(bildCsv((String) data[i][j]) + SEP);
                    }
                }
            }
        }
    }

    private static String bildCsv(String s) {
        int p = 0;
        boolean flag = false;
        StringBuilder sb = new StringBuilder(s);
        while (p < sb.length()) {
            if(sb.charAt(p) == '"') {
                sb.insert(p, '"');
                p += 2;
                flag = true;
                continue;
            }
            if(sb.charAt(p) == ',') {
                flag = true;
            }
            p++;
        }
        if(flag) {
            sb.insert(0, '"').append('"');
        }
        return sb.toString();
    }

    public static Class[] types(String[][] csv) {
        Class[] columns = new Class[csv[1].length];
        for(int i = 0; i < csv.length; i++) {
            for(int j = 0; j < csv[0].length; j++) {
                int p = 0;
                String s = csv[i][j];
                if(s == null) {
                    continue;
                }
                if(s.equals("true") || s.equals("false")) {
                    columns[j] = Boolean.class;
                    continue;
                }
                if(matchDate(s)) {
                    columns[j] = LocalDate.class;
                    continue;
                }
                int d = 0;
                int c = 0;
                if(Character.isDigit(s.charAt(p))) {
                    while (p < s.length()) {
                        if(Character.isDigit(s.charAt(p))) {
                            d++;
                        }
                        if(s.charAt(p) == SEP && p != s.length() - 1) {
                            c++;
                        }
                        p++;
                    }
                }
                boolean flag = columns[j] == Integer.class || columns[j] == null;
                if(d == s.length()) {
                    if(flag) {
                        columns[j] = Integer.class;
                    } else if (columns[j] != String.class){
                        columns[j] = BigDecimal.class;
                    }
                    continue;
                }
                if(d == s.length() - 1 && c == 1) {
                    columns[j] = BigDecimal.class;
                }
                else {
                    columns[j] = String.class;
                }
            }
        }
        return columns;
    }

    private static boolean matchDate(String s) {
        Pattern pattern = Pattern.compile("\\d\\d\\d\\d.\\d\\d.\\d\\d");
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }
}
