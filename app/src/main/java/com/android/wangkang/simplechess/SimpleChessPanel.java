package com.android.wangkang.simplechess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by WangKang on 2016/5/17.
 */
public class SimpleChessPanel extends View {
    //棋盘宽度
    private int mPanelWidth;
    //一个格子的高度
    private float mLineHeight;
    private int Max_LINE=2;
    //相当于画笔
    private Paint mPaint=new Paint();
    //双方棋子图片
    private Bitmap mWhitePieces;
    private Bitmap mBlackPieces;

    //棋子大小，行高3/8
    private float ratioPiecesOfLineHeight=3*1.0f/8;
    //白棋先手，当前轮到白棋
    private boolean mIsWhite=true;


    public SimpleChessPanel(Context context, AttributeSet attrs) {
        super(context, attrs);

        setBackgroundColor(0x44ff0000);
        //初始化画笔
        init();
    }

    private void init() {
        //画笔的一些初始化
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        //初始化棋子
        mWhitePieces= BitmapFactory.decodeResource(getResources(),R.drawable.white_pieces);
        mBlackPieces= BitmapFactory.decodeResource(getResources(),R.drawable.black_pieces);
    }

    //一，测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //可以得到用户所设置的宽和高，与layout_width和layout_height相对应
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);

        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);

        int width=Math.min(widthSize,heightSize);
        //防止在如ScrollView中某一项为0或者为不确定值
        if (widthMode==MeasureSpec.UNSPECIFIED){
            width=heightSize;
        }else if (heightMode==MeasureSpec.UNSPECIFIED){
            width=widthSize;
        }
        setMeasuredDimension(width,width);
    }

    //二，获取View的相关尺寸
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth=w;
        mLineHeight=2*mPanelWidth*1.0f/(2*Max_LINE+1);

        //尺寸
        int pieceWidth= (int) (mLineHeight*ratioPiecesOfLineHeight);
        mWhitePieces=Bitmap.createScaledBitmap(mWhitePieces,pieceWidth,pieceWidth,false);
        mBlackPieces=Bitmap.createScaledBitmap(mBlackPieces,pieceWidth,pieceWidth,false);
    }

    /*三，开始绘制
        1、设置画笔的属性
        2、绘制棋盘
        3、绘制棋子，先初始化,再设置棋子大小
     */

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制棋盘
        drawBoard(canvas);
        //绘制棋子
        drawPieces(canvas);
    }

    private void drawPieces(Canvas canvas) {
        float lineHeight=mLineHeight;

        //开始绘制
        for (int i=0;i<=Max_LINE;i++){
            int x=(int)(lineHeight/4);
            int halfPiecesWidth= (int) (lineHeight*ratioPiecesOfLineHeight/2);
            canvas.drawBitmap(mWhitePieces,x+i*lineHeight-halfPiecesWidth,x-halfPiecesWidth,null);
            canvas.drawBitmap(mBlackPieces,x+i*lineHeight-halfPiecesWidth,x+Max_LINE*lineHeight-halfPiecesWidth,null);
        }

    }

    private void drawBoard(Canvas canvas) {
        int w =mPanelWidth;
        float lineHeight=mLineHeight;
        //绘制纵横线
        for (int i=0;i<=Max_LINE;i++){
            int startX=(int)(lineHeight/4);
            int endX=(int)(w-lineHeight/4);

            int y= (int) (lineHeight/4+i*lineHeight);
            canvas.drawLine(startX,y,endX,y,mPaint);
            canvas.drawLine(y,startX,y,endX,mPaint);
        }
        //绘制斜线
        int a=(int)(lineHeight/4);
        int b= (int) (w-lineHeight/4);
        canvas.drawLine(a,a,b,b,mPaint);
        canvas.drawLine(a,b,b,a,mPaint);
    }
}
