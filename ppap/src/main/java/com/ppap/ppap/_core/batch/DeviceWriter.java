package com.ppap.ppap._core.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.ppap.ppap._core.firebase.FcmTokenValidator;
import com.ppap.ppap._core.utils.EmailService;
import com.ppap.ppap.domain.user.entity.Device;
import com.ppap.ppap.domain.user.repository.DeviceJpaRepository;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeviceWriter implements ItemWriter<Device> {

	private final DeviceJpaRepository deviceJpaRepository;
	private final FcmTokenValidator fcmTokenValidator;
	private final EmailService emailService;

	public DeviceWriter(DeviceJpaRepository deviceJpaRepository, FcmTokenValidator fcmTokenValidator,
		EmailService emailService) {
		this.deviceJpaRepository = deviceJpaRepository;
		this.fcmTokenValidator = fcmTokenValidator;
		this.emailService = emailService;
	}

	@Override
	public void write(Chunk<? extends Device> chunk) throws Exception {
		List<Device> removeDevices = new ArrayList<>();
		chunk.forEach( device -> {
			try {
				fcmTokenValidator.validateTokenThrowException(device.getFcmToken());
			} catch (FirebaseMessagingException e) {
				switch (e.getMessagingErrorCode()) {
					case UNREGISTERED, INVALID_ARGUMENT -> removeDevices.add(device);
					case QUOTA_EXCEEDED, THIRD_PARTY_AUTH_ERROR, SENDER_ID_MISMATCH -> {
						try {
							emailService.sendEmailToAdmin(e.getMessagingErrorCode().toString());
						} catch (MessagingException ex) {
							log.error("Device Id: {}, Error Code: {}",
								device.getId(),
								e.getMessagingErrorCode().toString()
							);
						}
					}
				}

			}
		});

		if (!removeDevices.isEmpty()) {
			deviceJpaRepository.deleteAllByIdInBatch(removeDevices.stream()
				.map(Device::getId)
				.toList());
		}
	}
}
