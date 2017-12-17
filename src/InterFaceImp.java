
/*
 * @author:加藤鹰
 * @date:2017年11月13日 上午9:50:59
 * @description:
 */

import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;

import Bean.Enity;
import Common.Common;
import Rule.Check;
import Show.Face;
import Show.MyPanel;
import Transfer.InterFace;

public class InterFaceImp implements InterFace {
    private static Vector<MyEnity> myAims = new Vector<>();
    private static List<MyEnity> badge = new ArrayList<>();
    private static ImageIcon[] walks = new ImageIcon[2];
    private static ImageIcon[] left_walks = new ImageIcon[2];
    private static ImageIcon[] right_walks = new ImageIcon[2];
    private static ImageIcon stand;
    private static int count = 0;
    private static int step = 0;
    private static boolean first = true;

    static {
        List<Enity> enities = Enity.createAimPoint();
        for (Enity enity : enities) myAims.add(new MyEnity(enity));
        stand = new ImageIcon("images/player_stand.png");
        for (int i = 0; i < walks.length; ++i) {
            walks[i] = new ImageIcon("images/player_walk_right_" + i + ".png");
            left_walks[i] = new ImageIcon("images/player_walk_left_" + i + ".png");
            right_walks[i] = new ImageIcon("images/player_walk_right_" + i + ".png");
        }
        bfs();
        badge.add(new MyEnity(25,14,48));
        badge.add(new MyEnity(25,13,49));
        badge.add(new MyEnity(24,12,52));
        badge.add(new MyEnity(23,11,54));
        badge.add(new MyEnity(23,10,55));
        badge.add(new MyEnity(22,9,57));
        badge.add(new MyEnity(21,8,59));
        badge.add(new MyEnity(24,8,60));
        badge.add(new MyEnity(25,8,61));
        badge.add(new MyEnity(25,7,61));
//14,25   depth=27 |13,25 d=28 | 12,24 d=30 |11,23 d=32|10,23 d=33|9,22 d=35|8,21 d=36|8,24 d=36 |8,25 d=37|7,25 d=38
        badge.sort(new MyEnityComparator());
    }

    private static void bfs() {
        System.out.println("bfs");
        LinkedList<MyEnity> que1 = new LinkedList<>();
        LinkedList<MyEnity> que2 = new LinkedList<>();
        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};
        int depth = 0;
        que1.add(new MyEnity(10, 10, depth));
        boolean que1empty = false;
        boolean[][] marked = new boolean[30][32];
        while (!que1.isEmpty() || !que2.isEmpty()) {
            if (que1empty != que1.isEmpty()) depth++;
            que1empty = que1.isEmpty();

            if (!que1.isEmpty()) {
                MyEnity enity = que1.pop();
                marked[enity.getX()][enity.getY()] = true;
                badge.add(enity);
                for (int i = 0; i < 4; ++i) {
                    MyEnity next = new MyEnity(enity.getX() + dx[i], enity.getY() + dy[i], depth + 1);
                    if (!myAims.contains(next) && !marked[next.getX()][next.getY()]) que2.add(next);
                }
            } else {
                MyEnity enity = que2.pop();
                badge.add(enity);
                for (int i = 0; i < 4; ++i) {
                    MyEnity next = new MyEnity(enity.getX() + dx[i], enity.getY() + dy[i], depth + 1);
                    if (!myAims.contains(next) && !marked[next.getX()][next.getY()]) que1.add(next);
                }
            }

        }
    }

    // 这个函数是负责画画的。画每个点当前的位置，目标的位置，通过jlabel来定位
    public JLabel[][] draw(List<Enity> now, List<Enity> aim, JLabel[][] jlabel) {

        // 这里你们可以把人物以图标的形式展现出来，也可以改善下棋盘框框的样子

        // 以下是画画的流程
        // 1.棋盘清空
        System.out.println("Width:" + jlabel[0][0].getWidth());
        System.out.println("Height:" + jlabel[0][0].getHeight());
        for (int i = 0; i < jlabel.length; i++) {
            for (int j = 0; j < jlabel[i].length; j++) {
                jlabel[i][j].setIcon(null);
                jlabel[i][j].setText("");
                jlabel[i][j].setBackground(Color.red);
            }
        }
        // 2.画目标位置(党徽)
        for (int i = 0; i < aim.size(); i++) {
            jlabel[aim.get(i).getX()][aim.get(i).getY()].setBackground(Color.GRAY);
        }
        if (now != null) {
            // 3. 画上现在的每个点的位置，这里可以增加图标

            ImageIcon icon = walks[step];
//            icon = getResizedImageIcon(icon, jlabel[0][0]);
            for (int i = 0; i < now.size(); i++) {
                int nx = now.get(i).getX();
                int ny = now.get(i).getY();
//                if (nx == aim.get(i).getX() && ny == aim.get(i).getY()) {
//                    System.out.println("nx=" + nx + " ny=" + ny
//                            + " ax=" + aim.get(i).getX() + " ay=" + aim.get(i).getY());
//                    jlabel[nx][ny].setIcon(stand);
//                } else
                if (aim.get(i).getY() > ny)
                    jlabel[nx][ny].setIcon(right_walks[step]);
                else
                    jlabel[nx][ny].setIcon(left_walks[step]);

//                jlabel[nx][ny].setText(
//                        jlabel[nx][ny].getText() + " " + now.get(i).getId() + "");
                if (first) jlabel[nx][ny].setBackground(Color.RED);
            }
            step = (step + 1) % walks.length;
            count++;
        }
        first = false;
        assert now != null;
        if (Check.isSuccess(now, aim) && count > 10) {
            count = 0;
            first = true;
            for (int i = 0; i < now.size(); i++) {
                int nx = now.get(i).getX();
                int ny = now.get(i).getY();
                jlabel[nx][ny].setIcon(null);
                jlabel[nx][ny].setBackground(Color.YELLOW);
                jlabel[nx][ny].updateUI();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            int predep = badge.get(0).getDepth();
            int curdep = 0;
            for (MyEnity enity : badge) {
                curdep = enity.getDepth();
                if (curdep != predep) {
                    jlabel[enity.getX()][enity.getY()].updateUI();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                jlabel[enity.getX()][enity.getY()].setBackground(Color.YELLOW);
                predep = curdep;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return jlabel;
    }

    private ImageIcon getResizedImageIcon(ImageIcon icon, JLabel label) {
        int imgWidth = icon.getIconWidth();
        int imgHeight = icon.getIconHeight();
        int conWidth = label.getWidth();
        int conHeight = label.getHeight();
        int reImgWidth;
        int reImgHeight;
        if (imgWidth / imgHeight >= conWidth / conHeight) {
            if (imgWidth > conWidth) {
                reImgWidth = conWidth;
                reImgHeight = imgHeight * reImgWidth / imgWidth;
            } else {
                reImgWidth = imgWidth;
                reImgHeight = imgHeight;
            }
        } else {
            if (imgWidth > conWidth) {
                reImgHeight = conHeight;
                reImgWidth = imgWidth * reImgHeight / imgHeight;
            } else {
                reImgWidth = imgWidth;
                reImgHeight = imgHeight;
            }
        }
        //这个是强制缩放到与组件(Label)大小相同
//        icon = new ImageIcon(icon.getImage()
//                .getScaledInstance(
//                        label.getWidth(),
//                        label.getHeight(),
//                        Image.SCALE_DEFAULT));
        //这个是按等比缩放
        icon = new ImageIcon(icon.getImage().getScaledInstance(reImgWidth, reImgHeight, Image.SCALE_DEFAULT));
        return icon;
    }

    //以下这些事初始化时调用的设置
    // 设置棋盘和格子的框架样子，就是每个矩形的border
    public Border setBorder() {
        //这里可以设置棋盘的格子的border样式
        //createEtchedBorder(BorderUIResource.EtchedBorderUIResource.RAISED);
        //createLoweredSoftBevelBorder();
//        BorderFactory.createEtchedBorder(BorderUIResource.EtchedBorderUIResource.LOWERED,
//                new Color(Integer.parseInt("660000",16)),
//                new Color(Integer.parseInt("cc0000",16)));
        return BorderFactory.createLineBorder(Color.BLACK, 1);
    }

    // 我直接把框架类扔给你美化吧
    public Face setFaceUI(Face face) {
        //这里能对整个游戏的外部框架，菜单栏进行美化，还能添加你觉得需要的其他容器
        JMenu menu = new JMenu("一般人你tm给我在这里加一个菜单试试！");
        JMenuItem item = new JMenuItem("撤销");
        JMenuBar bar = new JMenuBar();
        menu.add(item);
        bar.add(menu);
        try {
            Class<?> cls = Class.forName("Show.Face");
            Field menuBar = cls.getDeclaredField("menuBar");
            menuBar.setAccessible(true);
            menuBar.set(face, bar);
        } catch (Exception e) {
            e.printStackTrace();
        }

        face.getContentPane().add(bar);

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(face.getWidth(), 200));
        panel.setBackground(Color.GRAY);
        face.getContentPane().add(panel,"South");

        face.setResizable(false);

        return face;
    }

    // 我直接把棋盘类扔给你。让你美化算了
    public MyPanel setJPanelUI(MyPanel myPanel) {
        //这里能对棋盘jpanel进行美化

        return myPanel;
    }

    //这是时时调用的。每次线程都会调用
    // 图形化美化进度条，这里是时时更新的。
    public JProgressBar beautifyBar(JProgressBar bar) {
        //这里可以美化进度条，你可能需要的参数。我也提供了
        int allTime = Common.getAlltime();// 游戏的总时间
        int count = Common.getCount();// 游戏当前碰撞次数
        int useTime = Common.getUseTime();// 游戏已用时间
        bar.setBackground(Color.GRAY);
        bar.setForeground(Color.RED);
        bar.setString(String.valueOf(allTime - count - useTime));
        bar.setStringPainted(true);
        bar.setMinimumSize(new Dimension(500, 20));
        return bar;
    }
}
