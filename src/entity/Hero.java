package entity;

import boot.ShootGame;
import flyobj.FlyingObject;
import java.awt.image.BufferedImage;

/**
 * 英雄机
 */
public class Hero extends FlyingObject{
    private BufferedImage[] images = {};// 英雄机图片
    private int index = 0;//英雄机图片切换索引

    private int doubleFire;// 双倍火力
    private int life;// 命

    /**
     * 初始化数据
     */
    public Hero() {
        life = 3;// 初始化为3条命
        images = new BufferedImage[]{ShootGame.hero0, ShootGame.hero1};// 英雄图片数组
        doubleFire = 0;//初始化火力为0
        image = ShootGame.hero0;
        width = image.getWidth();
        height = image.getHeight();
        x = 150;
        y = 400;
    }

    /**
     * 当前物体移动一下，相对距离x，y鼠标位置
     * @param x
     * @param y
     */
    public void moveTo(int x, int y) {
        this.x = x - width / 2;
        this.y = y - height / 2;
    }

    /**
     * 获取双倍火力
     * @return
     */
    public int isDoubleFire() {
        return doubleFire;
    }

    /**
     * 设置双倍火力
     * @param doubleFire
     */
    public void setDoubleFire(int doubleFire) {
        this.doubleFire = doubleFire;
    }

    /**
     * 设置双倍火力
     */
    public void addDoubleFire() {
        doubleFire = 40;
    }

    /**
     * 加命
     */
    public void addLife() {
        life++;
    }

    /**
     * 减命
     */
    public void subtractLife() {
        life--;
    }

    /**
     * 获得命
     * @return
     */
    public int getLife() {
        return life;
    }

    /**
     * 越界处理
     * @return
     */
    @Override
    public boolean outOfBounds() {
        return false;
    }

    /**
     * 移动
     */
    @Override
    public void step() {
        if (images.length > 0) {
            // 切换图片hero0, hero1  发射10颗子弹就切图
            image = images[index++ / 10 % images.length];
        }
    }

    /**
     * 发射子弹
     * @return
     */
    public Bullet[] shoot() {
        int xStep = width / 4;//4半
        int yStep = 20;// 步
        if (doubleFire > 0) {// 双倍火力
            Bullet[] bullets = new Bullet[2];
            bullets[0] = new Bullet(x + xStep, y - yStep);// y- yStep 子弹距飞机的位置
            bullets[1] = new Bullet(x + 3 * xStep, y - yStep);// y- yStep 子弹距飞机的位置
            return bullets;
        } else {// 单倍火力
            Bullet[] bullets = new Bullet[1];
            bullets[0] = new Bullet(x + 2 * xStep, y - yStep);
            return bullets;
        }
    }

    /**
     * 碰撞算法
     * @return
     */
    public boolean hit(FlyingObject other) {
        int x1 = other.getX() - this.width / 2;// x坐标最小距离
        int x2 = other.getX() - this.width / 2 + other.getWidth();// x坐标最大距离
        int y1 = other.getY() - this.height / 2;// y坐标最小距离
        int y2 = other.getY() - this.height / 2 + other.getHeight();// y坐标最大距离

        int herox = this.x + this.width / 2;// 英雄机坐标中心点距离
        int heroy = this.y + this.height / 2;// 英雄机y坐标中心点距离

        return herox > x1 && herox < x2 && heroy > y1 && heroy < y2;//区间范围内为撞上了
    }

    /*public static void main(String[] args) {
        int len = 2;
        for (int i = 0; i < 301; i++) {
//            System.out.println((int) i++ / 10 % 2);
            System.out.println(i % 30);
        }
    }*/
}
