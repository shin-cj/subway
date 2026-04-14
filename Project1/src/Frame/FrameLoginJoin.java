package Frame;

import Share.UserInfo;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import Font.loadfont;
//ww
/**
 * FrameLoginJoin: 회원가입 화면 패널입니다.
 * 일기장 테마(모눈종이, 기차 배경)와 나눔스퀘어 폰트, 부드러운 입력창 디자인을 적용했습니다.
 */
public class FrameLoginJoin extends JPanel {
	//진현
   // 테마 배경색 (일기장 종이색)
   private Color bgColor = new Color(255, 253, 240);

   private boolean isIdChecked = false; // 중복 확인 통과 여부
   private String checkedId = "";       // 중복 확인 완료된 아이디 보관용

   public FrameLoginJoin() {
      // [폰트 확인] 커스텀 폰트가 로드되지 않았다면 로드합니다.
      if (loadfont.NanumEB == null) {
         loadfont.loadFonts();
      }

      // [추가] 팝업창(JOptionPane)의 텍스트와 버튼 폰트를 나눔스퀘어 라운드로 일괄 적용
      UIManager.put("OptionPane.messageFont", loadfont.NanumB.deriveFont(14f));
      UIManager.put("OptionPane.buttonFont", loadfont.NanumB.deriveFont(13f));

      // 기본 패널 설정 (투명하게 설정하여 paintComponent 배경이 보이게 함)
      setOpaque(false);
      setLayout(new BorderLayout());
      // [수정] 위쪽 전체 여백을 더 줄여서 콘텐츠를 위로 끌어올립니다.
      setBorder(new EmptyBorder(10, 35, 30, 35)); 

      // =========================================================
      // 1. 상단 영역 (타이틀 및 뒤로가기 버튼)
      // =========================================================
      JPanel topPanel = new JPanel(new BorderLayout());
      topPanel.setOpaque(false); // 배경 투명화

      // --- [추가] 뒤로가기(닫기) 버튼 ---
      JButton backBtn = new JButton();
      try {
         URL imgUrl = getClass().getResource("/reply_18032509.png");
         if (imgUrl != null) {
            Image img = ImageIO.read(imgUrl);
            backBtn.setIcon(makeTransparentIcon(img, 35, 35, 0.6f)); // 60% 투명도 적용 (톤 다운)
         } else {
            backBtn.setText("〈");
            backBtn.setFont(new Font(Font.DIALOG, Font.BOLD, 22));
            backBtn.setForeground(new Color(150, 150, 150));
         }
      } catch (Exception e) {
         backBtn.setText("〈");
         backBtn.setFont(new Font(Font.DIALOG, Font.BOLD, 22));
         backBtn.setForeground(new Color(150, 150, 150));
      }
      backBtn.setContentAreaFilled(false);
      backBtn.setBorderPainted(false);
      backBtn.setFocusPainted(false);
      backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
      
      // [위치 조절 옵션] 뒤로가기 버튼의 여백을 조절합니다. (EmptyBorder: 위, 왼쪽, 아래, 오른쪽)
      // - 버튼을 더 위로 올리고 싶다면: 첫 번째 숫자(30)를 줄이세요. (예: 20)
      // - 버튼을 더 오른쪽으로 밀고 싶다면: 두 번째 숫자(15)를 늘리세요. (예: 25)
      backBtn.setBorder(new EmptyBorder(-33, 15, 0, 0)); 
      backBtn.addActionListener(e -> FrameBase.getInstance(new FrameLoginMain())); // 로그인 화면으로 돌아가기

      topPanel.add(backBtn, BorderLayout.WEST);

      // --- "회원가입" 타이틀 ---
      JLabel titleLabel = new JLabel("회원가입", SwingConstants.CENTER);
      // [유지] 폰트 크기를 56f로 유지합니다.
      titleLabel.setFont(loadfont.UhBeeSehyun.deriveFont(56f)); 
      titleLabel.setForeground(new Color(70, 70, 70)); // 진한 회갈색
      
      // [위치 조절 옵션] 타이틀의 여백을 조절합니다. (EmptyBorder: 위, 왼쪽, 아래, 오른쪽)
      // 뒤로가기 버튼이 오른쪽으로(15) 밀리면서 차지하는 공간이 늘어났기 때문에, 
      // 타이틀이 화면 정중앙에 오도록 우측 여백을 조금 더 줬습니다. 중앙이 안맞으면 네 번째 숫자(50)를 조절하세요.
      titleLabel.setBorder(new EmptyBorder(45, 0, 20, 50)); 

      topPanel.add(titleLabel, BorderLayout.CENTER);

      // =========================================================
      // 2. 중앙 영역 (입력 폼 - GridBagLayout)
      // =========================================================
      JPanel centerPanel = new JPanel(new GridBagLayout());
      centerPanel.setOpaque(false); // 배경 투명화
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.weightx = 1.0;

      // 공통 입력창 테두리 스타일 (둥글고 연한 베이지색)
      javax.swing.border.Border inputBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 200, 180), 2, true),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
      );

      // --- [ID 입력 파트] ---
      JLabel idLabel = new JLabel("ID");
      idLabel.setFont(loadfont.NanumEB.deriveFont(14f));
      idLabel.setForeground(new Color(100, 80, 70));

      JTextField id = new JTextField(15);
      id.setPreferredSize(new Dimension(0, 40));
      id.setBackground(new Color(255, 255, 250));
      id.setFont(loadfont.NanumB.deriveFont(15f));
      id.setBorder(inputBorder);

      // 중복확인 버튼 (작고 둥근 베이지색 버튼)
      JButton checkBtn = new JButton("중복확인") {
         @Override
         protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // 둥근 모서리
            super.paintComponent(g);
            g2.dispose();
         }
      };
      // 버튼 가로 크기 여유있게 유지
      checkBtn.setPreferredSize(new Dimension(95, 40)); 
      checkBtn.setBackground(new Color(230, 225, 210)); 
      checkBtn.setForeground(new Color(100, 80, 70));
      checkBtn.setFocusPainted(false);
      checkBtn.setContentAreaFilled(false);
      checkBtn.setBorderPainted(false);
      checkBtn.setFont(loadfont.NanumEB.deriveFont(13f));
      checkBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

      // ID 텍스트필드와 중복확인 버튼을 묶을 패널
      JPanel idInputPanel = new JPanel(new BorderLayout(10, 0)); // 컴포넌트 간격 10px
      idInputPanel.setOpaque(false);
      idInputPanel.add(id, BorderLayout.CENTER);
      idInputPanel.add(checkBtn, BorderLayout.EAST);

      // 결과 메시지 레이블 (중복확인 결과 등 표시)
      JLabel msg = new JLabel(" ", SwingConstants.LEFT);
      msg.setFont(loadfont.NanumB.deriveFont(12f));
      msg.setForeground(Color.RED);

      // --- [PW 입력 파트] ---
      JLabel pwLabel = new JLabel("Password");
      pwLabel.setFont(loadfont.NanumEB.deriveFont(14f));
      pwLabel.setForeground(new Color(100, 80, 70));

      // 첫 번째 비밀번호 창 (깨짐 방지용 PasswordFont 적용)
      JPasswordField pw = new JPasswordField(15);
      pw.setPreferredSize(new Dimension(0, 40));
      pw.setBackground(new Color(255, 255, 250));
      pw.setFont(loadfont.PasswordFont); 
      pw.setBorder(inputBorder);

      // 두 번째 비밀번호 확인 창 (깨짐 방지용 PasswordFont 적용)
      JPasswordField repw = new JPasswordField(15);
      repw.setPreferredSize(new Dimension(0, 40));
      repw.setBackground(new Color(255, 255, 250));
      repw.setFont(loadfont.PasswordFont);
      repw.setBorder(inputBorder);

      // PW 규정 안내 메시지
      JLabel pwPolicyMsg = new JLabel("*(대소문자 포함 8~15글자, 특수문자 필수)");
      pwPolicyMsg.setFont(loadfont.NanumR.deriveFont(11f));
      pwPolicyMsg.setForeground(new Color(150, 150, 150));

      // --- [약관 동의 파트] ---
      JCheckBox agree = new JCheckBox(" 개인정보 수집 및 이용 약관 동의");
      agree.setOpaque(false); // 체크박스 배경 투명화
      agree.setFont(loadfont.NanumEB.deriveFont(13f));
      agree.setForeground(new Color(100, 80, 70));
      agree.setCursor(new Cursor(Cursor.HAND_CURSOR));

      // --- [가입 완료 버튼 파트] ---
      JButton signupBtn = new JButton("가입 완료") {
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
      signupBtn.setPreferredSize(new Dimension(0, 45));
      signupBtn.setBackground(new Color(110, 160, 220)); // 파스텔 블루
      signupBtn.setForeground(Color.WHITE);
      signupBtn.setFocusPainted(false);
      signupBtn.setContentAreaFilled(false);
      signupBtn.setBorderPainted(false);
      signupBtn.setFont(loadfont.NanumEB.deriveFont(16f));
      signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

      // =========================================================
      // 3. GridBagLayout을 이용한 컴포넌트 배치 조립
      // =========================================================
      
      // [수정] 폼 전체를 위로 올리기 위해 요소들 사이의 상하 여백(Insets)을 축소했습니다.

      // (1) ID 라벨
      gbc.insets = new Insets(0, 0, 2, 0); 
      gbc.gridy = 0;
      centerPanel.add(idLabel, gbc);

      // (2) ID 입력창 + 중복확인 버튼
      gbc.gridy = 1;
      centerPanel.add(idInputPanel, gbc);

      // (3) ID 피드백 메시지
      gbc.insets = new Insets(2, 5, 5, 0); 
      gbc.gridy = 2;
      centerPanel.add(msg, gbc);

      // (4) PW 라벨
      gbc.insets = new Insets(0, 0, 2, 0);
      gbc.gridy = 3;
      centerPanel.add(pwLabel, gbc);

      // (5) PW 1차 입력창
      gbc.gridy = 4;
      centerPanel.add(pw, gbc);

      // (6) PW 2차 입력창 
      gbc.insets = new Insets(4, 0, 5, 0);
      gbc.gridy = 5;
      centerPanel.add(repw, gbc);

      // (7) PW 정책 안내 문구
      gbc.insets = new Insets(0, 5, 10, 0);
      gbc.gridy = 6;
      centerPanel.add(pwPolicyMsg, gbc);

      // (8) 약관 동의 체크박스
      gbc.insets = new Insets(5, 0, 10, 0);
      gbc.gridy = 7;
      centerPanel.add(agree, gbc);

      // (9) 가입 완료 버튼
      gbc.insets = new Insets(5, 0, 0, 0);
      gbc.gridy = 8;
      centerPanel.add(signupBtn, gbc);

      // [핵심 수정] 하단에 투명한 빈 공간(weighty=1.0)을 추가하여 전체 폼을 위로 밀어 올립니다.
      gbc.gridy = 9;
      gbc.weighty = 1.0; 
      centerPanel.add(Box.createVerticalGlue(), gbc);

      // 전체 폼 조립
      add(topPanel, BorderLayout.NORTH);
      add(centerPanel, BorderLayout.CENTER);


      // =========================================================
      // 4. 기능 및 액션 리스너 설정
      // =========================================================
      
      // [중복 확인 로직]
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
            JOptionPane.showMessageDialog(this, "아이디는 8글자~15글자 사이로 입력해주세요.");
            isIdChecked = false;
            id.requestFocus();
            return;
         }

         if (UserInfo.userDatabase.containsKey(inputId)) {
            msg.setText("이미 존재하는 아이디입니다.");
            msg.setForeground(new Color(220, 80, 80)); 
            isIdChecked = false;
            checkedId = "";
            id.requestFocus();
         } else {
            msg.setText("사용 가능한 아이디입니다.");
            msg.setForeground(new Color(50, 150, 50)); 
            isIdChecked = true; 
            checkedId = inputId; 
            pw.requestFocus();
         }
      });

      // [약관 동의 팝업 로직]
      agree.addActionListener(e -> {
         int result = JOptionPane.showConfirmDialog(this,
               "개인정보 수집 및 이용 동의 (안)\n\n" 
                     + "1. 수집하는 개인정보 항목\n" 
                     + "필수항목: 아이디(ID), 비밀번호(PW)\n\n"
                     + "2. 개인정보의 수집 및 이용 목적\n" 
                     + "회원 관리 및 포인트 지급, 좌석 공유 시스템 이용\n\n" 
                     + "3. 보유 및 이용 기간\n"
                     + "목적 달성 후 지체 없이 파기\n\n" 
                     + "위 약관에 동의하십니까?",
               "약관 동의", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

         if (result == JOptionPane.YES_OPTION) {
            agree.setSelected(true);
         } else {
            agree.setSelected(false);
         }
      });

      // [가입 완료 로직]
      signupBtn.addActionListener(e -> {
         String currentId = id.getText().trim();
         String inputId = id.getText();
         String pwText = new String(pw.getPassword());
         String rePwText = new String(repw.getPassword());
         
         // 1. 아이디 중복 확인 여부 체크
         if (!isIdChecked || !currentId.equals(checkedId)) {
            JOptionPane.showMessageDialog(this, "아이디 중복 확인이 필요합니다.", "알림", JOptionPane.WARNING_MESSAGE);
            isIdChecked = false;
            return;
         }
         // 2. 아이디 입력 체크
         if (inputId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디를 입력해주세요!", "입력 오류", JOptionPane.WARNING_MESSAGE);
            id.requestFocus();
            return;
         }
         // 3. 비밀번호 재확인 체크
         if (!pwText.equals(rePwText)) {
            JOptionPane.showMessageDialog(this, "비밀번호가 서로 일치하지 않습니다.", "비밀번호 오류", JOptionPane.ERROR_MESSAGE);
            repw.requestFocus();
            return;
         }
         // 4. 약관 동의 체크
         if (!agree.isSelected()) {
            JOptionPane.showMessageDialog(this, "약관에 동의해야 가입이 가능합니다.", "동의 필요", JOptionPane.INFORMATION_MESSAGE);
            return;
         }

         // 5. 비밀번호 정규식 체크
         String pwPattern = "^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,15}$";
         if (!pwText.matches(pwPattern)) {
            JOptionPane.showMessageDialog(this, "비밀번호는 특수문자를 포함하여\n8글자 이상 15글자 이하로 입력해주세요.", "비밀번호 형식 오류",
                  JOptionPane.WARNING_MESSAGE);
            pw.requestFocus();
            return;
         }

         // 유저 생성 및 DB 저장
         UserInfo newUser = new UserInfo(inputId, pwText, 500);
         UserInfo.userDatabase.put(inputId, newUser);

         JOptionPane.showMessageDialog(null, "가입 완료! 환영 선물로 500포인트가 지급되었습니다.");

         // 로그인 화면으로 돌아가기
         FrameBase.getInstance(new FrameLoginMain());
      });
   }

   /**
    * 이미지를 지정한 크기와 투명도(alpha)로 변경해주는 유틸리티 메서드
    */
   private ImageIcon makeTransparentIcon(Image img, int width, int height, float alpha) {
      BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = bimg.createGraphics();
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g2.drawImage(img, 0, 0, width, height, null);
      g2.dispose();
      return new ImageIcon(bimg);
   }

   // =========================================================
   // 5. 배경 직접 그리기 (모눈종이 + 정지된 기차 낙서)
   // =========================================================
   @Override
   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      int w = getWidth(), h = getHeight();

      // 1. 일기장 종이 베이스 색상
      g2.setColor(bgColor);
      g2.fillRect(0, 0, w, h);
      
      // 2. 모눈종이 격자 선
      g2.setColor(new Color(235, 230, 210));
      for (int i = 0; i < w; i += 30) g2.drawLine(i, 0, i, h);
      for (int j = 0; j < h; j += 30) g2.drawLine(0, j, w, j);
      
      // 3. 배경 낙서 기차 (멈춰 있는 상태)
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
      int trainX = 80, trainY = 540; 
      Color tColor = new Color(150, 150, 150); 

      // 객차 2칸 그리기
      for (int i = 0; i < 2; i++) {
         int carX = trainX + (i * 60);
         g2.setColor(tColor);
         g2.fillRoundRect(carX, trainY, 52, 35, 10, 10);
         g2.setColor(new Color(80, 80, 80)); 
         g2.fillOval(carX + 8, trainY + 30, 12, 12);
         g2.fillOval(carX + 32, trainY + 30, 12, 12);
         g2.setColor(Color.GRAY); 
         g2.fillRect(carX + 52, trainY + 20, 8, 4);
      }
      // 기관차 그리기
      int engineX = trainX + 120;
      g2.setColor(tColor);
      g2.fillRoundRect(engineX, trainY, 60, 35, 10, 10);
      g2.setColor(tColor.darker()); 
      g2.fillRoundRect(engineX + 35, trainY - 15, 25, 20, 5, 5);
      g2.setColor(Color.WHITE); 
      g2.fillRoundRect(engineX + 42, trainY - 10, 12, 10, 3, 3);
      g2.setColor(new Color(80, 80, 80)); 
      g2.fillOval(engineX + 10, trainY + 30, 14, 14);
      g2.fillOval(engineX + 40, trainY + 30, 14, 14);
      
      // 굴뚝 연기
      g2.setColor(Color.WHITE); 
      g2.fillOval(engineX + 25, trainY - 35, 20, 15);
      g2.fillOval(engineX + 40, trainY - 45, 25, 20);

      // 구름
      g2.fillOval(w - 120, 100, 45, 35);
      g2.fillOval(w - 90, 85, 55, 45);
      g2.fillOval(w - 60, 100, 45, 35);
      
      g2.dispose();
   }
}