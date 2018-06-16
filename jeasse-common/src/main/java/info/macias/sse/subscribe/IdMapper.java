package info.macias.sse.subscribe;

@FunctionalInterface
public interface IdMapper<RequestType, IdType> {
    IdType map(RequestType request);
}
