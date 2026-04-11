package Share;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import Font.loadfont;
import Frame.FrameBase;
import Frame.LineSelect;
import MyPage.MyPage;

public class BottomMenu {

	public static JPanel addFooterBar(JPanel frame) {
		JPanel footerPanel = new JPanel();
		footerPanel.setLayout(new GridLayout(1, 3));

		// 패널 자체 배경도 하얀색으로 맞춤 (이미지처럼 회색이 안 남게)
		footerPanel.setBackground(Color.WHITE);
		footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
		footerPanel.setPreferredSize(new Dimension(500, 70));

		JButton btnHome = new JButton("메인화면");
		JButton btnSubway = new JButton("지하철정보");
		JButton btnSettings = new JButton("내정보");

		JButton[] footers = { btnHome, btnSubway, btnSettings };

		for (int i = 0; i < footers.length; i++) {
			JButton b = footers[i];
			b.setFont(loadfont.Freesentation7Bold.deriveFont(16f));
			b.setBackground(Color.WHITE);
			b.setMargin(new Insets(10, 0, 10, 0));
			
			b.setOpaque(true); // 배경색을 불투명하게 (꽉 채우기)
			b.setBorderPainted(true); // 테두리를 그리도록 설정
			b.setContentAreaFilled(true); // 내용 영역 채우기
			b.setFocusPainted(false);
			b.setCursor(new Cursor(Cursor.HAND_CURSOR));

			// 구분선 설정: 상하 여백을 20씩 주고 오른쪽에만 선 (마지막 버튼 제외)
			if (i < footers.length - 1) {
				b.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(235, 235, 235)));
			} else {
				b.setBorder(null);
			}

			// 롤오버 음영 효과
			b.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					b.setBackground(new Color(248, 248, 248)); // 마우스 올리면 연한 회색
				}

				@Override
				public void mouseExited(MouseEvent e) {
					b.setBackground(Color.WHITE); // 나가면 다시 흰색
				}
			});

			footerPanel.add(b);
		}
		btnHome.addActionListener(e -> {
            // FrameBase의 싱글톤을 호출하여 패널만 교체!
            FrameBase.getInstance(new LineSelect()); 
        });

        btnSubway.addActionListener(e -> {
            // 지하철 정보 페이지(LineSelect 등)로 이동
            FrameBase.getInstance(new LineSelect());
        });

        btnSettings.addActionListener(e -> {
            // 내 정보 페이지로 이동
            FrameBase.getInstance(new MyPage());
        });
		/*
		 * // 클릭 이벤트 (이전과 동일) btnHome.addActionListener(e -> { //frame.dispose(); new
		 * LineSelect(); }); btnSettings.addActionListener(e -> { frame.dispose(); new
		 * MyPage().setVisible(true); });
		 */

		return footerPanel;
	}
}