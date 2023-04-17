import greenfoot.Actor;
import greenfoot.Greenfoot;
import greenfoot.MouseInfo;
import greenfoot.World;

public class Draggable extends Actor {
    private boolean isGrabbed;
    private boolean mouseDown, dragging = false;
    private static int offsetX, offsetY, startingX, startingY, releaseX, releaseY;
    private MouseInfo mouse;
    
    void onDropped(){

    }
    void onDragStart(){

    }

    public void act()
    {
        if (Greenfoot.mousePressed(this) && !isGrabbed)
        {
            // grab the object
            isGrabbed = true;
            // the rest of this block will avoid this object being dragged UNDER other objects
            World world = getWorld();
            MouseInfo mi = Greenfoot.getMouseInfo();
            world.removeObject(this);
            world.addObject(this, mi.getX(), mi.getY());
            return;
        }
        // check for actual dragging of the object
        if ((Greenfoot.mouseDragged(this)) && isGrabbed)
        {
            // follow the mouse
            MouseInfo mi = Greenfoot.getMouseInfo();
            setLocation(mi.getX(), mi.getY());
            return;
        }
        // check for mouse button release
        if (Greenfoot.mouseDragEnded(this) && isGrabbed)
        {
            // release the object
            isGrabbed = false;
            onDropped();
            return;
        }
    }
}
