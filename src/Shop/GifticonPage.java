package Shop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class GifticonPage extends JFrame{

	public GifticonPage(ShopDataManager dataManager) {
		setTitle("기프티콘 보관함");
		setSize(400, 500);
		setLayout(new BorderLayout());
		setResizable(false);

		// 상단 제목
		JLabel title = new JLabel("내 기프티콘", SwingConstants.CENTER);
		title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
		add(title, BorderLayout.NORTH);

		// 내역 리스트 패널
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setBackground(Color.WHITE);

		Map<String, Integer> orderHistory = dataManager.getOrderHistory();
		Set<String> completedOrders = dataManager.getCompletedOrders();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
		String currentTime = LocalDateTime.now().format(formatter); 

		for (Map.Entry<String, Integer> entry : orderHistory.entrySet()) {
			String itemName = entry.getKey();
			int qty = entry.getValue();

			JPanel rowPanel = new JPanel(new BorderLayout(10, 10));
			rowPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
					BorderFactory.createEmptyBorder(10, 10, 10, 10)));
			rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

			// [중앙] 정보 영역
			JPanel infoPanel = new JPanel();
			infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
			infoPanel.setOpaque(false);

			JLabel dateLabel = new JLabel("구매 일시: " + currentTime);
			dateLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
			dateLabel.setForeground(Color.GRAY);

			JLabel nameLabel = new JLabel(itemName + " : " + qty + "개");
			nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));

			infoPanel.add(dateLabel);
			infoPanel.add(Box.createVerticalStrut(5));
			infoPanel.add(nameLabel);
			rowPanel.add(infoPanel, BorderLayout.CENTER);

			// [우측] 버튼 영역
			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
			btnPanel.setOpaque(false);

			// 💡 이미 사용(구매 완료)된 기프티콘인지 확인
			if (completedOrders.contains(itemName)) {
				JLabel usedLabel = new JLabel("사용 완료");
				usedLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
				usedLabel.setForeground(Color.GRAY);
				btnPanel.add(usedLabel);
			} else {
				JButton useBtn = new JButton("구매 완료"); // 기프티콘 사용 버튼
				useBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
				
				useBtn.addActionListener(e -> {
					int result = JOptionPane.showConfirmDialog(this, "[" + itemName + "] 기프티콘을 사용하시겠습니까?", "사용 확인", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						// 💡 상태를 '완료'로 변경!
						completedOrders.add(itemName);
						
						JOptionPane.showMessageDialog(this, "기프티콘 사용이 완료되었습니다!");
						
						// 화면 새로고침
						listPanel.removeAll();
						dispose(); // 창을 닫고 다시 열어서 갱신 (가장 간단한 갱신 방법)
						new GifticonPage(dataManager);
					}
				});
				btnPanel.add(useBtn);
			}
			rowPanel.add(btnPanel, BorderLayout.EAST);
			listPanel.add(rowPanel);
		}

		JScrollPane scrollPane = new JScrollPane(listPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane, BorderLayout.CENTER);

		JButton closeBtn = new JButton("닫기");
		closeBtn.addActionListener(e -> dispose());
		add(closeBtn, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	
}
