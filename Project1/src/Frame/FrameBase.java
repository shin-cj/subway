package Frame;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class FrameBase extends JFrame {

	private static FrameBase instance;

	public FrameBase(JPanel e) {

		// 화면 크기 얻음
		Toolkit tk = Toolkit.getDefaultToolkit();

		// 기본 JFrame 구조
		setTitle("저 이번역에서 내려요");
		setLayout(new BorderLayout());
		setBounds(((int) tk.getScreenSize().getWidth()) / 2 - 300, 
				((int) tk.getScreenSize().getHeight()) / 2 - 400,	500, 700);
		
		if(e != null) add(e, BorderLayout.CENTER);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
	
	//싱글톤
	public static FrameBase getInstance(JPanel e) {
		//instance 가 없을 때만 생성
		if(instance == null) {
			instance = new FrameBase(e);
		}else {
			instance.getContentPane().removeAll();
			instance.getContentPane().add(e);
			instance.revalidate();
			instance.repaint();
		}
		return instance;
	}

}
