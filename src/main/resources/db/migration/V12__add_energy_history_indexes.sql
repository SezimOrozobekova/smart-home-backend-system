CREATE INDEX IF NOT EXISTS idx_device_energy_history_device_recorded_at
    ON device_energy_history (device_id, recorded_at);

CREATE INDEX IF NOT EXISTS idx_device_energy_history_recorded_at
    ON device_energy_history (recorded_at);

CREATE INDEX IF NOT EXISTS idx_device_energy_history_device_recorded_at_total
    ON device_energy_history (device_id, recorded_at, total_energy_wh);