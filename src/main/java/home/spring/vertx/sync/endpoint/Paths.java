package home.spring.vertx.sync.endpoint;

/**
 * Created by alex on 9/27/2015.
 */
public enum Paths {
    JSON("/json"),
    FILE("/file"),
    MYSQL("/mysql"),
    EXTERNAL_API("/slow"),
    MONGO("/mongo");

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
