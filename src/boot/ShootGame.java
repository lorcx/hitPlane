package boot;

import entity.*;
import flyobj.FlyingObject;
import interf.Award;
import interf.Enemy;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 游戏启动类
 */
public class ShootGame extends JPanel{
    public static final int WIDTH = 400;// 面板宽
    public static final int HEIGHT = 654;// 面板高
    /*游戏状态: START RUNNING PAUSE GAME_OVER*/
    private int state;
    private static final int START = 0;
    private static final int RUNNING = 1;
    private static final int PAUSE = 2;
    private static final int GAME_OVER = 3;

    private int score = 0;// 得分
    private Timer timer;// 定时器
    private int intervel = 10;// 时间间隔（毫秒）

    public static BufferedImage background;
    public static BufferedImage start;
    public static BufferedImage airplane;
    public static BufferedImage bee;
    public static BufferedImage bullet;
    public static BufferedImage hero0;
    public static BufferedImage hero1;
    public static BufferedImage pause;
    public static BufferedImage gameover;

    private FlyingObject[] flyings = {};// 敌机数组
    private Bullet[] bullets = {};// 子弹数组
    private Hero hero = new Hero();// 英雄机

    /**
     * 静态初始化代码，初始化图片资源
     */
    static {
        try {
            background = ImageIO.read(ShootGame.class.getResource("/pic/background.png"));
            start = ImageIO.read(ShootGame.class.getResource("/pic/start.png"));
            airplane = ImageIO.read(ShootGame.class.getResource("/pic/airplane.png"));
            bee = ImageIO.read(ShootGame.class.getResource("/pic/bee.png"));
            bullet = ImageIO.read(ShootGame.class.getResource("/pic/bullet.png"));
            hero0 = ImageIO.read(ShootGame.class.getResource("/pic/hero0.png"));
            hero1 = ImageIO.read(ShootGame.class.getResource("/pic/hero1.png"));
            pause = ImageIO.read(ShootGame.class.getResource("/pic/pause.png"));
            gameover = ImageIO.read(ShootGame.class.getResource("/pic/gameover.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("飞机大战");// 窗体标题
        ShootGame game = new ShootGame();// 面板对象
        frame.add(game);// 将面板添加到JFrame
        frame.setSize(WIDTH, HEIGHT);// 设置大小
        frame.setAlwaysOnTop(true);// 设置总在最上
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 默认关闭操作
        frame.setIconImage(new ImageIcon("images/icon.jpg").getImage());// 设置窗体图标
        frame.setLocationRelativeTo(null);// 设置初始窗体位置
        frame.setVisible(true);// 尽快调用paint

        game.action();// 启动执行

    }

    /**
     * 启动执行代码
     */
    private void action() {
        // 鼠标监听事件
        MouseAdapter l = new MouseAdapter() {
            /**
             * 鼠标移动
             * @param e
             */
            @Override
            public void mouseMoved(MouseEvent e) {
                // 运行状态下移动英雄机--随鼠标位置
                if (state == RUNNING) {
                    int x = e.getX();
                    int y = e.getY();
                    hero.moveTo(x, y);
                }
            }

            /**
             * 鼠标进入
             * @param e
             */
            @Override
            public void mouseEntered(MouseEvent e) {
                // 暂停状态下运行
                if (state == PAUSE) {
                    state = RUNNING;
                }
            }

            /**
             * 鼠标退出
             * @param e
             */
            @Override
            public void mouseExited(MouseEvent e) {
                if (state == RUNNING) {
                    state = PAUSE;
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
               switch (state) {
                   case START:
                       state = RUNNING;
                       break;
                   case GAME_OVER:
                       flyings = new FlyingObject[0];// 清空飞行物
                       bullets = new Bullet[0];//清空子弹
                       hero = new Hero();// 重置英雄机
                       state = START;// 启动状态
                       score = 0;// 清空成绩
                       break;
               }
            }
        };

        this.addMouseListener(l);// 处理鼠标点击操作
        this.addMouseMotionListener(l);// 处理鼠标滑动操作

        timer = new Timer();// 主流程控制
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (state == RUNNING) {
                    enterAction();//飞行物入场
                    stepAction();//走一步
                    shootAction();//英雄机射击
                    bangAction();//子弹打飞行物
                    outOfBoundsAction();//删除越界的飞行物及子弹
                    checkGameOverAction();//检查游戏结束
                }
                repaint();// 重绘，调用paint（）方法
            }
        }, intervel, intervel);
    }

    int flyEnteredIndex = 0;// 飞行物入场次数

    /**
     * 飞行物入场
     */
    private void checkGameOverAction() {
        if (isGameOver()) {
            state = GAME_OVER;
        }
    }

    /**
     * 随机生成飞行物
     * @return
     */
    private FlyingObject nextOne() {
        Random random = new Random();
        int type = random.nextInt(20);//[0,20)
        if (type < 4) {
            return new Bee();
        } else {
            return new AirPlane();
        }
    }

    /**
     * 删除越界飞行器及子弹
     */
    private void outOfBoundsAction() {
        int index = 0;
        FlyingObject[] flyingLives = new FlyingObject[flyings.length];// 活着的飞行物
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject f = flyings[i];
            if (!f.outOfBounds()) {
                flyingLives[index++] = f;// 不越界的留着
            }
        }
        flyings = Arrays.copyOf(flyingLives, index);//将不越界的飞行物留下

        index = 0;//重置
        Bullet[] bulletLives = new Bullet[bullets.length];
        for (int i = 0; i < bullets.length; i++) {
            Bullet b = bullets[i];
            if (!b.outOfBounds()) {
                bulletLives[index++] = b;
            }
        }
        bullets = Arrays.copyOf(bulletLives, index);// 将不越界的子弹留着
    }

    /**
     * 子弹与飞行物碰撞测试
     */
    private void bangAction() {
        for (int i = 0; i < bullets.length; i++) {
            Bullet b = bullets[i];
            bang(b);// 子弹和飞行物之间的碰撞检测
        }
    }

    /**
     * 子弹和飞行物之间的碰撞检测
     * @param bullet
     */
    private void bang(Bullet bullet) {
        int index = -1;// 击中的飞行物索引
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject obj = flyings[i];
            if (obj.shootBy(bullet)) {// 判断是否被击中
                index = i;// 记录被击中的飞行物索引
                break;
            }
        }

        // 有击中的物体
        if (index != -1) {
            FlyingObject one = flyings[index];//记录被击中的物体
            FlyingObject temp = flyings[index];// 被击中的飞行物与最后一个飞行物交换
            flyings[index] = flyings[flyings.length - 1];
            flyings[flyings.length - 1] = temp;
            flyings = Arrays.copyOf(flyings, flyings.length - 1);// 删除最后一个飞行物
            // 检查one的类型 （敌人加分，获取奖励）
            if (one instanceof Enemy) {
                Enemy e = (Enemy) one;
                score += e.getScore();//加分
            } else {
                Award a = (Award) one;
                int type = a.getType();// 获取奖励类型
                switch (type) {
                    case 1://双倍活力
                        hero.addDoubleFire();
                        break;
                    case 2://设置加命
                        hero.addLife();
                        break;
                }
            }
        }
    }

    int shootIndex; //射击次数

    /**
     * 射击
     */
    private void shootAction() {
        shootIndex++;
        if (shootIndex % 30 == 0) {// 300毫秒发射一颗
            Bullet[] bs = hero.shoot();//英雄打出的子弹
            bullets = Arrays.copyOf(bullets, bullets.length + bs.length);// 扩容
            // 追加数组
            System.arraycopy(bs, 0, bullets, bullets.length - bs.length, bs.length);
        }
    }

    /**
     * 走一步
     */
    private void stepAction() {
        for (int i = 0; i < flyings.length; i++) {// 飞行物走一步
            flyings[i].step();
        }

        for (int i = 0; i< bullets.length; i++) {// 子弹走一步
            Bullet b = bullets[i];
            b.step();
        }
        hero.step();// 英雄走一步
    }

    /**
     * 飞行器物体入场
     */
    private void enterAction() {
        flyEnteredIndex++;
        if (flyEnteredIndex % 40 == 0) {// 400毫秒生成一个飞行物
            FlyingObject obj = nextOne();// 随机生成一个飞行物
            flyings = Arrays.copyOf(flyings, flyings.length + 1);
            flyings[flyings.length - 1] = obj;
        }
    }

    /**
     * 检查游戏是否结束
     * @return
     */
    public boolean isGameOver() {
       for (int i = 0; i < flyings.length; i++) {
           int index = -1;
           FlyingObject obj = flyings[i];
           if (hero.hit(obj)) { // 检查英雄机与飞行物是否碰撞
               hero.subtractLife();//减命
               hero.setDoubleFire(0);//双倍火力解除
               index = i;
           }
           if (index != -1) {
               FlyingObject t = flyings[index];
               flyings[index] = flyings[flyings.length - 1];
               flyings[flyings.length - 1] = t;// 碰上的与最后一个飞行物交换
               flyings = Arrays.copyOf(flyings, flyings.length - 1);// 删除碰撞物体
           }
       }
       return hero.getLife() <= 0;
    }

    /**
     * 画
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        g.drawImage(background, 0, 0, null);
        paintHero(g);//画英雄机
        paintBullets(g);//画子弹
        paintFlyingObjects(g);//画飞行物
        paintScore(g);//画分数
        paintState(g);//画游戏状态
    }

    /**
     * 画游戏状态
     * @param g
     */
    private void paintState(Graphics g) {
        switch(state) {
            case START:
                g.drawImage(start, 0, 0, null);
                break;
            case PAUSE:
                g.drawImage(pause, 0, 0, null);
                break;
            case GAME_OVER:// 游戏终止
                g.drawImage(gameover, 0, 0, null);
                break;
        }
    }

    /**
     * 画分数
     * @param g
     */
    private void paintScore(Graphics g) {
        int x = 10;// x坐标
        int y = 25;// y坐标
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 22);
        g.setColor(new Color(0xFF0000));
        g.setFont(font);
        g.drawString("SCORE:" + score, x, y);// 画分数
        y = y + 20;
        g.drawString("LIFE:" + hero.getLife(), x, y);//画命
    }

    /**
     * 画飞行物
     * @param g
     */
    private void paintFlyingObjects(Graphics g) {
        for (int i = 0; i < flyings.length; i++) {
            FlyingObject f = flyings[i];
            g.drawImage(f.getImage(), f.getX(), f.getY(),null);
        }
    }

    /**
     * 画子弹
     * @param g
     */
    private void paintBullets(Graphics g) {
        for (int i = 0; i < bullets.length; i++) {
            Bullet b = bullets[i];
            g.drawImage(b.getImage(), b.getX() - b.getWidth() / 2, b.getY(), null);
        }
    }

    /**
     * 画英雄机
     * @param g
     */
    private void paintHero(Graphics g) {
        g.drawImage(hero.getImage(), hero.getX(), hero.getY(), null);
    }
}
