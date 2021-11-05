package org.springframework.data.redis.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.context.ApplicationEvent;
import org.springframework.data.redis.core.convert.MappingRedisConverter.BinaryKeyspaceIdentifier;
import org.springframework.lang.Nullable;

/**
 * {@link RedisKeyDeletedEvent} is a Redis specific {@link ApplicationEvent} published when a particular key in Redis
 * delete. It can hold the value of the deleted key next to the key, but is not required to do so.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 1.7
 */
public class RedisKeyDeletedEvent<T> extends RedisKeyspaceEvent {

	/**
	 * Use {@literal UTF-8} as default charset.
	 */
	private static final Charset CHARSET = StandardCharsets.UTF_8;

	private final BinaryKeyspaceIdentifier objectId;
	private final @Nullable Object value;

	/**
	 * Creates new {@link RedisKeyDeletedEvent}.
	 *
	 * @param key the deleted key.
	 */
	public RedisKeyDeletedEvent(byte[] key) {
		this(key, null);
	}

	/**
	 * Creates new {@link RedisKeyDeletedEvent}
	 *
	 * @param key the deleted key.
	 * @param value the value of the deleted key. Can be {@literal null}.
	 */
	public RedisKeyDeletedEvent(byte[] key, @Nullable Object value) {
		this(null, key, value);
	}

	/**
	 * Creates new {@link RedisKeyDeletedEvent}
	 *
	 * @param channel the Pub/Sub channel through which this event was received.
	 * @param key the deleted key.
	 * @param value the value of the deleted key. Can be {@literal null}.
	 * @since 1.8
	 */
	public RedisKeyDeletedEvent(@Nullable String channel, byte[] key, @Nullable Object value) {
		super(channel, key);

		if (BinaryKeyspaceIdentifier.isValid(key)) {
			this.objectId = BinaryKeyspaceIdentifier.of(key);
		} else {
			this.objectId = null;
		}

		this.value = value;
	}

	/**
	 * Gets the keyspace in which the expiration occured.
	 *
	 * @return {@literal null} if it could not be determined.
	 */
	public String getKeyspace() {
		return objectId != null ? new String(objectId.getKeyspace(), CHARSET) : null;
	}

	/**
	 * Get the deleted objects id.
	 *
	 * @return the deleted objects id.
	 */
	public byte[] getId() {
		return objectId != null ? objectId.getId() : getSource();
	}

	/**
	 * Get the deleted Object
	 *
	 * @return {@literal null} if not present.
	 */
	@Nullable
	public Object getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.EventObject#toString()
	 */
	@Override
	public String toString() {

		byte[] id = getId();
		return "RedisKeyDeletedEvent [keyspace=" + getKeyspace() + ", id=" + (id == null ? null : new String(id)) + "]";
	}

}