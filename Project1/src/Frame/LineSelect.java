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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import Font.loadfont;
import Lines.subwayApp1;
import Lines.subwayApp2;
import Lines.subwayApp3;
import Lines.subwayApp4;
import Lines.subwayApp5;
import Lines.subwayApp6;
import Lines.subwayApp7;
import Lines.subwayApp8;
import Lines.subwayApp9;

public class LineSelect extends JPanel {

   public LineSelect() {

      // 1. 프레임 설정
      setSize(500, 700);
      setLayout(new BorderLayout());
      setBackground(new Color(255, 253, 240)); // 포근한 연노란색 일기장 종이색

      // --- 상단 타이틀 (일기장 제목 느낌) ---
      JPanel headerPanel = new JPanel() {
         @Override
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 제목 칸 (크레파스 테두리 느낌)
            g2.setColor(new Color(255, 255, 255, 180));
            g2.fillRoundRect(30, 25, getWidth() - 60, 50, 15, 15);
            g2.setColor(new Color(180, 180, 180));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawRoundRect(30, 25, getWidth() - 60, 50, 15, 15);
            g2.dispose();
         }
      };

      headerPanel.setOpaque(false);
      headerPanel.setPreferredSize(new Dimension(430, 100));
      headerPanel.setLayout(new BorderLayout());

      JLabel lblTitle = new JLabel("오늘 탈 호선을 선택해요", JLabel.CENTER);
      lblTitle.setFont(loadfont.Freesentation9Black.deriveFont(30f));
      lblTitle.setForeground(new Color(70, 70, 70));
      headerPanel.add(lblTitle, BorderLayout.CENTER);
      add(headerPanel, BorderLayout.NORTH);

      // --- 중앙 버튼 영역 (배경 낙서 포함) ---
      DoodlePanel linePanel = new DoodlePanel();
      linePanel.setLayout(new GridLayout(subwayLines.length, 1, 0, 15));
      linePanel.setBorder(new EmptyBorder(20, 35, 100, 35)); // 하단 낙서 공간 확보
      

      for (int i = 0; i < subwayLines.length; i++) {
         String info = (String) subwayLines[i][0];
         Color color = (Color) subwayLines[i][1];

         RoundedButton linebtn = new RoundedButton(info);
         linebtn.setBackground(color);
         linebtn.setFont(loadfont.Freesentation9Black.deriveFont(18F));
         linebtn.setPreferredSize(new Dimension(300, 65));

         linebtn.addActionListener(e -> {
            System.out.println(info + "을(를) 선택하셨습니다.");
            JPanel nextPanel = null;
       
            // 2. 선택한 호선에 맞는 팀원의 상세 창 띄우기
            // info 문자열에서 숫자만 추출하거나 인덱스(i)를 활용합니다.
            switch (info.substring(0, 2)) {
            case "1호": nextPanel = new Lines.subwayApp1(); break;
            case "2호": nextPanel = new Lines.subwayApp2(); break;
            case "3호": nextPanel = new Lines.subwayApp3(); break;
            case "4호": nextPanel = new Lines.subwayApp4(); break;
            case "5호": nextPanel = new Lines.subwayApp5(); break;
            case "6호": nextPanel = new Lines.subwayApp6(); break;
            case "7호": nextPanel = new Lines.subwayApp7(); break;
            case "8호": nextPanel = new Lines.subwayApp8(); break;
            case "9호": nextPanel = new Lines.subwayApp9(); break;
            default:
               System.out.println("해당 호선의 상세 페이지가 없습니다.");
            }
            if (nextPanel != null) {
                // FrameBase의 싱글톤 인스턴스를 사용하여 패널만 교체합니다.
                FrameBase.getInstance(nextPanel);
            }

         });

         linePanel.add(linebtn);
      }

      

      // 스크롤 설정
      JScrollPane scroll = new JScrollPane(linePanel);
      scroll.setBorder(null);
      scroll.setOpaque(false);
      scroll.getViewport().setOpaque(false);
      scroll.getVerticalScrollBar().setUnitIncrement(25);

      add(scroll, BorderLayout.CENTER);

      // 하단바
      JPanel footer = Share.BottomMenu.addFooterBar(this);
      add(footer, BorderLayout.SOUTH);
      
      revalidate();
      repaint(); // 화면 다시 그리기
   }

   // 데이터 (진짜 호선 색상)
   Object[][] subwayLines = { { "1호선 (소요산 ↔ 인천/신창)", new Color(0, 82, 164) },
         { "2호선 (시청 ↔ 성수 - 순환)", new Color(51, 167, 81) }, { "3호선 (대화 ↔ 오금)", new Color(243, 117, 34) },
         { "4호선 (당고개 ↔ 오이도)", new Color(0, 162, 227) }, { "5호선 (방화 ↔ 상일동/마천)", new Color(160, 94, 181) },
         { "6호선 (응암 ↔ 신내)", new Color(213, 112, 45) }, { "7호선 (장암 ↔ 석남)", new Color(105, 121, 52) },
         { "8호선 (암사 ↔ 모란)", new Color(230, 30, 123) }, { "9호선 (개화 ↔ 중앙보훈병원)", new Color(189, 178, 147) } };

   // --- ⭐ 낙서를 그리는 배경 패널 ---
   class DoodlePanel extends JPanel {
      public DoodlePanel() {
         setOpaque(false);
      }

      @Override
      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D) g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

         int w = getWidth();
         int h = getHeight();

         // 1. 모눈종이 배경
         g2.setColor(new Color(235, 230, 210));
         for (int i = 0; i < w; i += 30)
            g2.drawLine(i, 0, i, h);
         for (int j = 0; j < h; j += 30)
            g2.drawLine(0, j, w, j);

         // 2. 귀여운 낙서 (투명도 조절)
         g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));

         // 하단 칙칙폭폭 기차 (단순한 도형으로 귀엽게)
         int trainY = h - 80;
         g2.setColor(new Color(255, 100, 100)); // 빨간 기차
         g2.fillRoundRect(50, trainY, 60, 40, 10, 10);
         g2.setColor(new Color(100, 150, 255)); // 파란 기차
         g2.fillRoundRect(115, trainY, 60, 40, 10, 10);

         g2.setColor(new Color(80, 80, 80)); // 바퀴
         g2.fillOval(60, trainY + 35, 15, 15);
         g2.fillOval(85, trainY + 35, 15, 15);
         g2.fillOval(125, trainY + 35, 15, 15);
         g2.fillOval(150, trainY + 35, 15, 15);

         // 뭉게구름
         g2.setColor(Color.WHITE);
         g2.fillOval(w - 120, trainY - 50, 40, 30);
         g2.fillOval(w - 100, trainY - 65, 50, 40);
         g2.fillOval(w - 70, trainY - 50, 40, 30);

         g2.dispose();
      }

   }

   // --- 둥근 버튼 (입체감 있는 크레파스 느낌) ---
   class RoundedButton extends JButton {
      public RoundedButton(String label) {
         super(label);
         setContentAreaFilled(false);
         setFocusPainted(false);
         setBorderPainted(false);
         setForeground(Color.WHITE);
         setCursor(new Cursor(Cursor.HAND_CURSOR));
      }

      @Override
      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D) g.create();
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

         // 버튼 그림자
         g2.setColor(new Color(0, 0, 0, 40));
         g2.fillRoundRect(2, 4, getWidth() - 2, getHeight() - 4, 25, 25);

         // 마우스 반응
         if (getModel().isPressed())
            g2.setColor(getBackground().darker());
         else if (getModel().isRollover())
            g2.setColor(getBackground().brighter());
         else
            g2.setColor(getBackground());

         g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 4, 25, 25);

         g2.dispose();
         super.paintComponent(g);
      }

   }

   
}