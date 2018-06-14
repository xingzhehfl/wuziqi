package com.example.hfl.wuziqi;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class SRGame extends AppCompatActivity implements GameCallBack {

    private FiveChessView fiveChessView;
    private TextView whiteWinTv,blackWinTv;
    private Button restart;
    private MediaPlayer mMediaPlayer3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.srgame_activity);
        fiveChessView = (FiveChessView) findViewById(R.id.five_chess_view3);
        fiveChessView.setCallBack(this);
        whiteWinTv = (TextView) findViewById(R.id.white_count_tv);
        blackWinTv = (TextView) findViewById(R.id.black_count_tv);
        restart=(Button)findViewById(R.id.restart_game);
        Game.backgroundmusic = (RadioGroup)findViewById(R.id.backgroundmusic);
        mMediaPlayer3= MediaPlayer.create(SRGame.this, R.raw.music);
        mMediaPlayer3.setLooping(true);      //设置循环播放模式      //设置循环播放模式
        mMediaPlayer3.start();
        Game.backgroundmusic.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch(checkedId){
                    case R.id.closebackgroundmusic:       //关闭背景音乐
                        if(mMediaPlayer3.isPlaying())
                            mMediaPlayer3.stop();          //停止状态
                        break;
                    case R.id.openbackgroundmusic:        //播放背景音乐
                        if(!mMediaPlayer3.isPlaying())
                        {
                            try{
                                    mMediaPlayer3.prepare();
                                     mMediaPlayer3.seekTo(0);    //回到起始播放位置
                                    mMediaPlayer3.start();
                            }
                            catch(IOException e){
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }

        });
        restart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                fiveChessView.resetGame();
            }
        });
    }

    @Override
    public void GameOver(int winner) {
        //更新游戏胜利局数
        updateWinInfo();
        switch (winner) {
            case FiveChessView.BLACK_WIN:
                showToast("黑棋胜利！");
                break;
            case FiveChessView.NO_WIN:
                showToast("平局！");
                break;
            case FiveChessView.WHITE_WIN:
                showToast("白棋胜利！");
                break;
        }
    }

    //更新游戏胜利局数
    private void updateWinInfo(){
        whiteWinTv.setText(fiveChessView.getWhiteChessCount()+" ");
        blackWinTv.setText(fiveChessView.getBlackChessCount()+" ");
    }

    @Override
    public void ChangeGamer(boolean isWhite) {

    }

    private void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
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