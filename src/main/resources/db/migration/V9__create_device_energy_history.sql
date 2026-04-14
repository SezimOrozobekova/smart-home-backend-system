create table if not exists device_energy_history (
                                                     id uuid primary key,
                                                     device_id uuid not null,
                                                     recorded_at timestamp with time zone not null,
                                                     power_watts numeric(10,3),
                                                     voltage numeric(10,3),
                                                     current numeric(10,3),
                                                     total_energy_wh numeric(14,3),
                                                     temperature_c numeric(10,3),

                                                     constraint fk_device_energy_history_device
                                                         foreign key (device_id) references devices(id)
);

create index if not exists idx_device_energy_history_device_id
    on device_energy_history(device_id);

create index if not exists idx_device_energy_history_recorded_at
    on device_energy_history(recorded_at);

create index if not exists idx_device_energy_history_device_recorded_at
    on device_energy_history(device_id, recorded_at);