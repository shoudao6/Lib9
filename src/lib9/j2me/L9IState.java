package lib9.j2me;

/**
 * �ṩ��Init��Update��Paint����������ӿ�ʵ����Lib9�����е�IUP�ܹ���ʵ�ָýӿڵ����ʵ������һ�������״̬��
 * Lib9��������ɲ�ͬ��״̬���ɵ�һ��״̬��
 * 
 * @author not attributable
 * @version 1.0
 */
public interface L9IState {
	/**
	 * ����״̬�ĳ�ʼ��
	 */
	public abstract void Init();

	/**
	 * ����״̬���߼���AI
	 */
	public abstract void Update();

	/**
	 * ����״̬�Ļ������
	 */
	public abstract void Paint();

	/**
	 * �ͷ�ͼƬ
	 */
	public abstract void RemovePic();
}
