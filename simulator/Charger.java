import org.json.*;

public class Charger {
    public static enum Status{
        CHARGING, FREE, OUT, ASSIGNED
    }

    public static enum UserType{
        USER, NONUSER, NONE
    }

    public String description = "geen omschrijving";
    public String userId = null;
    public String assignedJoin = null;
    public Status status = null;
    public UserType userType = UserType.NONE;
    public String id;

    public Charger(String mid,Status mstatus, String desc, String userid, String assignedjoin, UserType usertype){
        id = mid;
        status = mstatus;
        description = desc;
        assignedJoin = assignedjoin;
        userId = userid;
        userType = usertype;
    }
    

}
