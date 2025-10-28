package code;

public class Object {
    private String id, name;

    public Object (String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    } 

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(!name.equals("")){
            this.name = name;
        }
    }
}
