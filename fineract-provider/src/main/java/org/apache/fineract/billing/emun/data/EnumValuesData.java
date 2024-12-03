package org.apache.fineract.billing.emun.data;

public class EnumValuesData {

    private final Long id;
    private final String value;
    private final String type;
    private String name;

    public EnumValuesData(Long id, String value, String type) {

        this.id = id;
        this.value = value;
        this.type = type;
    }

    public EnumValuesData(Long id, String value, String type, String name) {
        super();
        this.id = id;
        this.value = value;
        this.type = type;
        this.name = name;
    }


    public Long getId() {
        return id;
    }


    public String getValue() {
        return value;
    }


    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
