package Shop;

import javax.swing.*;

import Share.UserInfo;

import java.awt.*;
import java.net.URL;
import java.util.Map;

public class CartDetail extends JFrame {
	public CartDetail(ShopDataManager dataManager, Runnable updateMainUI) {
		setTitle("장바구니 상세");
		setSize(400, 500);
		setLayout(new BorderLayout());
		setResizable(false);

		// 상단 제목
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

		JLabel title = new JLabel("나의 장바구니", SwingConstants.LEFT);
		title.setFont(new Font("맑은 고딕", Font.BOLD, 22));

		JButton buyAllBtn = new JButton("전체 구매");
		buyAllBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		//buyAllBtn.setBackground(new Color(0, 102, 204)); 
		buyAllBtn.setForeground(Color.BLACK); 

		topPanel.add(title, BorderLayout.CENTER);
		topPanel.add(buyAllBtn, BorderLayout.EAST);

		add(topPanel, BorderLayout.NORTH);

		// 내역 리스트 패널
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setBackground(Color.WHITE);

		Map<String, Integer> cart = dataManager.getCart();
		Map<String, ItemInfo> shopDB = dataManager.getShopDB();
		
		buyAllBtn.addActionListener(e -> {
		    if (cart.isEmpty()) {
		        JOptionPane.showMessageDialog(this, "장바구니가 비어 있습니다.", "알림", JOptionPane.WARNING_MESSAGE);
		        return;
		    }

		    int totalQty = 0;   
		    int totalPrice = 0; 

		    // 1. 계산부터 먼저 합니다.
		    for (Map.Entry<String, Integer> entry : cart.entrySet()) {
		        String itemName = entry.getKey();
		        int qty = entry.getValue();
		        totalQty += qty;
		        totalPrice += (shopDB.get(itemName).getPrice() * qty);
		    }

		    String confirmMsg = String.format("총 %d개의 상품 (합계 %dP)\n정말 구매하시겠습니까?", totalQty, totalPrice);
		    int result = JOptionPane.showConfirmDialog(this, confirmMsg, "구매 확인", JOptionPane.YES_NO_OPTION);
		    
		    // 사용자가 '아니오'를 누르거나 창을 그냥 끄면 여기서 바로 멈춥니다(return).
		    if (result != JOptionPane.YES_OPTION) {
		        return; 
		    }

		    // 3. '예'를 눌렀을 때만 아래 결제 로직이 실행됩니다.
		    int currentPrice = dataManager.getMyPrice();
		    
		    if (currentPrice >= totalPrice) {
		        dataManager.setMyPrice(currentPrice - totalPrice);
		        
		     // 💡 [추가] 전체 구매 시 포인트 사용 내역 기록!
		        if (UserInfo.currentUser != null) {
		        	String timeStr = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MM/dd HH:mm"));
		            UserInfo.currentUser.addPointHistory("[" + timeStr + "전체 구매] 총 " + totalQty + "개 결제: -" + totalPrice + " P");
		        }
		        
		        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
		            String itemName = entry.getKey();
		            int qty = entry.getValue();
		            dataManager.getOrderHistory().put(itemName, dataManager.getOrderHistory().getOrDefault(itemName, 0) + qty);
		        }

		        cart.clear(); 

		        String msg = String.format("전체 구매가 완료되었습니다! \n\n총 구매 수량 : %d개\n총 결제 금액 : %dP\n남은 잔액 : %dP", 
		                                    totalQty, totalPrice, dataManager.getMyPrice());
		        JOptionPane.showMessageDialog(this, msg, "결제 완료", JOptionPane.INFORMATION_MESSAGE);

		        if (updateMainUI != null) updateMainUI.run();
		        dispose(); 

		    } else {
		        JOptionPane.showMessageDialog(this, "잔액이 부족합니다. ㅠㅠ\n필요 금액: " + (totalPrice-UserInfo.currentUser.getPoints()) + "P", "구매 실패", JOptionPane.ERROR_MESSAGE);
		    }
		});
		
		// 데이터 채우기
		for (Map.Entry<String, Integer> entry : cart.entrySet()) {
			String itemName = entry.getKey();
			int qty = entry.getValue();
			ItemInfo itemInfo = shopDB.get(itemName); // 상품 상세 정보
			
			//한 줄을 담을 가로형 패널
			JPanel rowPanel = new JPanel(new BorderLayout(15, 10));
			rowPanel.setBorder(
			BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
			BorderFactory.createEmptyBorder(10, 10, 10, 10)
			));
			// 높이 고정 (안 커지게)
			rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
			
			//[좌측] 이미지 영역
			URL imgUrl = getClass().getResource(itemInfo.getImgUrl());
			ImageIcon icon = null; 
			
			if(imgUrl != null) {
				ImageIcon originalIcon = new ImageIcon(imgUrl);
				Image scaledImage = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
				icon = new ImageIcon(scaledImage);
			}

			JLabel imgLabel;
            if (icon != null) {
                imgLabel = new JLabel(icon);
            } else {
                imgLabel = new JLabel("No Img", SwingConstants.CENTER);
            }
            
            imgLabel.setPreferredSize(new Dimension(60, 60));
            imgLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); 
            rowPanel.add(imgLabel, BorderLayout.WEST);
            
            // 3. [중앙] 상품명 및 수량
            JLabel infoLabel = new JLabel(itemName + " : " + qty + "개");
            infoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
            rowPanel.add(infoLabel, BorderLayout.CENTER);
            
            // 💡 4. [우측] 제어 영역 (버튼 + 합계 금액) 수정됨!
            JPanel rightPanel = new JPanel(new BorderLayout()); // 위아래로 나누기 위해 BorderLayout 사용
            rightPanel.setOpaque(false);
            
            // 4-1. 버튼 패널 (위쪽)
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            btnPanel.setOpaque(false); // 배경 투명하게
            
            JButton removeBtn = new JButton("삭제");
            JButton buyBtn = new JButton("구매");
            
            btnPanel.add(removeBtn);
            btnPanel.add(buyBtn);
            
            // 4-2. 합계 금액 라벨 (아래쪽)
            int totalPrice = itemInfo.getPrice() * qty; // 상품 단가 * 수량
            JLabel priceLabel = new JLabel("합계: " + totalPrice + "P");
            priceLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
            priceLabel.setForeground(new Color(0, 102, 204)); // 눈에 띄게 파란색 계열 적용
            priceLabel.setHorizontalAlignment(SwingConstants.RIGHT); // 우측 정렬
            priceLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 2)); // 상단 여백으로 버튼과 떨어뜨림
            
            // 우측 패널에 위(버튼), 아래(가격)로 조립
            rightPanel.add(btnPanel, BorderLayout.NORTH);
            rightPanel.add(priceLabel, BorderLayout.SOUTH);
            
            //제외 버튼 클릭 시
            removeBtn.addActionListener(e -> {
            	dataManager.getCart().remove(itemName);
            	listPanel.remove(rowPanel);
            	listPanel.revalidate();
            	listPanel.repaint();
            	
            	if(dataManager.getCart().isEmpty()) {
            		dispose();
            	}
            });
            
            //구매 버튼 클릭 시
            buyBtn.addActionListener(e ->{
            	int currentPrice = dataManager.getMyPrice();
            	
            	if(currentPrice >= totalPrice) {
            		//잔액 차감
            		dataManager.setMyPrice(currentPrice - totalPrice);
            		
            		// 💡 [추가] 개별 구매 시 포인트 사용 내역 기록!
            		if (UserInfo.currentUser != null) {
            			String timeStr = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MM/dd HH:mm"));
            		    UserInfo.currentUser.addPointHistory("[" + timeStr + "] [" + itemName + "] " + qty + "개 구매: -" + totalPrice + " P");
            		}
            		
            		//주문 내역에 추가 (기존 수량에 방금 산 수량 +)
            		dataManager.getOrderHistory().put(
            		itemName, dataManager.getOrderHistory().
            		getOrDefault(itemName, 0) + qty);
            		//장바구니에서 삭제 
            		dataManager.getCart().remove(itemName);
            		listPanel.remove(rowPanel);
            		listPanel.revalidate();
            		listPanel.repaint();
            		
            		JOptionPane.showMessageDialog(this,"["+ itemName +"] 결제가 완료되었습니다.\n 남은 잔액: " + dataManager.getMyPrice()+"P");
            	
            	if(updateMainUI != null) {
            		updateMainUI.run();
            	}
            	
            	if(dataManager.getCart().isEmpty()) {
            		dispose();
            	}
            	
            	}else {
            		
            		JOptionPane.showMessageDialog(this, "잔액이 부족합니다. ㅠㅠ\n필요 금액: " +(totalPrice-UserInfo.currentUser.getPoints())+ "P", "구매 실패", JOptionPane.ERROR_MESSAGE);
            	}
            });
            
            // 💡 기존 btnPanel 대신 새로 조립한 rightPanel을 우측에 붙입니다.
            rowPanel.add(rightPanel, BorderLayout.EAST);
            
            // 리스트에 추가
            listPanel.add(rowPanel);
		}

        // 💡 스크롤 속도 부드럽게 추가
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);

		// 하단 닫기 버튼
		JButton closeBtn = new JButton("닫기");
		closeBtn.addActionListener(e -> dispose()); // 현재 창만 닫기
		add(closeBtn, BorderLayout.SOUTH);

		setLocationRelativeTo(null);
		setVisible(true);
	}
}
