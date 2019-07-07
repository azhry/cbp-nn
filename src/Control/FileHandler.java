/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Azhary Arliansyah
 */
public class FileHandler {
    
    private static String CURRENT_DIR;
    public static int NUM_FILES;
    public static Map<String, List<String>> LABELS = new HashMap<>();
    
    
    public static File[] readDirectoryContent(String path) {
        final File directory = new File(path);
        return directory.listFiles();
    }
    
    public static void readRecursive(String path) {
        
        File[] entries = FileHandler.readDirectoryContent(path);
        for (File entry : entries) {
            if (entry.isDirectory()) {
                FileHandler.CURRENT_DIR = entry.getName();
                FileHandler.readRecursive(path + "/" + entry.getName());
            }
            else {
                String key = Character.toString(entry.getName().charAt(0));
                List<String> filenames;
                if (FileHandler.LABELS.containsKey(key)) {
                    filenames = FileHandler.LABELS.get(key);
                }
                else {
                    filenames = new ArrayList<>();
                }
                filenames.add(entry.getName());
                FileHandler.LABELS.put(key, filenames);
                FileHandler.NUM_FILES++;
            }
        }
        
    }
    
    public static void read(String path) {
        FileHandler.NUM_FILES = 0;
        FileHandler.LABELS = new HashMap<>();
        FileHandler.readRecursive(path);
    }
}
