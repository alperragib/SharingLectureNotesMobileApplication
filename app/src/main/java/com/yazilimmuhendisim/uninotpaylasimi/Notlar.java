package com.yazilimmuhendisim.uninotpaylasimi;

import java.util.ArrayList;

public class Notlar {
    private String not;
    private String uni_adi;
    private String bolum_adi;
    private String ders_adi;
    private String date;
    private ArrayList<String> imagesUrl;

    public Notlar() {
    }

    public Notlar(String not, String uni_adi,String bolum_adi, String ders_adi, String date,ArrayList<String> imagesUrl) {
        this.not = not;
        this.uni_adi = uni_adi;
        this.bolum_adi = bolum_adi;
        this.ders_adi = ders_adi;
        this.date = date;
        this.imagesUrl = imagesUrl;

    }

    public String getNot() {
        return not;
    }

    public void setNot(String not) {
        this.not = not;
    }

    public String getUni_adi() {
        return uni_adi;
    }

    public void setUni_adi(String uni_adi) {
        this.uni_adi = uni_adi;
    }
    public String getBolum_adi() {
        return bolum_adi;
    }

    public void setBolum_adi(String bolum_adi) {
        this.bolum_adi = bolum_adi;
    }

    public String getDers_adi() {
        return ders_adi;
    }

    public void setDers_adi(String ders_adi) {
        this.ders_adi = ders_adi;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public ArrayList<String> getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(ArrayList<String> imagesUrl) {
        this.imagesUrl = imagesUrl;
    }


}
