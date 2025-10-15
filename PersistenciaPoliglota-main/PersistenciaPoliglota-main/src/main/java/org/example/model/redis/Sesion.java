package org.example.model.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import lombok.*;

import java.time.Instant;

/**
 * Session stored in Redis. TTL should be configured in Redis or via higher level code when saving.
 */
@RedisHash("session")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sesion {
	@Id
	private String id; // sessionId
	private Integer userId;
	private String role;
	private Instant startedAt;
	private Instant closedAt;
	private String status; // activa / inactiva
	private Instant lastSeenAt;
	private String ipAddress;
}
