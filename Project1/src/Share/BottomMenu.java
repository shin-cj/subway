package Share;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Font.loadfont;
import Frame.FrameBase;
import Frame.LineSelect;
import MyPage.MyPage;
import Shop.CategoryPage;

/**
 * BottomMenu: 앱 하단의 내비게이션 바를 담당하는 클래스입니다.
 * 나눔스퀘어 라운드 폰트와 따뜻한 일기장 톤의 디자인이 적용되었습니다.
 */
public class BottomMenu {

    // 디자인 테마 색상 정의
    private static final Color COLOR_ACTIVE = new Color(70, 70, 70);       // 활성화된 글자색 (진한 회색)
    private static final Color COLOR_INACTIVE = new Color(180, 170, 160);  // 비활성화된 글자색 (연한 회갈색)
    private static final Color COLOR_BG = Color.WHITE;                     // 바 배경색
    private static final Color COLOR_HOVER = new Color(255, 253, 240);     // 마우스 오버 시 따뜻한 크림색

    public static JPanel addFooterBar(JPanel currentPanel) {
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new GridLayout(1, 3));
        footerPanel.setBackground(COLOR_BG);
        
        // 상단에만 아주 연한 베이지색 선을 그어 영역을 구분합니다.
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(235, 230, 220)));
        footerPanel.setPreferredSize(new Dimension(410, 60)); // 높이를 적절히 조절

        // 각 버튼 생성 (텍스트, 이모지 아이콘, 활성화 여부)
        JButton btnshop = createNavButton("상점", "🏠", currentPanel instanceof LineSelect);
        JButton btnSubway = createNavButton("지하철", "🚇", !(currentPanel instanceof MyPage) && !(currentPanel instanceof LineSelect));
        JButton btnSettings = createNavButton("내정보", "👤", currentPanel instanceof MyPage);

        // --- 이벤트 연결 (패널 교체 로직) ---
        btnshop.addActionListener(e -> FrameBase.getInstance(new CategoryPage()));
        btnSubway.addActionListener(e -> FrameBase.getInstance(new LineSelect())); 
        btnSettings.addActionListener(e -> FrameBase.getInstance(new MyPage()));

        footerPanel.add(btnshop);
        footerPanel.add(btnSubway);
        footerPanel.add(btnSettings);

        return footerPanel;
    }

    /**
     * 일기장 감성에 맞춘 커스텀 네비게이션 버튼을 생성합니다.
     */
    private static JButton createNavButton(String text, String icon, boolean isActive) {
        // HTML을 사용하여 아이콘(이모지)과 텍스트를 수직으로 배치
        String buttonHtml = "<html><center><font size='5'>" + icon + "</font><br>" + text + "</center></html>";
        JButton btn = new JButton(buttonHtml);
        
        // [수정] 폰트를 나눔스퀘어 라운드 Bold로 변경
        btn.setFont(loadfont.NanumB.deriveFont(12f));
        btn.setBackground(COLOR_BG);
        btn.setForeground(isActive ? COLOR_ACTIVE : COLOR_INACTIVE);
        
        // 버튼 기본 스타일 제거 및 부드러운 설정
        btn.setOpaque(true);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 0, 5, 0)); // 상하 여백 조절

        // 롤오버 효과: 마우스를 올리면 따뜻한 배경색으로 변경
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setContentAreaFilled(true);
                btn.setBackground(COLOR_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setContentAreaFilled(false);
                btn.setBackground(COLOR_BG);
            }
        });

        return btn;
    }
}