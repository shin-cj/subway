package Frame;

import Share.UserInfo;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import Font.loadfont;

public class FrameLogin11111 extends JPanel {

    private Color bgColor = Color.WHITE;
    // 깃허브
    public FrameLogin11111() {
        if (loadfont.Freesentation9Black == null) {
            loadfont.loadFonts(); 
        }

        setBackground(bgColor);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 50, 30, 50));

        // 상단 영역
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(bgColor);
        
        JLabel img = new JLabel("이미지 자리", SwingConstants.CENTER);
        img.setPreferredSize(new Dimension(400, 150));
        img.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); 
        
        JLabel titleLabel = new JLabel("저 여기서 내려요", SwingConstants.CENTER);
        titleLabel.setFont(loadfont.Freesentation9Black.deriveFont(24f));
        titleLabel.setBorder(new EmptyBorder(20, 0, 10, 0));

        top.add(img, BorderLayout.NORTH);
        top.add(titleLabel, BorderLayout.CENTER);

        // 중앙 영역
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(bgColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel idLabel = new JLabel("ID ");
        idLabel.setFont(loadfont.Freesentation8ExtraBold.deriveFont(12f));
        JTextField id = new JTextField(15);
        id.setPreferredSize(new Dimension(0, 35));

        JLabel pwLabel = new JLabel("PW");
        pwLabel.setFont(loadfont.Freesentation8ExtraBold.deriveFont(12f));
        JPasswordField pw = new JPasswordField(15);
        pw.setPreferredSize(new Dimension(0, 35));

        // 버튼 영역
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(bgColor);
        JButton loginBtn = new JButton("로그인");
        JButton signupBtn = new JButton("회원가입");
        
        loginBtn.setPreferredSize(new Dimension(0, 40));
        loginBtn.setBackground(new Color(50, 120, 240));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(loadfont.Freesentation9Black.deriveFont(14f));

        signupBtn.setBackground(Color.WHITE);
        signupBtn.setFocusPainted(false);
        signupBtn.setFont(loadfont.Freesentation8ExtraBold.deriveFont(13f));

        btnPanel.add(loginBtn);
        btnPanel.add(signupBtn);

        gbc.gridx = 0; gbc.gridy = 0; center.add(idLabel, gbc);
        gbc.gridy = 1; center.add(id, gbc);
        gbc.insets = new Insets(10, 0, 5, 0); 
        gbc.gridy = 2; center.add(pwLabel, gbc);
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridy = 3; center.add(pw, gbc);
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.gridy = 4; center.add(btnPanel, gbc);

        // 하단 안내 메시지DD
        JLabel pointMsg = new JLabel("회원 가입 시 500 포인트 지급", SwingConstants.CENTER);
        pointMsg.setFont(loadfont.Freesentation9Black.deriveFont(18f));
        pointMsg.setBorder(new EmptyBorder(20, 0, 0, 0));

        Timer blinkTimer = new Timer(500, e -> {
            if (pointMsg.getForeground().equals(Color.RED)) {
                pointMsg.setForeground(bgColor); 
            } else {
                pointMsg.setForeground(Color.RED);
            }
        });
        blinkTimer.start();
        pointMsg.setForeground(Color.RED); 

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(pointMsg, BorderLayout.SOUTH);

        // --- 기능 구현부 ---

        // 1. PW 창에서 엔터 키 누르면 로그인 버튼 클릭 효과
        pw.addActionListener(e -> {
            loginBtn.doClick();
        });

        // 2. 로그인 버튼 클릭 리스너
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

        // 3. 회원가입 버튼 클릭 리스너
        signupBtn.addActionListener(e -> {
            FrameBase.getInstance(new FrameLogin2222());
        });
    }
}