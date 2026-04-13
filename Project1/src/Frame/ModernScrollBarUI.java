package Frame; // 프로젝트 구조에 맞게 패키지명을 수정하세요 (예: UI, Share 등)

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * 기본 스크롤바 대신 얇고 둥근 모바일 스타일 스크롤바를 그리는 공용 클래스입니다.
 * 앱 전체에서 스크롤바 디자인을 통일할 때 사용합니다.
 */
public class ModernScrollBarUI extends BasicScrollBarUI {
    
    // 스크롤바 양끝의 화살표 버튼 숨기기 (크기를 0으로 만듦)
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        Dimension zeroDim = new Dimension(0, 0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
    }

    // 스크롤바가 다니는 길(배경 트랙)을 투명하게 만듦
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        // 아무것도 안 그림 = 투명
    }

    // 실제 마우스로 잡고 내리는 손잡이(Thumb) 디자인
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        // 테두리를 부드럽게 깎는 안티앨리어싱 켜기 (선명도 증가)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 손잡이 색상: 연한 회색, 투명도 약간 (150)
        g2.setColor(new Color(200, 200, 200, 150)); 
        
        // 손잡이를 조금 더 얇게 보이도록 여백을 두고 그림 (곡률은 10)
        g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4, 10, 10);
        
        g2.dispose();
    }
}