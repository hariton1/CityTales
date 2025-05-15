package group_05.ase.data_scraper.Old_Scraper.Entity.Json;

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

    public int getPageid() {
        return pageid;
    }

    public void setPageid(int pageid) {
        this.pageid = pageid;
    }

    @Override
    public String toString() {
        return "Page{" +
                "pageid=" + pageid +
                ", ns=" + ns +
                ", title='" + title + '\'' +
                ", contentmodel='" + contentmodel + '\'' +
                ", pagelanguage='" + pagelanguage + '\'' +
                ", pagelanguagehtmlcode='" + pagelanguagehtmlcode + '\'' +
                ", pagelanguagedir='" + pagelanguagedir + '\'' +
                ", touched='" + touched + '\'' +
                ", lastrevid=" + lastrevid +
                ", length=" + length +
                '}';
    }
}
