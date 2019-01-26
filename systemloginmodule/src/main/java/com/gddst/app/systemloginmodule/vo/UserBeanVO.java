package com.gddst.app.systemloginmodule.vo;


import java.util.ArrayList;
import java.util.List;

public class UserBeanVO {
	/**
	 * state : 1
	 * configFile : 0001
	 * password : null
	 * ticket : N2ZkMDU0ZGMtYzQ5Mi00Mjc4LThiMzMtNzJiZTAxODUzNmJi
	 * deptId : 03498b74-cbbb-41dd-ade3-b93af2833ccf
	 * deptName : 信息科
	 * deptNum : 03498b74-cbbb-41dd-ade3-b93af2833ccf
	 * groupNum : 0001
	 * groupName : 广州
	 * groupId : 7
	 * userName : 007
	 * phone : null
	 * userId : d1eb1913-b531-4566-b5c3-3cd37c35362a
	 * userAlias : 王五
	 * parentGroupId : 6
	 * listPrem : null
	 * stateStr : null
	 * arcgisUser : null
	 * arcgisPWD : 7f3a20dcec6bf386a61159e4a58d6492
	 */

	private String state;
	private String configFile;
	private String password;
	private String ticket;
	private String deptId;
	private String deptName;
	private String deptNum;
	private String groupNum;
	private String groupName;
	private Long groupId;
	private String userName;
	private String phone;
	private String userId;
	private String userAlias;
	private String parentGroupId;
	private List<String> listPrem;
	private String stateStr;
	private String arcgisUser;
	private String arcgisPWD;
	//工号
	private String jobNumber;


	public String getState() {
		return state == null ? "" : state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getConfigFile() {
		return configFile == null ? "" : configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public String getPassword() {
		return password == null ? "" : password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTicket() {
		return ticket == null ? "" : ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public String getDeptId() {
		return deptId == null ? "" : deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName == null ? "" : deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getDeptNum() {
		return deptNum == null ? "" : deptNum;
	}

	public void setDeptNum(String deptNum) {
		this.deptNum = deptNum;
	}

	public String getGroupNum() {
		return groupNum == null ? "" : groupNum;
	}

	public void setGroupNum(String groupNum) {
		this.groupNum = groupNum;
	}

	public String getGroupName() {
		return groupName == null ? "" : groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getUserName() {
		return userName == null ? "" : userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhone() {
		return phone == null ? "" : phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getUserId() {
		return userId == null ? "" : userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserAlias() {
		return userAlias == null ? "" : userAlias;
	}

	public void setUserAlias(String userAlias) {
		this.userAlias = userAlias;
	}

	public String getParentGroupId() {
		return parentGroupId == null ? "" : parentGroupId;
	}

	public void setParentGroupId(String parentGroupId) {
		this.parentGroupId = parentGroupId;
	}

	public List<String> getListPrem() {
		if (listPrem == null) {
			return new ArrayList<>();
		}
		return listPrem;
	}

	public void setListPrem(List<String> listPrem) {
		this.listPrem = listPrem;
	}

	public String getStateStr() {
		return stateStr == null ? "" : stateStr;
	}

	public void setStateStr(String stateStr) {
		this.stateStr = stateStr;
	}

	public String getArcgisUser() {
		return arcgisUser == null ? "" : arcgisUser;
	}

	public void setArcgisUser(String arcgisUser) {
		this.arcgisUser = arcgisUser;
	}

	public String getArcgisPWD() {
		return arcgisPWD == null ? "" : arcgisPWD;
	}

	public void setArcgisPWD(String arcgisPWD) {
		this.arcgisPWD = arcgisPWD;
	}

	public String getJobNumber() {
		return jobNumber == null ? "" : jobNumber;
	}

	public void setJobNumber(String jobNumber) {
		this.jobNumber = jobNumber;
	}


}
