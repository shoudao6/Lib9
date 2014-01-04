package lib9.j2me;

import javax.microedition.lcdui.Graphics;

/**
 * 用来显示动画信息，动画数据来源于L9Sprite对象,Lib9Editor编辑器导出的数据
 * @author not attributable
 * @version 1.0
 */
public class L9Animation {
    public L9Animation(L9Sprite sprite, int animIndex) {
        _sprite = sprite;
        curAnim = animIndex;
        setAnimPlayInfo(0, 0, false);
    }

    /**
     * 返回动画帧ID，参数是动画帧索引
     * @param aframe int
     * @return int
     */
    public int getAFrameID(int aframe) {
        return _sprite.getAFrameID(curAnim, aframe);
    }

    /**
     * 返回动画帧在动画中的X坐标，参数是动画帧索引
     * @param aframe int
     * @return int
     */
    public int getAFrameX(int aframe) {
        return _sprite.getAFrameX(curAnim, aframe);
    }

    /**
     * 返回动画帧在动画中的Y坐标，参数是动画帧索引
     * @param aframe int
     * @return int
     */
    public int getAFrameY(int aframe) {
        return _sprite.getAFrameY(curAnim, aframe);
    }

    /**
     * 返回动画帧的播放次数，参数是动画帧索引
     * @param aframe int
     * @return int
     */
    public int getAFrameTime(int aframe) {
        return _sprite.getAFrameTime(curAnim, aframe);
    }

    /**
     * 获取动画的宽,该动画所有frame的并集宽
     * @return int
     */
    public int getAnimWidth() {
        return _sprite.getAnimWidth(curAnim);
    }

    /**
     * 获取动画的高,该动画所有frame的并集高
     * @return int
     */
    public int getAnimHeight() {
        return _sprite.getAnimHeight(curAnim);
    }

    /**
     * 返回每秒动画播放的帧数，即fps值
     * @return int
     */
    public int getAnimFPS() {
        return _sprite.getAnimFPS(curAnim);
    }

    /**
     * 返回动画的帧数
     * @return int
     */
    public int getAFrames() {
        return _sprite.getAFrames(curAnim);
    }

    private L9Sprite _sprite;
    private int curAnim;

    private short _curTime;
    private short _curFrame;
    private short _anim_PosX;
    private short _anim_PosY;
    private boolean _bPlayOver;
    private boolean _bLoopPlay;
    /**
     * 获取当前动画帧，表示动画的当前帧索引
     * @return int
     */
    public int getACurFrame() {
        return _curFrame;
    }

    /**
     * 返回当前帧的当前播放次数，动画帧有可能重复播放
     * @return int
     */
    public int getACurTime() {
        return _curTime;
    }

    /**
     * 返回动画的X坐标
     * @return int
     */
    public int getAnimX() {
        return _anim_PosX;
    }

    /**
     * 返回动画的Y坐标
     * @return int
     */
    public int getAnimY() {
        return _anim_PosY;
    }

    /**
     * 返回动画是否是循环播放
     * @return boolean
     */
    public boolean isLoopPlay() {
        return _bLoopPlay;
    }

    /**
     * 设置动画播放的信息,播放那个动画，动画的显示坐标，是否循环播放
     * @param curAnim int 哪一个动画,一个sprite可能还有多个动画
     * @param anim_PosX int 动画播放的x位置
     * @param anim_PosY int 动画播放的y位置
     * @param bLoopPlay boolean 是否循环播放
     */
    public void setAnim(int anim_PosX, int anim_PosY, boolean bLoopPlay) {
        _anim_PosX = (short) anim_PosX;
        _anim_PosY = (short) anim_PosY;
        _bLoopPlay = bLoopPlay;
    }

    public void setAnimPlayInfo(int curFrame, int curTime, boolean bPlayOver) {
        _curFrame = (short) curFrame;
        _curTime = (short) curTime;
        _bPlayOver = bPlayOver;
    }

    /**
     * 判断动画是否结束,如果为循环播放则不可能结束
     * @return boolean
     */
    public boolean isPlayOver() {
        if (_bLoopPlay) {
            return false;
        }
        return _bPlayOver;
    }

    /**
     * 停止动画的播放
     */
    public void stopPlay() {
        _bLoopPlay = false;
        _bPlayOver = true;
    }

    /**
     * 播放动画，将动画绘制在参数graphics上
     * @param g Graphics
     */
    private int[] AnimationSkipFrameIndex;
    /**
     * 设置播放动画时跳过的帧，参数skipFrameIndex是动画中的帧索引
     * @param anim int
     * @param skipFrameIndex int[]
     */
    public void setSkipFrame(int[] skipFrameIndex) {
        AnimationSkipFrameIndex = skipFrameIndex;
    }

    public void updateAnimation(Graphics g) {
        if (isPlayOver()) { //动画已经播放结束
            return;
        }
        _curTime++;

        int frame_index = getAFrameID(_curFrame);
        int time = getAFrameTime(_curFrame);

        if ( -1 != L9Util.getIndexByID(AnimationSkipFrameIndex, _curFrame)) { //播放动画时跳过的帧,这个主要的原因是 该帧用去做缩略图了,注意这里是动画中帧的 索引，不是帧ID
            _curFrame++;
            _curTime = 0;
            return;
        }
        //允许坐标为负数
        int af_x = getAFrameX(_curFrame);
        int af_y = getAFrameY(_curFrame);
        _sprite.paintFrame(g, frame_index, af_x + _anim_PosX, af_y + _anim_PosY);

        if (_curTime >= time) {
            _curFrame++;
            _curTime = 0;
            if (_curFrame >= getAFrames()) {
                _curFrame = 0;
                _curTime = 0;
                _bPlayOver = true;
            }
        }
    }
}
