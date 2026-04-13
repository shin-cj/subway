package Shop;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import Share.UserInfo;

public class ShopDataManager {
    
    private static ShopDataManager instance = new ShopDataManager();
    
    // 💡 상점 DB 및 사용자 데이터
    private Map<String, ItemInfo> shopDB = new LinkedHashMap<>();
    private Map<String, Integer> cart = new HashMap<>();
    private Map<String, Integer> orderHistory = new HashMap<>();
    private Set<String> wishlist = new LinkedHashSet<>();
    
    public ShopDataManager() {
        shopDB.put("전설의 검", new ItemInfo("전설의 검", 3000, "무기", "/testIMGjpg.jpg"));
        shopDB.put("빨간 물약", new ItemInfo("빨간 물약", 500, "포션", "/testIMG2.jpg"));
        shopDB.put("가죽 갑옷", new ItemInfo("가죽 갑옷", 2000, "방어구", "/testIMG3.jpg"));
        shopDB.put("마법의 지팡이", new ItemInfo("마법의 지팡이", 4500, "무기", "/testIMG4.jpg"));
        shopDB.put("귀환 주문서", new ItemInfo("귀환 주문서", 1000, "소비", "/testIMG5.jpg"));
        shopDB.put("박카스", new ItemInfo("박카스", 3000, "음료", "/testIMG6.png"));
        shopDB.put("녹차", new ItemInfo("녹차", 4100, "음료", "/testIMG7.jpg"));
    }

    public static ShopDataManager getInstance() {
        return instance;
    }
    
    // 💡 UserInfo.currentUser와 포인트 연동
    public int getMyPrice() { 
        if (UserInfo.currentUser != null) {
            return UserInfo.currentUser.getPoints();
        }
        return 0;
    }
    
    public void setMyPrice(int myPrice) {
        if (UserInfo.currentUser != null) {
            UserInfo.currentUser.setPoints(myPrice);
        }
    }
    
    public void toggleWishlist(String itemName) {
        if (wishlist.contains(itemName)) {
            wishlist.remove(itemName);
        } else {
            wishlist.add(itemName);
        }
    }
    
    public Map<String, ItemInfo> getShopDB() { return shopDB; }
    public Map<String, Integer> getCart() { return cart; }
    public Map<String, Integer> getOrderHistory() { return orderHistory; }
    public Set<String> getWishlist() { return wishlist; }

    public void addToCart(String itemName) {
        cart.put(itemName, cart.getOrDefault(itemName, 0) + 1);
    }

    @Override
    public String toString() {
        return "보유 포인트 : " + getMyPrice() + "p";
    }
}