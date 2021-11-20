package com.example.basicapplication;

public class AssetConfirmModel {
    private String KodeUnit;
    private String KodeRuangan;
    private String AssetId;
    private String AssetName;
    private String KodeBarcode;
    private String Keterangan;
    private String Status;

    public AssetConfirmModel() {

    }

    public AssetConfirmModel(String kodeUnit, String kodeRuangan, String assetId, String assetName, String kodeBarcode, String keterangan, String status) {
        KodeUnit = kodeUnit;
        KodeRuangan = kodeRuangan;
        AssetId = assetId;
        AssetName = assetName;
        KodeBarcode = kodeBarcode;
        Keterangan = keterangan;
        Status = status;
    }

    public String getKodeUnit() {
        return KodeUnit;
    }

    public void setKodeUnit(String kodeUnit) {
        KodeUnit = kodeUnit;
    }

    public String getKodeRuangan() {
        return KodeRuangan;
    }

    public void setKodeRuangan(String kodeRuangan) {
        KodeRuangan = kodeRuangan;
    }

    public String getAssetId() {
        return AssetId;
    }

    public void setAssetId(String assetId) {
        AssetId = assetId;
    }

    public String getAssetName() {
        return AssetName;
    }

    public void setAssetName(String assetName) {
        AssetName = assetName;
    }

    public String getKodeBarcode() { return KodeBarcode; }

    public void setKodeBarcode(String kodeBarcode) { KodeBarcode = kodeBarcode; }

    public String getKeterangan() {
        return Keterangan;
    }

    public void setKeterangan(String keterangan) {
        Keterangan = keterangan;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
