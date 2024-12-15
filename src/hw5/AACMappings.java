package hw5;

import java.io.*;
import java.util.*;

/**
 * Creates a set of mappings of an AAC that has two levels,
 * one for categories and then within each category, it has
 * images that have associated text to be spoken. This class
 * provides the methods for interacting with the categories
 * and updating the set of images that would be shown and handling
 * interactions.
 * 
 * @author Catie Baker, Sherri Weitl-Harms & Jerome Bustarga
 */
public class AACMappings implements AACPage {
    private Map<String, AACCategory> categories;
    private AACCategory currentCategory;
    private AACCategory homeCategory;

    /**
     * Creates a set of mappings for the AAC based on the provided
     * file. The file is read in to create categories and fill each
     * of the categories with initial items. The file is formatted as
     * the text location of the category followed by the text name of the
     * category and then one line per item in the category that starts with
     * > and then has the file name and text of that image
     * 
     * @param filename the name of the file that stores the mapping information
     */
    public AACMappings(String filename) {
        categories = new HashMap<>();
        homeCategory = new AACCategory("");
        currentCategory = homeCategory;
        readMappingsFromFile(filename);
    }

    private void readMappingsFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            AACCategory category = null;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith(">")) {
                    String[] parts = line.split(" ", 2);
                    category = new AACCategory(parts[1]);
                    categories.put(parts[1], category);
                    homeCategory.addItem(parts[0], parts[1]);
                } else {
                    String[] parts = line.substring(1).split(" ", 2);
                    category.addItem(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Given the image location selected, it determines the action to be
     * taken. This can be updating the information that should be displayed
     * or returning text to be spoken. If the image provided is a category, 
     * it updates the AAC's current category to be the category associated 
     * with that image and returns the empty string. If the AAC is currently
     * in a category and the image provided is in that category, it returns
     * the text to be spoken.
     * 
     * @param imageLoc the location where the image is stored
     * @return if there is text to be spoken, it returns that information, otherwise
     *         it returns the empty string
     * @throws NoSuchElementException if the image provided is not in the current 
     *         category
     */
    public String select(String imageLoc) {
        if (currentCategory == homeCategory) {
            String categoryName = homeCategory.select(imageLoc);
            if (categories.containsKey(categoryName)) {
                currentCategory = categories.get(categoryName);
                return "";
            }
        } else if (currentCategory.hasImage(imageLoc)) {
            return currentCategory.select(imageLoc);
        }
        
        throw new NoSuchElementException("Image not found: " + imageLoc);
    }

    /**
     * Provides an array of all the images in the current category
     * 
     * @return the array of images in the current category; if there are no images,
     *         it should return an empty array
     */
    public String[] getImageLocs() {
        return currentCategory.getImageLocs();
    }

    /**
     * Resets the current category of the AAC back to the default
     * category
     */
    public void reset() {
        currentCategory = homeCategory;
    }

    /**
     * Writes the AAC mappings stored to a file. The file is formatted as
     * the text location of the category followed by the text name of the
     * category and then one line per item in the category that starts with
     * > and then has the file name and text of that image
     * 
     * @param filename the name of the file to write the AAC mapping to
     */
    public void writeToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, AACCategory> entry : categories.entrySet()) {
                String categoryName = entry.getKey();
                AACCategory category = entry.getValue();
                String categoryImage = homeCategory.select(categoryName);
                writer.write(categoryImage + " " + categoryName);
                writer.newLine();
                for (String imageLoc : category.getImageLocs()) {
                    writer.write(">" + imageLoc + " " + category.select(imageLoc));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the mapping to the current category (or the default category if
     * that is the current category)
     * 
     * @param imageLoc the location of the image
     * @param text the text associated with the image
     */
    public void addItem(String imageLoc, String text) {
        if (currentCategory == homeCategory) {
            AACCategory newCategory = new AACCategory(text);
            categories.put(text, newCategory);
            homeCategory.addItem(imageLoc, text);
        } else {
            currentCategory.addItem(imageLoc, text);
        }
    }

    /**
     * Gets the name of the current category
     * 
     * @return returns the current category or the empty string if 
     *         on the default category
     */
    public String getCategory() {
        return currentCategory.getCategory();
    }

    /**
     * Determines if the provided image is in the set of images that
     * can be displayed and false otherwise
     * 
     * @param imageLoc the location of the category
     * @return true if it is in the set of images that
     *         can be displayed, false otherwise
     */
    public boolean hasImage(String imageLoc) {
        if (currentCategory == homeCategory) {
            return homeCategory.hasImage(imageLoc);
        } else {
            return currentCategory.hasImage(imageLoc);
        }
    }
}