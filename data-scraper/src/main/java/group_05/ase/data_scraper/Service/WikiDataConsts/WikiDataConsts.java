package group_05.ase.data_scraper.Service.WikiDataConsts;

import java.util.Arrays;
import java.util.List;

public class WikiDataConsts {
    public static final String COORDINATE_PROPERTY_ID = "P625";
    public static final String INSTANCE_OF_PROPERTY_ID = "P31";

    // Places
    public static final String CITY_CODE = "Q515";
    public static final String TOWN_CODE = "Q3957";
    public static final String VILLAGE_CODE = "Q56061";
    public static final String COUNTRY_CODE = "Q6256";
    public static final String MUNICIPALITY_CODE = "Q5107";
    public static final String HUMAN_SETTLEMENT_CODE = "Q1048835";
    public static final String GEOGRAPHIC_REGION_CODE = "Q486972";
    public static final String GEOGRAPHIC_OBJECT_CODE = "Q82794";
    public static final String ADMIN_TERRITORIAL_ENTITY_CODE = "Q2221906";
    public static final String GEO_POLITICAL_ENTITY_CODE = "Q15642541";

    public static final List<String> PLACE_CODES = Arrays.asList(
            CITY_CODE,
            TOWN_CODE,
            VILLAGE_CODE,
            COUNTRY_CODE,
            MUNICIPALITY_CODE,
            HUMAN_SETTLEMENT_CODE,
            GEOGRAPHIC_REGION_CODE,
            GEOGRAPHIC_OBJECT_CODE,
            ADMIN_TERRITORIAL_ENTITY_CODE,
            GEO_POLITICAL_ENTITY_CODE
    );

    // People
    public static final String HUMAN_CODE = "Q5";
    public static final String PERSON_CODE = "Q215627";
    public static final String ADULT_CODE = "Q82955";
    public static final String CHILD_CODE = "Q15632617";
    public static final String MALE_HUMAN_CODE = "Q6581097";
    public static final String FEMALE_HUMAN_CODE = "Q6581072";
    public static final String CELEBRITY_CODE = "Q327333";
    public static final String HISTORICAL_FIGURE_CODE = "Q937857";
    public static final String POLITICIAN_CODE = "Q215380";
    public static final String ARTIST_CODE = "Q36180";

    public static final List<String> PERSON_CODES = Arrays.asList(
            HUMAN_CODE,
            PERSON_CODE,
            ADULT_CODE,
            CHILD_CODE,
            MALE_HUMAN_CODE,
            FEMALE_HUMAN_CODE,
            CELEBRITY_CODE,
            HISTORICAL_FIGURE_CODE,
            POLITICIAN_CODE,
            ARTIST_CODE
    );

    public static final String VIENNA_HISTORY_WIKI_ID = "P7842";
}

