alter table device_connections
    add column if not exists external_device_id varchar(255);

alter table device_connections
    add column if not exists config_json jsonb not null default '{}'::jsonb;

alter table device_connections
    add column if not exists credentials_json jsonb;

alter table device_connections
    add column if not exists last_error text;

create index if not exists idx_device_connections_external_device_id
    on device_connections(external_device_id);