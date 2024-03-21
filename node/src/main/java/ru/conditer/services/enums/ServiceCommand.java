package ru.conditer.services.enums;

public enum ServiceCommand {

    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String value;

    ServiceCommand(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean equals(String cmd){
        return this.toString().equals(cmd);
    }
}
