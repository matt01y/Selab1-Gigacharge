import greenfoot.Actor;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;
import greenfoot.MouseInfo;
import java.util.List;


public class ChargerActor extends Draggable
{
    public final GreenfootImage image_notcharging = new GreenfootImage("new/transparent_3.png");
    public final GreenfootImage image_charging = new GreenfootImage("new/transparent_2.png");
    public final GreenfootImage image_assigned = new GreenfootImage("new/transparent_1.png");
    
    public static int width = 100;
    public static int height = 200;


    public Charger charger;
    public CarActor carCharging = null;
    private boolean dorefresh = true;
    

    /**
     * Act - do whatever the WorldImage wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public ChargerActor(Charger mcharger){
        super();
        charger = mcharger;
        dorefresh = true;
    }
    
    public void act(){
        if(dorefresh){
            refresh();
            dorefresh = false;
        }
    }

    void refresh(){
        if(charger.status == Charger.Status.FREE){
            setImage(image_notcharging);
        }else if(charger.status == Charger.Status.CHARGING){
            if(charger.userType == Charger.UserType.USER){
                System.out.println(charger.userId);
                System.out.println("Cars:");
                System.out.println(State.cars);
                List<CarActor> cars = getWorld().getObjects(CarActor.class);
                for(int i = 0; i < cars.size(); i++){
                    System.out.println(cars.get(i).uid);
                    if(cars.get(i).uid.equals(charger.userId) && cars.get(i) != carCharging){
                        System.out.println("FOUND CHARGER");
                        System.out.println(cars.get(i));
                        chargeCar(cars.get(i));
                        break;
                    }
                }
            }
            
            setImage(image_charging);
        }else if(charger.status == Charger.Status.ASSIGNED){
            setImage(image_assigned);
        }
        else{
            System.err.println("foute charger status!");
            setImage(image_notcharging);
        }
        getImage().drawString(charger.description, 0,  45);
    }
    
    void setFree(){
        System.out.println("FREEING");
        carCharging = null;
        charger.status = Charger.Status.FREE;
        charger.userType = Charger.UserType.NONE;
        charger.userId = "NONE";
        refresh();
        
        Api.updateCharger(this.charger);
    }
    
    void chargeCar(CarActor car){
        carCharging = car;
        car.chargingOn = this;
        charger.status = Charger.Status.CHARGING;
        charger.userType = Charger.UserType.USER;
        charger.userId = car.uid;
        refresh();
        car.setLocation(getX() + 0, getY() + 0);
        
        Api.updateCharger(this.charger);
    }
    
    void update(){
        try{
            System.out.println("updating...");
            charger = Api.getCharger(charger.id);
            refresh();
        }catch(Exception e){
            System.out.println("charger update failed");
        }
        
    }
}