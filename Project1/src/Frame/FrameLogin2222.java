package Frame;

import Share.UserInfo;
import java.awt.*;
import javax.swing.*;

public class FrameLogin2222 extends JPanel {

    public FrameLogin2222() {
        setLayout(new GridLayout(8, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel title = new JLabel("회원가입", SwingConstants.CENTER);

        JTextField id = new JTextField();
        JButton checkBtn = new JButton("중복확인");

        JPasswordField pw = new JPasswordField();
        JPasswordField repw = new JPasswordField();

        JCheckBox agree = new JCheckBox("약관 동의");
        JButton signupBtn = new JButton("회원가입");
        JLabel msg = new JLabel("", SwingConstants.CENTER);

        add(title);

        JPanel idPanel = new JPanel(new BorderLayout());
        idPanel.add(id, BorderLayout.CENTER);
        idPanel.add(checkBtn, BorderLayout.EAST);
        add(idPanel);

        add(new JLabel("Password (대소문자 포함 8자 이상, 특수문자)"));
        add(pw);
        add(repw);
        add(agree);
        add(signupBtn);
        add(msg);

        checkBtn.addActionListener(e -> {
            if (UserInfo.userDatabase.containsKey(id.getText())) {
                msg.setText("이미 존재하는 아이디입니다.");
            } else {
                msg.setText("사용 가능한 아이디입니다.");
            }
        });

        signupBtn.addActionListener(e -> {
            String inputId = id.getText();
            String pwText = new String(pw.getPassword());
            // ... (중복 확인 및 유효성 검사 로직)

            // 1. 새 유저 생성 (생성 시 500포인트 부여)
            UserInfo newUser = new UserInfo(inputId, pwText, 500);
            
            // 2. DB에 저장
            UserInfo.userDatabase.put(inputId, newUser);

            JOptionPane.showMessageDialog(null, "가입 완료! 500포인트가 지급되었습니다.");
            
            // 3. 로그인 화면으로 이동
            FrameBase.getInstance(new FrameLogin11111());
        });
    
    }
}