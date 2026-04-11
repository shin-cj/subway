package Frame; // 이 파일이 위치한 폴더(패키지) 경로를 지정합니다.

// 색상, 폰트, 레이아웃 등 디자인 도구
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Window;
// 클릭, 마우스 이동 등 사용자 동작 처리 도구
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList; // 순서가 있는 목록(리스트) 저장 도구
import java.util.HashMap;
import java.util.LinkedHashSet; // 중복을 허용하지 않으면서 순서를 기억하는 저장 도구
import java.util.List; // 리스트 인터페이스
import java.util.Map;
import java.util.Set; // 집합(중복 불가) 인터페이스

import javax.imageio.ImageIO;
// 프로그램 제작에 필요한 자바의 도구(라이브러리)들을 가져옵니다.
// 버튼, 라벨, 창 등 UI 구성 요소를 위한 도구
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent; // 텍스트 입력 감지를 위한 이벤트 도구
import javax.swing.event.DocumentListener; // 텍스트 변경을 실시간으로 듣고 있는 감시 도구


import Font.loadfont;
import Frame.FrameBase;

/**
 * subwayBase: 모든 호선(1호선, 2호선 등) 앱이 공통적으로 사용하는 부모 클래스입니다. JFrame을 상속받아 기본
 * 창(Window) 기능을 가집니다.
 */
public class subwayBase extends JPanel {
   // --- 데이터를 담는 변수들 (전역 변수) ---
   protected String[] stationData; // 이 호선에 속한 모든 역 이름들을 담을 배열입니다.
   protected Color lineColor; // 호선을 상징하는 색상(예: 2호선은 초록색)입니다.
   protected String lineName; // 호선의 이름(예: "1호선")을 저장합니다.

   // --- 내부 동작을 위한 변수 ---
   private Set<String> recentSearches; // 사용자가 최근 검색한 역 이름을 중복 없이 저장합니다.
   private static Map<String, Set<String>> globalSearchHistory = new HashMap<>();
   private JPanel listContainer; // 최근 검색 기록 레이블들이 하나씩 쌓일 바구니(패널)입니다.
   protected JTextField startField; // 사용자가 출발역을 입력하는 칸입니다.
   protected JTextField endField; // 사용자가 도착역을 입력하는 칸입니다.
   private ImageIcon backIcon;
   /**
    * [생성자] 클래스가 처음 만들어질 때 호출되어 초기 설정을 수행합니다.
    */
   public subwayBase(String lineName, String[] stationData, Color lineColor) {
      this.lineName = lineName; // 외부에서 받아온 호선 이름을 이 클래스 변수에 넣습니다.
      this.stationData = stationData; // 외부에서 받아온 역 리스트를 넣습니다.
      this.lineColor = lineColor; // 외부에서 받아온 색상을 넣습니다.
      
      // [수정] src 폴더 내 이미지를 클래스 로더를 통해 가져옵니다.
      try {
          // getResource는 src 폴더(또는 빌드된 클래스 경로)를 기준으로 파일을 찾습니다.
          URL imgUrl = getClass().getResource("/reply_18032509.png"); 
          if (imgUrl != null) {
              Image img = ImageIO.read(imgUrl);
              backIcon = new ImageIcon(img.getScaledInstance(35, 35, Image.SCALE_SMOOTH));
          } else {
              System.err.println("이미지 파일을 찾을 수 없습니다: src/reply_18032509.png");
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
      if (!globalSearchHistory.containsKey(lineName)) {
          // 2. 없으면 새 바구니를 창고에 넣어줌
          globalSearchHistory.put(lineName, new LinkedHashSet<>());
      }
      
      // 3. 창고에서 내 호선 이름표가 붙은 바구니를 꺼내서 연결
      this.recentSearches = globalSearchHistory.get(lineName);

      setSize(500, 700); // 창의 너비를 400, 높이를 650 픽셀로 정합니다.
      setLayout(null); // 컴포넌트들을 자유로운 위치(좌표)에 배치하기 위해 기본 레이아웃을 끕니다.
      initUI(); // 버튼, 입력창 등 실제 눈에 보이는 요소들을 만드는 기능을 실행합니다.
   }

   private void initUI() {

      // [1] 헤더 패널 제작 (상단 흰색 바 영역)
      JPanel header = new JPanel(null); // 레이아웃 없는 패널 생성
      header.setBounds(0, 0, 500, 70); // 0,0 위치에 너비 400, 높이 100 크기
      header.setBackground(Color.WHITE); // 배경은 흰색
      header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230))); // 아래쪽에만 실선 테두리 추가
      add(header); // 메인 프레임에 추가

      // 뒤로가기 버튼 만들기
      JButton backBtn = new JButton();
      if (backIcon != null) {
          backBtn.setIcon(backIcon); // 이미지 설정
      } else {
          backBtn.setText("◀"); // 이미지 로드 실패 시 대체 텍스트
      }
      
      backBtn.setBounds(15, 2, 80, 80); // 위치와 크기 조절
      backBtn.setBackground(Color.WHITE);
      backBtn.setBorderPainted(false);     // 외곽선 제거
      backBtn.setContentAreaFilled(false); // 배경색 제거
      backBtn.setFocusPainted(false);      // 포커스 표시 제거
      backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
      
      backBtn.addActionListener(e -> {
         FrameBase.getInstance(new LineSelect());
      });
      header.add(backBtn);

      // 제목 라벨 (위치 살짝 조정)
      JLabel titleLabel = new JLabel(lineName);
      titleLabel.setFont(loadfont.Freesentation9Black.deriveFont(38f));
      titleLabel.setForeground(lineColor);
      titleLabel.setBounds(80, 21, 200, 50); 
      header.add(titleLabel);
      // [2] 입력창들이 담길 하얀색 카드 패널
      JPanel inputCard = new JPanel(null); // 자유 배치를 위한 패널
      inputCard.setBounds(40, 90, 398, 180); // 중앙 상단쯤에 배치
      inputCard.setBackground(Color.WHITE); // 배경 흰색
      inputCard.setBorder(BorderFactory.createLineBorder(new Color(235, 235, 235), 1)); // 얇은 회색 테두리
      add(inputCard); // 메인 프레임에 추가

      // 출발역 입력 묶음(라벨+입력창) 생성
      startField = createInputGroup(inputCard, "📍 타는 곳 (출발역)", 20);

      // [스왑 버튼] 출발지와 목적지를 맞바꾸는 버튼
      JButton swapBtn = new JButton("⇅"); // 상하 화살표 문구
      swapBtn.setBounds(295, 75, 80, 50); // 입력창 사이 우측에 배치
      swapBtn.setFont(new Font("Monospaced", Font.BOLD, 28)); // 등폭 글꼴로 기호 가독성 확보
      swapBtn.setForeground(Color.GRAY); // 연회색 글자
      swapBtn.setContentAreaFilled(false); // 버튼 내부 배경색 채우기 제거 (투명버튼)
      swapBtn.setBorder(null); // 테두리 제거
      swapBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 손가락 커서 적용
      swapBtn.addActionListener(e -> { // 버튼 클릭 시 동작 정의
         String temp = startField.getText(); // 현재 출발역 내용을 temp라는 임시 그릇에 담습니다.
         startField.setText(endField.getText()); // 도착역 내용을 출발역 칸에 씁니다.
         endField.setText(temp); // 아까 담아둔 출발역 내용을 도착역 칸에 씁니다.
      });
      inputCard.add(swapBtn); // 카드 패널에 스왑 버튼 추가

      // 도착역 입력 묶음 생성
      endField = createInputGroup(inputCard, "🏁 내리는 곳 (도착역)", 95);

      // [3] 하단 실행 버튼 (경로 검색 버튼)
      JButton searchBtn = new JButton("경로 검색"); // 버튼 문구
      searchBtn.setFont(loadfont.Freesentation9Black.deriveFont(16f)); // 볼드체 16포인트
      searchBtn.setBounds(40, 285, 398, 55); // 카드 아래쪽에 큼직하게 배치
      searchBtn.setBackground(lineColor); // 버튼 색상은 해당 호선의 색상으로 강조
      searchBtn.setForeground(Color.WHITE); // 글자 색상은 흰색
      searchBtn.setFocusPainted(false); // 점선 테두리 제거
      searchBtn.setBorder(null); // 테두리 제거
      searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 손가락 커서
      searchBtn.addActionListener(e -> handleSearch()); // 클릭 시 검색 로직 실행 메서드 호출
      add(searchBtn); // 메인 프레임에 추가

      // [4] 최근 검색 기록 섹션
      JLabel listHeader = new JLabel("최근 검색 기록"); // 안내 문구 라벨
      listHeader.setFont(loadfont.Freesentation9Black.deriveFont(15f)); // 15포인트 볼드체
      listHeader.setBounds(45, 350, 200, 25); // 제목 아래 위치 설정
      add(listHeader); // 메인 프레임에 추가

      // 기록 항목들이 차곡차곡 쌓일 패널
      listContainer = new JPanel(); // 패널 생성
      listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS)); // 안쪽 요소들을 세로(Y축) 방향으로 쌓음
      listContainer.setBackground(Color.WHITE); // 배경 흰색

      // 검색 기록 패널을 감싸는 스크롤 패널 (기록이 많아지면 스크롤바 생성)
      JScrollPane scrollPane = new JScrollPane(listContainer); // 바구니를 스크롤 패널에 넣음
      scrollPane.setBounds(40, 380, 398, 170); // 리스트 영역 위치 설정
      scrollPane.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240))); // 연한 테두리
      scrollPane.getViewport().setBackground(Color.WHITE); // 스크롤 안쪽 배경 흰색
      add(scrollPane); // 메인 프레임에 추가

      updateListDisplay(); // 처음 앱이 켜질 때 저장된 기록이 있으면 보여줌
      // 하단바
      JPanel footer = Share.BottomMenu.addFooterBar(this);
      footer.setBounds(0,591, 484, 70);
      add(footer);

      repaint(); // 화면 다시 그리기
   }

   /**
    * 설명 라벨과 입력용 텍스트 필드를 세트로 만들어 지정된 패널에 추가합니다.
    */
   private JTextField createInputGroup(JPanel container, String labelText, int yPos) {
      JLabel label = new JLabel(labelText); // 설명 라벨 (예: "📍 타는 곳")
      label.setFont(loadfont.Freesentation9Black.deriveFont(13f)); // 중간 두께 서체 적용
      label.setForeground(new Color(120, 120, 120)); // 진한 회색 글자
      label.setBounds(15, yPos, 200, 20); // 위치
      container.add(label); // 카드 패널에 추가

      JTextField textField = new JTextField(); // 실제 글자를 치는 칸 생성
      textField.setFont(loadfont.Freesentation9Black.deriveFont(16f)); // 큰 폰트 적용
      textField.setBounds(15, yPos + 22, 270, 40); // 라벨 바로 아래에 배치
      // 입력창 테두리 설정: 겉에는 연한 선을 긋고, 안쪽에는 10픽셀만큼의 여백(Padding)을 줍니다.
      textField.setBorder(
            BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                  BorderFactory.createEmptyBorder(0, 10, 0, 10)));
      container.add(textField); // 카드 패널에 추가

      setupAutoComplete(textField); // 이 입력창에 자동완성 마법을 걸어줍니다.
      return textField; // 완성된 입력창을 밖으로 돌려줍니다.
   }

   /**
    * 사용자가 글자를 칠 때마다 실시간으로 역 이름을 추천해 주는 자동완성 기능을 설정합니다.
    */
   private void setupAutoComplete(JTextField textField) {
      JPopupMenu menu = new JPopupMenu(); // 추천 리스트가 나타날 팝업창 바구니입니다.
      menu.setFocusable(false); // 팝업창이 뜨더라도 키보드 입력 권한은 여전히 입력창에 남겨둡니다 (중요!)
      menu.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220))); // 추천창 테두리

      // 실시간으로 메뉴를 보여주는 작업 내용을 정의합니다.
      Runnable showMenuTask = () -> {
         int foundCount = 0; // 몇 개의 추천 역을 찾았는지 세는 변수입니다.
         String input = textField.getText().trim(); // 입력창에 써진 글자를 가져와 앞뒤 공백을 지웁니다.
         if (input.isEmpty()) {
            menu.setVisible(false);
            return;
         } // 칸이 비어있으면 추천창을 닫고 종료합니다.

         menu.removeAll(); // 새로 검색하기 위해 이전 추천 목록을 싹 비웁니다.

         // 모든 역 데이터(stationData)를 하나하나 살펴봅니다.
         for (String station : stationData) {
            // [수정된 로직] 역 이름에 내 글자가 포함되어 있고 && 현재 내 글자와 완전히 똑같지는 않을 때만 추천합니다.
            if (matchStation(station, input) && !station.equals(input)) {
               addMenuItem(textField, menu, station); // 팝업창에 이 역 이름을 메뉴로 추가합니다.
               foundCount++; // 찾은 개수를 하나 올립니다.
            }
            if (foundCount >= 8)
               break; // 추천 목록이 너무 길어지지 않게 8개만 찾으면 그만 찾습니다.
         }

         // 하나라도 찾은 게 있다면 추천창을 화면에 보여줍니다.
         if (foundCount > 0) {
            if (!menu.isVisible()) { // 아직 창이 안 떠있다면
               menu.show(textField, 0, textField.getHeight()); // 입력창 바로 아래(높이만큼 내려서) 띄웁니다.
            } else {
               menu.pack(); // 이미 떠있다면 크기만 다시 맞춥니다.
            }
            textField.requestFocusInWindow(); // 추천창이 떠도 포커스는 입력창에 남깁니다.
         } else {
            menu.setVisible(false); // 찾은 게 없으면 창을 숨깁니다.
         }
      };

      // 입력창에 글자가 추가/삭제/변경되는 모든 순간을 감시하는 센서를 답니다.
      textField.getDocument().addDocumentListener(new DocumentListener() {
         public void insertUpdate(DocumentEvent e) {
            SwingUtilities.invokeLater(showMenuTask);
         } // 글자 쳐질 때 실행

         public void removeUpdate(DocumentEvent e) {
            SwingUtilities.invokeLater(showMenuTask);
         } // 글자 지워질 때 실행

         public void changedUpdate(DocumentEvent e) {
            SwingUtilities.invokeLater(showMenuTask);
         } // 속성 바뀔 때 실행
      });

      // 한글 조합 도중에도 자연스럽게 나타나도록 커서가 움직일 때도 추천 기능을 실행합니다.
      textField.addCaretListener(e -> SwingUtilities.invokeLater(showMenuTask));

      // 사용자가 입력창에서 엔터(Enter)를 치면 맨 위의 추천 항목을 바로 선택해 버립니다.
      textField.addActionListener(e -> {
         if (menu.isVisible() && menu.getComponentCount() > 0) { // 추천창이 열려있고 항목이 있다면
            JMenuItem firstItem = (JMenuItem) menu.getComponent(0); // 첫 번째 항목을 가져와서
            textField.setText(firstItem.getText()); // 입력창에 써버립니다.
            menu.setVisible(false); // 추천창을 닫습니다.
         }
      });
   }

   /**
    * 입력한 글자가 역 이름에 들어있는지 확인하는 도우미 메서드입니다.
    */
   private boolean matchStation(String station, String input) {
      return station.contains(input); // 문자열이 포함되어 있으면 true를 돌려줍니다.
   }

   /**
    * 자동완성 팝업 메뉴에 개별 역 이름 항목(줄)을 디자인하고 클릭 이벤트를 설정합니다.
    */
   private void addMenuItem(JTextField textField, JPopupMenu menu, String station) {
      JMenuItem item = new JMenuItem(station); // "대림" 같은 역 이름이 써진 줄을 만듭니다.
      item.setFont(loadfont.Freesentation9Black.deriveFont(14f)); // 중간 굵기 폰트 적용
      item.setBackground(Color.WHITE); // 배경 흰색
      item.setPreferredSize(new Dimension(270, 35)); // 입력창 너비와 비슷한 크기 설정

      // 이 항목(줄)을 마우스로 클릭했을 때의 동작입니다.
      item.addActionListener(e -> {
         textField.setText(station); // 클릭한 역 이름을 입력창에 넣습니다.
         addRecentSearch(station); // 최근 검색 기록에도 추가합니다.
         menu.setVisible(false); // 추천창을 닫습니다.
         // textField.requestFocusInWindow(); // (주석처리됨) 다음 입력을 위해 대기
      });

      // 마우스를 꾹 누르는 동작(Pressed)에도 반응하도록 안정성을 높입니다.
      item.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            textField.setText(station); // 역 이름 입력
            addRecentSearch(station); // 기록 추가
            menu.setVisible(false); // 추천창 닫기
            textField.requestFocusInWindow(); // 입력창에 커서 고정
         }
      });
      menu.add(item); // 완성된 항목 한 줄을 팝업 바구니에 넣습니다.
   }

   /**
    * '경로 검색' 대형 버튼을 눌렀을 때 실행되는 최종 확인 로직입니다.
    */
   private void handleSearch() {
      String s = startField.getText().trim(); // 출발역 텍스트 가져오기
      String d = endField.getText().trim(); // 도착역 텍스트 가져오기

   // 1. 출발역과 도착역이 같은지 확인 (문자열 비교는 .equals 사용)
      if (s.equals(d)) {
          JOptionPane.showMessageDialog(this, "출발역과 도착역이 같습니다. 다시 확인해주세요.");
          return; // 검색 중단
      }
      if (s.isEmpty() || d.isEmpty())
         return; // 하나라도 안 써져 있으면 아무것도 안 하고 종료합니다.

      // 실제로 이 호선에 존재하는 역인지 최종 확인합니다.
      if (!isValidStation(s) || !isValidStation(d)) {
         JOptionPane.showMessageDialog(this, "유효한 역 이름을 입력해주세요."); // 틀렸으면 경고창 띄우기
         return;
      }

      // 정상적으로 확인되면 두 역 모두 '최근 검색 기록' 저장소에 담습니다.
      addRecentSearch(s);
      addRecentSearch(d);

      // 실제 프로젝트 연동을 위한 안내 메시지를 띄웁니다.
      JOptionPane.showMessageDialog(this, s + "에서 " + d + "까지의 경로 검색을 완료했습니다.");
   }

   /**
    * 입력받은 텍스트가 실제로 우리 역 목록(stationData)에 있는지 검사합니다.
    */
   private boolean isValidStation(String name) {
      for (String s : stationData) { // 배열을 처음부터 끝까지 훑으며
         if (s.equals(name))
            return true; // 똑같은 이름이 있으면 "있어요!"(true)를 돌려줍니다.
      }
      return false; // 다 훑었는데 없으면 "없어요!"(false)를 돌려줍니다.
   }

   /**
    * 새로운 검색어를 기록 저장소(Set)에 넣고, 화면에 보이는 목록 리스트를 갱신합니다.
    */
   private void addRecentSearch(String station) {
      if (station == null || station.trim().isEmpty())
         return; // 이상한 값이면 패스합니다.
      String s = station.trim(); // 양옆 공백 제거
      recentSearches.remove(s); // 중복 방지를 위해 이미 목록에 있다면 지우고
      recentSearches.add(s); // 맨 뒤(최신)에 다시 넣습니다.
      updateListDisplay(); // 화면의 리스트 패널을 다시 그립니다.
   }

   /**
    * 최근 검색 기록 패널(`listContainer`)의 내용을 데이터에 맞춰 새로 그립니다.
    */
   private void updateListDisplay() {
      listContainer.removeAll(); // 일단 바구니를 싹 비웁니다.
      List<String> list = new ArrayList<>(recentSearches); // 정순 리스트로 변환한 뒤
      // 최신 항목이 맨 위로 오게 하기 위해 리스트를 거꾸로(역순) 훑으며 화면에 담습니다.
      for (int i = list.size() - 1; i >= 0; i--) {
         listContainer.add(createListRow(list.get(i))); // 개별 줄 디자인을 생성해서 패널에 추가합니다.
      }
      listContainer.revalidate(); // 패널의 구조가 바뀌었음을 알리고 다시 계산합니다.
      listContainer.repaint(); // 패널을 다시 깨끗하게 그립니다.
   // 추가 팁: 만약 스크롤이 자동으로 안 내려간다면 부모를 다시 그리게 합니다.
      if (listContainer.getParent() != null) {
          listContainer.getParent().revalidate();
      }
   }

   /**
    * 최근 검색 기록의 '한 줄' 디자인(아이콘 + 이름 + 삭제버튼)을 만듭니다.
    */
   private JPanel createListRow(String name) {
      JPanel row = new JPanel(new BorderLayout()); // 왼쪽, 중앙, 오른쪽 영역이 있는 레이아웃
      row.setMaximumSize(new Dimension(345, 45)); // 각 줄이 너무 뚱뚱해지지 않게 크기 고정
      row.setBackground(Color.WHITE); // 배경 흰색
      row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(250, 250, 250))); // 아래쪽 실선 테두리

      // 시계 아이콘과 역 이름이 적힌 라벨
      JLabel nameLabel = new JLabel("  🕒  " + name);
      nameLabel.setFont(loadfont.Freesentation9Black.deriveFont(14f)); // 폰트 적용
      nameLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 손가락 커서 적용

      // 기록된 역 이름을 마우스로 클릭하면, 지금 글자를 쓰고 있던 칸에 해당 이름을 쏙 넣어줍니다.
      nameLabel.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            // 출발역 칸에 커서가 있으면 출발역에, 도착역에 있으면 도착역에 글자를 넣어줍니다.
            if (startField.isFocusOwner())
               startField.setText(name);
            else if (endField.isFocusOwner())
               endField.setText(name);
            else
               startField.setText(name); // 아무 데도 없으면 그냥 출발역에 넣습니다.
         }
      });

      // 개별 기록을 삭제할 수 있는 '✕' 버튼
      JButton deleteBtn = new JButton("✕ ");
      deleteBtn.setForeground(Color.LIGHT_GRAY); // 연한 회색 글자
      deleteBtn.setBorder(null); // 테두리 제거
      deleteBtn.setContentAreaFilled(false); // 버튼 배경색 제거
      deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 손가락 커서 적용
      deleteBtn.addActionListener(e -> { // 버튼 클릭 시 동작
         recentSearches.remove(name); // 저장소에서 데이터를 지웁니다.
         updateListDisplay(); // 목록 화면을 갱신합니다.
      });

      row.add(nameLabel, BorderLayout.CENTER); // 이름을 줄의 중앙에 배치
      row.add(deleteBtn, BorderLayout.EAST); // 삭제 버튼을 줄의 오른쪽에 배치
      return row; // 완성된 한 줄 패널을 돌려줍니다.
   }

}