import greenfoot.GreenfootImage;
import greenfoot.Color;
import greenfoot.Font;

public class CarActor extends Draggable{
    public static final GreenfootImage[] carimages = {
        new GreenfootImage("new/transparent_4.png"),
        new GreenfootImage("new/transparent_5.png"),
        new GreenfootImage("new/transparent_6.png"),
        new GreenfootImage("new/transparent_7.png"),
        new GreenfootImage("new/transparent_8.png"),
        new GreenfootImage("new/transparent_9.png"),
    };
    
    public String uid;
    public ChargerActor chargingOn = null;
    public String name = "lege naam";
    public CarActor(String mname, String muid, int imageindex){
        super();
        uid = muid;
        name = mname;
        setImage(new GreenfootImage(carimages[imageindex % carimages.length]));
        getImage().setColor(Color.WHITE);
        getImage().setFont(new Font("Arial", 16));
        getImage().drawString(name, 18,  65);
        
    }

    @Override
    void onDropped() {
        super.onDropped();
        System.out.println("DROPPED on");
        ChargerActor droppedOn = (ChargerActor)getOneIntersectingObject(ChargerActor.class);
        System.out.println(droppedOn);
        if(droppedOn != null && droppedOn != chargingOn && (droppedOn.charger.status == Charger.Status.FREE || droppedOn.charger.status == Charger.Status.ASSIGNED)){
            System.out.println("FOUND CHARGER");
            if(chargingOn != null) chargingOn.setFree();
            chargingOn = droppedOn;
            chargingOn.chargeCar(this);
        }else{
            System.out.println("no charger");
            if(chargingOn != null && droppedOn != chargingOn){
                chargingOn.setFree();
                chargingOn = null;
            }
            
        }
    }
}
