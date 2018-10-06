package org.kyojo.plugin.bs4Lab.checkoutLab

class CartItem {

	String productType
	String productName
	String productDesc
	int productAmount

	public CartItem(String productType, String productName, String productDesc, int productAmount) {
		this.productType = productType
		this.productName = productName;
		this.productDesc = productDesc;
		this.productAmount = productAmount;
	}

}
