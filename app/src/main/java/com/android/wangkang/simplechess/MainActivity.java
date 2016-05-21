package com.android.wangkang.simplechess;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private SimpleChessPanel mSimpleChessPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSimpleChessPanel=(SimpleChessPanel)findViewById(R.id.chess_panel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_restart_game:
                mSimpleChessPanel.restartGame();
                return true;
            case R.id.menu_rules:
                new AlertDialog.Builder(this)
                        .setView(R.layout.dialog_rules)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
