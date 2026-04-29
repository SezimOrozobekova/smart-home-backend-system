ALTER TABLE device_connections
    DROP COLUMN IF EXISTS ip_address,
    DROP COLUMN IF EXISTS port,
    DROP COLUMN IF EXISTS username,
    DROP COLUMN IF EXISTS password,
    DROP COLUMN IF EXISTS mqtt_broker_host,
    DROP COLUMN IF EXISTS mqtt_broker_port,
    DROP COLUMN IF EXISTS mqtt_username,
    DROP COLUMN IF EXISTS mqtt_password,
    DROP COLUMN IF EXISTS mqtt_topic_prefix,
    DROP COLUMN IF EXISTS mqtt_use_tls,
    DROP COLUMN IF EXISTS external_id;


ALTER TABLE auth_users
    DROP COLUMN IF EXISTS telegram_chat_id;