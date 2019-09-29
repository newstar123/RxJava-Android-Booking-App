package app.gateway.qorumcache.basecache;

public interface BaseCacheInterface {

    <T> boolean save(BaseCacheType type, T value);
    <T> T get(BaseCacheType type);
    void reset();
    boolean exist();
}
