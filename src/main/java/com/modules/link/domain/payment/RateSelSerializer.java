package com.modules.link.domain.payment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class RateSelSerializer extends JsonSerializer<RateSel> {
    @Override
    public void serialize(RateSel value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if(value == null) {
            gen.writeNull();
        } else {
            gen.writeString(value.toString());
        }
    }
}
