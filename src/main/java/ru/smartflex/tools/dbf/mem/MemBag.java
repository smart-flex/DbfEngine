package ru.smartflex.tools.dbf.mem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Mem file container.
 *
 * @author galisha
 * @since 1.05
 */
public class MemBag {

    private Map<String, Double> mapDouble = new HashMap<String, Double>();
    private Map<String, MemArray> mapArray = new HashMap<String, MemArray>();
    private Map<String, Boolean> mapBoolean = new HashMap<String, Boolean>();
    private Map<String, String> mapString = new HashMap<String, String>();
    private Map<String, Date> mapDate = new HashMap<String, Date>();
    private String enc;

    MemBag(File memFile, String enc) {
        this.enc = enc;
        handleFile(memFile);
    }

    MemBag(InputStream is, String enc) {
        this.enc = enc;
        try {
            handleStream(is);
        } catch (IOException e) {
            throw new MemEngineException(MemConstants.EXCP_IO_ERROR, e);
        }
    }

    private void handleFile(File memFile) {
        try {
            InputStream is = new FileInputStream(memFile);
            handleStream(is);
        } catch (FileNotFoundException e) {
            throw new MemEngineException(MemConstants.EXCP_FILE_PROBLEM, e);
        } catch (IOException e) {
            throw new MemEngineException(MemConstants.EXCP_IO_ERROR, e);
        }
    }

    private void handleStream(InputStream is) throws IOException {
        byte[] header = new byte[11 + 1 + 4 + 1 + 1 + 14];
        byte[] valString = new byte[0xFFFF];
        byte[] valOther = new byte[MemConstants.LENGTH_NUMERIC];

        MemArray ma = null;
        boolean readNext = true;
        do {
            boolean flagEof = MemHelper.readFully(is, header, 0, header.length);

            if (flagEof == false) {
                String fieldName = MemHelper.getFieldName(header, 0, 11);
                String fieldType = MemHelper.getFieldName(header, 11, 1);
                MemBagTypesEnum memType = MemBagTypesEnum
                        .getByOriginalType(fieldType);

                if (memType == MemBagTypesEnum.Array) {

                    MemHelper.readFully(is, valOther, 0,
                            MemConstants.LENGTH_ARRAY_DIM);
                    int rows = (int) valOther[0] + (int) valOther[1] * 256;
                    int cols = (int) valOther[2] + (int) valOther[3] * 256;

                    ma = new MemArray(rows, cols);
                    mapArray.put(fieldName, ma);

                } else {
                    switch (memType) {
                        case Numeric:
                            MemHelper.readFully(is, valOther, 0,
                                    MemConstants.LENGTH_NUMERIC);
                            MemHelper.reverse(valOther);
                            double d = MemHelper.getDouble(valOther);
                            mapDouble.put(fieldName, Double.valueOf(d));

                            storeValue(fieldName, d, ma);
                            break;
                        case Date:
                            MemHelper.readFully(is, valOther, 0,
                                    MemConstants.LENGTH_DATE);
                            MemHelper.reverse(valOther);
                            double ddate = MemHelper.getDouble(valOther);
                            Date date = MemHelper.getDate(ddate);
                            storeValue(fieldName, date, ma);
                            break;
                        case Character:
                            int stringLength = (int) header[16] + (int) header[17]
                                    * 256;
                            MemHelper.readFully(is, valString, 0, stringLength);
                            String str;
                            try {
                                str = MemHelper.getString(valString, 0,
                                        stringLength, enc);
                            } catch (Exception e) {
                                throw new MemEngineException(
                                        MemConstants.EXCP_STRING_CREATE, e);
                            }
                            storeValue(fieldName, str, ma);
                            break;
                        case Logical:
                            MemHelper.readFully(is, valOther, 0,
                                    MemConstants.LENGTH_LOGICAL);
                            boolean flag = false;
                            if (valOther[0] == 1) {
                                flag = true;
                            }
                            storeValue(fieldName, flag, ma);
                            break;
                    }
                }
            } else {
                break;
            }
        } while (readNext);

        is.close();
    }

    private void storeValue(String memName, String s, MemArray ma) {
        if (isAbleAppendToArray(ma)) {
            ma.addString(s);
        } else {
            mapString.put(memName, s);
        }
    }

    private void storeValue(String memName, Date d, MemArray ma) {
        if (isAbleAppendToArray(ma)) {
            ma.addDate(d);
        } else {
            mapDate.put(memName, d);
        }
    }

    private void storeValue(String memName, boolean b, MemArray ma) {
        if (isAbleAppendToArray(ma)) {
            ma.addBoolean(b);
        } else {
            mapBoolean.put(memName, Boolean.valueOf(b));
        }
    }

    private void storeValue(String memName, double d, MemArray ma) {
        if (isAbleAppendToArray(ma)) {
            ma.addDouble(d);
        } else {
            mapDouble.put(memName, Double.valueOf(d));
        }
    }

    private boolean isAbleAppendToArray(MemArray ma) {
        boolean fok = false;
        if (ma != null) {
            fok = ma.isAbbleToAppend();
        }
        return fok;
    }

    /**
     * Retrieves the value for memory variable as a Date in the Java programming language
     *
     * @param memName memory name
     * @return Date
     */
    public Date getDate(String memName) {
        Date val;

        if (memName != null) {
            val = mapDate.get(memName.toUpperCase());
            if (val == null) {
                throw new MemEngineException(
                        MemConstants.EXCP_NF_VALUE_MEM_NAME + memName);
            }
        } else {
            throw new MemEngineException(MemConstants.EXCP_MISS_MEM_NAME);
        }

        return val;
    }

    /**
     * Retrieves the value for memory variable as a String in the Java programming language
     *
     * @param memName memory name
     * @return String
     */
    public String getString(String memName) {
        String val;

        if (memName != null) {
            val = mapString.get(memName.toUpperCase());
            if (val == null) {
                throw new MemEngineException(
                        MemConstants.EXCP_NF_VALUE_MEM_NAME + memName);
            }
        } else {
            throw new MemEngineException(MemConstants.EXCP_MISS_MEM_NAME);
        }

        return val;
    }

    /**
     * Retrieves the value for memory variable as a boolean in the Java programming language
     *
     * @param memName memory name
     * @return boolean
     */
    public boolean getBoolean(String memName) {
        boolean fok;

        if (memName != null) {
            Boolean f = mapBoolean.get(memName.toUpperCase());
            if (f != null) {
                fok = f.booleanValue();
            } else {
                throw new MemEngineException(
                        MemConstants.EXCP_NF_VALUE_MEM_NAME + memName);
            }
        } else {
            throw new MemEngineException(MemConstants.EXCP_MISS_MEM_NAME);
        }

        return fok;
    }

    /**
     * Retrieves the value for memory variable as a double in the Java programming language
     *
     * @param memName memory name
     * @return double
     */
    public double getDouble(String memName) {
        double ret;

        if (memName != null) {
            Double d = mapDouble.get(memName.toUpperCase());
            if (d != null) {
                ret = d.doubleValue();
            } else {
                throw new MemEngineException(
                        MemConstants.EXCP_NF_VALUE_MEM_NAME + memName);
            }
        } else {
            throw new MemEngineException(MemConstants.EXCP_MISS_MEM_NAME);
        }

        return ret;
    }

    /**
     * Retrieves the value for one-dimensional array as a Object in the Java programming language
     *
     * @param memName memory array name
     * @param index   This parameter is beginning from 1 (not zero)
     * @return Object
     */
    public Object getArrayElement(String memName, int index) {
        return getArrayObj(memName, index, 0);
    }

    /**
     * Retrieves the value for two-dimensional array as a Object in the Java programming language
     *
     * @param memName memory array name
     * @param row     This parameter (row) is beginning from 1 (not zero)
     * @param col     This parameter (column) is beginning from 1 (not zero)
     * @return Object
     */
    public Object getArrayElement(String memName, int row, int col) {
        return getArrayObj(memName, row, col);
    }

    private Object getArrayObj(String memName, int row, int col) {
        if (memName != null) {
            MemArray ma = mapArray.get(memName.toUpperCase());
            if (ma != null) {
                return ma.getElement(row, col);
            } else {
                throw new MemEngineException(
                        MemConstants.EXCP_NF_VALUE_MEM_NAME + memName);
            }
        } else {
            throw new MemEngineException(MemConstants.EXCP_MISS_MEM_NAME);
        }
    }

}
