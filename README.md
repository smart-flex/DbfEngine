# DbfEngine Java API

DbfEngine - a Java API to read, write, append and clone xBase(DBASE, Foxpro dbf files). Also API allows read memory files (.mem) of Foxpro.
Samples for both operations you can see at [DbfEngine javadoc](https://smart-flex.ru/htm/de_api/index.html)

This API is pure lightweight library without memory consumption and any third party libraries (there are no java loggers and etc.)
The DBF Java API is intended as a fast engine for data exchange purposes.

## Features

Engine is very small and fast.
API for reading is made as iterator, API for writing is made in manner as JDBC statement. It is allows to write compact Java code.

Also, you can look inside your dbf file by invoking it through command line:
java -jar dbfEngine-1.12.jar your.dbf

The result of parsing dbf header and content will be saved into text file.

## Limitations

This version was tested under MS Foxpro 2.6 without memo field support.

## Requirements

DbfEngine requires JDK 1.6 or higher.

## Code samples
```java
public class Fp26Reader {
    private static void testRead() {
        DbfIterator dbfIterator = DbfEngine.getReader(
            Fp26Reader.class.getResourceAsStream("FP_26_SAMPLE.DBF"), null);

        while (dbfIterator.hasMoreRecords()) {
            DbfRecord dbfRecord = dbfIterator.nextRecord();
            String string = dbfRecord.getString("string");
            float sumFloat = dbfRecord.getFloat("sum_f");
            BigDecimal sumNumeric = dbfRecord.getBigDecimal("sum_n");
            boolean bool = dbfRecord.getBoolean("bool_val");
            Date date = dbfRecord.getDate("date_val");

            System.out.println(string + " " + sumFloat + " " + sumNumeric + " " +
                bool + " " + date);
        }
    }

    public static void main(String[] args) {
        Fp26Reader.testRead();
    }
}

public class Fp26Writer {
    private static void testWrite() {
        DbfAppender dbfAppender = DbfEngine.getWriter("WRT_PERSON.DBF", DbfCodePages.Cp866);

        DbfColumn dc01 = new DbfColumn("magic", DbfColumnTypes.Logical, 0, 0);
        DbfColumn dc02 = new DbfColumn("actor", DbfColumnTypes.Character, 60, 0);
        DbfColumn dc03 = new DbfColumn("currdate", DbfColumnTypes.Date, 0, 0);
        DbfColumn dc04 = new DbfColumn("hit", DbfColumnTypes.Numeric, 10, 2);
        DbfColumn dc05 = new DbfColumn("forever", DbfColumnTypes.Logical, 0, 0);
        dbfAppender.defineColumns(dc01, dc02, dc03, dc04, dc05);

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

    public static void main(String[] args) {
        Fp26Writer.testWrite();
    }
}

public class DbfClonePerformance {

	private static void cloneDBF(String srcFile, String cloneFile) {
		File room64File = new File(srcFile);
		System.out.println("Length src file (bytes): " + room64File.length() + " (megabytes): " + room64File.length()/(1024*1024));
		DbfHeader header = DbfEngine.getHeader(room64File, "Cp866");
		System.out.println(header.toString());
		DbfIterator dbfIterator = header.getDbfIterator();

		File cloneDbf = new File(cloneFile);
		if (cloneDbf.exists()) {
			cloneDbf.delete();
		}
		DbfAppender dbfAppender = DbfEngine.getWriter(cloneDbf,
				DbfCodePages.Cp866);
		dbfAppender.defineColumns(header);

		while (dbfIterator.hasMoreRecords()) {
			DbfRecord dbfRecord = dbfIterator.nextRecord();

			// here can be your filtering code ....
			
			DbfStatement statement = dbfAppender.getStatement();
			statement.fillStatement(dbfRecord);
			statement.insertStatement();
		}

		dbfAppender.writeDbfAndClose();
		header.closeDbfHeader();
	}

	public static void main(String[] args) {

		long start = System.currentTimeMillis();
		
		cloneDBF("F:\\fias_dbf\\ROOM64.DBF",
				"F:\\fias_dbf\\ROOM64_CLONE.DBF");

		long finish = System.currentTimeMillis();
		
		System.out.println("Clone finished for: " + (finish - start)/1000 + " sec");
	}
}
```
#### Performance

The result of performance are (below is the work log of DbfClonePerformance class):

* Length src file (bytes): 600606501 (megabytes): 572
* DbfHeader [firstRecordPosition=641, countRecords=1078287, countColumns=19, lengthRecord=557, codePage=Cp866, typeDbf={3,FoxBASE_dBASE_III_PLUS_without_memo}]
* Clone finished for: 52 sec

This java code is worked under jdk1.6/32; Intel Core i3; Windows 10. And there is no one messages such as: java.lang.OutOfMemoryError: Java heap space

#### Licensing

DbfEngine is issued on under the GNU Lesser General Public License.

#### Support

If you have any issues or questions or suggestions you can send me a letter by email: <gali.shaimardanov@gmail.com>

#### Related project

Djf is Desktop Java Forms, a compact master-detail UI library like FoxBase, but based on Swing: [You can see Djf here](https://github.com/smart-flex/Djf)

