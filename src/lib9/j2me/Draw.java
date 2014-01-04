package lib9.j2me;
import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


public class Draw {
	
	/**
	 * ����ͼƬ
	 */
	public static Image loadImage(String path){
		Image img=null;
		try {
			img=Image.createImage(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return img;
	}
	
	/**
	 * ������ͼƬ
	 * 
	 * @param imagea_number
	 * @param x
	 * @param y
	 * @param offer
	 * @param canvas
	 */
	public static void DrawNumber(Image[] imagea_number, int key,
			int x, int y, int offer, Graphics g) {
		if(key<0)return;
		String strkey = String.valueOf(key);
		byte strlenght = (byte) strkey.length();//
		byte[] bytea_str = new byte[strlenght];// �õ�����int�͵ĳ���
		for (int a = 0; a < strlenght; a++) {
			bytea_str[a] = (byte) Integer.parseInt(strkey.substring(a, a + 1));
		}
		for (int a = 0; a < strlenght; a++) {
			g.drawImage(imagea_number[bytea_str[(strlenght - a - 1)]], x
					- a * offer, y, Graphics.LEFT | Graphics.TOP);
		}
	}
	
	 /**
		 * ���Զ���ͼ�ֿ��������
		 * 
		 * @param g
		 * @param img
		 *            �ֿ�ͼ
		 * @param str
		 *            Ҫ���Ƶ��ַ���
		 * @param fontStoreroom
		 *            �ֿ��ַ���
		 * @param x
		 *            ���Ƶ�xλ��
		 * @param y
		 *            ���Ƶ�yλ��
		 * @param clipW
		 *            ͼ�Ͳü����(ÿ����)
		 * @param clipH
		 *            ͼ�Ͳü��߶�(ÿ����)
		 * @param anchor
		 *            �Ե�ǰһ�����ֽ���ê��
		 * @param space
		 *            ÿ�����ֵĿ�϶
		 * @param numDigit
		 *            �����ֽ��в�0 ,�����Ϊ6����Ϊ1��ôӦ�û���Ϊ000001
		 */
		public static void drawString(Graphics g, Image img, String str,
				String fontStoreroom, int x, int y, int clipW, int clipH,
				int anchor, int space, int numDigit) {
			StringBuffer sb = new StringBuffer();
			sb.append(str);
			for (int i = sb.length(); i < numDigit; i++) {
				sb.insert(0, "0");// maxNumDigit �������Ļ���0 ��1 == 001
			}
			int charId;
			str = sb.toString();
			int strLength = str.length();
			int offset = getAnchorOffset(anchor, strLength * clipW + space, clipH);
			x -= (offset & 0xffff0000) >> 16;
			y -= (offset & 0xffff);
			for (int i = 0; i < strLength; i++, x += clipW + space) {
				charId = fontStoreroom.indexOf(str.charAt(i));
				if (charId == -1) {
					g.setClip(x, y, clipW, 30);
					g.drawChar(str.charAt(i), x, y, 0);
					// continue;
				} else {
					g.setClip(x, y, clipW, clipH);
					g.drawImage(img, x - charId * clipW, y, 0);
				}
			}
			g.setClip(0, 0, 640, 530);
		}

		/**
		 * �Զ����ê�㷽��. ��Ϊ����ͼ�ͺ����ֽ���ê�� �˷�����֧��Graphics.BASELINE��Ϊ transform���� �÷�int
		 * offsetXY = getAnchorOffset(anchor,transform,width,height); x_dest -=
		 * (short)((offsetXY >> 16) & 0xffff); y_dest -= (short)(offsetXY & 0xffff);
		 * 
		 * @param anchor
		 *            Graphics���ê��ֵ
		 * @param w
		 *            ����Ŀ��
		 * @param h
		 *            ����ĸ߶�
		 * @return һ��intֵ�������ê����λ��ƫ������
		 */
		public static int getAnchorOffset(int anchor, int w, int h) {
			int offX = 0;
			int offY = 0;
			if ((anchor & Graphics.BASELINE) != 0)
				throw new java.lang.IllegalArgumentException();
			if ((anchor & Graphics.HCENTER) != 0) {
				offX = w >> 1;
			} else if ((anchor & Graphics.RIGHT) != 0) {
				offX = w;
			}
			if ((anchor & Graphics.VCENTER) != 0) {
				offY = h >> 1;
			} else if ((anchor & Graphics.BOTTOM) != 0) {
				offY = h;
			}
			return (offX << 16) | offY;
		}
		/**
		 * 
		 * @param img
		 * @param count
		 * @param width
		 * @param height
		 * @param transform
		 * @param drawX
		 * @param drawY
		 * @param g
		 * @param anchor
		 * @param cutType    1��ʾ���У�2��ʾ����
		 */
		public static void drawRegion(Image img,int count,int width,int height,int transform,int drawX,int drawY,Graphics g,int anchor,int cutType){
			if(cutType==2){  //����
				g.drawRegion(img, 0, count*height, width, height, transform, drawX,drawY,anchor);
			}else if(cutType==1){   //����
				g.drawRegion(img, count*width, 0, width, height, transform, drawX,drawY,anchor);
			}
		}

}
