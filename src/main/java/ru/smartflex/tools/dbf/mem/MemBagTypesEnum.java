package ru.smartflex.tools.dbf.mem;

/**
 * Enumeration of the supported mem types
 *
 * @author galisha
 * @since 1.05
 */
public enum MemBagTypesEnum {

    Character("C"), Numeric("N"), Date("D"), Logical("L"), Array("A");

    private String memType;

    MemBagTypesEnum(String memType) {
        this.memType = memType;
    }

    protected static MemBagTypesEnum getByOriginalType(String type) {
        MemBagTypesEnum mbte = null;

        for (MemBagTypesEnum mt : MemBagTypesEnum.values()) {
            if (mt.memType.equals(type)) {
                mbte = mt;
                break;
            }
        }

        return mbte;
    }

}
