package com.android.wangkang.simplechess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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
    private Bitmap mSelectedWhitePieces;
    private Bitmap mSelectedBlackPieces;

    //棋子大小，行高3/8
    private float ratioPiecesOfLineHeight=3*1.0f/8;
    private float ratioSelectedPiecesOfLineheight= (float) (1.5*ratioPiecesOfLineHeight);
    //白棋先手，当前轮到白棋
    private boolean mIsWhite=true;

    private PointF mPointF=new PointF(0,0);

    public static final int NO_PIECES=0;
    public static final int WHITE_PIECES=1;
    public static final int BLACK_PIECES=2;
    //是否是选中状态
    public static final int SELECT_WHITE_PIECES=3;
    public static final int SELECT_BLACK_PIECES=4;

    //双方共走的步数
    private int mCounts=0;
    //哪个先走
    private int mFirst=0;
    //是够轮到该棋子走
    private boolean isMoveRight=true;

    private int[][] positionCondition={
            {WHITE_PIECES,WHITE_PIECES,WHITE_PIECES},
            {NO_PIECES,NO_PIECES,NO_PIECES},
            {BLACK_PIECES,BLACK_PIECES,BLACK_PIECES}
    };

    private int downi=0;
    private int downj=0;
    //是否在移动中
    private boolean isMoving=false;
    //移动的是那个棋子
    private int movingPieces=0;

    public SimpleChessPanel(Context context, AttributeSet attrs) {
        super(context, attrs);

        //setBackgroundColor(0x44ff0000);
        //初始化画笔
        init();
    }

    private void init() {
        //画笔的一些初始化
        mPaint.setColor(0x88ffffff);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        //初始化棋子
        mWhitePieces= BitmapFactory.decodeResource(getResources(),R.drawable.white_chess);
        mBlackPieces= BitmapFactory.decodeResource(getResources(),R.drawable.black_chess);
        mSelectedWhitePieces=mWhitePieces;
        mSelectedBlackPieces=mBlackPieces;
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
        setMeasuredDimension(width,width);//是个正方形的棋盘
    }

    //二，获取View的相关尺寸
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth=w;
        mLineHeight=2*mPanelWidth*1.0f/(2*Max_LINE+1);

        //尺寸
        int pieceWidth= (int) (mLineHeight*ratioPiecesOfLineHeight);
        int selectedPieceWidth= (int) (mLineHeight*ratioSelectedPiecesOfLineheight);
        mWhitePieces=Bitmap.createScaledBitmap(mWhitePieces,pieceWidth,pieceWidth,false);
        mBlackPieces=Bitmap.createScaledBitmap(mBlackPieces,pieceWidth,pieceWidth,false);
        //选中放大1.5倍
        mSelectedWhitePieces=Bitmap.createScaledBitmap(mSelectedWhitePieces,selectedPieceWidth,selectedPieceWidth,false);
        mSelectedBlackPieces=Bitmap.createScaledBitmap(mSelectedBlackPieces,selectedPieceWidth,selectedPieceWidth,false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=event.getAction();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                int downX=(int)event.getX();
                int downY= (int) event.getY();
                downi= (int) ((downX+mLineHeight/4)/mLineHeight);
                downj= (int) ((downY+mLineHeight/4)/mLineHeight);
                movingPieces=positionCondition[downj][downi];

                if (positionCondition[downj][downi]>0){
                    //如果有棋子则放大显示
                    positionCondition[downj][downi]=positionCondition[downj][downi]+2;
                }
                invalidate();
                //Log.d("Touch","DOWN");
                return true;

            case MotionEvent.ACTION_MOVE:
                positionCondition[downj][downi]=NO_PIECES;
                mPointF.x=event.getX();
                mPointF.y=event.getY();
                //正在移动
                if (mFirst==0
                        ||(mFirst==movingPieces&&mCounts%2==0)     //是否轮到的条件，如果没轮到则不移动
                        ||(mFirst!=movingPieces&&mCounts%2==1)){
                    isMoving=true;
                }else {
                    //如果不轮到走的话之前的要放大显示
                    positionCondition[downj][downi] =movingPieces+ 2;
                }

                invalidate();
                //Log.d("Touch","MOVE");
                return true;
            case MotionEvent.ACTION_UP:

                int upX=(int)event.getX();
                int upY= (int) event.getY();
                int upi= (int) ((upX+mLineHeight/4)/mLineHeight);
                int upj= (int) ((upY+mLineHeight/4)/mLineHeight);
                boolean judge=(upi+upj)%2!=0&&(downi+downj)%2!=0;  //不能走在规定外的斜线
                if (positionCondition[upj][upi]==NO_PIECES  //落点必须没有棋子
                        &&Math.abs(upi-downi)<=1   //不能走两格以上
                        &&Math.abs(upj-downj)<=1
                        &&!judge  //不能走规定外的斜线
                        &&Math.abs(upi-downi)+Math.abs(upj-downj)!=0 //不能原地不动
                        &&isMoving  //确定移动了
                        ){
                    positionCondition[upj][upi]=movingPieces;
                    mCounts=mCounts+1;
                    //判断是哪个棋子先走
                    if (mCounts==1){
                        mFirst=movingPieces;
                    }
                }else {
                    //棋子回到原位
                    positionCondition[downj][downi]=movingPieces;
                }
                isMoving=false;
                invalidate();
                //检查是否游戏结束
                checkIsGameOver();
                //Log.d("Touch","UP");
                break;
        }
        return super.onTouchEvent(event);
    }

    private void checkIsGameOver() {
        boolean a=positionCondition[1][1]==positionCondition[0][0]
                &&positionCondition[1][1]==positionCondition[2][2];
        boolean b=positionCondition[1][1]==positionCondition[0][1]
                &&positionCondition[1][1]==positionCondition[2][1];
        boolean c=positionCondition[1][1]==positionCondition[0][2]
                &&positionCondition[1][1]==positionCondition[2][0];
        boolean d=positionCondition[1][1]==positionCondition[1][0]
                &&positionCondition[1][1]==positionCondition[1][2];
        if (a||b||c||d){
            if (positionCondition[1][1]==WHITE_PIECES){
                Toast.makeText(getContext(),R.string.white_goal,Toast.LENGTH_LONG).show();
            }else if (positionCondition[1][1]==BLACK_PIECES){
                Toast.makeText(getContext(),R.string.black_goal,Toast.LENGTH_LONG).show();
            }
        }
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
        if (isMoving){
            drawMovingPices(canvas);
        }
    }

    private void drawMovingPices(Canvas canvas) {
        float selectedHalfPiecesWidth= mLineHeight*ratioSelectedPiecesOfLineheight/2;
        if (movingPieces==WHITE_PIECES){
            canvas.drawBitmap(mSelectedWhitePieces,mPointF.x-selectedHalfPiecesWidth,mPointF.y-selectedHalfPiecesWidth,null);
        }else if (movingPieces==BLACK_PIECES){
            canvas.drawBitmap(mSelectedBlackPieces,mPointF.x-selectedHalfPiecesWidth,mPointF.y-selectedHalfPiecesWidth,null);
        }

    }

    private void drawPieces(Canvas canvas) {
        float lineHeight=mLineHeight;

        //开始绘制
        //通过判断每个点是否有棋子
        for (int i=0;i<=Max_LINE;i++) {
            for (int j=0;j<=Max_LINE;j++){
                int x=(int)(lineHeight/4);
                int halfPiecesWidth= (int) (lineHeight*ratioPiecesOfLineHeight/2);
                int selectedHalfPiecesWidth= (int) (lineHeight*ratioSelectedPiecesOfLineheight/2);
                if (positionCondition[i][j]==WHITE_PIECES){
                    canvas.drawBitmap(mWhitePieces,x+j*lineHeight-halfPiecesWidth,x+i*lineHeight-halfPiecesWidth,null);
                }else if (positionCondition[i][j]==BLACK_PIECES){
                    canvas.drawBitmap(mBlackPieces,x+j*lineHeight-halfPiecesWidth,x+i*lineHeight-halfPiecesWidth,null);
                }else if (positionCondition[i][j]==SELECT_WHITE_PIECES){
                    canvas.drawBitmap(mSelectedWhitePieces,x+j*lineHeight-selectedHalfPiecesWidth,x+i*lineHeight-selectedHalfPiecesWidth,null);
                }else if (positionCondition[i][j]==SELECT_BLACK_PIECES){
                    canvas.drawBitmap(mSelectedBlackPieces,x+j*lineHeight-selectedHalfPiecesWidth,x+i*lineHeight-selectedHalfPiecesWidth,null);
                }
            }
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
    //重新开始游戏
    public void restartGame(){
        for (int i=0;i<=Max_LINE;i++){
            positionCondition[0][i]=WHITE_PIECES;
        }
        for (int i=0;i<=Max_LINE;i++){
            positionCondition[1][i]=NO_PIECES;
        }
        for (int i=0;i<=Max_LINE;i++){
            positionCondition[2][i]=BLACK_PIECES;
        }
        mFirst=0;
        mCounts=0;
        invalidate();
    }
}
