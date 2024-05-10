package com.example.bluetooth_api_app;

public class Device {

    // Valeurs par défaut
    private int ID = 0;

    private String Name = "NoName";

    private String Brand = "NoBrand";

    private String Modele = "NoModel";

    private String Type = "NoType";

    private int Autonomy = 0;

    private Boolean State = Boolean.FALSE;

    private String Data = "NoData";

    public int getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getModele() {
        return Modele;
    }

    public void setModele(String modele) {
        Modele = modele;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getAutonomy() {
        return Autonomy;
    }

    public void setAutonomy(int autonomy) {
        Autonomy = autonomy;
    }

    public Boolean getState() {
        return State;
    }

    public void setState(Boolean state) {
        State = state;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
