package ru.smartflex.tools.dbf;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The <b>main class</b> that supplies iterators and appenders - start here. <br>
 * <br>
 * The <b>reader parts</b> may be used in two ways:
 * <ol>
 * <li>to get iterator directly:
 *
 * <pre>
 * DbfIterator dbfIterator = DbfEngine.getReader(
 * 		Fp26Reader.class.getResourceAsStream(&quot;FP_26_SAMPLE.DBF&quot;), null);
 * </pre>
 *
 * <li>first to get dbf header definition and then get iterator:
 *
 * <pre>
 * DbfHeader dbfHeader = DbfEngine.getHeader(
 * 		Fp26Reader.class.getResourceAsStream(&quot;FP_26_SAMPLE.DBF&quot;), null);
 * DbfIterator dbfIterator = dbfHeader.getDbfIterator();
 * </pre>
 *
 * </ol>
 * Then you have to read dbf data in cycle like as:
 *
 * <pre>
 * while (dbfIterator.hasMoreRecords()) {
 * 	DbfRecord dbfRecord = dbfIterator.nextRecord();
 * 	String string = dbfRecord.getString(&quot;string&quot;);
 * 	float sumFloat = dbfRecord.getFloat(&quot;sum_f&quot;);
 * 	BigDecimal sumNumeric = dbfRecord.getBigDecimal(&quot;sum_n&quot;);
 * 	boolean bool = dbfRecord.getBoolean(&quot;bool_val&quot;);
 * 	Date date = dbfRecord.getDate(&quot;date_val&quot;);
 * 	System.out.println(string + &quot; &quot; + sumFloat + &quot; &quot; + sumNumeric + &quot; &quot; + bool
 * 			+ &quot; &quot; + date);
 * }
 * </pre>
 *
 * <br>
 * The <b>writer parts</b> may be used in one way:<br>
 * <br>
 * First: you have to get DbfAppender:
 *
 * <pre>
 * DbfAppender dbfAppender = DbfEngine.getWriter(&quot;WRT_PERSON.DBF&quot;,
 * 		DbfCodePages.Cp866);
 * </pre>
 * <p>
 * And then you have to define columns for dbf header:
 *
 * <pre>
 * DbfColumn dc01 = new DbfColumn(&quot;magic&quot;, DbfColumnTypes.Logical, 0, 0);
 * DbfColumn dc02 = new DbfColumn(&quot;actor&quot;, DbfColumnTypes.Character, 60, 0);
 * DbfColumn dc03 = new DbfColumn(&quot;currdate&quot;, DbfColumnTypes.Date, 0, 0);
 * DbfColumn dc04 = new DbfColumn(&quot;hit&quot;, DbfColumnTypes.Numeric, 10, 2);
 * DbfColumn dc05 = new DbfColumn(&quot;forever&quot;, DbfColumnTypes.Logical, 0, 0);
 *
 * dbfAppender.defineColumns(dc01, dc02, dc03, dc04, dc05);
 * </pre>
 * <p>
 * After that you have to use DbfStatement:
 *
 * <pre>
 * DbfStatement statement = dbfAppender.getStatement();
 * <br>
 * statement.setString(&quot;actor&quot;, &quot;Chuck Norris&quot;);
 * statement.setDate(&quot;currdate&quot;, new Date());
 * statement.setBigDecimal(&quot;hit&quot;, new BigDecimal(&quot;500.5&quot;));
 * <br>
 * statement.insertStatement();
 * <br>
 * statement.setBoolean(&quot;magic&quot;, Boolean.TRUE);
 * statement.setString(&quot;actor&quot;, &quot;Bruce Lee&quot;);
 * statement.setBigDecimal(&quot;hit&quot;, new BigDecimal(&quot;1000.10&quot;));
 * statement.setBoolean(&quot;forever&quot;, Boolean.TRUE);
 * <br>
 * statement.insertStatement();
 * <br>
 * dbfAppender.writeDbfAndClose();
 * </pre>
 *
 * @author galisha
 * @since 1.00
 */
public class DbfEngine {

    private DbfEngine() {

    }

    /**
     * Gets DBF header.
     *
     * @param dbfFileName dbf file name
     * @param enc If codepage parameter is missed (in other words == null) then it will be filled as Cp866. And this parameter will be used if in DBF header code page is unknown.
     * @return dbf header
     * @since 1.05
     */
    public static DbfHeader getHeader(String dbfFileName, String enc) {
        File dbfFile = new File(dbfFileName);
        return getReader(dbfFile, enc).getDbfHeader();
    }

    /**
     * Gets DBF header.
     *
     * @param path        path to the folder
     * @param dbfFileName dbf file name
     * @param enc If codepage parameter is missed (in other words == null) then it will be filled as Cp866. And this parameter will be used if in DBF header code page is unknown.
     * @return dbf header
     * @since 1.05
     */
    public static DbfHeader getHeader(String path, String dbfFileName,
                                      String enc) {
        File dbfFile = new File(path, dbfFileName);
        return getReader(dbfFile, enc).getDbfHeader();
    }

    /**
     * Gets DBF header.
     *
     * @param dbfFile dbf file
     * @param enc If codepage parameter is missed (in other words == null) then it will be filled as Cp866. And this parameter will be used if in DBF header code page is unknown.
     * @return dbf header
     * @since 1.05
     */
    public static DbfHeader getHeader(File dbfFile, String enc) {
        DbfIterator dbfIterator = new DbfIterator(dbfFile, enc);
        return dbfIterator.getDbfHeader();
    }

    /**
     * Gets DBF header.
     *
     * @param dbfStream input stream
     * @param enc If codepage parameter is missed (in other words == null) then it will be filled as Cp866. And this parameter will be used if in DBF header code page is unknown.
     * @return dbf header
     * @since 1.05
     */
    public static DbfHeader getHeader(InputStream dbfStream, String enc) {
        DbfIterator dbfIterator = new DbfIterator(dbfStream, enc);
        return dbfIterator.getDbfHeader();
    }

    /**
     * Gets DBF iterator
     *
     * @param dbfFileName dbf file name
     * @param enc If codepage parameter is missed (in other words == null) then it will be filled as Cp866. And this parameter will be used if in DBF header code page is unknown.
     * @return dbf iterator
     * @since 1.00
     */
    public static DbfIterator getReader(String dbfFileName, String enc) {
        File dbfFile = new File(dbfFileName);
        return getReader(dbfFile, enc);
    }

    /**
     * Gets DBF iterator
     *
     * @param path        path to the folder
     * @param dbfFileName dbf file name
     * @param enc If codepage parameter is missed (in other words == null) then it will be filled as Cp866. And this parameter will be used if in DBF header code page is unknown.
     * @return dbf iterator
     * @since 1.00
     */
    public static DbfIterator getReader(String path, String dbfFileName,
                                        String enc) {
        File dbfFile = new File(path, dbfFileName);
        return getReader(dbfFile, enc);
    }

    /**
     * Gets DBF iterator
     *
     * @param dbfFile dbf file
     * @param enc If codepage parameter is missed (in other words == null) then it will be filled as Cp866. And this parameter will be used if in DBF header code page is unknown.
     * @return dbf iterator
     * @since 1.00
     */
    public static DbfIterator getReader(File dbfFile, String enc) {
        return new DbfIterator(dbfFile, enc);
    }

    /**
     * Gets DBF iterator
     *
     * @param dbfStream input stream
     * @param enc If codepage parameter is missed (in other words == null) then it will be filled as Cp866. And this parameter will be used if in DBF header code page is unknown.
     * @return dbf iterator
     * @since 1.00
     */
    public static DbfIterator getReader(InputStream dbfStream, String enc) {
        return new DbfIterator(dbfStream, enc);
    }

    /**
     * Gets DBF appender for a newly created file
     *
     * @param path        path to the folder
     * @param dbfFileName dbf file name
     * @param dbfCodePage dbf code page
     * @return dbf appender
     * @since 1.00
     */
    public static DbfAppender getWriter(String path, String dbfFileName,
                                        DbfCodePages dbfCodePage) {
        File dbfFile = new File(path, dbfFileName);
        return getWriter(dbfFile, dbfCodePage);
    }

    /**
     * Gets DBF appender for a newly created file
     *
     * @param dbfFileName dbf file name
     * @param dbfCodePage dbf code page
     * @return dbf appender
     * @since 1.00
     */
    public static DbfAppender getWriter(String dbfFileName,
                                        DbfCodePages dbfCodePage) {
        File dbfFile = new File(dbfFileName);
        return getWriter(dbfFile, dbfCodePage);
    }

    /**
     * Gets DBF appender for a newly created file
     *
     * @param dbfFile     dbf file
     * @param dbfCodePage dbf code page
     * @return dbf appender
     * @since 1.00
     */
    public static DbfAppender getWriter(File dbfFile, DbfCodePages dbfCodePage) {
        if (dbfFile.exists()) {
            throw new DbfEngineException(DbfConstants.EXCP_DBF_EXISTS);
        }
        return new DbfAppender(dbfFile, dbfCodePage);
    }

    /**
     * Gets DBF appender for a newly created file
     *
     * @param dbfStream   output stream
     * @param dbfCodePage dbf code page
     * @return dbf appender
     * @since 1.00
     */
    public static DbfAppender getWriter(OutputStream dbfStream,
                                        DbfCodePages dbfCodePage) {
        return new DbfAppender(dbfStream, dbfCodePage);
    }

    /**
     * Gets DBF appender for existed file
     *
     * @param dbfFileName dbf file name
     * @param enc If codepage parameter is missed (in other words == null) then it will be filled as Cp866. And this parameter will be used if in DBF header code page is unknown.
     * @return dbf appender
     * @since 1.11
     */
    public static DbfAppender getAppender(String dbfFileName, String enc) {
        File dbfFile = new File(dbfFileName);
        return getAppender(dbfFile, enc);
    }

    /**
     * Gets DBF appender for existed file
     *
     * @param path        path to the folder
     * @param dbfFileName dbf file name
     * @param enc If codepage parameter is missed (in other words == null) then it will be filled as Cp866. And this parameter will be used if in DBF header code page is unknown.
     * @return dbf appender
     * @since 1.11
     */
    public static DbfAppender getAppender(String path, String dbfFileName,
                                        String enc) {
        File dbfFile = new File(path, dbfFileName);
        return getAppender(dbfFile, enc);
    }

    /**
     * Gets DBF appender for existed file
     *
     * @param dbfFile     dbf file
     * @param enc If codepage parameter is missed (in other words == null) then it will be filled as Cp866. And this parameter will be used if in DBF header code page is unknown.
     * @return dbf appender
     * @since 1.11
     */
    public static DbfAppender getAppender(File dbfFile, String enc) {
        if (!dbfFile.exists()) {
            throw new DbfEngineException(DbfConstants.EXCP_DBF_NOT_EXISTS);
        }
        DbfIterator iter = new DbfIterator(dbfFile, enc);
        DbfHeader header = iter.getDbfHeader();
        iter.closeIterator();
        return new DbfAppender(dbfFile, header);
    }

}
