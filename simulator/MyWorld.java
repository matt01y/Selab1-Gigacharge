
import greenfoot.World;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyWorld extends World {

    List<Charger> chargers;
    public MyWorld() throws IOException, ExecutionException, InterruptedException {
        super(720, 850, 1);
        setPaintOrder(CarActor.class, ChargerActor.class);
        System.out.println("start");
        refresh();
        System.out.println("ok");

        
    }
    

    void refresh(){
        removeObjects(getObjects(ChargerActor.class));
        removeObjects(getObjects(CarActor.class));
        chargers = Api.getChargers();
        //chargers = List.of(new Charger("fake", Charger.Status.CHARGING, "fake", "fake", "fake", Charger.UserType.NONUSER));
        System.out.println("api done");
        State.cars.clear();

        for(int i = 0; i < Config.userids.size(); i+=2){
            CarActor car = new CarActor(Config.userids.get(i), Config.userids.get(i + 1), i / 2);
            System.out.println(car);
            addObject(car, i * 45 + 100, getHeight() - car.getImage().getHeight());
        }
        
        for(int i = 0; i < chargers.size(); i++){
            System.out.println("add");
            addObject(new ChargerActor(chargers.get(i)), i * (10 + ChargerActor.width) + ChargerActor.width, ChargerActor.height / 2);
        }
        
        addObject(new RefreshButton(), getWidth() - 100, getHeight() - 500);

    }
}
