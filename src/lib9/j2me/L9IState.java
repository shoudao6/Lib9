package lib9.j2me;

/**
 * 提供了Init、Update、Paint方法，这个接口实现了Lib9引擎中的IUP架构，实现该接口的类的实例就是一个具体的状态，
 * Lib9程序就是由不同的状态构成的一个状态机
 * 
 * @author not attributable
 * @version 1.0
 */
public interface L9IState {
	/**
	 * 负责状态的初始化
	 */
	public abstract void Init();

	/**
	 * 负责状态的逻辑或AI
	 */
	public abstract void Update();

	/**
	 * 负责状态的画面绘制
	 */
	public abstract void Paint();

	/**
	 * 释放图片
	 */
	public abstract void RemovePic();
}
