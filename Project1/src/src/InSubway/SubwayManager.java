package InSubway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class SubwayManager {
    // 역 이름을 키로 하여 Station 객체를 저장 (중복 방지 및 빠른 검색)
    private static Map<String, Station> allStations = new HashMap<>();

    // 구간 데이터를 등록하는 메서드
    public static void registerSection(String... names) {
        Station prev = null;
        for (String name : names) {
            // 이미 존재하는 역이면 가져오고, 없으면 새로 생성
            Station curr = allStations.computeIfAbsent(name, Station::new);//allStations에 "역이름"과 역객체(이름동일)을 넣는다(put).
            
            if (prev != null) {
                curr.addNeighbor(prev); // 앞뒤 역 연결
            }
            prev = curr;
        }
    }

    // 특정 역이 존재하는지 확인 (subwayBase에서 사용)
    public static boolean exists(String name) {
        return allStations.containsKey(name);
    }

    // 최단 경로 탐색 (BFS 알고리즘)
    public static List<String> findRoute(String start, String end) {
        if (!exists(start) || !exists(end)) return null;// 역이 존재하는지 확인

        Queue<List<String>> queue = new LinkedList<>();
        queue.add(Collections.singletonList(start));// 큐에 [시작역] 경로를 담음
        
        Set<String> visited = new HashSet<>();
        visited.add(start);// "나 여기 가봤어!" 표시

        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String current = path.get(path.size() - 1);//current:현재 역의 정보 저장

            if (current.equals(end)) return path;// 목적지 도착! 지금까지의 경로 리턴

            for (Station neighbor : allStations.get(current).neighbors) {
                if (!visited.contains(neighbor.name)) {
                    visited.add(neighbor.name);
                    List<String> newPath = new ArrayList<>(path);
                    newPath.add(neighbor.name);
                    queue.add(newPath);
                }
            }
        }
        return null; // 경로 없음
    }
}

class Station {
	
    String name;
    Set<Station> neighbors = new HashSet<>(); // 연결된 이웃 역들

    public Station(String name) {
    	this.name = name; 
    	}

    public void addNeighbor(Station s) {//이 메서드를 소환한 객체에 s를 추가(연결)하고//s에 메서드를 소환한 객체를 추가//철도 양방향을 한 번에 만드는 느낌
        this.neighbors.add(s);
        s.neighbors.add(this); // 양방향 연결
    }
}
