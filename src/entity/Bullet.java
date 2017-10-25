package entity;

import boot.ShootGame;
import flyobj.FlyingObject;

/**
 * 子弹类:是飞行物
 */
public class Bullet extends FlyingObject{
    private int speed = 3;// 移动速度

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
        this.image = ShootGame.bullet;
    }

    /**
     * 越界处理
     * @return
     */
    @Override
    public boolean outOfBounds() {
        return y < -height;
    }

    /**
     * 走一步
     */
    @Override
    public void step() {
        y-=speed;
    }
}
