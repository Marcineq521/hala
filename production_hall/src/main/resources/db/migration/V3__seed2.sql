ALTER TABLE assignments
ADD COLUMN work_order_id uuid;

ALTER TABLE assignments
ADD CONSTRAINT fk_assignments_work_order
FOREIGN KEY (work_order_id) REFERENCES work_orders(id);