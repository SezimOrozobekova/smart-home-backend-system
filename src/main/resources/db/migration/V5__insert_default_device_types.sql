INSERT INTO device_types (
    id,
    code,
    name,
    category,
    icon,
    is_controllable,
    is_active,
    created_at,
    updated_at
) VALUES
      (gen_random_uuid(), 'LIGHT', 'Light', 'LIGHTING', 'lightbulb', TRUE, TRUE, now(), now()),
      (gen_random_uuid(), 'SMART_PLUG', 'Smart Plug', 'POWER', 'plug', TRUE, TRUE, now(), now()),
      (gen_random_uuid(), 'TV', 'TV', 'MEDIA', 'tv', TRUE, TRUE, now(), now()),
      (gen_random_uuid(), 'SENSOR', 'Sensor', 'SECURITY', 'activity', FALSE, TRUE, now(), now()),
      (gen_random_uuid(), 'CAMERA', 'Camera', 'SECURITY', 'camera', FALSE, TRUE, now(), now()),
      (gen_random_uuid(), 'THERMOSTAT', 'Thermostat', 'CLIMATE', 'thermostat', TRUE, TRUE, now(), now()),
      (gen_random_uuid(), 'BATTERY', 'Battery', 'POWER', 'battery', TRUE, TRUE, now(), now()),
      (gen_random_uuid(), 'WASHING_MACHINE', 'Washing Machine', 'APPLIANCE', 'washer', TRUE, TRUE, now(), now()),
      (gen_random_uuid(), 'WATER_HEATER', 'Water Heater', 'CLIMATE', 'heater', TRUE, TRUE, now(), now());