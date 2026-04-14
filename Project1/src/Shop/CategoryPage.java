package Shop;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Font.loadfont; // 폰트 적용 (필요하면 맑은 고딕으로 바꿔도 됨)
import Frame.FrameBase;
import Share.ModernScrollBarUI;
import Share.BottomMenu;
import java.awt.geom.RoundRectangle2D;

public class CategoryPage extends JPanel {

    public CategoryPage() {
        setSize(500, 700);
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250)); // 연한 배경

        // 상단 타이틀
        JLabel title = new JLabel("카테고리 선택", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(30, 0, 30, 0));
        add(title, BorderLayout.NORTH);

        // 중앙 리스트 패널 (위에서 아래로 쌓이는 구조)
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(new EmptyBorder(10, 30, 10, 30)); // 좌우 여백

        // 💡 카테고리 데이터 (이름, 아이콘 이미지 경로)
        // 나중에 실제 예쁜 아이콘을 /Img/ 폴더에 넣고 경로만 바꿔주면 돼!
        // 추가 시 String + String으로
        String[][] categories = {
            {"음료", "/Img/icon_drink.png"},
            {"디저트", "/Img/icon_dessert.png"},
            {"레저/스포츠", "/Img/icon_meal.png"},
            {"뷰티","/Img/icon_meal.png"},
            {"디지털/가전" ,"/Img/icon_meal.png"},
            {"패션", "/Img/icon_meal.png"},
            {"전체보기", "/Img/icon_all.png"}
        };

        // 데이터 개수만큼 버튼(패널) 생성해서 붙이기
        for (String[] cat : categories) {
            String categoryName = cat[0];
            String iconPath = cat[1];

            // 💡 따로 만든 메서드를 호출해서 아이콘+텍스트가 합쳐진 패널을 받아옴
            JPanel btnPanel = createCategoryButton(categoryName, iconPath);
            listPanel.add(btnPanel);
            listPanel.add(Box.createVerticalStrut(15)); // 버튼 사이의 간격 띄우기
        }

        // 스크롤 추가 (나중에 카테고리가 많아질 경우 대비)
        JScrollPane scrollPane = new JScrollPane(listPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		add(scrollPane, BorderLayout.CENTER);

        // 하단 바 추가
        add(BottomMenu.addFooterBar(this), BorderLayout.SOUTH);
    }

    // 💡 [핵심] 아이콘 + 텍스트를 하나로 묶어서 클릭되는 버튼(패널)으로 만들어주는 메서드
    private JPanel createCategoryButton(String title, String iconPath) {
        // 아이콘(West)과 텍스트(Center)를 나눌 수 있게 BorderLayout 사용
        JPanel panel = new JPanel(new BorderLayout(10, 0)); // 아이콘과 글자 사이 간격 25
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1), // 겉 테두리
            new EmptyBorder(15, 20, 15, 20) // 안쪽 여백
        ));
        panel.setMaximumSize(new Dimension(400, 80)); // 버튼 최대 크기 고정
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 마우스 올리면 손가락 모양

        // 1. [왼쪽] 아이콘 영역
        JLabel iconLabel = new JLabel() {
        	@Override
        	protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getIcon() == null) {
                    // 💡 이미지가 없을 때는 파란색 둥근 배경을 "직접" 색칠합니다!
                    g2.setColor(new Color(100, 150, 220));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                } else {
                    // 💡 이미지가 있을 때는 마스크를 씌워서 그립니다!
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

        // 2. [오른쪽] 텍스트 영역
        JLabel textLabel = new JLabel(title);
        textLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        
        // 텍스트 글자 사이에 간격을 주고 싶다면 아래처럼 띄어쓰기를 넣어도 돼!
        // 예: "음 료", "디 저 트" 
        // 텍스트를 살짝 더 오른쪽으로 보내고 싶다면 여백을 줌
        textLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textLabel, BorderLayout.CENTER);

        // 3. [이벤트] 마우스 클릭 & 호버 효과 달아주기
        MouseAdapter clickEvent = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(240, 248, 255)); // 마우스 올리면 아주 연한 파란색
            }
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE); // 마우스 떼면 다시 흰색
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                // 💡 패널을 클릭하면 상점 화면으로 이동!
                // "전체보기"를 누르면 null을 보내서 필터링 없이 다 나오게 함
                String filter = title.equals("전체보기") ? null : title;
                FrameBase.getInstance(new Shop(filter)); 
            }
        };

        // 💡 텍스트를 누르든, 이미지를 누르든, 여백을 누르든 다 똑같이 작동하도록 셋 다 이벤트 달기
        panel.addMouseListener(clickEvent);
        iconLabel.addMouseListener(clickEvent);
        textLabel.addMouseListener(clickEvent);

        return panel;
    }
}