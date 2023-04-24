import greenfoot.GreenfootImage;

public class CarActor extends Draggable{
    public final GreenfootImage carimage = new GreenfootImage("4.png");
    public String uid;
    public ChargerActor chargingOn = null;
    public String name = "lege naam";
    public CarActor(String mname, String muid){
        super();
        uid = muid;
        name = mname;
        setImage(carimage);
        getImage().drawString(name, 0,  45);
    }

    @Override
    void onDropped() {
        super.onDropped();
        System.out.println("DROPPED on");
        ChargerActor droppedOn = (ChargerActor)getOneIntersectingObject(ChargerActor.class);
        System.out.println(droppedOn);
        if(droppedOn != null && droppedOn != chargingOn && (droppedOn.charger.status == Charger.Status.FREE || droppedOn.charger.status == Charger.Status.ASSIGNED)){
            System.out.println("FOUND CHARGER");
            chargingOn = droppedOn;
            chargingOn.chargeCar(this);
        }else{
            System.out.println("no charger");
            if(chargingOn != null){
                chargingOn.setFree();
                chargingOn = null;
            }
            
        }
    }
}
