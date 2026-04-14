package Shop;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.geom.RoundRectangle2D;

import Font.loadfont; 
import Frame.FrameBase;
import Frame.ModernScrollBarUI;
import Share.BottomMenu;

public class CategoryPage extends JPanel {

    public CategoryPage() {
        // 폰트가 로드되었는지 확인
        if (loadfont.NanumEB == null) {
            loadfont.loadFonts();
        }

        setSize(410, 670); // LineSelect와 동일한 창 사이즈에 맞춤 (필요시 500,700 유지 가능)
        setLayout(new BorderLayout());
        setBackground(new Color(255, 253, 240)); // 💡 일기장 종이 배경색 적용
        
        
        
        // --- 📝 상단 타이틀 영역 (LineSelect와 완벽 동일) ---
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false); 
        headerPanel.setPreferredSize(new Dimension(410, 80));
        headerPanel.setLayout(new BorderLayout());

        JLabel title = new JLabel("카테고리를 선택해요", SwingConstants.CENTER);
        title.setFont(loadfont.UhBeeSehyun.deriveFont(32f)); // 💡 어비세현 32f 폰트 적용
        title.setForeground(new Color(70, 70, 70)); // 진한 회갈색
        headerPanel.add(title, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // --- 📦 중앙 리스트 패널 (낙서 배경이 포함된 커스텀 패널 사용) ---
        DoodleListPanel listPanel = new DoodleListPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(new EmptyBorder(15, 30, 80, 30)); // 💡 LineSelect와 동일한 여백

        String[][] categories = {
            {"음료", "/Img/icon_drink.jpg"},
            {"디저트", "/Img/icon_dessert.jpg"},
            {"레저/스포츠", "/Img/icon_Sports.jpg"},
            {"뷰티","/Img/icon_Beauty.jpg"},
            {"디지털/가전" ,"/Img/icon_digital.jpg"},
            {"패션", "/Img/icon_fashion.jpg"},
            {"전체보기", "/Img/icon_all.jpg"}
        };

        for (String[] cat : categories) {
            String categoryName = cat[0];
            String iconPath = cat[1];

            JButton btnPanel = createCategoryButton(categoryName, iconPath);
            listPanel.add(btnPanel);
            listPanel.add(Box.createVerticalStrut(12)); // 버튼 사이 간격
        }

        // --- 📜 스크롤 패널 ---
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setOpaque(false); // 배경 투명화
        scrollPane.getViewport().setOpaque(false); // 뷰포트 투명화
        scrollPane.setBorder(null); // 테두리 제거
        
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        add(scrollPane, BorderLayout.CENTER);

        add(BottomMenu.addFooterBar(this), BorderLayout.SOUTH);
    }

    // --- 🎨 배경 낙서 패널 (모눈종이 + 멈춰있는 기차) ---
    class DoodleListPanel extends JPanel {
        public DoodleListPanel() { 
            setOpaque(false); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // 1. 모눈종이 격자무늬 그리기
            g2.setColor(new Color(235, 230, 210));
            for (int i = 0; i < w; i += 30) g2.drawLine(i, 0, i, h);
            for (int j = 0; j < h; j += 30) g2.drawLine(0, j, w, j);

            // 2. 하단 낙서 (기차 + 구름) 그리기
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            int trainY = h - 70; // 가장 아래쪽(h)에서 70만큼 띄워서 그리기
            
            // 객차 2칸
            g2.setColor(new Color(255, 100, 100)); g2.fillRoundRect(50, trainY, 60, 35, 10, 10);
            g2.setColor(new Color(100, 150, 255)); g2.fillRoundRect(115, trainY, 60, 35, 10, 10);
            
            // 바퀴
            g2.setColor(new Color(80, 80, 80));
            g2.fillOval(60, trainY + 30, 12, 12); g2.fillOval(85, trainY + 30, 12, 12);
            g2.fillOval(125, trainY + 30, 12, 12); g2.fillOval(150, trainY + 30, 12, 12);

            // 구름
            g2.setColor(Color.WHITE);
            g2.fillOval(w - 110, trainY - 45, 35, 25);
            g2.fillOval(w - 90, trainY - 55, 45, 35);
            g2.fillOval(w - 65, trainY - 45, 35, 25);
            
            g2.dispose();
        }
    }

    // --- 카테고리 버튼 생성 메서드 ---
    private JButton createCategoryButton(String title, String iconPath) {
    	// 💡 1. 클래스를 따로 만들지 않고, 여기서 바로 JButton의 디자인을 덮어씌움 (익명 클래스 활용)
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 입체감을 위한 그림자
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(2, 4, getWidth() - 2, getHeight() - 4, 25, 25);

                // 마우스 상태(호버, 클릭)에 따른 배경색 변경
                if (getModel().isPressed()) {
                    g2.setColor(new Color(230, 230, 230)); // 클릭 시 살짝 회색
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(240, 248, 255)); // 호버 시 연한 파란색
                } else {
                    g2.setColor(Color.WHITE); // 기본 배경은 흰색
                }

                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 4, 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        // 💡 2. 버튼 기본 설정
        btn.setLayout(new BorderLayout(10, 0)); 
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 15, 20)); 
        btn.setMaximumSize(new Dimension(350, 75));

        // 💡 3. 아이콘 설정
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getIcon() == null) {
                    g2.setColor(new Color(100, 150, 220));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                } else {
                    g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                    super.paintComponent(g2);
                }
                g2.dispose();
            }
        };
        iconLabel.setPreferredSize(new Dimension(60, 50));
        iconLabel.setOpaque(false);
        
        java.net.URL imgURL = getClass().getResource(iconPath);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage().getScaledInstance(60, 50, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(img));
        }

        // 💡 4. 텍스트 설정
        JLabel textLabel = new JLabel(title);
        textLabel.setFont(loadfont.NanumEB.deriveFont(18f));
        textLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        // 💡 5. 버튼(btn) 안에 아이콘과 텍스트 조립
        btn.add(iconLabel, BorderLayout.WEST);
        btn.add(textLabel, BorderLayout.CENTER);

        // 💡 6. 클릭 시 상점 이동 이벤트
        btn.addActionListener(e -> {
            String filter = title.equals("전체보기") ? null : title;
            FrameBase.getInstance(new Shop(filter)); 
        });

        return btn; // 완성된 버튼 반환
    }
}