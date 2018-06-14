package com.example.hfl.wuziqi;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Game extends AppCompatActivity {
    public static RadioGroup backgroundmusic;
    @Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);
        Button rj_game=(Button)findViewById(R.id.rj_game);
        Button lj_game=(Button)findViewById(R.id.lj_game);
        Button sr_game=(Button)findViewById(R.id.sr_game);
        Button ab_game=(Button)findViewById(R.id.about_game);
        rj_game.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent rj_intent=new Intent(Game.this,RJGame.class);
                startActivity(rj_intent);
            }
        });
        lj_game.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent lj_intent=new Intent(Game.this,LJGame.class);
                startActivity(lj_intent);
            }
        });
        sr_game.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent sr_intent=new Intent(Game.this,SRGame.class);
                startActivity(sr_intent);
            }
        });
        ab_game.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent ab_intent=new Intent(Game.this,ABGame.class);
                startActivity(ab_intent);
            }
        });
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
