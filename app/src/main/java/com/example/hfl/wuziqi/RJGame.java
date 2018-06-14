package com.example.hfl.wuziqi;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class RJGame extends AppCompatActivity implements GameCallBack, AICallBack, View.OnClickListener {

    //五子棋UI
    private AIFiveChessView aifiveChessView;
    //显示用户以及ai得分
    private TextView userScoreTv, aiScoreTv;
    //显示玩家/ai执子
    private ImageView userChessIv, aiChessIv;
    //玩家/ai回合标识
    private ImageView userTimeIv, aiTimeIv;
    //游戏ai
    private AI ai;
    //PopUpWindow选择玩家执子
    private PopupWindow chooseChess;

    private MediaPlayer mMediaPlayer1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rjgame_activity);
        //初始化控件
        initViews();
        //初始化ai
        ai = new AI(aifiveChessView.getChessArray(), this);
        //view加载完成
        aifiveChessView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //初始化PopupWindow
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                initPop(wm.getDefaultDisplay().getWidth(), wm.getDefaultDisplay().getHeight());
            }
        });
    }

    private void initViews() {
        //五子棋UI
        aifiveChessView = (AIFiveChessView) findViewById(R.id.five_chess_view1);
        aifiveChessView.setCallBack(this);
        //显示用户以及ai得分
        userScoreTv = (TextView) findViewById(R.id.user_score_tv);
        aiScoreTv = (TextView) findViewById(R.id.ai_score_tv);
        //显示玩家/ai执子
        userChessIv = (ImageView) findViewById(R.id.user_chess_iv);
        aiChessIv = (ImageView) findViewById(R.id.ai_chess_iv);
        //玩家/ai回合标识
        userTimeIv = (ImageView) findViewById(R.id.user_think_iv);
        aiTimeIv = (ImageView) findViewById(R.id.ai_think_iv);
        /* 重开游戏设置点击事件 */
        findViewById(R.id.restart_game).setOnClickListener(this);
        Game.backgroundmusic= (RadioGroup)findViewById(R.id.backgroundmusic);
        mMediaPlayer1= MediaPlayer.create(RJGame.this, R.raw.music);
        mMediaPlayer1.setLooping(true);      //设置循环播放模式
        mMediaPlayer1.start();
        Game.backgroundmusic.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch(checkedId){
                    case R.id.closebackgroundmusic:       //关闭背景音乐
                        if(mMediaPlayer1.isPlaying())
                           mMediaPlayer1.stop();          //停止状态
                        break;
                    case R.id.openbackgroundmusic:        //播放背景音乐
                        if(mMediaPlayer1.isPlaying())
                        {
                            try{
                                 mMediaPlayer1.prepare();
                                 mMediaPlayer1.seekTo(0);    //回到起始播放位置
                                 mMediaPlayer1.start();
                            }
                            catch(IOException e){
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }

        });
    }


    //初始化PopupWindow
    private void initPop(int width, int height) {
        if (chooseChess == null) {
            View view = View.inflate(this, R.layout.pop_choose_chess, null);
            ImageButton white = (ImageButton) view.findViewById(R.id.choose_white);
            ImageButton black = (ImageButton) view.findViewById(R.id.choose_black);
            white.setOnClickListener(this);
            black.setOnClickListener(this);
            chooseChess = new PopupWindow(view, width, height);
            chooseChess.setOutsideTouchable(false);
            chooseChess.showAtLocation(aifiveChessView, Gravity.CENTER, 0, 0);
        }
    }

    @Override
    public void GameOver(int winner) {
        //更新游戏胜利局数
        updateWinInfo();
        switch (winner) {
            case AIFiveChessView.BLACK_WIN:
                showToast("黑棋胜利！");
                break;
            case AIFiveChessView.NO_WIN:
                showToast("平局！");
                break;
            case AIFiveChessView.WHITE_WIN:
                showToast("白棋胜利！");
                break;
        }
    }

    //更新游戏胜利局数
    private void updateWinInfo() {
        userScoreTv.setText(aifiveChessView.getUserScore() + " ");
        aiScoreTv.setText(aifiveChessView.getAiScore() + " ");
    }

    @Override
    public void ChangeGamer(boolean isWhite) {
        //ai回合
        ai.aiBout();
        //更改当前落子
        aiTimeIv.setVisibility(View.VISIBLE);
        userTimeIv.setVisibility(View.GONE);
    }

    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void aiAtTheBell() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //更新UI
                aifiveChessView.postInvalidate();
                //检查游戏是否结束
                aifiveChessView.checkAiGameOver();
                //设置为玩家回合
                aifiveChessView.setUserBout(true);
                //更改当前落子
                aiTimeIv.setVisibility(View.GONE);
                userTimeIv.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.restart_game:
                //显示PopupWindow
                chooseChess.showAtLocation(aifiveChessView, Gravity.CENTER, 0, 0);
                //重新开始游戏
                aifiveChessView.resetGame();
                break;
            case R.id.choose_black:
                changeUI(false);
                chooseChess.dismiss();
                break;
            case R.id.choose_white:
                changeUI(true);
                chooseChess.dismiss();
                break;
        }
    }

    //根据玩家选择执子，更新UI
    private void changeUI(boolean isUserWhite) {
        if (isUserWhite) {
            //玩家选择白棋
            aifiveChessView.setUserChess(AIFiveChessView.WHITE_CHESS);
            ai.setAiChess(AIFiveChessView.BLACK_CHESS);
            //玩家先手
            aifiveChessView.setUserBout(true);
            //更改当前落子
            userChessIv.setBackgroundResource(R.drawable.white_chess);
            aiChessIv.setBackgroundResource(R.drawable.black_chess);
            aiTimeIv.setVisibility(View.GONE);
            userTimeIv.setVisibility(View.VISIBLE);
        } else {
            //玩家选择黑棋
            aifiveChessView.setUserChess(AIFiveChessView.BLACK_CHESS);
            aifiveChessView.setUserBout(false);
            //ai先手
            ai.setAiChess(AIFiveChessView.WHITE_CHESS);
            ai.aiBout();
            //更改当前落子
            userChessIv.setBackgroundResource(R.drawable.black_chess);
            aiChessIv.setBackgroundResource(R.drawable.white_chess);
            aiTimeIv.setVisibility(View.VISIBLE);
            userTimeIv.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
                finish();break;
            case  R.id.setting:
                Toast.makeText(this,"",Toast.LENGTH_SHORT).show();break;
            default:
        }
        return true;
    }
}
