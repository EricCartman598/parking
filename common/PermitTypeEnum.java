package com.epam.parking.common;

public enum PermitTypeEnum {
    IN_LINE("in_line"),
    ACTIVE("active"),
    INACTIVE("inactive");

    private String text;

    PermitTypeEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    public static PermitTypeEnum fromString(String text) {
        for (PermitTypeEnum permitTypeEnum : PermitTypeEnum.values()) {
            if (permitTypeEnum.text.equalsIgnoreCase(text)) {
                return permitTypeEnum;
            }
        }
        throw new IllegalArgumentException();
    }
}
