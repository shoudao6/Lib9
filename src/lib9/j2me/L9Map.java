package lib9.j2me;

import javax.microedition.lcdui.Graphics;

/**
 * �ṩ����Ϸ��ͼ��֧�֣���ͼһ���Ϊ��ͼ�����͵�ͼ�ϵ���Ʒ���󣬵�ͼ�ϵ���Ʒ����֧�ֲַ�ĸ���(ÿ�����󶼺���Z���ԣ�ZԽ��Խ��ʾ������)
 * @author not attributable
 * @version 1.0
 */
public class L9Map {

    /**
     * �ƶ���ͼ�����������ĸ�������
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
    private int nTiles_X; //��ͼ��X����Tile�ĸ���
    private int nTiles_Y; //��ͼ��Y����Tile�ĸ���
    private int Tile_X; //��Ļ���ڴ���X�����Tile
    private int Tile_Y; //��Ļ���ڴ���Y�����Tile
    private int nTiles_BufferX; //��Ļ����X����Tile��
    private int nTiles_BufferY; //��Ļ����Y����Tile��
    public int screenOffsetX; //��Ļ�ڵ�ͼ�ϵ�����ƫ��X
    public int screenOffsetY; //��Ļ�ڵ�ͼ�ϵ�����ƫ��Y
    private boolean bMoveEnd = false; //��ʾ��ͼ�Ƿ��ƶ���ĩβ��

    private short[][] Map_Tile_Module_ID;
    private byte[][] Map_Tile_IsP;
    private byte[][] Map_Tile_Flag;
    private byte[][] Map_Tile_Pal;

    /**
     * �ж���Ļ�ڵ�ͼ���Ƿ񻹴��ڹ�����״̬��
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
     * ���ص�ǰ��ͼ�ĵ�ͼModule��
     * @return int
     */
    public int getMapModules() {
        return _nMap_Modules;
    }

    /**
     * ���ص�ǰ��ͼ�ĵ�ͼFrame��
     * @return int
     */
    public int getMapFrames() {
        return _nMap_Frames;
    }

    /**
     * ���ص�ǰ��ͼ�ĵ�ͼAnimation��
     * @return int
     */
    public int getMapAnimations() {
        return _nMap_Animations;
    }

    /**
     * ���ݵ�ͼmodule����������ȡModuleID
     * @param index int
     * @return int
     */
    public int getModuleID(int index) {
        int off = index * K_Map_Module_Byte_Num;
        return L9InputStream.readShortLow(_map_modules, off);
    }

    /**
     * ���ص�ͼ��index��Module���ڵ�ͼ�ϵ�X����
     * @param index int
     * @return int
     */
    public int getModuleX(int index) {
        int off = index * K_Map_Module_Byte_Num + 2;
        return L9InputStream.readShortLow(_map_modules, off);
    }

    /**
     * ���ص�ͼ��index��Module���ڵ�ͼ�ϵ�Y����
     * @param index int
     * @return int
     */
    public int getModuleY(int index) {
        int off = index * K_Map_Module_Byte_Num + 4;
        return L9InputStream.readShortLow(_map_modules, off);
    }

    /**
     * ���ص�ͼModule��Z����
     * @param index int
     * @return int
     */
    public int getModuleZ(int index) {
        int off = index * K_Map_Module_Byte_Num + 6;
        return _map_modules[off] & 0xFF;
    }

    /**
     * ���ص�ͼModule�ķ�ת���
     * @param index int
     * @return int
     */
    public int getModuleFlag(int index) {
        int off = index * K_Map_Module_Byte_Num + 7;
        return _map_modules[off] & 0xFF;
    }

    /**
     * ���ص�ͼModule�ĵ�ɫ��
     * @param index int
     * @return int
     */
    public int getModulePal(int index) {
        int off = index * K_Map_Module_Byte_Num + 8;
        return _map_modules[off] & 0xFF;
    }

    /**
     * ���ص�ͼmodule������,0��ʾ����ͨ��,1��ʾ����ͨ��
     * @param index int
     * @return int
     */
    public int getModuleProperty(int index) {
        int off = index * K_Map_Module_Byte_Num + 9;
        return _map_modules[off] & 0xFF;
    }

    /**
     * �����������ص�ͼFrameID
     * @param index int
     * @return int
     */
    public int getFrameID(int index) {
        int off = index * K_Map_Frame_Byte_Num;
        return L9InputStream.readShortLow(_map_frames, off);
    }

    /**
     * ���ص�ͼFrame��X����
     * @param index int
     * @return int
     */
    public int getFrameX(int index) {
        int off = index * K_Map_Frame_Byte_Num + 2;
        return L9InputStream.readShortLow(_map_frames, off);
    }

    /**
     * ���ص�ͼFrame��Y����
     * @param index int
     * @return int
     */
    public int getFrameY(int index) {
        int off = index * K_Map_Frame_Byte_Num + 4;
        return L9InputStream.readShortLow(_map_frames, off);
    }

    /**
     * ���ص�ͼFrame��Z����
     * @param index int
     * @return int
     */
    public int getFrameZ(int index) {
        int off = index * K_Map_Frame_Byte_Num + 6;
        return _map_frames[off] & 0xFF;
    }

    /**
     * �����������ص�ͼAnimationID
     * @param index int
     * @return int
     */
    public int getAnimationID(int index) {
        int off = index * K_Map_Animation_Byte_Num;
        return L9InputStream.readShortLow(_map_animations, off);
    }

    /**
     * ���ص�ͼAnimation��X����
     * @param index int
     * @return int
     */
    public int getAnimationX(int index) {
        int off = index * K_Map_Animation_Byte_Num + 2;
        return L9InputStream.readShortLow(_map_animations, off);
    }

    /**
     * ���ص�ͼAnimation��Y����
     * @param index int
     * @return int
     */
    public int getAnimationY(int index) {
        int off = index * K_Map_Animation_Byte_Num + 4;
        return L9InputStream.readShortLow(_map_animations, off);
    }

    /**
     * ���ص�ͼAnimation��Z����
     * @param index int
     * @return int
     */
    public int getAnimationZ(int index) {
        int off = index * K_Map_Animation_Byte_Num + 6;
        return _map_animations[off] & 0xFF;
    }

    /**
     * ���ص�ͼmodule�ڵ�ͼ�ϵľ���
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
     * ���ص�ͼframe�ڵ�ͼ�ϵľ���
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
     * ���ص�ͼAnimation�ڵ�ͼ�ϵľ���
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
     * ���ص�ͼ�Ŀ�
     * @return int
     */
    public int getMapWidth() {
        return nTiles_X * Tile_W;
    }

    /**
     * ���ص�ͼ�ĸ�
     * @return int
     */
    public int getMapHeight() {
        return nTiles_Y * Tile_H;
    }

    final static int K_Map_Obejct_Type_Module = 0;
    final static int K_Map_Obejct_Type_Frame = 1;
    final static int K_Map_Obejct_Type_Animation = 2;

    /**
     * �ж�module,frame,animation�����Ƿ�����Ļ��
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
     * ���ص�ͼ����Ʒ��������������Ϊ��Ч���ı��棬��ͼ����������úü�����Ʒ
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
     * �ڻ��Ƶ�ͼ��ʱ����ʱϣ����Щ������ʾ��������������þ��ǹ��˵���Щ����ʾ��module����
     * ע�����filterIndex�Ƕ����ڵ�ͼ�е����������Ƕ������id
     * @param filterIndex int[]
     */
    public void setMapModuleFilter(int[] filterIndex) {
        mapModuleFilterIndex = filterIndex;
    }

    /**
     * ���ص�ͼ�ϱ�����(Ҳ���ǲ�����ʾ)��module�����ڵ�ͼ�ϵ���������
     * @return int[]
     */
    public int[] getMapModuleFilter() {
        return mapModuleFilterIndex;
    }

    /**
     * �ڻ��Ƶ�ͼ��ʱ����ʱϣ����Щ������ʾ��������������þ��ǹ��˵���Щ����ʾ��frame����
     * ע�����filterIndex�Ƕ����ڵ�ͼ�е����������Ƕ������id
     * @param filterIndex int[]
     */
    public void setMapFrameFilter(int[] filterIndex) {
        mapFrameFilterIndex = filterIndex;
    }

    /**
     * ���ص�ͼ�ϱ�����(Ҳ���ǲ�����ʾ)��frame�����ڵ�ͼ�ϵ���������
     * @return int[]
     */
    public int[] getMapFrameFilter() {
        return mapFrameFilterIndex;
    }

    /**
     * �ڻ��Ƶ�ͼ��ʱ����ʱϣ����Щ������ʾ��������������þ��ǹ��˵���Щ����ʾ��animation����
     * ע�����filterIndex�Ƕ����ڵ�ͼ�е����������Ƕ������id
     * @param filterIndex int[]
     */
    public void setMapAnimationFilter(int[] filterIndex) {
        mapAnimationFilterIndex = filterIndex;
    }

    /**
     * ���ص�ͼ�ϱ�����(Ҳ���ǲ�����ʾ)��animation�����ڵ�ͼ�ϵ���������
     * @return int[]
     */
    public int[] getMapAnimationFilter() {
        return mapAnimationFilterIndex;
    }

    /**
     * ���Ƶ�ͼ�ϵĶ��󣬵��ҽ�����������Ļ�ڲ�������û�б����˲Ż��ƣ����Ƶ�ʱ���Ǹ��ݶ����Z˳�������л��ƣ�ZԽ��Խ���������
     * @param g Graphics
     */
    public void updateMapObjects(Graphics g) {
        int Z = getMaxZ();
        for (int jk = 0; jk <= Z; jk++) { //ZԽ��Խ��ʾ������
            for (int i = 0; i < _nMap_Modules; i++) {
                if ( -1 == L9Util.getIndexByID(mapModuleFilterIndex, i)) {
                    if (isInScreen(K_Map_Obejct_Type_Module, i) &&
                        getModuleZ(i) == jk) {
                        PaintMapModule(g, i);
                    }
                }
            }
            for (int i = 0; i < _nMap_Frames; i++) { //֡
                if ( -1 == L9Util.getIndexByID(mapFrameFilterIndex, i)) {
                    if (isInScreen(K_Map_Obejct_Type_Frame, i) &&
                        getFrameZ(i) == jk) {
                        PaintMapFrame(g, i);
                    }
                }
            }
            for (int i = 0; i < _nMap_Animations; i++) { //����
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
     * ���ص�ͼ��module�Ŀ�
     * @param index int
     * @return int
     */
    public int getModuleWidth(int index) {
        int id = getModuleID(index);
        return _sprite.getModuleWidth(id);
    }

    /**
     * ���ص�ͼ��module�ĸ�
     * @param index int
     * @return int
     */
    public int getModuleHeight(int index) {
        int id = getModuleID(index);
        return _sprite.getModuleHeight(id);
    }

    /**
     * ���ص�ͼ��module��Alpha
     * @param index int
     * @return boolean
     */
    public boolean getModuleAlpha(int index) {
        int id = getModuleID(index);
        return _sprite.getModuleAlpha(id);
    }

    /**
     * ���ص�ͼ��frame�Ŀ�
     * @param index int
     * @return int
     */
    public int getFrameWidth(int index) {
        return _sprite.getFrameWidth(getFrameID(index));
    }

    /**
     * ���ص�ͼ��frame�ĸ�
     * @param frame int
     * @return int
     */
    public int getFrameHeight(int index) {
        return _sprite.getFrameHeight(getFrameID(index));
    }

    /**
     * ���ص�ͼ��animation�Ŀ������Ŀ�Ϊ��������֡�Ŀ�Ĳ���
     * @param index int
     * @return int
     */
    public int getAnimationWidth(int index) {
        return _sprite.Animations[getAnimationID(index)].getAnimWidth();
    }

    /**
     * ��ȡ��ͼ�϶����ĸ�
     * @param anim int
     * @return int
     */
    public int getAnimationHeight(int index) {
        return _sprite.Animations[getAnimationID(index)].getAnimHeight();
    }

    /**
     * ���ص�ͼ��ĳ��������fps
     * @param anim int
     * @return int
     */
    public int getAnimationFPS(int index) {
        return _sprite.Animations[getAnimationID(index)].getAnimFPS();
    }

    /**
     * �����ͼ����������Ϸ�е�ͼһ����tile���ɣ�Ϊ���ṩ�ٶȵ�ͼ����Ҫ�󻺴�
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
     * �жϸõ�ͼ����x,y(x,yΪ��ͼ�ϵ�����һ����)����Ӧ��tile�Ƿ�Ϊ�����,���Ϊ���������һ������Ϊ����ͨ��
     * @param x int
     * @param y int
     * @return boolean
     */
    public boolean IsMapTilePhysical(int x, int y) {
        Calc_Tile_XY(x, y);
        return Map_Tile_IsP[_Tmp_Map_Tile_Y][_Tmp_Map_Tile_X] == 1;
    }

    /**
     * ����Ϊ�ֻ���Ļ�ĸߺͿ��ֻ���Ļ�����Ʒ�͵�ͼ���û���
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

        cacheMapBg(); //�����ͼ����tile
    }


    /**
     * ������Ļ�ڵ�ͼ��Tile
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
    //��һ��Ŀ����Ϊ�˱߽�����
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
     * ������������ͼ��Χ���ƶ������Ҫ�����ڵ�ͼ��һ���ַ�Χ���ƶ���ʹ��MoveLimitMap����
     * @param Direction int
     * @param Step int
     */
    public void moveMap(int Direction, int Step) {
        moveLimitMap(Direction, Step, getMapWidth(), getMapHeight());
    }

    /**
     * �ƶ���ͼ��������Ϸ��Ҫ���������ڵ�ͼ��һ���ַ�Χ���ƶ�������Ϊ��������4������(��Ӧ����K_Map_Direction_Up=0,K_Map_Direction_Down=1,K_Map_Direction_Left=2,K_Map_Direction_Right=3)
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
     * ���»��Ƶ�ͼ,ֻ��������Ļ�ڵĵ�ͼ
     * @param g Graphics
     */
    public void updateMap(Graphics g) { //����������Ļ����
        for (int i = Tile_Y; i < Tile_Y + nTiles_BufferY && i < nTiles_Y; i++) {
            for (int j = Tile_X; j < Tile_X + nTiles_BufferX && j < nTiles_X; j++) {
                int XX = j * Tile_W - screenOffsetX;
                int YY = i * Tile_H - screenOffsetY;
                //int index = i * nTiles_BufferX + j; //��������
                _sprite.paintModule(g, Map_Tile_Module_ID[i][j], XX, YY,
                                    Map_Tile_Flag[i][j], Map_Tile_Pal[i][j]);
            }
        }
//        bSystemMapInfo = false;
    }
}
