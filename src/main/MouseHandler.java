package main;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import javax.swing.SwingUtilities;

public class MouseHandler implements MouseMotionListener, MouseListener {

    public int mouseX, mouseY;
    public int clickX, clickY; // For mouse click coordinates
    public boolean rightButtonPressed, leftButtonPressed;

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        clickX = e.getX();
        clickY = e.getY();
        // Add additional click handling code here if needed
    }

    // Required empty implementations for the rest of MouseListener methods
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            leftButtonPressed = true;
        }
        if (SwingUtilities.isRightMouseButton(e)) {
            rightButtonPressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            leftButtonPressed = false;
        }
        if (SwingUtilities.isRightMouseButton(e)) {
            rightButtonPressed = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }
}
