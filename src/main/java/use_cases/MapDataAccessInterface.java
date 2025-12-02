package use_cases;

import javax.swing.ImageIcon;

public interface MapDataAccessInterface {

    ImageIcon fetchMapForCity(String city) throws Exception;
}
