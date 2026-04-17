CREATE INDEX idx_device_energy_history_device_id_recorded_at
    ON device_energy_history (device_id, recorded_at);
