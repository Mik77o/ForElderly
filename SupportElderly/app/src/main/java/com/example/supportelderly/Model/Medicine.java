package com.example.supportelderly.Model;

/**
 * Model dla leków
 */
public class Medicine {
    private int id;
    private String dose;
    private String frequency;
    private String place;
    private byte[] image;
    private String nameMedicine;

    public Medicine(String nameOfMedicine, String dose, String frequency, String place, byte[] image, int id) {
        this.nameMedicine = nameOfMedicine;
        this.dose = dose;
        this.frequency = frequency;
        this.place = place;
        this.image = image;
        this.id = id;
    }

    public Medicine(String nameOfMedicine, String dose, String frequency, String place, byte[] image) {
        this.nameMedicine = nameOfMedicine;
        this.dose = dose;
        this.frequency = frequency;
        this.place = place;
        this.image = image;
    }

    public Medicine() {
    }

    public void setName(String name) {
        this.nameMedicine = name;
    }

    public String getDose() {
        return dose;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameOfMedicine() {
        return nameMedicine;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
