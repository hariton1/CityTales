package group_05.ase.data_scraper.Entity.CustomRestAPIObjects.WhatLinksHereFormat;

public class Continue {
    public String gblcontinue;

    // Need this because "continue" is a reserved word
    public String _continue;
    @com.fasterxml.jackson.annotation.JsonProperty("continue")
    public void setContinue(String cont) {
        this._continue = cont;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("continue")
    public String getContinue() {
        return _continue;
    }
}
