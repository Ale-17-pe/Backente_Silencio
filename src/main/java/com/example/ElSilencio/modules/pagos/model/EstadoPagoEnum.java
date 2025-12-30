package com.example.ElSilencio.modules.pagos.model;

public enum EstadoPagoEnum {
    PENDIENTE_VERIFICACION("Pendiente de Verificación"),
    VERIFICADO("Verificado"),
    RECHAZADO("Rechazado");

    private final String displayName;

    EstadoPagoEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
