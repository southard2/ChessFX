/**
 * Title: Chess 
 * Files: Main.java, Tile.java, ValueList.java
 * Start Date: 7/17/2020 
 * End Date: - 
 * Author: Danny Southard
 * 
 * Note - I completed this project mostly on my own, but did use google to resolve issues I had in
 * figuring out some of JavaFX's mechanics. I did not copy any code from the internet, a part from
 * tiny bits such as the color red '#ff0000' used in the tryAgain label that I could not figure out
 * otherwise. I do not intend to make any money off of this project, this is/has been a passion
 * project and a way for me to challenge myself.
 */
package application;

import java.util.ArrayList;

/**
 * ValueList - ArrayList with addGreatest and addLowest methods to help cut down on time
 *
 * @author Danny Southard
 */
public class ValueList<Integer> extends ArrayList<Integer> {

  /**
   * Adds a value to the array at a given index only if it is the greatest value found from the
   * index to the end of the list
   * 
   * @param index - the index of the greatest value in the segment
   * @param value - the value being added (if it is greater than the value at the given index)
   */
  public void addGreatest(int index, Integer value) {
    if (this.size() <= index) {
      this.add(value);
    } else {
      if ((int)this.get(index) < (int)value) {
        this.remove(index);
        this.add(index, value);
      } 
    }
  }
  
  /**
   * Adds a value to the array at a given index only if it is lower than the value at the given index
   * 
   * @param index - the index of the lowest value in the segment
   * @param value - the value being added (if it is lesser than the value at the given index)
   */
  public void addLowest(int index, Integer value) {
    if (this.size() <= index) {
      this.add(value);
    } else {
      if ((int)this.get(index) > (int)value) {
        this.remove(index);
        this.add(index, value);
      } 
    }
  }
}
