package cn.kevin.puzzle.model;

/**
 * 创建日期：2017/10/23.
 *
 * @author kevin
 */

public class Block {
    public Block(int position, int vPosition, int hPosition){
        this.position = position;
        this.vPosition = vPosition;
        this.hPosition = hPosition;
    }
    public int position;
    public int vPosition;
    public int hPosition;
}
