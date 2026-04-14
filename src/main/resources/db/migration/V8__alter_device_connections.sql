alter table device_connections
    add column mqtt_broker_host varchar(255);

alter table device_connections
    add column mqtt_broker_port integer;

alter table device_connections
    add column mqtt_username varchar(255);

alter table device_connections
    add column mqtt_password varchar(255);

alter table device_connections
    add column mqtt_topic_prefix varchar(255);

alter table device_connections
    add column mqtt_use_tls boolean default false;