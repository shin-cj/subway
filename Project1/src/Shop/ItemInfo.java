package Shop;

public class ItemInfo {
	
	private String name;
	private int price;
	private String type;
	private String imgUrl;
	
	public ItemInfo(String name, int price,String type,String imgUrl) {
		this.name = name;
		this.price = price;
		this.type = type;
		this.imgUrl = imgUrl;
	}

	
	//getter,setter	
	
	public String getName() {
		return name;
	}

	public String getImgUrl() {
		return imgUrl;
	}


	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}


	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	@Override
	public String toString() {
		return " ["+ name + "] 의 가격은 : " + price + "포인트 입니다.";
	}
	
	
	
}
