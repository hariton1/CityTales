package group_05.ase.neo4j_data_access.formaters;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.format.DateTimeFormatter;

public class CustomTimestampDeserializer extends LocalDateTimeDeserializer {

    public CustomTimestampDeserializer() {

        super(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    }

}
