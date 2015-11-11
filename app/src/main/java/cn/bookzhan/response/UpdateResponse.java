package cn.bookzhan.response;

import cn.bookzhan.model.UpdateModel;

public class UpdateResponse extends BaseResponse {
	private UpdateModel data;

	public UpdateModel getData() {
		return data;
	}

	public void setData(UpdateModel data) {
		this.data = data;
	}
	

}
