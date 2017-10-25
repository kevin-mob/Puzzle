package cn.kevin.puzzle;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PuzzleActivity extends AppCompatActivity implements Runnable, View.OnTouchListener {
    PuzzleLayout puzzleLayout;
    TextView tvTips;
    ImageView ivTips;
    int squareRootNum = 2;
    int drawableId = R.mipmap.pic_02;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        ivTips = (ImageView) findViewById(R.id.iv_tips);
        ivTips.setImageResource(drawableId);
        tvTips = (TextView) findViewById(R.id.tv_tips);
        tvTips.setOnTouchListener(this);
        puzzleLayout = (PuzzleLayout) findViewById(R.id.activity_swipe_card);
        puzzleLayout.setImage(drawableId, squareRootNum);
        puzzleLayout.setOnCompleteCallback(new PuzzleLayout.OnCompleteCallback() {
            @Override
            public void onComplete() {
                Toast.makeText(PuzzleActivity.this, R.string.next, Toast.LENGTH_LONG).show();
                puzzleLayout.postDelayed(PuzzleActivity.this, 800);
            }
        });
    }

    @Override
    public void run() {
        squareRootNum++;
        drawableId++;
        if(squareRootNum > 10){
            Toast.makeText(PuzzleActivity.this, R.string.complete, Toast.LENGTH_SHORT).show();
            showDialog();
        }else {
            ivTips.setImageResource(drawableId);
            puzzleLayout.setImage(drawableId, squareRootNum);
        }
    }

    private void showDialog() {
        new AlertDialog.Builder(PuzzleActivity.this)
                .setTitle(R.string.success)
                .setMessage(R.string.restart)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                squareRootNum = 2;
                                drawableId = R.mipmap.pic_02;
                                ivTips.setImageResource(drawableId);
                                puzzleLayout.setImage(drawableId, squareRootNum);
                            }
                        }).setNegativeButton(R.string.exit,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                ivTips.setVisibility(View.VISIBLE);
                break;
            default:
                ivTips.setVisibility(View.GONE);
        }
        return true;
    }
}
