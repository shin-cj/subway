package Frame;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import Font.loadfont;


/**
 * LineSelect: 앱 실행 시 호선을 선택하는 메인 화면 패널입니다.
 */
public class LineSelect extends JPanel {

   // 지하철 노선 데이터 (이름 및 공식 색상)
   private Object[][] subwayLines = { 
      { "1호선 (소요산 ↔ 인천/신창)", new Color(0, 82, 164) },
      { "2호선 (시청 ↔ 성수 - 순환)", new Color(51, 167, 81) }, 
      { "3호선 (대화 ↔ 오금)", new Color(243, 117, 34) },
      { "4호선 (당고개 ↔ 오이도)", new Color(0, 162, 227) }, 
      { "5호선 (방화 ↔ 상일동/마천)", new Color(153, 108, 172) },
      { "6호선 (응암 ↔ 신내)", new Color(205, 124, 47) }, 
      { "7호선 (장암 ↔ 석남)", new Color(116, 127, 0) },
      { "8호선 (암사 ↔ 모란)", new Color(230, 30, 123) }, 
      { "9호선 (개화 ↔ 중앙보훈병원)", new Color(189, 176, 146) } 
   };

   public LineSelect() {
      // [수정] FrameBase의 410px 너비와 상단바를 제외한 높이(670)에 맞춰 패널 크기 설정
      setSize(410, 670); 
      setLayout(new BorderLayout());
      setBackground(new Color(255, 253, 240)); 

      // 2. 상단 타이틀 영역
      JPanel headerPanel = new JPanel() {
         @Override
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // [수정] 410px 너비에 맞춰 제목 박스 여백(25) 및 크기 조정
            g2.setColor(new Color(255, 255, 255, 180));
            g2.fillRoundRect(25, 20, getWidth() - 50, 50, 15, 15);
            g2.setColor(new Color(180, 180, 180));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(25, 20, getWidth() - 50, 50, 15, 15);
            g2.dispose();
         }
      };

      headerPanel.setOpaque(false);
      headerPanel.setPreferredSize(new Dimension(410, 90)); // [수정] 너비 410 적용
      headerPanel.setLayout(new BorderLayout());

      JLabel lblTitle = new JLabel("오늘 탈 호선을 선택해요", JLabel.CENTER);
      lblTitle.setFont(loadfont.Freesentation9Black.deriveFont(26f)); // [수정] 가독성을 위해 폰트 크기 살짝 조정
      lblTitle.setForeground(new Color(70, 70, 70));
      headerPanel.add(lblTitle, BorderLayout.CENTER);
      add(headerPanel, BorderLayout.NORTH);

      // 3. 중앙 버튼 리스트 영역
      DoodlePanel lineContainer = new DoodlePanel();
      lineContainer.setLayout(new GridLayout(subwayLines.length, 1, 0, 12));
      lineContainer.setBorder(new EmptyBorder(15, 30, 80, 30)); // [수정] 좌우 여백을 30px로 설정 (410 - 60 = 350 너비 확보)

      for (int i = 0; i < subwayLines.length; i++) {
         String info = (String) subwayLines[i][0];
         Color color = (Color) subwayLines[i][1];

         RoundedButton linebtn = new RoundedButton(info);
         linebtn.setBackground(color);
         linebtn.setFont(loadfont.Freesentation9Black.deriveFont(17F));
         linebtn.setPreferredSize(new Dimension(350, 60)); // [수정] 여백을 제외한 가득 찬 너비 350 설정

         linebtn.addActionListener(e -> {
            JPanel nextPanel = null;
            String prefix = info.substring(0, 2);
            switch (prefix) {
               case "1호": nextPanel = new Lines.subwayApp1(); break;
               case "2호": nextPanel = new Lines.subwayApp2(); break;
               case "3호": nextPanel = new Lines.subwayApp3(); break;
               case "4호": nextPanel = new Lines.subwayApp4(); break;
               case "5호": nextPanel = new Lines.subwayApp5(); break;
               case "6호": nextPanel = new Lines.subwayApp6(); break;
               case "7호": nextPanel = new Lines.subwayApp7(); break;
               case "8호": nextPanel = new Lines.subwayApp8(); break;
               case "9호": nextPanel = new Lines.subwayApp9(); break;
            }
            if (nextPanel != null) {
                FrameBase.getInstance(nextPanel);
            }
         });
         lineContainer.add(linebtn);
      }

      // 4. 스크롤 설정
      JScrollPane scroll = new JScrollPane(lineContainer);
      scroll.setBorder(null);
      scroll.setOpaque(false);
      scroll.getViewport().setOpaque(false);
      scroll.getVerticalScrollBar().setUnitIncrement(25);
      scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      
      // [추가] 모던 스크롤바 디자인 및 너비 설정 적용
      scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
      scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

      add(scroll, BorderLayout.CENTER);

      // 5. 하단바 추가
      JPanel footer = Share.BottomMenu.addFooterBar(this);
      add(footer, BorderLayout.SOUTH);
      
      revalidate();
      repaint();
   }

   // --- 낙서를 그리는 배경 패널 ---
   class DoodlePanel extends JPanel {
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
}