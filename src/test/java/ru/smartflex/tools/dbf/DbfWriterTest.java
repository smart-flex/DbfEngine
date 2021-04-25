package ru.smartflex.tools.dbf;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class DbfWriterTest {

    @Test
    public void testWriteEmptyDbf() {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        DbfAppender dbfAppender = DbfEngine.getWriter(bos, DbfCodePages.Cp866);
        DbfColumn dc01 = new DbfColumn("fio", DbfColumnTypes.Character, 20, 0);
        DbfColumn dc02 = new DbfColumn("birthday", DbfColumnTypes.Date, 0, 0);
        dbfAppender.defineColumns(dc01, dc02);
        dbfAppender.writeDbfAndClose();

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        DbfHeader dbfHeader = DbfEngine.getHeader(bis, null);

        System.out.println("DBF header info: " + dbfHeader.toString());

        assertEquals("Cp866", dbfHeader.getCodePage());
        assertEquals(2, dbfHeader.getCountColumns());
        assertEquals(0, dbfHeader.getCountRecords());

        dbfHeader.closeDbfHeader();
    }

    @Test
    public void testWriteDbf() {
        File writeDbf = new File("WRT_PERSON.DBF");
        if (writeDbf.exists()) {
            writeDbf.delete();
        }

        writeTwoRecordsToDbf(writeDbf);

        DbfHeader dbfHeader = DbfEngine.getHeader(writeDbf, null);

        System.out.println("DBF header info: " + dbfHeader.toString());

        assertEquals("Cp866", dbfHeader.getCodePage());
        assertEquals(5, dbfHeader.getCountColumns());
        assertEquals(2, dbfHeader.getCountRecords());

        dbfHeader.closeDbfHeader();

        writeDbf.delete();
    }

    @Test
    public void testAppendDbf() {
        File writeDbf = new File("WRT_PERSON.DBF");
        if (writeDbf.exists()) {
            writeDbf.delete();
        }

        writeTwoRecordsToDbf(writeDbf);

        DbfAppender dbfAppender = DbfEngine.getAppender(writeDbf, "Cp866");

        DbfStatement statement = dbfAppender.getStatement();
        statement.setString("actor", "Jean-Claude Van Damme");
        statement.setDate("currdate", new Date());
        statement.setBigDecimal("hit", new BigDecimal("450.5"));
        statement.insertStatement();

        dbfAppender.writeDbfAndClose();

        DbfHeader dbfHeader = DbfEngine.getHeader(writeDbf, null);

        System.out.println("DBF header info: " + dbfHeader.toString());

        assertEquals("Cp866", dbfHeader.getCodePage());
        assertEquals(5, dbfHeader.getCountColumns());
        assertEquals(3, dbfHeader.getCountRecords());

        dbfHeader.closeDbfHeader();

        writeDbf.delete();
    }

    @Test
    public void testDbfCloning() {

        DbfHeader dbfHeader = DbfEngine.getHeader(
                TestHelper.getFile("FP_26_SAMPLE.DBF"), null);
        DbfIterator dbfIterator = dbfHeader.getDbfIterator();
        System.out.println("DBF header info: " + dbfHeader.toString());

        Iterator<DbfColumn> iter = dbfHeader.getColumnIterator();
        while (iter.hasNext()) {
            DbfColumn column = iter.next();
            System.out.println(column.getColumnName() + " " + column.getDbfColumnType() + " " + column.getDbfColumnPosition().getColumnLength());
        }

        File cloneDbf = new File("FP_26_SAMPLE_CLONE.DBF");
        if (cloneDbf.exists()) {
            cloneDbf.delete();
        }
        DbfAppender dbfAppender = DbfEngine.getWriter(cloneDbf, DbfCodePages.Cp866);
        dbfAppender.defineColumns(dbfHeader);

        while (dbfIterator.hasMoreRecords()) {
            DbfRecord dbfRecord = dbfIterator.nextRecord();
            DbfStatement statement = dbfAppender.getStatement();
            statement.fillStatement(dbfRecord);
            statement.insertStatement();
        }

        dbfAppender.writeDbfAndClose();
        dbfHeader.closeDbfHeader();
    }

    private void writeTwoRecordsToDbf(File writeDbf) {

        DbfAppender dbfAppender = DbfEngine.getWriter(writeDbf, DbfCodePages.Cp866);
        DbfColumn dc01 = new DbfColumn("magic", DbfColumnTypes.Logical, 0, 0);
        DbfColumn dc02 = new DbfColumn("actor", DbfColumnTypes.Character, 60, 0);
        DbfColumn dc03 = new DbfColumn("currdate", DbfColumnTypes.Date, 0, 0);
        DbfColumn dc04 = new DbfColumn("hit", DbfColumnTypes.Numeric, 10, 2);
        DbfColumn dc05 = new DbfColumn("forever", DbfColumnTypes.Logical, 0, 0);
        dbfAppender.defineColumns(dc01,dc02, dc03, dc04, dc05);

        DbfStatement statement = dbfAppender.getStatement();
        statement.setString("actor", "Chuck Norris");
        statement.setDate("currdate", new Date());
        statement.setBigDecimal("hit", new BigDecimal("500.5"));
        statement.insertStatement();

        statement.setBoolean("magic", Boolean.TRUE);
        statement.setString("actor", "Bruce Lee");
        statement.setBigDecimal("hit", new BigDecimal("1000.10"));
        statement.setBoolean("forever", Boolean.TRUE);
        statement.insertStatement();

        dbfAppender.writeDbfAndClose();

    }
}
