package Shop; // 💡 본인의 프로젝트 패키지 구조에 맞춰서 변경해 줘! (예: package Shop;)

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class DiaryBackgroundPanel extends JPanel {

    public DiaryBackgroundPanel() {
        // 배경을 직접 그릴 것이므로 기본 배경색은 신경 쓰지 않아도 돼
        setOpaque(false); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        // 그래픽 렌더링 부드럽게 설정
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 💡 1. 일기장 종이 질감의 따뜻한 크림색 (격자무늬 제거!)
        g2.setColor(new Color(255, 253, 240)); 
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // 💡 (선택사항) 만약 화면 맨 밑에 멈춰있는 기차만 살짝 그리고 싶다면 
        // 여기에 이전 코드의 기차 그리는 로직을 넣어도 돼. 
        // 지금은 정보 가독성을 위해 완전히 깔끔한 단색으로 비워뒀어!

        g2.dispose();
    }
}