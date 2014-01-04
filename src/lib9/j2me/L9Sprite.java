package lib9.j2me;

import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.Sprite; // just for MIDP2 transformations
import javax.microedition.media.*;

/**
 * Lib9������һ���ǳ���Ҫ���࣬һ���Ϊ�����࣬��Lib9Editor���������Ӧ��(��Ϸ)�Ļ����ͼ��Ԫ��
 * @author not attributable
 * @version 1.0
 */

public class L9Sprite {
    /**
     * module��ӦͼƬ����ɫ���ͣ���Ҫ������ɫ��RGB��ɫ����͸��ɫ��RGB��ɫ
     */
    final static byte K_Color_Type_Index = 0;
    final static byte K_Color_Type_RGB = 1;
    final static byte K_Color_Type_RGBA = 2;

    /**
     * module����ʹ�õ�ѹ���㷨,K_Alpha_Value���Ϊ�ж��Ƿ����͸��ɫ��ʶ
     */
    final static byte K_Compress_Type_None = 0;
    final static byte K_Compress_Type_LZ77 = 1;
    final static byte K_Compress_Type_RLE = 2;
    final static byte K_Alpha_Value = 3;
    /**
     * sprite���ͱ�ʶ,��Ҫ�ɵ���module,����frame����������
     */
    private byte _spriteType;
    /**
     * ��ʾ��sprite�ǵ�����module
     */
    public final static int K_Sprite_Single_Module = 0;
    /**
     * ��ʾ��sprite�ǵ�����frame
     */
    public final static int K_Sprite_Single_Frame = 1;
    /**
     * ��ʾ��sprite�ǵ�����animation
     */
    public final static int K_Sprite_Single_Animation = 2;
    /**
     * ��ʾ��sprite����������
     */
    public final static int K_Sprite_Sound = 3;
    /**
     * ��ʾ��sprite��һ����ͼ
     */
    public final static int K_Sprite_Map = 4;
    /**
     * ����sprite����ʾsprite���ܺ���module,frame,animation,map,sound�е�һ�ֻ��߼���
     */
    public final static int K_Sprite_General = 5;

    /**
     * ����sprite�����ͣ�����������鿴sprite���ͳ���
     * @return int
     */
    public int getSpriteType() {
        return _spriteType;
    }

    /**
     * ����Top����,setFModule����(�����滻֡�ĵ�Module)�Ĳ������滻��module��ԭ����module��С��һ������Ҫѡ����뷽ʽ
     */
    public final static int K_Align_Top = 1 << 1;
    /**
     * ����Bottom����,setFModule����(�����滻֡�ĵ�Module)�Ĳ������滻��module��ԭ����module��С��һ������Ҫѡ����뷽ʽ
     */
    public final static int K_Align_Bottom = 1 << 2;
    /**
     * ����Left����,setFModule����(�����滻֡�ĵ�Module)�Ĳ������滻��module��ԭ����module��С��һ������Ҫѡ����뷽ʽ
     */
    public final static int K_Align_Left = 1 << 3;
    /**
     * ����Left����,setFModule����(�����滻֡�ĵ�Module)�Ĳ������滻��module��ԭ����module��С��һ������Ҫѡ����뷽ʽ
     */
    public final static int K_Align_Right = 1 << 4;
    /**
     * ����X����ж���,setFModule����(�����滻֡�ĵ�Module)�Ĳ������滻��module��ԭ����module��С��һ������Ҫѡ����뷽ʽ
     */
    public final static int K_Align_X_Middle = 1 << 5;
    /**
     * ����Y����ж���,setFModule����(�����滻֡�ĵ�Module)�Ĳ������滻��module��ԭ����module��С��һ������Ҫѡ����뷽ʽ
     */
    public final static int K_Align_Y_Middle = 1 << 6;

    private byte bZ = 0; //��ʾ�Ƿ�ѹ��

    private int BS_Flag; //���
    //////////////////////////////////////////////////
//    private int _npal = 1; //������һ�ֵ�ɫ�壬����ͼƬ����ĵ�ɫ��
    private int _nMaxPal = 1; //����ͼƬ��ɫ������ֵ
    private int[][][] _pals;
    private int[] _trans;
    private byte[] _image_type; //�ж���rgb���ǵ�ɫ��ͼƬ

    /**
     * ����ͼƬ��ɫ�����ɫ�����������ڳ�����ͨ���ı��ɫ�����ɫ���ﵽ�ḻɫ�ʵ�Ŀ�ģ����磺�ڿ���DIY���ǵ���Ϸ�����ǳ�����
     * @param img_index int
     * @param pal_index int
     * @param color_index int
     * @param color int
     */
    public void setPalColor(int img_index, int pal_index, int color_index,
                            int color) {
        _pals[img_index][pal_index][color_index] = color;
    }

    /**
     * ���ص�ɫ�����ɫֵ
     * @param img_index int
     * @param pal_index int
     * @param color_index int
     * @return int
     */
    public int getPalColor(int img_index, int pal_index, int color_index) {
        return _pals[img_index][pal_index][color_index];
    }

    // Modules...
    private short _nModules; // number of modules
    private short _nImages; //ͼƬ��
    private short _nSounds; //������
    private short[] _images_id;
    private short[] _modules_w; // width for each module
    private short[] _modules_h; // height for each module
    private byte[] _b_misc; //���ֱ�ʶ

    // Frames...
    private short[] _frames_nfm; // number of FModules (max 256 FModules/Frame)
    private short[] _frames_fm_start; // index of the first FModule
    private short[] _frames_w; //֡��
    private short[] _frames_h; //֡��
    // FModules...
    final static int K_FM_Bytes_Num = 9;
    private short _nFModules;
    private byte[] _fmodules; // 9 for each FModule(module_id,x,y,flag,pal,isP)

    /**
     * sprite�����������Ķ���
     */
    public L9Animation[] Animations;

    private short _nFrames;
    private short _nAFrames;
    private short _nAnims;
    private short _nMaps; //һֱ����1�����������Ŀ����Ϊ������ǰ�ĸ�ʽ���ݣ���ǰ�����ж��ŵ�ͼ

    // Graphics data (for each module)...
    private byte[][] _modules_data; // encoded image data for all modules
    private Image[][] _modules_image; // cache image for each module / with each palette
    private int[][][] _modules_rgb; // cache rgb for each module / with each palette
    /**
     * sprite���������������������������
     */
    public byte[][] _sound_data;

    // Anims...
    private short[] _anims_naf; // number of AFrames
    private short[] _anims_af_start; // index of the first AFrame
    private short[] _anims_w;
    private short[] _anims_h;
    private byte[] _anims_fps; //fps max 127
    // AFrames...
    final static int K_AF_Bytes_Num = 8;
    private byte[] _aframes; // 8 int for each AFrame(frame_id,time,x,y)
    /**
     * ����������ͼ������,���磺��ͼ���е�module,frame,animation,tile��
     */
    public byte[] _mapData;
    /**
     * Ĭ�Ϲ��캯��
     */
    public L9Sprite() {
    }

    /**
     * ʹ��fileָ�����ļ�������CSprite
     * @param file String
     */
    public L9Sprite(String file) {
        Load(file);
    }

    /**
     * ����������������Sprite����L9InputStreamΪ�Զ������������Ϊ�˺�C/C++,C#�����Լ��ݣ�ʹ�ôӵ͵��ߵ��ֽ�˳���ȡ
     * @param in L9InputStream
     */
    public L9Sprite(L9InputStream in) {
        Load2(in);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * ���������ļ�
     * @param sFile String
     */
    public void Load(String sFile) {
        InputStream is = "".getClass().getResourceAsStream(sFile);
        Load2(new L9InputStream(is));
    }

    /**
     * ���������ļ�
     * @param file byte[]
     * @param off int
     */
    public void Load2(L9InputStream in) {
        try {
            in.skipBytes(1); //�����ļ���ʶ
            _spriteType = in.readByte(); //�������ͱ�ʶ
            in.skipBytes(2); //�汾��Ϣ

            bZ = in.readByte(); //�Ƿ�ѹ����ʶ
            byte[] bin = null;
            byte[] pRs = null;
            switch (bZ) {
            case K_Compress_Type_None:
                break;
            case K_Compress_Type_LZ77:
                bin = in.getBytes();
                pRs = L9Util.LZ77_Decode(bin, in.getOff(),
                                         bin.length - in.getOff());
                in = new L9InputStream(pRs, 0, pRs.length);
                break;
            case K_Compress_Type_RLE: {
                bin = in.getBytes();
                pRs = L9Util.RLE_Decode(bin, in.getOff(),
                                        bin.length - in.getOff());
                in = new L9InputStream(pRs, 0, pRs.length);
            }
            break;
            }

            BS_Flag = in.readInt();

            _nSounds = in.readShort();
            if (L9Config.bDebugSprite) {
                System.out.println("nSounds = " + _nSounds);
            }
            if (_nSounds > 0) {
                _sound_data = new byte[_nSounds][];
                for (int i = 0; i < _nSounds; i++) {
                    int dataSize = in.readInt(); //ѹ��ǰ���ݴ�С
                    _sound_data[i] = in.readBytes(dataSize);
                }
            }

            if (_spriteType == K_Sprite_Sound) {
                return;
            }

            //Images
            _nImages = in.readShort();
            if (_nImages > 0) {
                _trans = new int[_nImages];
                _image_type = new byte[_nImages];
                _pals = new int[_nImages][][];
                for (int i = 0; i < _nImages; i++) {
                    _trans[i] = in.readInt();
                    _image_type[i] = in.readByte(); //image_type���ᳬ��127
                    if (_image_type[i] == K_Color_Type_Index) { //ֻ�е�ɫ��ͼ��ű����ɫ��
                        int _npal = in.readShort();
                        if (_npal > _nMaxPal) { //��¼���ĵ�ɫ����
                            _nMaxPal = _npal;
                        }
                        int ncolor = in.readShort();
                        _pals[i] = new int[_npal][ncolor];
                        for (int j = 0; j < _npal; j++) {
                            for (int k = 0; k < ncolor; k++) {
                                _pals[i][j][k] = in.readInt();
                            }
                        }
                    }
                }
            }

            // Modules...
            _nModules = in.readShort();

            if (L9Config.bDebugSprite) {
                System.out.println("nModules = " + _nModules);
            }
            if (_nModules > 0) {
                //_modules_x = new int[_nModules];
                //_modules_y = new int[_nModules];
                _images_id = new short[_nModules];
                _modules_w = new short[_nModules];
                _modules_h = new short[_nModules];
                _modules_data = new byte[_nModules][];
                _b_misc = new byte[_nModules];
                for (int i = 0; i < _nModules; i++) {
                    _images_id[i] = in.readShort();
                    _modules_w[i] = in.readShort();
                    _modules_h[i] = in.readShort();
                    _b_misc[i] = in.readByte(); //bZ���ᳬ��127
                    int img_size = in.readInt();
                    if (img_size > 0) { //img_sizeС�ڵ���0��ʾ����ǹ�����Դ
                        _modules_data[i] = in.readBytes(img_size);
                    }
                }
            }

            if (_spriteType == K_Sprite_Single_Module) {
                return;
            }

            // FModules...
            _nFModules = in.readShort();
            if (L9Config.bDebugSprite) {
                System.out.println("nFModules = " + _nFModules);
            }
            if (_nFModules > 0) {
                _fmodules = in.readBytes(_nFModules * K_FM_Bytes_Num);
            }
            // Frames...
            _nFrames = in.readShort();
            if (L9Config.bDebugSprite) {
                System.out.println("nFrames = " + _nFrames);
            }
            if (_nFrames > 0) {
                _frames_nfm = new short[_nFrames];
                _frames_fm_start = new short[_nFrames];

                _frames_w = new short[_nFrames];
                _frames_h = new short[_nFrames];
                for (int i = 0; i < _frames_nfm.length; i++) {
                    _frames_nfm[i] = in.readShort();
                    _frames_fm_start[i] = in.readShort();
                    _frames_w[i] = in.readShort();
                    _frames_h[i] = in.readShort();
                }
            }

            if (_spriteType == K_Sprite_Single_Frame) {
                return;
            }
            // AFrames...
            _nAFrames = in.readShort();
            if (L9Config.bDebugSprite) {
                System.out.println("nAFrames = " + _nAFrames);
            }

            if (_nAFrames > 0) {
                _aframes = in.readBytes(_nAFrames * K_AF_Bytes_Num);

                // Anims...
                int _nAnims = in.readShort();
                if (L9Config.bDebugSprite) {
                    System.out.println("nAnims = " + _nAnims);
                }
                if (_nAnims > 0) {
                    _anims_naf = new short[_nAnims];
                    _anims_af_start = new short[_nAnims];
                    _anims_w = new short[_nAnims];
                    _anims_h = new short[_nAnims];
                    _anims_fps = new byte[_nAnims];
                    Animations = new L9Animation[_nAnims];
                    for (int i = 0; i < _nAnims; i++) {
                        _anims_naf[i] = in.readShort();
                        _anims_af_start[i] = in.readShort();
                        _anims_w[i] = in.readShort();
                        _anims_h[i] = in.readShort();
                        _anims_fps[i] = in.readByte(); //fps<128
                    }
                    for (int i = 0; i < Animations.length; i++) {
                        Animations[i] = new L9Animation(this, i);
                    }
                }
            }

            if (_spriteType == K_Sprite_Single_Animation) {
                return;
            }

//��ͼ����
            int nMapSize = in.readInt();
            if (nMapSize > 0) { //������ڵ�ͼ�Ļ��ͻ�ȡ��ͼ����
                _mapData = in.readBytes(nMapSize);
            }

//            TUtil.System_gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ����pal��ɫ���m1-m2��ĵ�module���ݣ����m2Ϊ-1���򻺴�m1��m1�������module
     * @param m1 int ��ʼ��module
     * @param m2 int ������module,���Ϊ-1��ʾ ����m1��m1�������module
     * @param pal int �����ĳ�׵�ɫ�建��module����
     */
    public void cacheImages(int m1, int m2, int pal) {
        if (_nModules == 0) {
            return;
        }

        if (m2 == -1) {
            m2 = _nModules - 1;
        }

        if (L9Config.bUseDrawRgb) { //drawrgb
            if (_modules_rgb == null) {
                _modules_rgb = new int[_nMaxPal][][];
            }
            if (_modules_rgb[pal] == null) {
                _modules_rgb[pal] = new int[_nModules][];
            }
        } else {
            if (_modules_image == null) {
                _modules_image = new Image[_nMaxPal][];
            }
            if (_modules_image[pal] == null) {
                _modules_image[pal] = new Image[_nModules];
            }
        }

        for (int i = m1; i <= m2; i++) {
            if (L9Config.bUseDrawRgb) { //����Ѿ�����
                if (_modules_rgb[pal][i] != null) {
                    continue;
                }
            } else {
                if (_modules_image[pal][i] != null) {
                    continue;
                }
            }

            int w = _modules_w[i];
            int h = _modules_h[i];
            boolean bAlpha = (_b_misc[i] & (1 << K_Alpha_Value)) != 0;
            if (w <= 0 || h <= 0) {
                continue;
            }

            int[] image_data = decodeImage(i, pal);
            if (image_data == null) {
                continue;
            }
            if (L9Config.bUseDrawRgb) { //drawrgb
                _modules_rgb[pal][i] = image_data;

            } else {
                _modules_image[pal][i] = Image.createRGBImage(image_data, w, h,
                        bAlpha);
            }

            image_data = null;
        }
//        System.gc();
    }

    /**
     * �ж�pal��ɫ���ϵ�module(m1Ϊ����)�Ƿ��Ѿ�������
     * @param m1 int
     * @param pal int
     * @return boolean
     */
    public boolean isCacheImage(int m1, int pal) {
        if (_nModules == 0) {
            return false;
        }

        if (L9Config.bUseDrawRgb) { //drawrgb
            if (_modules_rgb == null) {
                _modules_rgb = new int[_nMaxPal][][];
            }
            if (_modules_rgb[pal] == null) {
                _modules_rgb[pal] = new int[_nModules][];
            }
        } else {
            if (_modules_image == null) {
                _modules_image = new Image[_nMaxPal][];
            }
            if (_modules_image[pal] == null) {
                _modules_image[pal] = new Image[_nModules];
            }
        }

        if (L9Config.bUseDrawRgb) { //����Ѿ�����
            if (_modules_rgb[pal][m1] != null) {
                return true;
            }
        } else {
            if (_modules_image[pal][m1] != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * �ͷŵ�ɫ��Ϊpal��m1-m2֮���module
     * @param m1 int
     * @param m2 int
     * @param pal int
     */
    public void freeCacheImages(int m1, int m2, int pal) {
        if (_nModules == 0) {
            return;
        }

        if (m2 == -1) {
            m2 = _nModules - 1;
        }

        if (L9Config.bUseDrawRgb) { //drawrgb
            if (_modules_rgb == null || _modules_rgb[pal] == null) {
                return;
            }
        } else {
            if (_modules_image == null || _modules_image[pal] == null) {
                return;
            }
        }

        for (int i = m1; i <= m2; i++) {
            int w = _modules_w[i];
            int h = _modules_h[i];
            if (w <= 0 || h <= 0) {
                continue;
            }
            if (L9Config.bUseDrawRgb) { //drawrgb
                _modules_rgb[pal][i] = null;
            } else {
                _modules_image[pal][i] = null;
            }
        }
//        System.gc();
    }

    /**
     * ���ظ�CSprite��modules��
     * @return int
     */
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public int getModules() {
        return _nModules;
    }

    /**
     * ��ȡmodule�Ŀ�
     * @param index int
     * @return int
     */
    public int getModuleWidth(int index) {
        return _modules_w[index];
    }

    /**
     * ��ȡmodule�ĸ�
     * @param index int
     * @return int
     */
    public int getModuleHeight(int index) {
        return _modules_h[index];
    }

    /**
     * ��ȡ��module�Ƿ���͸��ɫ��Ϣ
     * @param module_id int
     * @return boolean
     */
    public boolean getModuleAlpha(int index) {
        return (_b_misc[index] & (1 << K_Alpha_Value)) != 0;
    }

    /**
     * ���ظ�sprite��frame��
     * @return int
     */
    public int getFrames() {
        return _frames_w == null ? 0 : _frames_w.length;
    }

    /**
     * ����ĳ��Frame�ĵڼ���module���
     * @param frame int
     * @param fmodule int
     * @return int
     */
    public int getFModuleID(int frame, int fmodule) {
        int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num;
        int module_id = ((_fmodules[off++]) + (_fmodules[off++] << 8));
        return module_id;
    }

    /**
     * ����ĳ��֡�ĵ�fmodule��module��frame�е�x����
     * @param frame int
     * @param fmodule int
     * @return int
     */
    public int getFModuleX(int frame, int fmodule) {
        int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num + 2;
        return ((_fmodules[off++]) + (_fmodules[off++] << 8));
    }

    /**
     * ����ĳ��֡�ĵ�fmodule��module��frame�е�y����
     * @param frame int
     * @param fmodule int
     * @return int
     */
    public int getFModuleY(int frame, int fmodule) {
        int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num + 4;
        return ((_fmodules[off++]) + (_fmodules[off++] << 8));
    }

    /**
     * ����ĳ��֡�ĵ�fmodule��module��frame�еĵ�ɫ��
     * @param frame int
     * @param fmodule int
     * @return int
     */
    public int getFModulePal(int frame, int fmodule) {
        int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num + 7;
        return ((_fmodules[off++]));
    }

    /**
     * ��ȡframe�Ŀ�,��֡����module�Ĳ�����
     * @param frame int
     * @return int
     */
    public int getFrameWidth(int frame) {
        return _frames_w[frame];
    }

    /**
     * ��ȡframe�ĸ�,��֡����module�Ĳ�����
     * @param frame int
     * @return int
     */
    public int getFrameHeight(int frame) {
        return _frames_h[frame];
    }

    /**
     * ���ظ�sprite�Ķ�����
     * @return int
     */
    public int getAnimations() {
        return Animations == null ? 0 : Animations.length;
    }

    //����
    private void Assert(int curAnim, int aframe) {
        if (aframe >= _anims_naf[curAnim]) {
            L9Util.throwException("֡Խ���ˣ��ö���û����ô��֡! anim:" + curAnim + " frame:" +
                                  aframe + " max_frame=" + _anims_naf[curAnim]);
        }
    }

    /**
     * ���ض���֡ID�������Ƕ���֡����
     * @param aframe int
     * @return int
     */
    public int getAFrameID(int curAnim, int aframe) {
        Assert(curAnim, aframe);

        int off = (_anims_af_start[curAnim] + aframe) * K_AF_Bytes_Num;
        //off += 2;
        int frame_id = (_aframes[off++] & 0xFF) +
                       ((_aframes[off++] & 0xFF) << 8);
        return frame_id;
    }

    /**
     * ���ض���֡�ڶ����е�X���꣬�����Ƕ���֡����
     * @param aframe int
     * @return int
     */
    public int getAFrameX(int curAnim, int aframe) {
        Assert(curAnim, aframe);

        int off = (_anims_af_start[curAnim] + aframe) * K_AF_Bytes_Num;
        int frame_index = (_aframes[off++] & 0xFF) +
                          ((_aframes[off++] & 0xFF) << 8);
        int time = (_aframes[off++] & 0xFF) + ((_aframes[off++] & 0xFF) << 8);

        //��������Ϊ����
        int af_x = L9InputStream.readShortLow(_aframes, off);
        off += 2;
        int af_y = L9InputStream.readShortLow(_aframes, off);
        off += 2;

        return af_x;
    }

    /**
     * ���ض���֡�ڶ����е�Y���꣬�����Ƕ���֡����
     * @param aframe int
     * @return int
     */
    public int getAFrameY(int curAnim, int aframe) {
        Assert(curAnim, aframe);

        int off = (_anims_af_start[curAnim] + aframe) * K_AF_Bytes_Num;
        int frame_index = (_aframes[off++] & 0xFF) +
                          ((_aframes[off++] & 0xFF) << 8);
        int time = (_aframes[off++] & 0xFF) + ((_aframes[off++] & 0xFF) << 8);

        //��������Ϊ����
        int af_x = L9InputStream.readShortLow(_aframes, off);
        off += 2;
        int af_y = L9InputStream.readShortLow(_aframes, off);
        off += 2;

        return af_y;
    }

    /**
     * ���ض���֡�Ĳ��Ŵ����������Ƕ���֡����
     * @param aframe int
     * @return int
     */
    public int getAFrameTime(int curAnim, int aframe) {
        Assert(curAnim, aframe);

        int off = (_anims_af_start[curAnim] + aframe) * K_AF_Bytes_Num;
        off += 2;
        int time = (_aframes[off++] & 0xFF) + ((_aframes[off++] & 0xFF) << 8);
        return time;
    }

    /**
     * ��ȡ�����Ŀ�,�ö�������frame�Ĳ�����
     * @return int
     */
    public int getAnimWidth(int curAnim) {
        return _anims_w[curAnim];
    }

    /**
     * ��ȡ�����ĸ�,�ö�������frame�Ĳ�����
     * @return int
     */
    public int getAnimHeight(int curAnim) {
        return _anims_h[curAnim];
    }

    /**
     * ����ÿ�붯�����ŵ�֡������fpsֵ
     * @return int
     */
    public int getAnimFPS(int curAnim) {
        return _anims_fps[curAnim];
    }

    /**
     * ���ض�����֡��
     * @return int
     */
    public int getAFrames(int curAnim) {
        return _anims_naf[curAnim];
    }

    /**
     * ��λ��posX,posY����frame
     * @param g Graphics
     * @param frame int Ҫ���Ƶ�֡����
     * @param posX int ����֡��X����
     * @param posY int ����֡��Y����
     */
    public void paintFrame(Graphics g, int frame, int posX, int posY) {
        //	System.out.println("PaintFrame(g, "+frame+", "+posX+", "+posY+", 0x"+", "+hx+", "+hy+")");
        int nFModules = _frames_nfm[frame];

        for (int fmodule = 0; fmodule < nFModules; fmodule++) {
            int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num;
            int module_id = ((_fmodules[off++]) + (_fmodules[off++] << 8));
            //��������Ϊ����
//     int fm_x = (_fmodules[off++]) + (_fmodules[off++] << 8);
//     int fm_y = (_fmodules[off++]) + (_fmodules[off++] << 8);
            int fm_x = L9InputStream.readShortLow(_fmodules, off);
            off += 2;
            int fm_y = L9InputStream.readShortLow(_fmodules, off);
            off += 2;

            int fm_flag = _fmodules[off++];
            int fm_pal = _fmodules[off++];

            int x = posX + fm_x;
            int y = posY + fm_y;

            if ((fm_flag & K_Flag_MD_Frm) != 0) { //��ʾframe moduleΪ֡
                paintFrame(g, module_id, x, y);
            } else {
                paintModule(g, module_id, x, y, fm_flag, fm_pal);
            }
        }
    }

    /**
     * ��������֡,ʵ���Ͼ��ǻ����֡�е�����module
     * @param frame int
     */
    public void cacheFrame(int frame) {
        //	System.out.println("PaintFrame(g, "+frame+", "+posX+", "+posY+", 0x"+Integer.toHexString(flags)+", "+hx+", "+hy+")");
        int nFModules = _frames_nfm[frame];

        for (int fmodule = 0; fmodule < nFModules; fmodule++) {
            int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num;
            int module_id = ((_fmodules[off++]) + (_fmodules[off++] << 8));
            //��������Ϊ����
//     int fm_x = (_fmodules[off++]) + (_fmodules[off++] << 8);
//     int fm_y = (_fmodules[off++]) + (_fmodules[off++] << 8);
            int fm_x = L9InputStream.readShortLow(_fmodules, off);
            off += 2;
            int fm_y = L9InputStream.readShortLow(_fmodules, off);
            off += 2;

            int fm_flag = _fmodules[off++];
            int fm_pal = _fmodules[off++];

            if ((fm_flag & K_Flag_MD_Frm) != 0) { //��ʾframe moduleΪ֡
                cacheFrame(module_id);
            } else {
                cacheImages(module_id, module_id, fm_pal);
            }
        }
    }

    /**
     * �ͷŸ�֡�Ļ��棬�������Լ����ڴ�
     * @param frame int
     */
    public void freeFrameCache(int frame) {
        int nFModules = _frames_nfm[frame];

        for (int fmodule = 0; fmodule < nFModules; fmodule++) {
            int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num;
            int module_id = ((_fmodules[off++]) + (_fmodules[off++] << 8));
            //��������Ϊ����
//     int fm_x = (_fmodules[off++]) + (_fmodules[off++] << 8);
//     int fm_y = (_fmodules[off++]) + (_fmodules[off++] << 8);
            int fm_x = L9InputStream.readShortLow(_fmodules, off);
            off += 2;
            int fm_y = L9InputStream.readShortLow(_fmodules, off);
            off += 2;

            int fm_flag = _fmodules[off++];
            int fm_pal = _fmodules[off++];

            if ((fm_flag & K_Flag_MD_Frm) != 0) { //��ʾframe moduleΪ֡
                freeFrameCache(module_id);
            } else {
                //BuildCacheImages(module_id, module_id, fm_pal);
                freeCacheImages(module_id, module_id, fm_pal);
            }
        }
    }

    /**
     * ��ģ��new_md_id�滻����֡�����е�ģ��old_md_id,ͬʱ΢�����꣬�������
     * @param frame int
     * @param old_md_id int
     * @param new_md_id int
     */

    public void setFModule(int frame, int old_md_id, int new_md_id,
                           int align_flag) {
        int nFModules = _frames_nfm[frame];

        for (int fmodule = 0; fmodule < nFModules; fmodule++) {
            int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num;
            int module_id = ((_fmodules[off++]) + (_fmodules[off++] << 8));
            if (module_id == old_md_id) {
                int fm_x = L9InputStream.readShortLow(_fmodules, off);
                off += 2;
                int fm_y = L9InputStream.readShortLow(_fmodules, off);
                off += 2;

                int _w = (_modules_w[old_md_id] - _modules_w[new_md_id]);
                int _h = (_modules_h[old_md_id] - _modules_h[new_md_id]);

                //Ĭ�Ͼ���
                int XX = fm_x + _w / 2;
                int YY = fm_y + _h / 2;

                if ((align_flag & K_Align_Top) != 0) {
                    YY = fm_y;
                } else if ((align_flag & K_Align_Bottom) != 0) {
                    YY = fm_y + _h;
                }
                if ((align_flag & K_Align_Left) != 0) {
                    XX = fm_x;
                } else if ((align_flag & K_Align_Right) != 0) {
                    XX = fm_x + _w;
                }
                if ((align_flag & K_Align_X_Middle) != 0) {
                    XX = fm_x + _w / 2;
                }
                if ((align_flag & K_Align_Y_Middle) != 0) {
                    YY = fm_y + _h / 2;
                }

                off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num;
                L9OutputStream.writeShortLow(_fmodules, off, (short) new_md_id);
                off += 2;
                L9OutputStream.writeShortLow(_fmodules, off, (short) XX);
                off += 2;
                L9OutputStream.writeShortLow(_fmodules, off, (short) YY);
                off += 2;
            }
        }
    }

    /**
     * ��ģ��new_md_id�滻������֡�����е�ģ��old_md_id,ͬʱ΢�����꣬�������
     * @param old_md_id int
     * @param new_md_id int
     * @param align_flag int
     */
    public void setFModuleAll(int old_md_id, int new_md_id, int align_flag) {
        for (int i = 0; i < _frames_nfm.length; i++) {
            setFModule(i, old_md_id, new_md_id, align_flag);
        }
    }

    /**
     * ����frame֡�ĵ�fmodule��ģ����֡�е�X����
     * @param frame int
     * @param fmodule int
     * @param fm_x int
     */
    public void setFModuleX(int frame, int fmodule, int fm_x) {
        int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num + 2;
        L9OutputStream.writeShortLow(_fmodules, off, (short) fm_x);
    }

    /**
     * ����frame֡�ĵ�fmodule��ģ����֡�е�Y����
     * @param frame int
     * @param fmodule int
     * @param fm_y int
     */
    public void setFModuleY(int frame, int fmodule, int fm_y) {
        int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num + 4;
        L9OutputStream.writeShortLow(_fmodules, off, (short) fm_y);
    }

    /**
     * ����frame֡�ĵ�fmodule��ģ����֡�еĵ�ɫ��
     * @param frame int
     * @param fmodule int
     * @param fm_pal int
     */
    public void setFModulePal(int frame, int fmodule, int fm_pal) {
        int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num + 7;
        _fmodules[off] = (byte) fm_pal;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * ʹ��Ĭ�ϵ�ɫ�����module,ÿһ��֡(Frame)���������һ�����߶��ģ��(Module)���
     * @param g Graphics
     * @param module_id int Ҫ���Ƶ�ģ��ID
     * @param posX int Ҫ���Ƶ�ģ���X����
     * @param posY int Ҫ���Ƶ�ģ���Y����
     * @param flags int ����ʱ�ķ�ת����ת��Ϣ ��Ҫ��X��ת��Y��ת��˳ʱ����ת90�ȼ����ǵ����
     */
    public void paintModule(Graphics g, int module_id, int posX, int posY,
                            int flags) {
        paintModule(g, module_id, posX, posY, flags, 0);
    }

    private int[] _share_module_id;
    private int[] _own_module_id;
    private L9Sprite _spriteShare;
    /**
     * ����Ϸ�У�Ϊ�˽�ʡ��Դ������sprite������Թ���ĳЩmodule����
     * @param spriteShare L9Sprite
     * @param share_module_id int[],���������¼���ǹ����sprite��module
     * @param own_module_id int[],�����¼�����Լ�sprite����û�д洢��Ҫʹ�ù���sprite��module��own_module_idӦ����share_module_id���Ӧ
     */
    public void setShareModules(L9Sprite spriteShare, int[] share_module_id,
                                int[] own_module_id) {
        _spriteShare = spriteShare;
        _share_module_id = share_module_id;
        _own_module_id = own_module_id;
    }

    /**
     * ����module��Ϣ,ÿһ��֡(Frame)���������һ�����߶��ģ��(Module)���,ʹ��palָ���ĵ�ɫ��������
     * @param g Graphics
     * @param module_id int Ҫ���Ƶ�ģ��ID
     * @param posX int Ҫ���Ƶ�ģ���X����
     * @param posY int Ҫ���Ƶ�ģ���Y����
     * @param flags int ����ʱ�ķ�ת����ת��Ϣ ��Ҫ��X��ת��Y��ת��˳ʱ����ת90�ȼ����ǵ����
     * @param pal int  ʹ�����׵�ɫ��������
     */
    public void paintModule(Graphics g, int module_id, int posX, int posY,
                            int flags, int pal) {
        //������������sprite
        int iFind = L9Util.getIndexByID(_own_module_id, module_id);
        if (iFind != -1) {
            _spriteShare.paintModule(g, _share_module_id[iFind], posX, posY,
                                     flags, pal);
            return;
        }

        if (pal < 0) {
            pal = 0;
        }

        //	DEBUG_ASSERT(module >= 0, "module >= 0");
        //System.out.println("module_id="+module_id);
        int w = _modules_w[module_id];
        int h = _modules_h[module_id];
        boolean bAlpha = getModuleAlpha(module_id);
        if (w <= 0 || h <= 0) {
            return;
        }

        if (L9Config.bUseDrawRgb) { //drawrgb
            int[] rgb = null;
            if (_modules_rgb != null && _modules_rgb[pal] != null) {
                rgb = _modules_rgb[pal][module_id];
            }
            if (rgb == null) {
                rgb = decodeImage(module_id, pal);
            }
            // Draw...
            rgb = transformRgb(rgb, w, h, flags);
            g.drawRGB(rgb, 0, w, posX, posY, w, h, bAlpha);
            //System.out.println("drawrgb");
        } else {
            Image img = null;
            // Try to use cached image
            if (_modules_image != null && _modules_image[pal] != null) {
                img = _modules_image[pal][module_id];
            }

            // Build RGB image...
            if (img == null) {
                int[] rgb = decodeImage(module_id, pal);
                if (rgb == null) {
                    if (L9Config.bDebugSprite) {
                        System.out.println("DecodeImage() FAILED !");
                    }
                    return;
                }
                img = Image.createRGBImage(rgb, w, h, bAlpha);
            }
            // Draw...
            switch (flags &
                    (K_Flag_Flip_X | K_Flag_Flip_Y |
                     K_Flag_Rotate_90)) {
            case K_Flag_Flip_X:
                g.drawRegion(img, 0, 0, w, h, Sprite.TRANS_MIRROR_ROT180, posX,
                             posY,
                             0);
                break;
            case K_Flag_Flip_Y:
                g.drawRegion(img, 0, 0, w, h, Sprite.TRANS_MIRROR, posX, posY,
                             0);
                break;
            case K_Flag_Flip_X | K_Flag_Flip_Y:
                g.drawRegion(img, 0, 0, w, h, Sprite.TRANS_ROT180, posX, posY,
                             0);
                break;
            case K_Flag_Rotate_90:
                g.drawRegion(img, 0, 0, w, h, Sprite.TRANS_ROT90, posX, posY, 0);
                break;
            case K_Flag_Rotate_90 | K_Flag_Flip_X:
                g.drawRegion(img, 0, 0, w, h, Sprite.TRANS_MIRROR_ROT270, posX,
                             posY,
                             0);
                break;
            case K_Flag_Rotate_90 | K_Flag_Flip_Y:
                g.drawRegion(img, 0, 0, w, h, Sprite.TRANS_MIRROR_ROT90, posX,
                             posY,
                             0);
                break;
            case K_Flag_Rotate_90 | K_Flag_Flip_X |
                    K_Flag_Flip_Y:
                g.drawRegion(img, 0, 0, w, h, Sprite.TRANS_ROT270, posX, posY,
                             0);
                break;
            default: //flag =0

//          g.drawRegion(img, 0, 0, w, h, Sprite.TRANS_NONE, posX, posY, 0);
                g.drawImage(img, posX, posY, 0);
                break;
            }
        }
    }

    /**
     * ���ͼƬ�Ѿ������ˣ�������������ͼƬ��ԭʼ���ݣ��������Խ�ʡ�ڴ�,ע����øú����������ٵ���FreeCacheImages�������ͷŻ���ĺ����ˣ���Ϊû��ԭʼ�����ˣ����潫�޷��ٴ���ͼƬ
     */
    public void freeImageData() {
//        if (_nMaxPal > 1) { //��ʱ��������׵�ɫ��
//            return;
//        }

        if (L9Config.bUseDrawRgb) { //drawrgb
            int[] img = null;

            for (int pal = 0; pal < _nMaxPal; pal++) {
                for (int i = 0; i < _modules_data.length; i++) {
                    img = null;
                    if (_modules_rgb != null && _modules_rgb[pal] != null) {
                        img = _modules_rgb[pal][i];
                    }
                    if (img != null) { //�Ѿ������ˣ����������Ӧ��module������
                        _modules_data[i] = null;
                    }
                }
            }
        } else {
            Image img = null;

            for (int pal = 0; pal < _nMaxPal; pal++) {
                for (int i = 0; i < _modules_data.length; i++) {
                    img = null;
                    if (_modules_image != null && _modules_image[pal] != null) {
                        img = _modules_image[pal][i];
                    }
                    if (img != null) { //�Ѿ������ˣ����������Ӧ��module������
                        _modules_data[i] = null;
                    }
                }
            }
        }
    }

    /**
     * ��module���ݽ���Ϊargb���� �Ա��ڻ���
     * @param module_id int
     * @param pal_index int
     * @return int[]
     */

    public int[] decodeImage(int module_id, int pal_index) {
        //	System.out.println("DecodeImage("+module+", 0x"+Integer.toHexString(flags)+")...");
        if (_modules_data == null || module_id < 0 ||
            module_id > _modules_data.length - 1 || _modules_data[module_id] == null) {
            return null;
        }

        int w = _modules_w[module_id];
        int h = _modules_h[module_id];
        if (w <= 0 || h <= 0) {
            return null;
        }
        int[] rgb = null;
        // Choose palette...
        int _cur_img = _images_id[module_id];
        int[] pal = null;
        if (_image_type[_cur_img] == K_Color_Type_Index) {
            if (pal_index >= _pals[_cur_img].length) {
                return null;
            }
            pal = _pals[_cur_img][pal_index];
        }

        rgb = new int[w * h];

        int bM = _b_misc[module_id];

        //System.out.println("bM="+bM);
        switch (_image_type[_cur_img]) {
        case K_Color_Type_Index:
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int index = _modules_data[module_id][y * w + x] & 0xFF;
                    //����ɫҪô͸��Ҫô��͸��,û���м�Ľ���
                    rgb[y * w + x] = pal[index];
                    if (rgb[y * w + x] == _trans[_cur_img]) {
                        rgb[y * w + x] &= 0x00FFFFFF;
                    } else {
                        rgb[y * w + x] |= (0xFF << 24);
                    }
                }
            }
            break;
        case K_Color_Type_RGB:
        case K_Color_Type_RGBA:
            int offset = 0;
            byte[] pByte = _modules_data[module_id];
            boolean bAlpha = (pByte[offset++] == 1); //��һ���ֽڴ洢͸����ʾ
            int size = w * h;
            int xx_off = 0;
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    try {
                        xx_off = i * w + j;
                        rgb[xx_off] = (pByte[xx_off] & 0xFF) << 16; //R
                        rgb[xx_off] += (pByte[xx_off + size] & 0xFF) << 8; //G
                        rgb[xx_off] += (pByte[xx_off + 2 * size] & 0xFF); //B
                        if (bAlpha) {
                            rgb[xx_off] |= (pByte[xx_off + 3 * size] & 0xFF) <<
                                    24; //A
                            //System.out.println("a="+(pAlphaRs[xx_off]&0xFF));
                        } else {
                            rgb[xx_off] |= (0xFF) << 24;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }

            //LZ77_UnCompress(_modules_data[module_id], 0, w, h, rgb);
            break;
        }
        return rgb;
    }


    //////////////////////////////////////////////////////////////////////////////
    /**
     * module��ת����ת��ʶ,��Ҫ�� X��ת��Y��ת,˳ʱ����ת90��,ʵ�ʵ�ʹ�ÿ����ǵ�����һ���������ǵ����
     * ���磺flag=K_Flag_Flip_X|K_Flag_Rotate_90
     * X��ת��ʶ
     */
    public final static byte K_Flag_Flip_X = 0x01;
    /**
     * module��ת����ת��ʶ,��Ҫ�� X��ת��Y��ת,˳ʱ����ת90��,ʵ�ʵ�ʹ�ÿ����ǵ�����һ���������ǵ����
     * ���磺flag=K_Flag_Flip_X|K_Flag_Rotate_90
     * Y��ת��ʶ
     */
    public final static byte K_Flag_Flip_Y = 0x02;
    /**
     * module��ת����ת��ʶ,��Ҫ�� X��ת��Y��ת,˳ʱ����ת90��,ʵ�ʵ�ʹ�ÿ����ǵ�����һ���������ǵ����
     * ���磺flag=K_Flag_Flip_X|K_Flag_Rotate_90
     * ˳ʱ����ת90�ȱ�ʶ
     */
    public final static byte K_Flag_Rotate_90 = 0x04;
    /**
     * ֡�е�module�Ƿ�Ϊ֡�ı�ʶ,L9Sprite��֧֡�ְ���֡�������ʶ��������
     */
    public final static byte K_Flag_MD_Frm = 0x08; //�����ʾframe_module�д洢����֡��������module

    /**
     * ����flags��Ƕ�ͼƬ��rgb����ִ�з�ת����ת����
     * @param image_data int[]
     * @param w int
     * @param h int
     * @param flags int
     * @return int[]
     */
    public static int[] transformRgb(int[] image_data, int w, int h, int flags) {
        int[] rgb_transform = new int[image_data.length];
        // no transform
        if ((flags & (K_Flag_Flip_X | K_Flag_Flip_Y | K_Flag_Rotate_90)) == 0) {
            return image_data;
        }
        switch (flags & (K_Flag_Flip_X | K_Flag_Flip_Y | K_Flag_Rotate_90)) {
        case K_Flag_Flip_X:
            rgb_transform = flipX(image_data, w, h);
            break;

        case K_Flag_Flip_Y:
            rgb_transform = flipY(image_data, w, h);

            break;

        case (K_Flag_Flip_X | K_Flag_Flip_Y):
            rgb_transform = rotate180(image_data, w, h);

//            rgb_transform = Flip_X(image_data, w, h);
//            rgb_transform = Flip_Y(rgb_transform, w, h);
            break;

        case K_Flag_Rotate_90:
            rgb_transform = rotate90(image_data, w, h);
            w += h;
            h = w - h;
            w = w - h;

            break;

        case (K_Flag_Flip_X | K_Flag_Rotate_90):
            rgb_transform = flipX(image_data, w, h);
            rgb_transform = rotate90(rgb_transform, w, h);
            w += h;
            h = w - h;
            w = w - h;
            break;
        case (K_Flag_Flip_Y | K_Flag_Rotate_90):
            rgb_transform = flipY(image_data, w, h);
            rgb_transform = rotate270(rgb_transform, w, h);
            w += h;
            h = w - h;
            w = w - h;
            break;

        case (K_Flag_Flip_X | K_Flag_Flip_Y | K_Flag_Rotate_90):
            rgb_transform = rotate270(image_data, w, h);
            w += h;
            h = w - h;
            w = w - h;

            break;
        }

        return rgb_transform;

    }

    /**
     * ��ͼ����ת270��
     * @param src int[]
     * @param width int
     * @param height int
     * @return int[]
     */
    public static int[] rotate270(int src[], int width, int height) {
        int size = width * height;
        int[] temp = new int[size];

        for (int i = 0; i < size; i++) {
            temp[i] = src[((i + 1) * width - i / height - 1) % size];
        }

        return temp;
    }

    /**
     * ��ͼ����ת180��
     * @param src int[]
     * @param width int
     * @param height int
     * @return int[]
     */
    public static int[] rotate180(int src[], int width, int height) {
        int size = width * height;
        int temp;

        for (int i = 0; i < size / 2; i++) {
            temp = src[i];
            src[i] = src[size - i - 1];
            src[size - i - 1] = temp;
        }

        return src;
    }

    /**
     * ��ͼ����ת90��
     * @param src int[]
     * @param width int
     * @param height int
     * @return int[]
     */
    public static int[] rotate90(int src[], int width, int height) {
        int size = width * height;
        int[] temp = new int[size];

        for (int i = 0; i < size; i++) {
            int offset = (width * (i + 1)) % size;
            if (offset == 0) {
                offset = size;
            }

            temp[i] = src[size + i / height - offset];
        }

        return temp;
    }

    /**
     * ��ͼ�����X�ᷭת
     * @param src int[]
     * @param width int
     * @param height int
     * @return int[]
     */
    public static int[] flipY(int src[], int width, int height) {
        int size = width * height;
        int[] temp = new int[size];

        for (int i = 0; i < size; i++) {
            temp[i] = src[(i / width) * width + (width - (i % width) - 1)];
        }

        return temp;
    }

    /**
     * ��ͼ�����Y�ᷭת
     * @param src int[]
     * @param width int
     * @param height int
     * @return int[]
     */
    public static int[] flipX(int src[], int width, int height) {
        int size = width * height;
        int[] temp = new int[size];

        for (int i = 0; i < size; i++) {
            temp[i] = src[(height - i / width - 1) * width + (i % width)];
        }

        return temp;
    }

} // class ASprite

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
