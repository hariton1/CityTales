package group_05.ase.neo4j_data_access.formaters;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.format.DateTimeFormatter;

public class CustomTimestampSerializer extends LocalDateTimeSerializer {

    public CustomTimestampSerializer() {

        super(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS a"));

    }

}
