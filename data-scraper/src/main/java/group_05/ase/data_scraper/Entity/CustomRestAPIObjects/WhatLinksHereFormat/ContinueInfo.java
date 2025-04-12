package group_05.ase.data_scraper.Entity.CustomRestAPIObjects.WhatLinksHereFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

class ContinueInfo {
    public String gblcontinue;

    @JsonProperty("continue")
    public String continueVal;
}
