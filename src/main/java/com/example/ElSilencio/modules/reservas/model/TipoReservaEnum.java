package com.example.ElSilencio.modules.reservas.model;

public enum TipoReservaEnum {
    ONLINE("Reserva Online"),
    WALKIN("Walk-In (Sin Reserva)"),
    TELEFONO("Reserva Telefónica");

    private final String displayName;

    TipoReservaEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
