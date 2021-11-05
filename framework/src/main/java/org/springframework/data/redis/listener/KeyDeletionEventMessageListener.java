package org.springframework.data.redis.listener;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisKeyDeletedEvent;
import org.springframework.lang.Nullable;

/**
 * {@link MessageListener} publishing {@link RedisKeyDeletedEvent}s via {@link ApplicationEventPublisher} by listening
 * to Redis keyspace notifications for key deletions.
 *
 * @author Christoph Strobl
 * @since 1.7
 */
public class KeyDeletionEventMessageListener extends KeyspaceEventMessageListener implements
		ApplicationEventPublisherAware {

	private static final Topic KEYEVENT_DELETE_TOPIC = new PatternTopic("__keyevent@*__:del");

	private @Nullable ApplicationEventPublisher publisher;

	/**
	 * Creates new {@link MessageListener} for {@code __keyevent@*__:del} messages.
	 *
	 * @param listenerContainer must not be {@literal null}.
	 */
	public KeyDeletionEventMessageListener(RedisMessageListenerContainer listenerContainer) {
		super(listenerContainer);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.redis.listener.KeyspaceEventMessageListener#doRegister(org.springframework.data.redis.listener.RedisMessageListenerContainer)
	 */
	@Override
	protected void doRegister(RedisMessageListenerContainer listenerContainer) {
		listenerContainer.addMessageListener(this, KEYEVENT_DELETE_TOPIC);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.redis.listener.KeyspaceEventMessageListener#doHandleMessage(org.springframework.data.redis.connection.Message)
	 */
	@Override
	protected void doHandleMessage(Message message) {
		publishEvent(new RedisKeyDeletedEvent(message.getBody()));
	}

	/**
	 * Publish the event in case an {@link ApplicationEventPublisher} is set.
	 *
	 * @param event can be {@literal null}.
	 */
	protected void publishEvent(RedisKeyDeletedEvent event) {

		if (publisher != null) {
			this.publisher.publishEvent(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher(org.springframework.context.ApplicationEventPublisher)
	 */
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;
	}
}