CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE machines (
  id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  code      VARCHAR(10)  NOT NULL UNIQUE,
  name      VARCHAR(100) NOT NULL,
  is_active BOOLEAN      NOT NULL DEFAULT TRUE
);


CREATE TABLE assignments (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  machine_id    UUID         NOT NULL REFERENCES machines(id) ON DELETE RESTRICT,
  operator_name VARCHAR(120) NOT NULL,
  start_time    TIMESTAMPTZ  NOT NULL DEFAULT now(),
  end_time      TIMESTAMPTZ  NULL
);

CREATE UNIQUE INDEX uq_assignments_active_per_machine
ON assignments(machine_id)
WHERE end_time IS NULL;

CREATE TABLE work_orders (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_no   VARCHAR(30)  NOT NULL UNIQUE,
  title      VARCHAR(160) NOT NULL,
  status     VARCHAR(20)  NOT NULL,
  machine_id UUID         NOT NULL REFERENCES machines(id) ON DELETE RESTRICT,
  created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ  NOT NULL DEFAULT now(),

  CONSTRAINT ck_work_orders_status
  CHECK (status IN ('NEW','IN_PROGRESS','PAUSED','DONE','CANCELLED'))
);

CREATE TABLE event_logs (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
  type          VARCHAR(40)  NOT NULL,
  machine_id    UUID         NULL REFERENCES machines(id) ON DELETE SET NULL,
  assignment_id UUID         NULL REFERENCES assignments(id) ON DELETE SET NULL,
  work_order_id UUID         NULL REFERENCES work_orders(id) ON DELETE SET NULL,
  message       TEXT         NOT NULL
);

CREATE TABLE admins (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email         VARCHAR(160) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
  created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);


