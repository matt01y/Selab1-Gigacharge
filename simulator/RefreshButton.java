import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * Write a description of class RefreshButton here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RefreshButton extends Draggable
{
    /**
     * Act - do whatever the RefreshButton wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        if(Greenfoot.mouseClicked(this)){
            List<ChargerActor> chargers = getWorld().getObjects(ChargerActor.class);
            for(int i = 0; i < chargers.size(); i++){
                chargers.get(i).update();
            }
        }
    }
}
