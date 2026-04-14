package Frame;

import Share.UserInfo;
import java.awt.*;
import java.io.InputStream;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import Font.loadfont;

public class FrameLoginMain extends JPanel {
   
    // =========================================================================
    // 🎨 전역 변수 설정 구역
    // =========================================================================
    private Color bgColor = new Color(255, 253, 240); // 일기장 종이 배경색 (연한 크림색)
    private Timer bgTimer; // 배경에서 기차를 움직이게 할 애니메이션 타이머
    
    // --- 🚂 양방향 다중 기차 관리를 위한 내부 클래스 (기차 설계도) ---
    class Train {
        int x, y; // 기차의 현재 가로(x), 세로(y) 위치
        int speed; // 기차의 이동 속도 (숫자가 클수록 빠름)
        int direction; // 이동 방향 (1 = 오른쪽으로 이동, -1 = 왼쪽으로 이동)
        Color color; // 기차 호선별 고유 색상

        // 기차를 처음 만들 때 속성들을 셋팅해주는 생성자입니다.
        public Train(int x, int y, int speed, int direction, Color color) {
            this.x = x; this.y = y; this.speed = speed; this.direction = direction; this.color = color;
        }
    }
    
    // 화면에 굴러다닐 여러 대의 기차를 담아두는 배열(목록)입니다.
    private Train[] trains;

    // =========================================================================
    // 🚀 메인 화면 생성자 (화면 조립 및 초기화)
    // =========================================================================
    public FrameLoginMain() {
        // [폰트 확인] 나눔스퀘어 라운드 폰트가 로드되어 있는지 확인하고 로드합니다.
        if (loadfont.NanumEB == null) {
            loadfont.loadFonts(); 
        }

        // 화면 전체 레이아웃을 상단(NORTH), 중앙(CENTER), 하단(SOUTH)으로 나눕니다.
        setLayout(new BorderLayout());
        
        // 전체 화면의 가장자리에 여백을 줍니다. (위 30, 왼쪽 20, 아래 30, 오른쪽 20)
        setBorder(new EmptyBorder(30, 20, 30, 20));
        
        // --- 1~7호선 기차들의 초기 상태 셋팅 ---
        trains = new Train[] {
            new Train(-200, 60,  4,  1, new Color(0, 82, 164)),     // 1호선
            new Train(600,  140, 3, -1, new Color(51, 167, 81)),    // 2호선
            new Train(-500, 220, 5,  1, new Color(243, 117, 34)),   // 3호선
            new Train(800,  300, 4, -1, new Color(0, 165, 224)),    // 4호선
            new Train(-100, 380, 3,  1, new Color(153, 108, 172)),  // 5호선
            new Train(500,  460, 5, -1, new Color(205, 124, 47)),   // 6호선
            new Train(-300, 540, 4,  1, new Color(116, 127, 0))     // 7호선
        };

        // --- 🚂 애니메이션 타이머 가동 ---
        bgTimer = new Timer(30, e -> {
            for (Train t : trains) {
                t.x += (t.speed * t.direction); 
                if (t.direction == 1 && t.x > getWidth() + 50) t.x = -300; 
                else if (t.direction == -1 && t.x < -350) t.x = getWidth() + 50;
            }
            repaint(); 
        });
        bgTimer.start();

        // =========================================================================
        // 📝 1. 상단 영역 (일기장 제목 텍스트)
        // =========================================================================
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false); 
        top.setBorder(new EmptyBorder(10, 0, 10, 0)); 
        
        JPanel titleContainer = new JPanel(null); 
        titleContainer.setOpaque(false);
        titleContainer.setPreferredSize(new Dimension(380, 160)); 

        JLabel titleLabel1 = new JLabel("저 이번역에서", SwingConstants.CENTER);
        JLabel titleLabel2 = new JLabel("내려요..", SwingConstants.CENTER);
        
        titleLabel1.setVerticalAlignment(SwingConstants.TOP);
        titleLabel2.setVerticalAlignment(SwingConstants.TOP);

        // --- 제목 폰트 로드 (손글씨 느낌 유지) ---
        try {
            InputStream fontStream = getClass().getResourceAsStream("/UhBee Se_hyun Bold.ttf");
            if (fontStream != null) {
                Font baseFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                titleLabel1.setFont(baseFont.deriveFont(48f)); 
                titleLabel2.setFont(baseFont.deriveFont(60f)); 
            } else {
                titleLabel1.setFont(new Font("Malgun Gothic", Font.BOLD, 48));
                titleLabel2.setFont(new Font("Malgun Gothic", Font.BOLD, 60));
            }
        } catch (Exception ex) {
            titleLabel1.setFont(new Font("Malgun Gothic", Font.BOLD, 48));
            titleLabel2.setFont(new Font("Malgun Gothic", Font.BOLD, 60));
        }
        
        Color titleColor = new Color(70, 70, 70);
        titleLabel1.setForeground(titleColor);
        titleLabel2.setForeground(titleColor);
        
        titleLabel1.setBounds(0, 0, 380, 150); 
        titleLabel2.setBounds(0, 65, 380, 150); 

        titleContainer.add(titleLabel1);
        titleContainer.add(titleLabel2);
        top.add(titleContainer, BorderLayout.CENTER);

        // =========================================================================
        // 🔑 2. 중앙 입력 영역 (나눔스퀘어 라운드 적용)
        // =========================================================================
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false); 
        
        // [디자인 유지] 전체 폼 영역 살짝 우측 배치
        center.setBorder(new EmptyBorder(0, 21, 0, 26));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- ID 입력 파트 (나눔스퀘어 라운드 EB 적용) ---
        JLabel idLabel = new JLabel("ID", SwingConstants.RIGHT); 
        idLabel.setFont(loadfont.NanumEB.deriveFont(14f)); // 나눔EB 적용
        idLabel.setForeground(new Color(100, 80, 70)); 
        idLabel.setPreferredSize(new Dimension(30, 35));

        JTextField id = new JTextField(15);
        id.setPreferredSize(new Dimension(0, 35)); 
        id.setBackground(new Color(255, 255, 250));
        id.setFont(loadfont.NanumB.deriveFont(15f)); // 입력 텍스트는 나눔B 적용
        id.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 200, 180), 2, true), 
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));

        // --- PW 입력 파트 (나눔스퀘어 라운드 EB 적용) ---
        JLabel pwLabel = new JLabel("PW", SwingConstants.RIGHT);
        pwLabel.setFont(loadfont.NanumEB.deriveFont(14f)); // 나눔EB 적용
        pwLabel.setForeground(new Color(100, 80, 70)); 
        pwLabel.setPreferredSize(new Dimension(30, 35));

        JPasswordField pw = new JPasswordField(15);
        pw.setPreferredSize(new Dimension(0, 35));
        pw.setBackground(new Color(255, 255, 250)); 
        pw.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15)); // 입력 텍스트는 나눔B 적용
        pw.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 200, 180), 2, true), 
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));

        // --- 로그인 & 회원가입 버튼 패널 ---
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0)); 
        btnPanel.setOpaque(false);
        
        // [로그인 버튼] 둥근 스타일 및 나눔EB 폰트 적용
        JButton loginBtn = new JButton("로그인") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground()); 
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25); 
                super.paintComponent(g);
                g2.dispose();
            }
        };
        loginBtn.setPreferredSize(new Dimension(0, 45)); 
        loginBtn.setBackground(new Color(110, 160, 220)); 
        loginBtn.setForeground(Color.WHITE); 
        loginBtn.setContentAreaFilled(false); 
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(loadfont.NanumEB.deriveFont(16f)); // 나눔EB 적용
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // [회원가입 버튼] 배경색 일치 테두리 스타일 및 나눔B 폰트 적용
        JButton signupBtn = new JButton("회원가입") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.setColor(new Color(230, 225, 210)); 
                g2.setStroke(new BasicStroke(2f)); 
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 25, 25); 
                super.paintComponent(g);
                g2.dispose();
            }
        };
        signupBtn.setPreferredSize(new Dimension(0, 45));
        signupBtn.setBackground(bgColor); 
        signupBtn.setForeground(new Color(100, 80, 70)); 
        signupBtn.setContentAreaFilled(false);
        signupBtn.setBorderPainted(false);
        signupBtn.setFocusPainted(false);
        signupBtn.setFont(loadfont.NanumB.deriveFont(15f)); // 나눔B 적용
        signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnPanel.add(loginBtn);
        btnPanel.add(signupBtn);

        // --- GridBagLayout 배치 조립 ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0; gbc.insets = new Insets(5, 0, 5, 10);
        center.add(idLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.insets = new Insets(5, 0, 5, 0); 
        center.add(id, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; gbc.insets = new Insets(10, 0, 5, 10);
        center.add(pwLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; gbc.insets = new Insets(10, 0, 5, 0); 
        center.add(pw, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.insets = new Insets(25, 25, 0, 0); 
        center.add(btnPanel, gbc);

        // =========================================================================
        // 🌟 3. 하단 안내 메시지 (나눔스퀘어 라운드 EB 적용)
        // =========================================================================
        JLabel pointMsg = new JLabel("회원 가입 시 500 포인트 지급", SwingConstants.CENTER);
        pointMsg.setFont(loadfont.NanumEB.deriveFont(18f)); // 나눔EB 적용
        pointMsg.setBorder(new EmptyBorder(30, 0, 10, 0));

        Timer blinkTimer = new Timer(600, e -> {
            if (pointMsg.getForeground().equals(new Color(240, 100, 100))) {
                pointMsg.setForeground(new Color(180, 170, 160)); 
            } else {
                pointMsg.setForeground(new Color(240, 100, 100)); 
            }
        });
        blinkTimer.start();
        pointMsg.setForeground(new Color(240, 100, 100)); 

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(pointMsg, BorderLayout.SOUTH);

        // =========================================================================
        // ⚙️ 4. 버튼 동작 로직
        // =========================================================================
        pw.addActionListener(e -> loginBtn.doClick());
        loginBtn.addActionListener(e -> {
            String inputId = id.getText(); 
            String inputPw = new String(pw.getPassword()); 
            
            if (UserInfo.userDatabase.containsKey(inputId)) {
                UserInfo user = UserInfo.userDatabase.get(inputId);
                if (user.getPassword().equals(inputPw)) { 
                    UserInfo.currentUser = user; 
                    FrameBase.getInstance(new LineSelect()); 
                } else {
                    JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "존재하지 않는 아이디입니다.");
            }
        });
        
        signupBtn.addActionListener(e -> FrameBase.getInstance(new FrameLoginJoin()));
    }

    // =========================================================================
    // 🎨 5. 화면 배경 그리기 (모눈종이 + 기차 애니메이션)
    // =========================================================================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // 1. 일기장 종이색 채우기
        g2.setColor(bgColor);
        g2.fillRect(0, 0, w, h);
        
        // 2. 모눈종이 격자무늬
        g2.setColor(new Color(235, 230, 210)); 
        for (int i = 0; i < w; i += 30) g2.drawLine(i, 0, i, h); 
        for (int j = 0; j < h; j += 30) g2.drawLine(0, j, w, j); 

        // 3. 기차 드로잉 (투명도 0.50 적용)
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f));
        int carSpacing = 60; 

        for (Train t : trains) {
            if (t.direction == 1) { 
                for (int i = 0; i < 3; i++) {
                    int carX = t.x + (i * carSpacing); 
                    g2.setColor(t.color);
                    g2.fillRoundRect(carX, t.y, 52, 35, 10, 10); 
                    g2.setColor(new Color(80, 80, 80)); 
                    g2.fillOval(carX + 8, t.y + 30, 12, 12);
                    g2.fillOval(carX + 32, t.y + 30, 12, 12);
                    g2.setColor(Color.GRAY); 
                    g2.fillRect(carX + 52, t.y + 20, 8, 4); 
                }
                int engineX = t.x + (3 * carSpacing);
                g2.setColor(t.color); 
                g2.fillRoundRect(engineX, t.y, 60, 35, 10, 10); 
                g2.setColor(t.color.darker()); 
                g2.fillRoundRect(engineX + 35, t.y - 15, 25, 20, 5, 5); 
                g2.setColor(Color.WHITE); 
                g2.fillRoundRect(engineX + 42, t.y - 10, 12, 10, 3, 3); 
                g2.setColor(new Color(80, 80, 80)); 
                g2.fillOval(engineX + 10, t.y + 30, 14, 14);
                g2.fillOval(engineX + 40, t.y + 30, 14, 14);
                g2.setColor(Color.WHITE); 
                g2.fillOval(engineX + 25, t.y - 35, 20, 15);
                g2.fillOval(engineX + 40, t.y - 45, 25, 20);
            } else { 
                int engineX = t.x;
                g2.setColor(t.color); 
                g2.fillRoundRect(engineX, t.y, 60, 35, 10, 10);
                g2.setColor(t.color.darker()); 
                g2.fillRoundRect(engineX, t.y - 15, 25, 20, 5, 5); 
                g2.setColor(Color.WHITE); 
                g2.fillRoundRect(engineX + 6, t.y - 10, 12, 10, 3, 3); 
                g2.setColor(new Color(80, 80, 80));
                g2.fillOval(engineX + 10, t.y + 30, 14, 14);
                g2.fillOval(engineX + 40, t.y + 30, 14, 14);
                g2.setColor(Color.GRAY); 
                g2.fillRect(engineX + 60, t.y + 20, 8, 4); 
                g2.setColor(Color.WHITE); 
                g2.fillOval(engineX + 15, t.y - 35, 20, 15);
                g2.fillOval(engineX - 5, t.y - 45, 25, 20); 
                for (int i = 0; i < 3; i++) {
                    int carX = t.x + 68 + (i * carSpacing); 
                    g2.setColor(t.color);
                    g2.fillRoundRect(carX, t.y, 52, 35, 10, 10);
                    g2.setColor(new Color(80, 80, 80));
                    g2.fillOval(carX + 8, t.y + 30, 12, 12);
                    g2.fillOval(carX + 32, t.y + 30, 12, 12);
                    if (i < 2) { 
                        g2.setColor(Color.GRAY);
                        g2.fillRect(carX + 52, t.y + 20, 8, 4);
                    }
                }
            }
        }
        
        g2.setColor(Color.WHITE);
        g2.fillOval(w - 120, 60, 45, 35);
        g2.fillOval(w - 90, 45, 55, 45);
        g2.fillOval(w - 60, 60, 45, 35);
        
        g2.dispose();
    }
}