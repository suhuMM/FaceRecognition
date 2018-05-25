package com.android.face.bean;

import java.io.Serializable;

public class PhotoBean implements Serializable {
    private static final long serialVersionUID=-8632786943821178661L;
   // public Bitmap bitmap;
    //public byte[] bitmapArray;
    public String path;
    public float[] feature;
    public String name;
    public SerialBitmap bitmap;


}
