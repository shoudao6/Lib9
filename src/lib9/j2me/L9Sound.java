package lib9.j2me;

import javax.microedition.media.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Lib9�����ṩ�Ķ�������֧�֣������˿������̵߳ķ�ʽ������������Ϊ����Ϸ�ڲ���������ʱ�����ƽ��
 * @author not attributable
 * @version 1.0
 */
public class L9Sound implements Runnable {
    /**
     * L9Soundʹ��L9Sprite�ṩ������������Դ������������nChannelΪ����Ƶ��,��������Ϊ2��һ�����������֣�һ�������ŵ�����
     * @param sprite L9Sprite
     * @param nChannel int
     */
    public L9Sound(L9Sprite sprite, int nChannel) {
        _sound_data = sprite._sound_data;
        _players = new Player[nChannel];
        new Thread(this).start();
    }

    /**
     *  L9Soundʹ��L9Sprite�ṩ������������Դ��������Ĭ�ϲ���Ƶ����Ϊ2��һ�����������֣�һ�������ŵ�����
     * @param sprite L9Sprite
     */
    public L9Sound(L9Sprite sprite) {
        this(sprite, 2);
    }

    private Player[] _players;
    private byte[][] _sound_data;
    private boolean bRun = true;

    final static int K_Sound_Cmd_Play = 0;
    final static int K_Sound_Cmd_Stop = 1;
    final static int K_Sound_Cmd_StopAll = 2;
    private int _cmd = -1;
    private int _channel;
    private int _sound_id;
    private int _loop;

    private void _playSound(int channel, int id, int loop) {
        try {
            if (_players[channel] == null) {
                _players[channel] = createPlayer(_sound_data[id]);
            } else {
                _players[channel].prefetch();
            }
            _players[channel].setLoopCount(loop);
            _players[channel].start();
            Thread.sleep(50);
        } catch (Exception ex) {
        }
    }

    private void _stopSound(int channel) {
        try {
            if (_players[channel] != null) {
                if (_players[channel].getState() == Player.STARTED) {
                    _players[channel].stop();
                }
                if (_players[channel].getState() == Player.PREFETCHED) {
                    _players[channel].deallocate();
                }
                _players[channel].close();
                _players[channel] = null;
                Thread.sleep(50);
            }
        } catch (Exception e) {
            _players[channel] = null;
        }
    }

    public void run() {
        while (bRun) {
            switch (_cmd) {
            case K_Sound_Cmd_Play:
                _playSound(_channel, _sound_id, _loop);
                break;
            case K_Sound_Cmd_Stop:
                _stopSound(_channel);
                break
                        ;
            case K_Sound_Cmd_StopAll:
                for (int i = 0; i < _players.length; i++) {
                    _stopSound(i);
                }
                break;
            }
            _cmd = -1;
        }
    }

    /**
     * ʹ��soundData������ǿ�ƴ���Player
     * @param soundData byte[]
     * @return Player
     */
    private Player createPlayer(byte[] soundData) {
        String type = "";
        if (soundData[0] == 'M' && soundData[1] == 'T'
            && soundData[2] == 'h' && soundData[3] == 'd') {
            type = "audio/midi";
        } else if (soundData[0] == 'R' && soundData[1] == 'I'
                   && soundData[2] == 'F' && soundData[3] == 'F') {
            type = "audio/x-wav";
        } else if (soundData[0] == '#' && soundData[1] == '!' &&
                   soundData[2] == 'A'
                   && soundData[3] == 'M' && soundData[4] == 'R') {
            type = "audio/amr";
        }

        Player player = null;
        try {
            player = Manager.createPlayer(new ByteArrayInputStream(soundData),
                                          type);
        } catch (Exception ex) {

        }
        return player;
    }

    /**
     * ����ĳ��Ƶ���ϵ�����,ÿ��Ƶ��ͬʱֻ�ܲ���һ������
     * @param channel int
     * @param id int
     * @param loop int
     */
    public void playSound(int channel, int id, int loop) {
        if (id < 0) {
            return;
        }
        _cmd = K_Sound_Cmd_Play;
        _channel = channel;
        _sound_id = id;
        _loop = loop;
    }

    /**
     * �ж��Ƿ��ڲ���
     * @param channel int
     * @return boolean
     */
    public boolean isPlaying(int channel) {
        if (_players[channel] != null) {
            if (_players[channel].getState() == Player.STARTED) {
                return true;
            }
        }
        return false;
    }

    /**
     *ֹͣĳ��Ƶ���ϵ�����
     * @param channel int
     */
    public void stopSound(int channel) {
        _cmd = K_Sound_Cmd_Stop;
        _channel = channel;
    }

    /**
     * ֹͣȫ������
     */
    public void stopAllSound() {
        _cmd = K_Sound_Cmd_StopAll;
    }

    /**
     * L9Sound�������Ų����˶������߳�ģʽ������������endSoundThread()�������������������������̵߳ģ������ڳ����˳�֮ǰ����
     */
    public void endSoundThread() {
        bRun = false;
    }

}
