package app.core;


public interface BaseInteractor<INPUT, OUTPUT> {
    OUTPUT process(INPUT input);
}
