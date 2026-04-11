package MyPage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Font.loadfont;
import Frame.FrameBase;
import Frame.FrameLogin11111;
import Share.BottomMenu;
import Share.UserInfo;
import Shop.OrderDetail;
import Shop.ShopDataManager;
import Shop.Shop;

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

        setSize(500, 700);
        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(null);
        centerPanel.setBackground(new Color(245, 245, 250));

        // 유저 카드 (아이디 및 포인트 표시)
        JPanel userCard = new JPanel(null);
        userCard.setBounds(40, 30, 405, 140);
        userCard.setBackground(Color.WHITE);
        userCard.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));

        JLabel lblUser = new JLabel("어서오세요 " + userId + "님!", SwingConstants.CENTER);
        lblUser.setBounds(0, 25, 405, 30);
        lblUser.setFont(loadfont.Freesentation8ExtraBold.deriveFont(22f));

        // [포인트 라벨]
        lblPoint = new JLabel("보유 포인트: " + userPoints + " P", SwingConstants.CENTER);
        lblPoint.setBounds(0, 60, 405, 40);
        lblPoint.setFont(loadfont.Freesentation9Black.deriveFont(32f));
        lblPoint.setForeground(new Color(40, 100, 200));

        JLabel Pointmax = new JLabel("포인트 적립 유효기간은 적립일로부터 1년입니다.", SwingConstants.CENTER);
        Pointmax.setBounds(0, 105, 405, 20);
        Pointmax.setFont(loadfont.Freesentation7Bold.deriveFont(13f));
        Pointmax.setForeground(Color.GRAY);

        userCard.add(lblUser);
        userCard.add(lblPoint);
        userCard.add(Pointmax);
        centerPanel.add(userCard);

        // 메뉴 버튼들
        JButton userInfoBtn = createStyledButton("내 정보 확인 및 변경", 200);
        JButton pointHistory = createStyledButton("포인트 적립 내역", 270);
        JButton btnHistory = createStyledButton("구매내역", 340);
        JButton btnFortune = createStyledButton("오늘의 운세 보기  -10p", 410);
        JButton btnShop = createStyledButton("상점 가기", 480);
        JButton logout = new JButton("로그아웃");
        logout.setBounds(180, 550, 120, 40); // 중앙 하단 배치 (x: 180, y: 560)
        logout.setFont(loadfont.Freesentation8ExtraBold.deriveFont(14f));
        logout.setBackground(Color.WHITE);
        logout.setForeground(Color.GRAY);
        logout.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        
        centerPanel.add(userInfoBtn);
        centerPanel.add(pointHistory);
        centerPanel.add(btnHistory);
        centerPanel.add(btnFortune);
        centerPanel.add(btnShop);
        centerPanel.add(logout);
        
        // --- 이벤트 설정 ---
     // 1. 내 정보 확인 및 비밀번호 변경 (비밀번호 마스킹 처리)
        userInfoBtn.addActionListener(e -> {
            if (user != null) {
                // 커스텀 입력 패널 생성
                JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
                JLabel label = new JLabel("새로운 비밀번호를 입력하세요:");
                label.setFont(loadfont.Freesentation8ExtraBold.deriveFont(14f));
                // 💡 JPasswordField를 사용하여 입력값을 *로 마스킹
                JPasswordField pwField = new JPasswordField();
                
                panel.add(label);
                panel.add(pwField);

                // 상단 아이디 표시와 함께 입력창 띄우기
                int option = JOptionPane.showConfirmDialog(this, panel, 
                    userId + "님의 정보 변경", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    // char[] 배열을 String으로 변환
                    String newPw = new String(pwField.getPassword());

                    if (!newPw.trim().isEmpty()) {
                        user.setPassword(newPw);
                        // 변경 내역 기록 (필요 시)
                        user.addHistory("비밀번호 변경 완료", 0);
                        
                        showSimplePopup("변경 완료", "비밀번호가 안전하게 변경되었습니다.");
                    } else {
                        showSimplePopup("알림", "비밀번호를 입력해야 합니다.");
                    }
                }
            }
            else {
                showSimplePopup("알림", "로그인 정보가 없습니다.");
            }
        });
        pointHistory.addActionListener(e -> {
            if (user != null) {
                java.util.List<String> history = user.getPointHistory();
                
                if (history == null || history.isEmpty()) {
                    showSimplePopup("알림", "적립된 내역이 없습니다.");
                } else {
                    // HTML을 사용하여 리스트 형태로 출력
                    StringBuilder sb = new StringBuilder("<html><body style='width: 250px;'>");
                    sb.append("<h3 style='text-align: center; color: #2864C8;'>포인트 적립 리스트</h3><hr>");
                    
                    // 내역 중에서 "적립" 혹은 "+" 기호가 포함된 내역만 필터링하거나 전체를 보여줌
                    for (String log : history) {
                        // 만약 적립 내역만 골라내고 싶다면 조건문(log.contains("+"))을 추가할 수 있습니다.
                        sb.append("• ").append(log).append("<br>");
                    }
                    sb.append("</body></html>");

                    // 스크롤이 가능하도록 JOptionPane 또는 JDialog로 표시
                    JOptionPane.showMessageDialog(this, sb.toString(), "포인트 적립 내역", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
        
        // 구매 내역 및 환불 시 포인트 갱신
        btnHistory.addActionListener(e -> {
            if (dataManager.getOrderHistory().isEmpty()) {
                showSimplePopup("알림", "구매 내역이 없습니다.");
            } else {
                new OrderDetail(dataManager, () -> refreshUI());
            }
        });

        // 운세 보기 (포인트 차감 로직)
        btnFortune.addActionListener(e -> {
            if (UserInfo.currentUser != null) {
                int current = UserInfo.currentUser.getPoints();
                if (current >= 10) {
                    // 1. 데이터 차감
                    UserInfo.currentUser.setPoints(current - 10);
                    // 2. 화면 및 매니저 갱신
                    refreshUI();
                    // 3. 결과 표시
                    showSimplePopup("오늘의 운세", coment.getRandomFortune());
                } else {
                    showSimplePopup("포인트 부족", "운세를 보려면 10P가 필요합니다.");
                }
            }
        });

        btnShop.addActionListener(e -> FrameBase.getInstance(new Shop()));

        logout.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, 
                "로그아웃 하시겠습니까?", "로그아웃", 
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                // 1. 세션 비우기
                UserInfo.currentUser = null;
                
                // 2. 로그인 화면으로 이동 (로그인 프레임 클래스명을 확인하세요!)
                // 보통 FrameLogin11111 또는 프로젝트의 시작 페이지로 연결합니다.
                // 아래 코드는 FrameBase를 통해 로그인 화면 객체를 다시 불러오는 예시입니다.
                JOptionPane.showMessageDialog(this, "로그아웃 되었습니다.");
                
                // 💡 프로젝트 구조에 맞춰 로그인 페이지 클래스명을 넣으세요 (예: FrameLogin11111)
                FrameBase.getInstance(new FrameLogin11111()); 
            }
        });
        
        add(centerPanel, BorderLayout.CENTER);
        add(BottomMenu.addFooterBar(this), BorderLayout.SOUTH);
    }

    // 포인트 데이터를 다시 불러와서 라벨과 매니저를 업데이트하는 메서드
    private void refreshUI() {
        if (UserInfo.currentUser != null) {
            int p = UserInfo.currentUser.getPoints();
            lblPoint.setText("보유 포인트: " + p + " P");
            ShopDataManager.getInstance().setMyPrice(p);
        }
    }

    private JButton createStyledButton(String text, int yPos) {
        JButton btn = new JButton(text);
        btn.setBounds(40, yPos, 405, 55);
        btn.setFont(loadfont.Freesentation8ExtraBold.deriveFont(16f));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createPopupBtn(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(80, 35));
        btn.setFont(loadfont.Freesentation7Bold.deriveFont(14f));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        return btn;
    }

    private void showSimplePopup(String title, String message) {
        JDialog popup = new JDialog((Frame)null, title, true);
        popup.setSize(300, 180);
        popup.setLayout(new BorderLayout());
        popup.setLocationRelativeTo(this);
        popup.getContentPane().setBackground(Color.WHITE);

        JLabel msgLabel = new JLabel("<html><center>" + message + "</center></html>", SwingConstants.CENTER);
        msgLabel.setFont(loadfont.Freesentation8ExtraBold.deriveFont(16f));
        popup.add(msgLabel, BorderLayout.CENTER);

        JButton closeBtn = createPopupBtn("닫기");
        closeBtn.addActionListener(ae -> popup.dispose());
        JPanel p = new JPanel(); p.setBackground(Color.WHITE); p.add(closeBtn);
        popup.add(p, BorderLayout.SOUTH);
        popup.setVisible(true);
    }
}