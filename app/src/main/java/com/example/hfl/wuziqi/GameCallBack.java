package com.example.hfl.wuziqi;

/**
 * Created by hfl on 2018/4/17.
 */

public interface GameCallBack {
    //游戏结束回调
    void GameOver(int winner);
    //游戏更换执子回调
    void ChangeGamer(boolean isWhite);
}