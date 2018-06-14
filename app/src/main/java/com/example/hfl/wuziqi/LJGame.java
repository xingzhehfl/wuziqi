package com.example.hfl.wuziqi;

import android.media.MediaPlayer;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

public class LJGame extends AppCompatActivity implements GameCallBack,View.OnClickListener{

    //五子棋UI
    private LJFiveChessView ljfiveChessView;
    private TextView oneWinTv,otherWinTv;
    //PopUpWindow选择玩家执子
    private PopupWindow createWindow;
    public static Button create;
    public static Button join;
    public static EditText edit;
    private MediaPlayer mMediaPlayer2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rjgame_activity);
        //初始化控件
        ljfiveChessView = (LJFiveChessView) findViewById(R.id.five_chess_view2);
        ljfiveChessView.setCallBack(this);
        oneWinTv = (TextView) findViewById(R.id.white_count_tv);
        otherWinTv = (TextView) findViewById(R.id.black_count_tv);
        create.setOnClickListener(this);
        join.setOnClickListener(this);
        /* 重开游戏设置点击事件 */
        findViewById(R.id.restart_game).setOnClickListener(this);
        Game.backgroundmusic= (RadioGroup)findViewById(R.id.backgroundmusic);
        mMediaPlayer2= MediaPlayer.create(LJGame.this, R.raw.music);
        mMediaPlayer2.setLooping(true);      //设置循环播放模式
        mMediaPlayer2.start();
        Game.backgroundmusic.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch(checkedId){
                    case R.id.closebackgroundmusic:       //关闭背景音乐
                        if(mMediaPlayer2.isPlaying())
                            mMediaPlayer2.stop();          //停止状态
                        break;
                    case R.id.openbackgroundmusic:        //播放背景音乐
                        if(mMediaPlayer2.isPlaying())
                        {
                            try{
                                mMediaPlayer2.prepare();
                                mMediaPlayer2.seekTo(0);    //回到起始播放位置
                                mMediaPlayer2.start();
                            }
                            catch(IOException e){
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }

        });
        //view加载完成
        ljfiveChessView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //初始化PopupWindow
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                initPop(wm.getDefaultDisplay().getWidth(), wm.getDefaultDisplay().getHeight());
            }
        });
    }


    //初始化PopupWindow
    private void initPop(int width, int height) {
        if (createWindow == null) {
            View view = View.inflate(this, R.layout.connect, null);
            create=(Button)findViewById(R.id.create);
            join=(Button)findViewById(R.id.join);
            edit=(EditText)findViewById(R.id.editText);
            create.setOnClickListener(this);
            join.setOnClickListener(this);
            createWindow = new PopupWindow(view, width, height);
            createWindow.setOutsideTouchable(false);
            createWindow.showAtLocation(ljfiveChessView, Gravity.CENTER, 0, 0);
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

    @Override
    public void ChangeGamer(boolean isWhite) {

    }

    //更新游戏胜利局数
    private void updateWinInfo(){
       oneWinTv.setText(ljfiveChessView.getWhiteChessCount()+" ");
       otherWinTv.setText(ljfiveChessView.getBlackChessCount()+" ");
    }

    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.restart_game:
                //显示PopupWindow
                createWindow.showAtLocation(ljfiveChessView, Gravity.CENTER, 0, 0);
                //重新开始游戏
               ljfiveChessView.resetGame();
                break;
            case R.id.create:
                ljfiveChessView.actionperformed(create.getText().toString());
                createWindow.dismiss();
                break;
            case R.id.join:
                ljfiveChessView.actionperformed(join.getText().toString());
                createWindow.dismiss();
                break;
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
