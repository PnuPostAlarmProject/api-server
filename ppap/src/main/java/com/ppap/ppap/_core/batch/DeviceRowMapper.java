package com.ppap.ppap._core.batch;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ppap.ppap.domain.user.entity.Device;

public class DeviceRowMapper implements RowMapper<Device> {

	private static final String ID_COLUMN = "device_id";
	private static final String FCM_TOKEN_COLUMN = "fcm_token";

	@Override
	public Device mapRow(ResultSet rs, int rowNum) throws SQLException {
		return Device.builder()
			.id(rs.getLong(ID_COLUMN))
			.fcmToken(rs.getString(FCM_TOKEN_COLUMN))
			.build();
	}
}
