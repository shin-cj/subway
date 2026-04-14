package MyPage;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

import Font.loadfont;
import Frame.FrameBase;
import Frame.FrameLoginMain;
import Share.BottomMenu;
import Share.UserInfo;
import Shop.OrderDetail;
import Shop.ShopDataManager;

public class MyPage extends JPanel {

    private JLabel lblPoint; // 포인트 실시간 갱신을 위해 필드로 선언

    public MyPage() {
        FortuneComent coment = new FortuneComent();
        ShopDataManager dataManager = ShopDataManager.getInstance();

        // 1. 로그인 유저 정보 및 포인트 동기화
        UserInfo user = UserInfo.currentUser;
        String userId = (user != null) ? user.getId() : "Guest";
        int userPoints = (user != null) ? user.getPoints() : 0;

        // 상점 데이터매니저와 유저 포인트 일치시킴
        dataManager.setMyPrice(userPoints);
        //ww
        // FrameBase의 타이틀바(30px)를 제외한 높이 670과 폭 410에 맞춤
        setSize(410, 670);
        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(null);
        centerPanel.setBackground(new Color(250, 250, 252)); // 약간 더 밝고 깨끗한 배경

        // [1] 유저 카드 (너비 370으로 조정, 여백 20)
        JPanel userCard = new JPanel(null);
        userCard.setBounds(20, 20, 370, 140);
        userCard.setBackground(Color.WHITE);
        userCard.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        JLabel lblUser = new JLabel("어서오세요 " + userId + "님!", SwingConstants.CENTER);
        lblUser.setBounds(0, 25, 370, 30);
        lblUser.setFont(loadfont.Freesentation8ExtraBold.deriveFont(20f));

        lblPoint = new JLabel("보유 포인트: " + userPoints + " P", SwingConstants.CENTER);
        lblPoint.setBounds(0, 55, 370, 45);
        lblPoint.setFont(loadfont.Freesentation9Black.deriveFont(28f));
        lblPoint.setForeground(new Color(40, 100, 200));

        JLabel Pointmax = new JLabel("포인트 적립 유효기간은 적립일로부터 1년입니다.", SwingConstants.CENTER);
        Pointmax.setBounds(0, 105, 370, 20);
        Pointmax.setFont(loadfont.Freesentation7Bold.deriveFont(12f));
        Pointmax.setForeground(Color.GRAY);

        123123123
        
        userCard.add(lblUser);
        userCard.add(lblPoint);
        userCard.add(Pointmax);
        centerPanel.add(userCard);

        // [2] 메뉴 버튼들 (너비 370, 위치 x=20 고정)
        JButton userInfoBtn = createStyledButton("내 정보 확인 및 변경", 180);
        JButton pointHistory = createStyledButton("포인트 적립 내역", 245);
        JButton btnHistory = createStyledButton("구매내역", 310);
        JButton btnFortune = createStyledButton("오늘의 운세 보기  -10p", 375);
        // test
        // [3] 로그아웃 버튼 (하단 중앙 배치)
        JButton logout = new JButton("로그아웃");
        logout.setBounds(145, 520, 120, 38); // 410 너비에서 중앙 정렬 (145 + 120 + 145 = 410)
        logout.setFont(loadfont.Freesentation8ExtraBold.deriveFont(13f));
        logout.setBackground(Color.WHITE);
        logout.setForeground(new Color(150, 150, 150));
        logout.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logout.setFocusPainted(false);

        centerPanel.add(userInfoBtn);
        centerPanel.add(pointHistory);
        centerPanel.add(btnHistory);
        centerPanel.add(btnFortune);
        centerPanel.add(logout);
        
        // --- 이벤트 설정 (기존 로직 유지) ---
        userInfoBtn.addActionListener(e -> {
            if (user != null) {
                JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
                JLabel label = new JLabel("새로운 비밀번호를 입력하세요:");
                label.setFont(loadfont.Freesentation8ExtraBold.deriveFont(14f));
                JPasswordField pwField = new JPasswordField();
                panel.add(label);
                panel.add(pwField);

                int option = JOptionPane.showConfirmDialog(this, panel, 
                    userId + "님의 정보 변경", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    String newPw = new String(pwField.getPassword());
                    if (!newPw.trim().isEmpty()) {
                        user.setPassword(newPw);
                        showSimplePopup("변경 완료", "비밀번호가 안전하게 변경되었습니다.");
                    } else {
                        showSimplePopup("알림", "비밀번호를 입력해야 합니다.");
                    }
                }
            } else {
                showSimplePopup("알림", "로그인 정보가 없습니다.");
            }
        });

        pointHistory.addActionListener(e -> {
            if (user != null) {
                java.util.List<String> history = user.getPointHistory();
                if (history == null || history.isEmpty()) {
                    showSimplePopup("알림", "적립된 내역이 없습니다.");
                } else {
                    StringBuilder sb = new StringBuilder("<html><body style='width: 200px;'>");
                    sb.append("<h4 style='text-align: center; color: #2864C8;'>포인트 적립 리스트</h4><hr>");
                    for (String log : history) sb.append("• ").append(log).append("<br>");
                    sb.append("</body></html>");
                    JOptionPane.showMessageDialog(this, sb.toString(), "내역", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
        
        btnHistory.addActionListener(e -> {
            if (dataManager.getOrderHistory().isEmpty()) {
                showSimplePopup("알림", "구매 내역이 없습니다.");
            } else {
                new OrderDetail(dataManager, () -> refreshUI());
            }
        });

        btnFortune.addActionListener(e -> {
            if (UserInfo.currentUser != null) {
                int current = UserInfo.currentUser.getPoints();
                if (current >= 10) {
                    UserInfo.currentUser.setPoints(current - 10);
                    refreshUI();
                    showSimplePopup("오늘의 운세", coment.getRandomFortune());
                } else {
                    showSimplePopup("포인트 부족", "운세를 보려면 10P가 필요합니다.");
                }
            }
        });

        logout.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "로그아웃 하시겠습니까?", "Logout", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                UserInfo.currentUser = null;
                JOptionPane.showMessageDialog(this, "로그아웃 되었습니다.");
                FrameBase.getInstance(new FrameLoginMain()); 
            }
        });
        
        add(centerPanel, BorderLayout.CENTER);
        
        // 하단바 위치 및 크기 조정
        JPanel footer = BottomMenu.addFooterBar(this);
        footer.setPreferredSize(new Dimension(410, 70));
        add(footer, BorderLayout.SOUTH);
    }

    private void refreshUI() {
        if (UserInfo.currentUser != null) {
            int p = UserInfo.currentUser.getPoints();
            lblPoint.setText("보유 포인트: " + p + " P");
            ShopDataManager.getInstance().setMyPrice(p);
        }
    }

    private JButton createStyledButton(String text, int yPos) {
        JButton btn = new JButton(text);
        btn.setBounds(20, yPos, 370, 52); // 너비 370으로 조정
        btn.setFont(loadfont.Freesentation8ExtraBold.deriveFont(15f));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225), 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // 호버 효과 추가
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(252, 252, 252)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(Color.WHITE); }
        });
        return btn;
    }

    private void showSimplePopup(String title, String message) {
        JDialog popup = new JDialog((Frame)null, title, true);
        popup.setSize(300, 180);
        popup.setLayout(new BorderLayout());
        popup.setLocationRelativeTo(this);
        popup.setUndecorated(true); // 팝업도 깔끔하게 테두리 제거 가능 (선택사항)
        
        JPanel pMain = new JPanel(new BorderLayout());
        pMain.setBackground(Color.WHITE);
        pMain.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JLabel msgLabel = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        msgLabel.setFont(loadfont.Freesentation8ExtraBold.deriveFont(15f));
        pMain.add(msgLabel, BorderLayout.CENTER);

        JButton closeBtn = new JButton("닫기");
        closeBtn.setPreferredSize(new Dimension(80, 35));
        closeBtn.setFont(loadfont.Freesentation7Bold.deriveFont(13f));
        closeBtn.setBackground(new Color(245, 245, 245));
        closeBtn.addActionListener(ae -> popup.dispose());
        
        JPanel pBot = new JPanel(); pBot.setBackground(Color.WHITE); pBot.add(closeBtn);
        pMain.add(pBot, BorderLayout.SOUTH);
        
        popup.add(pMain);
        popup.setVisible(true);
    }
}