package group_05.ase.data_scraper.Entity.CustomRestAPIObjects.WhatLinksHereFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Page {
    public int pageid;
    public int ns;
    public String title;
    public String contentmodel;
    public String pagelanguage;
    public String pagelanguagehtmlcode;
    public String pagelanguagedir;
    public String touched;
    public long lastrevid;
    public int length;
}
