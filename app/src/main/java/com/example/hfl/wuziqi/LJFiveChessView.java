package com.example.hfl.wuziqi;

/**
 * Created by hfl on 2018/4/20.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LJFiveChessView extends View implements View.OnTouchListener {

    //画笔
    private Paint paint;
    private Canvas canvas;
    //棋子数组
    private int[][] chessArray;
    //当前下棋顺序(默认白棋先下)
    private boolean isWhite =true;
    //游戏是否结束
    private boolean isGameOver = false;

    //bitmap
    private Bitmap whiteChess;
    private Bitmap blackChess;
    //Rect
    private Rect rect;
    //棋盘宽高
    private float len;
    //棋盘格数
    private int GRID_NUMBER = 15;
    //每格之间的距离
    private float preWidth;
    //边距
    private float offset;
    //回调
    private GameCallBack callBack;
    //当前黑白棋胜利次数
    private int whiteChessCount, blackChessCount;

    //是否是玩家的回合
    private boolean isYourBout = true;
    // 是否联机成功开始游戏
    private boolean isStart = false;
    private boolean isBlack = true;	// 是否轮到黑棋
    //玩家执子颜色
    private int oneChess = WHITE_CHESS;
    private int oneScore = 0,otherScore = 0;

    private DataInputStream din = null;
    private DataOutputStream dout = null;
    /**
     * 一些常量
     */
    //白棋
    public static final int WHITE_CHESS = 1;
    //黑棋
    public static final int BLACK_CHESS = 2;
    //无棋
    public static final int NO_CHESS = 0;
    //白棋赢
    public static final int WHITE_WIN = 101;
    //黑棋赢
    public static final int BLACK_WIN = 102;
    //平局
    public static final int NO_WIN = 103;

    public LJFiveChessView(Context context) {
        this(context, null);
    }

    public LJFiveChessView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LJFiveChessView(Context context, AttributeSet attrs, int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化Paint
        paint = new Paint();
        canvas=new Canvas();
        //设置抗锯齿
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setColor(Color.GRAY);
        //初始化chessArray
        chessArray = new int[GRID_NUMBER][GRID_NUMBER];
        //初始化棋子图片bitmap
        whiteChess = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.white_chess);
        blackChess = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.black_chess);
        //初始化胜利局数
        whiteChessCount = 0;
        blackChessCount = 0;
        //初始化Rect
        rect = new Rect();
        //设置点击监听
        setOnTouchListener(this);
        //重置棋盘状态
        for (int i = 0; i < GRID_NUMBER; i++) {
            for (int j = 0; j < GRID_NUMBER; j++) {
                chessArray[i][j] = 0;
            }
        }
        // 创建一个线程监听对方走棋的消息
        new Thread(new Runnable() {
            public void run(){
                while (true) {	// 循环监听对方发送的走棋消息
                    while (isStart && !isYourBout) {
                        try {
                            // 读取对方落子的行列以及是黑子还是白子
                            int row = din.readInt();
                            int col = din.readInt();
                            isBlack = din.readBoolean();
                            addPiece(row, col, isBlack);	// 向棋盘添加对方走的棋子
                            postInvalidate();
                            checkGameOver();
                            isYourBout = !isYourBout;	// 收到对方棋子后交换走棋方
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 重新测量宽高，确保宽高一样
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取高宽值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        //获取宽高中较小的值
        int len = width > height ? height : width;
        //重新设置宽高
        setMeasuredDimension(len, len);
    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawColor(Color.parseColor("#F4A460"));
        //棋盘为一个GRID_NUMBER*GRID_NUMBER的正方形，所有棋盘宽高必须一样
        len = getWidth() > getHeight() ? getHeight() : getWidth();
        preWidth = len / GRID_NUMBER;
        //边距
        offset = preWidth / 2;
        //棋盘线条
        for (int i = 0; i < GRID_NUMBER; i++) {
            float start = i * preWidth + offset;
            //横线
            canvas.drawLine(offset, start, len - offset, start, paint);
            //竖线
            canvas.drawLine(start, offset, start, len - offset, paint);
        }
        for (int i = 0; i < GRID_NUMBER; i++) {
            float start = i * preWidth + offset;
            if(i==(GRID_NUMBER-1)/2){
                paint.setColor(Color.BLACK);
                canvas.drawLine(offset, start, len - offset, start, paint);
                canvas.drawLine(start, offset, start, len - offset, paint);
                paint.setColor(Color.GRAY);
            }
        }
        //绘制棋子
        for (int i = 0; i < GRID_NUMBER; i++) {
            for (int j = 0; j < GRID_NUMBER; j++) {
                //rect中点坐标
                float rectX = offset + i * preWidth;
                float rectY = offset + j * preWidth;
                //设置rect位置
                rect.set((int) (rectX - offset), (int) (rectY - offset),
                        (int) (rectX + offset), (int) (rectY + offset));
                //遍历chessArray
                switch (chessArray[i][j]) {
                    case WHITE_CHESS:
                        //绘制白棋
                        canvas.drawBitmap(whiteChess, null, rect, paint);
                        break;
                    case BLACK_CHESS:
                        //绘制黑棋
                        canvas.drawBitmap(blackChess, null, rect, paint);
                        break;
                }
            }
        }

    }
    public void addPiece(int p,int q,boolean isBlack) {
        if (isBlack == true) {
            chessArray[p][q]=BLACK_CHESS;
            float rectP = offset + p * preWidth;
            float rectQ= offset + q * preWidth;
            //设置rect位置
            rect.set((int) (rectP - offset), (int) (rectQ- offset),
                    (int) (rectP + offset), (int) (rectQ + offset));
            canvas.drawBitmap(blackChess, null, rect, paint);
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isGameOver && isYourBout) {
                    //获取按下时的位置
                    float downX = event.getX();
                    float downY = event.getY();
                    //点击的位置在棋盘上
                    if (downX >= offset / 2 && downX <= len - offset / 2
                            && downY >= offset / 2 && downY <= len - offset /
                            2) {
                        //获取棋子对应的位置
                        int x = (int) (downX / preWidth);
                        int y = (int) (downY / preWidth);
                        //判断当前位置是否已经有子
                        if (chessArray[x][y] != WHITE_CHESS &&
                                chessArray[x][y] != BLACK_CHESS) {
                            //给数组赋值
                            chessArray[x][y] = oneChess;
                            //修改当前落子颜色
                            isWhite = oneChess != WHITE_CHESS;
                            isYourBout = false;
                            try {	// 向对方棋盘发送刚才落子的位置(行、列)以及黑子还是白子
                                dout.writeInt(x);
                                dout.writeInt(y);
                                dout.writeBoolean(isBlack);
                            }
                            catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            //更新棋盘
                            postInvalidate();
                            //判断是否结束
                            checkGameOver();
                            //回调当前执子
                            if (callBack != null) {
                                callBack.ChangeGamer(isWhite);
                            }
                        }
                    }
                } else if (isGameOver) {
                    Toast.makeText(getContext(), "游戏已经结束，请重新开始！",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return false;
    }
    public void actionperformed(String name){
        String command=name;
        if (command.equals("创建游戏")) {

            // 创建一个线程启动服务器套接字
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        ServerSocket server = new ServerSocket(5566);//****************
                        Socket client = server.accept();//************
                        din = new DataInputStream(client.getInputStream());//************;
                        dout = new DataOutputStream(client.getOutputStream());
                        isStart = true;
                        isBlack = true;
                        isYourBout= true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            LJGame.join.setEnabled(false);
        } else if (command.equals("加入游戏")) {
            String ip = LJGame.edit.getText().toString();
            if(ip != null && !ip.equals("")) {
                try {
                    Socket client = new Socket(ip, 5566);
                    din = new DataInputStream(client.getInputStream());
                    dout = new DataOutputStream(client.getOutputStream());
                    isStart = true;
                    isYourBout = false;
                    isBlack = false;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                postInvalidate();
                LJGame.create.setEnabled(false);
            }
        }
    }
    /**
     * 判断是否结束
     */
    private void checkGameOver() {
        //获取落子的颜色(如果当前是白棋，则落子是黑棋)
        int chess = isWhite ? BLACK_CHESS : WHITE_CHESS;
        //棋盘是否填满
        boolean isFull = true;
        //遍历chessArray
        for (int i = 0; i < GRID_NUMBER; i++) {
            for (int j = 0; j < GRID_NUMBER; j++) {
                //判断棋盘是否填满
                if (chessArray[i][j] != BLACK_CHESS && chessArray[i][j] !=
                        WHITE_CHESS) {
                    isFull = false;
                }
                //只需要判断落子是否五连即可
                if (chessArray[i][j] == chess) {
                    //判断五子相连
                    if (isFiveSame(i, j)) {
                        //五子相连游戏结束
                        isGameOver = true;
                        if (callBack != null) {
                            //判断黑白棋胜利
                            if (chess == WHITE_CHESS) {
                                whiteChessCount++;
                            } else {
                                blackChessCount++;
                            }
                            if (oneChess == chess) {
                               oneScore++;
                            } else {
                               otherScore++;
                            }
                            callBack.GameOver(chess == WHITE_CHESS ?
                                    WHITE_WIN : BLACK_WIN);
                        }
                        return;
                    }
                }
            }
        }
        //如果棋盘填满，平局结束
        if (isFull) {
            isGameOver = true;
            if (callBack != null) {
                callBack.GameOver(NO_WIN);
            }
        }
    }

    /**
     * 重置游戏
     */
    public void resetGame() {
        isGameOver = false;
        //重置棋盘状态
        for (int i = 0; i < GRID_NUMBER; i++) {
            for (int j = 0; j < GRID_NUMBER; j++) {
                chessArray[i][j] = 0;
            }
        }
        //更新UI
        postInvalidate();
    }

    /**
     * 判断是否存在五子相连
     *
     * @return
     */
    private boolean isFiveSame(int x, int y) {
        //判断横向
        if (x + 4 < GRID_NUMBER) {
            if (chessArray[x][y] == chessArray[x + 1][y] && chessArray[x][y]
                    == chessArray[x + 2][y]
                    && chessArray[x][y] == chessArray[x + 3][y] &&
                    chessArray[x][y] == chessArray[x + 4][y]) {
                return true;
            }
        }
        //判断纵向
        if (y + 4 < GRID_NUMBER) {
            if (chessArray[x][y] == chessArray[x][y + 1] && chessArray[x][y]
                    == chessArray[x][y + 2]
                    && chessArray[x][y] == chessArray[x][y + 3] &&
                    chessArray[x][y] == chessArray[x][y + 4]) {
                return true;
            }
        }
        //判断斜向(左上到右下)
        if (y + 4 < GRID_NUMBER && x + 4 < GRID_NUMBER) {
            if (chessArray[x][y] == chessArray[x + 1][y + 1] &&
                    chessArray[x][y] == chessArray[x + 2][y + 2]
                    && chessArray[x][y] == chessArray[x + 3][y + 3] &&
                    chessArray[x][y] == chessArray[x + 4][y + 4]) {
                return true;
            }
        }
        //判断斜向(左下到右上)
        if (y - 4 > 0 && x + 4 < GRID_NUMBER) {
            if (chessArray[x][y] == chessArray[x + 1][y - 1] &&
                    chessArray[x][y] == chessArray[x + 2][y - 2]
                    && chessArray[x][y] == chessArray[x + 3][y - 3] &&
                    chessArray[x][y] == chessArray[x + 4][y - 4]) {
                return true;
            }
        }
        return false;
    }

    public void setCallBack(GameCallBack callBack) {
        this.callBack = callBack;
    }

    public int getWhiteChessCount() {
        return whiteChessCount;
    }

    public int getBlackChessCount() {
        return blackChessCount;
    }

    public int[][] getChessArray() {
        return chessArray;
    }

    public void setOneBout(boolean oneBout) {
        isYourBout =oneBout;
    }

    public void setOneChess(int userChess) {
        this.oneChess = oneChess;
    }

    public int getOneScore() {
        return oneScore;
    }

    public int getOtherScore() {
        return otherScore;
    }
}
