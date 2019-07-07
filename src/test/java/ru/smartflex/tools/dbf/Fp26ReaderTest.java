package ru.smartflex.tools.dbf;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class Fp26ReaderTest {

    @Test
    public void testReadHeader() {

        DbfHeader dbfHeader = DbfEngine.getHeader(
                TestHelper.getFile("FP_26_SAMPLE.DBF"), null);

        System.out.println("DBF header info: " + dbfHeader.toString());

        Iterator<DbfColumn> iter = dbfHeader.getColumnIterator();
        while (iter.hasNext()) {
            DbfColumn column = iter.next();
            System.out.println(column.getColumnName() + " " + column.getDbfColumnType() + " " + column.getDbfColumnPosition().getColumnLength());
        }

        assertEquals("Cp866", dbfHeader.getCodePage());
        assertEquals(7, dbfHeader.getCountColumns());
        assertEquals(3, dbfHeader.getCountRecords());

        dbfHeader.closeDbfHeader();
    }

    @Test
    public void testReadBody() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        Date date1912 = null;
        try {
            date1912 = sdf.parse("1912.01.02");
        } catch (ParseException e) {
            assertEquals(true, false);
        }

        DbfIterator dbfIterator = DbfEngine.getReader(TestHelper.getFile("FP_26_SAMPLE.DBF"), null);

        while (dbfIterator.hasMoreRecords()) {
            DbfRecord dbfRecord = dbfIterator.nextRecord();
            String string = dbfRecord.getString("string");
            float sumFloat = dbfRecord.getFloat("sum_f");
            BigDecimal sumNumeric = dbfRecord.getBigDecimal("sum_n");
            boolean bool = dbfRecord.getBoolean("bool_val");
            Date date = dbfRecord.getDate("date_val");

            System.out.printf("Record num: %2d %-10s % 3.4f %-10s %-6s %tF %n", dbfIterator.getCurrentRecord(), string, sumFloat, sumNumeric, bool, date);

            if (dbfIterator.getCurrentRecord() == 2) {
                assertEquals("Hello", string);
                assertEquals(2.4456f, sumFloat, 0.0f);
                assertEquals(new BigDecimal("120.44"), sumNumeric);
                assertEquals(true, bool);
                assertEquals(date1912, date);
            }
        }
    }

}
