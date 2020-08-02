/**
 * Title: Tile.java
 * Files:  
 * Program function:
 * Author: 
 * Email: 
 * Class:
 * Lecture No.: 
 * Semester:
 */
package application;

/**
 * Tile - Purpose of class
 *
 * @author Danny Southard
 */
public class Tile {
  public int[] values;
  
  /**
   * Constructor for the tile using given integers representing the color of the tile, the color of
   * the piece on the tile, and the piece.
   * 
   * Color:         Piece Color:
   * 0 - Light      0 - no piece on tile
   * 1 - Dark       1 - white
   *                2 - black
   * Piece:
   * 0 - Pawn       3 - Rook
   * 1 - Knight     4 - Queen
   * 2 - Bishop     5 - King
   */
  public Tile(int color, int pieceColor, int piece) {
    values = new int[3];
    values[0] = color;
    values[1] = pieceColor;
    values[2] = piece;
  }
  
  /**
   * Getter method for the int array of values for the given tile
   * 
   * @return the int array of values stored in the tile
   */
  public int[] getValues() {
    return values;
  }
  
  /**
   * Sets a new piece to the tile when a piece is moved off of/onto the tile
   * 
   * @param pieceColor - the color of the piece now on the tile
   * @param piece - the type of piece now on the tile
   */
  public void setPiece(int pieceColor, int piece) {
    values[1] = pieceColor;
    values[2] = piece;
  }
}
