package com.satyrlabs.newsnet.ui;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;


public class CustomSource extends Model{
    @Column(name = "name")
    public String name;
}
