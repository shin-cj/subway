package Share;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfo {
    private String id;
    private String password;
    private int points;
    


    // 💡 [추가] 포인트 '적립' 및 내역을 저장할 리스트
    private List<String> pointHistory = new ArrayList<>();

    public static Map<String, UserInfo> userDatabase = new HashMap<>();
    public static UserInfo currentUser;

    public UserInfo(String id, String password, int points) {
        this.id = id;
        this.password = password;
        this.points = points;
        
        
        
        // 💡 초기 가입 축하 포인트 적립 기록
        addHistory("회원가입 축하", points);
    }
    
    static {
        // 아이디: admin, 비밀번호: admin, 포인트: 999999
        userDatabase.put("admin", new UserInfo("admin", "admin", 999999));
    }
    public void addPointHistory(String history) {
        if (this.pointHistory != null) {
            this.pointHistory.add(history);
        }
    }
    
    // 내역을 리스트에 쌓는 메서드
    public void addHistory(String eventName, int amount) {
        String date = java.time.format.DateTimeFormatter.ofPattern("MM/dd HH:mm").format(java.time.LocalDateTime.now());
        // 형식 예: [04/10 15:30] 회원가입 축하 : +500P
        String record = String.format("[%s] %s : +%dP", date, eventName, amount);
        pointHistory.add(record);
    }

    public List<String> getPointHistory() {
        return pointHistory;
    }

    public String getId() { 
    	return id; 
    	}
    public int getPoints() { 
    	return this.points;
    	}
    public void setPoints(int points) { 
    	this.points = points;
    	}
    public String getPassword() { 
    	return this.password;
    	}
    public void setPassword(String password) {
    	this.password=password;
    }
}