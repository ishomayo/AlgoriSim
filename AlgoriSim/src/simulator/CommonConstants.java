import java.awt.*;
import java.io.File;
import java.net.URL;

public class CommonConstants {
    // Define resource paths using a more robust method
    private static final String BASE_PATH;
    
    static {
        // Try to get the resources directory from the classpath
        URL resourceUrl = CommonConstants.class.getClassLoader().getResource("simulator/resources");
        
        if (resourceUrl != null) {
            // Use the URL path if available
            BASE_PATH = resourceUrl.getPath() + File.separator;
        } else {
            // Fallback to a relative path from the current working directory
            String currentDir = System.getProperty("user.dir");
            
            // Check if we're running from the project root or from within the src directory
            if (new File(currentDir + "/AlgoriSim/src/simulator/resources").exists()) {
                BASE_PATH = "AlgoriSim/src/simulator/resources/";
            } else if (new File(currentDir + "/src/simulator/resources").exists()) {
                BASE_PATH = "src/simulator/resources/";
            } else if (new File(currentDir + "/simulator/resources").exists()) {
                BASE_PATH = "simulator/resources/";
            } else {
                // If none of the above paths work, try to use a resource folder in the current directory
                BASE_PATH = "resources/";
                
                // Create the resources directory if it doesn't exist
                new File(BASE_PATH).mkdirs();
                
                System.out.println("Warning: Resource directory not found. Using " + 
                                  new File(BASE_PATH).getAbsolutePath() + " instead.");
            }
        }
        
        System.out.println("Using resource path: " + BASE_PATH);
    }

    // File paths using the determined BASE_PATH
    public static final String selectBG = BASE_PATH + "selection.jpg";
    public static final String Lobby = BASE_PATH + "Lobby.png";
    public static final String DataInput = BASE_PATH + "DataInputScreen.jpg";
    public static final String startDefault = BASE_PATH + "start_default.png";
    public static final String startHover = BASE_PATH + "start_hover.png";
    public static final String startClicked = BASE_PATH + "start_clicked.png";
    public static final String helpDefault = BASE_PATH + "help_default.png";
    public static final String helpHover = BASE_PATH + "help_hover.png";
    public static final String helpClicked = BASE_PATH + "help_clicked.png";
    public static final String creditsDefault = BASE_PATH + "credits_default.png";
    public static final String creditsHover = BASE_PATH + "credits_hover.png";
    public static final String creditsClicked = BASE_PATH + "credits_clicked.png";
    public static final String exitDefault = BASE_PATH + "exit_default.png";
    public static final String exitHover = BASE_PATH + "exit_hover.png";
    public static final String exitClicked = BASE_PATH + "exit_clicked.png";
    public static final String randomDefault = BASE_PATH + "random_default.png";
    public static final String randomHover = BASE_PATH + "random_hover.png";
    public static final String randomClicked = BASE_PATH + "random_clicked.png";
    public static final String userinpDefault = BASE_PATH + "userinp_default.png";
    public static final String userinpHover = BASE_PATH + "userinp_hover.png";
    public static final String userinpClicked = BASE_PATH + "userinp_clicked.png";
    public static final String fileDefault = BASE_PATH + "file_default.png";
    public static final String fileHover = BASE_PATH + "file_hover.png";
    public static final String fileClicked = BASE_PATH + "file_clicked.png";
    public static final String backDefault = BASE_PATH + "back_default.png";
    public static final String backClicked = BASE_PATH + "back_clicked.png";
    public static final String FCFSDefault = BASE_PATH + "FCFS_default.png";
    public static final String FCFSClicked = BASE_PATH + "FCFS_clicked.png";
    public static final String RRDefault = BASE_PATH + "RR_default.png";
    public static final String RRClicked = BASE_PATH + "RR_clicked.png";
    public static final String SJFPreDefault = BASE_PATH + "SJFPre_default.png";
    public static final String SJFPreClicked = BASE_PATH + "SJFPre_clicked.png";
    public static final String SJFNonDefault = BASE_PATH + "SJFNon_default.png";
    public static final String SJFNonClicked = BASE_PATH + "SJFNon_clicked.png";
    public static final String PrioPreDefault = BASE_PATH + "PrioPre_default.png";
    public static final String PrioPreClicked = BASE_PATH + "PrioPre_clicked.png";
    public static final String PrioNonDefault = BASE_PATH + "PrioNon_default.png";
    public static final String PrioNonClicked = BASE_PATH + "PrioNon_clicked.png";
    public static final String randomBG = BASE_PATH + "RandomBG.jpg";
    public static final String UserDBG = BASE_PATH + "User-definedBG.jpg";
    public static final String FileBG = BASE_PATH + "FileBG.jpg";
    public static final String splash = BASE_PATH + "splash.gif";
    public static final String help = BASE_PATH + "help.jpg";
    public static final String credits = BASE_PATH + "credits.jpg";
    public static final String BG = BASE_PATH + "BG.jpg";
    public static final String genDefault = BASE_PATH + "gen_default.png";
    public static final String genHover = BASE_PATH + "gen_hover.png";
    public static final String genClicked = BASE_PATH + "gen_clicked.png";
    public static final String contDefault = BASE_PATH + "cont_default.png";
    public static final String contHover = BASE_PATH + "cont_hover.png";
    public static final String contClicked = BASE_PATH + "cont_clicked.png";
    public static final String addprocDefault = BASE_PATH + "addproc_default.png";
    public static final String addprocHover = BASE_PATH + "addproc_hover.png";
    public static final String addprocClicked = BASE_PATH + "addproc_clicked.png";
    public static final String chooseDefault = BASE_PATH + "choose_default.png";
    public static final String chooseHover = BASE_PATH + "choose_hover.png";
    public static final String chooseClicked = BASE_PATH + "choose_clicked.png";
    public static final String stopDefault = BASE_PATH + "stop_default.png";
    public static final String stopHover = BASE_PATH + "stop_hover.png";
    public static final String stopClicked = BASE_PATH + "stop_clicked.png";
    public static final String homeDefault = BASE_PATH + "home_default.png";
    public static final String homeHover = BASE_PATH + "home_hover.png";
    public static final String homeClicked = BASE_PATH + "home_clicked.png";
    public static final String removeDefault = BASE_PATH + "remove_default.png";
    public static final String removeHover = BASE_PATH + "remove_hover.png";
    public static final String removeClicked = BASE_PATH + "remove_clicked.png";

    // Utility method to load images
    public static Image loadImage(String path) {
        try {
            return Toolkit.getDefaultToolkit().getImage(path);
        } catch (Exception e) {
            System.err.println("Failed to load image: " + path);
            e.printStackTrace();
            return null;
        }
    }
}