package Frame;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import InSubway.MetroLine;
import Share.ModernScrollBarUI;

class SeatData {
   SeatStatus status;
   String targetStation; // 내리는 역 이름
   int targetIndex; // 내리는 역의 인덱스

   public SeatData(SeatStatus status, String targetStation, int targetIndex) {
      this.status = status;
      this.targetStation = targetStation;
      this.targetIndex = targetIndex;
   }
}

enum SeatStatus {
   EMPTY, // 흰색
   NEAR, // 초록
   MEDIUM, // 주황
   FAR, // 빨강
   SELECTED// 호선 색
}

public class SubwayTopview extends JPanel {

   private int direction = 1; // 1이면 인덱스 증가 방향, -1이면 감소 방향

   private RoundButton lastSelectedButton = null;
   private RoundButton[][] allSeats = new RoundButton[6][7];
   // 호차별 데이터를 저장할 Map (Key: 호차인덱스 0~5, Value: 2차원 상태 배열)
   private Map<Integer, SeatData[][]> trainDataMap = new HashMap<>();
   private int currentCarIndex = 0;

   private String[] lineStations;
   private String currentStationName;
   private Color lineColor;
   private String endSt;

   private int myReservedCar = -1; // 내가 예약한 호차 인덱스
   private int myReservedRow = -1; // 내가 예약한 좌석 행
   private int myReservedCol = -1; // 내가 예약한 좌석 열

   private int currentStationIndex = 0;

   public SubwayTopview(Color lineColor, String startSt, String endSt, List<String> stationData) {
      this.lineColor = lineColor;
      lineStations = stationData.stream().toArray(String[]::new);
      this.endSt = endSt;

      int startIdx = 0;
      int endIdx = 0;

      for (int i = 0; i < lineStations.length; i++) {
         if (lineStations[i].equals(startSt))
            startIdx = i;
         if (lineStations[i].equals(endSt))
            endIdx = i;
      }

      this.currentStationIndex = startIdx;
      // [추가] 운행 방향 결정
      this.direction = (startIdx < endIdx) ? 1 : -1;
      initData();
      setupUI(lineColor, startSt, endSt, lineStations);
      updateSeatUI();
   }

   // 외부(MetroLine)에서 5초마다 호출할 메서드
   public void setCurrentStation(String stationName) {
      this.currentStationName = stationName;

      // 1. 전체 노선 리스트에서 현재 역의 인덱스 번호를 찾음
      for (int i = 0; i < lineStations.length; i++) {
         if (lineStations[i].equals(stationName)) {
            this.currentStationIndex = i;
            break;
         }
      }
      refreshDataByCurrentStation(); // 역이 바뀌었으니 데이터 갱신
      updateSeatUI(); // UI 다시 그리기
   }

   // MetroLine에서 매 역마다 호출할 통합 업데이트 메서드
   public void updateStatusOnMove(String stationName, int stationsPassed) {
      this.currentStationName = stationName;

      // 1. 현재 역 인덱스 갱신
      for (int i = 0; i < lineStations.length; i++) {
         if (lineStations[i].equals(stationName)) {
            this.currentStationIndex = i;
            break;
         }
      }

      // 2. 기존 승객 상태 갱신 (도착한 사람 내리기 포함)
      refreshDataByCurrentStation();

      // 3. ★ 핵심: 5정거장마다 빈 좌석 10% 채우기 ★
      if (stationsPassed > 0 && stationsPassed % 3 == 0) {
         fillNewPassengers(0.1); // 10% 확률
      }

      // 4. UI 새로고침
      updateSeatUI();
   }

   private void fillNewPassengers(double probability) {
      Random rand = new Random();

      for (int c = 0; c < 6; c++) {
         SeatData[][] carSeats = trainDataMap.get(c);
         for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
               // 현재 좌석이 비어있고(targetIndex == -1), 내가 예약한 자리가 아닐 때
               if (carSeats[i][j].targetIndex == -1
                     && !(c == myReservedCar && i == myReservedRow && j == myReservedCol)) {

                  if (rand.nextDouble() < probability) {
                     // 새로운 목적지 생성 (현재 역 이후로 설정)
                     int offset = (rand.nextInt(15) + 1) * direction;
                     int tIdx = currentStationIndex + offset;

                     // 범위 초과 방지
                     tIdx = Math.max(0, Math.min(tIdx, lineStations.length - 1));

                     // 데이터 할당
                     carSeats[i][j].targetIndex = tIdx;
                     carSeats[i][j].targetStation = lineStations[tIdx];
                  }
               }
            }
         }
      }
      System.out.println("새로운 승객이 탑승했습니다.");
   }

   // 좌석 내릴역 랜덤 할당
   private void initData() {
      Random rand = new Random();
      for (int c = 0; c < 6; c++) {
         SeatData[][] carSeats = new SeatData[6][7];
         for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
               if (rand.nextDouble() < 0.3) {
                  int offset = (rand.nextInt(15) + 1) * direction; // 방향 반영
                  int tIdx = currentStationIndex + offset;

                  // 인덱스 범위 초과 방지
                  tIdx = Math.max(0, Math.min(tIdx, lineStations.length - 1));

                  carSeats[i][j] = new SeatData(SeatStatus.EMPTY, lineStations[tIdx], tIdx);
               } else {
                  carSeats[i][j] = new SeatData(SeatStatus.EMPTY, "", -1);
               }
            }
         }
         trainDataMap.put(c, carSeats);
      }
      refreshDataByCurrentStation();
   }

   // 현재 역 기반으로 모든 좌석의 상태(색상 조건)를 재계산
   private void refreshDataByCurrentStation() {
      for (SeatData[][] car : trainDataMap.values()) {
         for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
               SeatData data = car[i][j];

               // 내릴 역 인덱스가 설정된 좌석만 계산
               if (data.targetIndex != -1) {
                  // [수정] 절댓값을 사용하여 방향에 관계없이 남은 정거장 수 계산
                  int diff = Math.abs(data.targetIndex - currentStationIndex);

                  // 이미 도착했는지 확인하는 로직 (인덱스가 일치하면 도착)
                  if (data.targetIndex == currentStationIndex) {
                     data.status = SeatStatus.EMPTY;
                     data.targetIndex = -1;
                  } else if (data.status == SeatStatus.SELECTED)
                     data.status = SeatStatus.SELECTED;
                  else if (diff <= 2)
                     data.status = SeatStatus.NEAR;
                  else if (diff <= 4)
                     data.status = SeatStatus.MEDIUM;
                  else
                     data.status = SeatStatus.FAR;

               }
            }
         }
      }
   }

   private void handleSeatClick(int row, int col) {
      SeatData data = trainDataMap.get(currentCarIndex)[row][col];
      RoundButton currentBtn = allSeats[row][col];

      if (lastSelectedButton != null) {
         updateSeatUI();
      }

      // 2. 현재 버튼 강조 및 저장
      currentBtn.setBackground(Color.LIGHT_GRAY);
      lastSelectedButton = currentBtn;

      // 팝업 생성
      JDialog dialog = new JDialog();
      dialog.setTitle("좌석 정보");
      dialog.setModal(true);
      dialog.setLayout(new BorderLayout(10, 10));
      dialog.setSize(300, 150);
      dialog.setLocationRelativeTo(this);

      String infoText = (data.targetIndex == -1) ? "비어있는 좌석입니다. 앉으시겠습니까?" : "내리는 역 : " + data.targetStation;
      JLabel label = new JLabel(infoText, JLabel.CENTER);
      dialog.add(label, BorderLayout.CENTER);

      JPanel btnPanel = new JPanel();
      JButton btnSat = new JButton("앉았어요");
      JButton btnNotSat = new JButton("못 앉았어요");

      btnSat.addActionListener(e -> {
         if (myReservedCar != -1) {
            SeatData prevData = trainDataMap.get(myReservedCar)[myReservedRow][myReservedCol];
            prevData.status = SeatStatus.EMPTY;
            prevData.targetIndex = -1;
            prevData.targetStation = "";
         }
         // 1. 실제 사용자의 도착역 인덱스 찾기 (endSt는 생성자에서 받은 목적지 역 이름)
         int myEndIdx = -1;
         for (int i = 0; i < lineStations.length; i++) {
            if (lineStations[i].equals(endSt)) { // endSt는 SubwayTopview 생성 시 받은 매개변수
               myEndIdx = i;
               break;
            }
         }

         // 2. 좌석 데이터 업데이트
         data.targetIndex = myEndIdx; // 내리는 역을 나의 목적지로 변경
         data.targetStation = endSt; // 역 이름도 변경
         data.status = SeatStatus.SELECTED;

         // 현재 위치를 전역 변수에 저장 (나중에 다른 곳 앉을 때 지우기 위함)
         myReservedCar = currentCarIndex;
         myReservedRow = row;
         myReservedCol = col;// 상태를 SELECTED로 고정

         lastSelectedButton = null; // 강조 표시 해제 (이제 데이터 색상으로 관리됨)
         updateSeatUI(); // UI 갱신 (이제 switch문에서 SELECTED 색상인 lineColor가 적용됨)
         dialog.dispose();
      });

      btnNotSat.addActionListener(e -> {
         lastSelectedButton = null;
         updateSeatUI();
         dialog.dispose();
      });

      dialog.addWindowListener(new WindowAdapter() {

         @Override
         public void windowClosing(WindowEvent e) {
            lastSelectedButton = null;
            updateSeatUI();
            dialog.dispose();
         }
      });

      btnPanel.add(btnSat);
      btnPanel.add(btnNotSat);
      dialog.add(btnPanel, BorderLayout.SOUTH);
      dialog.setVisible(true);
   }

   // 데이터를 UI에 반영하는 메서드
   private void updateSeatUI() {
      SeatData[][] data = trainDataMap.get(currentCarIndex);
      for (int i = 0; i < 6; i++) {
         for (int j = 0; j < 7; j++) {
            if (allSeats[i][j] == null)
               continue; // 방어 코드
            SeatStatus s = data[i][j].status;
            RoundButton btn = allSeats[i][j];

            if (btn == lastSelectedButton) {
               btn.setBackground(Color.LIGHT_GRAY); // 회색 유지
               continue;
            }
            switch (s) {
            case EMPTY:
               btn.setBackground(Color.WHITE);
               break;
            case NEAR:
               btn.setBackground(Color.GREEN);
               break;
            case MEDIUM:
               btn.setBackground(Color.ORANGE);
               break;
            case FAR:
               btn.setBackground(Color.RED);
               break;
            case SELECTED:
               btn.setBackground(lineColor);
               break;
            }
         }
      }
      revalidate();
      repaint();
   }

   // UI세팅 메서드
   private void setupUI(Color lineColor, String startSt, String endSt, String[] stationData) {

      // 보여주기용 임시 데이터
      MetroLine metroLine1 = new MetroLine(stationData);

      setSize(410, 700);
      setLayout(null);
      setBackground(new Color(255, 253, 240));

      // 1.상단 패널
      JPanel topPanel = new JPanel();
      topPanel.setLayout(new GridLayout(2, 1));
      topPanel.setBounds(0, 0, 410, 60);
      topPanel.setBackground(Color.black);
      JLabel station = new JLabel("라벨 테스트", JLabel.CENTER);
      station.setForeground(Color.yellow);
      try {
         // 1. 프로젝트 내의 폰트 파일을 읽어옵니다. (경로는 실제 파일 위치에 맞게 수정하세요)
         // 예: src/res/Korail_M.ttf 에 있다면 "/res/Korail_M.ttf"
         InputStream is = getClass().getResourceAsStream("/Font/Korail_M.ttf");

         if (is != null) {
            // 2. 폰트 생성 및 스타일/크기 적용 (deriveFont 사용)
            Font baseFont = Font.createFont(Font.TRUETYPE_FONT, is);
            Font stationFont = baseFont.deriveFont(Font.BOLD, 20f); // 20f는 크기
            station.setFont(stationFont);
         } else {
            System.out.println("폰트 파일을 찾을 수 없어 기본 폰트를 사용합니다.");
            station.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
         }
      } catch (Exception e) {
         e.printStackTrace();
         station.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
      }
      topPanel.add(station);

      // 제일 위 역이 움직이는 패널 데이터 불러옴
      metroLine1.trainStart(startSt, endSt, station, this);

      Choice trainCar = new Choice();
      for (int i = 1; i <= 6; i++)
         trainCar.add(i + "호차");

      // [핵심] 호차 변경 시 이벤트 리스너
      trainCar.addItemListener(e -> {
         if (e.getStateChange() == ItemEvent.SELECTED) {
            currentCarIndex = trainCar.getSelectedIndex();
            updateSeatUI(); // UI 갱신
         }
      });
      trainCar.setSize(400, 10);
      topPanel.add(trainCar);

      add(topPanel);

      // --- 2. 중앙 좌석 패널 (스크롤 포함) ---
      Image bgImg = null;
      try {
         bgImg = ImageIO.read(new File("subwayCarout.png"));
      } catch (IOException e) {
         e.printStackTrace();
         System.err.println("subwayCarout.png 파일을 읽을 수 없습니다.");
      }
      JPanel subwayCar = new JPanel();// 의자 그룹을 담을 패널
      BackgroundPanel subwayCarout = new BackgroundPanel(bgImg);// 호선 배경 색상 패널
      subwayCar.setLayout(new GridLayout(3, 2, 100, 50));
      subwayCar.setPreferredSize(new Dimension(200, 1160));
      subwayCarout.setLayout(new GridBagLayout());
      subwayCarout.setPreferredSize(new Dimension(220, 1270));
      subwayCar.setBackground(new Color(234, 234, 234));
      subwayCarout.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

      JPanel seatL1 = new JPanel();
      JPanel seatL2 = new JPanel();
      JPanel seatL3 = new JPanel();
      JPanel seatR1 = new JPanel();
      JPanel seatR2 = new JPanel();
      JPanel seatR3 = new JPanel();

      // 의자 그룹 배열
      JPanel[] seatPanels = { seatL1, seatL2, seatL3, seatR1, seatR2, seatR3 };
      allSeats = new RoundButton[6][7];
      ImageIcon originalL = new ImageIcon("chairL_op.png");
      ImageIcon originalR = new ImageIcon("chairR_op.png");

      // 2. Image 객체로 변환하여 리사이징 (30x30 크기, 부드러운 알고리즘 적용)
      Image imgL = originalL.getImage().getScaledInstance(50, 52, Image.SCALE_SMOOTH);
      Image imgR = originalR.getImage().getScaledInstance(50, 52, Image.SCALE_SMOOTH);

      // 3. 리사이징된 이미지를 다시 ImageIcon으로 만듭니다.
      ImageIcon seatL = new ImageIcon(imgL);
      ImageIcon seatR = new ImageIcon(imgR);

      for (int i = 0; i < seatPanels.length; i++) {

         seatPanels[i].setLayout(new GridLayout(7, 1, 0, 0));
         seatPanels[i].setPreferredSize(new Dimension(30, 210));

         for (int j = 0; j < 7; j++) {
            final int row = i;
            final int col = j;

            allSeats[i][j] = new RoundButton("");

            if (i < 3) {
               allSeats[i][j].setIcon(seatL); // 왼쪽 의자 이미지
            } else {
               allSeats[i][j].setIcon(seatR); // 오른쪽 의자 이미지
            }

            allSeats[i][j].setPreferredSize(new Dimension(30, 30));
            allSeats[i][j].setMargin(new Insets(0, 0, 0, 0));// 여백 제거
            allSeats[i][j].setBorderPainted(false); // 테두리 안 보이게
            allSeats[i][j].setFocusPainted(false); // 클릭 시 테두리 제거
            allSeats[i][j].setBackground(Color.white);

            // 버튼을 눌렀을 때 액션 조정
            allSeats[i][j].addActionListener(e -> {

               handleSeatClick(row, col);
            });

            seatPanels[i].add(allSeats[i][j]);
         }
      }
      subwayCar.add(seatL1);
      subwayCar.add(seatR1);

      subwayCar.add(seatL2);
      subwayCar.add(seatR2);

      subwayCar.add(seatL3);
      subwayCar.add(seatR3);

      subwayCarout.add(subwayCar);

      DoodlePanel subColorBg = new DoodlePanel();
      subColorBg.setBorder(new EmptyBorder(15, 30, 80, 30));
      
      subwayCarout.setBackground(lineColor);
      subColorBg.add(subwayCarout);

      JScrollPane scrollPane = new JScrollPane(subColorBg, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      scrollPane.setBounds(0, 60, 410, 531); // 상자의 크기 결정
      scrollPane.getVerticalScrollBar().setUnitIncrement(16);
      scrollPane.setOpaque(false);
      scrollPane.getViewport().setOpaque(false);
      scrollPane.setBorder(null); // 지저분한 테두리 제거
      scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
      scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

      add(scrollPane);

      JPanel footer = Share.BottomMenu.addFooterBar(this);
      footer.setBounds(0, 600, 410, 70);
      add(footer);

      repaint();
   }

}// SubwayTopview class

class BackgroundPanel extends JPanel {
   private Image backgroundImage;

   public BackgroundPanel(Image image) {
      this.backgroundImage = image;
   }

   @Override
   protected void paintComponent(Graphics g) {
      super.paintComponent(g); // 기본 그리기 수행
      if (backgroundImage != null) {
         // 이미지를 패널 크기에 꽉 차게 그립니다.
         g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
      }
   }
}// BackgroundPanel class

class RoundButton extends JButton {
   private int remainingStations = 0; // 남은 정거장 수

   public RoundButton(String text) {
      super(text);
      setOpaque(false);
      setFocusPainted(false);
      setBorderPainted(false);
      setContentAreaFilled(false);
   }

   @Override
   protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      g2.setColor(getBackground());
      g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

      g2.dispose();
      super.paintComponent(g);
   }
}// RoundButton class

class  DoodlePanel extends JPanel {
    public DoodlePanel() { setOpaque(false); }

    @Override
    protected void paintComponent(Graphics g) {
       super.paintComponent(g);
       Graphics2D g2 = (Graphics2D) g.create();
       g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

       int w = getWidth(), h = getHeight();

       g2.setColor(new Color(235, 230, 210));
       for (int i = 0; i < w; i += 30) g2.drawLine(i, 0, i, h);
       for (int j = 0; j < h; j += 30) g2.drawLine(0, j, w, j);

       g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
       int trainY = h - 70;
       g2.setColor(new Color(255, 100, 100)); g2.fillRoundRect(50, trainY, 60, 35, 10, 10);
       g2.setColor(new Color(100, 150, 255)); g2.fillRoundRect(115, trainY, 60, 35, 10, 10);
       g2.setColor(new Color(80, 80, 80));
       g2.fillOval(60, trainY + 30, 12, 12); g2.fillOval(85, trainY + 30, 12, 12);
       g2.fillOval(125, trainY + 30, 12, 12); g2.fillOval(150, trainY + 30, 12, 12);

       // [수정] 410px 너비에 맞춰 구름 위치 살짝 조정
       g2.setColor(Color.WHITE);
       g2.fillOval(w - 110, trainY - 45, 35, 25);
       g2.fillOval(w - 90, trainY - 55, 45, 35);
       g2.fillOval(w - 65, trainY - 45, 35, 25);
       g2.dispose();
    }
 }

 // --- 커스텀 둥근 버튼 ---
 class RoundedButton extends JButton {
    public RoundedButton(String label) {
       super(label);
       setContentAreaFilled(false);
       setFocusPainted(false);
       setBorderPainted(false);
       setForeground(Color.WHITE);
       setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // [추가] 9호선처럼 명도가 높은 색상이 brighter() 호출 시 너무 하얗게 되는 현상을 방지하기 위한 커스텀 컬러 계산기
    private Color getCustomHoverColor(Color c) {
        int r = Math.min(255, c.getRed() + 25);   // RGB 값을 일정한 수치(25)만큼만 증가시킴
        int g = Math.min(255, c.getGreen() + 25);
        int b = Math.min(255, c.getBlue() + 25);
        return new Color(r, g, b);
    }

    @Override
    protected void paintComponent(Graphics g) {
       Graphics2D g2 = (Graphics2D) g.create();
       g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

       g2.setColor(new Color(0, 0, 0, 35));
       g2.fillRoundRect(2, 4, getWidth() - 2, getHeight() - 4, 25, 25);

       // [수정] 마우스 롤오버 시 brighter() 대신 위에서 만든 getCustomHoverColor 공식 사용
       if (getModel().isPressed()) {
          g2.setColor(getBackground().darker());
       } else if (getModel().isRollover()) {
          g2.setColor(getCustomHoverColor(getBackground())); // 이 부분이 수정되었습니다.
       } else {
          g2.setColor(getBackground());
       }

       g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 4, 25, 25);
       g2.dispose();
       super.paintComponent(g);
    }
 }

