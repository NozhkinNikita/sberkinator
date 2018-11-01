package com.sbt.hackaton.web;

import java.util.Objects;

public class ClientData {
    private String clientFirstName;
    private String clientLastName;
    private String clientUserName;

    public ClientData() {
    }

    public ClientData(String clientFirstName, String clientLastName, String clientUserName) {
        this.clientFirstName = clientFirstName;
        this.clientLastName = clientLastName;
        this.clientUserName = clientUserName;
    }

    public String getClientFirstName() {
        return clientFirstName;
    }

    public void setClientFirstName(String clientFirstName) {
        this.clientFirstName = clientFirstName;
    }

    public String getClientLastName() {
        return clientLastName;
    }

    public void setClientLastName(String clientLastName) {
        this.clientLastName = clientLastName;
    }

    public String getClientUserName() {
        return clientUserName;
    }

    public void setClientUserName(String clientUserName) {
        this.clientUserName = clientUserName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientData clientData = (ClientData) o;
        return Objects.equals(clientFirstName, clientData.clientFirstName) &&
                Objects.equals(clientLastName, clientData.clientLastName) &&
                Objects.equals(clientUserName, clientData.clientUserName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientFirstName, clientLastName, clientUserName);
    }

    @Override
    public String toString() {
        return "ClientData{" +
                "clientFirstName='" + clientFirstName + '\'' +
                ", clientLastName='" + clientLastName + '\'' +
                ", clientUserName='" + clientUserName + '\'' +
                '}';
    }
}
