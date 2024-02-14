package com.ppap.ppap._core.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.SendResponse;
import com.ppap.ppap._core.firebase.FcmTokenValidator;
import com.ppap.ppap._core.utils.EmailService;
import com.ppap.ppap.domain.user.entity.Device;
import com.ppap.ppap.domain.user.repository.DeviceJpaRepository;

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

		try {
			BatchResponse batchResponse = fcmTokenValidator.validateTokenThrowException(
				chunk.getItems().stream()
					.map(Device::getFcmToken)
					.toList());

			List<SendResponse> responseList = batchResponse.getResponses();
			for (int i=0; i<responseList.size(); i++) {
				if ( !responseList.get(i).isSuccessful() ) {
					switch (responseList.get(i).getException().getMessagingErrorCode()) {
						case UNREGISTERED, INVALID_ARGUMENT, SENDER_ID_MISMATCH ->
							removeDevices.add(chunk.getItems().get(i));
					}
				}
			}
		} catch (FirebaseMessagingException e) {
			emailService.sendEmailToAdmin(e.getMessagingErrorCode().toString());
			log.error(e.getMessagingErrorCode().toString());
		}

		if (!removeDevices.isEmpty()) {
			deviceJpaRepository.deleteAllByIdInBatch(removeDevices.stream()
				.map(Device::getId)
				.toList());
		}
	}
}
