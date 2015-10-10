package home.spring.vertx.sync.endpoint.direct;

/**
 * Created by alex on 9/27/2015.
 */
public enum Paths {
    JSON("/direct/json"),
    FILE("/direct/file"),
    MYSQL("/direct/mysql"),
    MONGO("/direct/mongo"),
    COMPLEX("/direct/complex");

    private String value;

    private Paths(String value) {
        this.value = value;
    }

    public static Paths getByValue(String value){
        for(Paths operation : values()){
            if(operation.value.equals(value)){
                return operation;
            }
        }
        throw new IllegalArgumentException("unmapped path");
    }


    public String getValue() {
        return value;
    }
}
