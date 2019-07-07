package ru.smartflex.tools.dbf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;

/**
 * A class for command line dbf reading.
 *
 * @author galisha
 * @since 1.08
 */
public class DBFCommandLineStarter {

    private static final String COMMA = ",";

    private File fileLog = null;
    private BufferedWriter buff = null;

    private void testRead(File file) {
        boolean noErr = true;
        createLog(file);
        try {
            DbfHeader dbfHeader = DbfEngine.getHeader(file, "Cp866");
            println(dbfHeader.toString());
            println(null);

            StringBuilder columns = new StringBuilder(dbfHeader.getCountColumns() * 2);
            Iterator<DbfColumn> iter = dbfHeader.getColumnIterator();
            while (iter.hasNext()) {
                DbfColumn column = iter.next();
                println(column.toString());
                if (columns.length() > 0) {
                    columns.append(COMMA);
                }
                columns.append(column.getColumnName());
            }
            println(null);
            println("*** DBF content as comma delimited CSV ***");
            println(null);

            println(columns.toString());

            DbfIterator dbfIterator = dbfHeader.getDbfIterator();
            while (dbfIterator.hasMoreRecords()) {
                DbfRecord dbfRecord = dbfIterator.nextRecord();

                boolean firstColumn = true;
                Iterator<DbfColumn> iterColumn = dbfHeader.getColumnIterator();
                while (iterColumn.hasNext()) {
                    DbfColumn column = iterColumn.next();
                    if (!firstColumn) {
                        print(COMMA);
                    }
                    print(dbfRecord.getAsString(column));
                    firstColumn = false;
                }
                println(null);
            }
        } catch (Exception e) {
            noErr = false;
            e.printStackTrace();
            println(null);
            print("Got error, program terminated: ");
            println(e.getMessage());
            closeLog();
            renameToError(file);
        }

        if (noErr) {
            closeLog();
        }
    }

    private void renameToError(File src) {
        String errName = src.getName() + ".error.txt";
        String dir = System.getProperty("user.dir");
        File toRename = new File(dir, errName);
        if (toRename.exists()) {
            toRename.delete();
        }
        fileLog.renameTo(toRename);

    }

    private void print(String txt) {
        try {
            if (txt != null) {
                buff.write(txt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void println(String txt) {
        try {
            if (txt != null) {
                buff.write(txt);
            }
            buff.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeLog() {
        if (buff != null) {
            try {
                buff.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createLog(File file) {
        String dir = System.getProperty("user.dir");
        String name = file.getName() + ".log.txt";
        fileLog = new File(dir, name);
        if (fileLog.exists()) {
            fileLog.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(fileLog);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            buff = new BufferedWriter(osw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testRead(String path) {
        File file = new File(path);
        if (file.exists()) {
            testRead(file);
        } else {
            System.out.println("File does not exists");
        }
    }

    /**
     * @param args command line arguments
     */
    public static void main(String[] args) {

        if (args == null || args.length == 0 || args.length > 1) {
            String info = "java -jar dbfEngine-"
                    + DbfConstants.DBF_ENGINE_VERSION + ".jar" + " your.dbf";
            System.out.println("*** Sample of usage ***");
            System.out.println(info);

        } else {
            new DBFCommandLineStarter().testRead(args[0]);
        }

    }

}
