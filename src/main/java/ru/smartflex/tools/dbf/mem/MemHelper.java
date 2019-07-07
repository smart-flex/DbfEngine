package ru.smartflex.tools.dbf.mem;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Helper class that provides conversion from internal .mem file format.
 *
 * @author galisha
 * @since 1.05
 */
public class MemHelper {

    private static final int STARTNO = 2305508; /*
     * 1st Mar, 1600 (nominally, not
     * actually)
     */
    private static final int STARTYY = 1600;
    private static final int STARTMM = 3;
    private static final int STARTDD = 1;
    private static int[] daymonths = new int[12]; /* Days in a month */
    private static double daysin400yrs, daysin100yrs, daysin4yrs, daysin1yr;
    private static DateFormat df = new SimpleDateFormat("yyyyMMdd");
    private static Lock lockDF = new ReentrantLock(false);

    static {
        /* Days in a given month */
        daymonths[0] = 31; /* Mar - after the dreaded February 28/29 */
        daymonths[1] = 30; /* Apr */
        daymonths[2] = 31; /* May */
        daymonths[3] = 30; /* Jun */
        daymonths[4] = 31; /* Jul */
        daymonths[5] = 31; /* Aug */
        daymonths[6] = 30; /* Sep */
        daymonths[7] = 31; /* Oct */
        daymonths[8] = 30; /* Nov */
        daymonths[9] = 31; /* Dec */
        daymonths[10] = 31; /* Jan */
        daymonths[11] = 28; /* Feb */

        /* These allow quick calculations and partial avoidance of leap years */
        daysin1yr = 365;
        daysin4yrs = ((daysin1yr * 4) + 1);
        daysin100yrs = ((daysin4yrs * 25) - 1);
        daysin400yrs = ((daysin100yrs * 4) + 1);
    }

    private MemHelper() {
    }

    static double getDouble(byte[] record) {
        return Double.longBitsToDouble(readLong(record));
    }

    private static long readLong(byte[] record) {
        // copy past from DataInputStream
        return (((long) record[0] << 56) + ((long) (record[1] & 255) << 48)
                + ((long) (record[2] & 255) << 40)
                + ((long) (record[3] & 255) << 32)
                + ((long) (record[4] & 255) << 24) + ((record[5] & 255) << 16)
                + ((record[6] & 255) << 8) + ((record[7] & 255) << 0));
    }

    /**
     * Return date from double - many thanks to Peter Townsend (He wrote dbmem.c; In Russia we usually said - ku two times ;)). <br>
     * Limitation: As I understood beginner date is starting with 1600 year.
     *
     * @param num date value as double
     * @return date
     */
    static Date getDate(double num) {
        Date date = null;

        int nyy, nmm, ndd; /* Number of year, month, day */
        int monthcntr; /* Loop counter for months */

        char leapyear; /* Is the current year a leapyear? */

        /* log of 100 is a blank date */
        if (Math.log10(num) != 100) {
            if (num >= STARTNO) {
                num -= STARTNO;
                nyy = STARTYY;
                nmm = STARTMM;
                ndd = STARTDD;
                leapyear = 'N';
                while (num >= daysin400yrs) {
                    num -= daysin400yrs;
                    nyy += 400;
                }
                if (num == (daysin400yrs - 1)) {
                    nyy += 400;
                    num = 0;
                    leapyear = 'Y';
                }
                while (num >= daysin100yrs) {
                    num -= daysin100yrs;
                    nyy += 100;
                    leapyear = 'N';
                }
                while (num >= daysin4yrs) {
                    num -= daysin4yrs;
                    nyy += 4;
                }
                if (num == (daysin4yrs - 1)) {
                    nyy += 4;
                    num = 0;
                    leapyear = 'Y';
                }

                if (leapyear == 'Y') {
                    ndd = 29;
                    nmm = 2;
                }

                while (num >= daysin1yr) {
                    num -= daysin1yr;
                    nyy++;
                }

                monthcntr = 0;
                while ((monthcntr < 11) && (num >= daymonths[monthcntr])) {
                    num -= daymonths[monthcntr];
                    nmm++;
                    if (nmm == 13) {
                        nmm = 1;
                        nyy++;
                    }
                    monthcntr++;
                }
                ndd += num;

                StringBuilder strDate = new StringBuilder(3);
                strDate.append(String.valueOf(nyy));
                if (nmm < 10) {
                    strDate.append("0");
                    strDate.append(String.valueOf(nmm));
                } else {
                    strDate.append(String.valueOf(nmm));
                }
                if (ndd < 10) {
                    strDate.append("0");
                    strDate.append(String.valueOf(ndd));
                } else {
                    strDate.append(String.valueOf(ndd));
                }

                lockDF.lock();
                try {
                    date = df.parse(strDate.toString());
                } catch (Exception e) {
                    throw new MemEngineException("Parse date", e);
                } finally {
                    lockDF.unlock();
                }
            }
        }

        return date;
    }

    static String getString(byte[] record, int off, int len,
                                      String enc) throws UnsupportedEncodingException {
        int amount = getAmount(record, off, len);
        return new String(record, off, amount, enc);
    }

    static String getFieldName(byte[] record, int off, int len) {
        int amount = getAmount(record, off, len);
        return new String(record, off, amount);
    }

    private static int getAmount(byte[] record, int off, int len) {
        int amount = 0;
        int index = off;
        boolean fok = true;
        do {
            if (amount < len && record[index] != 0) {
                amount++;
                index++;
            } else {
                fok = false;
            }
        } while (fok);
        return amount;
    }

    /**
     * Copy past from org.apache.commons.lang3.ArrayUtils - many thanks.
     *
     * @param array array of bytes to reverse
     */
    static void reverse(byte[] array) {
        if (array == null) {
            return;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * Copy past from DataInputStream - many thanks
     *
     * @param in  input stream
     * @param b   byte array
     * @param off offset
     * @param len length to read
     * @return flag of reading
     */
    static boolean readFully(InputStream in, byte b[], int off,
                                       int len) {
        if (len <= 0)
            throw new MemEngineException(MemConstants.EXCP_LN_POS);
        boolean attention = false;
        int n = 0;
        while (n < len) {
            int count;
            try {
                count = in.read(b, off + n, len - n);
                if (count == 1) {
                    if (b[off + n] == MemConstants.MEM_FILE_EOF_BYTE) {
                        attention = true;
                    }
                }
            } catch (IOException e) {
                throw new MemEngineException(MemConstants.EXCP_IO_ERROR, e);
            }
            if (count < 0) {
                if (attention == false) {
                    throw new MemEngineException(MemConstants.EXCP_EOF);
                } else {
                    break;
                }
            }
            n += count;
        }
        return attention;
    }

}
