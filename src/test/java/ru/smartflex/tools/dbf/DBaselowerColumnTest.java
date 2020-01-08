package ru.smartflex.tools.dbf;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class DBaselowerColumnTest {

    @Test
    public void testReadLowerColumn() {

        DbfHeader dbfHeader = DbfEngine.getHeader(
                TestHelper.getFile("Dbase_lower_column.dbf"), null);

        System.out.println("DBF header info: " + dbfHeader.toString());

        Iterator<DbfColumn> iter = dbfHeader.getColumnIterator();
        while (iter.hasNext()) {
            DbfColumn column = iter.next();
            System.out.println(column.getColumnName() + " " + column.getDbfColumnType() + " " + column.getDbfColumnPosition().getColumnLength());
        }

        assertEquals(1, dbfHeader.getCountColumns());
        assertEquals(1, dbfHeader.getCountRecords());

        DbfIterator dbfIterator = dbfHeader.getDbfIterator();

        while (dbfIterator.hasMoreRecords()) {
            DbfRecord dbfRecord = dbfIterator.nextRecord();
            String lower = dbfRecord.getString("lower");
            System.out.printf("Record num: %2d %-17s", dbfIterator.getCurrentRecord(), lower);

        }

        dbfHeader.closeDbfHeader();
    }

}
