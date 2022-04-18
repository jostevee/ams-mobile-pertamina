package com.example.basicapplication;

public class AssetModel {
    private String koderuangan;
    private String assetid;
    private String barcode;
    private String assetname;
    private String tahunperolehan;

    public AssetModel(){
    }

    /*
    public AssetModel(String koderuangan, String assetid, String barcode, String assetname, String tahunperolehan) {
        this.koderuangan = koderuangan;
        this.assetid = assetid;
        this.barcode = barcode;
        this.assetname = assetname;
        this.tahunperolehan = tahunperolehan;
    }
     */

    public String getKodeRuangan(){return koderuangan;}

    public void setKodeRuangan(String koderuangan){this.koderuangan = koderuangan;}

    public String getAssetid() {
        return assetid;
    }

    public void setAssetid(String assetid) {
        this.assetid = assetid;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getAssetname() {
        return assetname;
    }

    public void setAssetname(String assetname) {
        this.assetname = assetname;
    }

    public String getTahunperolehan() {
        return tahunperolehan;
    }

    public void setTahunperolehan(String tahunperolehan) {
        this.tahunperolehan = tahunperolehan;
    }
}
