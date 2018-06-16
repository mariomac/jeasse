package info.macias.sse.subscribe;

@FunctionalInterface
public interface IdMapper<RequestType, IdType> {
    IdType map(RequestType request);

    static <T> T identity(T idHolder) {
        return idHolder;
    }
}
