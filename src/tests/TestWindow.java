import javax.swing.*;
import java.awt.*;

public class TestWindow {
    public static void main(String[] args) {
        System.out.println("Test starting - will show message dialog...");

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                "SkyBook GUI Test\n\nIf you see this message, Java GUI is working!\n\nClick OK to continue.",
                "GUI Test - Airline Reservation System",
                JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        });
    }
}
