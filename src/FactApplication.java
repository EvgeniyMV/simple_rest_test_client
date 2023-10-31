
import client.FactClient;
import gui.FactGraphicUserInterface;
import javax.swing.*;

public class FactApplication {
    public static void main(String[] args) throws InterruptedException {
        FactClient client = new FactClient();
        FactGraphicUserInterface fgui = new FactGraphicUserInterface(client);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                fgui.createGUI();
            }
        });
    }
}
