package cn.kevin.puzzle;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.kevin.puzzle.model.Block;

/**
 * 创建日期：2017/10/23.
 *
 * @author kevin
 */
class DataHelper {
    static final int N = -1;
    static final int L = 0;
    static final int T = 1;
    static final int R = 2;
    static final int B = 3;
    private static final String TAG = DataHelper.class.getSimpleName();

    private int squareRootNum;
    private List<Block> models;

    DataHelper(){
        models = new ArrayList<>();
    }

    private void reset() {
        models.clear();
        int position = 0;
        for (int i = 0; i< squareRootNum; i++){
            for (int j = 0; j < squareRootNum; j++){
                models.add(new Block(position, i, j));
                position ++;
            }
        }
    }

    void setSquareRootNum(int squareRootNum){
        this.squareRootNum = squareRootNum;
        reset();
    }

    /**
     * 将索引出的model的值与空白model的值互换。
     */
    boolean swapValueWithInvisibleModel(int index){
        Block formModel = models.get(index);
        Block invisibleModel = models.get(0);
        swapValue(formModel, invisibleModel);
        return isCompleted();
    }

    /**
     * 交换两个model的值
     */
    private void swapValue(Block model, Block invisibleModel) {

        int position = model.position;
        int hPosition = model.hPosition;
        int vPosition = model.vPosition;

        model.position = invisibleModel.position;
        model.hPosition = invisibleModel.hPosition;
        model.vPosition = invisibleModel.vPosition;

        invisibleModel.position = position;
        invisibleModel.hPosition = hPosition;
        invisibleModel.vPosition = vPosition;
    }

    /**
     * 判断是否拼图完成。
     */
    private boolean isCompleted(){
        int num = squareRootNum * squareRootNum;
        for (int i = 0; i < num; i++){
            Block model = models.get(i);
            if(model.position != i){
                return false;
            }
        }
        return true;
    }

    public Block getModel(int index){
        return models.get(index);
    }

    /**
     * 通过给定的位置获取model的索引
     */
    private int getIndexByCurrentPosition(int currentPosition){
        int num = squareRootNum * squareRootNum;
        for (int i = 0; i < num; i++) {
            if(models.get(i).position == currentPosition)
                return i;
        }
        return -1;
    }

    /**
     * 随机查询出空白位置周围的一个model的索引。
     */
    public int findNeighborIndexOfInvisibleModel() {
        Block invisibleModel = models.get(0);
        int position = invisibleModel.position;
        int x = position % squareRootNum;
        int y = position / squareRootNum;
        int direction = new Random(System.nanoTime()).nextInt(4);
        Log.d(TAG, "direction " + direction);
        switch (direction){
            case L:
                if(x != 0)
                    return getIndexByCurrentPosition(position - 1);
            case T:
                if(y != 0)
                    return getIndexByCurrentPosition(position - squareRootNum);
            case R:
                if(x != squareRootNum - 1)
                    return getIndexByCurrentPosition(position + 1);
            case B:
                if(y != squareRootNum - 1)
                    return getIndexByCurrentPosition(position + squareRootNum);
        }
        return findNeighborIndexOfInvisibleModel();
    }

    /**
     * 获取索引处model的可移动方向，不能移动返回 -1。
     */
    int getScrollDirection(int index){

        Block model = models.get(index);
        int position = model.position;

        //获取当前view所在位置的坐标 x y
        /*
         *      * * * *
         *      * o * *
         *      * * * *
         *      * * * *
         */
        int x = position % squareRootNum;
        int y = position / squareRootNum;
        int invisibleModelPosition = models.get(0).position;

        /*
         * 判断当前位置是否可以移动，如果可以移动就return可移动的方向。
         */

        if(x != 0 && invisibleModelPosition == position - 1)
            return L;

        if(x != squareRootNum - 1 && invisibleModelPosition == position + 1)
            return R;

        if(y != 0 && invisibleModelPosition == position - squareRootNum)
            return T;

        if(y != squareRootNum - 1 && invisibleModelPosition == position + squareRootNum)
            return B;

        return N;
    }
}
