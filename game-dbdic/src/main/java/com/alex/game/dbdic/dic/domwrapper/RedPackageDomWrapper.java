package com.alex.game.dbdic.dic.domwrapper;
import java.util.List;
import com.alex.game.dbdic.dom.RedPackageDom;
import com.alibaba.fastjson.JSON;
public class RedPackageDomWrapper extends RedPackageDom {
	//红包的提示内容
	private final List<String> redContent;
	private final RedPackageDom dom;
	public int getId() {
		return dom.getId();
	}
	public void setId(int id) {
		dom.setId(id);
	}
	public String getName() {
		return dom.getName();
	}
	public void setName(String name) {
		dom.setName(name);
	}
	public int getRedType() {
		return dom.getRedType();
	}
	public void setRedType(int redType) {
		dom.setRedType(redType);
	}
	public int getSum() {
		return dom.getSum();
	}
	public void setSum(int sum) {
		dom.setSum(sum);
	}
	public int getNum() {
		return dom.getNum();
	}
	public void setNum(int num) {
		dom.setNum(num);
	}
	public String getRedContent() {
		return dom.getRedContent();
	}
	public void setRedContent(String redContent) {
		dom.setRedContent(redContent);
	}
	public RedPackageDomWrapper(RedPackageDom dom) {
		this.dom=dom;
		this.redContent = JSON.parseArray("[" +dom.getRedContent() + "]" , String.class);
	}
	public List<String> getRedContentArray() {
		return redContent;
	}
}
