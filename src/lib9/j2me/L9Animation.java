package lib9.j2me;

import javax.microedition.lcdui.Graphics;

/**
 * ������ʾ������Ϣ������������Դ��L9Sprite����,Lib9Editor�༭������������
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
     * ���ض���֡ID�������Ƕ���֡����
     * @param aframe int
     * @return int
     */
    public int getAFrameID(int aframe) {
        return _sprite.getAFrameID(curAnim, aframe);
    }

    /**
     * ���ض���֡�ڶ����е�X���꣬�����Ƕ���֡����
     * @param aframe int
     * @return int
     */
    public int getAFrameX(int aframe) {
        return _sprite.getAFrameX(curAnim, aframe);
    }

    /**
     * ���ض���֡�ڶ����е�Y���꣬�����Ƕ���֡����
     * @param aframe int
     * @return int
     */
    public int getAFrameY(int aframe) {
        return _sprite.getAFrameY(curAnim, aframe);
    }

    /**
     * ���ض���֡�Ĳ��Ŵ����������Ƕ���֡����
     * @param aframe int
     * @return int
     */
    public int getAFrameTime(int aframe) {
        return _sprite.getAFrameTime(curAnim, aframe);
    }

    /**
     * ��ȡ�����Ŀ�,�ö�������frame�Ĳ�����
     * @return int
     */
    public int getAnimWidth() {
        return _sprite.getAnimWidth(curAnim);
    }

    /**
     * ��ȡ�����ĸ�,�ö�������frame�Ĳ�����
     * @return int
     */
    public int getAnimHeight() {
        return _sprite.getAnimHeight(curAnim);
    }

    /**
     * ����ÿ�붯�����ŵ�֡������fpsֵ
     * @return int
     */
    public int getAnimFPS() {
        return _sprite.getAnimFPS(curAnim);
    }

    /**
     * ���ض�����֡��
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
     * ��ȡ��ǰ����֡����ʾ�����ĵ�ǰ֡����
     * @return int
     */
    public int getACurFrame() {
        return _curFrame;
    }

    /**
     * ���ص�ǰ֡�ĵ�ǰ���Ŵ���������֡�п����ظ�����
     * @return int
     */
    public int getACurTime() {
        return _curTime;
    }

    /**
     * ���ض�����X����
     * @return int
     */
    public int getAnimX() {
        return _anim_PosX;
    }

    /**
     * ���ض�����Y����
     * @return int
     */
    public int getAnimY() {
        return _anim_PosY;
    }

    /**
     * ���ض����Ƿ���ѭ������
     * @return boolean
     */
    public boolean isLoopPlay() {
        return _bLoopPlay;
    }

    /**
     * ���ö������ŵ���Ϣ,�����Ǹ���������������ʾ���꣬�Ƿ�ѭ������
     * @param curAnim int ��һ������,һ��sprite���ܻ��ж������
     * @param anim_PosX int �������ŵ�xλ��
     * @param anim_PosY int �������ŵ�yλ��
     * @param bLoopPlay boolean �Ƿ�ѭ������
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
     * �ж϶����Ƿ����,���Ϊѭ�������򲻿��ܽ���
     * @return boolean
     */
    public boolean isPlayOver() {
        if (_bLoopPlay) {
            return false;
        }
        return _bPlayOver;
    }

    /**
     * ֹͣ�����Ĳ���
     */
    public void stopPlay() {
        _bLoopPlay = false;
        _bPlayOver = true;
    }

    /**
     * ���Ŷ����������������ڲ���graphics��
     * @param g Graphics
     */
    private int[] AnimationSkipFrameIndex;
    /**
     * ���ò��Ŷ���ʱ������֡������skipFrameIndex�Ƕ����е�֡����
     * @param anim int
     * @param skipFrameIndex int[]
     */
    public void setSkipFrame(int[] skipFrameIndex) {
        AnimationSkipFrameIndex = skipFrameIndex;
    }

    public void updateAnimation(Graphics g) {
        if (isPlayOver()) { //�����Ѿ����Ž���
            return;
        }
        _curTime++;

        int frame_index = getAFrameID(_curFrame);
        int time = getAFrameTime(_curFrame);

        if ( -1 != L9Util.getIndexByID(AnimationSkipFrameIndex, _curFrame)) { //���Ŷ���ʱ������֡,�����Ҫ��ԭ���� ��֡��ȥ������ͼ��,ע�������Ƕ�����֡�� ����������֡ID
            _curFrame++;
            _curTime = 0;
            return;
        }
        //��������Ϊ����
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
