
import greenfoot.World;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyWorld extends World {

    List<Charger> chargers;
    public MyWorld() throws IOException, ExecutionException, InterruptedException {
        super(720, 850, 1);
        
        
        // File path is passed as parameter
        //File file = new File(
        //    "/home/robin/docs/code/project2023-groep-3/simulator/config.txt");
 
        //BufferedReader br
        //    = new BufferedReader(new FileReader(file));
 
        String st;
        //Config.userids.clear();
        // there is character in a string
        /*while ((st = br.readLine()) != null){
            String[] line = st.split(",");
            Config.userids.add(line[0]);
            Config.userids.add(line[1]);
            // Print the string
            System.out.println(st);
        }*/
        
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
