package com.example.barcoders;

public class Barcode {

    private String barcode;
    private String item;
    private String material;

    public void setBarcode(String barcode){ this.barcode = barcode; }
    public void setItem(String item){ this.item = item; }
    public void setMaterial(String material){this.material = material;}

    public String getBarcode(){return barcode;}
    public String getItem(){return item;}
    public String getMaterial(){return material;}

}
