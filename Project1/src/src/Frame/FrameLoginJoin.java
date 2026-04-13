package Frame;

import Share.UserInfo;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import Font.loadfont;

public class FrameLoginJoin extends JPanel {

   private Color bgColor = Color.WHITE;

   private boolean isIdChecked = false; // 중복 확인 통과 여부
   private String checkedId = ""; // 중복 확인 완료된 아이디 보관용

   public FrameLoginJoin() {
      // 폰트 로드 확인
      if (loadfont.Freesentation9Black == null) {
         loadfont.loadFonts();
      }

      setBackground(bgColor);
      setLayout(new BorderLayout());
      // Main과 동일한 전체 여백 설정
      setBorder(new EmptyBorder(30, 50, 30, 50));

      // --- 상단 영역 (Main의 top panel 디자인 복사) ---
      JPanel topPanel = new JPanel(new BorderLayout());
      topPanel.setBackground(bgColor);

      JLabel img = new JLabel("이미지 자리", SwingConstants.CENTER);
      img.setPreferredSize(new Dimension(400, 150));
      // Main과 동일한 경계선
      img.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

      JLabel titleLabel = new JLabel("회원가입", SwingConstants.CENTER);
      // Main과 동일한 타이틀 폰트 및 여백
      titleLabel.setFont(loadfont.Freesentation9Black.deriveFont(24f));
      titleLabel.setBorder(new EmptyBorder(20, 0, 10, 0));

      topPanel.add(img, BorderLayout.NORTH);
      topPanel.add(titleLabel, BorderLayout.CENTER);

      // --- 중앙 영역 (GridBagLayout 사용) ---
      JPanel centerPanel = new JPanel(new GridBagLayout());
      centerPanel.setBackground(bgColor);
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.weightx = 1.0;

      // ID 입력부
      JLabel idLabel = new JLabel("ID ");
      idLabel.setFont(loadfont.Freesentation8ExtraBold.deriveFont(12f));
      JTextField id = new JTextField(15);
      id.setPreferredSize(new Dimension(0, 35)); // Main과 동일한 높이

      JButton checkBtn = new JButton("중복확인");
      // Main의 signupBtn과 유사한 스타일 적용
      checkBtn.setBackground(Color.WHITE);
      checkBtn.setFocusPainted(false);
      checkBtn.setFont(loadfont.Freesentation8ExtraBold.deriveFont(12f));

      // ID 입력창과 중복확인 버튼을 묶을 패널
      JPanel idInputPanel = new JPanel(new BorderLayout(5, 0));
      idInputPanel.setBackground(bgColor);
      idInputPanel.add(id, BorderLayout.CENTER);
      idInputPanel.add(checkBtn, BorderLayout.EAST);

      // PW 입력부
      JLabel pwLabel = new JLabel("Password");
      pwLabel.setFont(loadfont.Freesentation8ExtraBold.deriveFont(12f));
      // 비밀번호 입력창들
      JPasswordField pw = new JPasswordField(15);
      pw.setPreferredSize(new Dimension(0, 35));

      JPasswordField repw = new JPasswordField(15);
      repw.setPreferredSize(new Dimension(0, 35));

      // PW 규정 안내 메시지
      JLabel pwPolicyMsg = new JLabel("*(대소문자 포함 8자 이상 15글자 이하, 특수문자)");
      pwPolicyMsg.setFont(loadfont.Freesentation8ExtraBold.deriveFont(11f));
      pwPolicyMsg.setForeground(Color.GRAY);
      pwPolicyMsg.setBorder(new EmptyBorder(0, 5, 5, 0));

      // 약관 동의
      JCheckBox agree = new JCheckBox("약관 동의");
      agree.setBackground(bgColor);
      agree.setFont(loadfont.Freesentation8ExtraBold.deriveFont(12f));

      // 회원가입 완료 버튼 영역 (Main의 btnPanel 디자인 복사)
      JPanel btnPanel = new JPanel(new GridLayout(1, 1)); // 버튼 하나이므로 1열
      btnPanel.setBackground(bgColor);

      JButton signupBtn = new JButton("가입 완료");
      signupBtn.setPreferredSize(new Dimension(0, 40)); // Main과 동일한 높이
      // Main의 loginBtn과 동일한 파란색 스타일 적용
      signupBtn.setBackground(new Color(50, 120, 240));
      signupBtn.setForeground(Color.WHITE);
      signupBtn.setFocusPainted(false);
      signupBtn.setFont(loadfont.Freesentation9Black.deriveFont(14f));

      btnPanel.add(signupBtn);

      // 결과 메시지 레이블 (중앙 하단에 배치)
      JLabel msg = new JLabel("", SwingConstants.CENTER);
      msg.setFont(loadfont.Freesentation8ExtraBold.deriveFont(12f));
      msg.setForeground(Color.RED);
      msg.setBorder(new EmptyBorder(10, 0, 0, 0));

      // 1. ID 레이블
      gbc.insets = new Insets(5, 0, 5, 0);
      gbc.gridx = 0;
      gbc.gridy = 0;
      centerPanel.add(idLabel, gbc);

      // 2. ID 입력창 패널 (입력창 + 중복확인 버튼)
      gbc.gridy = 1;
      centerPanel.add(idInputPanel, gbc);

      // 3. 메시지 전용 패널 (ID 바로 밑에 배치)
      JPanel msgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
      msgPanel.setBackground(bgColor);
      msgPanel.add(msg); // 메시지 레이블을 패널에 담음

      gbc.insets = new Insets(2, 0, 10, 0); // 위쪽은 붙이고 아래쪽은 뗌
      gbc.gridy = 2;
      centerPanel.add(msgPanel, gbc);

      // 4. Password 레이블
      gbc.insets = new Insets(5, 0, 0, 0);
      gbc.gridy = 3;
      centerPanel.add(pwLabel, gbc);

      // 5. PW 규정 안내 (Main 디자인과 동일하게)
      gbc.insets = new Insets(0, 0, 5, 0);
      gbc.gridy = 4;
      centerPanel.add(pwPolicyMsg, gbc);

      // 6. 비밀번호 입력창들
      gbc.insets = new Insets(5, 0, 5, 0);
      gbc.gridy = 5;
      centerPanel.add(pw, gbc);
      gbc.gridy = 6;
      centerPanel.add(repw, gbc);

      // 7. 약관 동의 및 가입 버튼
      gbc.insets = new Insets(15, 0, 10, 0);
      gbc.gridy = 7;
      centerPanel.add(agree, gbc);

      gbc.insets = new Insets(10, 0, 0, 0);
      gbc.gridy = 8;
      centerPanel.add(btnPanel, gbc);

      // 전체 화면에 패널 붙이기
      add(topPanel, BorderLayout.NORTH);
      add(centerPanel, BorderLayout.CENTER);
      // 중복확인 눌렀을 때 액션~
      checkBtn.addActionListener(e -> {
         String inputId = id.getText();
         String IDpattern = "^[a-zA-Z0-9]{8,15}$";

         if (inputId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디를 먼저 입력해주세요.");
            id.requestFocus();
            return;
         } else if (inputId.contains(" ")) {
            JOptionPane.showMessageDialog(this, "아이디에 공백을 포함할 수 없습니다.");
            isIdChecked = false;
            id.requestFocus();
            return;
         } else if (!inputId.matches(IDpattern)) {
            JOptionPane.showMessageDialog(this, "아이디는 8글자~15글자 사이로 입력해주세오");
            isIdChecked = false;
            id.requestFocus();
            return;
         }

         if (UserInfo.userDatabase.containsKey(inputId)) {
            msg.setText("이미 존재하는 아이디입니다.");
            msg.setForeground(Color.RED);
            isIdChecked = false;
            checkedId = "";
            id.requestFocus();
         } else {
            msg.setText("사용 가능한 아이디입니다.");
            msg.setForeground(new Color(50, 150, 50));
            isIdChecked = true; // 중복 확인 통과!
            checkedId = inputId; // 통과한 당시의 아이디를 저장
            pw.requestFocus();
         }
      });

      agree.addActionListener(e -> {
         int result = JOptionPane.showConfirmDialog(this,
               "개인정보 수집 및 이용 동의 (안)\n\n" + "1. 수집하는 개인정보 항목\n" + "필수항목: 아이디(ID), 비밀번호(PW)\n\n"
                     + "2. 개인정보의 수집 및 이용 목적\n" + "회원 관리 및 포인트 지급, 좌석 공유 시스템 이용\n\n" + "3. 보유 및 이용 기간\n"
                     + "목적 달성 후 지체 없이 파기\n\n" + "위 약관에 동의하십니까?",
               "약관 동의", JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE);

         if (result == JOptionPane.YES_OPTION) {
            agree.setSelected(true);
         } else {
            // [아니오]를 누르면 체크 해제 (가입 화면 그대로 유지)
            agree.setSelected(false);
         }
      });

      // 회원가입 버튼 눌렀을 때 액션
      signupBtn.addActionListener(e -> {
         String currentId = id.getText().trim();
         String inputId = id.getText();
         String pwText = new String(pw.getPassword());
         String rePwText = new String(repw.getPassword());
         // 아이디 중복 확인 버튼 안누름~
         if (!isIdChecked || !currentId.equals(checkedId)) {
            JOptionPane.showMessageDialog(this, "아이디 중복 확인이 필요합니다.", "알림", JOptionPane.WARNING_MESSAGE);
            isIdChecked = false; // 상태 초기화
            return;
         }
         // 아이디 입력 안됨~
         if (inputId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디를 입력해주세요!", "입력 오류", JOptionPane.WARNING_MESSAGE);
            id.requestFocus(); // 아이디 칸으로 커서 이동
            return;
         }
         // 비밀번호 일치 안됨~
         if (!pwText.equals(rePwText)) {
            JOptionPane.showMessageDialog(this, "비밀번호가 서로 일치하지 않습니다.", "비밀번호 오류", JOptionPane.ERROR_MESSAGE);
            repw.requestFocus();
            return;
         }
         // 약관 안누르면 안됨~
         if (!agree.isSelected()) {
            JOptionPane.showMessageDialog(this, "약관에 동의해야 가입이 가능합니다.", "동의 필요", JOptionPane.INFORMATION_MESSAGE);
            return;
         }

         String pwPattern = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,15}$";
         // 특수문자 포함 안하고 8-15 사이~
         if (!pwText.matches(pwPattern)) {
            JOptionPane.showMessageDialog(this, "비밀번호는 특수문자를 포함하여\n8글자 이상 15글자 이하로 입력해주세요.", "비밀번호 형식 오류",
                  JOptionPane.WARNING_MESSAGE);
            pw.requestFocus();
            return;
         }

         // 1. 새 유저 생성 (생성 시 500포인트 부여)
         UserInfo newUser = new UserInfo(inputId, pwText, 500);

         // 2. DB에 저장
         UserInfo.userDatabase.put(inputId, newUser);

         JOptionPane.showMessageDialog(null, "가입 완료! 500포인트가 지급되었습니다.");

         // 3. 로그인 화면으로 이동
         FrameBase.getInstance(new FrameLoginMain());
      });
   }
}