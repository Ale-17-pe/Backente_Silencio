package com.example.ElSilencio.modules.pagos.model;

public enum MetodoPagoEnum {
    EFECTIVO("Efectivo", false),
    YAPE("Yape", true),
    PLIN("Plin", true),
    TRANSFERENCIA("Transferencia", true);

    private final String displayName;
    private final boolean requiereEvidencia;

    MetodoPagoEnum(String displayName, boolean requiereEvidencia) {
        this.displayName = displayName;
        this.requiereEvidencia = requiereEvidencia;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isRequiereEvidencia() {
        return requiereEvidencia;
    }
}
