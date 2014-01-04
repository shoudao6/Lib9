package lib9.j2me;

import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.Sprite; // just for MIDP2 transformations
import javax.microedition.media.*;

/**
 * Lib9引擎中一个非常重要的类，一般称为精灵类，与Lib9Editor的配合制作应用(游戏)的画面的图形元素
 * @author not attributable
 * @version 1.0
 */

public class L9Sprite {
    /**
     * module对应图片的颜色类型，主要有索引色、RGB颜色、带透明色的RGB颜色
     */
    final static byte K_Color_Type_Index = 0;
    final static byte K_Color_Type_RGB = 1;
    final static byte K_Color_Type_RGBA = 2;

    /**
     * module数据使用的压缩算法,K_Alpha_Value这个为判断是否带有透明色标识
     */
    final static byte K_Compress_Type_None = 0;
    final static byte K_Compress_Type_LZ77 = 1;
    final static byte K_Compress_Type_RLE = 2;
    final static byte K_Alpha_Value = 3;
    /**
     * sprite类型标识,主要由单个module,单个frame、单个动画
     */
    private byte _spriteType;
    /**
     * 表示该sprite是单个的module
     */
    public final static int K_Sprite_Single_Module = 0;
    /**
     * 表示该sprite是单个的frame
     */
    public final static int K_Sprite_Single_Frame = 1;
    /**
     * 表示该sprite是单个的animation
     */
    public final static int K_Sprite_Single_Animation = 2;
    /**
     * 表示该sprite是声音数据
     */
    public final static int K_Sprite_Sound = 3;
    /**
     * 表示该sprite是一个地图
     */
    public final static int K_Sprite_Map = 4;
    /**
     * 泛型sprite，表示sprite可能含有module,frame,animation,map,sound中的一种或者几种
     */
    public final static int K_Sprite_General = 5;

    /**
     * 返回sprite的类型，具体类型请查看sprite类型常量
     * @return int
     */
    public int getSpriteType() {
        return _spriteType;
    }

    /**
     * 常量Top对齐,setFModule函数(用来替换帧的的Module)的参数，替换的module和原来的module大小不一样是需要选择对齐方式
     */
    public final static int K_Align_Top = 1 << 1;
    /**
     * 常量Bottom对齐,setFModule函数(用来替换帧的的Module)的参数，替换的module和原来的module大小不一样是需要选择对齐方式
     */
    public final static int K_Align_Bottom = 1 << 2;
    /**
     * 常量Left对齐,setFModule函数(用来替换帧的的Module)的参数，替换的module和原来的module大小不一样是需要选择对齐方式
     */
    public final static int K_Align_Left = 1 << 3;
    /**
     * 常量Left对齐,setFModule函数(用来替换帧的的Module)的参数，替换的module和原来的module大小不一样是需要选择对齐方式
     */
    public final static int K_Align_Right = 1 << 4;
    /**
     * 常量X轴居中对齐,setFModule函数(用来替换帧的的Module)的参数，替换的module和原来的module大小不一样是需要选择对齐方式
     */
    public final static int K_Align_X_Middle = 1 << 5;
    /**
     * 常量Y轴居中对齐,setFModule函数(用来替换帧的的Module)的参数，替换的module和原来的module大小不一样是需要选择对齐方式
     */
    public final static int K_Align_Y_Middle = 1 << 6;

    private byte bZ = 0; //表示是否压缩

    private int BS_Flag; //标记
    //////////////////////////////////////////////////
//    private int _npal = 1; //至少有一种调色板，就是图片本身的调色板
    private int _nMaxPal = 1; //所有图片调色板的最大值
    private int[][][] _pals;
    private int[] _trans;
    private byte[] _image_type; //判断是rgb还是调色板图片

    /**
     * 设置图片调色板的颜色，这样可以在程序中通过改变调色板的颜色来达到丰富色彩的目的，比如：在可以DIY主角的游戏往往非常有用
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
     * 返回调色板的颜色值
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
    private short _nImages; //图片数
    private short _nSounds; //声音数
    private short[] _images_id;
    private short[] _modules_w; // width for each module
    private short[] _modules_h; // height for each module
    private byte[] _b_misc; //各种标识

    // Frames...
    private short[] _frames_nfm; // number of FModules (max 256 FModules/Frame)
    private short[] _frames_fm_start; // index of the first FModule
    private short[] _frames_w; //帧宽
    private short[] _frames_h; //帧高
    // FModules...
    final static int K_FM_Bytes_Num = 9;
    private short _nFModules;
    private byte[] _fmodules; // 9 for each FModule(module_id,x,y,flag,pal,isP)

    /**
     * sprite对象所包含的动画
     */
    public L9Animation[] Animations;

    private short _nFrames;
    private short _nAFrames;
    private short _nAnims;
    private short _nMaps; //一直等于1，保留这个的目的是为了与以前的格式兼容，以前允许有多张地图

    // Graphics data (for each module)...
    private byte[][] _modules_data; // encoded image data for all modules
    private Image[][] _modules_image; // cache image for each module / with each palette
    private int[][][] _modules_rgb; // cache rgb for each module / with each palette
    /**
     * sprite包含的用来创建声音对象的数据
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
     * 用来创建地图的数据,比如：地图含有的module,frame,animation,tile等
     */
    public byte[] _mapData;
    /**
     * 默认构造函数
     */
    public L9Sprite() {
    }

    /**
     * 使用file指定的文件来创建CSprite
     * @param file String
     */
    public L9Sprite(String file) {
        Load(file);
    }

    /**
     * 根据输入流来创建Sprite对象，L9InputStream为自定义的输入流，为了和C/C++,C#等语言兼容，使用从低到高的字节顺序读取
     * @param in L9InputStream
     */
    public L9Sprite(L9InputStream in) {
        Load2(in);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 加载数据文件
     * @param sFile String
     */
    public void Load(String sFile) {
        InputStream is = "".getClass().getResourceAsStream(sFile);
        Load2(new L9InputStream(is));
    }

    /**
     * 加载数据文件
     * @param file byte[]
     * @param off int
     */
    public void Load2(L9InputStream in) {
        try {
            in.skipBytes(1); //跳过文件标识
            _spriteType = in.readByte(); //导出类型标识
            in.skipBytes(2); //版本信息

            bZ = in.readByte(); //是否压缩标识
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
                    int dataSize = in.readInt(); //压缩前数据大小
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
                    _image_type[i] = in.readByte(); //image_type不会超过127
                    if (_image_type[i] == K_Color_Type_Index) { //只有调色板图像才保存调色板
                        int _npal = in.readShort();
                        if (_npal > _nMaxPal) { //记录最大的调色板数
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
                    _b_misc[i] = in.readByte(); //bZ不会超过127
                    int img_size = in.readInt();
                    if (img_size > 0) { //img_size小于等于0表示这个是共享资源
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

//地图数据
            int nMapSize = in.readInt();
            if (nMapSize > 0) { //如果存在地图的话就获取地图数据
                _mapData = in.readBytes(nMapSize);
            }

//            TUtil.System_gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 缓存pal调色板的m1-m2间的的module数据，如果m2为-1，则缓存m1及m1后的所有module
     * @param m1 int 开始的module
     * @param m2 int 结束的module,如果为-1表示 缓存m1及m1后的所有module
     * @param pal int 针对于某套调色板缓存module数据
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
            if (L9Config.bUseDrawRgb) { //如果已经缓存
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
     * 判断pal调色板上的module(m1为索引)是否已经缓存了
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

        if (L9Config.bUseDrawRgb) { //如果已经缓存
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
     * 释放调色板为pal的m1-m2之间的module
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
     * 返回该CSprite的modules数
     * @return int
     */
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public int getModules() {
        return _nModules;
    }

    /**
     * 获取module的宽
     * @param index int
     * @return int
     */
    public int getModuleWidth(int index) {
        return _modules_w[index];
    }

    /**
     * 获取module的高
     * @param index int
     * @return int
     */
    public int getModuleHeight(int index) {
        return _modules_h[index];
    }

    /**
     * 获取该module是否含有透明色信息
     * @param module_id int
     * @return boolean
     */
    public boolean getModuleAlpha(int index) {
        return (_b_misc[index] & (1 << K_Alpha_Value)) != 0;
    }

    /**
     * 返回该sprite的frame数
     * @return int
     */
    public int getFrames() {
        return _frames_w == null ? 0 : _frames_w.length;
    }

    /**
     * 返回某个Frame的第几个module编号
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
     * 返回某个帧的第fmodule个module在frame中的x坐标
     * @param frame int
     * @param fmodule int
     * @return int
     */
    public int getFModuleX(int frame, int fmodule) {
        int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num + 2;
        return ((_fmodules[off++]) + (_fmodules[off++] << 8));
    }

    /**
     * 返回某个帧的第fmodule个module在frame中的y坐标
     * @param frame int
     * @param fmodule int
     * @return int
     */
    public int getFModuleY(int frame, int fmodule) {
        int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num + 4;
        return ((_fmodules[off++]) + (_fmodules[off++] << 8));
    }

    /**
     * 返回某个帧的第fmodule个module在frame中的调色板
     * @param frame int
     * @param fmodule int
     * @return int
     */
    public int getFModulePal(int frame, int fmodule) {
        int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num + 7;
        return ((_fmodules[off++]));
    }

    /**
     * 获取frame的宽,该帧所有module的并集宽
     * @param frame int
     * @return int
     */
    public int getFrameWidth(int frame) {
        return _frames_w[frame];
    }

    /**
     * 获取frame的高,该帧所有module的并集高
     * @param frame int
     * @return int
     */
    public int getFrameHeight(int frame) {
        return _frames_h[frame];
    }

    /**
     * 返回该sprite的动画数
     * @return int
     */
    public int getAnimations() {
        return Animations == null ? 0 : Animations.length;
    }

    //断言
    private void Assert(int curAnim, int aframe) {
        if (aframe >= _anims_naf[curAnim]) {
            L9Util.throwException("帧越界了，该动画没有这么多帧! anim:" + curAnim + " frame:" +
                                  aframe + " max_frame=" + _anims_naf[curAnim]);
        }
    }

    /**
     * 返回动画帧ID，参数是动画帧索引
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
     * 返回动画帧在动画中的X坐标，参数是动画帧索引
     * @param aframe int
     * @return int
     */
    public int getAFrameX(int curAnim, int aframe) {
        Assert(curAnim, aframe);

        int off = (_anims_af_start[curAnim] + aframe) * K_AF_Bytes_Num;
        int frame_index = (_aframes[off++] & 0xFF) +
                          ((_aframes[off++] & 0xFF) << 8);
        int time = (_aframes[off++] & 0xFF) + ((_aframes[off++] & 0xFF) << 8);

        //允许坐标为负数
        int af_x = L9InputStream.readShortLow(_aframes, off);
        off += 2;
        int af_y = L9InputStream.readShortLow(_aframes, off);
        off += 2;

        return af_x;
    }

    /**
     * 返回动画帧在动画中的Y坐标，参数是动画帧索引
     * @param aframe int
     * @return int
     */
    public int getAFrameY(int curAnim, int aframe) {
        Assert(curAnim, aframe);

        int off = (_anims_af_start[curAnim] + aframe) * K_AF_Bytes_Num;
        int frame_index = (_aframes[off++] & 0xFF) +
                          ((_aframes[off++] & 0xFF) << 8);
        int time = (_aframes[off++] & 0xFF) + ((_aframes[off++] & 0xFF) << 8);

        //允许坐标为负数
        int af_x = L9InputStream.readShortLow(_aframes, off);
        off += 2;
        int af_y = L9InputStream.readShortLow(_aframes, off);
        off += 2;

        return af_y;
    }

    /**
     * 返回动画帧的播放次数，参数是动画帧索引
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
     * 获取动画的宽,该动画所有frame的并集宽
     * @return int
     */
    public int getAnimWidth(int curAnim) {
        return _anims_w[curAnim];
    }

    /**
     * 获取动画的高,该动画所有frame的并集高
     * @return int
     */
    public int getAnimHeight(int curAnim) {
        return _anims_h[curAnim];
    }

    /**
     * 返回每秒动画播放的帧数，即fps值
     * @return int
     */
    public int getAnimFPS(int curAnim) {
        return _anims_fps[curAnim];
    }

    /**
     * 返回动画的帧数
     * @return int
     */
    public int getAFrames(int curAnim) {
        return _anims_naf[curAnim];
    }

    /**
     * 在位置posX,posY绘制frame
     * @param g Graphics
     * @param frame int 要绘制的帧索引
     * @param posX int 绘制帧的X坐标
     * @param posY int 绘制帧的Y坐标
     */
    public void paintFrame(Graphics g, int frame, int posX, int posY) {
        //	System.out.println("PaintFrame(g, "+frame+", "+posX+", "+posY+", 0x"+", "+hx+", "+hy+")");
        int nFModules = _frames_nfm[frame];

        for (int fmodule = 0; fmodule < nFModules; fmodule++) {
            int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num;
            int module_id = ((_fmodules[off++]) + (_fmodules[off++] << 8));
            //允许坐标为负数
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

            if ((fm_flag & K_Flag_MD_Frm) != 0) { //表示frame module为帧
                paintFrame(g, module_id, x, y);
            } else {
                paintModule(g, module_id, x, y, fm_flag, fm_pal);
            }
        }
    }

    /**
     * 缓存整个帧,实际上就是缓存该帧中的所有module
     * @param frame int
     */
    public void cacheFrame(int frame) {
        //	System.out.println("PaintFrame(g, "+frame+", "+posX+", "+posY+", 0x"+Integer.toHexString(flags)+", "+hx+", "+hy+")");
        int nFModules = _frames_nfm[frame];

        for (int fmodule = 0; fmodule < nFModules; fmodule++) {
            int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num;
            int module_id = ((_fmodules[off++]) + (_fmodules[off++] << 8));
            //允许坐标为负数
//     int fm_x = (_fmodules[off++]) + (_fmodules[off++] << 8);
//     int fm_y = (_fmodules[off++]) + (_fmodules[off++] << 8);
            int fm_x = L9InputStream.readShortLow(_fmodules, off);
            off += 2;
            int fm_y = L9InputStream.readShortLow(_fmodules, off);
            off += 2;

            int fm_flag = _fmodules[off++];
            int fm_pal = _fmodules[off++];

            if ((fm_flag & K_Flag_MD_Frm) != 0) { //表示frame module为帧
                cacheFrame(module_id);
            } else {
                cacheImages(module_id, module_id, fm_pal);
            }
        }
    }

    /**
     * 释放该帧的缓存，这样可以减少内存
     * @param frame int
     */
    public void freeFrameCache(int frame) {
        int nFModules = _frames_nfm[frame];

        for (int fmodule = 0; fmodule < nFModules; fmodule++) {
            int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num;
            int module_id = ((_fmodules[off++]) + (_fmodules[off++] << 8));
            //允许坐标为负数
//     int fm_x = (_fmodules[off++]) + (_fmodules[off++] << 8);
//     int fm_y = (_fmodules[off++]) + (_fmodules[off++] << 8);
            int fm_x = L9InputStream.readShortLow(_fmodules, off);
            off += 2;
            int fm_y = L9InputStream.readShortLow(_fmodules, off);
            off += 2;

            int fm_flag = _fmodules[off++];
            int fm_pal = _fmodules[off++];

            if ((fm_flag & K_Flag_MD_Frm) != 0) { //表示frame module为帧
                freeFrameCache(module_id);
            } else {
                //BuildCacheImages(module_id, module_id, fm_pal);
                freeCacheImages(module_id, module_id, fm_pal);
            }
        }
    }

    /**
     * 用模块new_md_id替换掉该帧中所有的模块old_md_id,同时微调坐标，让其对齐
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

                //默认居中
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
     * 用模块new_md_id替换掉所有帧中所有的模块old_md_id,同时微调坐标，让其对齐
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
     * 设置frame帧的第fmodule个模块在帧中的X坐标
     * @param frame int
     * @param fmodule int
     * @param fm_x int
     */
    public void setFModuleX(int frame, int fmodule, int fm_x) {
        int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num + 2;
        L9OutputStream.writeShortLow(_fmodules, off, (short) fm_x);
    }

    /**
     * 设置frame帧的第fmodule个模块在帧中的Y坐标
     * @param frame int
     * @param fmodule int
     * @param fm_y int
     */
    public void setFModuleY(int frame, int fmodule, int fm_y) {
        int off = (_frames_fm_start[frame] + fmodule) * K_FM_Bytes_Num + 4;
        L9OutputStream.writeShortLow(_fmodules, off, (short) fm_y);
    }

    /**
     * 设置frame帧的第fmodule个模块在帧中的调色板
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
     * 使用默认调色板绘制module,每一个帧(Frame)画面就是由一个或者多个模块(Module)组成
     * @param g Graphics
     * @param module_id int 要绘制的模块ID
     * @param posX int 要绘制的模块的X坐标
     * @param posY int 要绘制的模块的Y坐标
     * @param flags int 绘制时的翻转、旋转信息 主要有X翻转、Y翻转、顺时针旋转90度及它们的组合
     */
    public void paintModule(Graphics g, int module_id, int posX, int posY,
                            int flags) {
        paintModule(g, module_id, posX, posY, flags, 0);
    }

    private int[] _share_module_id;
    private int[] _own_module_id;
    private L9Sprite _spriteShare;
    /**
     * 在游戏中，为了节省资源，两个sprite对象可以共享某些module数据
     * @param spriteShare L9Sprite
     * @param share_module_id int[],这个参数记录的是共享的sprite的module
     * @param own_module_id int[],这个记录的是自己sprite对象没有存储需要使用共享sprite的module，own_module_id应该与share_module_id相对应
     */
    public void setShareModules(L9Sprite spriteShare, int[] share_module_id,
                                int[] own_module_id) {
        _spriteShare = spriteShare;
        _share_module_id = share_module_id;
        _own_module_id = own_module_id;
    }

    /**
     * 绘制module信息,每一个帧(Frame)画面就是由一个或者多个模块(Module)组成,使用pal指定的调色板来绘制
     * @param g Graphics
     * @param module_id int 要绘制的模块ID
     * @param posX int 要绘制的模块的X坐标
     * @param posY int 要绘制的模块的Y坐标
     * @param flags int 绘制时的翻转、旋转信息 主要有X翻转、Y翻转、顺时针旋转90度及它们的组合
     * @param pal int  使用那套调色板来绘制
     */
    public void paintModule(Graphics g, int module_id, int posX, int posY,
                            int flags, int pal) {
        //如果共享另外的sprite
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
     * 如果图片已经缓存了，则可以清除创建图片的原始数据，这样可以节省内存,注意调用该函数后，则不能再调用FreeCacheImages等类似释放缓存的函数了，因为没有原始数据了，后面将无法再创建图片
     */
    public void freeImageData() {
//        if (_nMaxPal > 1) { //暂时不处理多套调色板
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
                    if (img != null) { //已经缓存了，可以清楚对应的module数据了
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
                    if (img != null) { //已经缓存了，可以清楚对应的module数据了
                        _modules_data[i] = null;
                    }
                }
            }
        }
    }

    /**
     * 对module数据解码为argb数据 以便于绘制
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
                    //索引色要么透明要么不透明,没有中间的渐变
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
            boolean bAlpha = (pByte[offset++] == 1); //第一个字节存储透明标示
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
     * module翻转、旋转标识,主要有 X翻转、Y翻转,顺时针旋转90度,实际的使用可能是单独的一个或者他们的组合
     * 例如：flag=K_Flag_Flip_X|K_Flag_Rotate_90
     * X翻转标识
     */
    public final static byte K_Flag_Flip_X = 0x01;
    /**
     * module翻转、旋转标识,主要有 X翻转、Y翻转,顺时针旋转90度,实际的使用可能是单独的一个或者他们的组合
     * 例如：flag=K_Flag_Flip_X|K_Flag_Rotate_90
     * Y翻转标识
     */
    public final static byte K_Flag_Flip_Y = 0x02;
    /**
     * module翻转、旋转标识,主要有 X翻转、Y翻转,顺时针旋转90度,实际的使用可能是单独的一个或者他们的组合
     * 例如：flag=K_Flag_Flip_X|K_Flag_Rotate_90
     * 顺时针旋转90度标识
     */
    public final static byte K_Flag_Rotate_90 = 0x04;
    /**
     * 帧中的module是否为帧的标识,L9Sprite的帧支持包含帧，这个标识用来区分
     */
    public final static byte K_Flag_MD_Frm = 0x08; //这个表示frame_module中存储的是帧，而不是module

    /**
     * 根据flags标记对图片的rgb数据执行翻转或旋转操作
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
     * 对图像旋转270度
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
     * 对图像旋转180度
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
     * 对图像旋转90度
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
     * 对图像进行X轴翻转
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
     * 对图像进行Y轴翻转
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
