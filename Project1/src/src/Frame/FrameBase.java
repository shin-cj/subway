package Frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
public class FrameBase extends JFrame {

	private static FrameBase instance;
	private Point initialClick; // 창 드래그를 위한 시작 좌표 변수

	public FrameBase(JPanel e) {

		Toolkit tk = Toolkit.getDefaultToolkit();
		setTitle("저 이번역에서 내려요");
		
		// 1. 윈도우 기본 창틀 제거 및 크기 고정
		setUndecorated(true); 
		setResizable(false);

		// 2. 창 크기(410x700) 지정 및 화면 정중앙 배치
		int width = 410;
		int height = 700;
		setBounds(((int) tk.getScreenSize().getWidth() - width) / 2, 
				((int) tk.getScreenSize().getHeight() - height) / 2, width, height);
		
		// 3. 모서리 둥글기 완화 (곡률 40 -> 20으로 줄여서 자연스럽게 변경)
		setShape(new RoundRectangle2D.Double(0, 0, width, height, 20, 20));

		// 4. [추가] 드래그 및 닫기 버튼이 있는 커스텀 상단 타이틀 바
		setLayout(new BorderLayout());
		
		JPanel titleBar = new JPanel(new BorderLayout());
		titleBar.setPreferredSize(new Dimension(width, 30));
		titleBar.setBackground(Color.WHITE); // 배경색과 맞춤
		
		// 우측 상단 닫기 버튼
		JButton closeBtn = new JButton("✕");
		closeBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
		closeBtn.setForeground(Color.GRAY);
		closeBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15)); // 우측 여백
		closeBtn.setContentAreaFilled(false);
		closeBtn.setFocusPainted(false);
		closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		closeBtn.addActionListener(evt -> System.exit(0)); // 프로그램 완전 종료
		titleBar.add(closeBtn, BorderLayout.EAST);

		// 타이틀 바를 잡고 드래그하면 창이 움직이도록 설정
		titleBar.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				initialClick = evt.getPoint(); 
			}
		});
		titleBar.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent evt) {
				int thisX = getLocation().x;
				int thisY = getLocation().y;
				int xMoved = evt.getX() - initialClick.x;
				int yMoved = evt.getY() - initialClick.y;
				setLocation(thisX + xMoved, thisY + yMoved);
			}
		});

		// 프레임에 조립
		add(titleBar, BorderLayout.NORTH); // 타이틀 바는 상단 고정
		if(e != null) add(e, BorderLayout.CENTER); // 메인 화면은 중앙에

		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	// 싱글톤
	public static FrameBase getInstance(JPanel e) {
		if(instance == null) {
			instance = new FrameBase(e);
		} else {
			// [중요 수정] 화면 이동 시 타이틀바(닫기버튼)가 날아가지 않도록 CENTER 패널만 교체
			BorderLayout layout = (BorderLayout) instance.getContentPane().getLayout();
			Component center = layout.getLayoutComponent(BorderLayout.CENTER);
			if (center != null) {
				instance.getContentPane().remove(center);
			}
			instance.getContentPane().add(e, BorderLayout.CENTER);
			instance.revalidate();
			instance.repaint();
		}
		return instance;
	}
}