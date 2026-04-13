package Frame; // 사용 중이신 패키지명에 맞게 수정하세요.

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import Font.loadfont;

public class subwayBase extends JPanel {
    protected String[] stationData;
    protected Color lineColor;
    protected String lineName;

    private Set<String> recentSearches;
    private static Map<String, Set<String>> globalSearchHistory = new HashMap<>();
    private JPanel listContainer;
    protected JTextField startField;
    protected JTextField endField;
   
    private ImageIcon backIcon;
    private ImageIcon listIcon;

    public subwayBase(String lineName, String[] stationData, Color lineColor) {
        this.lineName = lineName;
        this.stationData = stationData;
        this.lineColor = lineColor;
      
        try {
            // 뒤로가기 아이콘 비율 유지 (20x35)
            URL imgUrl = getClass().getResource("/reply_18032509.png"); 
            if (imgUrl != null) {
                Image img = ImageIO.read(imgUrl);
                backIcon = new ImageIcon(img.getScaledInstance(20, 35, Image.SCALE_SMOOTH));
            }
          
            // 리스트 버튼 아이콘
            URL listUrl = getClass().getResource("/list_icon.png"); 
            if (listUrl != null) {
                Image lImg = ImageIO.read(listUrl);
                listIcon = new ImageIcon(lImg.getScaledInstance(20, 20, Image.SCALE_SMOOTH));
            }
        } catch (IOException e) { e.printStackTrace(); }
      
        if (!globalSearchHistory.containsKey(lineName)) {
            globalSearchHistory.put(lineName, new LinkedHashSet<>());
        }
        this.recentSearches = globalSearchHistory.get(lineName);

        // FrameBase의 타이틀바(30px)를 뺀 높이 670과 폭 410에 맞춤
        setSize(410, 670); 
        setLayout(null);
        setBackground(new Color(250, 250, 250));
        initUI();
    }

    private void initUI() {
        // [1] 헤더 패널 (너비 410)
        JPanel header = new JPanel(null);
        header.setBounds(0, 0, 410, 75); 
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        add(header);

        JButton backBtn = new JButton();
        if (backIcon != null) backBtn.setIcon(backIcon);
        else backBtn.setText("◀");
        
        backBtn.setBounds(15, 15, 45, 45); 
        backBtn.setBackground(Color.WHITE);
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> FrameBase.getInstance(new LineSelect()));
        header.add(backBtn);

        JLabel titleLabel = new JLabel(lineName);
        titleLabel.setFont(loadfont.Freesentation9Black.deriveFont(32f));
        titleLabel.setForeground(lineColor);
        titleLabel.setBounds(70, 15, 200, 45); 
        header.add(titleLabel);
      
        // [2] 입력 카드 (너비 410에 맞춰 370으로 재조정)
        JPanel inputCard = new JPanel(null);
        inputCard.setBounds(20, 95, 370, 180); 
        inputCard.setBackground(Color.WHITE);
        inputCard.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        add(inputCard);

        startField = createInputGroup(inputCard, "📍 타는 곳 (출발역)", 20);

        // 스왑 버튼 (입력 카드 너비에 비례해 위치 이동)
        JButton swapBtn = new JButton("⇅");
        swapBtn.setBounds(315, 75, 40, 40); 
        swapBtn.setFont(new Font("Monospaced", Font.BOLD, 24));
        swapBtn.setForeground(Color.GRAY);
        swapBtn.setContentAreaFilled(false);
        swapBtn.setBorder(null);
        swapBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        swapBtn.addActionListener(e -> {
            String temp = startField.getText();
            startField.setText(endField.getText());
            endField.setText(temp);
        });
        inputCard.add(swapBtn);

        endField = createInputGroup(inputCard, "🏁 내리는 곳 (도착역)", 95);

        // [3] 하단 경로 검색 버튼 (너비 370)
        JButton searchBtn = new JButton("경로 검색");
        searchBtn.setFont(loadfont.Freesentation9Black.deriveFont(18f));
        searchBtn.setBounds(20, 290, 370, 55); 
        searchBtn.setBackground(lineColor);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorder(null);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> handleSearch());
        add(searchBtn);

        // [4] 최근 검색 기록 섹션
        JLabel listHeader = new JLabel("최근 검색 기록");
        listHeader.setFont(loadfont.Freesentation9Black.deriveFont(15f));
        listHeader.setBounds(25, 365, 200, 25);
        add(listHeader);

        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBounds(20, 395, 370, 190); 
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        add(scrollPane);

        updateListDisplay();
      
        // 하단바 (타이틀 바 높이를 감안하여 Y좌표를 600으로 맞춤, 총합 670)
        JPanel footer = Share.BottomMenu.addFooterBar(this);
        footer.setBounds(0, 600, 410, 70);
        add(footer);

        repaint();
    }

    private JTextField createInputGroup(JPanel container, String labelText, int yPos) {
        JLabel label = new JLabel(labelText);
        label.setFont(loadfont.Freesentation9Black.deriveFont(13f));
        label.setForeground(new Color(120, 120, 120));
        label.setBounds(15, yPos, 200, 20);
        container.add(label);

        JTextField textField = new JTextField();
        textField.setFont(loadfont.Freesentation9Black.deriveFont(16f));
        // 입력 카드 너비가 370으로 늘어났으므로 텍스트 필드 너비도 280으로 연장
        textField.setBounds(15, yPos + 22, 280, 42); 
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        container.add(textField);

        JButton listBtn = new JButton();
        if (listIcon != null) listBtn.setIcon(listIcon);
        else listBtn.setText("≡");
        
        // 텍스트 필드 우측에 알맞게 리스트 버튼 배치
        listBtn.setBounds(305, yPos + 22, 40, 42); 
        listBtn.setContentAreaFilled(false);
        listBtn.setBorder(null);
        listBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        listBtn.setFocusPainted(false);
        listBtn.addActionListener(e -> showStationListModal(textField));
        container.add(listBtn);

        setupAutoComplete(textField);
        return textField;
    }

    private void showStationListModal(JTextField targetField) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, "", Dialog.ModalityType.APPLICATION_MODAL);
        
        dialog.setSize(320, 480); 
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0)); 
        dialog.setLocationRelativeTo(parentWindow);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true)); 

        JPanel customHeader = new JPanel(new BorderLayout());
        customHeader.setBackground(Color.WHITE);
        customHeader.setPreferredSize(new Dimension(320, 50));
        customHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        JLabel titleLabel = new JLabel(lineName + " 전체 역", SwingConstants.CENTER);
        titleLabel.setFont(loadfont.Freesentation9Black.deriveFont(18f));
        titleLabel.setForeground(new Color(50, 50, 50));
        customHeader.add(titleLabel, BorderLayout.CENTER);

        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        closeBtn.setForeground(Color.GRAY);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dialog.dispose());
        customHeader.add(closeBtn, BorderLayout.EAST);

        mainPanel.add(customHeader, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        for (String station : stationData) {
            JButton itemBtn = new JButton("  " + station);
            itemBtn.setMaximumSize(new Dimension(320, 50));
            itemBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemBtn.setHorizontalAlignment(SwingConstants.LEFT);
            itemBtn.setFont(loadfont.Freesentation9Black.deriveFont(16f));
            itemBtn.setBackground(Color.WHITE);
            itemBtn.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(245, 245, 245)));
            itemBtn.setFocusPainted(false);
            itemBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            itemBtn.addActionListener(e -> {
                targetField.setText(station);
                addRecentSearch(station);
                dialog.dispose();
            });
            listPanel.add(itemBtn);
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void setupAutoComplete(JTextField textField) {
        JPopupMenu menu = new JPopupMenu();
        menu.setFocusable(false);
        menu.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        Runnable showMenuTask = () -> {
            int foundCount = 0;
            String input = textField.getText().trim();
            if (input.isEmpty()) {
                menu.setVisible(false);
                return;
            }

            menu.removeAll();

            for (String station : stationData) {
                if (matchStation(station, input) && !station.equals(input)) {
                    addMenuItem(textField, menu, station);
                    foundCount++;
                }
                if (foundCount >= 8) break;
            }

            if (foundCount > 0) {
                if (!menu.isVisible()) {
                    menu.show(textField, 0, textField.getHeight());
                } else {
                    menu.pack();
                }
                textField.requestFocusInWindow();
            } else {
                menu.setVisible(false);
            }
        };

        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { SwingUtilities.invokeLater(showMenuTask); }
            public void removeUpdate(DocumentEvent e) { SwingUtilities.invokeLater(showMenuTask); }
            public void changedUpdate(DocumentEvent e) { SwingUtilities.invokeLater(showMenuTask); }
        });

        textField.addCaretListener(e -> SwingUtilities.invokeLater(showMenuTask));

        textField.addActionListener(e -> {
            if (menu.isVisible() && menu.getComponentCount() > 0) {
                JMenuItem firstItem = (JMenuItem) menu.getComponent(0);
                textField.setText(firstItem.getText());
                menu.setVisible(false);
            }
        });
    }

    private boolean matchStation(String station, String input) {
        return station.contains(input);
    }

    private void addMenuItem(JTextField textField, JPopupMenu menu, String station) {
        JMenuItem item = new JMenuItem(station);
        item.setFont(loadfont.Freesentation9Black.deriveFont(14f));
        item.setBackground(Color.WHITE);
        item.setPreferredSize(new Dimension(280, 35));

        item.addActionListener(e -> {
            textField.setText(station);
            addRecentSearch(station);
            menu.setVisible(false);
        });

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                textField.setText(station);
                addRecentSearch(station);
                menu.setVisible(false);
                textField.requestFocusInWindow();
            }
        });
        menu.add(item);
    }

    private void handleSearch() {
        String s = startField.getText().trim();
        String d = endField.getText().trim();

        if (s.equals(d)) {
            JOptionPane.showMessageDialog(this, "출발역과 도착역이 같습니다. 다시 확인해주세요.");
            return;
        }
        if (s.isEmpty() || d.isEmpty()) return;

        if (!isValidStation(s) || !isValidStation(d)) {
            JOptionPane.showMessageDialog(this, "유효한 역 이름을 입력해주세요.");
            return;
        }

        addRecentSearch(s);
        addRecentSearch(d);

        JOptionPane.showMessageDialog(this, s + "에서 " + d + "까지의 경로 검색을 완료했습니다.");
    }

    private boolean isValidStation(String name) {
        for (String s : stationData) {
            if (s.equals(name)) return true;
        }
        return false;
    }

    private void addRecentSearch(String station) {
        if (station == null || station.trim().isEmpty()) return;
        String s = station.trim();
        recentSearches.remove(s);
        recentSearches.add(s);
        updateListDisplay();
    }

    private void updateListDisplay() {
        listContainer.removeAll();
        List<String> list = new ArrayList<>(recentSearches);
        for (int i = list.size() - 1; i >= 0; i--) {
            listContainer.add(createListRow(list.get(i)));
        }
        listContainer.revalidate();
        listContainer.repaint();
        if (listContainer.getParent() != null) {
            listContainer.getParent().revalidate();
        }
    }

    private JPanel createListRow(String name) {
        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(370, 45)); 
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(245, 245, 245)));

        JLabel nameLabel = new JLabel("  🕒  " + name);
        nameLabel.setFont(loadfont.Freesentation9Black.deriveFont(14f));
        nameLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        nameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (startField.isFocusOwner()) startField.setText(name);
                else if (endField.isFocusOwner()) endField.setText(name);
                else startField.setText(name);
            }
        });

        JButton deleteBtn = new JButton("✕ ");
        deleteBtn.setForeground(Color.LIGHT_GRAY);
        deleteBtn.setBorder(null);
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> {
            recentSearches.remove(name);
            updateListDisplay();
        });

        row.add(nameLabel, BorderLayout.CENTER);
        row.add(deleteBtn, BorderLayout.EAST);
        return row;
    }
}