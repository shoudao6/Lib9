package lib9.j2me;

import javax.microedition.lcdui.Graphics;

/**
 * 提供对游戏地图的支持，地图一般分为地图背景和地图上的物品对象，地图上的物品对象支持分层的概念(每个对象都含有Z属性，Z越大越显示在上面)
 * @author not attributable
 * @version 1.0
 */
public class L9Map {

    /**
     * 移动地图的上下左右四个方向常量
     */
    public final static int K_Map_Direction_Up = 0;
    public final static int K_Map_Direction_Down = 1;
    public final static int K_Map_Direction_Left = 2;
    public final static int K_Map_Direction_Right = 3;

    private short _nMap_Modules;
    final static int K_Map_Module_Byte_Num = 10; //index,x,y,z,flag,pal,isP
    private byte[] _map_modules;
    private short _nMap_Frames;
    final static int K_Map_Frame_Byte_Num = 7; //index,x,y,z
    private byte[] _map_frames;
    private short _nMap_Animations;
    final static int K_Map_Animation_Byte_Num = 7; //index,x,y,z
    private byte[] _map_animations;
    public L9Animation[] MapAnimations;
    private short _nMap_Rects;
    final static int K_Map_Rect_Byte_Num = 8; //index,x,y
    private int[][] _map_rects; //left,top,right,bottom

    public int Tile_W;
    public int Tile_H;
    private int nTiles_X; //地图上X方向Tile的个数
    private int nTiles_Y; //地图上Y方向Tile的个数
    private int Tile_X; //屏幕现在处于X方向的Tile
    private int Tile_Y; //屏幕现在处于Y方向的Tile
    private int nTiles_BufferX; //屏幕缓冲X方向Tile数
    private int nTiles_BufferY; //屏幕缓冲Y方向Tile数
    public int screenOffsetX; //屏幕在地图上的坐标偏移X
    public int screenOffsetY; //屏幕在地图上的坐标偏移Y
    private boolean bMoveEnd = false; //表示地图是否移动到末尾了

    private short[][] Map_Tile_Module_ID;
    private byte[][] Map_Tile_IsP;
    private byte[][] Map_Tile_Flag;
    private byte[][] Map_Tile_Pal;

    /**
     * 判断屏幕在地图上是否还处在滚动的状态中
     * @return boolean
     */
    public boolean isInScrollingStatus() {
        return bMoveEnd;
    }

    public L9Map(L9Sprite sprite, int SCR_W, int SCR_H) {
        _sprite = sprite;
        L9InputStream in = new L9InputStream(sprite._mapData, 0,
                                             sprite._mapData.length);
        Tile_W = in.readInt();
        Tile_H = in.readInt();
        nTiles_X = in.readInt();
        nTiles_Y = in.readInt();

        Map_Tile_Module_ID = new short[nTiles_Y][nTiles_X];
        Map_Tile_IsP = new byte[nTiles_Y][nTiles_X];
        Map_Tile_Flag = new byte[nTiles_Y][nTiles_X];
        Map_Tile_Pal = new byte[nTiles_Y][nTiles_X];
        for (int i = 0; i < nTiles_Y; i++) {
            for (int j = 0; j < nTiles_X; j++) {
                Map_Tile_Module_ID[i][j] = in.readShort();
                Map_Tile_IsP[i][j] = in.readByte();
                Map_Tile_Flag[i][j] = in.readByte();
                Map_Tile_Pal[i][j] = in.readByte();
            }
        }

        _nMap_Modules = in.readShort();
        if (_nMap_Modules > 0) {
            _map_modules = in.readBytes(_nMap_Modules *
                                        K_Map_Module_Byte_Num);
        }
        _nMap_Frames = in.readShort();
        if (_nMap_Frames > 0) {
            _map_frames = in.readBytes(_nMap_Frames *
                                       K_Map_Frame_Byte_Num);
        }
        _nMap_Animations = in.readShort();
        if (_nMap_Animations > 0) {
            _map_animations = in.readBytes(_nMap_Animations *
                                           K_Map_Animation_Byte_Num);
        }
        _nMap_Rects = in.readShort();
        if (_nMap_Rects > 0) {
            _map_rects = new int[_nMap_Rects][4];
            for (int i = 0; i < _nMap_Rects; i++) {
                _map_rects[i][0] = in.readShort();
                _map_rects[i][1] = in.readShort();
                _map_rects[i][2] = in.readShort();
                _map_rects[i][3] = in.readShort();
            }

        }

        MapAnimations = new L9Animation[_nMap_Animations];
        for (int i = 0; i < MapAnimations.length; i++) {
            MapAnimations[i] = new L9Animation(_sprite, i);
        }

        _SCR_W = SCR_W;
        _SCR_H = SCR_H;

        initMap();
    }

    private L9Sprite _sprite;
    private int _SCR_W;
    private int _SCR_H;
    /**
     * 返回当前地图的地图Module数
     * @return int
     */
    public int getMapModules() {
        return _nMap_Modules;
    }

    /**
     * 返回当前地图的地图Frame数
     * @return int
     */
    public int getMapFrames() {
        return _nMap_Frames;
    }

    /**
     * 返回当前地图的地图Animation数
     * @return int
     */
    public int getMapAnimations() {
        return _nMap_Animations;
    }

    /**
     * 根据地图module的索引来获取ModuleID
     * @param index int
     * @return int
     */
    public int getModuleID(int index) {
        int off = index * K_Map_Module_Byte_Num;
        return L9InputStream.readShortLow(_map_modules, off);
    }

    /**
     * 返回地图第index个Module的在地图上得X坐标
     * @param index int
     * @return int
     */
    public int getModuleX(int index) {
        int off = index * K_Map_Module_Byte_Num + 2;
        return L9InputStream.readShortLow(_map_modules, off);
    }

    /**
     * 返回地图第index个Module的在地图上得Y坐标
     * @param index int
     * @return int
     */
    public int getModuleY(int index) {
        int off = index * K_Map_Module_Byte_Num + 4;
        return L9InputStream.readShortLow(_map_modules, off);
    }

    /**
     * 返回地图Module的Z次序
     * @param index int
     * @return int
     */
    public int getModuleZ(int index) {
        int off = index * K_Map_Module_Byte_Num + 6;
        return _map_modules[off] & 0xFF;
    }

    /**
     * 返回地图Module的翻转标记
     * @param index int
     * @return int
     */
    public int getModuleFlag(int index) {
        int off = index * K_Map_Module_Byte_Num + 7;
        return _map_modules[off] & 0xFF;
    }

    /**
     * 返回地图Module的调色板
     * @param index int
     * @return int
     */
    public int getModulePal(int index) {
        int off = index * K_Map_Module_Byte_Num + 8;
        return _map_modules[off] & 0xFF;
    }

    /**
     * 返回地图module的属性,0表示可以通过,1表示不能通过
     * @param index int
     * @return int
     */
    public int getModuleProperty(int index) {
        int off = index * K_Map_Module_Byte_Num + 9;
        return _map_modules[off] & 0xFF;
    }

    /**
     * 根据索引返回地图FrameID
     * @param index int
     * @return int
     */
    public int getFrameID(int index) {
        int off = index * K_Map_Frame_Byte_Num;
        return L9InputStream.readShortLow(_map_frames, off);
    }

    /**
     * 返回地图Frame的X坐标
     * @param index int
     * @return int
     */
    public int getFrameX(int index) {
        int off = index * K_Map_Frame_Byte_Num + 2;
        return L9InputStream.readShortLow(_map_frames, off);
    }

    /**
     * 返回地图Frame的Y坐标
     * @param index int
     * @return int
     */
    public int getFrameY(int index) {
        int off = index * K_Map_Frame_Byte_Num + 4;
        return L9InputStream.readShortLow(_map_frames, off);
    }

    /**
     * 返回地图Frame的Z次序
     * @param index int
     * @return int
     */
    public int getFrameZ(int index) {
        int off = index * K_Map_Frame_Byte_Num + 6;
        return _map_frames[off] & 0xFF;
    }

    /**
     * 根据索引返回地图AnimationID
     * @param index int
     * @return int
     */
    public int getAnimationID(int index) {
        int off = index * K_Map_Animation_Byte_Num;
        return L9InputStream.readShortLow(_map_animations, off);
    }

    /**
     * 返回地图Animation的X坐标
     * @param index int
     * @return int
     */
    public int getAnimationX(int index) {
        int off = index * K_Map_Animation_Byte_Num + 2;
        return L9InputStream.readShortLow(_map_animations, off);
    }

    /**
     * 返回地图Animation的Y坐标
     * @param index int
     * @return int
     */
    public int getAnimationY(int index) {
        int off = index * K_Map_Animation_Byte_Num + 4;
        return L9InputStream.readShortLow(_map_animations, off);
    }

    /**
     * 返回地图Animation的Z次序
     * @param index int
     * @return int
     */
    public int getAnimationZ(int index) {
        int off = index * K_Map_Animation_Byte_Num + 6;
        return _map_animations[off] & 0xFF;
    }

    /**
     * 返回地图module在地图上的矩形
     * @param index int
     * @return L9Rect
     */
    public L9Rect getModuleRect(int index) {
        int left = getModuleX(index);
        int top = getModuleY(index);
        int w = getModuleWidth(index);
        int h = getModuleHeight(index);
        return new L9Rect(left, top, left + w, top + h);
    }

    /**
     * 返回地图frame在地图上的矩形
     * @param index int
     * @return L9Rect
     */
    public L9Rect getFrameRect(int index) {
        int left = getFrameX(index);
        int top = getFrameY(index);
        int w = getFrameWidth(index);
        int h = getFrameHeight(index);
        return new L9Rect(left, top, left + w, top + h);
    }

    /**
     * 返回地图Animation在地图上的矩形
     * @param index int
     * @return L9Rect
     */
    public L9Rect getAnimationRect(int index) {
        int left = getAnimationX(index);
        int top = getAnimationY(index);
        int w = getAnimationWidth(index);
        int h = getAnimationHeight(index);
        return new L9Rect(left, top, left + w, top + h);
    }

    /**
     * 返回地图的宽
     * @return int
     */
    public int getMapWidth() {
        return nTiles_X * Tile_W;
    }

    /**
     * 返回地图的高
     * @return int
     */
    public int getMapHeight() {
        return nTiles_Y * Tile_H;
    }

    final static int K_Map_Obejct_Type_Module = 0;
    final static int K_Map_Obejct_Type_Frame = 1;
    final static int K_Map_Obejct_Type_Animation = 2;

    /**
     * 判断module,frame,animation对象是否在屏幕内
     * @param type int
     * @param index int
     * @return boolean
     */
    public boolean isInScreen(int spriteType, int index) {
        L9Rect rect1 = new L9Rect();
        L9Rect rect2 = new L9Rect(screenOffsetX, screenOffsetY,
                                  screenOffsetX + _SCR_W,
                                  screenOffsetY + _SCR_H);
        switch (spriteType) {
        case K_Map_Obejct_Type_Module:
            rect1 = getModuleRect(index);
            break;
        case K_Map_Obejct_Type_Frame:
            rect1 = getFrameRect(index);
            break;
        case K_Map_Obejct_Type_Animation:
            rect1 = getAnimationRect(index);
            break;
        }
        return rect1.isInterRect(rect2);
    }

    /**
     * 返回地图中物品对象的最大层次数，为了效果的逼真，地图上往往会放置好几层物品
     * @return int
     */
    public int getMaxZ() {
        int maxZ = 0;
        for (int i = 0; i < _nMap_Modules; i++) {
            if (maxZ < getModuleZ(i)) {
                maxZ = getModuleZ(i);
            }
        }
        for (int i = 0; i < _nMap_Frames; i++) {
            if (maxZ < getFrameZ(i)) {
                maxZ = getFrameZ(i);
            }
        }
        for (int i = 0; i < _nMap_Animations; i++) {
            if (maxZ < getAnimationZ(i)) {
                maxZ = getAnimationZ(i);
            }
        }
        return maxZ;
    }

    private void PaintMapModule(Graphics g, int map_module_index) {
        int module_index = getModuleID(map_module_index);
        int XX = getModuleX(map_module_index) - screenOffsetX;
        int YY = getModuleY(map_module_index) - screenOffsetY;
        int flag = getModuleFlag(map_module_index);
        int pal = getModulePal(map_module_index);
        _sprite.paintModule(g, module_index, XX, YY, flag, pal);
    }

    private void PaintMapFrame(Graphics g, int map_frame_index) {
        int frame_index = getFrameID(map_frame_index);
        int XX = getFrameX(map_frame_index) - screenOffsetX;
        int YY = getFrameY(map_frame_index) - screenOffsetY;
        _sprite.paintFrame(g, frame_index, XX, YY);
    }

    private void UpdateMapAnimation(Graphics g, int map_anim_index) {
        int XX = getAnimationX(map_anim_index) - screenOffsetX;
        int YY = getAnimationY(map_anim_index) - screenOffsetY;
        MapAnimations[map_anim_index].setAnim(XX, YY, true);
        MapAnimations[map_anim_index].updateAnimation(g);
    }

    private int[] mapModuleFilterIndex;
    private int[] mapFrameFilterIndex;
    private int[] mapAnimationFilterIndex;
    /**
     * 在绘制地图的时候，有时希望有些对象不显示，这个函数的作用就是过滤掉哪些不显示的module对象
     * 注意参数filterIndex是对象在地图中的索引，不是对象本身的id
     * @param filterIndex int[]
     */
    public void setMapModuleFilter(int[] filterIndex) {
        mapModuleFilterIndex = filterIndex;
    }

    /**
     * 返回地图上被过滤(也就是不会显示)的module对象在地图上的索引数组
     * @return int[]
     */
    public int[] getMapModuleFilter() {
        return mapModuleFilterIndex;
    }

    /**
     * 在绘制地图的时候，有时希望有些对象不显示，这个函数的作用就是过滤掉哪些不显示的frame对象
     * 注意参数filterIndex是对象在地图中的索引，不是对象本身的id
     * @param filterIndex int[]
     */
    public void setMapFrameFilter(int[] filterIndex) {
        mapFrameFilterIndex = filterIndex;
    }

    /**
     * 返回地图上被过滤(也就是不会显示)的frame对象在地图上的索引数组
     * @return int[]
     */
    public int[] getMapFrameFilter() {
        return mapFrameFilterIndex;
    }

    /**
     * 在绘制地图的时候，有时希望有些对象不显示，这个函数的作用就是过滤掉哪些不显示的animation对象
     * 注意参数filterIndex是对象在地图中的索引，不是对象本身的id
     * @param filterIndex int[]
     */
    public void setMapAnimationFilter(int[] filterIndex) {
        mapAnimationFilterIndex = filterIndex;
    }

    /**
     * 返回地图上被过滤(也就是不会显示)的animation对象在地图上的索引数组
     * @return int[]
     */
    public int[] getMapAnimationFilter() {
        return mapAnimationFilterIndex;
    }

    /**
     * 绘制地图上的对象，当且仅当对象在屏幕内才且索引没有被过滤才绘制，绘制的时候是根据对象的Z顺序来进行绘制，Z越大越在上面绘制
     * @param g Graphics
     */
    public void updateMapObjects(Graphics g) {
        int Z = getMaxZ();
        for (int jk = 0; jk <= Z; jk++) { //Z越大越显示在上面
            for (int i = 0; i < _nMap_Modules; i++) {
                if ( -1 == L9Util.getIndexByID(mapModuleFilterIndex, i)) {
                    if (isInScreen(K_Map_Obejct_Type_Module, i) &&
                        getModuleZ(i) == jk) {
                        PaintMapModule(g, i);
                    }
                }
            }
            for (int i = 0; i < _nMap_Frames; i++) { //帧
                if ( -1 == L9Util.getIndexByID(mapFrameFilterIndex, i)) {
                    if (isInScreen(K_Map_Obejct_Type_Frame, i) &&
                        getFrameZ(i) == jk) {
                        PaintMapFrame(g, i);
                    }
                }
            }
            for (int i = 0; i < _nMap_Animations; i++) { //动画
                if ( -1 == L9Util.getIndexByID(mapAnimationFilterIndex, i)) {
                    if (isInScreen(K_Map_Obejct_Type_Animation, i) &&
                        getAnimationZ(i) == jk) {
                        UpdateMapAnimation(g, i);
                    }
                }
            }
        }
    }

    /**
     * 返回地图上module的宽
     * @param index int
     * @return int
     */
    public int getModuleWidth(int index) {
        int id = getModuleID(index);
        return _sprite.getModuleWidth(id);
    }

    /**
     * 返回地图上module的高
     * @param index int
     * @return int
     */
    public int getModuleHeight(int index) {
        int id = getModuleID(index);
        return _sprite.getModuleHeight(id);
    }

    /**
     * 返回地图上module的Alpha
     * @param index int
     * @return boolean
     */
    public boolean getModuleAlpha(int index) {
        int id = getModuleID(index);
        return _sprite.getModuleAlpha(id);
    }

    /**
     * 返回地图上frame的宽
     * @param index int
     * @return int
     */
    public int getFrameWidth(int index) {
        return _sprite.getFrameWidth(getFrameID(index));
    }

    /**
     * 返回地图上frame的高
     * @param frame int
     * @return int
     */
    public int getFrameHeight(int index) {
        return _sprite.getFrameHeight(getFrameID(index));
    }

    /**
     * 返回地图上animation的宽，动画的宽为动画所有帧的宽的并集
     * @param index int
     * @return int
     */
    public int getAnimationWidth(int index) {
        return _sprite.Animations[getAnimationID(index)].getAnimWidth();
    }

    /**
     * 获取地图上动画的高
     * @param anim int
     * @return int
     */
    public int getAnimationHeight(int index) {
        return _sprite.Animations[getAnimationID(index)].getAnimHeight();
    }

    /**
     * 返回地图上某个动画的fps
     * @param anim int
     * @return int
     */
    public int getAnimationFPS(int index) {
        return _sprite.Animations[getAnimationID(index)].getAnimFPS();
    }

    /**
     * 缓存地图背景，在游戏中地图一般由tile构成，为了提供速度地图常常要求缓存
     */
    public void cacheMapBg() {
        for (int i = 0; i < Map_Tile_Module_ID.length; i++) {
            for (int j = 0; j < Map_Tile_Module_ID[i].length; j++) {
                if (!_sprite.isCacheImage(Map_Tile_Module_ID[i][j], 0)) {
                    _sprite.cacheImages(Map_Tile_Module_ID[i][j],
                                        Map_Tile_Module_ID[i][j],
                                        Map_Tile_Pal[i][j]);
                }
            }
        }
    }

    /**
     * 判断该地图坐标x,y(x,y为地图上的任意一个点)所对应的tile是否为物理层,如果为物理层主角一般设置为不能通过
     * @param x int
     * @param y int
     * @return boolean
     */
    public boolean IsMapTilePhysical(int x, int y) {
        Calc_Tile_XY(x, y);
        return Map_Tile_IsP[_Tmp_Map_Tile_Y][_Tmp_Map_Tile_X] == 1;
    }

    /**
     * 参数为手机屏幕的高和宽，手机屏幕外的物品和地图则不用绘制
     * @param scrW int
     * @param scrH int
     */
    public void initMap() {
        screenOffsetX = 0;
        screenOffsetY = 0;

        Tile_X = 0;
        Tile_Y = 0;

        nTiles_BufferX = _SCR_W / Tile_W + 1;
        nTiles_BufferY = _SCR_H / Tile_H + 1;

        cacheMapBg(); //缓存地图背景tile
    }


    /**
     * 计算屏幕在地图上Tile
     */
    private void Calc_Tile_XY() {
        Tile_X = screenOffsetX / Tile_W;
        if (Tile_X < 0) {
            Tile_X = 0;
        }

        Tile_Y = screenOffsetY / Tile_H;
        if (Tile_Y < 0) {
            Tile_Y = 0;
        }
    }

    private int _Tmp_Map_Tile_X;
    private int _Tmp_Map_Tile_Y;
    //减一的目的是为了边界问题
    private void Calc_Tile_XY(int x, int y) {
        _Tmp_Map_Tile_X = (x - 1) / Tile_W;
        if (_Tmp_Map_Tile_X < 0) {
            _Tmp_Map_Tile_X = 0;
        }

        _Tmp_Map_Tile_Y = (y - 1) / Tile_H;
        if (_Tmp_Map_Tile_Y < 0) {
            _Tmp_Map_Tile_Y = 0;
        }
    }

    /**
     * 允许在整个地图范围内移动，如果要限制在地图的一部分范围内移动则使用MoveLimitMap函数
     * @param Direction int
     * @param Step int
     */
    public void moveMap(int Direction, int Step) {
        moveLimitMap(Direction, Step, getMapWidth(), getMapHeight());
    }

    /**
     * 移动地图，根据游戏需要可以限制在地图的一部分范围内移动，方向为上下左右4个方向(对应常量K_Map_Direction_Up=0,K_Map_Direction_Down=1,K_Map_Direction_Left=2,K_Map_Direction_Right=3)
     * @param Direction int
     * @param Step int
     * @param map_W int
     * @param map_H int
     */
    public void moveLimitMap(int Direction, int Step, int map_W, int map_H) {
        switch (Direction) {
        case K_Map_Direction_Up:
            screenOffsetY -= Step;
            if (screenOffsetY < 0) {
                screenOffsetY = 0;
                bMoveEnd = true;
            }
            Calc_Tile_XY();
            break;
        case K_Map_Direction_Down:
            screenOffsetY += Step;
            if (screenOffsetY > map_H - _SCR_H) {
                screenOffsetY = map_H - _SCR_H;
                bMoveEnd = true;
            }
            Calc_Tile_XY();
            break;
        case K_Map_Direction_Left:
            screenOffsetX -= Step;
            if (screenOffsetX < 0) {
                screenOffsetX = 0;
                bMoveEnd = true;
            }
            Calc_Tile_XY();
            break;
        case K_Map_Direction_Right:
            screenOffsetX += Step;
            if (screenOffsetX > map_W - _SCR_W) {
                screenOffsetX = map_W - _SCR_W;
                bMoveEnd = true;
            }
            Calc_Tile_XY();
            break;
        }
    }

    /**
     * 更新绘制地图,只绘制在屏幕内的地图
     * @param g Graphics
     */
    public void updateMap(Graphics g) { //绘制整个屏幕缓冲
        for (int i = Tile_Y; i < Tile_Y + nTiles_BufferY && i < nTiles_Y; i++) {
            for (int j = Tile_X; j < Tile_X + nTiles_BufferX && j < nTiles_X; j++) {
                int XX = j * Tile_W - screenOffsetX;
                int YY = i * Tile_H - screenOffsetY;
                //int index = i * nTiles_BufferX + j; //存在问题
                _sprite.paintModule(g, Map_Tile_Module_ID[i][j], XX, YY,
                                    Map_Tile_Flag[i][j], Map_Tile_Pal[i][j]);
            }
        }
//        bSystemMapInfo = false;
    }
}
