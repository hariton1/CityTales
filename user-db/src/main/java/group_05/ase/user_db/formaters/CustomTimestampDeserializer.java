package group_05.ase.user_db.formaters;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.format.DateTimeFormatter;

public class CustomTimestampDeserializer extends LocalDateTimeDeserializer {

    public CustomTimestampDeserializer() {

        super(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    }

}
