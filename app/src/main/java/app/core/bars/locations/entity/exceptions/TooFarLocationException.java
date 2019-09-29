package app.core.bars.locations.entity.exceptions;

public class TooFarLocationException extends RuntimeException {

    public TooFarLocationException(String imageUrl) {
        super(imageUrl);
    }
}
