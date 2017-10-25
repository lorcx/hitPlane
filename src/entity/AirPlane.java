package entity;

import boot.ShootGame;
import flyobj.FlyingObject;
import interf.Enemy;

import java.util.Random;

/**
 * 敌飞机
 */
public class AirPlane extends FlyingObject implements Enemy{
    private int speed = 3; //移动步骤

    public AirPlane() {
        this.image = ShootGame.airplane;
        width = image.getWidth();
        height = image.getHeight();
        y = -height;
        Random rand = new Random();
        x = rand.nextInt(ShootGame.WIDTH - width);
    }

    /**
     * 越界处理
     * @return
     */
    @Override
    public boolean outOfBounds() {
        return y > ShootGame.HEIGHT;
    }

    /**
     * 移动
     */
    @Override
    public void step() {
        y += speed;
    }

    /**
     * 获取分数
     * @return
     */
    @Override
    public int getScore() {
        return 5;
    }
}
