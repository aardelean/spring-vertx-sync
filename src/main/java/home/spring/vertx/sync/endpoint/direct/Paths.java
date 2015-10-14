package home.spring.vertx.sync.endpoint.direct;

import java.util.regex.Pattern;

/**
 * Created by alex on 9/27/2015.
 */
public enum Paths {

    JSON("/json"),
    FILE("/index.html", true),
    FAVICON("/favicon.ico", true),
    MYSQL("/mysql"),
    MONGO("/mongo"),
    COMPLEX("/complex"),
    JS("/js/*", true),
    TEMPLATES("/templates/*", true),
    COMMENT("/comment");


    private String value;

    private Pattern pattern;

    private boolean resource;

    Paths(String value) {
        this.value = value;
        this.pattern= Pattern.compile(value);
    }

    private Paths(String value, boolean resource) {
        this.value = value;
        this.resource = resource;
        this.pattern= Pattern.compile(value);
    }

    public static Paths getByValue(String value){
        for(Paths operation : values()){
            if(operation.pattern.matcher(value).find()){
                return operation;
            }
        }
        throw new IllegalArgumentException("unmapped path: "+value);
    }

    public static boolean isStaticResource(String path){
        Paths paths = getByValue(path);
        return paths.resource;
    }

    public boolean isResource(){
        return resource;
    }

    public String getValue() {
        return value;
    }
}
