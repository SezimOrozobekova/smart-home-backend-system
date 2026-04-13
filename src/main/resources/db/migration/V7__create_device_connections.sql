create table device_connections (
                                    id uuid primary key,
                                    device_id uuid not null unique,
                                    provider varchar(50) not null,
                                    connection_type varchar(50) not null,
                                    ip_address varchar(255),
                                    port integer,
                                    username varchar(255),
                                    password varchar(255),
                                    external_id varchar(255),
                                    is_enabled boolean not null default true,
                                    created_at timestamp with time zone not null default now(),
                                    updated_at timestamp with time zone not null default now(),

                                    constraint fk_device_connections_device
                                        foreign key (device_id) references devices(id) on delete cascade,

                                    constraint chk_device_connections_port
                                        check (port is null or port > 0)
);

create index idx_device_connections_device_id
    on device_connections(device_id);

create index idx_device_connections_provider
    on device_connections(provider);

create index idx_device_connections_connection_type
    on device_connections(connection_type);

create index idx_device_connections_external_id
    on device_connections(external_id);