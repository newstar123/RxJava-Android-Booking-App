package app.gateway.rest.client;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import app.core.uber.start.entity.RideDirection;


class FromUpperToLowerAdapter extends TypeAdapter<RideDirection> {
    @Override public void write(JsonWriter out, RideDirection value) throws IOException {
        out.value(value.name().toLowerCase());
    }

    @Override public RideDirection read(JsonReader in) throws IOException {
        return RideDirection.valueOf(in.nextString().toUpperCase());
    }
}
