package Shop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import Frame.FrameBase;
import Frame.ModernScrollBarUI;
import MyPage.MyPage;
import Share.UserInfo;

public class Shop extends JPanel {
	private ShopDataManager dataManager = ShopDataManager.getInstance();
	private JLabel myMoneLabel;
	private JPanel listPanel;
	private String selectedCategory;
	
	
	// 추가된 전역 변수: 상점 DB와 잔액 표시 라벨
	private Map<String, Integer> shopDB = new LinkedHashMap<>();
	private JLabel myMoneyLabel;

	private CartDetail cartDetailWindow = null;
	private OrderDetail orderDetailWindow = null;
	
	public Shop(String category) {
		this.selectedCategory = category;
		
		setLayout(new BorderLayout());

		// [상단] 내 잔액과 공통 버튼 배치
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		myMoneyLabel = new JLabel("내 잔액: " + dataManager.getMyPrice() + "P");
		myMoneyLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));

		JPanel topBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		JButton cartBtnTop = new JButton("장바구니");
		JButton orderHistoryBtn = new JButton("주문 내역");
		JButton backBtn = new JButton("뒤로가기");

		Insets btnMargin = new Insets(2, 5, 2, 5);
		Font btnFont = new Font("맑은 고딕", Font.BOLD, 12);

		cartBtnTop.setMargin(btnMargin);
		cartBtnTop.setFont(btnFont);
		orderHistoryBtn.setMargin(btnMargin);
		orderHistoryBtn.setFont(btnFont);
		backBtn.setMargin(btnMargin);
		backBtn.setFont(btnFont);

		topBtnPanel.add(cartBtnTop);
		topBtnPanel.add(orderHistoryBtn);
		topBtnPanel.add(backBtn);

		topPanel.add(myMoneyLabel, BorderLayout.WEST);
		topPanel.add(topBtnPanel, BorderLayout.EAST);
		add(topPanel, BorderLayout.NORTH);

		// [중앙] 상품 목록 패널
		listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

		refreshProductList();
		
		JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(listPanel, BorderLayout.NORTH); // listPanel을 북쪽에 딱 붙임
		
		JScrollPane scrollPane = new JScrollPane(wrapperPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		add(scrollPane, BorderLayout.CENTER);

		// --- [공통 버튼 이벤트] ---
		backBtn.addActionListener(e -> {
			JOptionPane.showMessageDialog(this, "이전 화면으로 돌아갑니다.");
			FrameBase.getInstance(new CategoryPage());
		});

		cartBtnTop.addActionListener(e -> {
			if (dataManager.getCart().isEmpty()) {
				JOptionPane.showMessageDialog(this, "장바구니가 비어 있습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
			} else {
				if (cartDetailWindow == null || !cartDetailWindow.isVisible()) {
					cartDetailWindow = new CartDetail(dataManager, () -> {
						myMoneyLabel.setText("내 잔액: " + dataManager.getMyPrice() + "P");
					});
				} else {
					cartDetailWindow.toFront();
				}
			}
		});

		orderHistoryBtn.addActionListener(e -> {
			if (dataManager.getOrderHistory().isEmpty()) {
				JOptionPane.showMessageDialog(this, "주문 내역이 없습니다.", "알림", JOptionPane.WARNING_MESSAGE);
			} else {
				if (orderDetailWindow == null || !orderDetailWindow.isVisible()) {
					orderDetailWindow = new OrderDetail(dataManager, () -> {
						myMoneyLabel.setText("내 잔액: " + dataManager.getMyPrice() + "P");
					});
				} else {
					orderDetailWindow.toFront();
				}
			}
		});
		add(Share.BottomMenu.addFooterBar(this), BorderLayout.SOUTH);
		setVisible(true);
	}
	// 💡 [핵심] 리스트를 새로고침하는 메서드
	private void refreshProductList() {
        listPanel.removeAll(); // 일단 다 지우기

        Map<String, ItemInfo> shopDB = dataManager.getShopDB();
        Set<String> wishItems = dataManager.getWishlist();

        // 1. 찜한 상품 먼저 그리기 (LinkedHashSet이라 누른 순서대로 나옴)
        for (String itemName : wishItems) {
            ItemInfo item = shopDB.get(itemName);
            if (item != null && (selectedCategory == null || item.getType().equals(selectedCategory))) {
                listPanel.add(createProductPanel(item));
            }
        }

        // 2. 찜하지 않은 나머지 상품 그리기
        for (Map.Entry<String, ItemInfo> entry : shopDB.entrySet()) {
            if (!wishItems.contains(entry.getKey())) {
                ItemInfo item = entry.getValue();
                // 💡 [필터링] 똑같이 타입 비교 조건 추가!
                if (selectedCategory == null || item.getType().equals(selectedCategory)) {
                    listPanel.add(createProductPanel(item));
                }
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

	// 💡 개별 상품 UI
	private JPanel createProductPanel(ItemInfo item) {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBorder(
				//메인 화면 테두리 그리는 부분
				BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
				//선 안쪽 여백		
				BorderFactory.createEmptyBorder(5, 15, 5, 15)));

		// [왼쪽] 이미지 영역
		String imgPath = item.getImgUrl();
		ImageIcon img = null;

		if (imgPath != null && !imgPath.trim().isEmpty()) {
			java.net.URL imgURL = getClass().getResource(imgPath);
			if (imgURL != null) {
				img = new ImageIcon(imgURL);
				Image scaledImg = img.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
				img = new ImageIcon(scaledImg);
			}
		}

		JLabel imageLabel;
		if (img != null) {
			imageLabel = new JLabel(img, SwingConstants.CENTER);
		} else {
			imageLabel = new JLabel("이미지 없음", SwingConstants.CENTER);
		}

		imageLabel.setPreferredSize(new Dimension(100, 100));
		imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

		// 📝 [가운데] 컨텐츠 영역
		JPanel contentPanel = new JPanel(new BorderLayout());

		// 1. 상단: [상품명/가격] + [수량 스피너]
		JPanel topDataPanel = new JPanel(new BorderLayout());

		// 1-1. 상품명/가격 (왼쪽으로 이동 및 좌측 정렬)
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		// 💡 왼쪽 여백을 살짝 주어 이미지와 너무 붙지 않게 합니다+.
		textPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

		JLabel nameLabel = new JLabel(item.getName());
		nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 15));
		nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // 💡 좌측 정렬로 변경

		JLabel priceLabel = new JLabel("가격: " + item.getPrice() + "P");
		priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // 💡 좌측 정렬로 변경

		textPanel.add(Box.createVerticalStrut(10));
		textPanel.add(nameLabel);
		textPanel.add(Box.createVerticalStrut(5));
		textPanel.add(priceLabel);

		// 1-2. 수량 스피너 (우측으로 이동)
		JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15)); // 💡 우측 정렬로 변경
		qtyPanel.add(new JLabel("수량:"));
		JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
		qtySpinner.setPreferredSize(new Dimension(50, 25));
		qtyPanel.add(qtySpinner);

		// 💡 상단 패널에 조립 (왼쪽: 텍스트, 오른쪽: 수량)
		topDataPanel.add(textPanel, BorderLayout.CENTER); // 텍스트를 Center에 두어 남는 공간 차지
		topDataPanel.add(qtyPanel, BorderLayout.EAST); // 수량을 East에 두어 우측 끝 고정

		// 2. 하단: 버튼 패널 (기존과 동일)
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		JButton wishBtn = new JButton("❤️ 찜");
		JButton cartBtn = new JButton("🛒 담기");
		JButton buyBtn = new JButton("💳 구매");

		btnPanel.add(wishBtn);
		btnPanel.add(cartBtn);
		btnPanel.add(buyBtn);

		// 컨텐츠 영역에 부착
		contentPanel.add(topDataPanel, BorderLayout.CENTER);
		contentPanel.add(btnPanel, BorderLayout.SOUTH);

		panel.add(imageLabel, BorderLayout.WEST);
		panel.add(contentPanel, BorderLayout.CENTER);

		// 💡 찜 버튼 상태 확인
        boolean isWished = dataManager.getWishlist().contains(item.getName());
        
        // 💡 찜 상태면 글자색 빨간색으로 변경
        if (isWished) {
            wishBtn.setForeground(Color.RED);
        }
		
		// --- 버튼 이벤트 (💡 수량 연동 적용) ---
		wishBtn.addActionListener(e -> {
			dataManager.toggleWishlist(item.getName());
			refreshProductList();
		});

		cartBtn.addActionListener(e -> {
			// 💡 스피너에서 사용자가 설정한 수량 가져오기
			int selectedQty = (Integer) qtySpinner.getValue();

			// 기존에 1개씩 담던 메서드 대신, 설정한 수량만큼 직접 더해줍니다.
			Map<String, Integer> cart = dataManager.getCart();
			cart.put(item.getName(), cart.getOrDefault(item.getName(), 0) + selectedQty);

			JOptionPane.showMessageDialog(this, "[" + item.getName() + "] " + selectedQty + "개를 장바구니에 담았습니다. 🛒");
			qtySpinner.setValue(1);
		});

		buyBtn.addActionListener(e -> {
			// 💡 스피너에서 사용자가 설정한 수량 가져오기
			int selectedQty = (Integer) qtySpinner.getValue();
			int currentPrice = dataManager.getMyPrice();

			// 💡 총 결제 금액 = 단가 * 선택 수량
			int totalPrice = item.getPrice() * selectedQty;

			if (currentPrice >= totalPrice) {
				dataManager.setMyPrice(currentPrice - totalPrice);
	
				// 💡 [추가] 상점 구매 지출 내역 기록,상점 구매 지출 내역에 시간 추가
				if (UserInfo.currentUser != null) {
					
					String timeStr = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MM/dd HH:mm"));
					UserInfo.currentUser.addPointHistory("[" + timeStr + "] [" + item.getName() + "] " + selectedQty + "개 구매: -" + totalPrice + " P");
				}
				
				// 주문 내역에도 선택한 수량만큼 추가
				dataManager.getOrderHistory().put(item.getName(),
						dataManager.getOrderHistory().getOrDefault(item.getName(), 0) + selectedQty);

				myMoneyLabel.setText("내 잔액: " + dataManager.getMyPrice() + "P");
				JOptionPane.showMessageDialog(this,
				"[" + item.getName() + "] " + selectedQty + "개 결제가 완료되었습니다! 🎉\n결제 금액: " + totalPrice + "P");
				qtySpinner.setValue(1);
			} else {
				JOptionPane.showMessageDialog(this, "잔액이 부족합니다. ㅠㅠ\n필요 금액: " + (totalPrice-UserInfo.currentUser.getPoints()) + "P", "구매 실패",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		return panel;
	}
}
//
//	public static void main(String[] args) {
//		SwingUtilities.invokeLater(() -> FrameBase.getInstance(new Shop()));
//	}
//}
